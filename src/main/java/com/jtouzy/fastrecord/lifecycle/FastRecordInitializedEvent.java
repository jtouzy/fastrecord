package com.jtouzy.fastrecord.lifecycle;

import org.springframework.context.ApplicationEvent;

public class FastRecordInitializedEvent extends ApplicationEvent {
    public FastRecordInitializedEvent(Object source) {
        super(source);
    }
}
