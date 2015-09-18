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
 * COMMENT_BEGIN sectionName
 * script
 * script
 * COMMENT_END
 * body
 * body
 * body
 * COMMENT_BEGIN /sectionName COMMENT_END
 * }
 * </pre>
 * This class is a minimal implementation of a CommentScript.  To create a CommentScript,
 * you must provide:
 * <ul>
 * <li>A {@link Parser} which can separate CommentScript blocks from the underlying document.</li>
 * <li>A {@code Function<String, String>} which transforms the script into the body.</li>
 * </ul>
 * See {@link FreshMark} for a sample implementation.
 */
public class CommentScript implements Parser.SectionCompiler {
	@FunctionalInterface
	public interface SectionFunction<T, R> {
		String apply(String section, String script) throws ScriptException;
	}

	public class Builder {
		private final Parser parser;
		private SectionFunctino

		[rovate]
	}
	

	
	private Templater templater;
	private ScriptCreator scriptCreator;

	private final Parser parser;
	private final Templater templater;
	private final ScriptCreator scriptCreator;

	protected CommentScript(Parser parser, Templater template, ScriptCreator scriptCreator) {
		this.parser = parser;
		this.templater = template;
		this.scriptCreator = scriptCreator;
	}

	/** Compiles a single section/script/input combo into the appropriate output. */
	@Override
	public String compileSection(String section, String script, String input) {
		return Errors.rethrow().get(() -> {
			ScriptEngine engine = scriptCreator.createScriptEngine(section);

			// apply the templating engine to the script
			String templatedProgram = templater.template(section, script);
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
}
