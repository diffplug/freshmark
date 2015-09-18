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

import java.io.Reader;
import java.util.Optional;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/** Wraps up a ScriptEngine with a null-checking and type-checking API. */
public class TypedScriptEngine {
	private final ScriptEngine scriptEngine;

	public TypedScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	/** No return value. */
	public void eval(String script) throws ScriptException {
		scriptEngine.eval(script);
	}

	/** No return value. */
	public void eval(Reader reader) throws ScriptException {
		scriptEngine.eval(reader);
	}

	/** Nulls and wrong class are errors. */
	public <T> T eval(String script, Class<T> clazz) throws ScriptException {
		return Check.cast(scriptEngine.eval(script), clazz);
	}

	/** Nulls and wrong class are errors. */
	public <T> T eval(Reader reader, Class<T> clazz) throws ScriptException {
		return Check.cast(scriptEngine.eval(reader), clazz);
	}

	/** Nulls and wrong class are errors. */
	public <T> T get(String name, Class<T> clazz) throws ScriptException {
		return Check.cast(scriptEngine.get(name), clazz);
	}

	/** Wrong class is an error. */
	public <T> Optional<T> evalOpt(String script, Class<T> clazz) throws ScriptException {
		return Check.castOpt(scriptEngine.eval(script), clazz);
	}

	/** Wrong class is an error. */
	public <T> Optional<T> evalOpt(Reader reader, Class<T> clazz) throws ScriptException {
		return Check.castOpt(scriptEngine.eval(reader), clazz);
	}

	/** Wrong class is an error. */
	public <T> Optional<T> getOpt(String name, Class<T> clazz) throws ScriptException {
		return Check.castOpt(scriptEngine.get(name), clazz);
	}

	/** Puts the given value into the script engine. */
	public void put(String name, Object value) {
		scriptEngine.put(name, value);
	}

	/** The underlying ScriptEngine. */
	public ScriptEngine getRaw() {
		return scriptEngine;
	}
}
