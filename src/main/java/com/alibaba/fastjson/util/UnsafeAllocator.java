//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.fastjson.util;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {
    public UnsafeAllocator() {
    }

    public abstract <T> T newInstance(Class<T> var1) throws Exception;

    public static UnsafeAllocator create() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Object unsafe = f.get((Object)null);
            final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            return new UnsafeAllocator() {
                public <T> T newInstance(Class<T> c) throws Exception {
                    UnsafeAllocator.assertInstantiable(c);
                    return (T) allocateInstance.invoke(unsafe, c);
                }
            };
        } catch (Exception var6) {
            Method newInstance;
            try {
                newInstance = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
                newInstance.setAccessible(true);
                final int constructorId = (Integer)newInstance.invoke((Object)null, Object.class);
                newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
                newInstance.setAccessible(true);
                final Method finalNewInstance = newInstance;
                return new UnsafeAllocator() {
                    public <T> T newInstance(Class<T> c) throws Exception {
                        UnsafeAllocator.assertInstantiable(c);
                        return (T) finalNewInstance.invoke((Object)null, c, constructorId);
                    }
                };
            } catch (Exception var5) {
                try {
                    newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
                    newInstance.setAccessible(true);
                    final Method finalNewInstance1 = newInstance;
                    return new UnsafeAllocator() {
                        public <T> T newInstance(Class<T> c) throws Exception {
                            UnsafeAllocator.assertInstantiable(c);
                            return (T) finalNewInstance1.invoke((Object)null, c, Object.class);
                        }
                    };
                } catch (Exception var4) {
                    return new UnsafeAllocator() {
                        public <T> T newInstance(Class<T> c) {
                            throw new UnsupportedOperationException("Cannot allocate " + c);
                        }
                    };
                }
            }
        }
    }

    private static void assertInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.getName());
        } else if (Modifier.isAbstract(modifiers)) {
            throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.getName());
        }
    }
}
