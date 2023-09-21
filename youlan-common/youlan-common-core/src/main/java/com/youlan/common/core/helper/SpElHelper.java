package com.youlan.common.core.helper;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import lombok.Getter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SpElHelper {

    @Getter
    public static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Getter
    public static final TemplateParserContext templateParserContext = new TemplateParserContext();

    /**
     * 创建计算上下文
     *
     * @param method 方法
     * @param args   方法参数
     */
    public static StandardEvaluationContext createEvaluationContext(Method method, Object[] args) {
        String[] names = getParameterNames(method);
        return createEvaluationContext(names, args);
    }

    /**
     * 创建计算上下文
     *
     * @param names 参数名称
     * @param args  参数值
     */
    public static StandardEvaluationContext createEvaluationContext(String[] names, Object[] args) {
        Assert.notEmpty(names, "参数名称不能为空");
        Assert.notEmpty(args, "参数值不能为空");
        Assert.isTrue(names.length == args.length, "参数名称和参数值长度不相等");
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < names.length; i++) {
            evaluationContext.setVariable(names[i], args[i]);
        }
        return evaluationContext;
    }

    /**
     * 将表达式解析为表达式对象
     *
     * @param expressionString 表达式
     * @return 表达式对象
     */
    public static Expression parseExpression(String expressionString) {
        return parseExpression(expressionString, templateParserContext);
    }

    /**
     * 将表达式解析为表达式对象
     *
     * @param expressionString 表达式
     * @return 表达式对象
     */
    public static Expression parseExpression(String expressionString, ParserContext parserContext) {
        Assert.notBlank(expressionString, () -> new BizRuntimeException("表达式不能为空"));
        if (ObjectUtil.isNull(parserContext)) {
            parserContext = templateParserContext;
        }
        SpelExpressionParser expressionParser = new SpelExpressionParser();
        if (expressionString.startsWith(parserContext.getExpressionPrefix()) &&
                expressionString.endsWith(parserContext.getExpressionSuffix())) {
            return expressionParser.parseExpression(expressionString, parserContext);
        }
        return expressionParser.parseExpression(expressionString);
    }

    /**
     * 获取方法参数名称
     */
    public static String[] getParameterNames(Method method) {
        return parameterNameDiscoverer.getParameterNames(method);
    }

    /**
     * 获取构造器参数名称
     */
    public static String[] getParameterNames(Constructor<?> constructor) {
        return parameterNameDiscoverer.getParameterNames(constructor);
    }

    /**
     * 判断是否是表达式
     */
    public static boolean isExpressionString(String expressionString) {
        return StrUtil.isNotBlank(expressionString) && expressionString.startsWith("#");
    }
}
