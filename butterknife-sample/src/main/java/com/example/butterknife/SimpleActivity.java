package com.example.butterknife;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Views;

import static android.widget.Toast.LENGTH_SHORT;

public class SimpleActivity extends Activity {
  @InjectView(R.id.title) TextView title;
  @InjectView(R.id.subtitle) TextView subtitle;
  @InjectView(R.id.hello) Button hello;
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
    listOfThings.setAdapter(new SimpleAdapter(this));
  }

  @OnClick(R.id.hello)
  public void helloClicked(Button button) {
    Toast.makeText(SimpleActivity.this, "Hello, click!", LENGTH_SHORT).show();
    button.setText("Clicked!");
  }

  @OnClick({ R.id.title, R.id.subtitle, R.id.footer })
  public void textClicked(TextView view) {
    Toast.makeText(SimpleActivity.this, "Hello, views!", LENGTH_SHORT).show();
  }

  @OnLongClick(R.id.hello)
  public void helloLongClicked(View view) {
    Toast.makeText(SimpleActivity.this, "Hello, long click!", LENGTH_SHORT).show();
  }
}
