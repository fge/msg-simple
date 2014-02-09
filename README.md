## Read me first

This project, as of version 1.0, is licensed under both LGPLv3 and ASL 2.0. See
file LICENSE for more details. Versions 1.0 and lower are licensed under LGPLv3
only.

**Note the "L" in "LGPL". LGPL AND GPL ARE QUITE DIFFERENT!**

**NOTE**: this project uses [Gradle](http://gradle.org) as a build system. See the `BUILD.md` file
for more details.

## What this is

This is a lightweight, extensible message bundle API which you can use as a replacement to Java's
`ResourceBundle`. It is able to load legacy `ResourceBundle`s. Its only dependency is
[btf](https://github.com/fge/btf).

Among features that this library offers which `ResourceBundle` doesn't are:

* UTF-8 support,
* `printf()`-like format for messages (in addition to the antique `MessageFormat`),
* builtin assertions,
* error resistant.

See below for more.

## Versions

The current version is **1.0**. Javadoc [here](http://fge.github.io/msg-simple/index.html).

See [here](https://github.com/fge/msg-simple/wiki/Examples) for sample API usage.

## Downloads and Maven artifact

You can download the jar directly on [Bintray](https://bintray.com/fge/maven/msg-simple).

For Gradle:

```gradle
dependencies {
    compile(group: "com.github.fge", name: "msg-simple", version: "yourVersionHere");
}
```

For Maven:

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>msg-simple</artifactId>
    <version>your-version-here</version>
</dependency>
```

## Features and roadmap

This library currently has the following features:

* on-demand, cached message bundle loading;
* property files read using UTF-8, ISO-8859-1 or any other encoding of your choice;
* `printf()`-like message support, `MessageFormat` support;
* i18n/locale support;
* stackable message sources;
* bundles are reusable (using the [freeze/thaw pattern](https://github.com/fge/btf/wiki/The-freeze-thaw-pattern));
* builtin preconditions in bundles (`checkNotNull()`, `checkArgument()`, plus their `printf()`/`MessageFormat` equivalents);
* static, or loading-on-demand, message sources.

The roadmap for future versions can be found [here](https://github.com/fge/msg-simple/wiki/Roadmap). Feature requests are of course
welcome!

