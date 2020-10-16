package com.komlin.libcommon.recyclerview.loadAndRefresh;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author lipeiyong
 * @date 2019-12-28 18:41
 */
public class DefaultViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private final V view;

    public DefaultViewHolder(V view) {
        super(view.getRoot());
        this.view = view;
    }
}