package com.diffplug.freshmark;

import org.junit.Assert;
import org.junit.Test;

import com.diffplug.common.base.StringPrinter;

public class ParserTest {
	@Test
	public void testBodyAndTags() {
		TestResource.ALL.forEach(ParserTest::testCaseBodyAndTags);
	}

	static void testCaseBodyAndTags(String file) {
		String raw = TestResource.getTestResource(file);
		StringBuilder result = new StringBuilder(raw.length());
		FreshMark.parser.bodyAndTags(raw, body -> {
			result.append(body);
		}, tag -> {
			result.append("<!---freshmark");
			result.append(tag);
			result.append("-->");
		});
	}

	@Test
	public void testCompileNoTags() {
		// no change reguired == no problem!
		testCaseCompileSuccess("empty.md", TestResource.getTestResource("empty.md"));
		testCaseCompileSuccess("nocomment.md", TestResource.getTestResource("nocomment.md"));
	}

	@Test
	public void testCompileWiring() {
		testCaseCompileSuccess("simple.md", StringPrinter.buildStringFromLines(
				"Some stuff",
				"Nothing special",
				"section: simple",
				"program: output = 'BLOOPEY\\n' + input + 'DOOP\\n';",
				"input: ",
				"Does this work?",
				"",
				"Why yes!  Yes it does."
				));
	}

	static void testCaseCompileSuccess(String file, String expected) {
		String raw = TestResource.getTestResource(file);
		String result = FreshMark.parser.compile(raw, (section, program, in) -> {
			return "section: " + section + "\nprogram: " + program + "input: " + in;
		});
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testCompileUnclosed() {
		testCaseCompileError("unclosed.md", "Ended without a close tag for 'simple'");
		testCaseCompileError("unclosedthenstuff.md", "Ended without a close tag for 'simple'");
	}

	@Test
	public void testCompileMismatched() {
		testCaseCompileError("mismatched.md", "Error on line 7: Expecting '/simple'");
	}
	@Test
	public void testCompileNoProgram() {
		testCaseCompileError("noprogram.md", "Error on line 3: Section doesn't contain a program.");
	}

	static void testCaseCompileError(String file, String expected) {
		String raw = TestResource.getTestResource(file);
		try {
			FreshMark.parser.compile(raw, (section, program, in) -> in);
			Assert.fail("Expected an error");
		} catch (Throwable e) {
			Assert.assertEquals(expected, e.getMessage());
		}
	}
}
