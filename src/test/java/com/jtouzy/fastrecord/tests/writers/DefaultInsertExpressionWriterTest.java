package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultInsertExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultInsertExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultInsertExpressionWriterTest
        extends AbstractWriterTest<InsertExpression,DefaultInsertExpressionWriter> {

    public DefaultInsertExpressionWriterTest() {
        super(DefaultInsertExpressionWriter.class);
    }

    @Test
    public void singleColumnInsertExpressionTest()
            throws Exception {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression("table_name");
        InsertExpression expression = new DefaultInsertExpression(tableExpression);
        expression.getValues().put(
                new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                "column_value");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("INSERT INTO table_name (column_name) VALUES (?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("column_value", metadata.getParameters().get(0).getValue());
    }

    @Test
    public void twoColumnInsertExpressionTest()
    throws Exception {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression("table_name");
        InsertExpression expression = new DefaultInsertExpression(tableExpression);
        expression.getValues().put(
                new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                "column_value");
        expression.getValues().put(
                new DefaultSimpleTableColumnExpression(Types.INTEGER, tableExpression, "column_name2"),
                "2");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("INSERT INTO table_name (column_name, column_name2) VALUES (?, ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("column_value", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(1).getType());
        Assert.assertEquals("2", metadata.getParameters().get(1).getValue());
    }
}
