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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.diffplug.common.base.Errors;

public class TestResource {
	public static String getTestResource(String filename) {
		return Errors.rethrow().get(() -> {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream inputStream = TestResource.class.getResourceAsStream(filename);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			// return the string with unix line-endings
			return new String(baos.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");
		});
	}

	@Test
	public void testResources() {
		Assert.assertEquals("", getTestResource("empty.txt"));
		Assert.assertEquals("Some stuff\nNothing special", getTestResource("nocomment.txt"));
	}

	public static final List<String> ALL = Arrays.asList(
			"empty.txt",
			"mismatched.txt",
			"nocomment.txt",
			"noprogram.txt",
			"simple.txt",
			"unclosed.txt",
			"unclosedthenstuff.txt");
}
