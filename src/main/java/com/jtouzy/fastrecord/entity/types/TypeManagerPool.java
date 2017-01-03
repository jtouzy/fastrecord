package com.jtouzy.fastrecord.entity.types;

import com.jtouzy.fastrecord.annotations.support.Converts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
        for (String typeManagerBeanName : typeManagerBeanNames) {
            typeManagerBean = (TypeManager)applicationContext.getBean(typeManagerBeanName);
            typeManagersByClass.put(
                    typeManagerBean.getClass().getAnnotation(Converts.class).value(),
                    typeManagerBean);
        }
        logger.debug("End registering type managers.");
    }

    public Optional<TypeManager> getTypeManager(Class type) {
        return Optional.ofNullable(typeManagersByClass.get(type));
    }
}
