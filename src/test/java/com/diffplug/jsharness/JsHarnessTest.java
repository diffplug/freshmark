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
package com.diffplug.jsharness;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;

public class JsHarnessTest {
	@Test
	public void testBasicExpressions() throws ScriptException {
		ScriptEngine engine = JsHarness.create().build();
		Assert.assertEquals("abc", engine.eval("'abc'"));
		Assert.assertEquals(123, engine.eval("123"));
		Assert.assertEquals(123.5, engine.eval("123.5"));
	}

	@Test
	public void testBasicScript() throws ScriptException {
		ScriptEngine engine = JsHarness.create()
				.addScript("var txt = 'abc';")
				.addScript("var int = 123;")
				.addScript("var float = 123.5;")
				.build();
		Assert.assertEquals("abc", engine.eval("txt"));
		Assert.assertEquals(123, engine.eval("int"));
		Assert.assertEquals(123.5, engine.eval("float"));
	}

	@Test
	public void testVoid0() throws ScriptException {
		AtomicBoolean wasRun = new AtomicBoolean(false);
		ScriptEngine engine = JsHarness.create()
				.setName("void0").toVoid0(() -> wasRun.set(true))
				.build();
		// execute a script that m
		engine.eval("void0()");
		Assert.assertEquals(true, wasRun.get());
	}

	@Test
	public void testVoid1() throws ScriptException {
		AtomicReference<String> arg1 = new AtomicReference<>();
		ScriptEngine engine = JsHarness.create()
				.setName("void1").toVoid1(arg1::set)
				.build();
		// execute a script that m
		engine.eval("void1('it lives!')");
		Assert.assertEquals("it lives!", arg1.get());
	}

	@Test
	public void testVoid2() throws ScriptException {
		AtomicReference<Object> arg1 = new AtomicReference<>();
		AtomicReference<Object> arg2 = new AtomicReference<>();
		ScriptEngine engine = JsHarness.create()
				.setName("void2").toVoid2((a, b) -> {
					arg1.set(a);
					arg2.set(b);
				})
				.build();
		// execute a script that m
		engine.eval("void2('a', 'b')");
		Assert.assertEquals("a", arg1.get());
		Assert.assertEquals("b", arg2.get());
	}

	@Test
	public void testVoid3() throws ScriptException {
		AtomicReference<Object> arg1 = new AtomicReference<>();
		AtomicReference<Object> arg2 = new AtomicReference<>();
		AtomicReference<Object> arg3 = new AtomicReference<>();
		ScriptEngine engine = JsHarness.create()
				.setName("void3").toVoid3((a, b, c) -> {
					arg1.set(a);
					arg2.set(b);
					arg3.set(c);
				})
				.build();
		// execute a script that m
		engine.eval("void3('a', 'b', 'c')");
		Assert.assertEquals("a", arg1.get());
		Assert.assertEquals("b", arg2.get());
		Assert.assertEquals("c", arg3.get());
	}

	@Test
	public void testVoid4() throws ScriptException {
		AtomicReference<Object> arg1 = new AtomicReference<>();
		AtomicReference<Object> arg2 = new AtomicReference<>();
		AtomicReference<Object> arg3 = new AtomicReference<>();
		AtomicReference<Object> arg4 = new AtomicReference<>();
		ScriptEngine engine = JsHarness.create()
				.setName("void4").toVoid4((a, b, c, d) -> {
					arg1.set(a);
					arg2.set(b);
					arg3.set(c);
					arg4.set(d);
				})
				.build();
		// execute a script that m
		engine.eval("void4('a', 'b', 'c', 'd')");
		Assert.assertEquals("a", arg1.get());
		Assert.assertEquals("b", arg2.get());
		Assert.assertEquals("c", arg3.get());
		Assert.assertEquals("d", arg4.get());
	}

	@Test
	public void testVoidN() throws ScriptException {
		AtomicReference<Object[]> argArray = new AtomicReference<>();
		ScriptEngine engine = JsHarness.create()
				.setName("voidN").toVoidN((args) -> {
					argArray.set(args);
				})
				.build();
		// execute a script that m
		engine.eval("voidN('a', 'b', 'c', 'd', 'e', 'f', 'g')");
		Assert.assertArrayEquals(new Object[]{
				"a", "b", "c", "d", "e", "f", "g"
		}, argArray.get());
	}
}
