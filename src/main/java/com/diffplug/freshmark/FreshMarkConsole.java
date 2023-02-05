/*
 * Copyright (C) 2015-2023 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.freshmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;

public class FreshMarkConsole {
	/** Prints the usage for the given command. */
	public static void printUsage(String name, CmdLineParser parser, PrintStream stream) {
		stream.println();
		stream.print("usage: ");
		stream.print(name);
		parser.printSingleLineUsage(stream);
		stream.println();
		parser.printUsage(stream);
	}

	/** The line endings written by the tool. */
	public enum LineEnding {
		PLATFORM_NATIVE(System.getProperty("line.separator")), WINDOWS("\r\n"), UNIX("\n");

		public final String string;

		private LineEnding(String ending) {
			this.string = ending;
		}

		public boolean isWin() {
			return string.equals("\r\n");
		}
	}

	@Option(name = "-endings", usage = "determines the line endings to use in the output")
	private LineEnding lineEnding = LineEnding.PLATFORM_NATIVE;

	@Option(name = "-P", handler = MapOptionHandler.class, usage = "sets the properties which are available in the script, -P KEY_1=VALUE_1 -P KEY_2=VALUE_2")
	private Map<String, String> properties = new HashMap<>();

	@Option(name = "-properties", usage = "loads properties from the given file")
	private File propFile;

	@Option(name = "-file", required = true, usage = "applies freshmark to the given file (multiple are allowed)")
	private List<File> files = new ArrayList<File>();

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	public FreshMarkConsole(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments and get the config
			parser.parseArgument(args);
			// make sure some files were specified
			if (files.isEmpty()) {
				throw new IOException("No files were specified.");
			}
			// if a propFile was specified, load it and its contents to the properties map
			if (propFile != null) {
				Properties propFileContent = new Properties();
				try (FileInputStream stream = new FileInputStream(propFile)) {
					propFileContent.load(stream);
				}
				for (String key : propFileContent.stringPropertyNames()) {
					properties.put(key, propFileContent.getProperty(key));
				}
			}
			// load these properties into a FreshMark
			FreshMark freshMark = new FreshMark(properties, System.err::println);
			for (File file : files) {
				// read the file
				String raw = new String(Files.readAllBytes(file.toPath()), CHARSET)
						// ensure that it's all unix endings
						.replace(LineEnding.WINDOWS.string, LineEnding.UNIX.string);
				// compile the file
				String compiled = freshMark.compile(raw)
						// ensure that it has the requested line endings
						.replace(LineEnding.UNIX.string, lineEnding.string);
				// write out the compiled result
				Files.write(file.toPath(), compiled.getBytes(CHARSET), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			printUsage("freshmark", parser, System.err);
		}
	}

	public static void main(String[] args) {
		new FreshMarkConsole(args);
	}
}
