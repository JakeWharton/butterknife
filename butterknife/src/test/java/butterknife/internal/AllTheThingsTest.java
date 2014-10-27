package butterknife.internal;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnItemSelected;
import butterknife.OnLongClick;
import butterknife.OnPageChange;
import butterknife.OnTextChanged;
import butterknife.Optional;
import com.google.common.io.Files;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

@SuppressWarnings("UnusedDeclaration")
public class AllTheThingsTest {
  @Test public void allTheThings() throws IOException {
    File file = new File("src/test/java/butterknife/internal/AllTheThingsTest.java");
    String content = Files.toString(file, StandardCharsets.UTF_8);

    ASSERT.about(javaSource())
        .that(JavaFileObjects.forSourceString("butterknife.internal.AllTheThingsTest", content))
        .processedWith(butterknifeProcessors())
        .compilesWithoutError();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @InjectView(10) View injectView;
  @Optional @InjectView(20) View optinalInjectView;

  @InjectView(30) TextView injectTextView;
  @Optional @InjectView(40) TextView optinalInjectTextView;

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @InjectViews(50) List<View> injectViewsListOne;
  @InjectViews({ 60, 70 }) List<View> injectViewsListMultiple;

  @InjectViews(80) List<TextView> injectTextViewsListOne;
  @InjectViews({ 90, 100 }) List<TextView> injectTextViewsListMultiple;

  @InjectViews(110) View[] injectViewsArrayOne;
  @InjectViews({ 120, 130 }) View[] injectViewsArrayMultiple;

  @InjectViews(140) TextView[] injectTextViewsArrayOne;
  @InjectViews({ 150, 160 }) TextView[] injectTextViewsArrayMultiple;

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnCheckedChanged(170)
  void checkedChanged() {}
  @OnCheckedChanged(180)
  void checkedChanged(CompoundButton one) {}
  @OnCheckedChanged(190)
  void checkedChanged(boolean two) {}
  @OnCheckedChanged(200)
  void checkedChanged(CompoundButton one, boolean two) {}

  @OnCheckedChanged({ 210, 220 })
  void checkedChangedMultiple() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnClick(240)
  void click() {}
  @OnClick(250)
  void click(View one) {}

  @OnClick({ 260, 270 })
  void clickMultiple() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnEditorAction(290)
  boolean editorAction() { return false; }
  @OnEditorAction(300)
  boolean editorAction(TextView one) { return false; }
  @OnEditorAction(310)
  boolean editorAction(int one) { return false; }
  @OnEditorAction(320)
  boolean editorAction(KeyEvent one) { return false; }
  @OnEditorAction(330)
  boolean editorAction(TextView one, int two) { return false; }
  @OnEditorAction(340)
  boolean editorAction(TextView one, KeyEvent two) { return false; }
  @OnEditorAction(350)
  boolean editorAction(int one, KeyEvent two) { return false; }
  @OnEditorAction(360)
  boolean editorAction(TextView one, int two, KeyEvent three) { return false; }

  @OnEditorAction({ 370, 380 })
  boolean editorActionMultiple() { return false; }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnFocusChange(400)
  void focusChange() {}
  @OnFocusChange(410)
  void focusChange(View one) {}
  @OnFocusChange(420)
  void focusChange(boolean one) {}
  @OnFocusChange(430)
  void focusChange(View one, boolean two) {}

  @OnFocusChange({ 440, 450 })
  void focusChangeMultiple() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnItemClick(470)
  void itemClick() {}
  @OnItemClick(480)
  void itemClick(AdapterView<?> one) {}
  @OnItemClick(490)
  void itemClick(View one) {}
  @OnItemClick(500)
  void itemClick(int one) {}
  @OnItemClick(510)
  void itemClick(long one) {}
  @OnItemClick(520)
  void itemClick(AdapterView<?> one, View two) {}
  @OnItemClick(530)
  void itemClick(AdapterView<?> one, int two) {}
  @OnItemClick(540)
  void itemClick(AdapterView<?> one, long two) {}
  @OnItemClick(550)
  void itemClick(View one, long two) {}
  @OnItemClick(560)
  void itemClick(int one, long two) {}
  @OnItemClick(570)
  void itemClick(AdapterView<?> one, View two, int three) {}
  @OnItemClick(580)
  void itemClick(AdapterView<?> one, View two, long three) {}
  @OnItemClick(590)
  void itemClick(AdapterView<?> one, int two, long three) {}
  @OnItemClick(600)
  void itemClick(View one, int two, long three) {}
  @OnItemClick(610)
  void itemClick(AdapterView<?> one, View two, int three, long four) {}

  @OnItemClick({ 620, 630 })
  void itemClickMultiple() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnItemLongClick(650)
  boolean itemLongClick() { return false; }
  @OnItemLongClick(660)
  boolean itemLongClick(AdapterView<?> one) { return false; }
  @OnItemLongClick(670)
  boolean itemLongClick(View one) { return false; }
  @OnItemLongClick(680)
  boolean itemLongClick(int one) { return false; }
  @OnItemLongClick(690)
  boolean itemLongClick(long one) { return false; }
  @OnItemLongClick(700)
  boolean itemLongClick(AdapterView<?> one, View two) { return false; }
  @OnItemLongClick(710)
  boolean itemLongClick(AdapterView<?> one, int two) { return false; }
  @OnItemLongClick(720)
  boolean itemLongClick(AdapterView<?> one, long two) { return false; }
  @OnItemLongClick(730)
  boolean itemLongClick(View one, long two) { return false; }
  @OnItemLongClick(740)
  boolean itemLongClick(int one, long two) { return false; }
  @OnItemLongClick(750)
  boolean itemLongClick(AdapterView<?> one, View two, int three) { return false; }
  @OnItemLongClick(760)
  boolean itemLongClick(AdapterView<?> one, View two, long three) { return false; }
  @OnItemLongClick(770)
  boolean itemLongClick(AdapterView<?> one, int two, long three) { return false; }
  @OnItemLongClick(780)
  boolean itemLongClick(View one, int two, long three) { return false; }
  @OnItemLongClick(790)
  boolean itemLongClick(AdapterView<?> one, View two, int three, long four) { return false; }

  @OnItemLongClick({ 800, 810 })
  boolean itemLongClickMultiple() { return false; }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnItemSelected(830)
  void itemSelected() {}
  @OnItemSelected(840)
  void itemSelected(AdapterView<?> one) {}
  @OnItemSelected(850)
  void itemSelected(View one) {}
  @OnItemSelected(860)
  void itemSelected(int one) {}
  @OnItemSelected(870)
  void itemSelected(long one) {}
  @OnItemSelected(880)
  void itemSelected(AdapterView<?> one, View two) {}
  @OnItemSelected(890)
  void itemSelected(AdapterView<?> one, int two) {}
  @OnItemSelected(900)
  void itemSelected(AdapterView<?> one, long two) {}
  @OnItemSelected(910)
  void itemSelected(View one, long two) {}
  @OnItemSelected(920)
  void itemSelected(int one, long two) {}
  @OnItemSelected(930)
  void itemSelected(AdapterView<?> one, View two, int three) {}
  @OnItemSelected(940)
  void itemSelected(AdapterView<?> one, View two, long three) {}
  @OnItemSelected(950)
  void itemSelected(AdapterView<?> one, int two, long three) {}
  @OnItemSelected(960)
  void itemSelected(View one, int two, long three) {}
  @OnItemSelected(970)
  void itemSelected(AdapterView<?> one, View two, int three, long four) {}

  @OnItemSelected({ 980, 990 })
  void itemSelectedMultiple() {}
  @OnItemSelected(value = { 1000, 1010 }, callback = OnItemSelected.Callback.NOTHING_SELECTED)
  void nothingSelectedMultiple() {}

  @OnItemSelected(value = 1020, callback = OnItemSelected.Callback.ITEM_SELECTED)
  void itemSelectedQualifiedDefault() {}

  @OnItemSelected(value = 1030, callback = OnItemSelected.Callback.NOTHING_SELECTED)
  void nothingSelected() {}
  @OnItemSelected(value = 1040, callback = OnItemSelected.Callback.NOTHING_SELECTED)
  void nothingSelected(AdapterView<?> one) {}

  @OnItemSelected(1050)
  void itemSelectedBothMethods() {}
  @OnItemSelected(value = 1050, callback = OnItemSelected.Callback.NOTHING_SELECTED)
  void nothingSelectedBothMethods() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnLongClick(1060)
  boolean longClick() { return false; }
  @OnLongClick(1070)
  boolean longClick(View one) { return false; }

  @OnLongClick({ 1080, 1090 })
  boolean longClickMultiple() { return false; }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnPageChange(1110)
  void pageChange() {}
  @OnPageChange(1120)
  void pageChange(int one) {}

  @OnPageChange({ 1130, 1140 })
  void pageChangeMultiple() {}

  @OnPageChange(value = 1150, callback = OnPageChange.Callback.PAGE_SELECTED)
  void pageChangeQualifiedDefault() {}

  @OnPageChange(value = 1160, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled() {}
  @OnPageChange(value = 1170, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled(int one) {}
  @OnPageChange(value = 1180, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled(float two) {}
  // A single-argument method matching the third parameter can't happen.
  //@OnPageChange(value = 1190, callback = OnPageChange.Callback.PAGE_SCROLLED)
  //void pageScrolled(int three) {}
  @OnPageChange(value = 1200, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled(int one, float two) {}
  @OnPageChange(value = 1210, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled(float one, int two) {}
  @OnPageChange(value = 1220, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolled(int one, float two, int three) {}

  @OnPageChange(value = { 1230, 1240 }, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolledMultiple() {}

  @OnPageChange(value = 1250, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChanged() {}
  @OnPageChange(value = 1260, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChanged(int one) {}

  @OnPageChange(value = { 1270, 1280 }, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChangedMultiple() {}

  @OnPageChange(1290)
  void pageChangeFirstTwoMethods() {}
  @OnPageChange(value = 1290, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolledFirstTwoMethods() {}

  @OnPageChange(value = 1300, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolledSecondTwoMethods() {}
  @OnPageChange(value = 1300, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChangedSecondTwoMethods() {}

  @OnPageChange(1310)
  void pageChangeOuterTwoMethods() {}
  @OnPageChange(value = 1310, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChangedOuterTwoMethods() {}

  @OnPageChange(1320)
  void pageChangeAllMethods() {}
  @OnPageChange(value = 1320, callback = OnPageChange.Callback.PAGE_SCROLLED)
  void pageScrolledAllMethods() {}
  @OnPageChange(value = 1320, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
  void pageScrollStateChangedAllMethods() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////

  @OnTextChanged(1330)
  void textChanged() {}
  @OnTextChanged(1340)
  void textChanged(CharSequence one) {}
  @OnTextChanged(1350)
  void textChanged(int one) {}
  @OnTextChanged(1360)
  void textChanged(CharSequence one, int two) {}
  @OnTextChanged(1370)
  void textChanged(int one, int two) {}
  @OnTextChanged(1380)
  void textChanged(CharSequence one, int two, int three) {}
  @OnTextChanged(1390)
  void textChanged(int one, int two, int three) {}
  @OnTextChanged(1400)
  void textChanged(CharSequence one, int two, int three, int four) {}

  @OnTextChanged({ 1410, 1420 })
  void textChangedMultiple() {}

  @OnTextChanged(value = 1430, callback = OnTextChanged.Callback.TEXT_CHANGED)
  void textChangedQualifiedDefault() {}

  @OnTextChanged(value = 1440, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged() {}
  @OnTextChanged(value = 1450, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(CharSequence one) {}
  @OnTextChanged(value = 1460, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(int one) {}
  @OnTextChanged(value = 1470, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(CharSequence one, int two) {}
  @OnTextChanged(value = 1480, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(int one, int two) {}
  @OnTextChanged(value = 1490, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(CharSequence one, int two, int three) {}
  @OnTextChanged(value = 1500, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(int one, int two, int three) {}
  @OnTextChanged(value = 1510, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChanged(CharSequence one, int two, int three, int four) {}

  @OnTextChanged(value = { 1520, 1530 }, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChangedMultiple() {}

  @OnTextChanged(value = 1540, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChanged() {}
  @OnTextChanged(value = 1550, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChanged(Editable one) {}

  @OnTextChanged(value = { 1560, 1570}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChangedMultiple() {}

  @OnTextChanged(1580)
  void textChangedFirstTwoMethods() {}
  @OnTextChanged(value = 1580, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChangedFirstTwoMethods() {}

  @OnTextChanged(value = 1590, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChangedSecondTwoMethods() {}
  @OnTextChanged(value = 1590, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChangedSecondTwoMethods() {}

  @OnTextChanged(1600)
  void textChangedOuterTwoMethods() {}
  @OnTextChanged(value = 1600, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChangedOuterTwoMethods() {}

  @OnTextChanged(1610)
  void textChangedAllMethods() {}
  @OnTextChanged(value = 1610, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
  void beforeTextChangedAllMethods() {}
  @OnTextChanged(value = 1610, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
  void afterTextChangedAllMethods() {}

  /////////////////////////////////////////////////////////////////////////////////////////////////
}
