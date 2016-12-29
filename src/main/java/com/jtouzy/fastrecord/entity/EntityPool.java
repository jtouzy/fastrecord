package com.jtouzy.fastrecord.entity;

import com.jtouzy.fastrecord.annotations.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Singleton EntityPool Bean
 * This bean is used to create and store all the EntityDescriptors.
 *
 * @author jtouzy
 */
@Service("FastRecord.Core.EntityPool")
public class EntityPool {
    private static final Logger logger = LoggerFactory.getLogger(EntityPool.class);
    private final Map<Class,EntityDescriptor> entityDescriptorsByClass;

    @Autowired
    private EntityLoader entityLoader;

    private EntityPool() {
        entityDescriptorsByClass = new LinkedHashMap<>();
    }

    public void initializeEntities(ApplicationContext applicationContext) {
        logger.debug("Registering all entities...");
        String[] entitiesBeanNames = applicationContext.getBeanNamesForAnnotation(Entity.class);
        Object unusedEntityInstance;
        List<Class> entityClasses = new ArrayList<>();
        for (String entityBeanName : entitiesBeanNames) {
            unusedEntityInstance = applicationContext.getBean(entityBeanName);
            entityClasses.add(unusedEntityInstance.getClass());
        }
        entityDescriptorsByClass.putAll(entityLoader.load(entityClasses));
        logger.debug("End registering entities.");
    }

    public Optional<EntityDescriptor> getEntityDescriptor(Class entityClass) {
        return Optional.ofNullable(entityDescriptorsByClass.get(entityClass));
    }

    public Set<EntityDescriptor> getEntityDescriptors() {
        return new LinkedHashSet<>(entityDescriptorsByClass.values());
    }
}
