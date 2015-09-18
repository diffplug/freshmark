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

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A format defined by "tag start" and "tag end" chunks of text. */
public class ParserIntronExon extends Parser {
	final String intron, exon;
	final Pattern pattern;

	/**
	 * A Parser which uses simple intron / exon string to delimit comments.
	 * <p> 
	 * Comment blocks will be parsed using the following regex:
	 * <pre>
	 * Pattern.quote(intron) + "(.*?)" + Pattern.quote(exon)
	 * </pre>
	 */
	public ParserIntronExon(String intron, String exon) {
		this(intron, exon, Pattern.quote(intron) + "(.*?)" + Pattern.quote(exon));
	}

	/**
	 * A Parser with the given comment intron/exon pair, with a custom regex.
	 * <p>
	 * Usually, you should use the {@link #Parser(String, String)} constructor,
	 * unless there are some special rules for how comment blocks are parsed. 
	 */
	public ParserIntronExon(String intron, String exon, String regex) {
		this.intron = intron;
		this.exon = exon;
		pattern = Pattern.compile(regex, Pattern.DOTALL);
	}

	/**
	 * Given an input string, parses out the body sections from the tag sections.
	 * 
	 * @param rawInput 	the raw input string
	 * @param body		called for every chunk of text outside a tag
	 * @param tag		called for every chunk of text inside a tag
	 */
	@Override
	protected void bodyAndTags(String rawInput, Consumer<String> body, Consumer<String> tag) {
		Matcher matcher = pattern.matcher(rawInput);
		int last = 0;
		while (matcher.find()) {
			if (matcher.start() > last) {
				body.accept(rawInput.substring(last, matcher.start()));
			}
			tag.accept(matcher.group(1));
			last = matcher.end();
		}
		if (last < rawInput.length()) {
			body.accept(rawInput.substring(last));
		}
	}

	/**
	 * Reassembles a section/script/output chunk back into
	 * the full file.
	 * 
	 * @param section
	 * @param script
	 * @param output
	 * @return
	 */
	@Override
	protected String reassemble(String section, String script, String output) {
		// make sure that the compiled output starts and ends with a newline,
		// so that the tags stay separated separated nicely
		if (!output.startsWith("\n")) {
			output = "\n" + output;
		}
		if (!output.endsWith("\n")) {
			output = output + "\n";
		}
		return intron + " " + section + "\n" +
				script +
				exon +
				output +
				intron + " /" + section + " " + exon;
	}
}
