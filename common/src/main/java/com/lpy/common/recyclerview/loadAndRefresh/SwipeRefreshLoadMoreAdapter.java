package com.lpy.common.recyclerview.loadAndRefresh;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.ViewGroup;

/**
 * @author lipeiyong
 * @date 2019-12-28 18:28
 */
public class SwipeRefreshLoadMoreAdapter<T,V extends ViewDataBinding> extends DefaultFooterListAdapter<T,V> {


    @Override
    protected V onCreateFoolterBinding(@NonNull ViewGroup viewGroup) {
        return null;
    }

    @Override
    protected V onCreateCustomBinding(@Nullable ViewGroup viewGroup, int viewType) {
        return null;
    }
}
