package com.jtouzy.fastrecord.entity.types;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.fastrecord.annotations.support.Converts;
import com.jtouzy.fastrecord.utils.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service("FastRecord.Core.TypeManagerPool")
public class TypeManagerPool {
    private static final Logger logger = LoggerFactory.getLogger(TypeManagerPool.class);
    private final Map<Class,TypeManager> typeManagersByClass;

    public TypeManagerPool() {
        typeManagersByClass = new LinkedHashMap<>();
    }

    public void initializeTypeManagers(ApplicationContext applicationContext) {
        logger.debug("Registering type managers...");
        String[] typeManagerBeanNames = applicationContext.getBeanNamesForAnnotation(Converts.class);
        TypeManager typeManagerBean;
        Multimap<Class,TypeManager> classTypeManagers = ArrayListMultimap.create();
        Map<TypeManager,Integer> prioritiesByTypeManager = new HashMap<>();
        Converts annotation;
        for (String typeManagerBeanName : typeManagerBeanNames) {
            typeManagerBean = (TypeManager)applicationContext.getBean(typeManagerBeanName);
            annotation = typeManagerBean.getClass().getAnnotation(Converts.class);
            classTypeManagers.put(annotation.value(), typeManagerBean);
            prioritiesByTypeManager.put(typeManagerBean, annotation.priority());
        }
        typeManagersByClass.putAll(Priority.getPriorityMap(classTypeManagers, prioritiesByTypeManager));
        logger.debug("End registering type managers.");
    }

    public Optional<TypeManager> getTypeManager(Class type) {
        return Optional.ofNullable(typeManagersByClass.get(type));
    }
}
