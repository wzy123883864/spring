package com.spring;

import java.lang.annotation.*;

/**
 * @author 王志远
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {


}
