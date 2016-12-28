package com.jtouzy.fastrecord.tests.context;

import com.jtouzy.fastrecord.statements.context2.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.ConditionOperator;
import com.jtouzy.fastrecord.statements.context2.ConstantExpression;
import com.jtouzy.fastrecord.statements.context2.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context2.impl.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class QueryConditionChainTest {
    @Test
    public void constantConditionChainTest() {
        // Context
        QueryConditionChain queryConditionChain = new DefaultQueryConditionChain();
        queryConditionChain.addCondition(
                new DefaultQueryConditionWrapper(
                        new DefaultConstantExpression(Types.VARCHAR, "First value"),
                        ConditionOperator.EQUALS,
                        new DefaultConstantExpression(Types.VARCHAR, "Second value")));
        // Assertions
        QueryConditionChain conditionChain = queryConditionChain.getChain().get(0).getItem();
        Assert.assertEquals(DefaultQueryConditionWrapper.class, conditionChain.getClass());
        DefaultQueryConditionWrapper wrapper = (DefaultQueryConditionWrapper)conditionChain;
        Assert.assertTrue(ConstantExpression.class.
                isAssignableFrom(wrapper.getFirstConditionExpression().getClass()));
        Assert.assertTrue(ConstantExpression.class.
                isAssignableFrom(wrapper.getCompareConditionExpression().getClass()));
    }

    @Test
    public void aliasTableColumnConditionChainTest() {
        // Context
        QueryConditionChain queryConditionChain = new DefaultQueryConditionChain();
        queryConditionChain.addCondition(
                new DefaultQueryConditionWrapper(
                        new DefaultAliasTableColumnExpression(
                                Types.VARCHAR,
                                new DefaultAliasTableExpression("table", "alias"),
                                "column1"),
                        ConditionOperator.EQUALS,
                        new DefaultAliasTableColumnExpression(
                                Types.VARCHAR,
                                new DefaultAliasTableExpression("table", "alias"),
                                "column2")));
        // Assertions
        QueryConditionChain conditionChain = queryConditionChain.getChain().get(0).getItem();
        Assert.assertEquals(DefaultQueryConditionWrapper.class, conditionChain.getClass());
        DefaultQueryConditionWrapper wrapper = (DefaultQueryConditionWrapper)conditionChain;
        Assert.assertTrue(AliasTableColumnExpression.class.
                isAssignableFrom(wrapper.getFirstConditionExpression().getClass()));
        Assert.assertTrue(AliasTableColumnExpression.class.
                isAssignableFrom(wrapper.getCompareConditionExpression().getClass()));
    }
}
