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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class CommentScriptTest {
	@Test
	public void testMustacheTemplate() {
		String before = TestResource.getTestResource("template_before.txt");
		String after = TestResource.getTestResource("template_after.txt");
		String afterActual = CommentScript.mustacheTemplate(before, key -> key.toUpperCase(Locale.US));
		Assert.assertEquals(after, afterActual);
	}
}
