package com.diffplug.freshmark;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	private final String prefix, postfix;
	private final Pattern pattern;

	public Parser(String name) {
		prefix = "<!---" + name;
		postfix = "-->";
		pattern = Pattern.compile(prefix + "(.*?)" + postfix, Pattern.DOTALL);
	}

	/**
	 * Given an input string, parses out the body sections from the tag sections.
	 * 
	 * @param input 	the raw input string
	 * @param body		called for every chunk of text outside a tag
	 * @param tag		called for every chunk of text inside a tag
	 */
	void bodyAndTags(String input, Consumer<String> body, Consumer<String> tag) {
		Matcher matcher = pattern.matcher(input);
		int last = 0;
		while (matcher.find()) {
			if (matcher.start() > last) {
				body.accept(input.substring(last, matcher.start()));
			}
			tag.accept(matcher.group(1));
			last = matcher.end();
		}
		if (last < input.length()) {
			body.accept(input.substring(last));
		}
	}

	/**
	 * Compiles an input string to an output string, using the given compiler to compile each section.
	 * 
	 * @param input		the raw input string
	 * @param compiler	used to compile each section
	 * @return
	 */
	public String compile(String input, Compiler compiler) {
		StringBuilder result = new StringBuilder(input.length() * 3 / 2);
		/** Associates errors with the part of the input that caused it. */
		class ErrorFormatter {
			int numReadSoFar = 0;

			Consumer<String> wrap(Consumer<String> action) {
				return txt -> {
					try {
						action.accept(txt);
						String toRead = input.substring(numReadSoFar);
						if (toRead.startsWith(input)) {
							// body
							numReadSoFar += txt.length();
						} else {
							// tag
							String tag = prefix + txt + postfix;
							assert(toRead.startsWith(tag));
							numReadSoFar += tag.length();
						}
					} catch (Throwable e) {
						long problemStart = 1 + countNewlines(input.substring(0, numReadSoFar));
						throw new RuntimeException("Error on line " + problemStart + ": " + e.getMessage(), e);
					}
				};
			}

			private int countNewlines(String str) {
				return (int) str.codePoints().filter(c -> c == '\n').count();
			}
		}
		/** Maintains the parse state. */
		class State {
			/** The section for which we're looking for a close tag. */
			String section;
			/** The program for that section. */
			String program;
			/** The raw input which will be passed to the program. */
			String input;

			void body(String body) {
				assert(program == null);
				assert(input == null);
				if (section == null) {
					result.append(body);
				} else {
					input = body;
				}
			}

			void tag(String tag) {
				if (section == null) {
					assert(program == null);
					assert(input == null);
					// we were looking for an open tag, and now we've got one
					int firstLine = tag.indexOf('\n');
					if (firstLine < 0 || tag.length() <= firstLine) {
						throw new IllegalArgumentException("Section doesn't contain a program.");
					}
					// the section name is the first line (trimmed)
					section = tag.substring(0, firstLine).trim();
					// the program is the second line
					program = tag.substring(firstLine + 1);
				} else {
					assert(program != null);
					assert(input != null);
					// we were looking for a close tag
					String closing = tag.trim();
					if (!closing.equals("/" + section)) {
						// bail if we didn't find it
						throw new IllegalArgumentException("Expecting '/" + section + "'");
					}
					// and we found one!  compile it and accumulate the result
					String chunk = compiler.compile(section, program, input);
					result.append(chunk);
					// wipe the state
					section = null;
					program = null;
					input = null;
				}
			}

			void finish() {
				if (section != null) {
					throw new IllegalArgumentException("Ended without a close tag for '" + section + "'");
				}
			}
		}
		ErrorFormatter error = new ErrorFormatter();
		State state = new State();
		bodyAndTags(input, error.wrap(state::body), error.wrap(state::tag));
		state.finish();
		return result.toString();
	}
}
