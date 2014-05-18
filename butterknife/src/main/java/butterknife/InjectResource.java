package butterknife;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS) @Target(FIELD)
public @interface InjectResource {
	/** Resource ID to which the field will be bound. */
	int value();
}
