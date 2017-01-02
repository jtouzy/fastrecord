package com.jtouzy.fastrecord.builders;

import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import com.jtouzy.fastrecord.entity.ColumnDescriptor;
import com.jtouzy.fastrecord.entity.ColumnNotFoundException;
import com.jtouzy.fastrecord.entity.EntityPool;
import com.jtouzy.fastrecord.statements.context.ConditionChain;
import com.jtouzy.fastrecord.statements.context.ConditionChainHolder;
import com.jtouzy.fastrecord.statements.context.ConditionChainOperator;
import com.jtouzy.fastrecord.statements.context.ConditionOperator;
import com.jtouzy.fastrecord.statements.context.QueryConditionChain;
import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.context.impl.DefaultQueryConditionChain;
import com.jtouzy.fastrecord.statements.writers.WriterCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class EntityBasedConditionsProcessor<T,E extends WritableContext & ConditionChainHolder>
        extends EntityBasedProcessor<T,E> {

    // ---------------------------------------------------------------------------------------------
    // Private properties
    // ---------------------------------------------------------------------------------------------

    protected EntityBasedConditionsProcessor.ConditionsConfigurer conditionsConfigurer;

    // ---------------------------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------------------------

    public EntityBasedConditionsProcessor(EntityPool entityPool, WriterCache writerCache,
                                          FastRecordConfiguration configuration) {
        super(entityPool, writerCache, configuration);
    }

    // ---------------------------------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------------------------------

    /**
     * Getter to access the Public Conditions API to add conditions to the statement.
     * To get back to the Entity API, use <code>endConditions()</code> method on the Conditions API.
     *
     * @return The ConditionsConfigurer to manage conditions on the statement
     */
    public ConditionsConfigurer conditions() {
        return conditionsConfigurer;
    }

    // ---------------------------------------------------------------------------------------------
    // Private class to manage conditions
    // ---------------------------------------------------------------------------------------------

    /**
     * Manage conditions on statement.
     */
    public abstract class ConditionsConfigurer {
        protected EntityBasedConditionsProcessor<T,E> getProcessor() {
            return EntityBasedConditionsProcessor.this;
        }

        private List<ConditionChain> conditionChainHierarchy;
        private ConditionChain parentConditionChain;
        private ConditionChain currentConditionChain;

        protected ConditionsConfigurer(ConditionChain parentConditionChain) {
            this.parentConditionChain = parentConditionChain;
        }

        protected abstract ConditionChain createDefaultConditionChain();
        protected abstract ConditionChain createConditionWrapper(ColumnDescriptor columnDescriptor,
                                                                 ConditionOperator operator, Object value);

        public ConditionsConfigurer chain() {
            return chain(ConditionChainOperator.AND);
        }

        public ConditionsConfigurer and() {
            end();
            chain();
            return this;
        }

        public ConditionsConfigurer or() {
            end();
            chain(ConditionChainOperator.OR);
            return this;
        }

        public ConditionsConfigurer end() {
            if (currentConditionChain.getChain().size() == 0) {
                throw new IllegalStateException("Cannot close a chain without conditions");
            }
            conditionChainHierarchy.remove(currentConditionChain);
            currentConditionChain = conditionChainHierarchy.get(conditionChainHierarchy.size() - 1);
            return this;
        }

        private ConditionsConfigurer chain(ConditionChainOperator chainOperator) {
            initializeIfNeeded();
            QueryConditionChain newChain = new DefaultQueryConditionChain();
            conditionChainHierarchy.add(newChain);
            ConditionsHelper.addCondition(currentConditionChain, chainOperator, newChain);
            currentConditionChain = newChain;
            return this;
        }

        // --------------------------------------------------------------------------------------------------------
        // Public conditions API
        // --------------------------------------------------------------------------------------------------------

        public ConditionsConfigurer eq(String columnName, Object value) {
            return andEq(columnName, value);
        }
        public ConditionsConfigurer notEq(String columnName, Object value) {
            return andNotEq(columnName, value);
        }
        public ConditionsConfigurer like(String columnName, Object value) {
            return andLike(columnName, value);
        }
        public ConditionsConfigurer notLike(String columnName, Object value) {
            return andNotLike(columnName, value);
        }
        public ConditionsConfigurer andEq(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.EQUALS, value);
        }
        public ConditionsConfigurer andNotEq(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_EQUALS, value);
        }
        public ConditionsConfigurer andLike(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.LIKE, value);
        }
        public ConditionsConfigurer andNotLike(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.AND, columnName, ConditionOperator.NOT_LIKE, value);
        }
        public ConditionsConfigurer orEq(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.EQUALS, value);
        }
        public ConditionsConfigurer orNotEq(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_EQUALS, value);
        }
        public ConditionsConfigurer orLike(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.LIKE, value);
        }
        public ConditionsConfigurer orNotLike(String columnName, Object value) {
            return createSimpleCondition(ConditionChainOperator.OR, columnName, ConditionOperator.NOT_LIKE, value);
        }

        public EntityBasedConditionsProcessor<T,E> endConditions() {
            return getProcessor();
        }

        // --------------------------------------------------------------------------------------------------------
        // Global utility methods
        // --------------------------------------------------------------------------------------------------------

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

        @SuppressWarnings("unchecked")
        private ConditionsConfigurer createSimpleCondition(ConditionChainOperator chainOperator, String columnName,
                                                           ConditionOperator operator, Object value) {
            initializeIfNeeded();
            Optional<ColumnDescriptor> columnDescriptorOptional =
                    getProcessor().getEntityDescriptor().getColumnDescriptorByColumn(columnName);
            if (!columnDescriptorOptional.isPresent()) {
                throw new ColumnNotFoundException(columnName, getProcessor().getEntityDescriptor().getClazz());
            }
            ColumnDescriptor columnDescriptor = columnDescriptorOptional.get();
            checkValueType(columnDescriptor, value);

            ConditionChain wrapper = createConditionWrapper(columnDescriptor, operator, value);
            ConditionsHelper.addCondition(currentConditionChain, chainOperator, wrapper);
            return this;
        }

        private void initializeIfNeeded() {
            if (conditionChainHierarchy == null) {
                conditionChainHierarchy = new ArrayList<>();
                currentConditionChain = parentConditionChain;
                ConditionChain rootChain = createDefaultConditionChain();
                // QueryConditionChain rootChain = new DefaultQueryConditionChain();
                ConditionsHelper.addCondition(currentConditionChain, ConditionChainOperator.AND, rootChain);
                currentConditionChain = rootChain;
                conditionChainHierarchy.add(currentConditionChain);
            }
        }
    }
}
