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

