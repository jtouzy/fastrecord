package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.statements.context2.WritableContext;
import org.springframework.stereotype.Service;

@Service("FastRecord.WriterProcess.WriterPool")
public class WriterPool {
    public <C extends WritableContext, T extends Writer<C>> Class<T> findWriterClassFor(Class<C> writableContext) {
        return null;
    }
}
