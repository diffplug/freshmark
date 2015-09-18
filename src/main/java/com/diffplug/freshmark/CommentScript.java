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
public abstract class CommentScript {
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
	final ParserIntronExon.SectionCompiler compiler = new ParserIntronExon.SectionCompiler() {
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
	};

	/** Compiles the given input string. Input must contain only unix newlines, output is guaranteed to be the same. */
	public String compile(String input) {
		return parser.compile(input, compiler);
	}

	/** For the given section, perform templating on the given script. */
	protected abstract String template(String section, String script);

	/**
	 * For the given section, setup any built-in functions and variables.
	 * <p>
	 * The {@code input} value will be set for you, and the {@code output} value will
	 * be extracted for you, but you must do everything else.
	 */
	protected abstract ScriptEngine setupScriptEngine(String section) throws ScriptException;
}
