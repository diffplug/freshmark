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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diffplug.common.base.Errors;
import com.diffplug.scriptbox.Language;
import com.diffplug.scriptbox.ScriptBox;
import com.diffplug.scriptbox.TypedScriptEngine;

public class FreshMark implements Compiler {
	static final Parser parser = new Parser("freshmark");

	@Override
	public String compile(String section, String program, String input) {
		return Errors.rethrow().get(() -> {
			TypedScriptEngine engine = ScriptBox.create()
					.set("link").toFunc2(FreshMark::link)
					.set("image").toFunc2(FreshMark::image)
					.set("shield").toFunc4(FreshMark::shield)
					.set("prefixDelimReplacement").toFunc4(FreshMark::prefixDelimReplacement)
					.buildTyped(Language.nashorn());

			engine.getRaw().put("input", input);
			engine.eval(program);
			return engine.get("output", String.class);
		});
	}

	public static String link(String text, String url) {
		return "[" + text + "](" + url + ")";
	}

	public static String image(String altText, String url) {
		return "!" + link(altText, url);
	}

	/** Generates shields using <a href="http://shields.io/">shields.io</a>. */
	public static String shield(String altText, String subject, String status, String color) {
		return image(altText, "https://img.shields.io/badge/" + shieldEscape(subject) + "-" + shieldEscape(status) + "-" + shieldEscape(color) + ".svg");
	}

	private static String shieldEscape(String raw) {
		return raw.replace("_", "__").replace("-", "--").replace(" ", "_");
	}

	/** Replaces everything between the  */
	public static String prefixDelimReplacement(String input, String prefix, String delim, String replacement) {
		StringBuilder builder = new StringBuilder(input.length() * 3 / 2);

		int lastElement = 0;
		Pattern pattern = Pattern.compile("(.*?" + Pattern.quote(prefix) + ")(.*?)(" + Pattern.quote(delim) + ")", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			builder.append(matcher.group(1));
			builder.append(replacement);
			builder.append(matcher.group(3));
			lastElement = matcher.end();
		}
		builder.append(input.substring(lastElement));
		return builder.toString();
	}
}
