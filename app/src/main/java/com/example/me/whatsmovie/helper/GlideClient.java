package com.example.me.whatsmovie.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Me on 5/21/2017.
 */

public class GlideClient {
    public static void downloadImage (Context c, String imageUrl, ImageView img){
            Glide.with(c)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.01f)
                    .into(img);

    }
}
