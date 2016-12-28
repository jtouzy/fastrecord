package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.ConditionChain;
import com.jtouzy.fastrecord.statements.context2.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context2.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Chain;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component("FastRecord.Writer.DefaultConditionChainWriter")
@Scope("prototype")
@Writes(ConditionChain.class)
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
