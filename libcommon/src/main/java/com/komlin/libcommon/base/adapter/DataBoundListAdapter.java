package com.komlin.libcommon.base.adapter;

import android.annotation.SuppressLint;
import androidx.databinding.ViewDataBinding;
import android.os.AsyncTask;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;


import java.util.List;

import timber.log.Timber;

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The of the ViewDataBinding
 * @author lipeiyong
 */
public abstract class DataBoundListAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> {

    public List<T> items;
    private int dataVersion = 0;

    @Override
    public final DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        V binding = createBinding(parent, viewType);
        return new DataBoundViewHolder<>(binding);
    }

    @Override
    public final void onBindViewHolder(DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, position);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @SuppressLint("StaticFieldLeak")
    public void replace(List<T> update) {
        if (null != update) {
            Timber.i("replace called ,new size :[%d] ", update.size());
        }
        dataVersion++;
        if (items == null) {
            if (update == null) {
                return;
            }
            items = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = items.size();
            items = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = items;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = update.get(newItemPosition);
                            return DataBoundListAdapter.this.areContentsTheSame(oldItem, newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }
                    items = update;
                    diffResult.dispatchUpdatesTo(DataBoundListAdapter.this);
                }
            }.execute();
        }
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
     * @param binding 视图
     * @param position    位置
     */
    protected abstract void bind(V binding, int position);

    /**
     * 是否是同一条数据
     *
     * @param oldItem 旧的
     * @param newItem 新的
     * @return true 是同一条数据
     */
    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    /**
     * 判断视觉效果是否相同
     *
     * @param oldItem 旧的
     * @param newItem 新的
     * @return true 显示效果相同
     */
    protected abstract boolean areContentsTheSame(T oldItem, T newItem);


}
