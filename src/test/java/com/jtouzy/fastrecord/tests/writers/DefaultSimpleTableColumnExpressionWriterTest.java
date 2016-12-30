package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.SimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultSimpleTableColumnExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultSimpleTableColumnExpressionWriterTest
        extends AbstractWriterTest<SimpleTableColumnExpression,DefaultSimpleTableColumnExpressionWriter> {

    public DefaultSimpleTableColumnExpressionWriterTest() {
        super(DefaultSimpleTableColumnExpressionWriter.class);
    }

    @Test
    public void simpleTableColumnExpressionWriterTest()
    throws Exception {
        SimpleTableColumnExpression expression = new DefaultSimpleTableColumnExpression(Types.VARCHAR, new DefaultSimpleTableExpression("table_name"), "column_name");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_name.column_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
