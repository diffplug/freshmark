package com.diffplug.freshmark;

/** Generates shields using <a href="http://shields.io/">shields.io</a>. */
public class Shields {
	public static String url(String subject, String status, String color) {
		return "https://img.shields.io/badge/" + escape(subject) + "-" + escape(status) + "-" + escape(color) + ".svg";
	}

	private static String escape(String raw) {
		return raw.replace("_", "__").replace("-", "--").replace(" ", "_");
	}
}
