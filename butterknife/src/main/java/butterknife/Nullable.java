package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denote that the view specified by the injection is not required to be present.
 * <pre><code>
 * {@literal @}Nullable @InjectView(R.id.title) TextView subtitleView;
 * </code></pre>
 * This annotation is deprecated, and will be removed in a future release. It is encouraged to use
 * the {@code @Nullable} annotation from Android's "support-annotations" library.
 * @see <a href="http://tools.android.com/tech-docs/support-annotations">Android Tools Project</a>
 */
@Deprecated
@Retention(CLASS) @Target({ FIELD, METHOD })
public @interface Nullable {
}