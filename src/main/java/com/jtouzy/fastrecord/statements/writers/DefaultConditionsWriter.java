package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context.ConditionContext;
import com.jtouzy.fastrecord.statements.context.ConditionsContext;
import com.jtouzy.fastrecord.statements.context.ConditionsOperator;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.utils.Chain;

import java.util.Iterator;

@Writes(ConditionsContext.class)
public class DefaultConditionsWriter extends AbstractWriter<ConditionsContext> {
    public DefaultConditionsWriter(WriterCache writerCache, ConditionsContext context) {
        super(writerCache, context);
    }

    @Override
    public DbReadyStatementMetadata write() {
        super.write();
        Iterator<Chain.ChainItemWrapper<ConditionContext,ConditionsOperator>> it =
                getContext().getConditions().iterator();
        if (it.hasNext()) {
            getSqlString().append(" WHERE ");
        }
        Chain.ChainItemWrapper<ConditionContext,ConditionsOperator> conditionItem;
        while (it.hasNext()) {
            conditionItem = it.next();
            mergeWriter(conditionItem.getItem());
            if (it.hasNext()) {
                getSqlString().append(" ")
                              .append(conditionItem.getLinkToNextItem().toString())
                              .append(" ");
            }
        }
        return buildMetadata();
    }
}
