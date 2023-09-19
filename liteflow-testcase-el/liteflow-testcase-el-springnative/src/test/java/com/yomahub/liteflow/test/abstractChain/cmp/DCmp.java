package com.yomahub.liteflow.test.abstractChain.cmp;

import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

@Component("d")
public class DCmp extends NodeComponent {

	@Override
	public void process() {
		System.out.println("DCmp executed!");
	}

}
