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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ParserIntronExonTest {
	static final Parser freshmarkParser = new FreshMark(null, null).parser;

	@Test
	public void testBodyAndTags() {
		Arrays.asList("empty.txt",
				"mismatched.txt",
				"nocomment.txt",
				"noprogram.txt",
				"simple.txt",
				"unclosed.txt",
				"unclosedthenstuff.txt")
				.forEach(ParserIntronExonTest::testCaseBodyAndTags);
	}

	static void testCaseBodyAndTags(String file) {
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
	public void testCompileNoTags() {
		// no change reguired == no problem!
		testCaseCompileSuccess("empty.txt", TestResource.getTestResource("empty.txt"));
		testCaseCompileSuccess("nocomment.txt", TestResource.getTestResource("nocomment.txt"));
	}

	@Test
	public void testCompileWiring() {
		testCaseCompileSuccess("simple.txt", TestResource.getTestResource("simple_compiled.txt"));
	}

	static void testCaseCompileSuccess(String file, String expected) {
		String raw = TestResource.getTestResource(file);
		String result = freshmarkParser.compile(raw, (section, program, in) -> {
			return "section: " + section + "\nprogram: " + program + "input: " + in;
		});
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testCompileUnclosed() {
		testCaseCompileError("unclosed.txt", "Ended without a close tag for 'simple'");
		testCaseCompileError("unclosedthenstuff.txt", "Ended without a close tag for 'simple'");
	}

	@Test
	public void testCompileMismatched() {
		testCaseCompileError("mismatched.txt", "Error on line 7: Expecting '/simple'");
	}

	@Test
	public void testCompileNoProgram() {
		testCaseCompileError("noprogram.txt", "Error on line 3: Section doesn't contain a script.");
	}

	static void testCaseCompileError(String file, String expected) {
		String raw = TestResource.getTestResource(file);
		try {
			freshmarkParser.compile(raw, (section, program, in) -> in);
			Assert.fail("Expected an error");
		} catch (Throwable e) {
			Assert.assertEquals(expected, e.getMessage());
		}
	}
}
