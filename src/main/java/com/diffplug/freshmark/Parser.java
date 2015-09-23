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

import javax.script.ScriptException;

/**
 * Parser splits text into tags and body sections, passes
 * pairs of matching sections to a {@link SectionCompiler}, then
 * combines that output with the original document to generate
 * the final output.
 * 
 * @see ParserIntronExon
 */
public abstract class Parser {
	/** Interface which can compile a single section of a FreshMark document. */
	@FunctionalInterface
	public interface SectionCompiler {
		String compileSection(String section, String script, String input);
	}

	/** Interface for passing a chunk of the document. */
	@FunctionalInterface
	protected interface ChunkHandler {
		/**
		 * @param startIdxFromRaw 	index of the start of content, relative to the beginning of the raw input
		 * @param content			the content to be handled
		 */
		void handle(int startIdxFromRaw, String content) throws ScriptException;
	}

	/**
	 * Given an input string, parses out the body sections from the tags section.
	 * 
	 * @param fullInput	the raw input string
	 * @param body		called for every chunk of text outside a tag
	 * @param tag		called for every chunk of text inside a tag
	 * @throws ScriptException 
	 */
	protected abstract void bodyAndTags(String fullInput, ChunkHandler body, ChunkHandler tag) throws ScriptException;

	/** Reassembles a section/script/output chunk back into the full file. */
	protected abstract String reassemble(String section, String script, String output);

	/**
	 * Compiles an input string to an output string, using the given compiler to compile each section.
	 * 
	 * @param fullInput	the raw input string
	 * @param compiler	used to compile each section
	 * @return 			the compiled output string
	 * @throws ScriptException 
	 */
	public String compile(String fullInput, SectionCompiler compiler) throws ScriptException {
		StringBuilder result = new StringBuilder(fullInput.length() * 3 / 2);
		/** Associates errors with the part of the input that caused it. */
		@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "It's a bug in FindBugs.  TODO: report")
		class ErrorFormatter {
			ChunkHandler wrap(Consumer<String> action) {
				return (int startIdxFromRaw, String content) -> {
					try {
						action.accept(content);
					} catch (Throwable e) {
						if (e.getCause() instanceof ScriptException) {
							ScriptException script = (ScriptException) e.getCause();
							int problemStart = script.getLineNumber() + countNewlines(fullInput.substring(0, startIdxFromRaw));
							ScriptException wrappedScript = new ScriptException(script.getCause().getMessage(), script.getFileName(), problemStart, script.getColumnNumber());
							wrappedScript.initCause(script.getCause());
							throw wrappedScript;
						} else {
							long problemStart = 1 + countNewlines(fullInput.substring(0, startIdxFromRaw));
							throw new RuntimeException("Error on line " + problemStart + ": " + e.getMessage(), e);
						}
					}
				};
			}

			private int countNewlines(String str) {
				return (int) str.codePoints().filter(c -> c == '\n').count();
			}
		}

		/** Maintains the parse state. */
		@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = {"UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS", "SIC_INNER_SHOULD_BE_STATIC_ANON"}, justification = "It's a bug in FindBugs.  TODO: report")
		class State {
			/** The section for which we're looking for a close tag. */
			String section;
			/** The script for that section. */
			String script;
			/** The raw input which will be passed to the script. */
			String input;

			void body(String body) {
				assert(input == null);
				if (section == null) {
					assert(script == null);
					result.append(body);
				} else {
					assert(script != null);
					input = body;
				}
			}

			void tag(String tag) {
				if (section == null) {
					assert(script == null);
					assert(input == null);
					// we were looking for an open tag, and now we've got one
					int firstLine = tag.indexOf('\n');
					if (firstLine < 0 || tag.length() <= firstLine) {
						throw new IllegalArgumentException("Section doesn't contain a script.");
					}
					// the section name is the first line (trimmed)
					section = tag.substring(0, firstLine).trim();
					// the script is the second line
					script = tag.substring(firstLine + 1);
				} else {
					assert(script != null);
					assert(input != null);
					// we were looking for a close tag
					String closing = tag.trim();
					if (!closing.equals("/" + section)) {
						// bail if we didn't find it
						throw new IllegalArgumentException("Expecting '/" + section + "'");
					}
					// and we found one!  compile it and accumulate the result
					String compiled = compiler.compileSection(section, script, input);
					String reassembled = reassemble(section, script, compiled);
					result.append(reassembled);
					// wipe the state
					section = null;
					script = null;
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
		bodyAndTags(fullInput, error.wrap(state::body), error.wrap(state::tag));
		state.finish();
		return result.toString();
	}
}
