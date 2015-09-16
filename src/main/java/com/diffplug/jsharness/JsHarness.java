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

import static com.diffplug.jsharness.ArityN.*;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Harness for setting up a JavaScript engine which
 * has had all of the following variables setup.
 * 
 * @author ntwigg
 */
public class JsHarness {
	private StringBuilder initScript = new StringBuilder();
	private Map<String, Object> map = new HashMap<>();

	protected JsHarness() {}

	/** Creates a new NashornHarness and returns it. */
	public static JsHarness create() {
		return new JsHarness();
	}

	/** Adds a script to the harness. */
	public JsHarness addScript(String script) {
		initScript.append(script);
		return this;
	}

	/** Sets a name in the script to be a value or a function. */
	public NameSetter setName(String name) {
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

		public JsHarness toValue(Object value) {
			map.put(name, value);
			return JsHarness.this;
		}

		// @formatter:off
		public 				JsHarness toVoid0(Void0 value) { return toValue(value); }
		public <A>			JsHarness toVoid1(Void1<A> value) { return toValue(value); }
		public <A, B>		JsHarness toVoid2(Void2<A, B> value) { return toValue(value); }
		public <A, B, C>	JsHarness toVoid3(Void3<A, B, C> value) { return toValue(value); }
		public <A, B, C, D>	JsHarness toVoid4(Void4<A, B, C, D> value) { return toValue(value); }

		public <R>				JsHarness toFunc0(Func0<R> value) { return toValue(value); }
		public <A, R>			JsHarness toFunc1(Func1<A, R> value) { return toValue(value); }
		public <A, B, R>		JsHarness toFunc2(Func2<A, B, R> value) { return toValue(value); }
		public <A, B, C, R>		JsHarness toFunc3(Func3<A, B, C, R> value) { return toValue(value); }
		public <A, B, C, D, R>	JsHarness toFunc4(Func4<A, B, C, D, R> value) { return toValue(value); }
		// @formatter:on
	}

	public String mapInitScript(String mapName) {
		StringBuilder builder = new StringBuilder();
		map.entrySet().forEach(entry -> {
			builder.append("var ");
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(mapName);
			builder.append(".");
			builder.append(entry.getKey());
			builder.append(";\n");
		});
		return builder.toString();
	}

	/** Returns a ScriptEngine with the stuff above. */
	public ScriptEngine build() throws ScriptException {
		ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
		ScriptContext context = jsEngine.getContext();

		jsEngine.eval(initScript.toString());
		String mapName = "abcd";
		context.setAttribute(mapName, map, ScriptContext.ENGINE_SCOPE);
		jsEngine.eval(mapInitScript(mapName));

		return jsEngine;
	}

	/** Returns a {@link TypedScriptEngine} with the stuff above. */
	public TypedScriptEngine buildTyped() throws ScriptException {
		return new TypedScriptEngine(build());
	}
}
