package com.example.butterknife.library;

import android.content.Context;
import android.view.View;
import androidx.test.InstrumentationRegistry;
import com.example.butterknife.R;
import org.junit.Test;

import static com.example.butterknife.library.SimpleAdapter.ViewHolder;
import static com.google.common.truth.Truth.assertThat;

public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    Context context = InstrumentationRegistry.getTargetContext();

    View root = View.inflate(context, R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word.getId()).isEqualTo(R.id.word);
    assertThat(holder.length.getId()).isEqualTo(R.id.length);
    assertThat(holder.position.getId()).isEqualTo(R.id.position);
  }
}
