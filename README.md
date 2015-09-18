# <img align="left" src="durian-rx.png"> DurianRx: Reactive getters, powered by RxJava and ListenableFuture

[![Join the chat at https://gitter.im/diffplug/freshmark](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/diffplug/freshmark?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Maven artifact](https://img.shields.io/badge/mavenCentral-com.diffplug.durian%3Adurian--rx-blue.svg)](https://bintray.com/diffplug/opensource/durian-rx/view)
[![Latest version](http://img.shields.io/badge/latest-1.0.1-blue.svg)](https://github.com/diffplug/durian-rx/releases/latest)
[![Javadoc](http://img.shields.io/badge/javadoc-OK-blue.svg)](https://diffplug.github.io/durian-rx/javadoc/1.0.1/)
[![License](https://img.shields.io/badge/license-Apache-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))

[![Changelog](http://img.shields.io/badge/changelog-1.1.0--SNAPSHOT-brightgreen.svg)](CHANGES.md)
[![Travis CI](https://travis-ci.org/diffplug/durian-rx.svg?branch=master)](https://travis-ci.org/diffplug/durian-rx)

DurianRx unifies RxJava's [Observable](http://reactivex.io/documentation/observable.html) with Guava's [ListenableFuture](https://code.google.com/p/guava-libraries/wiki/ListenableFutureExplained).  If you happen to be using SWT as a widget toolkit, then you'll want to look at [DurianSwt](https://github.com/diffplug/durian-swt) as well.

```java
Observable<SomeType> observable = someObservable();
ListenableFuture<SomeType> future = someFuture();
Rx.subscribe(observable, val -> doSomething(val));
Rx.subscribe(future, val -> doSomething(val));
```

It also provides [reactive getters](src/com/diffplug/common/rx/RxGetter.java?ts=4), a simple abstraction for piping data which allows access via `T get()` or `Observable<T> asObservable()`.

```java
RxBox<Point> mousePos = RxBox.of(new Point(0, 0));
this.addMouseListener(e -> mousePos.set(new Point(e.x, e.y)));

Rectangle hotSpot = new Rectangle(0, 0, 10, 10)
RxGetter<Boolean> isMouseOver = mousePos.map(hotSpot::contains);
```

Debugging an error which involves lots of callbacks can be difficult.  To make this easier, DurianRx includes a [tracing capability](src/com/diffplug/common/rx/RxTracingPolicy.java?ts=4), which makes this task easier.

```java
// anytime an error is thrown in an Rx callback, the stack trace of the error
// will be wrapped by the stack trace of the original subscription
DurianPlugins.set(RxTracingPolicy.class, new LogSubscriptionTrace()).
```

Lastly, DurianRx provides convenience classes for manipulating Guava's immutable collections inside reactive containers, such as `RxSet<T> extends RxBox<ImmutableSet<T>>`, which can be used as such:

```java
public void mouseClicked(MouseEvent e) {
	rxMouseOver.get().ifPresent(cell -> {
		Set<Integer> currentSelection = rxSelection.get();
		if (e.isControlDown()) {
			// control => toggle mouseOver item in selection
			if (currentSelection.contains(cell)) {
				rxSelection.remove(cell);
			} else {
				rxSelection.add(cell);
			}
		} else {
			// no control => set selection to mouseOver
			rxSelection.set(Collections.singleton(cell));
		}
	});
}

...

Rx.subscribe(rxSelection, set -> {
	// take some action in response to
	// selection change
});
```

Perhaps most useful of all is the [Immutables](https://diffplug.github.io/durian-rx/javadoc/snapshot/com/diffplug/common/rx/Immutables.html) utility class, which helps with all kinds of manipulations of Guava's immmutable collections.

DurianRx's only requirements are [Guava](https://github.com/google/guava), [RxJava](https://github.com/reactivex/rxjava), and [Durian](https://github.com/diffplug/durian).

## Acknowledgements

* Many thanks to [RxJava](https://github.com/reactivex/rxjava) and [Guava](https://github.com/google/guava).
* Stream Collectors for Guava collections inspired by [Maciej Miklas's blog post](http://blog.comsysto.com/2014/11/12/java-8-collectors-for-guava-collections/).
* Formatted by [spotless](https://github.com/diffplug/spotless), [as such](https://github.com/diffplug/durian-rx/blob/v1.0/build.gradle?ts=4#L70-L90).
* Bugs found by [findbugs](http://findbugs.sourceforge.net/), [as such](https://github.com/diffplug/durian-rx/blob/v1.0/build.gradle?ts=4#L92-L116).
* Scripts in the `.ci` folder are inspired by [Ben Limmer's work](http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/).
* Built by [gradle](http://gradle.org/).
* Tested by [junit](http://junit.org/).
* Maintained by [DiffPlug](http://www.diffplug.com/).
