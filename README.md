<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h2>What this is</h2>

<p>This is meant to be a lightweight, strings-only alternative to Java's <span
class="font-family: monospace;">ResourceBundle</span> for getting messages from
properties files and ultimately, various other resources.</p>

<h2>Versions</h2>

<p>The current version is <b>0.2</b>. Javadoc <a
href="http://fge.github.io/msg-simple/index.html">here</a>.</p>

<h2>Maven artifact</h2>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>msg-simple</artifactId>
    <version>your-version-here</version>
</dependency>
```

<h2>Features and roadmap</h2>

<p>The list of features is, at this moment, quite small, but useful enough that this library is
usable. See <a href="https://github.com/fge/msg-simple/wiki/Features">here</a>.</p>

<p>The roadmap for future versions can be found <a
href="https://github.com/fge/msg-simple/wiki/Roadmap">here</a>. Feature requests are of course
welcome!</p>

<h2>Sample usage</h2>

<p><i>Note: not up to date</i></p>

<p>In order to use this library, you will need two things:</p>

<ul>
    <li>build a set of `MessageSource`s;</li>
    <li>build a `MessageBundle` out of these sources.</li>
</ul>

<h3>Message sources</h3>

<p>This library provides two `MessageSource` implementations: one `Map`-based implementation, and
another using Java property files. You will note that property files are read in UTF-8.</p>

<p>Some examples:</p>

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

<h3>Build the message bundle</h3>

<p>Once you are done building your set of sources, you can build a `MessageBundle`. For this, you
use its builder class, and append or prepend message sources as you see fit:</p>

```java
MessageBundle.Builder builder = new MessageBundle.Builder();

// Append two sources
builder = builder.appendSource(source1).appendSource(source2);
// Prepend another one
builder = builder.prependSource(source3);

// Finally, build the bundle
final MessageBundle bundle = builder.build();
```

<h3>Reusing a bundle</h3>

<p>You can also reuse a bundle and prepend/append other message sources to it.</p>

<p>For instance, here is how you would append another message source to the bundle created above:

```java
MessageBundle.Builder newBuilder = bundle.copy();

newBuilder = newBuilder.appendSource(source4);

final MessageBundle newBundle = newBuilder.build();
```


