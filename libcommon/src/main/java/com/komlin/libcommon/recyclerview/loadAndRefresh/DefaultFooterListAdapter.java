package com.komlin.libcommon.recyclerview.loadAndRefresh;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author lipeiyong
 * @date 2019-12-28 19:03
 */
public abstract class DefaultFooterListAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DefaultViewHolder<V>> {

    private static final int VT_FLOOR = 0x9999;

    private ArrayList<T> items = new ArrayList<>();
    private boolean showFooter;


    @NonNull
    @Override
    public DefaultViewHolder<V> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VT_FLOOR) {
            return new DefaultViewHolder<>(onCreateFoolterBinding(viewGroup));
        } else {
            return new DefaultViewHolder<>(onCreateCustomBinding(viewGroup, viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultViewHolder<V> vDefaultViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        int superCount = items == null ? 0 : items.size();
        if (showFooter) {
            return superCount + 1;
        } else {
            return superCount;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int superCount = items == null ? 0 : items.size();
        if (superCount == 0 && showFooter) {
            return VT_FLOOR;
        }
        if (superCount == items.size()) {
            return VT_FLOOR;
        }
        return super.getItemViewType(position);
    }

    /**
     * 创建底部加载更多布局
     *
     * @param viewGroup
     * @return
     */
    protected abstract V onCreateFoolterBinding(@NonNull ViewGroup viewGroup);

    /**
     * 创建自定义布局
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    protected abstract V onCreateCustomBinding(@Nullable ViewGroup viewGroup, int viewType);
}
