package com.jtouzy.fastrecord.annotations;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Scope("prototype")
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String name() default "";
}
