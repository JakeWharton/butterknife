package butterknife.shadow;

import android.view.View;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowView;

/**
 * Behaves just like standard Robolectric views, but always reports that it is in Edit Mode.
 */
@Implements(View.class)
public class EditModeShadowView extends ShadowView {
  @SuppressWarnings("UnusedDeclaration")
  @Implementation
  public boolean isInEditMode() {
    return true;
  }
}
