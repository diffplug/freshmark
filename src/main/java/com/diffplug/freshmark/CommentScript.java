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

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.diffplug.common.base.Errors;
import com.diffplug.jscriptbox.Check;

/**
 * A CommentScript is a way of automatically generating
 * or modifying parts of a document by embedding scripts
 * in the comments of that document.
 * <p>
 * A CommentScript has the following form:
 * <pre>
 * {@code
 * [COMMENT_START sectionName
 * script
 * script
 * COMMENT_END]
 * body
 * body
 * body
 * [COMMENT_START /sectionName COMMENT_END]
 * }
 * </pre>
 * This class is a minimal implementation of a CommentScript.  To create a CommentScript,
 * you must provide:
 * <ul>
 * <li>A {@link Parser} to split the comment text from the body text.</li>
 * <li>{@link #keyToValue} - defines how template keys in the script string are transformed into values.</li>
 * <li>{@link #setupScriptEngine} - initializes any functions or variables which should be available to the script.</li>
 * </ul>
 * @see FreshMark
 */
public abstract class CommentScript implements Parser.SectionCompiler {
	/**
	 * Creates a CommentScript using the given parser to
	 * delineate and combine comment blocks.
	 */
	protected CommentScript(Parser parser) {
		this.parser = parser;
	}

	/** Parser which splits up the raw document into structured tags which get passed to the compiler. */
	final Parser parser;

	/** Compiles a single section/script/input combo into the appropriate output. */
	@Override
	public String compileSection(String section, String script, String input) {
		return Errors.rethrow().get(() -> {
			ScriptEngine engine = setupScriptEngine(section);

			// apply the templating engine to the script
			String templatedProgram = template(section, script);
			// populate the input data
			engine.put("input", input);
			// evaluate the script and get the result
			engine.eval(templatedProgram);
			return Check.cast(engine.get("output"), String.class);
		});
	}

	/** Compiles the given input string. Input must contain only unix newlines, output is guaranteed to be the same. */
	public String compile(String input) {
		return parser.compile(input, this);
	}

	/**
	 * Performs templating on the script before passing it to the {@link ScriptEngine} created by {@link #setupScriptEngine}.
	 * <p>
	 * Defaults to mustache-based templating which uses {@link #keyToValue(String, String)} to decode keys.
	 */
	protected String template(String section, String script) {
		return mustacheTemplate(script, key -> keyToValue(section, key));
	}

	/** For the given section, return the templated value for the given key. */
	protected abstract String keyToValue(String section, String script);

	/**
	 * For the given section, setup any built-in functions and variables.
	 * <p>
	 * The {@code input} value will be set for you, and the {@code output} value will
	 * be extracted for you, but you must do everything else.
	 */
	protected abstract ScriptEngine setupScriptEngine(String section) throws ScriptException;

	/** Mustache templating. */
	static String mustacheTemplate(String input, Function<String, String> keyToValue) {
		Matcher matcher = MUSTACHE_PATTERN.matcher(input);
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

	/** Regex which matches for {@code {{key}}}. */
	private static final Pattern MUSTACHE_PATTERN = Pattern.compile("(.*?)\\{\\{(.*?)\\}\\}", Pattern.DOTALL);
}
