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

/**
 * Turns out that if you just give an `@FunctionalInterface`
 * to Nashorn, it will call the method correctly.
 * <p>
 * In order to make it easy to concisely specify functions, here
 * they are with consistent naming conventions, for arity 0 to 4,
 * with and without return values.
 */
public final class ArityN {
	private ArityN() {}

	// @formatter:off
	@FunctionalInterface public interface Void0 {				void apply() throws Throwable; }
	@FunctionalInterface public interface Void1<A> {			void apply(A a) throws Throwable; }
	@FunctionalInterface public interface Void2<A, B> {			void apply(A a, B b) throws Throwable; }
	@FunctionalInterface public interface Void3<A, B, C> {		void apply(A a, B b, C c) throws Throwable; }
	@FunctionalInterface public interface Void4<A, B, C, D> {	void apply(A a, B b, C c, D d) throws Throwable; }
	@FunctionalInterface public interface Func0<R> {			R apply() throws Throwable; }
	@FunctionalInterface public interface Func1<A, R> {			R apply(A a) throws Throwable; }
	@FunctionalInterface public interface Func2<A, B, R> {		R apply(A a, B b) throws Throwable;	}
	@FunctionalInterface public interface Func3<A, B, C, R> {	R apply(A a, B b, C c) throws Throwable; }
	@FunctionalInterface public interface Func4<A, B, C, D, R> {R apply(A a, B b, C c, D d) throws Throwable; }
	// @formatter:on
}
