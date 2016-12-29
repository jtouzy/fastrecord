package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultConditionChainWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultConditionChainWriterTest extends AbstractWriterTest<ConditionChain,DefaultConditionChainWriter> {
    public DefaultConditionChainWriterTest() {
        super(DefaultConditionChainWriter.class);
    }

    @Test
    public void querySingleConditionChainTest()
    throws Exception {
        QueryConditionChain conditionChain = new DefaultQueryConditionChain();
        conditionChain.addCondition(getSimpleEqualsCondition("First_value", "Second_value"));
        DbReadyStatementMetadata metadata = getWriterResult(conditionChain);

        Assert.assertEquals("(? = ?)", metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
    }

    @Test
    public void queryAndConditionChainTest()
    throws Exception {
        QueryConditionChain conditionChain = new DefaultQueryConditionChain();
        conditionChain.addCondition(getSimpleEqualsCondition("First_value", "Second_value"));
        conditionChain.addCondition(ConditionChainOperator.AND,
                getSimpleEqualsCondition("First_value2", "Second_value2"));
        DbReadyStatementMetadata metadata = getWriterResult(conditionChain);

        Assert.assertEquals("(? = ? AND ? = ?)", metadata.getSqlString().toString());
        Assert.assertEquals(4, metadata.getParameters().size());
    }

    @Test
    public void queryDoubleAndWithOrConditionChainTest()
    throws Exception {
        QueryConditionChain globalConditionChain = new DefaultQueryConditionChain();
        QueryConditionChain conditionChain = new DefaultQueryConditionChain();
        conditionChain.addCondition(getSimpleEqualsCondition("First_value", "Second_value"));
        conditionChain.addCondition(ConditionChainOperator.AND,
                getSimpleEqualsCondition("First_value2", "Second_value2"));
        globalConditionChain.addCondition(conditionChain);
        conditionChain = new DefaultQueryConditionChain();
        conditionChain.addCondition(getSimpleEqualsCondition("First_value3", "Second_value3"));
        conditionChain.addCondition(ConditionChainOperator.AND,
                getSimpleEqualsCondition("First_value4", "Second_value4"));
        globalConditionChain.addCondition(ConditionChainOperator.OR, conditionChain);
        DbReadyStatementMetadata metadata = getWriterResult(globalConditionChain);

        Assert.assertEquals("((? = ? AND ? = ?) OR (? = ? AND ? = ?))", metadata.getSqlString().toString());
        Assert.assertEquals(8, metadata.getParameters().size());
    }

    private DefaultQueryConditionWrapper getSimpleEqualsCondition(String firstValue, String secondValue) {
        return new DefaultQueryConditionWrapper(
                new DefaultConstantExpression(Types.VARCHAR, firstValue),
                ConditionOperator.EQUALS,
                new DefaultConstantExpression(Types.VARCHAR, secondValue));
    }
}
