package com.spring;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wzy
 * @version 1.0
 * @description: spring容器类
 * @date 2022/1/5 9:22
 */
public class ApplicationContext {

	private Class configClass;

	private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();
	private HashMap<String,Object> earlySingletonObjects= new HashMap<>();
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
	private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
	//提前进行aop标志 源码中是个 Map<Object, Object> earlyProxyReferences
	private Set<String> earlyProxyReferences = new HashSet<>();
	private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
	private Set<String> creatingBean = new HashSet<>();

	private boolean isAop;

	public ApplicationContext(Class configClass) {

		this.configClass = configClass;


		//注册代理后置处理器
		registerSingletonBean("proxBeanPostProcessor",new ProxBeanPostProcessor());
		//解析配置类  getDeclareAnnotation方法是不包含父类注解
		scanAnnotationBean(configClass);
		scan(configClass);


		beanDefinitionMap.forEach((beanName,bd) -> {
			if("singleton".equals(bd.getScope())){
				Object bean = createBean(beanName,bd);
				singletonObjects.put(beanName,bean);
			}
		});
	}

	private void scanAnnotationBean(Class configClass) {
		Method[] declaredMethods = configClass.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			Bean beanAnnotation = declaredMethod.getDeclaredAnnotation(Bean.class);
			if(beanAnnotation != null){
				String name = declaredMethod.getName();
				try {
					Object o = configClass.getConstructor().newInstance();
					Object invoke = declaredMethod.invoke(o);
					singletonObjects.put(name,invoke);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * ** 1、为什么写三个构造器（含有无参构造器），并且没有@Autowired注解，Spring总是使用无参构造器实例化Bean？**
	 * 答：参照没有注解的处理方式： 若构造器只有两个，且存在无参构造器，将直接使用无参构造器初始化。若大于两个构造器，将返回一个空集合，也就是没有
	 * 找到合适的构造器，那么参照第三节初始化Bean的第一段代码createBeanInstance方法的末尾，将会使用无参构造器进行实例化。这也就解答了为什么没
	 * 有注解，Spring总是会使用无参的构造器进行实例化Bean，并且此时若没有无参构造器会抛出异常，实例化Bean失败。
	 * ** 2、为什么注释掉两个构造器，留下一个有参构造器，并且没有@Autowired注解，Spring将会使用构造器注入Bean的方式初始化Bean？**
	 * 答：参照没有注解的处理方式： 构造器只有一个且有参数时，将会把此构造器作为适用的构造器返回出去，使用此构造器进行实例化，参数自然会从IOC中获
	 * 取Bean进行注入。
	 * **3、为什么写三个构造器，并且在其中一个构造器上打上@Autowired注解，就可以正常注入构造器？**
	 * 答：参照有注解的处理方式： 在最后判断candidates适用的构造器集合是否为空时，若有注解，此集合当然不为空，且required=true，也不会将默认构
	 * 造器集合defaultConstructor加入candidates集合中，最终返回的是candidates集合的数据，也就是这唯一一个打了注解的构造器，所以最终使用此
	 * 打了注解的构造器进行实例化。
	 * **4、两个@Autowired注解就会报错，一定需要在所有@Autowired中的required都加上false即可正常初始化？**
	 * 答：参照有注解的处理方式： 当打了两个@Autowired注解，也就是两个required都为true，将会抛出异常，若是一个为true，一个为false，也将会抛
	 * 出异常，无论顺序，因为有两层的判断，一个是requiredConstructor集合是否为空的判断，一个是candidates集合为空的判断，若两个构造器的
	 * required属性都为false，不会进行上述判断，直接放入candidates集合中，并且在下面的判断中会将defaultConstructor加入到candidates集合
	 * 中，也就是candidates集合有三个构造器，作为结果返回。
	 * **5、返回的构造器若有三个，Spring将如何判断使用哪一个构造器呢？**
	 * 在后面Spring会遍历三个构造器，依次判断参数是否是Spring的Bean（是否被IOC容器管理），若参数不是Bean，将跳过判断下一个构造器，也就是说，
	 * 例如上述两个参数的构造器其中一个参数不是Bean，将判断一个参数的构造器，若此参数是Bean，使用一个参数的构造器实例化，若此参数不是Bean，将使
	 * 用无参构造器实例化。也就是说，若使用@Autowired注解进行构造器注入，required属性都设置为false的话，将避免无Bean注入的异常，使用无参构造
	 * 器正常实例化。若两个参数都是Bean，则就直接使用两个参数的构造器进行实例化并获取对应Bean注入构造器。 在这里最后说一点，从上面可以看出，若想
	 * 使用构造器注入功能，最好将要注入的构造器都打上@Autowired注解（若有多个需要注入的构造器，将所有@Autowired中required属性都设置为false）
	 * ，若有多个构造器，只有一个构造器需要注入，将这个构造器打上@Autowired注解即可，不用设置required属性。如果不打注解也是可以使用构造器注入
	 * 功能的，但构造器数量只能为1，且代码可读性较差，读代码的人并不知道你这里使用了构造器注入的方式，所以这里我建议若使用构造器注入打上
	 * @Autowired注解会比较好一点。
	 * @param bd
	 * @return
	 */
	public Object createBean(String beanName,BeanDefinition bd){
		creatingBean.add(beanName);
		Object instatnce = getSingleton(beanName);
		Class clazz = bd.getClazz();

		Constructor[] constructors = clazz.getDeclaredConstructors();
		Constructor defaultConstructor = null;
		for (Constructor constructor : constructors) {
			if(constructor.isAnnotationPresent(Autowired.class)){
				defaultConstructor = constructor;
			}else if(constructor.getParameterCount() == 0){
				defaultConstructor = constructor;
			}
		}
		Parameter[] parameters = defaultConstructor.getParameters();


		if(parameters == null || parameters.length == 0){
			//无参构造器
			instatnce = newInstatnce(clazz, defaultConstructor);
		}else{
			instatnce =  newInstance(defaultConstructor, parameters);

		}
		//原始对象放入三级缓存
		Object finalInstatnce = instatnce;
		singletonFactories.put(beanName,()->createProxy(beanName, finalInstatnce));
		//依赖注入
		instatnce = autowired(clazz, instatnce);
		//Aware回调
		if(instatnce instanceof BeanAware){
			((BeanAware)instatnce).setBeanName(beanName);
			((BeanAware)instatnce).setBeanFactory(singletonObjects);
		}
		for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
			instatnce = beanPostProcessor.postProcessBeforeInitialization(instatnce,beanName);
		}
		//初始化
		if(instatnce instanceof InitializingBean){
			((InitializingBean)instatnce).afterPropertiesSet();
		}

		for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
			instatnce = beanPostProcessor.postProcessAfterInitialization(instatnce,beanName);
		}

		Object earlySingletonReference = getSingleton(beanName);
		if (earlySingletonReference != null) {
			if (instatnce == finalInstatnce) {
				instatnce = earlySingletonReference;
			}
		}
		if(!earlyProxyReferences.contains(beanName)){
			instatnce = createProxy(beanName, instatnce);
		}else{
			earlyProxyReferences.remove(beanName);
		}
		creatingBean.remove(beanName);
		return instatnce;
	}

	/**
	 * 根据调解创建代理对象
	 * @param beanName
	 * @param instatnce
	 * @return
	 */
	private Object createProxy(String beanName, Object instatnce) {
		earlyProxyReferences.add(beanName);
		//处理BeanNameAutoProxyCreator
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = (BeanNameAutoProxyCreator)singletonObjects.get("beanNameAutoProxyCreator");
		if(beanNameAutoProxyCreator != null){
			String proxyBeanName = beanNameAutoProxyCreator.getBeanName();
			String interceptorName = beanNameAutoProxyCreator.getInterceptorName();
			MethodInterceptor methodInterceptor = (MethodInterceptor)singletonObjects.get(interceptorName);
			if(proxyBeanName != null && interceptorName != null){
				if(beanName.equals(proxyBeanName)){
					Class<?> clazz = instatnce.getClass();
					Enhancer enhancer = new Enhancer();
					enhancer.setUseCache(true);
					enhancer.setSuperclass(clazz);
					enhancer.setClassLoader(clazz.getClassLoader());
					enhancer.setCallback(methodInterceptor);
					return enhancer.create();
				}
			}
		}
		return instatnce;
	}

	/**
	 * 有参数构造方法
	 * @param defaultConstructor
	 * @param parameters
	 * @return
	 */
	private Object newInstance(Constructor defaultConstructor, Parameter[] parameters) {

		Object[] params = new Object[parameters.length];
		Class[] paramTyps = new Class[parameters.length];
		//有参数构造方法
		for (int i = 0; i < parameters.length; i++) {
			String simpleName = parameters[i].getType().getSimpleName();
			String beanName = Introspector.decapitalize(simpleName);
			Object o = singletonObjects.get(beanName);
			params[i] = o;
			paramTyps[i] = parameters[i].getType();
		}
		try {
			return defaultConstructor.newInstance(params);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 调用无参构造方法创建对象
	 * @param clazz
	 * @param defaultConstructor
	 * @return
	 */
	private Object newInstatnce(Class clazz, Constructor defaultConstructor) {
		Object instance = null;
		try {
			return instance = defaultConstructor.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object autowired(Class clazz, Object instance) {
		Field[] declaredFields = clazz.getDeclaredFields();
		Constructor[] declaredConstructors = clazz.getDeclaredConstructors();
		for (Constructor declaredConstructor : declaredConstructors) {
			if(declaredConstructor.isAnnotationPresent(Autowired.class)){
				Class[] parameterTypes = declaredConstructor.getParameterTypes();
				Object[] parameters = new Object[parameterTypes.length];
				for (int i = 0; i < parameterTypes.length; i++) {
					String simpleName = parameterTypes[i].getSimpleName();
					String beanName = Introspector.decapitalize(simpleName);
					Object bean = getSingleton(beanName);
					if(bean == null){
						bean = createBean(beanName, beanDefinitionMap.get(beanName));
					}
					parameters[i] = bean;
				}
				try {
					return declaredConstructor.newInstance(parameters);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		for (Field declaredField : declaredFields) {
			if (declaredField.isAnnotationPresent(Autowired.class)){
				String fieldName = declaredField.getName();

				Object bean = getSingleton(fieldName);
				if(bean == null){
					bean = createBean(fieldName, beanDefinitionMap.get(fieldName));
				}
				declaredField.setAccessible(true);
				try {
					declaredField.set(instance,bean);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return instance;
	}

	private void scan(Class configClass) {
		ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
		if(componentScan != null){
			String path = componentScan.value();

			ClassLoader classLoader = this.getClass().getClassLoader();
			URL resource = classLoader.getResource(path.replace(".","/"));
			File componentScanfile = new File(resource.getFile());
			if(componentScanfile.isDirectory()){
				File[] files = componentScanfile.listFiles();
				for (File file : files) {
					String fileName = file.getAbsolutePath();
					if (fileName.endsWith(".class")){
						String className = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class")).replace("\\",".");
						try {
							Class<?> clazz = classLoader.loadClass(className);
							if (clazz.isAnnotationPresent(Component.class)){
								//表示当前类是一个bean
								//解析类判断类是单例还是原型
								//解析 -》 BeanDefinition
								if(BeanPostProcessor.class.isAssignableFrom(clazz)){
									BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
									beanPostProcessorList.add(beanPostProcessor);
								}

								Component component = clazz.getAnnotation(Component.class);
								String beanName = component.value();
								/**
								 * 源码中使用的方法 jdk原生方法 Introspector.decapitalize
								 获得一个字符串并将它转换成普通 Java 变量名称大写形式的实用工具方法。这通常意味着将首字符从大写转换成小写，
								 但在（不平常的）特殊情况下，当有多个字符且第一个和第二个字符都是大写字符时，不执行任何操作。
								 因此 "FooBah" 变成 "fooBah"，"X" 变成 "x"，但 "URL" 仍然是 "URL"。
								 */
								beanName = beanName.length() == 0 ? Introspector.decapitalize(clazz.getSimpleName()):beanName;
								String scope = "singleton";
								if(clazz.isAnnotationPresent(Scope.class)){
									Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
									scope = scopeAnnotation.value();
								}
								beanDefinitionMap.put(beanName,new BeanDefinition(clazz,scope));

							}

						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
					}

				}
			}else{
				throw new RuntimeException();
			}
		}else{
			try {
				throw new ClassNotFoundException("ComponentScan not found");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Object getBean(String beanName){
		if (beanDefinitionMap.containsKey(beanName)){
			BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
			if("singleton".equals(beanDefinition.getScope())){
				return singletonObjects.get(beanName);
			}else{
				Object bean = createBean(beanName,beanDefinition);
				return bean;
				//创建bean对象
			}
		}else{
			throw new NullPointerException(beanName+"的BD对象不存在");
		}
	}

	public <T>T getBean(String beanName, Class<T> t){
		Object bean = getBean(beanName);
		if(Proxy.isProxyClass(bean.getClass())){
			ProxyBean proxyBean = (ProxyBean)Proxy.getInvocationHandler(bean);
			return (T)proxyBean.getTarget();
		}
		return (T)bean;
	}


	public void registerSingletonBean(String beanName,Object bean){

		if(bean instanceof BeanPostProcessor){
			beanPostProcessorList.add((BeanPostProcessor)bean);
		}
		//Aware回调
		if(bean instanceof BeanAware){
			((BeanAware)bean).setBeanName(beanName);
			((BeanAware)bean).setBeanFactory(singletonObjects);
		}
		for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
			bean = beanPostProcessor.postProcessBeforeInitialization(bean,beanName);
		}

		for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
			bean = beanPostProcessor.postProcessAfterInitialization(bean,beanName);
		}
		singletonObjects.put(beanName,bean);
	}

	//源码中的方法
	protected Object getSingleton(String beanName) {
		// Quick check for existing instance without full singleton lock
		Object singletonObject = this.singletonObjects.get(beanName);
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			singletonObject = this.earlySingletonObjects.get(beanName);
			if (singletonObject == null) {
				synchronized (this.singletonObjects) {
					// Consistent creation of early reference within full singleton lock
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						singletonObject = this.earlySingletonObjects.get(beanName);
						if (singletonObject == null) {
							ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
							if (singletonFactory != null) {
								singletonObject = singletonFactory.getObject();
								this.earlySingletonObjects.put(beanName, singletonObject);
								this.singletonFactories.remove(beanName);
							}
						}
					}
				}
			}
		}
		return singletonObject;
	}

	private boolean isSingletonCurrentlyInCreation(String beanName) {
		return creatingBean.contains(beanName);
	}


}
