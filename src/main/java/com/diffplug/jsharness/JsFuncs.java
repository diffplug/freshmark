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
	public interface VoidN {
		void apply(Object... args);
	}

	@FunctionalInterface
	public interface Void0 extends VoidN {
		void applySpecific();

		@Override
		default void apply(Object... args) {
			assertThat(args.length == 0, "Must have 0 args, this was %s.", args.length);
			apply();
		}
	}

	@FunctionalInterface
	public interface Void1<A> extends VoidN {
		void applySpecific(A a);

		@Override
		default void apply(Object... args) {
			assertThat(args.length == 1, "Must have 1 args, this was %s.", args.length);
			apply(args[0]);
		}
	}

	@FunctionalInterface
	public interface Void2<A, B> extends VoidN {
		void applySpecific(A a, B b);

		@Override
		default void apply(Object... args) {
			assertThat(args.length == 2, "Must have 2 args, this was %s.", args.length);
			apply(args[0], args[1]);
		}
	}

	@FunctionalInterface
	public interface Void3<A, B, C> extends VoidN {
		void applySpecific(A a, B b, C c);

		@Override
		default void apply(Object... args) {
			assertThat(args.length == 3, "Must have 3 args, this was %s.", args.length);
			apply(args[0], args[1], args[2]);
		}
	}

	@FunctionalInterface
	public interface Void4<A, B, C, D> extends VoidN {
		void applySpecific(A a, B b, C c, D d);

		@Override
		default void apply(Object... args) {
			assertThat(args.length == 4, "Must have 4 args, this was %s.", args.length);
			apply(args[0], args[1], args[2], args[3]);
		}
	}

	///////////////////////////////////
	// Functions that return a value //
	///////////////////////////////////
	@FunctionalInterface
	public interface FuncN {
		Object apply(Object... args);
	}

	@FunctionalInterface
	public interface Func0<R> extends FuncN {
		R applySpecific();

		@Override
		default Object apply(Object... args) {
			assertThat(args.length == 0, "Must have 0 args, this was %s.", args.length);
			return apply();
		}
	}

	@FunctionalInterface
	public interface Func1<A, R> extends FuncN {
		R applySpecific(A a);

		@Override
		default Object apply(Object... args) {
			assertThat(args.length == 1, "Must have 1 args, this was %s.", args.length);
			return apply(args[0]);
		}
	}

	@FunctionalInterface
	public interface Func2<A, B, R> extends FuncN {
		R applySpecific(A a, B b);

		@Override
		default Object apply(Object... args) {
			assertThat(args.length == 2, "Must have 2 args, this was %s.", args.length);
			return apply(args[0], args[1]);
		}
	}

	@FunctionalInterface
	public interface Func3<A, B, C, R> extends FuncN {
		R applySpecific(A a, B b, C c);

		@Override
		default Object apply(Object... args) {
			assertThat(args.length == 3, "Must have 3 args, this was %s.", args.length);
			return apply(args[0], args[1], args[2]);
		}
	}

	@FunctionalInterface
	public interface Func4<A, B, C, D, R> extends FuncN {
		R applySpecific(A a, B b, C c, D d);

		@Override
		default Object apply(Object... args) {
			assertThat(args.length == 4, "Must have 4 args, this was %s.", args.length);
			return apply(args[0], args[1], args[2], args[3]);
		}
	}
}
