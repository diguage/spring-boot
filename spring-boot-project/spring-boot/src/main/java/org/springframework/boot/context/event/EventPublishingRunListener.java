/*
 * Copyright 2012-2024 the original author or authors.
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

package org.springframework.boot.context.event;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ErrorHandler;

/**
 * 事件发布监听器
 *
 * {@link SpringApplicationRunListener} to publish {@link SpringApplicationEvent}s.
 * <p>
 * Uses an internal {@link ApplicationEventMulticaster} for the events that are fired
 * before the context is actually refreshed.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Artsiom Yudovin
 * @author Brian Clozel
 * @author Chris Bono
 */
class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {

	private final SpringApplication application;

	private final String[] args;

	private final SimpleApplicationEventMulticaster initialMulticaster;

	EventPublishingRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
		this.initialMulticaster = new SimpleApplicationEventMulticaster();
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void starting(ConfigurableBootstrapContext bootstrapContext) {
		/**
		 * 这个类需要大篇幅介绍
		 * 这个类工作方式有点抽象，有点难以理解，我这里说清楚一点。
		 * 1、首先他会广播一个事件
		 *   对应代码 for (ApplicationListener<?> listener : getApplicationListeners(event, type))
		 *   getApplicationListeners(event, type) 干了两件事情，首先传了两个参数
		 *   这两个参数就是事件类型，意思告诉所有的监听器现在有了一个 type 类型的 event，你们感兴趣不？
		 *
		 * 2、告诉所有的监听器
		 *   getApplicationListeners 告诉所有的监听器（遍历所有的监听器）
		 *   然后监听器会接受到这个事件，继而监听器会判断这个事件自己感兴趣不
		 *   关于监听器如何知道自己感兴趣不？Spring 做的比较复杂，其实看源码就不复杂。
		 *   主要有两个步骤来确定：
		 *
		 *     第一步：两个方法确定：
		 *       1.1、supportEventType(eventType)
		 *       1.2、smartListener.supportsSourceType(sourceType)
		 *       上面两个方法可以见到理解通过传入一个事件类型返回一个boolean
		 *       任意一个返回false表示这个监听器对eventType的时间不感兴趣
		 *       如果感兴趣被add到一个list中，再由后续的代码中依次执行方法调用
		 *
		 *     第二个步：在监听器回调的时候，还是可以进行事件类型判断的
		 *         如果时间类型不感兴趣上面都不执行就可以
		 *
		 * 3、获取所有对这个事件感兴趣的监听器，遍历执行其 onApplicationEvent 方法
		 *     这里的代码传入了一个 ApplicationStartingEvent 的事件过去
		 *     那么在 SpringBoot 当中，定义的 11 个监听器哪些监听器对这个事件感兴趣？
		 *     或者换句话说哪些监听器订阅了这个事件呢？
		 *     先看结果一个是听歌监听器
		 *     为什么是这五个监听器？
		 *     根据上述第二点的第一个步骤我们可以去查看源代码：
		 *     1、org.springframework.boot.context.logging.LoggingApplicationListener
		 *     2、org.springframework.boot.autoconfigure.BackgroundPreinitializer
		 *     3、org.springframework.boot.context.config.DelegatingApplicationListener
		 *     4、?
		 *     5、?
		 *
		 * 4、initialMulticaster 可以看到是 SimpleApplicationEventMulticaster 类型的对象
		 *   主要两个方法，一个是广播事件，一个是执行 listener 的 onApplicationEvent 方法
		 *
		 */
		multicastInitialEvent(new ApplicationStartingEvent(bootstrapContext, this.application, this.args));
	}

	@Override
	public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
			ConfigurableEnvironment environment) {
		multicastInitialEvent(
				new ApplicationEnvironmentPreparedEvent(bootstrapContext, this.application, this.args, environment));
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		multicastInitialEvent(new ApplicationContextInitializedEvent(this.application, this.args, context));
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		for (ApplicationListener<?> listener : this.application.getListeners()) {
			if (listener instanceof ApplicationContextAware contextAware) {
				contextAware.setApplicationContext(context);
			}
			context.addApplicationListener(listener);
		}
		multicastInitialEvent(new ApplicationPreparedEvent(this.application, this.args, context));
	}

	@Override
	public void started(ConfigurableApplicationContext context, Duration timeTaken) {
		context.publishEvent(new ApplicationStartedEvent(this.application, this.args, context, timeTaken));
		AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
	}

	@Override
	public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
		context.publishEvent(new ApplicationReadyEvent(this.application, this.args, context, timeTaken));
		AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		ApplicationFailedEvent event = new ApplicationFailedEvent(this.application, this.args, context, exception);
		if (context != null && context.isActive()) {
			// Listeners have been registered to the application context so we should
			// use it at this point if we can
			context.publishEvent(event);
		}
		else {
			// An inactive context may not have a multicaster so we use our multicaster to
			// call all the context's listeners instead
			if (context instanceof AbstractApplicationContext abstractApplicationContext) {
				for (ApplicationListener<?> listener : abstractApplicationContext.getApplicationListeners()) {
					this.initialMulticaster.addApplicationListener(listener);
				}
			}
			this.initialMulticaster.setErrorHandler(new LoggingErrorHandler());
			this.initialMulticaster.multicastEvent(event);
		}
	}

	private void multicastInitialEvent(ApplicationEvent event) {
		refreshApplicationListeners();
		this.initialMulticaster.multicastEvent(event);
	}

	private void refreshApplicationListeners() {
		this.application.getListeners().forEach(this.initialMulticaster::addApplicationListener);
	}

	private static final class LoggingErrorHandler implements ErrorHandler {

		private static final Log logger = LogFactory.getLog(EventPublishingRunListener.class);

		@Override
		public void handleError(Throwable throwable) {
			logger.warn("Error calling ApplicationEventListener", throwable);
		}

	}

}
