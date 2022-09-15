/**
 * <p>Title: liteflow</p>
 * <p>Description: 轻量级的组件式流程框架</p>
 * @author Bryan.Zhang
 * @email weenyc31@163.com
 * @Date 2020/4/1
 */
package com.yomahub.liteflow.test.builder.cmp1;

import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.annotation.LiteflowSwitchCmpDefine;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;

@LiteflowSwitchCmpDefine
public class ECmp{

	@LiteflowMethod(LiteFlowMethodEnum.PROCESS_SWITCH)
	public String processSwitch(NodeComponent bindCmp) throws Exception {
		System.out.println("ECmp executed!");
		return "chain2";
	}

}