package live.itrip.jvmm.agent.convey.annotation;

import live.itrip.jvmm.agent.convey.enums.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 11:28 2022/9/13
 *
 * @author fengjianfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequest {

    String value();

    Method method() default Method.GET;
}
