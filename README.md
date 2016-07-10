Butter Knife
============

![Logo](website/static/logo.png)

Field and method binding for Android views which uses annotation processing to generate boilerplate
code for you.

 * Eliminate `findViewById` calls by using `@BindView` on fields.
 * Group multiple views in a list or array. Operate on all of them at once with actions,
   setters, or properties.
 * Eliminate anonymous inner-classes for listeners by annotating methods with `@OnClick` and others.
 * Eliminate resource lookups by using resource annotations on fields.

```java
class ExampleActivity extends Activity {
  @BindView(R.id.user) EditText username;
  @BindView(R.id.pass) EditText password;

  @BindString(R.string.login_error) String loginErrorMessage;

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

Add this to you project-level `build.gradle`:

```groovy
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}
```

Add this to your module-level `build.gradle`:

```groovy
apply plugin: 'android-apt'

android {
  ...
}

dependencies {
  compile 'com.jakewharton:butterknife:8.1.0'
  apt 'com.jakewharton:butterknife-compiler:8.1.0'
}
```

Make sure the line `apply plugin ...` is placed somewhere at the top of the file.

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].


Compiling with Jack and Build Tools 24
---------------------------------------
The new Jack compiler for Android requires new settings. The `android-apt`
module is no longer required.

Add this to you project-level `build.gradle`:

```groovy
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {

    // Requires Build Tools 2.2.0 at least
    classpath 'com.android.tools.build:gradle:2.2.0-alpha3'
  }
}
```

Add this to your module-level `build.gradle`:

```groovy

android {
    compileSdkVersion 24
    buildToolsVersion "24.0.0"

    defaultConfig {
      ...

      jackOptions {
          enabled true
      }
    }

    ...
}

dependencies {
    compile 'com.jakewharton:butterknife:8.1.0'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.1.0'
}
```

Also, make sure to have an updated `gradle-wrapper` version defined in `gradle/wrapper/gradle-wrapper.properties`:
```
distributionUrl=https\://services.gradle.org/distributions/gradle-2.14-all.zip
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
 [2]: https://search.maven.org/remote_content?g=com.jakewharton&a=butterknife&v=LATEST
 [3]: http://jakewharton.github.com/butterknife/
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
