package com.bobo.cloudalibaba.黑白名单控制;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;

import java.util.Collections;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-29 21:46
 **/
public class 黑白名单控制 {

    private static final String RESOURCE_NAME = "testABC";

    public static void main(String[] args) {
        initRules();

        testFor(RESOURCE_NAME, "appA");
        testFor(RESOURCE_NAME, "appB");
        testFor(RESOURCE_NAME, "appC");
        testFor(RESOURCE_NAME, "appE");
    }

    private static void testFor(/*@NonNull*/ String resource, /*@NonNull*/ String origin) {
        ContextUtil.enter(resource, origin);
        Entry entry = null;
        try {
            entry = SphU.entry(resource);
            System.out.println(String.format("Passed for resource %s, origin is %s", resource, origin));
        } catch (BlockException ex) {
            System.err.println(String.format("Blocked for resource %s, origin is %s", resource, origin));
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }
    public static void initRules() {

        AuthorityRule authorityRule  = new AuthorityRule();

        authorityRule.setResource(RESOURCE_NAME);

        authorityRule.setStrategy(RuleConstant.AUTHORITY_WHITE);

        authorityRule.setLimitApp("appA,appB");

        AuthorityRuleManager.loadRules(Collections.singletonList(authorityRule));
    }
}
