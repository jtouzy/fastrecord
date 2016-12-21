package com.jtouzy.fastrecord.entity;

import com.jtouzy.fastrecord.config.ConfigurationBased;
import com.jtouzy.fastrecord.config.FastRecordConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Singleton EntityPool Bean
 * This bean is used to create and store all the EntityDescriptors auto-registered by EntityLoader.
 *
 * @author jtouzy
 */
@Service
public class EntityPool extends ConfigurationBased {
    private final LinkedHashMap<Class,EntityDescriptor> entityDescriptorsByClass;

    @Autowired
    private EntityPool(FastRecordConfiguration configuration, EntityLoader entityLoader) {
        super(configuration);
        this.entityDescriptorsByClass = new LinkedHashMap<>();
        this.entityDescriptorsByClass.putAll(entityLoader.load());
    }

    public Optional<EntityDescriptor> getEntityDescriptor(Class entityClass) {
        return Optional.ofNullable(entityDescriptorsByClass.get(entityClass));
    }

    public Set<EntityDescriptor> getEntityDescriptors() {
        return new LinkedHashSet<>(entityDescriptorsByClass.values());
    }
}
