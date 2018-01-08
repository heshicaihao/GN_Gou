// Gionee <yuwei><2014-8-14> add for CR00821559 begin
/*
 * GNInjectView.java
 * classes : com.gionee.client.business.inject.GNInjectView
 * @author yuwei
 * V 1.0.0
 * Create at 2014-8-14 上午10:51:55
 */
package com.gionee.client.business.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.gionee.client.business.inject.GNInjectView
 * 
 * @author yuwei <br/>
 * @date create at 2014-8-14 上午10:51:55
 * @description TODO
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GNInjectView {
    /** View的ID */
    public int id() default -1;

    /** View的单击事件 */
    public String click() default "";

    /** View的长按键事件 */
    public String longClick() default "";

    /** View的焦点改变事件 */
    public String focuschange() default "";

    /** View的手机键盘事件 */
    public String key() default "";

    /** View的触摸事件 */
    public String Touch() default "";
}
