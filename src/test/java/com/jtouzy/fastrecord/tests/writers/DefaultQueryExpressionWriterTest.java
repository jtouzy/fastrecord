package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context2.QueryExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryExpression;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers2.DefaultQueryExpressionWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultQueryExpressionWriterTest extends AbstractWriterTest<QueryExpression,DefaultQueryExpressionWriter> {
    public DefaultQueryExpressionWriterTest() {
        super(DefaultQueryExpressionWriter.class);
    }

    @Test
    public void querySingleAliasColumnSingleTargetWithoutConditionTest()
    throws Exception {
        QueryExpression queryExpression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias",
                        new DefaultSimpleTableExpression("table_name")));
        queryExpression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper(
                        "column_alias",
                        new DefaultAliasTableColumnExpression(
                                Types.VARCHAR,
                                new DefaultAliasTableExpression("table_name", "table_alias"),
                                "column_name")));
        DbReadyStatementMetadata metadata = getWriterResult(queryExpression);

        Assert.assertEquals("SELECT table_alias.column_name as column_alias FROM table_name table_alias",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
