package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context2.AliasTableExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers2.DefaultAliasTableExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

public class DefaultAliasTableExpressionWriterTest
        extends AbstractWriterTest<AliasTableExpression,DefaultAliasTableExpressionWriter> {

    public DefaultAliasTableExpressionWriterTest() {
        super(DefaultAliasTableExpressionWriter.class);
    }

    @Test
    public void aliasTableExpressionTest()
    throws Exception {
        AliasTableExpression expression = new DefaultAliasTableExpression("table_name", "table_alias");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_alias", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void withoutAliasTableExpressionTest()
    throws Exception {
        AliasTableExpression expression = new DefaultAliasTableExpression("table_name", "");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
