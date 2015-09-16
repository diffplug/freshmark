package com.diffplug.freshmark;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/** A TextLambda takes a list of Strings, and returns a new String. */
public interface TextLambda {
	String result(List<String> args);

	public static TextLambda from1(Function<?, String> a) {
		return null;
	}

	public static TextLambda from2(BiFunction<?, ?, String> a) {
		return null;
	}

	public static TextLambda from3(TriFunction<?, ?, ?, String> a) {
		return null;
	}

	public static TextLambda from4(QuadFunction<?, ?, ?, ?, String> a) {
		return null;
	}

	interface TriFunction<A, B, C, R> {
		R apply(A a, B b, C c);
	}

	interface QuadFunction<A, B, C, D, R> {
		R apply(A a, B b, C c, D d);
	}
}
