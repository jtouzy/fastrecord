package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DefaultConditionsProcessor<T,E extends WritableContext>
        extends DefaultProcessor<T,E> implements ConditionsProcessor<T,E> {

    // =============================================================================
    // Attributes
    // =============================================================================

    private ConditionChain mainConditionChain;
    private List<ConditionChain> conditionChainHierarchy;
    private ConditionChain currentConditionChain;

    // =============================================================================
    // Constructors
    // =============================================================================

    public DefaultConditionsProcessor(WriterCache writerCache, EntityPool entityPool,
                                      FastRecordConfiguration configuration) {
        super(writerCache, entityPool, configuration);
    }

    // =============================================================================
    // Abstract methods
    // =============================================================================

    protected abstract ConditionChain createDefaultConditionChain();
    protected abstract ConditionChain createConditionWrapper(ColumnDescriptor columnDescriptor,
                                                             ConditionOperator operator, Object value);

    // =============================================================================
    // Interface overrides
    // =============================================================================

    @Override
    public ConditionsProcessor chain() {
        chain(ConditionChainOperator.AND);
        return this;
    }

    @Override
    public ConditionsProcessor and() {
        end();
        chain();
        return this;
    }

    @Override
    public ConditionsProcessor or() {
        end();
        chain(ConditionChainOperator.OR);
        return this;
    }

    @Override
    public ConditionsProcessor end() {
        if (currentConditionChain.getChain().size() == 0) {
            throw new IllegalStateException("Cannot close a chain without conditions");
        }
        conditionChainHierarchy.remove(currentConditionChain);
        currentConditionChain = conditionChainHierarchy.get(conditionChainHierarchy.size() - 1);
        return this;
    }

    @Override
    public ConditionsProcessor eq(String columnName, Object value) {
        return andEq(columnName, value);
    }

    @Override
    public ConditionsProcessor notEq(String columnName, Object value) {
        return andNotEq(columnName, value);
    }

    @Override
    public ConditionsProcessor like(String columnName, Object value) {
        return andLike(columnName, value);
    }

    @Override
    public ConditionsProcessor notLike(String columnName, Object value) {
        return andNotLike(columnName, value);
    }

    @Override
    public ConditionsProcessor andEq(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.EQUALS, value);
        return this;
    }

    @Override
    public ConditionsProcessor andNotEq(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_EQUALS, value);
        return this;
    }

    @Override
    public ConditionsProcessor andLike(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.LIKE, value);
        return this;
    }

    @Override
    public ConditionsProcessor andNotLike(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_LIKE, value);
        return this;
    }

    @Override
    public ConditionsProcessor orEq(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.EQUALS, value);
        return this;
    }

    @Override
    public ConditionsProcessor orNotEq(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_EQUALS, value);
        return this;
    }

    @Override
    public ConditionsProcessor orLike(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.LIKE, value);
        return this;
    }

    @Override
    public ConditionsProcessor orNotLike(String columnName, Object value) {
        createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_LIKE, value);
        return this;
    }

    // =============================================================================
    // Protected methods
    // =============================================================================

    protected void initConditionsProcessor(ConditionChain mainConditionChain) {
        this.mainConditionChain = mainConditionChain;
    }

    // =============================================================================
    // Private methods
    // =============================================================================

    private void initializeIfNeeded() {
        if (conditionChainHierarchy == null) {
            conditionChainHierarchy = new ArrayList<>();
            currentConditionChain = mainConditionChain;
            ConditionChain rootChain = createDefaultConditionChain();
            ConditionsHelper.addCondition(currentConditionChain, ConditionChainOperator.AND, rootChain);
            currentConditionChain = rootChain;
            conditionChainHierarchy.add(currentConditionChain);
        }
    }

    private void chain(ConditionChainOperator chainOperator) {
        initializeIfNeeded();
        QueryConditionChain newChain = new DefaultQueryConditionChain();
        conditionChainHierarchy.add(newChain);
        ConditionsHelper.addCondition(currentConditionChain, chainOperator, newChain);
        currentConditionChain = newChain;
    }

    @SuppressWarnings("unchecked")
    private void createSimpleCondition(ConditionChainOperator chainOperator, String columnName,
                                       ConditionOperator operator, Object value) {
        initializeIfNeeded();
        Optional<ColumnDescriptor> columnDescriptorOptional =
                getEntityDescriptor().getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, getEntityDescriptor().getClazz());
        }
        ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
        checkValueType(columnDescriptor, value);

        ConditionChain wrapper = createConditionWrapper(columnDescriptor, operator, value);
        ConditionsHelper.addCondition(currentConditionChain, chainOperator, wrapper);
    }

    @SuppressWarnings("unchecked")
    private void checkValueType(ColumnDescriptor descriptor, Object value) {
        ColumnDescriptor currentDescriptor = descriptor;
        Class propertyType = descriptor.getPropertyType();
        while (currentDescriptor.isRelated()) {
            currentDescriptor = currentDescriptor.getRelatedColumn();
            propertyType = currentDescriptor.getPropertyType();
        }
        if (!propertyType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The condition value of column [" + descriptor.getColumnName() +
                    " (property: " + descriptor.getPropertyName() + ")] must be type of [" + propertyType + "]");
        }
    }
}
