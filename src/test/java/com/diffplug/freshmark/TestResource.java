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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.diffplug.common.base.Errors;
import org.junit.jupiter.api.Test;

class TestResource {
	/** Returns the given test resource (with unix newlines). */
	static String getTestResource(String filename) {
		return Errors.rethrow().get(() -> {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream inputStream = TestResource.class.getResourceAsStream("/" + filename)) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) != -1) {
					baos.write(buffer, 0, length);
				}
			}
			// return the string with unix line-endings
			return new String(baos.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");
		});
	}

	@Test
	void testResources() {
		assertEquals("", getTestResource("empty.txt"));
		assertEquals("Some stuff\nNothing special", getTestResource("nocomment.txt"));
	}
}
