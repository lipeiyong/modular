package com.komlin.libcommon.base.adapter.loadmore;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.komlin.libcommon.base.adapter.DataBoundAble;
import com.komlin.libcommon.base.adapter.DataBoundViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认有更多数据就显示HeaderView，没有就不显示HeaderView
 * showHeaderAlways == true 时.一直显示HeaderView
 * <p>
 * 思路:RecyclerView支持显示多种布局的View,设置第零个item为HeaderView.
 * 默认显示第一个item,下拉到顶部时显示第零个item,并触发onHeaderLoadMore()
 * <p>
 * 无数据
 * 有数据但不足一页
 * 有数据大于等于一页
 * 注意点：分栏后普通item可能需要占据多个span,没有占据整个屏幕,而header部分需要占据整个屏幕宽度
 * 解决方案：gridLayoutManager.setSpanSizeLookup(position -> xx ? gridLayoutManager.getSpanCount() : 1).
 *           StaggeredGridLayoutManager.LayoutParams.setFullSpan(true);
 *
 * @author lipeiyong
 * @date 2019/11/23
 */
public abstract class BaseHeaderListAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> implements DataBoundAble<T> {
    private static final int VIEW_TYPE_REFRESH_HEARDER = 0x01;
    private static final int VIEW_TYPE_ITEM = 0x02;
    private boolean showHeader;
    private boolean showHeaderAlways;

    private ArrayList<T> items = new ArrayList<>();

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> vDataBoundViewHolder, int i) {

    }

    /**
     * 不建议重写该方法
     *
     * @return item count
     */
    @Override
    public int getItemCount() {
        int superCount = items == null ? 0 : items.size();
        //showHeaderAlways == true ,一直显示HeaderView
        if (showHeaderAlways) {
            return superCount + 1;
        }
        //有更多数据就显示HeaderView，没有就不显示HeaderView
        if (showHeader && superCount != 0) {
            return superCount + 1;
        } else {
            return superCount;
        }
    }

    /**
     * 不建议重写该方法 Use {@link #getCustomViewType(int)} instead.
     *
     * @param position 下表
     * @return item类型
     */
    @Override
    public int getItemViewType(int position) {
        int superCount = items == null ? 0 : items.size();

        return getCustomViewType(position);
    }

    @Override
    public void submitList(List<T> update) {

    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public void setShowHeaderAlways(boolean showHeaderAlways) {
        this.showHeaderAlways = showHeaderAlways;
    }

    /**
     * 多布局适配
     *
     * @param position 下标
     * @return itme类型
     */
    public int getCustomViewType(int position) {
        return super.getItemViewType(position);
    }
}
