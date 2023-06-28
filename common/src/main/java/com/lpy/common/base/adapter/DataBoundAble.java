package com.lpy.common.base.adapter;

import java.util.List;

/**
 * @author lipeiyong
 * @date on 2018/9/4 下午4:17
 */
public interface DataBoundAble<T> {

    void submitList(List<T> update);
}
