package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context2.QueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers2.DefaultQueryTargetExpressionWrapperWriter;
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
