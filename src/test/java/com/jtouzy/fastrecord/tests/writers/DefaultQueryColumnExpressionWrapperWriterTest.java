package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.QueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultQueryColumnExpressionWrapperWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultQueryColumnExpressionWrapperWriterTest
        extends AbstractWriterTest<QueryColumnExpressionWrapper,DefaultQueryColumnExpressionWrapperWriter> {

    public DefaultQueryColumnExpressionWrapperWriterTest() {
        super(DefaultQueryColumnExpressionWrapperWriter.class);
    }

    @Test
    public void queryColumnConstantTest()
    throws Exception {
        QueryColumnExpressionWrapper wrapper =
                new DefaultQueryColumnExpressionWrapper(
                        "column_alias", new DefaultConstantExpression(Types.VARCHAR, "Constant_value"));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("? as column_alias", metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Constant_value", metadata.getParameters().get(0).getValue());
    }
}
