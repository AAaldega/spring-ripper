package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//for BeanPostProcessor (after method proxy) example
@Retention(RetentionPolicy.RUNTIME)
public @interface Profiling {
}
