package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context2.ConstantExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers2.DefaultConstantExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultConstantExpressionWriterTest
        extends AbstractWriterTest<ConstantExpression,DefaultConstantExpressionWriter> {

    public DefaultConstantExpressionWriterTest() {
        super(DefaultConstantExpressionWriter.class);
    }

    @Test
    public void simpleConstantExpressionTest()
    throws Exception {
        ConstantExpression expression = new DefaultConstantExpression(Types.VARCHAR, "Constant_value");
        DbReadyStatementMetadata metadata = getWriterResult(expression);
        Assert.assertEquals("?", metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Constant_value", metadata.getParameters().get(0).getValue());
    }
}
