package com.jtouzy.fastrecord.tests.context;

import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.ConstantExpression;
import com.jtouzy.fastrecord.statements.context2.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.UpdateConditionChain;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultUpdateConditionChain;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultUpdateConditionWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class UpdateConditionChainTest {
    @Test
    public void constantConditionChainTest() {
        // Context
        UpdateConditionChain updateConditionChain = new DefaultUpdateConditionChain();
        updateConditionChain.addCondition(
                new DefaultUpdateConditionWrapper(
                        new DefaultConstantExpression(Types.VARCHAR, "First value"),
                        ConditionOperator.EQUALS,
                        new DefaultConstantExpression(Types.VARCHAR, "Second value")));
        // Assertions
        UpdateConditionChain conditionChain = updateConditionChain.getChain().get(0).getItem();
        Assert.assertEquals(DefaultUpdateConditionWrapper.class, conditionChain.getClass());
        DefaultUpdateConditionWrapper wrapper = (DefaultUpdateConditionWrapper)conditionChain;
        Assert.assertTrue(ConstantExpression.class.
                isAssignableFrom(wrapper.getFirstConditionExpression().getClass()));
        Assert.assertTrue(ConstantExpression.class.
                isAssignableFrom(wrapper.getCompareConditionExpression().getClass()));
    }

    @Test
    public void tableColumnConditionChainTest() {
        // Context
        UpdateConditionChain updateConditionChain = new DefaultUpdateConditionChain();
        updateConditionChain.addCondition(
                new DefaultUpdateConditionWrapper(
                        new DefaultSimpleTableColumnExpression(Types.VARCHAR, null, "column1"),
                        ConditionOperator.EQUALS,
                        new DefaultSimpleTableColumnExpression(Types.VARCHAR, null, "column2")));
        // Assertions
        UpdateConditionChain conditionChain = updateConditionChain.getChain().get(0).getItem();
        Assert.assertEquals(DefaultUpdateConditionWrapper.class, conditionChain.getClass());
        DefaultUpdateConditionWrapper wrapper = (DefaultUpdateConditionWrapper)conditionChain;
        Assert.assertTrue(SimpleTableColumnExpression.class.
                isAssignableFrom(wrapper.getFirstConditionExpression().getClass()));
        Assert.assertTrue(SimpleTableColumnExpression.class.
                isAssignableFrom(wrapper.getCompareConditionExpression().getClass()));
    }
}
