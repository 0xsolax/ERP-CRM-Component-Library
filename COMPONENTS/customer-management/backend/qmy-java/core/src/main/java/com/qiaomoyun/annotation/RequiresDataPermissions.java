package com.qiaomoyun.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresDataPermissions {
    String[] value();
    Condition[] conditions() default { @Condition(field = "create_user", logic = LogicType.AND) };
    
    enum LogicType {
        AND, OR
    }
    
    @interface Condition {
        String field();
        LogicType logic() default LogicType.AND;
    }
}
