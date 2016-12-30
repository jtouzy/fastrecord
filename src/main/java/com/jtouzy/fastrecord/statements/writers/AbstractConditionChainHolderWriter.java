package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.ConditionChainHolder;
import com.jtouzy.fastrecord.statements.context.WritableContext;

public abstract class AbstractConditionChainHolderWriter<T extends ConditionChainHolder & WritableContext>
        extends AbstractWriter<T> {

    protected void writeConditions() {
        if (getContext().getConditionChain().getChain().size() > 0) {
            getResult().getSqlString().append(" WHERE ");
            mergeWriter(getContext().getConditionChain());
        }
    }
}
