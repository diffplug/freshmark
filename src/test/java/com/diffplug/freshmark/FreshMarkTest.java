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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class FreshMarkTest {
	@Test
	public void testPrefixDelimReplacement() {
		String before = TestResource.getTestResource("javadoc_before.txt");
		String after = TestResource.getTestResource("javadoc_after.txt");
		String afterActual = FreshMark.prefixDelimReplace(before, "https://diffplug.github.io/durian/javadoc/", "/", "4.0");
		Assert.assertEquals(after, afterActual);
	}

	@Test
	public void testTemplate() {
		String before = TestResource.getTestResource("template_before.txt");
		String after = TestResource.getTestResource("template_after.txt");
		String afterActual = FreshMark.template(before, key -> key.toUpperCase(Locale.US));
		Assert.assertEquals(after, afterActual);
	}

	@Test
	public void testFull() {
		String before = TestResource.getTestResource("full_before.txt");
		String after = TestResource.getTestResource("full_after.txt");

		Map<String, String> props = new HashMap<>();
		props.put("stable", "3.2.0");
		props.put("version", "3.3.0-SNAPSHOT");
		props.put("group", "com.diffplug.durian");
		props.put("name", "durian");
		props.put("org", "diffplug");
		List<String> warnings = new ArrayList<>();
		FreshMark freshmark = new FreshMark(props, warnings::add);
		String afterActual = freshmark.compile(before);
		Assert.assertEquals(after, afterActual);
		Assert.assertTrue(warnings.isEmpty());
	}
}
