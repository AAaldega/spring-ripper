package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//for beanPostProcessor (before method) example
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectRandomInt {
    int min();

    int max();
}
