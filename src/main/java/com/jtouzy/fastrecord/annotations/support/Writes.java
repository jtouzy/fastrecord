package com.jtouzy.fastrecord.annotations.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Writes {
    Class value();
}
