package com.thebeastshop.liteflow.springboot;

import com.thebeastshop.liteflow.core.FlowExecutor;
import org.springframework.beans.factory.InitializingBean;
import javax.annotation.Resource;

public class LiteflowExecutorInit implements InitializingBean {

    @Resource
    private FlowExecutor flowExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        flowExecutor.init();
    }
}
