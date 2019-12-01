package org.seckill.infrastructure;

import java.lang.annotation.*;

/**
 * @author caoduanxi
 * @2019/12/1 16:17
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisComponent {

    String value() default "";

}