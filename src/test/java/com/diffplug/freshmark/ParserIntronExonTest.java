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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.HashMap;

import javax.script.ScriptException;

import com.diffplug.common.base.Consumers;
import com.diffplug.common.base.Errors;
import org.junit.jupiter.api.Test;

class ParserIntronExonTest {
	private static final Parser freshmarkParser = new FreshMark(new HashMap<>(), Consumers.doNothing()).parser;

	@Test
	void testBodyAndTags() {
		Arrays.asList("empty.txt",
				"mismatched.txt",
				"nocomment.txt",
				"noprogram.txt",
				"simple.txt",
				"unclosed.txt",
				"unclosedthenstuff.txt")
				.forEach(Errors.rethrow().wrap(ParserIntronExonTest::testCaseBodyAndTags));
	}

	private static void testCaseBodyAndTags(String file) throws ScriptException {
		String raw = TestResource.getTestResource(file);
		StringBuilder result = new StringBuilder(raw.length());
		freshmarkParser.bodyAndTags(raw, (startIdx, body) -> {
			result.append(body);
		}, (startIdx, tag) -> {
			result.append("<!---freshmark");
			result.append(tag);
			result.append("-->");
		});
	}

	@Test
	void testCompileNoTags() throws ScriptException {
		// no change reguired == no problem!
		testCaseCompileSuccess("empty.txt", TestResource.getTestResource("empty.txt"));
		testCaseCompileSuccess("nocomment.txt", TestResource.getTestResource("nocomment.txt"));
	}

	@Test
	void testCompileWiring() throws ScriptException {
		testCaseCompileSuccess("simple.txt", TestResource.getTestResource("simple_compiled.txt"));
	}

	private static void testCaseCompileSuccess(String file, String expected) throws ScriptException {
		String raw = TestResource.getTestResource(file);
		String result = freshmarkParser.compile(raw, (section, program, in)
				-> "section: " + section + "\nprogram: " + program + "input: " + in);
		assertEquals(expected, result);
	}

	@Test
	void testCompileUnclosed() {
		testCaseCompileError("unclosed.txt", "Ended without a close tag for 'simple'");
		testCaseCompileError("unclosedthenstuff.txt", "Ended without a close tag for 'simple'");
	}

	@Test
	void testCompileMismatched() {
		testCaseCompileError("mismatched.txt", "Error on line 7: Expecting '/simple'");
	}

	@Test
	void testCompileNoProgram() {
		testCaseCompileError("noprogram.txt", "Error on line 3: Section doesn't contain a script.");
	}

	private static void testCaseCompileError(String file, String expected) {
		String raw = TestResource.getTestResource(file);
		try {
			freshmarkParser.compile(raw, (section, program, in) -> in);
			fail("Expected an error");
		} catch (Throwable e) {
			assertEquals(expected, e.getMessage());
		}
	}
}
