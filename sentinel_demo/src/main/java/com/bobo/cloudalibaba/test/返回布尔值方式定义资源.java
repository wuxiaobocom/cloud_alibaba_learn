package com.bobo.cloudalibaba.test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-26 22:28
 **/
public class 返回布尔值方式定义资源 {

    public static void main(String[] args) {
        initFlowRules();
        // 获取资源名
        while (true) {
            if (SphO.entry("HelloWorld")) {
                try {
                    //被保护的业务逻辑
                    System.out.println("HelloWorld");
                }finally {
                    SphO.exit();
                }
            } else {
                System.out.println("Blocked");
            }
        }
    }

    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<FlowRule>();

        // 定义规则
        FlowRule rule = new FlowRule();
        //定义资源
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(20);
        //添加规则
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
