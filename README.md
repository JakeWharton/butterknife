Butter Knife
============

![Logo](website/static/logo.png)

View "injection" library for Android which uses annotation processing to generate boilerplate code
for you.

 * Eliminate `findViewById` calls by using `@InjectView` on fields.
 * Group multiple views in a list using `@InjectViews`. Operate on all of them at once with actions,
   setters, or properties.
 * Find resources using `@InjectString, @InjectDrawable & @InjectColor`
 * Eliminate anonymous inner-classes for listeners by annotating methods with `@OnClick` and others.

```java
class ExampleActivity extends Activity {
  @InjectView(R.id.user) EditText username;
  @InjectView(R.id.pass) EditText password;
  @InjectString(R.string.user_name) String name;
  @InjectDrawable(R.drawable.user_pic) Drawable userPic;
  @InjectColor(R.color.white) int whiteColor;

  @OnClick(R.id.submit) void submit() {
    // TODO call server...
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterKnife.inject(this);
    // TODO Use "injected" views...
  }
}
```

For documentation and additional information see [the website][3].

__Remember: A butter knife is like [a dagger][1] only infinitely less sharp.__



Download
--------

Download [the latest JAR][2] or grab via Maven:
```xml
<dependency>
  <groupId>com.jakewharton</groupId>
  <artifactId>butterknife</artifactId>
  <version>5.0.1</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.jakewharton:butterknife:5.0.1'
```


License
-------

    Copyright 2013 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://square.github.com/dagger/
 [2]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.jakewharton&a=butterknife&v=LATEST
 [3]: http://jakewharton.github.com/butterknife/
