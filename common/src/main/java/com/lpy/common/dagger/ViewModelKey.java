package com.lpy.common.dagger;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author lipeiyong
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
