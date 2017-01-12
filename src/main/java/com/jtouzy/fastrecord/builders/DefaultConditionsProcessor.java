package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.ConditionWrapper;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.context.impl.DefaultConstantExpression;
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
    protected abstract <C extends ConditionChain & ConditionWrapper> C createConditionWrapper(
            ColumnDescriptor columnDescriptor, ConditionOperator operator, Object value);

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
    public ConditionsProcessor in(String columnName, List<?> values) {
        return andIn(columnName, values);
    }

    @Override
    public ConditionsProcessor notIn(String columnName, List<?> values) {
        return andNotIn(columnName, values);
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
    public ConditionsProcessor andIn(String columnName, List<?> values) {
        createMultipleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.IN, values);
        return this;
    }

    @Override
    public ConditionsProcessor andNotIn(String columnName, List<?> values) {
        createMultipleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_IN, values);
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

    @Override
    public ConditionsProcessor orIn(String columnName, List<?> values) {
        createMultipleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.IN, values);
        return this;
    }

    @Override
    public ConditionsProcessor orNotIn(String columnName, List<?> values) {
        createMultipleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_IN, values);
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
        ColumnDescriptor columnDescriptor = safeGetColumnDescriptor(columnName);
        checkValueType(columnDescriptor, getPropertyType(columnDescriptor), value);

        ConditionChain wrapper = createConditionWrapper(columnDescriptor, operator, value);
        ConditionsHelper.addCondition(currentConditionChain, chainOperator, wrapper);
    }

    @SuppressWarnings("unchecked")
    private <C extends ConditionChain & ConditionWrapper> void createMultipleCondition(
            ConditionChainOperator chainOperator, String columnName, ConditionOperator operator, List<?> values) {
        if (values.isEmpty()) {
            throw new IllegalStateException("At least one value must be set for multiple conditions");
        }
        if (values.size() == 1) {
            createSimpleCondition(chainOperator, columnName, ConditionOperator.EQUALS, values.get(0));
        } else {
            initializeIfNeeded();
            ColumnDescriptor columnDescriptor = safeGetColumnDescriptor(columnName);
            checkValuesType(columnDescriptor, values);
            List<?> tempValues = new ArrayList<>(values);
            C wrapper = createConditionWrapper(columnDescriptor, operator, tempValues.get(0));
            tempValues.remove(0);
            for (Object value : tempValues) {
                wrapper.getCompareConditionExpressions().add(
                        new DefaultConstantExpression(
                                columnDescriptor.getColumnType(),
                                columnDescriptor.getTypeManager().convertToDatabase(value)));
            }
            ConditionsHelper.addCondition(currentConditionChain, chainOperator, wrapper);
        }
    }

    private ColumnDescriptor safeGetColumnDescriptor(String columnName) {
        Optional<ColumnDescriptor> columnDescriptorOptional =
                getEntityDescriptor().getColumnDescriptorByColumn(columnName);
        if (!columnDescriptorOptional.isPresent()) {
            throw new ColumnNotFoundException(columnName, getEntityDescriptor().getClazz());
        }
        return columnDescriptorOptional.get();
    }

    private void checkValuesType(ColumnDescriptor descriptor, List<?> values) {
        Class propertyType = getPropertyType(descriptor);
        for (Object value : values) {
            checkValueType(descriptor, propertyType, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkValueType(ColumnDescriptor descriptor, Class propertyType, Object value) {
        if (!propertyType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The condition value of column [" + descriptor.getColumnName() +
                    " (property: " + descriptor.getPropertyName() + ")] must be type of [" + propertyType + "]");
        }
    }

    private Class getPropertyType(ColumnDescriptor descriptor) {
        ColumnDescriptor currentDescriptor = descriptor;
        Class propertyType = descriptor.getPropertyType();
        while (currentDescriptor.isRelated()) {
            currentDescriptor = currentDescriptor.getRelatedColumn();
            propertyType = currentDescriptor.getPropertyType();
        }
        return propertyType;
    }
}
