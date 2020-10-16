package com.komlin.libcommon.base.adapter.select;

import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.Checkable;

import com.komlin.libcommon.base.adapter.DataBoundAble;
import com.komlin.libcommon.base.adapter.DataBoundViewHolder;

import java.util.List;

import timber.log.Timber;

/**
 * 单选
 * 由传入的实体对象自己来记录选中状态
 *
 * @author lipeiyong
 * @date 2019/9/5 16:36
 */
public abstract class BaseSingleAdapter<DataType extends Checkable, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> implements DataBoundAble<DataType> {

    private SparseBooleanArray mCheckStates = new SparseBooleanArray(0);

    public BaseSingleAdapter() {
    }

    public BaseSingleAdapter(List<DataType> items) {
        this.items = items;
    }

    public List<DataType> items;

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataBoundViewHolder<>(createBinding(parent, viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, position);
        holder.binding.executePendingBindings();
        holder.binding.getRoot().setOnClickListener(v -> {
            Timber.i("onClickStartTime:"+System.currentTimeMillis());
            boolean checked = !mCheckStates.get(position, false);
            if (mCheckStates.size() > 0 && mCheckStates.valueAt(0)) {//如果有选中的先消去
                int lastSelectPostion = mCheckStates.keyAt(0);
                items.get(lastSelectPostion).setChecked(false);
                notifyItemChanged(lastSelectPostion);
            }
            if (checked) {
                mCheckStates.clear();
                mCheckStates.put(position, true);
                items.get(position).setChecked(true);
            } else {
                mCheckStates.clear();
            }
            notifyItemChanged(position);

            onRootItemClick(holder.binding, position);
        });
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
     * 返回当前选中的item下标,如果没有选中的item则返回-1
     *
     * @return
     */
    public int getCurrSelectPosition() {
        if (mCheckStates.size() > 0) {
            return mCheckStates.keyAt(0);
        }
        return -1;
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

    /**
     * 点击
     *
     * @param binding
     * @param position
     */
    protected abstract void onRootItemClick(V binding, int position);
}
