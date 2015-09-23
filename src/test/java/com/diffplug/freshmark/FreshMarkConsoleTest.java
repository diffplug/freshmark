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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.diffplug.common.base.Errors;
import com.diffplug.common.base.StringPrinter;
import com.diffplug.freshmark.FreshMarkConsole.LineEnding;

public class FreshMarkConsoleTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/** Returns a File (in a temporary folder) which has the given contents. */
	protected File createTestFile(String filename, String content) throws IOException {
		File file = folder.newFile(filename);
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	/** Standard test case. */
	private void testCase(File toRead, String filenameAfter, String args, String consoleOutput) throws IOException {
		String consoleOutputActual = StringPrinter.buildString(printer -> {
			PrintStream out = System.out;
			PrintStream err = System.err;
			try {
				// capture sysOut and sysErr
				System.setOut(printer.toPrintStream());
				System.setErr(printer.toPrintStream());
				// now run the test, capturing the output as we go
				FreshMarkConsole.main(args.split(" "));
				// check the result
				String result = new String(Files.readAllBytes(toRead.toPath()), StandardCharsets.UTF_8)
						.replace(LineEnding.WINDOWS.string, LineEnding.UNIX.string);
				Assert.assertEquals(TestResource.getTestResource(filenameAfter), result);
			} catch (Exception e) {
				throw Errors.asRuntime(e);
			} finally {
				System.setOut(out);
				System.setErr(err);
			}
		});
		consoleOutputActual = consoleOutputActual.replace(LineEnding.WINDOWS.string, LineEnding.UNIX.string);
		Assert.assertEquals(consoleOutput, consoleOutputActual);
	}

	@Test
	public void testNoFile() throws IOException {
		File testFile = createTestFile("test.md", TestResource.getTestResource("full_before.txt"));
		testCase(testFile, "full_before.txt", "-P prop=key",
				"Option \"-file\" is required\n" +
						"\n" +
						"usage: freshmark [-P] [-endings [PLATFORM_NATIVE | WINDOWS | UNIX]] -file FILE [-properties FILE]\n" +
						" -P                                     : sets the properties which are\n" +
						"                                          available in the script, -P\n" +
						"                                          KEY_1=VALUE_1 -P KEY_2=VALUE_2\n" +
						"                                          (default: {prop=key})\n" +
						" -endings [PLATFORM_NATIVE | WINDOWS |  : determines the line endings to use in\n" +
						" UNIX]                                    the output (default: PLATFORM_NATIVE)\n" +
						" -file FILE                             : applies freshmark to the given file\n" +
						"                                          (multiple are allowed)\n" +
						" -properties FILE                       : loads properties from the given file\n" +
						"");
	}

	@Test
	public void testNoProps() throws IOException {
		File testFile = createTestFile("test.md", TestResource.getTestResource("full_before.txt"));
		testCase(testFile, "full_before.txt", "-file " + testFile.getAbsolutePath(),
				"Unknown key 'group'\n" +
						"Unknown key 'name'\n" +
						"Unknown key 'org'\n" +
						"Unknown key 'name'\n" +
						"Unknown key 'stable'\n" +
						"Unknown key 'org'\n" +
						"Unknown key 'name'\n" +
						"Unknown key 'org'\n" +
						"Unknown key 'name'\n" +
						"Unknown key 'stable'\n" +
						"Unknown key 'version'\n" +
						"Unknown key 'org'\n" +
						"Unknown key 'name'\n" +
						"ReferenceError: \"stable\" is not defined in <eval> at line number 24\n" +
						"\n" +
						"usage: freshmark [-P] [-endings [PLATFORM_NATIVE | WINDOWS | UNIX]] -file FILE [-properties FILE]\n" +
						" -P                                     : sets the properties which are\n" +
						"                                          available in the script, -P\n" +
						"                                          KEY_1=VALUE_1 -P KEY_2=VALUE_2\n" +
						"                                          (default: {})\n" +
						" -endings [PLATFORM_NATIVE | WINDOWS |  : determines the line endings to use in\n" +
						" UNIX]                                    the output (default: PLATFORM_NATIVE)\n" +
						" -file FILE                             : applies freshmark to the given file\n" +
						"                                          (multiple are allowed)\n" +
						" -properties FILE                       : loads properties from the given file\n" +
						"");
	}

	@Test
	public void testPropsOnCommandLine() throws IOException {
		File testFile = createTestFile("test.md", TestResource.getTestResource("full_before.txt"));
		testCase(testFile, "full_after.txt",
				"-P stable=3.2.0 -P version=3.3.0-SNAPSHOT -P group=com.diffplug.durian -P name=durian -P org=diffplug -file " + testFile.getAbsolutePath(),
				"");
	}

	@Test
	public void testPropsFromFile() throws IOException {
		File testFile = createTestFile("test.md", TestResource.getTestResource("full_before.txt"));
		File propFile = createTestFile("props.properties", TestResource.getTestResource("full_props.properties"));
		testCase(testFile, "full_after.txt", "-properties " + propFile + " -file " + testFile.getAbsolutePath(), "");
	}
}
