package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.AggregateFunctionType;
import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.AliasTableExpression;
import com.jtouzy.fastrecord.statements.context.JoinOperator;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.QueryWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAggregateFunctionExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultOrderByColumnWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionJoin;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultQueryExpressionWriter;
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

    @Test
    public void querySingleAggregateSingleTargetWithoutConditionTest()
    throws Exception {
        QueryExpression queryExpression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias",
                        new DefaultSimpleTableExpression("table_name")));
        queryExpression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper(
                        "column_alias",
                        new DefaultAggregateFunctionExpression(
                                AggregateFunctionType.COUNT,
                                new DefaultAliasTableColumnExpression(Types.VARCHAR,
                                        new DefaultAliasTableExpression("table_name", "table_alias"),
                                        "column_name"))));
        DbReadyStatementMetadata metadata = getWriterResult(queryExpression);

        Assert.assertEquals("SELECT COUNT(table_alias.column_name) as column_alias " +
                        "FROM table_name table_alias",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void querySingleAliasColumnSingleTargetWithoutConditionWithOrderByTest()
            throws Exception {
        AliasTableExpression tableExpression =
                new DefaultAliasTableExpression("table_name", "table_alias");
        AliasTableColumnExpression tableColumnExpression =
                new DefaultAliasTableColumnExpression(Types.VARCHAR, tableExpression, "column_name");
        QueryExpression queryExpression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias",
                        new DefaultSimpleTableExpression("table_name")));
        queryExpression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper("column_alias", tableColumnExpression));
        queryExpression.getOrderByColumns().add(new DefaultOrderByColumnWrapper(tableColumnExpression));
        DbReadyStatementMetadata metadata = getWriterResult(queryExpression);

        Assert.assertEquals("SELECT table_alias.column_name as column_alias FROM table_name table_alias " +
                        "ORDER BY table_alias.column_name DESC",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void querySingleSubQuerySingleTargetWithoutConditionTest()
    throws Exception {
        AliasTableExpression tableExpression =
                new DefaultAliasTableExpression("table_name", "table_alias");
        AliasTableColumnExpression tableColumnExpression =
                new DefaultAliasTableColumnExpression(Types.VARCHAR, tableExpression, "column_name");
        QueryExpression queryExpression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias",
                        new DefaultSimpleTableExpression("table_name")));
        queryExpression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper("column_alias", tableColumnExpression));

        QueryWrapper wrapper = new DefaultQueryWrapper(queryExpression);
        QueryExpression finalExpression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias_final",
                        new DefaultSimpleTableExpression("table_name_final")));
        finalExpression.getColumns().add(new DefaultQueryColumnExpressionWrapper("column_alias_final", wrapper));
        DbReadyStatementMetadata metadata = getWriterResult(finalExpression);

        Assert.assertEquals("SELECT (SELECT table_alias.column_name as column_alias FROM table_name table_alias) " +
                        "as column_alias_final FROM table_name_final table_alias_final",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void querySingleAliasColumnJoinTargetWithoutConditionTest()
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
        queryExpression.getTargetJoinList().add(
                new DefaultQueryTargetExpressionJoin(
                    queryExpression.getMainTargetExpression(),
                    JoinOperator.JOIN,
                    new DefaultQueryTargetExpressionWrapper(
                            "table_alias2",
                            new DefaultSimpleTableExpression("table_name2"))));
        DbReadyStatementMetadata metadata = getWriterResult(queryExpression);

        Assert.assertEquals("SELECT table_alias.column_name as column_alias " +
                        "FROM table_name table_alias, table_name2 table_alias2",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
