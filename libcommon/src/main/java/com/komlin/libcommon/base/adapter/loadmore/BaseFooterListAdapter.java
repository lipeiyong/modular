package com.komlin.libcommon.base.adapter.loadmore;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.komlin.libcommon.base.adapter.DataBoundAble;
import com.komlin.libcommon.base.adapter.DataBoundViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * @author lipeiyong
 */
public abstract class BaseFooterListAdapter<T, V extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<V>> implements DataBoundAble<T> {
    private static final int VT_FLOOR = 0x9999;
    private boolean showFooter;
    private boolean showFooterAlways;
    private Object footerData;

    private int dataVersion = 0;
    private ArrayList<T> items = new ArrayList<>();

    @Override
    public final DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VT_FLOOR) {
            return new DataBoundViewHolder<>(createFooterBinding(parent, viewType));
        } else {
            return getCustomViewHolder(parent, viewType);
        }
    }

    public DataBoundViewHolder getCustomViewHolder(ViewGroup parent, int viewType) {
        return new DataBoundViewHolder<>(createNormalBinding(parent, viewType));
    }

    @Override
    public final void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        int itemCount = items == null ? 0 : items.size();
        if ((itemCount == 0 && showFooterAlways)) {
            bindingFooter(holder.binding, footerData);
        }
        if (itemCount != 0) {
            if (position == itemCount) {
                bindingFooter(holder.binding, footerData);
            } else {
                bindingNormal(holder.binding, items.get(position));
            }
        }
        holder.binding.executePendingBindings();
    }


    @SuppressLint("StaticFieldLeak")
    public void submitList(List<T> update) {
        if (null != update) {
            Timber.i("submitList called ,new size :[%d] ", update.size());
        }
        dataVersion++;
        if (items == null || items.size() == 0) {
            if (update == null || update.size() == 0) {
                return;
            }
            updateItems(update);
            notifyDataSetChanged();
        } else if (update == null || update.size() == 0) {
            int oldSize = items.size();
            updateItems(Collections.emptyList());
            notifyDataSetChanged();
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = items;
            final boolean localShow = showFooter;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {

                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return localShow ? oldItems.size() : oldItems.size() + 1;
                        }

                        @Override
                        public int getNewListSize() {
                            return localShow ? update.size() : update.size() + 1;
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = null, newItem = null;
                            if (oldItemPosition != oldItems.size()) {
                                oldItem = oldItems.get(oldItemPosition);
                            }
                            if (newItemPosition != update.size()) {
                                newItem = update.get(newItemPosition);
                            }
                            if (newItem == oldItem) {
                                return true;
                            }
                            if (newItem == null || oldItem == null) {
                                return false;
                            }
                            return BaseFooterListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = null, newItem = null;
                            if (oldItemPosition != oldItems.size()) {
                                oldItem = oldItems.get(oldItemPosition);
                            }
                            if (newItemPosition != update.size()) {
                                newItem = update.get(newItemPosition);
                            }
                            if (newItem == null || oldItem == null) {
                                return false;
                            }
                            return BaseFooterListAdapter.this.areContentsTheSame(oldItem, newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        return;
                    }
                    updateItems(update);
                    diffResult.dispatchUpdatesTo(BaseFooterListAdapter.this);
                }
            }.execute();
        }
    }

    private void updateItems(List<T> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
    }


    @Override
    public int getItemCount() {
        int superCount = items == null ? 0 : items.size();
        if (superCount == 0 && showFooterAlways) {
            return 1;
        }
        if (showFooter && superCount != 0) {
            return superCount + 1;
        } else {
            return superCount;
        }
    }

    public int getCustomViewType(int postion) {
        return super.getItemViewType(postion);
    }


    @Override
    public int getItemViewType(int position) {
        int superCount = items == null ? 0 : items.size();
        if (superCount == 0 && showFooterAlways) {
            return VT_FLOOR;
        }
        if (position == superCount) {
            return VT_FLOOR;
        }
        return getCustomViewType(position);
    }


    public void showFooterView(boolean show) {
        showFooterView(show, null);
    }


    public void showFooterView(boolean show, Object data) {
        Timber.i("showFooterView : " + show);
        if (show == showFooter) {
            showFooter = show;
            footerData = data;
            if (showFooter) {
                Timber.i("showFooterView : notifyItemChanged" + (getItemCount() - 1));
                int lastIndex = getItemCount() - 1;
                if (/*数据被清空，Footer不存在*/lastIndex < 0) {
                    notifyDataSetChanged();
                } /*仅Footer状态改变，Footer依然存在*/ else {
                    notifyItemChanged(getItemCount() - 1);
                }
            }
        } else if (show) {
            showFooter = true;
            footerData = data;
            notifyItemInserted(getItemCount() - 1);
            Timber.i("showFooterView : notifyItemInserted" + (getItemCount() - 1));
        } else {
            showFooter = false;
            footerData = data;
            Timber.i("showFooterView : notifyItemRemoved" + (getItemCount() - 1));
            notifyItemRemoved(getItemCount() - 1);
        }
    }

    public void showFooterAlways(boolean alwaysShow) {
        showFooterAlways = alwaysShow;
    }

    protected abstract V createNormalBinding(ViewGroup parent, int viewType);

    protected abstract void bindingNormal(V binding, T data);

    protected abstract <VT extends ViewDataBinding> VT createFooterBinding(ViewGroup parent, int viewType);

    protected abstract void bindingFooter(ViewDataBinding binding, Object data);


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






