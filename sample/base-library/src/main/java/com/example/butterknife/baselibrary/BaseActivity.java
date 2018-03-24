package com.example.butterknife.baselibrary;

import android.app.Activity;
import butterknife.BindString;

public class BaseActivity extends Activity {
    @BindString(R2.string.app_name) protected String butterKnife;
    @BindString(R2.string.field_method) protected String fieldMethod;
    @BindString(R2.string.by_jake_wharton) protected String byJakeWharton;
    @BindString(R2.string.say_hello) protected String sayHello;
}
