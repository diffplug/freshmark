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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;
import org.junit.jupiter.api.Test;

class FreshMarkTest {

	@Test
	void testPrefixDelimReplacement() {
		String before = TestResource.getTestResource("javadoc_before.txt");
		String after = TestResource.getTestResource("javadoc_after.txt");
		String afterActual = FreshMark.prefixDelimiterReplace(before, "https://diffplug.github.io/durian/javadoc/", "/", "4.0");
		assertEquals(after, afterActual);
	}

	@Test
	void testFull() throws ScriptException {
		String before = TestResource.getTestResource("full_before.txt");
		String after = TestResource.getTestResource("full_after.txt");

		Map<String, String> props = new HashMap<>();
		props.put("stable", "3.2.0");
		props.put("version", "3.3.0-SNAPSHOT");
		props.put("group", "com.diffplug.durian");
		props.put("name", "durian");
		props.put("org", "diffplug");
		List<String> warnings = new ArrayList<>();
		CommentScript freshmark = new FreshMark(props, warnings::add);
		String afterActual = freshmark.compile(before);
		assertEquals(after, afterActual);
		assertTrue(warnings.isEmpty());
	}
}
