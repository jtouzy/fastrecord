package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultAliasTableColumnExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultAliasTableColumnExpressionWriterTest
        extends AbstractWriterTest<AliasTableColumnExpression,DefaultAliasTableColumnExpressionWriter> {

    public DefaultAliasTableColumnExpressionWriterTest() {
        super(DefaultAliasTableColumnExpressionWriter.class);
    }

    @Test
    public void aliasTableColumnExpressionTest()
    throws Exception {
        AliasTableColumnExpression expression =
                new DefaultAliasTableColumnExpression(
                        Types.VARCHAR,
                        new DefaultAliasTableExpression("table_name", "table_alias"),
                        "table_column_name");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_alias.table_column_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void withoutAliasTableColumnExpressionTest()
            throws Exception {
        AliasTableColumnExpression expression =
                new DefaultAliasTableColumnExpression(
                        Types.VARCHAR,
                        new DefaultAliasTableExpression("table_name", ""),
                        "table_column_name");
        DbReadyStatementMetadata metadata = getWriterResult(expression);

        Assert.assertEquals("table_name.table_column_name", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
