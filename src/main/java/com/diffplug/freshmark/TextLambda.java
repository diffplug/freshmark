/*
 * Copyright 2015 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
