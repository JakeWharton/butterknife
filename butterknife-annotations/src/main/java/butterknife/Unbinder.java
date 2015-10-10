package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a target field to an un-binder instance. Use when binding in a fragment to clear view
 * references in the {@code onDestroyView} callback.
 * <pre><code>
 * {@literal @}Unbinder ButterKnife.Unbinder unbinder;
 * </code></pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface Unbinder {
}
