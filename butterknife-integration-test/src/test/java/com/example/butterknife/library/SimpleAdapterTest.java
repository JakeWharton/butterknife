package com.example.butterknife.library;

import android.content.Context;
import android.view.View;
import com.example.butterknife.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.example.butterknife.library.SimpleAdapter.ViewHolder;
import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    Context context = RuntimeEnvironment.application;

    View root = View.inflate(context, R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word.getId()).isEqualTo(R.id.word);
    assertThat(holder.length.getId()).isEqualTo(R.id.length);
    assertThat(holder.position.getId()).isEqualTo(R.id.position);
  }
}
