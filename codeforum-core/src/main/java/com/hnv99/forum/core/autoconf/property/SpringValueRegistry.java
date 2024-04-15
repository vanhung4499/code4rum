package com.hnv99.forum.core.autoconf.property;

import com.hnv99.forum.core.util.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Configuration change registration
 *
 * @author YiHui
 * @date 2023/6/26
 */
@Slf4j
public class SpringValueRegistry {
    @Data
    public static class SpringValue {
        /**
         * Suitable for: configurations are injected and bound through set methods, with only one parameter, which is the corresponding configuration key
         */
        private MethodParameter methodParameter;
        /**
         * Member variable
         */
        private Field field;
        /**
         * Weak reference to the bean instance
         */
        private WeakReference<Object> beanRef;
        /**
         * Spring Bean Name
         */
        private String beanName;
        /**
         * Configuration key: such as config.user
         */
        private String key;
        /**
         * Configuration reference, such as ${config.user}
         */
        private String placeholder;
        /**
         * Target type bound to the configuration
         */
        private Class<?> targetType;

        public SpringValue(String key, String placeholder, Object bean, String beanName, Field field) {
            this.beanRef = new WeakReference<>(bean);
            this.beanName = beanName;
            this.field = field;
            this.key = key;
            this.placeholder = placeholder;
            this.targetType = field.getType();
        }

        public SpringValue(String key, String placeholder, Object bean, String beanName, Method method) {
            this.beanRef = new WeakReference<>(bean);
            this.beanName = beanName;
            this.methodParameter = new MethodParameter(method, 0);
            this.key = key;
            this.placeholder = placeholder;
            Class<?>[] paramTps = method.getParameterTypes();
            this.targetType = paramTps[0];
        }

        /**
         * Dynamic change based on reflection
         *
         * @param newVal String: Configuration key   Class: Type of member/method parameter binding, Object new configuration value
         * @throws Exception
         */
        public void update(BiFunction<String, Class, Object> newVal) throws Exception {
            if (isField()) {
                injectField(newVal);
            } else {
                injectMethod(newVal);
            }
        }

        private void injectField(BiFunction<String, Class, Object> newVal) throws Exception {
            Object bean = beanRef.get();
            if (bean == null) {
                return;
            }
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(bean, newVal.apply(key, field.getType()));
            field.setAccessible(accessible);
            log.info("Updated value: {}#{} = {}", beanName, field.getName(), field.get(bean));
        }

        private void injectMethod(BiFunction<String, Class, Object> newVal)
                throws Exception {
            Object bean = beanRef.get();
            if (bean == null) {
                return;
            }
            Object va = newVal.apply(key, methodParameter.getParameterType());
            methodParameter.getMethod().invoke(bean, va);
            log.info("Updated method: {}#{} = {}", beanName, methodParameter.getMethod().getName(), va);
        }

        public boolean isField() {
            return this.field != null;
        }
    }


    public static Map<String, Set<SpringValue>> registry = new ConcurrentHashMap<>();

    /**
     * Register the object bound to the configuration key in registry
     *
     * @param key
     * @param val
     */
    public static void register(String key, SpringValue val) {
        if (!registry.containsKey(key)) {
            synchronized (SpringValueRegistry.class) {
                if (!registry.containsKey(key)) {
                    registry.put(key, new HashSet<>());
                }
            }
        }

        Set<SpringValue> set = registry.getOrDefault(key, new HashSet<>());
        set.add(val);
    }

    /**
     * When the configuration corresponding to the key changes, find the properties bound to this configuration and refresh them through reflection
     *
     * @param key
     */
    public static void updateValue(String key) {
        Set<SpringValue> set = registry.getOrDefault(key, new HashSet<>());
        set.forEach(s -> {
            try {
                s.update((s1, aClass) -> SpringUtil.getBinder().bindOrCreate(s1, aClass));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
