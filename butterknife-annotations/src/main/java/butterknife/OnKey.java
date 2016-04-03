package butterknife;

import android.support.annotation.IdRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnKeyListener",
        type = "android.view.View.OnKeyListener",
        method = @ListenerMethod(
                name = "onKey",
                parameters = {
                        "android.view.View",
                        "int",
                        "android.view.KeyEvent"
                },
                returnType = "boolean",
                defaultReturn = "false"
        )
)
public @interface OnKey {
    /**
     * View IDs to which the method will be bound.
     */
    @IdRes int[] value() default {View.NO_ID};
}