package com.example.butterknife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.example.butterknife.SimpleAdapter.ViewHolder;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = "src/main/AndroidManifest.xml")
public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    Context context = Robolectric.application;

    View root = LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word.getId()).isEqualTo(R.id.word);
    assertThat(holder.length.getId()).isEqualTo(R.id.length);
    assertThat(holder.position.getId()).isEqualTo(R.id.position);
  }
}
