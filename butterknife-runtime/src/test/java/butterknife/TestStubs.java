package butterknife;

import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;

final class TestStubs {
  /**
   * The presence of this type (and {@code androidx.annotation.NonNull}, which should be on the
   * classpath already) trigger AndroidX emission.
   */
  static final JavaFileObject ANDROIDX_CONTEXT_COMPAT =
      JavaFileObjects.forSourceString("androidx.core.content.ContextCompat", ""
          + "package androidx.core.content;\n"
          + "public class ContextCompat {}");

  static final JavaFileObject ANDROIDX_VIEW_PAGER =
      JavaFileObjects.forSourceString("androidx.viewpager.widget.ViewPager", ""
          + "package androidx.viewpager.widget;\n"
          + "\n"
          + "public interface ViewPager {\n"
          + "  void addOnPageChangeListener(OnPageChangeListener listener);\n"
          + "  void removeOnPageChangeListener(OnPageChangeListener listener);\n"
          + "  interface OnPageChangeListener {\n"
          + "    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);\n"
          + "    void onPageSelected(int position);\n"
          + "    void onPageScrollStateChanged(int state);\n"
          + "  }\n"
          + "}\n");

  static final JavaFileObject SUPPORT_VIEW_PAGER =
      JavaFileObjects.forSourceString("androidx.viewpager.widget.ViewPager", ""
          + "package android.support.v4.view;\n"
          + "\n"
          + "public interface ViewPager {\n"
          + "  void addOnPageChangeListener(OnPageChangeListener listener);\n"
          + "  void removeOnPageChangeListener(OnPageChangeListener listener);\n"
          + "  interface OnPageChangeListener {\n"
          + "    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);\n"
          + "    void onPageSelected(int position);\n"
          + "    void onPageScrollStateChanged(int state);\n"
          + "  }\n"
          + "}\n");
}
