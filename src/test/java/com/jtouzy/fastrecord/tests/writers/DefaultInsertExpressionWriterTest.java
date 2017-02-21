package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.ConstantExpression;
import com.jtouzy.fastrecord.statements.context.InsertExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultInsertExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultInsertExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

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
        Map<SimpleTableColumnExpression,ConstantExpression> values = new LinkedHashMap<>();
        values.put(new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                new DefaultConstantExpression(Types.VARCHAR, "column_value"));
        expression.getValues().add(values);
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
        Map<SimpleTableColumnExpression,ConstantExpression> values = new LinkedHashMap<>();
        values.put(new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                new DefaultConstantExpression(Types.VARCHAR, "column_value"));
        values.put(new DefaultSimpleTableColumnExpression(Types.INTEGER, tableExpression, "column_name2"),
                new DefaultConstantExpression(Types.INTEGER, "2"));
        expression.getValues().add(values);
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("INSERT INTO table_name (column_name, column_name2) VALUES (?, ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("column_value", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(1).getType());
        Assert.assertEquals("2", metadata.getParameters().get(1).getValue());
    }

    @Test
    public void twoColumnAndMultipleValuesInsertExpressionTest()
    throws Exception {
        SimpleTableExpression tableExpression = new DefaultSimpleTableExpression("table_name");
        InsertExpression expression = new DefaultInsertExpression(tableExpression);
        Map<SimpleTableColumnExpression,ConstantExpression> values = new LinkedHashMap<>();
        values.put(new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                new DefaultConstantExpression(Types.VARCHAR, "column_value"));
        values.put(new DefaultSimpleTableColumnExpression(Types.INTEGER, tableExpression, "column_name2"),
                new DefaultConstantExpression(Types.INTEGER, "2"));
        expression.getValues().add(values);
        values = new LinkedHashMap<>();
        values.put(new DefaultSimpleTableColumnExpression(Types.VARCHAR, tableExpression, "column_name"),
                new DefaultConstantExpression(Types.VARCHAR, "column_value2"));
        values.put(new DefaultSimpleTableColumnExpression(Types.INTEGER, tableExpression, "column_name2"),
                new DefaultConstantExpression(Types.INTEGER, "3"));
        expression.getValues().add(values);
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("INSERT INTO table_name (column_name, column_name2) VALUES (?, ?), (?, ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(4, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("column_value", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(1).getType());
        Assert.assertEquals("2", metadata.getParameters().get(1).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(2).getType());
        Assert.assertEquals("column_value2", metadata.getParameters().get(2).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(3).getType());
        Assert.assertEquals("3", metadata.getParameters().get(3).getValue());
    }
}
