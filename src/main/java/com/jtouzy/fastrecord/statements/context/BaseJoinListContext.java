package com.jtouzy.fastrecord.statements.context;

import java.util.ArrayList;
import java.util.List;

public class BaseJoinListContext implements JoinListContext {
    private final TableAliasContext mainTableContext;
    private final List<JoinContext> joinContextList;

    public BaseJoinListContext(TableAliasContext mainFromContext) {
        this.mainTableContext = mainFromContext;
        this.joinContextList = new ArrayList<>();
    }

    @Override
    public void addJoinContext(JoinContext joinContext) {
        joinContextList.add(joinContext);
    }

    @Override
    public TableAliasContext getMainTableContext() {
        return mainTableContext;
    }

    @Override
    public List<JoinContext> getJoinContextList() {
        return joinContextList;
    }
}
