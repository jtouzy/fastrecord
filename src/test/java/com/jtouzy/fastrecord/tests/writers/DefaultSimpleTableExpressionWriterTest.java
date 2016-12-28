package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context2.SimpleTableExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers2.DefaultSimpleTableExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

public class DefaultSimpleTableExpressionWriterTest
        extends AbstractWriterTest<SimpleTableExpression,DefaultSimpleTableExpressionWriter> {

    public DefaultSimpleTableExpressionWriterTest() {
        super(DefaultSimpleTableExpressionWriter.class);
    }

    @Test
    public void simpleTableExpressionWriterTest()
    throws Exception {
        SimpleTableExpression expression = new DefaultSimpleTableExpression("table_name");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
