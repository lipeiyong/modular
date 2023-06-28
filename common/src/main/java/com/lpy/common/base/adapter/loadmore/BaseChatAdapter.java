package com.lpy.common.base.adapter.loadmore;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.lpy.common.base.adapter.DataBoundAble;
import com.lpy.common.base.adapter.DataBoundViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lipeiyong
 */
public class BaseChatAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> implements DataBoundAble<T> {

    private ArrayList<T> items = new ArrayList<>();

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> vDataBoundViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void submitList(List<T> update) {

    }
}
