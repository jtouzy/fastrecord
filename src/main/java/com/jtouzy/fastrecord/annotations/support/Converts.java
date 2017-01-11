package com.jtouzy.fastrecord.annotations.support;

import com.jtouzy.fastrecord.utils.Priority;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface Converts {
    Class value();
    int priority() default Priority.CUSTOM;
}
