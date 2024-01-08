package io.skodjob.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestDoc {

    Desc description();
    Step[] steps();
    Usecase[] usecases();

    @interface Desc {
        String value();
    }

    @interface Step {
        String value();
        String expected();
    }

    @interface Usecase {
        String id();
        String note() default "";
    }
}