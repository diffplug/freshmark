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
		Assert.assertEquals("", getTestResource("empty.md"));
		Assert.assertEquals("Some stuff\nNothing special", getTestResource("nocomment.md"));
	}

	public static final List<String> ALL = Arrays.asList(
			"empty.md",
			"mismatched.md",
			"nocomment.md",
			"noprogram.md",
			"simple.md",
			"unclosed.md",
			"unclosedthenstuff.md"
			);
}
