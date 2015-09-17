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
package com.diffplug.scriptbox;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.diffplug.scriptbox.ArityN.*;

/**
 * When exposing a scripting API, you want to expose
 * a set of objects and functions to the client code.
 * <p>
 * ScriptBox makes it easy to expose both objects and
 * functions to client code in a language-agnostic way.
 */
public class ScriptBox {
	private Map<String, Object> names = new HashMap<>();

	protected ScriptBox() {}

	/** Creates a new NashornHarness and returns it. */
	public static ScriptBox create() {
		return new ScriptBox();
	}

	/** Sets all of the properties contained in the given map. */
	public ScriptBox setAll(Map<String, ?> map) {
		names.putAll(map);
		return this;
	}

	/** Sets a name in the script to be a value or a function. */
	public NameSetter set(String name) {
		return new NameSetter(name);
	}

	/** Checks that the given name is a valid identifier. */
	static String checkValidIdentifier(String name) {
		Check.that(name.length() > 0 &&
				Character.isJavaIdentifierStart(name.codePointAt(0)) &&
				name.codePoints().skip(1).allMatch(Character::isJavaIdentifierPart),
				"'%0' is not a valid identifier", name);
		return name;
	}

	/** Fluent API for setting names in this JsHarness. */
	public class NameSetter {
		private final String name;

		public NameSetter(String name) {
			this.name = checkValidIdentifier(name);
		}

		public ScriptBox toValue(Object value) {
			names.put(name, value);
			return ScriptBox.this;
		}

		// @formatter:off
		public 				ScriptBox toVoid0(Void0 value) { return toValue(value); }
		public <A>			ScriptBox toVoid1(Void1<A> value) { return toValue(value); }
		public <A, B>		ScriptBox toVoid2(Void2<A, B> value) { return toValue(value); }
		public <A, B, C>	ScriptBox toVoid3(Void3<A, B, C> value) { return toValue(value); }
		public <A, B, C, D>	ScriptBox toVoid4(Void4<A, B, C, D> value) { return toValue(value); }

		public <R>				ScriptBox toFunc0(Func0<R> value) { return toValue(value); }
		public <A, R>			ScriptBox toFunc1(Func1<A, R> value) { return toValue(value); }
		public <A, B, R>		ScriptBox toFunc2(Func2<A, B, R> value) { return toValue(value); }
		public <A, B, C, R>		ScriptBox toFunc3(Func3<A, B, C, R> value) { return toValue(value); }
		public <A, B, C, D, R>	ScriptBox toFunc4(Func4<A, B, C, D, R> value) { return toValue(value); }
		// @formatter:on
	}

	/** Returns a ScriptEngine with the stuff above. */
	public ScriptEngine build(Language language) throws ScriptException {
		return language.initializeEngine(names);
	}

	/** Returns a {@link TypedScriptEngine} with the stuff above. */
	public TypedScriptEngine buildTyped(Language language) throws ScriptException {
		return new TypedScriptEngine(build(language));
	}
}
