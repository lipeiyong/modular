package com.lpy.common.base.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;


import java.util.List;

/**
 * A generic RecyclerView adapter that uses Data Binding
 * @author lipeiyong
 */
public abstract class BaseDataBoundAdapter<DataType, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> implements DataBoundAble<DataType> {


    public BaseDataBoundAdapter() {
    }

    public BaseDataBoundAdapter(List<DataType> items) {
        this.items = items;
    }

    public List<DataType> items;

    @Override
    public final DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataBoundViewHolder<>(createBinding(parent, viewType));
    }

    @Override
    public final void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, position);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public void submitList(List<DataType> update) {
        items = update;
        notifyDataSetChanged();
    }

    /**
     * 创建一个 V
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new DataBoundViewHolder<> that holds a View of the given view type.
     */
    protected abstract V createBinding(ViewGroup parent, int viewType);

    /**
     * 数据绑定
     *
     * @param binding  视图
     * @param position 数据下标
     */
    protected abstract void bind(V binding, int position);

}
