package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.UpdateExpression;
import com.jtouzy.fastrecord.statements.context.impl.*;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultUpdateExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultUpdateExpressionWriterTest
        extends AbstractWriterTest<UpdateExpression,DefaultUpdateExpressionWriter> {

    public DefaultUpdateExpressionWriterTest() {
        super(DefaultUpdateExpressionWriter.class);
    }

    @Test
    public void singleColumnUpdateWithOneConditionTest()
    throws Exception {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression("table_name");
        UpdateExpression expression = new DefaultUpdateExpression(tableExpression);
        expression.getValues().put(
                new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                new DefaultConstantExpression(Types.VARCHAR, "updated_value"));
        expression.getConditionChain().addCondition(
                new DefaultUpdateConditionWrapper(
                        new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_where"),
                        ConditionOperator.EQUALS,
                        new DefaultConstantExpression(Types.VARCHAR, "condition_where")));
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("UPDATE table_name SET table_name.column_name = ? " +
                        "WHERE (table_name.column_where = ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("updated_value", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(1).getType());
        Assert.assertEquals("condition_where", metadata.getParameters().get(1).getValue());
    }
}
