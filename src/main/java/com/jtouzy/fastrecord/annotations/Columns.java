package com.jtouzy.fastrecord.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Columns {
    String[] columns();
    String[] related();
}
