package com.diguage.truman;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MockBean 测试
 *
 * @author D瓜哥, https://www.diguage.com/
 * @since 2021-08-04 08:50:57
 */
@Slf4j
@SpringBootTest(classes = TrumanApplication.class)
@ExtendWith(SpringExtension.class)
public class MockBeanTest {

	@MockBean
	private Runnable run;

	@Test
	public void test() {
		assertThat(this.run).isNotNull();
		assertThat(this.run).isInstanceOf(Runnable.class);
		// org.mockito.codegen.Runnable$MockitoMock$XXXXX
		// 从这里可以看出，MockBean 是由 Mockito 创建的 Mock。
		// 底层是 Byte Buddy 利用字节码编辑技术动态生成的类。
		log.info("class={}", this.run.getClass().getName());
	}

}
