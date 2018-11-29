package com.bobo.cloudalibaba.热点参数限流;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.bobo.cloudalibaba.熔断降级.ParamFlowQpsRunner;

import java.util.Collections;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-28 22:48
 **/
public class 热点参数限流Demo {

    private static final int PARAM_A = 1;
    private static final int PARAM_B = 2;
    private static final int PARAM_C = 3;
    private static final int PARAM_D = 4;
    private static final String RESOURCE_KEY = "resA";

    private static final Integer[] PARAMS = new Integer[] {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

    public static void main(String[] args) {
        initHotParamFlowRules();

        final int threadCount = 8;
        ParamFlowQpsRunner<Integer> runner = new ParamFlowQpsRunner<>(PARAMS, RESOURCE_KEY, threadCount, 120);
        runner.simulateTraffic();
        runner.tick();
    }

    private static void initHotParamFlowRules() {
        // 设置热点参数规则，首先设置了resA的资源，然后设置热点参数的索引，
        // 设置限流模式是QPS模式，设置限流阈值是5
        ParamFlowRule rule = new ParamFlowRule(RESOURCE_KEY).setParamIdx(0).
                setBlockGrade(RuleConstant.FLOW_GRADE_QPS).setCount(5);
        // 针对int类型的参数PARAM_B,单独设置限流QPS阈值是10,而不是全局的5
        ParamFlowItem item = new ParamFlowItem().setObject(String.valueOf(PARAM_B)).setClassType(int.class.getName()).
                setCount(10);
        rule.setParamFlowItemList(Collections.singletonList(item));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
