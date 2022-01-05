import com.test.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2021/12/14 9:48
 */
public class MyApplication {

	public static void main(String[] args) {

		ApplicationContext context = new AnnotationConfigApplicationContext(User.class);

		User user = (User)context.getBean("user");
		System.out.println(User.class.getName());

	}
}
