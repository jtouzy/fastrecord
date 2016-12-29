package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.QueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultQueryTargetExpressionWrapperWriter;
import org.junit.Assert;
import org.junit.Test;

public class DefaultQueryTargetExpressionWrapperWriterTest
        extends AbstractWriterTest<QueryTargetExpressionWrapper,DefaultQueryTargetExpressionWrapperWriter> {

    public DefaultQueryTargetExpressionWrapperWriterTest() {
        super(DefaultQueryTargetExpressionWrapperWriter.class);
    }

    @Test
    public void queryTargetTableTest()
    throws Exception {
        QueryTargetExpressionWrapper wrapper =
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias", new DefaultSimpleTableExpression("table_name"));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("table_name table_alias", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void queryTargetTableWithoutAliasTest()
            throws Exception {
        QueryTargetExpressionWrapper wrapper =
                new DefaultQueryTargetExpressionWrapper(
                        "", new DefaultSimpleTableExpression("table_name"));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("table_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
