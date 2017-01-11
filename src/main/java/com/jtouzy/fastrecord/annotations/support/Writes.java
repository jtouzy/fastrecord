package com.jtouzy.fastrecord.annotations.support;

import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.utils.Priority;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Scope("prototype")
@Retention(RetentionPolicy.RUNTIME)
public @interface Writes {
    Class<? extends WritableContext> value();
    int priority() default Priority.CUSTOM;
}
