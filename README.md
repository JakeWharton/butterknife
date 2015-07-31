Butter Knife
============

![Logo](website/static/logo.png)

Field and method binding for Android views which uses annotation processing to generate boilerplate
code for you.

 * Eliminate `findViewById` calls by using `@Bind` on fields.
 * Group multiple views in a list or array. Operate on all of them at once with actions,
   setters, or properties.
 * Eliminate anonymous inner-classes for listeners by annotating methods with `@OnClick` and others.
 * Eliminate resource lookups by using resource annotations on fields.

```java
class ExampleActivity extends Activity {
  @Bind(R.id.user) EditText username;
  @Bind(R.id.pass) EditText password;

  @BindString(R.string.login_error)
  String loginErrorMessage;

  @OnClick(R.id.submit) void submit() {
    // TODO call server...
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterKnife.bind(this);
    // TODO Use fields...
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
  <version>7.0.1</version>
</dependency>
<dependency>
  <groupId>com.jakewharton</groupId>
  <artifactId>butterknife-compiler</artifactId>
  <version>7.0.1</version>
  <optional>true</optional>
</dependency>
```
or Gradle:
```groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    // ...
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.6'
  }
}

// ...

dependencies {
  compile 'com.jakewharton:butterknife:7.0.1'
  apt 'com.jakewharton:butterknife-compiler:7.0.1'
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].


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
 [2]: https://search.maven.org/remote_content?g=com.jakewharton&a=butterknife&v=LATEST
 [3]: http://jakewharton.github.com/butterknife/
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
