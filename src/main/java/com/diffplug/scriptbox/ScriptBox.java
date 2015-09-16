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

import static com.diffplug.scriptbox.ArityN.*;

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
public class ScriptBox {
	private Map<String, Object> map = new HashMap<>();

	protected ScriptBox() {}

	/** Creates a new NashornHarness and returns it. */
	public static ScriptBox create() {
		return new ScriptBox();
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

		public ScriptBox toValue(Object value) {
			map.put(name, value);
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

	protected String mapInitScript(String mapName) {
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
