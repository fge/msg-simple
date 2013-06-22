## Read me first

The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.

**NOTE**: from version 0.6 on, this project uses [Gradle](http://gradle.org) as a build system.

## What this is

This is a lightweight, extensible message bundle API which you can use as a replacement to Java's
`ResourceBundle`. Since 0.5, it is also able to load legacy `ResourceBundle`s as well. Its only
dependency is [btf](https://github.com/fge/btf).

Among features that this library offers which `ResourceBundle` doesn't are:

* UTF-8 support,
* `printf()`-like format for messages,
* builtin assertions.

See below for more.

## Versions

The current version is **0.6**. Javadoc [here](http://fge.github.io/msg-simple/index.html).

See [here](https://github.com/fge/msg-simple/wiki/Examples) for sample API usage.

## Downloads and Maven artifact

You can download the jar directly on [Bintray](https://bintray.com/fge/maven/msg-simple) (note: jar
with full dependencies is provided). If you use Maven, use the following dependency:

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>msg-simple</artifactId>
    <version>your-version-here</version>
</dependency>
```

## Features and roadmap

This library currently has the following features:

* automatic message bundle loading via [ServiceLoader](http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html);
* property files read using UTF-8, ISO-8859-1 or any other encoding of your choice;
* `printf()`-like message support, `MessageFormat` support;
* i18n/locale support;
* stackable message sources;
* bundles are reusable (using the [freeze/thaw pattern](https://github.com/fge/btf/wiki/The-freeze-thaw-pattern));
* builtin preconditions in bundles (`checkNotNull()`, `checkArgument()`, plus their `printf()`/`MessageFormat` equivalents).

The roadmap for future versions can be found [here](https://github.com/fge/msg-simple/wiki/Roadmap). Feature requests are of course
welcome!

