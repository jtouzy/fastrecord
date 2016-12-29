package com.jtouzy.fastrecord.statements.context.impl;

import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.UpdateConditionChain;
import com.jtouzy.fastrecord.statements.context.UpdateConditionExpression;

public class DefaultUpdateConditionWrapper
        extends AbstractConditionWrapper<UpdateConditionExpression,UpdateConditionChain>
        implements UpdateConditionChain {

    public DefaultUpdateConditionWrapper(UpdateConditionExpression firstConditionExpression,
                                         ConditionOperator conditionOperator,
                                         UpdateConditionExpression compareConditionExpression) {
        super(firstConditionExpression, conditionOperator, compareConditionExpression);
    }
}
