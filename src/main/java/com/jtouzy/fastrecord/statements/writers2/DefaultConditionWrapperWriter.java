package com.jtouzy.fastrecord.statements.writers2;

import com.jtouzy.fastrecord.annotations.support.Writes;
import com.jtouzy.fastrecord.statements.context2.ConditionWrapper;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("FastRecord.Writer.DefaultConditionWrapperWriter")
@Scope("prototype")
@Writes(ConditionWrapper.class)
public class DefaultConditionWrapperWriter extends AbstractWriter<ConditionWrapper> {
    @Override
    public void write() {
        mergeWriter(getContext().getFirstConditionExpression());
        appendOperator();
        mergeWriter(getContext().getCompareConditionExpression());
    }

    private void appendOperator() {
        DbReadyStatementMetadata metadata = getResult();
        switch (getContext().getConditionOperator()) {
            case EQUALS:
                metadata.getSqlString().append(" = ");
                break;
            case NOT_EQUALS:
                metadata.getSqlString().append(" != ");
                break;
            case IN:
                metadata.getSqlString().append(" IN ");
                break;
            case NOT_IN:
                metadata.getSqlString().append(" NOT IN ");
                break;
            case LIKE:
                metadata.getSqlString().append(" LIKE ");
                break;
            case NOT_LIKE:
                metadata.getSqlString().append(" NOT LIKE ");
                break;
        }
    }
}
