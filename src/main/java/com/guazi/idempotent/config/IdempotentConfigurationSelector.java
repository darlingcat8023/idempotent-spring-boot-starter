package com.guazi.idempotent.config;

import com.guazi.idempotent.ProxyIdempotentConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Idempotent自动配置
 * @author liujiajun
 * @date 2020/5/25
 */
public class IdempotentConfigurationSelector extends AdviceModeImportSelector<EnableIdempotent> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch(adviceMode) {
            case PROXY:
                return this.getProxyImports();
            case ASPECTJ:
                return this.getAspectjImports();
            default:
                return null;
        }
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return super.getExclusionFilter();
    }

    /**
     * Proxy动态代理模式
     * @return
     */
    private String[] getProxyImports() {
        List<String> result = new ArrayList<>(2);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyIdempotentConfiguration.class.getName());
        return StringUtils.toStringArray(result);
    }

    /**
     * AspectJ静态代理模式
     * @return
     */
    private String[] getAspectjImports() {
        List<String> result = new ArrayList<>(2);
        // 使用字符串，避免在没有AspectJ的环境中无法编译aspectj文件
        result.add("com.guazi.idempotent.aspect.AspectjIdempotentConfiguration");
        return StringUtils.toStringArray(result);
    }

}
