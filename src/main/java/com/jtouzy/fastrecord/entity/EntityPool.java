package com.jtouzy.fastrecord.entity;

import com.jtouzy.fastrecord.config.Configuration;
import com.jtouzy.fastrecord.config.ConfigurationBased;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class EntityPool extends ConfigurationBased {
    private static EntityPool instance = null;

    private final LinkedHashMap<Class,EntityDescriptor> entityDescriptorsByClass;

    public static EntityPool init(Configuration configuration) {
        if (instance != null)
            throw new IllegalStateException("EntityPool already initialized!");
        instance = new EntityPool(configuration);
        return instance;
    }

    private EntityPool(Configuration configuration) {
        super(configuration);
        this.entityDescriptorsByClass = new LinkedHashMap<>();
        this.entityDescriptorsByClass.putAll(new EntityLoader(configuration).load());
    }

    public Optional<EntityDescriptor> getEntityDescriptor(Class entityClass) {
        return Optional.ofNullable(entityDescriptorsByClass.get(entityClass));
    }

    public Set<EntityDescriptor> getEntityDescriptors() {
        return new LinkedHashSet<>(entityDescriptorsByClass.values());
    }
}
