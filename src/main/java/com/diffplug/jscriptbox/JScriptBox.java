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

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.diffplug.jscriptbox.ArityN.*;

/**
 * When exposing a scripting API, you want to expose
 * a set of objects and functions to the client code.
 * <p>
 * ScriptBox makes it easy to expose both objects and
 * functions to client code in a language-agnostic way.
 */
public class JScriptBox {
	private Map<String, Object> names = new HashMap<>();

	protected JScriptBox() {}

	/** Creates a new NashornHarness and returns it. */
	public static JScriptBox create() {
		return new JScriptBox();
	}

	/** Sets all of the properties contained in the given map. */
	public JScriptBox setAll(Map<String, ?> map) {
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

		public JScriptBox toValue(Object value) {
			names.put(name, value);
			return JScriptBox.this;
		}

		// @formatter:off
		public 				JScriptBox toVoid0(Void0 value) { return toValue(value); }
		public <A>			JScriptBox toVoid1(Void1<A> value) { return toValue(value); }
		public <A, B>		JScriptBox toVoid2(Void2<A, B> value) { return toValue(value); }
		public <A, B, C>	JScriptBox toVoid3(Void3<A, B, C> value) { return toValue(value); }
		public <A, B, C, D>	JScriptBox toVoid4(Void4<A, B, C, D> value) { return toValue(value); }

		public <R>				JScriptBox toFunc0(Func0<R> value) { return toValue(value); }
		public <A, R>			JScriptBox toFunc1(Func1<A, R> value) { return toValue(value); }
		public <A, B, R>		JScriptBox toFunc2(Func2<A, B, R> value) { return toValue(value); }
		public <A, B, C, R>		JScriptBox toFunc3(Func3<A, B, C, R> value) { return toValue(value); }
		public <A, B, C, D, R>	JScriptBox toFunc4(Func4<A, B, C, D, R> value) { return toValue(value); }
		// @formatter:on
	}

	/** Returns a {@link ScriptEngine} which has been populated with the values of this box. */
	public ScriptEngine build(Language language) throws ScriptException {
		return language.initializeEngine(names);
	}

	/** Returns a {@link TypedScriptEngine} which has been populated with the values of this box. */
	public TypedScriptEngine buildTyped(Language language) throws ScriptException {
		return new TypedScriptEngine(build(language));
	}
}
