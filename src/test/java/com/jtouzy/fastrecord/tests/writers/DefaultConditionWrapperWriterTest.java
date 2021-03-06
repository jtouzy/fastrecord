package com.jtouzy.fastrecord.tests.writers;

import com.jtouzy.fastrecord.statements.context.AliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionWrapper;
import com.jtouzy.fastrecord.statements.context.QueryExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableColumnExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultAliasTableExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryColumnExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryExpression;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryWrapper;
import com.jtouzy.fastrecord.statements.context.impl.DefaultSimpleTableExpression;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.statements.writers.DefaultConditionWrapperWriter;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DefaultConditionWrapperWriterTest
        extends AbstractWriterTest<ConditionWrapper,DefaultConditionWrapperWriter> {

    public DefaultConditionWrapperWriterTest() {
        super(DefaultConditionWrapperWriter.class);
    }

    @Test
    public void queryDoubleConstantEqualsConditionTest()
    throws Exception {
        ConditionWrapper wrapper = new DefaultQueryConditionWrapper(
                new DefaultConstantExpression(Types.VARCHAR, "Constant_value"),
                ConditionOperator.EQUALS,
                new DefaultConstantExpression(Types.VARCHAR, "Constant_value2"));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("? = ?", metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Constant_value", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(1).getType());
        Assert.assertEquals("Constant_value2", metadata.getParameters().get(1).getValue());
    }

    @Test
    public void queryDoubleTableColumnEqualsConditionTest()
    throws Exception {
        ConditionWrapper wrapper = new DefaultQueryConditionWrapper(
                getAliasTableColumnExpression(
                        "table_name", "table_alias", "column_name"),
                ConditionOperator.EQUALS,
                getAliasTableColumnExpression(
                        "table_name2", "table_alias2", "column_name2"));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("table_alias.column_name = table_alias2.column_name2",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void queryExistsConditionTest()
    throws Exception {
        QueryExpression expression = new DefaultQueryExpression(
                new DefaultQueryTargetExpressionWrapper(
                        "table_alias",
                        new DefaultSimpleTableExpression("table_name")));
        expression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper(
                        "column_alias",
                        new DefaultAliasTableColumnExpression(
                                Types.VARCHAR,
                                new DefaultAliasTableExpression("table_name", "table_alias"),
                                "column_name")));
        ConditionWrapper wrapper = new DefaultQueryConditionWrapper(
                ConditionOperator.EXISTS, new DefaultQueryWrapper(expression));
        DbReadyStatementMetadata metadata = getWriterResult(wrapper);

        Assert.assertEquals("EXISTS (SELECT table_alias.column_name as column_alias " +
                        "FROM table_name table_alias)", metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    private AliasTableColumnExpression getAliasTableColumnExpression(String tableName, String tableAlias,
                                                                     String columnName) {
        return new DefaultAliasTableColumnExpression(
                Types.VARCHAR, new DefaultAliasTableExpression(tableName, tableAlias), columnName);
    }
}
