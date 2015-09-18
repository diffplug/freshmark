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

/**
 * A CommentScript is a way of automatically generating
 * or modifying parts of a document by embedding scripts
 * in the comments of that document.
 * <p>
 * A CommentScript has the following form:
 * <pre>
 * {@code
 * [INTRON] sectionName
 * script
 * script
 * [EXON]
 * lastProgramExecutionResult
 * lastProgramExecutionResult
 * lastProgramExecutionResult
 * [INTRON] /sectionName [EXON]
 * }
 * </pre>
 * This class is a minimal implementation of a CommentScript.  To create a CommentScript,
 * you must provide:
 * <ul>
 * <li>The intron and exon strings in the constructor.</li>
 * <li>{@link #keyToValue} - defines how template keys in the script string are transformed into values</li>
 * <li>{@link #setupScriptEngine} - initializes any functions or variables which should be available to the script</li>
 * </ul>
 * See {@link FreshMark} for a sample implementation.
 */
public abstract class CommentScriptMustache implements CommentScript.Templater {
	public static CommentScriptMustache keyToValue(Function<String, String> function) {
		return new CommentScriptMustache() {
			@Override
			protected String keyToValue(String section, String key) {
				return function.apply(key);
			}
		};
	}

	/** Replaces whatever is inside of {@code &#123;&#123;key&#125;&#125;} tags using the {@code keyToValue} function. */
	@Override
	public String template(String section, String script) {
		return mustacheTemplate(script, key -> keyToValue(section, key));
	}

	/** For the given section, return the templated value for the given key. */
	protected abstract String keyToValue(String section, String key);

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
