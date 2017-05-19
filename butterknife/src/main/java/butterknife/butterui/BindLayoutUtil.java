package butterknife.butterui;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;

/**
 * Util class for handling {@link BindLayout} and reflection
 * @author
 *      <br>Jul 31 2016 idosu
 */
class BindLayoutUtil {
    private BindLayoutUtil() {
    }

    /**
     * Get the annotation {@code annotation} from the class {@code clazz}, if the annotation is not
     * present the code will throw IllegalStateException with messege {@code errorMessage}
     * @param clazz the class to get the annotation from
     * @param annotation the type of annotation to search
     * @param errorMessage the error message to throw when the annotation is not present
     * @param <T> the type of the annotation
     * @return the annotation
     * @throws IllegalStateException when the annotation is not present
     */
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

    /**
     * Gets the value of the annotation {@link BindLayout} from the class (@code clazz},
     * see also {@link #getAnnotation(Class, Class, String)}
     * @param clazz the class to get the annoation from
     * @return the annotation value
     */
    @LayoutRes
    public static int getBindLayout(@NonNull Class<?> clazz) {
        return getAnnotation(
            clazz,
            BindLayout.class,
            "Must set BindLayout to use this feature"
        ).value();
    }
}