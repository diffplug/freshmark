package com.diffplug.freshmark;

/**
 * Interface which can compile a single section of a freshmark document.
 */
@FunctionalInterface
public interface Compiler {
	String compile(String section, String program, String in);
}
