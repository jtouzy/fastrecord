package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.DeleteExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.*;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultDeleteExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultDeleteExpressionWriterTest
        extends AbstractWriterTest<DeleteExpression, DefaultDeleteExpressionWriter> {

    public DefaultDeleteExpressionWriterTest() {
        super(DefaultDeleteExpressionWriter.class);
    }

    @Test
    public void singleConditionDeleteExpressionTest()
    throws Exception {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression("table_name");
        DeleteExpression expression = new DefaultDeleteExpression(tableExpression);
        expression.getConditionChain().addCondition(
                new DefaultUpdateConditionWrapper(
                        new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_where"),
                        ConditionOperator.EQUALS,
                        new DefaultConstantExpression(Types.VARCHAR, "condition_where")));
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("DELETE FROM table_name WHERE (table_name.column_where = ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("condition_where", metadata.getParameters().get(0).getValue());
    }
}
