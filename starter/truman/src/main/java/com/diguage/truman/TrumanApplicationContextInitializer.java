/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diguage.truman;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static com.diguage.truman.util.Printer.print;

public class TrumanApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	public TrumanApplicationContextInitializer() {
		print("TrumanApplicationContextInitializer.Constructor()");
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		print("TrumanApplicationContextInitializer.initialize(applicationContext)");
	}
}
