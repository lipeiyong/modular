package com.lpy.common.base.adapter.loadmore;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lpy.common.R;
import com.lpy.common.databinding.ComListFloorViewBinding;
import com.lpy.common.util.numbers.UnboxUtils;


/**
 * 默认有更多数据就显示FooterView，没有就不显示FooterView
 * showAlways == true 时。一直显示FooterView
 *
 * @author lipeiyong
 */
public abstract class BaseLoadMoreAdapter<T, V extends ViewDataBinding> extends BaseFooterListAdapter<T, V> {


    private OnLoadMoreListener loadMoreListener;


    @Override
    protected final void bindingFooter(ViewDataBinding binding, Object data) {
        bindingFooter(binding, hasMoreData, data);
    }

    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastPosition == getItemCount() - 1 && null != loadMoreListener && hasMoreData) {
                        loadMoreListener.onLoadMore();
                    }
                }
            }
        });
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0 && null != loadMoreListener) {
                    loadMoreListener.onLoadMore();
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    private boolean hasMoreData = false;
    private boolean showNoMoreView = false;

    public final void showNoMoreView(boolean show) {
        this.showNoMoreView = show;
    }

    public final void hasMoreData(Boolean hasMore) {
        this.hasMoreData = UnboxUtils.safeUnBox(hasMore);
        if (null != recyclerView && (recyclerView.isAnimating() || recyclerView.isComputingLayout())) {
            return;
        }
        showFooterView(showNoMoreView || hasMoreData);
    }

    public final void hasMoreData(boolean hasMore, Object data) {
        this.hasMoreData = UnboxUtils.safeUnBox(hasMore);
        if (null != recyclerView && (recyclerView.isAnimating() || recyclerView.isComputingLayout())) {
            return;
        }
        showFooterView(showNoMoreView || hasMoreData, data);
    }

    @Override
    protected <VT extends ViewDataBinding> VT createFooterBinding(ViewGroup parent, int viewType) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.com_list_floor_view, parent, false);
    }

    protected void bindingFooter(ViewDataBinding binding, boolean hasMoreData, Object data) {
        if (binding instanceof ComListFloorViewBinding) {
            if (hasMoreData) {
                ((ComListFloorViewBinding) binding).hint.setText("正在加载更多数据...");
            } else {
                ((ComListFloorViewBinding) binding).hint.setText("没有更多数据！");
            }
        }
    }

}
