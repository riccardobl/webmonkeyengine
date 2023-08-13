package com.jme3.web.jvm.patches;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Function;


public class ClassPatch {
    private void walkThroughClass(Class<?> clazz, Function<Class<?>, Boolean> f) {
        if (clazz == null) {
            return;
        }
        if (!f.apply(clazz)) {
            return;
        }
        walkThroughClass(clazz.getSuperclass(), f);
        for (Class<?> c : clazz.getInterfaces()) {
            walkThroughClass(c, f);
        }
    }

    public Field getField(String field) throws NoSuchFieldException, SecurityException {
        Field out[] = new Field[1];
        Class clazz = (Class) (Object) this;
        walkThroughClass(clazz, (cls) -> {
            try {

                if (!Arrays.asList(cls.getDeclaredFields()).stream().filter(f -> f.getName().equals(field)).findAny().isPresent()) return true;
                Field f = cls.getDeclaredField(field);
                if (f != null) {
                    out[0]=f;
                    return false;
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        });
        return out[0];
    }

    public Method getMethod(String method) throws NoSuchMethodException, SecurityException {
        Method out[] = new Method[0];
        Class clazz = (Class) (Object) this;
        walkThroughClass(clazz, (cls) -> {
            try {
                if (!Arrays.asList(cls.getDeclaredMethods()).stream().filter(f -> f.getName().equals(method)).findAny().isPresent()) return true;
                Method f = cls.getDeclaredMethod(method);
                if (f != null) {
                    out[0] = f;
                    return false;
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        });
        return out[0];
    }

    public Method getMethod(String method, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        Method out[] = new Method[0];
        Class clazz = (Class) (Object) this;
        walkThroughClass(clazz, (cls) -> {
            try {
                if (!Arrays.asList(cls.getDeclaredMethods()).stream().filter(f -> f.getName().equals(method)).findAny().isPresent()) return true;
                Method f = cls.getDeclaredMethod(method, parameterTypes);
                if (f != null) {
                    out[0] = f;
                    return false;
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        });

        return out[0];
    }


    
    public URL getResource(String name) {
        throw new UnsupportedOperationException("Not supported. Use Resources.getResource instead"); 
    }
}
