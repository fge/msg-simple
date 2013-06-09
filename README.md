## Read me first

The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.

## What this is

This is a lightweight, extensible message bundle API which you can use as a replacement to Java's
`ResourceBundle`. It has no external dependencies other than the JRE (1.6 or better).

Among features that this library offers which `ResourceBundle` doesn't are:

* UTF-8 support,
* `printf()`-like format for messages,
* builtin assertions.

See below for more.

## Versions

The current version is **0.3**. Javadoc [here](http://fge.github.io/msg-simple/index.html).

## Downloads and Maven artifact

You can download the jar directly on [Bintray](https://bintray.com/fge/maven/msg-simple). If you use Maven, use the following
dependency:

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
* property files read using UTF-8;
* `printf()`-like message support;
* i18n/locale support;
* stackable message sources;
* bundles are reusable (using the [freeze/thaw pattern](https://github.com/fge/btf/wiki/The-freeze-thaw-pattern));
* builtin preconditions in bundles (`checkNotNull()`, `checkArgument()`);
* no external library dependencies.

The roadmap for future versions can be found [here](https://github.com/fge/msg-simple/wiki/Roadmap). Feature requests are of course
welcome!

## Sample usage

In order to build a message bundle, you need two things:

* a set of `MessageSource`s;
* a set of `MessageSourceProvider`s;

then you can build a bundle out of these elements.

Note that the examples below use shortcut methods to build a bundle only from sources. More
complete examples will be added later on.

### Message sources

This library provides two `MessageSource` implementations: one `Map`-based implementation, and
another using Java property files. You will note that property files are read in UTF-8.

Some examples:

```java
final Map<String, String> map = new HashMap<String, String>();

// Fill the map, and then:
final MessageSource mapSource = new MapMessageSource(map);

MessageSource propertySource;

// Read from a resource in the classpath
propertySource = PropertiesMessageSource.fromResource("/messages.properties");
// Read from a file on the filesystem
propertySource = PropertiesMessageSource.fromPath("/path/to/messages.properties");
// Others
```

### Build the message bundle

Once you are done building your set of sources, you can build a `MessageBundle`. For this, you
use its builder class, and append or prepend message sources as you see fit:

```java
MessageBundleBuilder builder = MessageBundle.newBuilder();

// Append two sources
builder = builder.appendSource(source1).appendSource(source2);
// Prepend another one
builder = builder.prependSource(source3);

// Finally, build the bundle
final MessageBundle bundle = builder.freeze();
```

### Reusing a bundle

You can also reuse a bundle and prepend/append other message sources to it.

For instance, here is how you would append another message source to the bundle created above:

```java
MessageBundleBuilder newBuilder = bundle.thaw();

newBuilder = newBuilder.appendSource(source4);

final MessageBundle newBundle = newBuilder.freeze();
```

