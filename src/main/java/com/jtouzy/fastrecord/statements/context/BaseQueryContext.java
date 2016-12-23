package com.jtouzy.fastrecord.statements.context;

import java.util.ArrayList;
import java.util.List;

public class BaseQueryContext implements QueryContext {
    private final List<AliasExpressionContext> columnContextList;
    private final JoinListContext joinListContext;
    private final ConditionsContext conditionsContext;

    public BaseQueryContext(TableAliasContext mainTableContext) {
        this.columnContextList = new ArrayList<>();
        this.joinListContext = createJoinListContext(mainTableContext);
        this.conditionsContext = createConditionsContext();
    }

    protected JoinListContext createJoinListContext(TableAliasContext mainTableContext) {
        return new BaseJoinListContext(mainTableContext);
    }

    protected ConditionsContext createConditionsContext() {
        return new BaseConditionsContext();
    }

    @Override
    public List<AliasExpressionContext> getColumnContextList() {
        return columnContextList;
    }

    @Override
    public JoinListContext getJoinListContext() {
        return joinListContext;
    }

    @Override
    public void addColumnContext(AliasExpressionContext expressionContext) {
        this.columnContextList.add(expressionContext);
    }

    @Override
    public ConditionsContext getConditionsContext() {
        return this.conditionsContext;
    }
}
