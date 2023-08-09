package org.mydrive.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * checkParams
     * @return
     */
    boolean checkParams() default false;

    /**
     * checkLogin
     * @return
     */
    boolean checkLogin() default true;

    /**
     * checkAdmin
     * @return
     */
    boolean checkAdmin() default false;
}
