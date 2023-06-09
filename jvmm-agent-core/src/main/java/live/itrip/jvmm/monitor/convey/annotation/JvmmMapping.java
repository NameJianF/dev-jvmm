package live.itrip.jvmm.monitor.convey.annotation;

import live.itrip.jvmm.monitor.convey.enums.GlobalType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 5:08 下午 2021/5/30
 *
 * @author fengjianfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JvmmMapping {

    String type() default "";

    GlobalType typeEnum() default GlobalType.JVMM_TYPE_HANDLE_MSG;
}
