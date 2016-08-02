package butterknife.butterui;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;

/**
 * @author
 *      <br>Jul 31 2016 idosu
 */
class BindLayoutUtil {
    private BindLayoutUtil() {
    }

    @NonNull
    public static <T extends Annotation> T getAnnotation(
            @NonNull Class<?> clazz,
            @NonNull Class<T> annotation,
            @NonNull String errorMessage) {
        T annot = clazz.getAnnotation(annotation);
        if (annot == null) {
            throw new IllegalStateException(errorMessage);
        }
        return annot;
    }

    @LayoutRes
    public static int getBindLayout(@NonNull Class<?> clazz) {
        return getAnnotation(
            clazz,
            BindLayout.class,
            "Must set BindLayout to use this feature"
        ).value();
    }
}
