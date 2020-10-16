package com.komlin.libcommon.base.databinding;

import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.komlin.libcommon.R;

import timber.log.Timber;

/**
 * @author lipeiyong
 * @date on 2018/9/3 下午3:55
 */
public class ImageDataBindingAdapter {


    @BindingAdapter("imageRes")
    public static void setImageRes(ImageView imageView, int res) {
        Timber.i("url = " + res);
        imageView.setImageResource(res);
    }

    @BindingAdapter("imageUrl")
    public static void setImageLevel(ImageView imageView, String url) {
        Timber.i("url = " + url);
        Glide.with(imageView)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.comm_img_load)
                        .error(R.drawable.comm_img_lose))
                .into(imageView);
    }

    @BindingAdapter("imageLevel")
    public static void setImageLevel(ImageView imageView, int level) {
        imageView.setImageLevel(level);
    }


}
