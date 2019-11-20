package com.example.butterknife.library;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Action;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnLongClick;
import butterknife.ViewCollections;
import com.example.butterknife.R;
import static com.example.butterknife.R.id.titleTv;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class SimpleActivity extends Activity {
  private static final Action<View> ALPHA_FADE = (view, index) -> {
    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
    alphaAnimation.setFillBefore(true);
    alphaAnimation.setDuration(500);
    alphaAnimation.setStartOffset(index * 100);
    view.startAnimation(alphaAnimation);
  };

  @BindView(titleTv) TextView title;
  @BindView(R.id.subtitle) TextView subtitle;
  @BindView(R.id.hello) Button hello;
  @BindView(R.id.list_of_things) ListView listOfThings;
  @BindView(R.id.footer) TextView footer;
  @BindString(R.string.app_name) String butterKnife;
  @BindString(R.string.field_method) String fieldMethod;
  @BindString(R.string.by_jake_wharton) String byJakeWharton;
  @BindString(R.string.say_hello) String sayHello;

  @BindViews({ titleTv, R.id.subtitle, R.id.hello }) List<View> headerViews;

  private SimpleAdapter adapter;

  @OnClick(R.id.hello) void sayHello() {
    Toast.makeText(this, "Hello, views!", LENGTH_SHORT).show();
    ViewCollections.run(headerViews, ALPHA_FADE);
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
    ButterKnife.bind(this);

    // Contrived code to use the bound fields.
    title.setText(butterKnife);
    subtitle.setText(fieldMethod);
    footer.setText(byJakeWharton);
    hello.setText(sayHello);

    adapter = new SimpleAdapter(this);
    listOfThings.setAdapter(adapter);
  }
}
