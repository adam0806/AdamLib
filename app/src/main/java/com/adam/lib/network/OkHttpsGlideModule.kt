package com.adam.lib.network

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.GlideModule
import java.io.InputStream

/**
 * Created By Adam on 2020/11/26
 */
class OkHttpsGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {}
    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(GlideUrl::class.java, InputStream::class.java, OkHttpsUrlLoader.Factory())
    }
}