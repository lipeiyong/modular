package com.komlin.libcommon.dagger.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author lipeiyong
 * @date on 2018/7/11 上午11:01
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface FragmentScope {

}
