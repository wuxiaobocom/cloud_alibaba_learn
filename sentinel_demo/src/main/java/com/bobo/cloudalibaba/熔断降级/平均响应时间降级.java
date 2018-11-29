package com.bobo.cloudalibaba.熔断降级;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-28 21:42
 **/
public class 平均响应时间降级 {

    public static void main(String[] args) {
        initRule();
//        Entry entry = null;
//        for (int i = 0; i < 100; i++) {
//            Thread entryThread = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    while (true) {
//                        Entry entry = null;
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(5);
//                            entry = SphU.entry("bobo");
//                            System.out.println("ok");
//                            // sleep 600 ms, as rt
//                            TimeUnit.MILLISECONDS.sleep(600);
//                        } catch (Exception e) {
//                            System.out.println("--------------");
//                        } finally {
//                            if (entry != null) {
//                                entry.exit();
//                            }
//                        }
//                    }
//                }
//
//            });
//            entryThread.setName("thread");
//            entryThread.start();
//        }

        while (true) {
            Entry entry = null;
            try {
                TimeUnit.MILLISECONDS.sleep(5);
                entry = SphU.entry("bobo");
                System.out.println("ok");
                // sleep 600 ms, as rt
                TimeUnit.MILLISECONDS.sleep(600);
            } catch (Exception e) {
                System.out.println("--------------");
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }
    }

    public static void initRule() {
        List<DegradeRule> degradeRuleList = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        // 降级规则设置资源
        rule.setResource("bobo");
        // 设置阈值是20ms
        rule.setCount(20);
        // 设置在降级之后接下来的10秒之内都会自动返回
        rule.setTimeWindow(1);
        // 设置降级类型是根据平均响应时间
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        degradeRuleList.add(rule);
        DegradeRuleManager.loadRules(degradeRuleList);
    }
}
