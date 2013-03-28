package com.example.butterknife;

import android.view.LayoutInflater;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.example.butterknife.SimpleAdapter.ViewHolder;
import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    SimpleActivity activity = new SimpleActivity();

    View root = LayoutInflater.from(activity).inflate(R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word).hasId(R.id.word);
    assertThat(holder.length).hasId(R.id.length);
    assertThat(holder.position).hasId(R.id.position);
  }
}
