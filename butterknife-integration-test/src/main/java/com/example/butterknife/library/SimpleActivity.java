package com.example.butterknife.library;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindBeanClass;
import butterknife.BindBean;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;

import com.example.butterknife.R;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
@BindBeanClass(SimpleActivity.Bean.class)
public class SimpleActivity extends Activity {


  private static final ButterKnife.Action<View> ALPHA_FADE = new ButterKnife.Action<View>() {
    @Override public void apply(@NonNull View view, int index) {
      AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
      alphaAnimation.setFillBefore(true);
      alphaAnimation.setDuration(500);
      alphaAnimation.setStartOffset(index * 100);
      view.startAnimation(alphaAnimation);
    }
  };

  @BindView(R.id.title) TextView title;
  @BindView(R.id.subtitle) TextView subtitle;

  @BindView(R.id.list_of_things) ListView listOfThings;
  @BindString(R.string.app_name) String butterKnife;
  @BindString(R.string.field_method) String fieldMethod;
  @BindString(R.string.by_jake_wharton) String byJakeWharton;

  @BindBeanClass(Bean.class)
  public static class TestBean extends Fragment{
    @BindBean(id=R.id.footer,value = "id()")
    ImageView image;
    void x(){

    }
  }
  public static class Bean{
    public String id="";
    public Uri id(){return Uri.parse("http://a.png");};
  }
  @BindBean(id=R.id.footer,value = "id")
  TextView ggg;
  @BindBean(id=R.id.hello,value = "id()")
  TextView hello;
  @BindViews({ R.id.title, R.id.subtitle, R.id.hello }) List<View> headerViews;

  private SimpleAdapter adapter;

  @OnClick(R.id.hello) void sayHello() {
    Toast.makeText(this, "Hello, views!", LENGTH_SHORT).show();
    ButterKnife.apply(headerViews, ALPHA_FADE);
  }

  @OnLongClick(R.id.hello) boolean sayGetOffMe() {
    Toast.makeText(this, "Let go of me!", LENGTH_SHORT).show();
    return true;
  }

  @OnItemClick(R.id.list_of_things) void onItemClick(int position) {
    Toast.makeText(this, "You clicked: " + adapter.getItem(position), LENGTH_SHORT).show();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    Unbinder binder = ButterKnife.bind(this);
    Bean bean=new Bean();
    binder.apply(bean);
    // Contrived code to use the bound fields.
    title.setText(butterKnife);
    subtitle.setText(fieldMethod);

    adapter = new SimpleAdapter(this);
    listOfThings.setAdapter(adapter);
  }
}
