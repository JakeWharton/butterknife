package com.example.butterknife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.Views;

import static android.view.View.OnClickListener;

public class SimpleActivity extends Activity {
  @InjectView(R.id.title) TextView title;
  @InjectView(R.id.subtitle) TextView subtitle;
  @InjectView(R.id.hello) Button hello;
  @InjectView(R.id.chkBeRespectful) CheckBox chkBeRespectful;
  @InjectView(R.id.list_of_things) ListView listOfThings;
  @InjectView(R.id.footer) TextView footer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    Views.inject(this);

    // Contrived code to use the "injected" views.
    title.setText("Butter Knife");
    subtitle.setText("View \"injection\" for Android.");
    footer.setText("by Jake Wharton");
    hello.setText("Say Hello");
    chkBeRespectful.setText("respectfully?");
    hello.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(SimpleActivity.this, HelloActivity.class);
        intent.putExtra(HelloActivity.EXTRA_ONE, "Hello");
        intent.putExtra(HelloActivity.EXTRA_TWO, chkBeRespectful.isChecked());
        startActivity(intent);
      }
    });
    listOfThings.setAdapter(new SimpleAdapter(this));
  }
}
