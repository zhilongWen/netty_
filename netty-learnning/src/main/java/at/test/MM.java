package at.test;

import io.netty.util.internal.ReflectionUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @create 2023-06-09
 */
public class MM {

    public static void main(String[] args) {

        // attempt to access field Unsafe#theUnsafe
        final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                    Throwable cause = ReflectionUtil.trySetAccessible(unsafeField);
                    if (cause != null) {
                        return cause;
                    }
                    // the unsafe instance
                    return unsafeField.get(null);
                } catch (NoSuchFieldException e) {
                    return e;
                } catch (SecurityException e) {
                    return e;
                } catch (IllegalAccessException e) {
                    return e;
                } catch (NoClassDefFoundError e) {
                    // Also catch NoClassDefFoundError in case someone uses for example OSGI and it made
                    // Unsafe unloadable.
                    return e;
                }
            }
        });


        System.out.println(maybeUnsafe.getClass());

        Unsafe unsafe = (Unsafe) maybeUnsafe;

        System.out.println(unsafe.getClass());

    }
}
