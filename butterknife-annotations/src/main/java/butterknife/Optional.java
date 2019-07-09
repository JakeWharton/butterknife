package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Denote that the view specified by the injection is not required to be present.
 * <pre><code>
 * {@literal @}Optional @OnClick(R.id.subtitle) void onSubtitleClick() {}
 * </code></pre>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Optional {
}
