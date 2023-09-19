package com.yomahub.liteflow.test.abstractChain.cmp;

import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

@Component("e")
public class ECmp extends NodeComponent {

	@Override
	public void process() {
		System.out.println("ECmp executed!");
	}

}
