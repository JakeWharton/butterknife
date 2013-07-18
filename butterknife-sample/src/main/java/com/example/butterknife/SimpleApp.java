package com.example.butterknife;

import android.app.Application;
import butterknife.Views;

public class SimpleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    Views.setDebug(BuildConfig.DEBUG);
  }
}
