# <img align="left" src="freshmark.png"> FreshMark: Keep your markdown fresh
<!---freshmark shields
output = [
	link(shield('Maven artifact', 'mavenCentral', '{{group}}:{{name}}', 'blue'), 'https://bintray.com/{{org}}/opensource/{{name}}/view'),
	link(shield('Latest version', 'latest', '{{stable}}', 'blue'), 'https://github.com/{{org}}/{{name}}/releases/latest'),
	link(shield('Javadoc', 'javadoc', 'OK', 'blue'), 'https://{{org}}.github.io/{{name}}/javadoc/{{stable}}/'),
	link(shield('License Apache', 'license', 'Apache', 'blue'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)'),
	'',
	link(shield('Changelog', 'changelog', '{{version}}', 'bright-green'), 'CHANGES.md'),
	link(image('Travis CI', 'https://travis-ci.org/{{org}}/{{name}}.svg?branch=master'), 'https://travis-ci.org/{{org}}/{{name}}'),
	'',
	link(shield('Gradle', 'supported', 'https://github.com/diffplug/spotless#adding-spotless-to-java-source', 'green'), 'CHANGES.md'),
	link(shield('CLI', 'supported', '{{version}}', 'green'), 'CHANGES.md'),
	].join('\n')
-->
[![Maven artifact](https://img.shields.io/badge/mavenCentral-com.diffplug.freshmark%3Afreshmark-blue.svg)](https://bintray.com/diffplug/opensource/freshmark/view)
[![Latest version](https://img.shields.io/badge/latest-1.2.0-blue.svg)](https://github.com/diffplug/freshmark/releases/latest)
[![Javadoc](https://img.shields.io/badge/javadoc-OK-blue.svg)](https://diffplug.github.io/freshmark/javadoc/1.2.0/)
[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))

[![Changelog](https://img.shields.io/badge/changelog-1.3.0--SNAPSHOT-bright--green.svg)](CHANGES.md)
[![Travis CI](https://travis-ci.org/diffplug/freshmark.svg?branch=master)](https://travis-ci.org/diffplug/freshmark)

[![Gradle](https://img.shields.io/badge/supported-https%3A%2F%2Fgithub.com%2Fdiffplug%2Fspotless%23adding--spotless--to--java--source-green.svg)](CHANGES.md)
[![CLI](https://img.shields.io/badge/supported-1.3.0--SNAPSHOT-green.svg)](CHANGES.md)
<!---freshmark /shields -->

Generating URL's for the buttons above is tricky.  Once they're generated, it's hard to keep them up-to-date as new versions are released.  FreshMark solves the "Markdown with variables" problem by embedding tiny JavaScript SCRIPTs into the comments of your Markdown, which statically generate the rest of the document.  By running these SCRIPTs as part of your build script, your project's documentation will always stay up-to-date.

Here is what the code looks like for the shields at the top of this document:

```javascript
<!---freshmark shields
output = [
	link(shield('Maven artifact', 'mavenCentral', '{{group}}:{{name}}', 'blue'), 'https://bintray.com/{{org}}/opensource/{{name}}/view'),
	link(shield('Latest version', 'latest', '{{stable}}', 'blue'), 'https://github.com/{{org}}/{{name}}/releases/latest'),
	link(shield('Javadoc', 'javadoc', 'OK', 'blue'), 'https://{{org}}.github.io/{{name}}/javadoc/{{stable}}/'),
	link(shield('License Apache', 'license', 'Apache', 'blue'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)'),
	].join('\n')
-->
[![Maven artifact](https://img.shields.io/badge/mavenCentral-com.diffplug.freshmark%3Afreshmark-blue.svg)](https://bintray.com/diffplug/opensource/freshmark/view)
[![Latest version](https://img.shields.io/badge/latest-1.2.0-blue.svg)](https://github.com/diffplug/freshmark/releases/latest)
[![Javadoc](https://img.shields.io/badge/javadoc-OK-blue.svg)](https://diffplug.github.io/freshmark/javadoc/1.2.0/)
[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
<!---freshmark /shields -->
```

In addition to generating Markdown from scratch, FreshMark can also modify existing Markdown.  This ensures that any inline documentation links stay fresh.

```javascript
<!---freshmark javadoc
output = prefixDelimiterReplace(input, 'https://{{org}}.github.io/{{name}}/javadoc/', '/', stable)
-->
To run FreshMark on some text, call [FreshMark.compile()](https://diffplug.github.io/freshmark/javadoc/1.2.0/com/diffplug/freshmark/FreshMark.html)
<!---freshmark /javadoc -->
```

## How it works

FreshMark has three pieces, `SECTION`, `SCRIPT`, and `BODY`.  They are parsed as shown below:

```javascript
<!---freshmark SECTION
var SCRIPT = 'any javascript can go here';
// this particular freshmark script isn't very useful
output = input;
-->
BODY (markdown)
<!---freshmark /SECTION -->
```

When `SCRIPT` is run, there are two magic variables:

* `input` - This is everything inside of BODY (guaranteed to have only unix newlines at runtime)
* `output` - The script must assign to this variable.  FreshMark will generate a new string where the `BODY` section has been replaced with this value.

Only four functions are provided:

* `link(text, url)` - returns a markdown link
* `image(altText, url)` - returns a markdown image
* `shield(altText, subject, status, color)` - returns a markdown image generated by [shields.io](http://shields.io/).
* `prefixDelimReplace(input, prefix, delimiter, replace)` - updates URLs which contain version numbers.
	* example: for parameters `prefix='http://website/', delimiter='/', replace='2.0'`
	* input `[entry point](http://website/1.2/docs/entryPoint)` would be transformed into `[entry point](http://website/2.0/docs/entryPoint)`

It's full ECMAScript 5.1, so you can define any other functions you like, but these should be all you need.

When you run FreshMark, you can supply it with a map of key-value pairs using the command line or via a properties file.  If you're running FreshMark from a build system plugin such as Gradle, then all of your project's metadata will automatically be supplied.  These key-value pairs are used in the following way:

* Before `SCRIPT` is executed, any `{{key}}` templates will be substituted with their corresponding value.
* When `SCRIPT` is executed, all of these key-value pairs are available as variables.

## How to run it

At the moment, you can run FreshMark using [Gradle](#gradle), the [console](#console), or the [Java API](#java-api directly.  If you need a different way to run FreshMark, build it and submit a PR!  We'd be happy to help [in Gitter](https://gitter.im/diffplug/freshmark).

### Gradle

Integration with Gradle is provided through the [Spotless](https://github.com/diffplug/spotless) plugin.  Spotless can also enforce lots of style rules as well (tab vs whitespace, Java import ordering, etc), but it's completely a-la-carte.  To just apply FreshMark to all of the markdown in your project (and nothing else), simply add this to your `build.gradle`:

```groovy
plugins {
	id 'com.diffplug.gradle.spotless' version '1.4.0'
}

spotless {
	freshmark '**/*.md'
}
```

See the [spotless docs] for more details.

### CLI

This repo is a command line application.  Just run `freshmark.bat` (Windows) or `freshmark` (Linux and Mac) to run it.

```
freshmark --help


```

### Java API

There's just one class that really matters.  If you want to add more functions, change which variables are there, make the behavior depend on the section name, etc, just take a peek at [FreshMark.java](src/main/java/com/diffplug/freshmark/FreshMark.java).

## Acknowledgements
* Scripts run by [JScriptBox](https://github.com/diffplug/jscriptbox).
* Bugs found by [findbugs](http://findbugs.sourceforge.net/), [as such](https://github.com/diffplug/durian-rx/blob/v1.0/build.gradle?ts=4#L92-L116).
* Scripts in the `.ci` folder are inspired by [Ben Limmer's work](http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/).
* Built by [gradle](http://gradle.org/).
* Tested by [junit](http://junit.org/).
* Maintained by [DiffPlug](http://www.diffplug.com/).
