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
package com.diffplug.jscriptbox;

import java.util.Objects;
import java.util.Optional;

/** Cheap (and performant) knock-off of Guava's Preconditions class. */
final class Check {
	private Check() {}

	/** If test is false, throws an exception with a message where {@code %0} is replaced with {@code Objects.toString(o0)}. */
	static void that(boolean test, String errorMsg, Object o0) {
		if (!test) {
			errorMsg = errorMsg.replace("%0", Objects.toString(o0));
			throw new IllegalArgumentException(errorMsg);
		}
	}

	/** If test is false, throws an exception with a message where {@code %0}, {@code %1} is replaced with {@code Objects.toString(o0)}, {@code Objects.toString(o1)}. */
	static void that(boolean test, String errorMsg, Object o0, Object o1) {
		if (!test) {
			errorMsg = errorMsg
					.replace("%0", Objects.toString(o0))
					.replace("%1", Objects.toString(o1));
			throw new IllegalArgumentException(errorMsg);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> T cast(Object o, Class<T> clazz) {
		if (o == null) {
			throw new IllegalArgumentException("Expected object of type '" + clazz + "', was 'null'");
		} else {
			Check.that(clazz.isAssignableFrom(o.getClass()), "Expected object of type '%0', was '%1'", clazz, o.getClass());
			return (T) o;
		}
	}

	@SuppressWarnings("unchecked")
	static <T> Optional<T> castOpt(Object o, Class<T> clazz) {
		if (o == null) {
			return Optional.empty();
		} else {
			Check.that(clazz.isAssignableFrom(o.getClass()), "Expected object of type '%0', was '%1'", clazz, o.getClass());
			return Optional.of((T) o);
		}
	}
}
