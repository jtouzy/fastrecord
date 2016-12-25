package com.jtouzy.fastrecord.entity.types;

import com.google.common.base.Strings;
import com.jtouzy.fastrecord.annotations.support.Converts;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class TypeManagerPool {
    private static final Logger logger = LoggerFactory.getLogger(TypeManagerPool.class);
    private static final String DEFAULT_TYPE_MANAGERS_CLASS_PACKAGE = "com.jtouzy.fastrecord.entity.types.impl";

    private Map<Class,TypeManager> defaultTypeManagers = new LinkedHashMap<>();
    private Map<Class,TypeManager> typeManagers = new LinkedHashMap<>();

    private TypeManagerPool() {
        this.initializeTypeManagers();
    }

    private void initializeTypeManagers() {
        putInCollection(DEFAULT_TYPE_MANAGERS_CLASS_PACKAGE, defaultTypeManagers);
    }

    @SuppressWarnings("unchecked")
    private void putInCollection(String classPackage, Map<Class,TypeManager> collection) {
        if (Strings.isNullOrEmpty(classPackage)) {
            return;
        }
        Reflections reflections = new Reflections(classPackage);
        Set<Class<?>> typeManagersClasses = reflections.getTypesAnnotatedWith(Converts.class);
        for (Class<?> typeManagerClass : typeManagersClasses) {
            Class baseClass = (typeManagerClass.getAnnotation(Converts.class)).value();
            if (TypeManager.class.isAssignableFrom(typeManagerClass)) {
                collection.put(baseClass, getStoredInstance((Class<? extends TypeManager>)typeManagerClass));
            } else {
                throw new TypeManagerDefinitionException("@Converts annotated class " + typeManagerClass +
                        " must implement TypeManager interface");
            }
        }
    }

    private TypeManager getStoredInstance(Class<? extends TypeManager> instanceClass) {
        try {
            return instanceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new TypeManagerDefinitionException(ex);
        }
    }

    public Optional<TypeManager> getTypeManager(Class type) {
        return Optional.ofNullable(defaultTypeManagers.getOrDefault(type, typeManagers.get(type)));
    }
}
