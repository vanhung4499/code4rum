package com.hnv99.forum.core.sensitive.ibatis;

import com.hnv99.forum.core.sensitive.ano.SensitiveField;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Configuration related to sensitive words, with higher priority given to configurations in the database configuration table, supports dynamic refreshing.
 */
@Data
public class SensitiveObjectMeta {
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";

    /**
     * Whether to enable desensitization
     */
    private Boolean enabledSensitiveReplace;

    /**
     * Class name
     */
    private String className;

    /**
     * Members annotated with SensitiveField
     */
    private List<SensitiveFieldMeta> sensitiveFieldMetaList;

    public static Optional<SensitiveObjectMeta> buildSensitiveObjectMeta(Object param) {
        if (Objects.isNull(param)) {
            return Optional.empty();
        }

        Class<?> clazz = param.getClass();
        SensitiveObjectMeta sensitiveObjectMeta = new SensitiveObjectMeta();
        sensitiveObjectMeta.setClassName(clazz.getName());

        List<SensitiveFieldMeta> sensitiveFieldMetaList = new ArrayList<>();
        sensitiveObjectMeta.setSensitiveFieldMetaList(sensitiveFieldMetaList);
        boolean sensitiveField = parseAllSensitiveFields(clazz, sensitiveFieldMetaList);
        sensitiveObjectMeta.setEnabledSensitiveReplace(sensitiveField);
        return Optional.of(sensitiveObjectMeta);
    }

    private static boolean parseAllSensitiveFields(Class<?> clazz, List<SensitiveFieldMeta> sensitiveFieldMetaList) {
        Class<?> tempClazz = clazz;
        boolean hasSensitiveField = false;
        while (Objects.nonNull(tempClazz) && !JAVA_LANG_OBJECT.equalsIgnoreCase(tempClazz.getName())) {
            for (Field field : tempClazz.getDeclaredFields()) {
                SensitiveField sensitiveField = field.getAnnotation(SensitiveField.class);
                if (Objects.nonNull(sensitiveField)) {
                    SensitiveFieldMeta sensitiveFieldMeta = new SensitiveFieldMeta();
                    sensitiveFieldMeta.setName(field.getName());
                    sensitiveFieldMeta.setBindField(sensitiveField.bind());
                    sensitiveFieldMetaList.add(sensitiveFieldMeta);
                    hasSensitiveField = true;
                }
            }
            tempClazz = tempClazz.getSuperclass();
        }
        return hasSensitiveField;
    }

    @Data
    public static class SensitiveFieldMeta {
        /**
         * By default, find fields in the database with the same name as the field
         */
        private String name;

        /**
         * Alias for the bound database field
         */
        private String bindField;
    }
}
