## 1.1

* Dead code removal (ServiceLoader now completely disabled).
* Update to gradle 1.11.
* Work around APK bug with duplicate license files in resources.
* Re-enable test outputs.
* Work around Java 8's javadoc tool bug.

## 1.0

* Update to gradle 1.10
* Update TestNG dependency.
* Update btf dependency.
* Change to dual licensing LGPL 3.0/ASL 2.0.

## 0.9

* New autoloading support, not requiring ServiceLoader.
* Deprecate ServiceLoader support.
* Update TestNG dependency.
* Fix javadoc generation.
* Remove ServiceLoader support from build files.

## 0.8

* Build file updates, now much easier to use.
* Change timeout policy: a timed out task stays there, but default source is
  returned if the time out is reached.
* Add method to inject message sources from a full bundle in
  `MessageBundleBuilder`.

Francis Galiegue (6):
      Announce 0.7
      Gradle build files update
      LoadingMessageSourceProvider: change timeout handling completely
      MessageBundleBuilder: allow injection of providers from another bundle
      dorelease.sh: add test before uploadArchives
      project.gradle: fix misgivings :/

## 0.7

* `LoadingMessageSourceProvider`: lower default timeout from 5 seconds to 1
  second.
* Add a convenience static factory method in `MessageBundle` to build a bundle
  from a single message source.

## 0.6

* Fix a stupid error in LoadingMessageSourceProvider with regards to initial
  expiration delay.
* Migrate to gradle for build.

## 0.5

* Complete Javadoc.
* Read property files using any encoding.
* `MessageFormat` support.
* New static factory method to load a legacy `ResourceBundle`.
* Implement loading expiry and timeout.

## 0.4.1

* Fix bug wih .printf() not capturing all possible exceptions thrown by
  Formatter.

## 0.4

* Copyright updated on all files.
* Javadoc updates.
* Introduce MessageBundleFactory to grab factories; obsolete MessageBundles.

## 0.3

* ServiceLoader support.
* Deprecated API removal.
* printf()-like messages.
* Builtin assertion methods.

## 0.2

* Initial locale support.
* Initial on-demand loading support.

## 0.1

* Initial version.

