package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Chain;
import com.jtouzy.fastrecord.utils.Priority;

import java.util.Iterator;

@Writes(value = ConditionChain.class, priority = Priority.NATIVE)
public class DefaultConditionChainWriter extends AbstractWriter<ConditionChain> {
    @Override
    @SuppressWarnings("unchecked")
    public void write() {
        DbReadyStatementMetadata metadata = getResult();
        metadata.getSqlString().append("(");
        Iterator<Chain.ChainItemWrapper> chainIterator = getContext().getChain().iterator();
        Chain.ChainItemWrapper<WritableContext,ConditionChainOperator> item;
        while (chainIterator.hasNext()) {
            item = chainIterator.next();
            mergeWriter(item.getItem());
            if (chainIterator.hasNext()) {
                metadata.getSqlString().append(" ").append(item.getLinkToNextItem()).append(" ");
            }
        }
        metadata.getSqlString().append(")");
    }

    @Override
    public boolean isCacheable() {
        return false;
    }
}
