package butterknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified font resource ID.
 * <pre><code>
 * {@literal @}BindFont(R.font.comic_sans) Typeface comicSans;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindFont {
  /** Font resource ID to which the field will be bound. */
  /* TODO support lib 26.0.0: @FontRes */ int value();
}
