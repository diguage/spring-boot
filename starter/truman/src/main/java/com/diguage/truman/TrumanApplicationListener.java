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

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import static com.diguage.truman.util.Printer.print;

public class TrumanApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
	public TrumanApplicationListener() {
		print("TrumanApplicationListener.Constructor()");
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		print("TrumanApplicationListener.onApplicationEvent(event)"
				, "Class: " + event.getClass().getSimpleName());
	}

	@Override
	public boolean supportsAsyncExecution() {
		return ApplicationListener.super.supportsAsyncExecution();
	}
}
