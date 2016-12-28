package com.jtouzy.fastrecord.tests.context;

import com.jtouzy.fastrecord.statements.context2.QueryExpression;
import com.jtouzy.fastrecord.statements.context2.QueryTargetExpressionWrapper;
import com.jtouzy.fastrecord.statements.context2.impl.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class QueryExpressionTest {
    @Test
    public void simpleQueryExpressionTest() {
        // Context
        QueryTargetExpressionWrapper targetExpressionWrapper =
                new DefaultQueryTargetExpressionWrapper(
                        "main_table_alias",
                        new DefaultSimpleTableExpression("table_name"));
        QueryExpression queryExpression = new DefaultQueryExpression(targetExpressionWrapper);
        queryExpression.getColumns().add(
                new DefaultQueryColumnExpressionWrapper(
                        "column_alias",
                        new DefaultAliasTableColumnExpression(Types.VARCHAR, null, "column")));
        // Assertions
        Assert.assertEquals(DefaultQueryTargetExpressionWrapper.class,
                queryExpression.getMainTargetExpression().getClass());
    }
}
