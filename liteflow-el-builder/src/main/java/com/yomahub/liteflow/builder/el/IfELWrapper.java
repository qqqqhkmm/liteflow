package com.yomahub.liteflow.builder.el;

import cn.hutool.core.util.ObjectUtil;
import com.yomahub.liteflow.util.JsonUtil;

import java.util.Map;

/**
 * 条件表达式
 * 按照调用方法IF(a,b,c) IF(a,b).else(c) IF(a,b).ELIF(c,d)对应输出
 * 支持设置 id tag data maxWaitSeconds 属性
 * 支持二元或三元函数初始化
 * 支持调用 else elif 方法
 *
 * @author gezuao
 * @since 2.11.1
 */
public class IfELWrapper extends ELWrapper{

    /**
     * 定义当前条件组件的输出格式
     */
    private static final int IF_FORMAT = 1;
    private static final int IF_ELSE_FORMAT = 2;
    private static final int ELIF_FORMAT = 3;

    private int format;

    /**
     * 单节点作为判断条件
     *
     * @param ifWrapper    如果包装器
     * @param trueWrapper  真正包装器
     * @param falseWrapper 假包装器
     */
    public IfELWrapper(NodeELWrapper ifWrapper, ELWrapper trueWrapper, ELWrapper falseWrapper) {
        this.setIfWrapper(ifWrapper);
        this.setTrueWrapper(trueWrapper);
        this.setFalseWrapper(falseWrapper);
        this.format = IF_FORMAT;
    }

    /**
     * 如果缠绕器
     *
     * @param ifWrapper   如果包装器
     * @param trueWrapper 真正包装器
     */
    public IfELWrapper(NodeELWrapper ifWrapper, ELWrapper trueWrapper) {
        this.setIfWrapper(ifWrapper);
        this.setTrueWrapper(trueWrapper);
        this.format = IF_ELSE_FORMAT;
    }

    /**
     * 与节点作为判断条件
     *
     * @param andELWrapper 和缠绕器
     * @param trueWrapper
     * @param falseWrapper
     */
    public IfELWrapper(AndELWrapper andELWrapper, ELWrapper trueWrapper, ELWrapper falseWrapper) {
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.setFalseWrapper(falseWrapper);
        this.format = IF_FORMAT;
    }

    public IfELWrapper(AndELWrapper andELWrapper, ELWrapper trueWrapper){
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.format = IF_ELSE_FORMAT;
    }

    /**
     * 如果缠绕器
     * 或节点作为判断条件
     *
     * @param andELWrapper 和缠绕器
     * @param trueWrapper  真正包装器
     * @param falseWrapper 假包装器
     */
    public IfELWrapper(OrELWrapper andELWrapper, ELWrapper trueWrapper, ELWrapper falseWrapper) {
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.setFalseWrapper(falseWrapper);
        this.format = IF_FORMAT;
    }

    public IfELWrapper(OrELWrapper andELWrapper, ELWrapper trueWrapper){
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.format = IF_ELSE_FORMAT;
    }

    /**
     * 非节点作为判断条件
     * @param andELWrapper
     * @param trueWrapper
     * @param falseWrapper
     */
    public IfELWrapper(NotELWrapper andELWrapper, ELWrapper trueWrapper, ELWrapper falseWrapper) {
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.setFalseWrapper(falseWrapper);
        this.format = IF_FORMAT;
    }

    public IfELWrapper(NotELWrapper andELWrapper, ELWrapper trueWrapper){
        this.setIfWrapper(andELWrapper);
        this.setTrueWrapper(trueWrapper);
        this.format = IF_ELSE_FORMAT;
    }

    /**
     * else语句
     * 设置最深层次条件表达式的 false分支的 表达式
     *
     * @param falseObject false分支
     * @return {@link IfELWrapper}
     */
    public IfELWrapper elseOpt(Object falseObject){
        ELWrapper falseWrapper = ELBus.convertToNonLogicOpt(falseObject);
        // 找到最深层的if组件
        ELWrapper prev = this;
        ELWrapper succ = this;
        // 设置最深层if组件false组件
        while(prev instanceof IfELWrapper){
            succ = prev;
            prev = prev.getElWrapperList().size() >= 3 ? prev.getElWrapperList().get(2) : null;
        }
        ((IfELWrapper) succ).setFalseWrapper(falseWrapper);
        return this;
    }

    /**
     * elif语句
     * 设置最深层次条件表达式的 判断表达式 和 true分支的 表达式
     *
     * @param ifObject   判断组件
     * @param trueObject true分支
     * @return {@link IfELWrapper}
     */
    public IfELWrapper elIfOpt(Object ifObject, Object trueObject) {
        // 包装判断表达式和true分支组件
        ELWrapper ifWrapper = ELBus.convertToLogicOpt(ifObject);
        ELWrapper trueWrapper = ELBus.convertToNonLogicOpt(trueObject);
        IfELWrapper elIfWrapper;
        if(ifWrapper instanceof NodeELWrapper){
            elIfWrapper = new IfELWrapper((NodeELWrapper) ifWrapper, trueWrapper);
        } else if (ifWrapper instanceof AndELWrapper){
            elIfWrapper = new IfELWrapper((AndELWrapper) ifWrapper, trueWrapper);
        } else if (ifWrapper instanceof OrELWrapper){
            elIfWrapper = new IfELWrapper((OrELWrapper) ifWrapper, trueWrapper);
        } else if (ifWrapper instanceof NotELWrapper){
            elIfWrapper = new IfELWrapper((NotELWrapper) ifWrapper, trueWrapper);
        } else {
            throw new RuntimeException("param error!");
        }
        elIfWrapper.setFormat(ELIF_FORMAT);
        // 找到最深层的if组件
        ELWrapper prev = this;
        ELWrapper succ = this;
        // 设置最深层的false分支为新建的条件表达式
        while(prev instanceof IfELWrapper){
            succ = prev;
            prev = prev.getElWrapperList().size() >= 3 ? prev.getElWrapperList().get(2) : null;
        }
        ((IfELWrapper) succ).elseOpt(elIfWrapper);
        return this;
    }

    private void setIfWrapper(ELWrapper ifWrapper){
        this.addWrapper(ifWrapper, 0);
    }

    private ELWrapper getIfWrapper(){
        return this.getElWrapperList().get(0);
    }

    private void setTrueWrapper(ELWrapper trueWrapper){
        this.addWrapper(trueWrapper, 1);
    }

    private ELWrapper getTrueWrapper(){
        return this.getElWrapperList().get(1);
    }

    private void setFalseWrapper(ELWrapper falseWrapper){
        this.addWrapper(falseWrapper, 2);
    }

    protected void setFormat(int formatCode){
        this.format = formatCode;
    }

    protected int getFormat(){
        return this.format;
    }

    private ELWrapper getFalseWrapper(){
        try{
            return this.getElWrapperList().get(2);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public IfELWrapper tag(String tag) {
        this.setTag(tag);
        return this;
    }

    @Override
    public IfELWrapper id(String id) {
        this.setId(id);
        return this;
    }

    @Override
    public IfELWrapper data(String dataName, Object object) {
        setData(JsonUtil.toJsonString(object));
        setDataName(dataName);
        return this;
    }

    @Override
    public IfELWrapper data(String dataName, String jsonString) {
        try {
            JsonUtil.parseObject(jsonString);
        } catch (Exception e){
            throw new RuntimeException("字符串不符合Json格式！");
        }
        setData(jsonString);
        setDataName(dataName);
        return this;
    }

    @Override
    public IfELWrapper data(String dataName, Map<String, Object> jsonMap) {
        setData(JsonUtil.toJsonString(jsonMap));
        setDataName(dataName);
        return this;
    }

    @Override
    public IfELWrapper maxWaitSeconds(Integer maxWaitSeconds){
        setMaxWaitSeconds(maxWaitSeconds);
        return this;
    }

    @Override
    protected String toEL(Integer depth, StringBuilder paramContext) {
        Integer sonDepth = depth == null ? null : depth + 1;
        StringBuilder sb = new StringBuilder();

        // 根据format不同分别按不同的格式输出
        processWrapperTabs(sb, depth);
        switch (this.format){
            // IF(a,b,c) 三元表达式输出格式
            case IF_FORMAT:
                sb.append("IF(");
                processWrapperNewLine(sb, depth);
                sb.append(this.getIfWrapper().toEL(sonDepth, paramContext)).append(",");
                processWrapperNewLine(sb, depth);
                sb.append(this.getTrueWrapper().toEL(sonDepth, paramContext)).append(",");
                processWrapperNewLine(sb, depth);
                sb.append(this.getFalseWrapper().toEL(sonDepth, paramContext));
                processWrapperNewLine(sb, depth);
                processWrapperTabs(sb, depth);
                sb.append(")");
                break;
            // IF(a,b).ELSE(c) 二元表达式输出格式
            case IF_ELSE_FORMAT:
                sb.append("IF(");
                processWrapperNewLine(sb, depth);
                sb.append(this.getIfWrapper().toEL(sonDepth, paramContext)).append(",");
                processWrapperNewLine(sb, depth);
                sb.append(this.getTrueWrapper().toEL(sonDepth, paramContext));
                processWrapperNewLine(sb, depth);
                processWrapperTabs(sb, depth);
                sb.append(")");
                processElseOutPut(depth, sonDepth, sb, paramContext);
                break;
            // IF(a,b).ELIF(c) ELIF输出格式
            case ELIF_FORMAT:
                // elif 树形结构输出
                sb.append(".ELIF(");
                processWrapperNewLine(sb, depth);
                sb.append(this.getIfWrapper().toEL(sonDepth, paramContext)).append(",");
                processWrapperNewLine(sb, depth);
                sb.append(this.getTrueWrapper().toEL(sonDepth, paramContext));
                processWrapperNewLine(sb, depth);
                processWrapperTabs(sb, depth);
                sb.append(")");
                processElseOutPut(depth, sonDepth, sb, paramContext);
                break;
            default:
                break;
        }

        // 设置共有属性
        processWrapperProperty(sb, paramContext);
        return sb.toString();
    }

    /**
     * if(a,b) 和 elif(a,b) 的 else 处理方法相同
     * 抽象出处理ELSE输出方法
     *
     * @param depth        深度
     * @param sonDepth     儿子深度
     * @param elContext    EL 上下文
     * @param paramContext 参数上下文
     */
    private void processElseOutPut(Integer depth, Integer sonDepth, StringBuilder elContext, StringBuilder paramContext){
        if (ObjectUtil.isNotNull(this.getFalseWrapper())){
            // 如果 使用ELIF 定义子表达式，则放到子表达式中处理
            if(this.getFalseWrapper() instanceof IfELWrapper && ((IfELWrapper) this.getFalseWrapper()).getFormat() == ELIF_FORMAT){
                elContext.append(this.getFalseWrapper().toEL(depth, paramContext));
            } else {
                // 如果使用 ELSE 处理 false分支表达式，则本层处理表达式输出
                elContext.append(".ELSE(");
                processWrapperNewLine(elContext, depth);
                elContext.append(this.getFalseWrapper().toEL(sonDepth, paramContext));
                processWrapperNewLine(elContext, depth);
                processWrapperTabs(elContext, depth);
                elContext.append(")");
            }
        }
    }
}