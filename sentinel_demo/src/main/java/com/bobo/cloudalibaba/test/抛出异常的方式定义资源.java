package com.bobo.cloudalibaba.test;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-26 22:28
 **/
public class 抛出异常的方式定义资源 {

    public static void main(String[] args) {
        initFlowRules();
        while (true) {
            Entry entry = null;
            try {
                entry = SphU.entry("HelloWorld");
                System.out.println("hello world");
            } catch (FlowException fe) {
                System.out.println("Flow block");
            }
            catch (BlockException e1) {
                // 资源访问阻止，被限流或者是降级
                // 进行相应的处理操作
                System.out.println("block!");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }
    }

    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<FlowRule>();

        // 定义规则
        FlowRule rule = new FlowRule();
        //定义资源
        rule.setResource("HelloWorld");
        // 通过这个方法进行流量控制
        rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        // 限流阈值类型，这个是QPS
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 这个表示是线程数
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        // Set limit QPS to 20.
        rule.setCount(20);
        //添加规则
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
