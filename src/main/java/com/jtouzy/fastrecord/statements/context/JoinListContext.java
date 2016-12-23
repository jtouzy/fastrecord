package com.jtouzy.fastrecord.statements.context;

import java.util.List;

public interface JoinListContext {
    void addJoinContext(JoinContext joinContext);
    TableAliasContext getMainTableContext();
    List<JoinContext> getJoinContextList();
}
