package com.yomahub.liteflow.test.absoluteConfigPath;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import com.yomahub.liteflow.property.LiteflowConfigGetter;
import com.yomahub.liteflow.test.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * spring环境下，规则配置文件通过绝对路径获取
 *
 * @author zendwang
 * @since 2.8.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:/absoluteConfigPath/application.xml")
public class AbsoluteConfigPathELSpringTest extends BaseTest {

	@Resource
	private FlowExecutor flowExecutor;

	@Test
	public void testAbsoluteConfig() throws Exception {
		Assertions.assertTrue(() -> {
			LiteflowConfig config = LiteflowConfigGetter.get();
			config.setRuleSource("C:/LiteFlow/Test/a/b/c/flow.el.xml");
			flowExecutor.reloadRule();
			return flowExecutor.execute2Resp("chain1", "arg").isSuccess();
		});
	}

	@Test
	public void testAbsolutePathMatch() throws Exception {
		Assertions.assertTrue(() -> {
			LiteflowConfig config = LiteflowConfigGetter.get();
			config.setRuleSource("C:/LiteFlow/Tes*/**/c/*.el.xml");
			flowExecutor.reloadRule();
			return flowExecutor.execute2Resp("chain1", "arg").isSuccess();
		});
	}

	@BeforeAll
	public static void createFiles() {
		String filePath = "C:/LiteFlow/Test/a/b/c";
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><flow><chain name=\"chain1\">WHEN(a, b, c);</chain></flow>";
		FileUtil.mkdir(filePath);
		FileUtil.writeString(content, filePath + "/flow.el.xml", CharsetUtil.CHARSET_UTF_8);
	}

	@AfterAll
	public static void removeFiles() {
		FileUtil.del("C:/LiteFlow");
	}

}
