package com.jtouzy.fastrecord.statements.context2.impl;

import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.UpdateConditionChain;
import com.jtouzy.fastrecord.statements.context2.UpdateConditionExpression;

public class DefaultUpdateConditionWrapper
        extends AbstractConditionWrapper<UpdateConditionExpression,UpdateConditionChain>
        implements UpdateConditionChain {

    public DefaultUpdateConditionWrapper(UpdateConditionExpression firstConditionExpression,
                                         ConditionOperator conditionOperator,
                                         UpdateConditionExpression compareConditionExpression) {
        super(firstConditionExpression, conditionOperator, compareConditionExpression);
    }
}
