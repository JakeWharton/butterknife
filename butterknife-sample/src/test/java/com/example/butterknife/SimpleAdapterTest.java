package com.example.butterknife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.example.butterknife.SimpleAdapter.ViewHolder;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    Context context = RuntimeEnvironment.application;

    View root = LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word.getId()).isEqualTo(R.id.word);
    assertThat(holder.length.getId()).isEqualTo(R.id.length);
    assertThat(holder.position.getId()).isEqualTo(R.id.position);
  }
}
