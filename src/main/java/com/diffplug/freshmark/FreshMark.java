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

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diffplug.common.base.Errors;
import com.diffplug.jscriptbox.Language;
import com.diffplug.jscriptbox.ScriptBox;
import com.diffplug.jscriptbox.TypedScriptEngine;

/**
 * The core implementation a FreshMark compiler.  Provides two methods:
 * <ul>
 * <li>{@link #keyToValue} - defines how template keys are transformed into values</li>
 * <li>{@link #setupScriptEngine} - initializes any functions or variables which should be available to the program</li>
 * </ul>
 * See {@link FreshMarkDefault} for the default implementation.
 */
public abstract class FreshMark {
	/** Parser which splits up the raw document into structured tags which get passed to the compiler. */
	static final Parser parser = new Parser("<!---freshmark", "-->");

	/** Compiles a single section/program/input combo into the appropriate output. */
	final Parser.Compiler compiler = new Parser.Compiler() {
		@Override
		public String compileSection(String section, String program, String input) {
			return Errors.rethrow().get(() -> {
				ScriptBox box = ScriptBox.create();
				setupScriptEngine(section, box);
				TypedScriptEngine engine = box.buildTyped(Language.nashorn());

				// apply the templating engine to the program
				String templatedProgram = template(program, key -> keyToValue(section, key));
				// populate the input data
				engine.getRaw().put("input", input);
				// evaluate the program and get the result
				engine.eval(templatedProgram);
				String compiled = engine.get("output", String.class);
				// make sure that the compiled output starts and ends with a newline,
				// so that the tags stay separated separated nicely
				if (!compiled.startsWith("\n")) {
					compiled = "\n" + compiled;
				}
				if (!compiled.endsWith("\n")) {
					compiled = compiled + "\n";
				}
				return parser.prefix + " " + section + "\n" +
						program +
						parser.postfix +
						compiled +
						parser.prefix + " /" + section + " " + parser.postfix;
			});
		}
	};

	/** Compiles the given input string. Input must contain only unix newlines, output is guaranteed to be the same. */
	public String compile(String input) {
		return parser.compile(input, compiler);
	}

	/** For the given section, return the proper templated value for the given key. */
	protected abstract String keyToValue(String section, String key);

	/** For the given section, setup the JScriptBox appropriately.  The `input` value will be set for you, but you need to do everything else. */
	protected abstract void setupScriptEngine(String section, ScriptBox scriptBox);

	/** Replaces whatever is inside of {@code &#123;&#123;key&#125;&#125;} tags using the {@code keyToValue} function. */
	static String template(String input, Function<String, String> keyToValue) {
		Matcher matcher = TEMPLATE.matcher(input);
		StringBuilder result = new StringBuilder(input.length() * 3 / 2);

		int lastElement = 0;
		while (matcher.find()) {
			result.append(matcher.group(1));
			result.append(keyToValue.apply(matcher.group(2)));
			lastElement = matcher.end();
		}
		result.append(input.substring(lastElement));
		return result.toString();
	}

	private static final Pattern TEMPLATE = Pattern.compile("(.*?)\\{\\{(.*?)\\}\\}", Pattern.DOTALL);
}
