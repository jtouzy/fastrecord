package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.statements.context.InsertExpression;

import java.util.List;

public interface InsertProcessor<T> extends WriteProcessor<T,InsertExpression> {
    void initProcessor(Class<T> entityClass, List<T> targets);
}
