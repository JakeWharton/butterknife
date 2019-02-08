package com.example.butterknife.functional;

import android.view.View;
import butterknife.BindViews;
import butterknife.ButterKnife;
import java.util.Deque;
import java.util.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class BindViewsFailureTest {
  private final View tree = ViewTree.create(1);

  static class NoIds {
    @BindViews({}) View[] actual;
  }

  @Test public void failsIfNoIds() {
    NoIds target = new NoIds();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindViews must specify at least one ID. "
              + "(com.example.butterknife.functional.BindViewsFailureTest$NoIds.actual)");
    }
  }

  static class NoGenericType {
    @BindViews(1) List actual;
  }

  @Test public void failsIfNoGenericType() {
    NoGenericType target = new NoGenericType();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindViews List must have a generic component. "
              + "(com.example.butterknife.functional.BindViewsFailureTest$NoGenericType.actual)");
    }
  }

  static class BadCollection {
    @BindViews(1) Deque<View> actual;
  }

  @Test public void failsIfUnsupportedCollection() {
    BadCollection target = new BadCollection();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindViews must be a List or array. "
              + "(com.example.butterknife.functional.BindViewsFailureTest$BadCollection.actual)");
    }
  }

  static class ListNotView {
    @BindViews(1) List<String> actual;
  }

  @Test public void failsIfGenericNotView() {
    ListNotView target = new ListNotView();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindViews List or array type must extend from View or be an interface. "
              + "(com.example.butterknife.functional.BindViewsFailureTest$ListNotView.actual)");
    }
  }

  static class ArrayNotView {
    @BindViews(1) List<String> actual;
  }

  @Test public void failsIfArrayNotView() {
    ArrayNotView target = new ArrayNotView();

    try {
      ButterKnife.bind(target, tree);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("@BindViews List or array type must extend from View or be an interface. "
              + "(com.example.butterknife.functional.BindViewsFailureTest$ArrayNotView.actual)");
    }
  }
}
