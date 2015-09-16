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
package com.diffplug.jsharness;


public final class JsFuncs {
	public static void assertThat(boolean value, String error, Object errorObj) {
		if (!value) {
			throw new IllegalArgumentException(error.replace("%s", errorObj.toString()));
		}
	}

	private JsFuncs() {}

	////////////////////////////////
	// Functions that return void //
	////////////////////////////////
	@FunctionalInterface
	public interface Void0 {
		void applySpecific();
	}

	@FunctionalInterface
	public interface Void1<A> {
		void applySpecific(A a);
	}

	@FunctionalInterface
	public interface Void2<A, B> {
		void applySpecific(A a, B b);
	}

	@FunctionalInterface
	public interface Void3<A, B, C> {
		void applySpecific(A a, B b, C c);
	}

	@FunctionalInterface
	public interface Void4<A, B, C, D> {
		void applySpecific(A a, B b, C c, D d);
	}

	///////////////////////////////////
	// Functions that return a value //
	///////////////////////////////////
	@FunctionalInterface
	public interface Func0<R> {
		R applySpecific();
	}

	@FunctionalInterface
	public interface Func1<A, R> {
		R applySpecific(A a);
	}

	@FunctionalInterface
	public interface Func2<A, B, R> {
		R applySpecific(A a, B b);
	}

	@FunctionalInterface
	public interface Func3<A, B, C, R> {
		R applySpecific(A a, B b, C c);
	}

	@FunctionalInterface
	public interface Func4<A, B, C, D, R> {
		R applySpecific(A a, B b, C c, D d);
	}
}
