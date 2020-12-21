package com.adam.lib.network

import android.content.Context
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GenericLoaderFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Created By Adam on 2020/11/26
 */
class OkHttpsUrlLoader(private val client: okhttp3.OkHttpClient) : ModelLoader<GlideUrl, InputStream> {

    /** * The default factory for [OkHttpsUrlLoader]s.  */
    class Factory
    /** * Constructor for a new Factory that runs requests using given client.  */
    @JvmOverloads constructor(private val client: okhttp3.OkHttpClient? = getInternalClient()) : ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(context: Context, factories: GenericLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpsUrlLoader(client!!)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }

        companion object {
            @Volatile
            private var internalClient: okhttp3.OkHttpClient? = null

            private fun getInternalClient(): okhttp3.OkHttpClient? {
                if (internalClient == null) {
                    synchronized(Factory::class.java) {
                        if (internalClient == null) {
                            //todo 暫時不用
                            internalClient = OkHttpClient();//RequestClient.getInstance().getOkHttpClientHome()
                        }
                    }
                }
                return internalClient
            }
        }
    }

    /** * Constructor for a new Factory that runs requests using a static singleton client.  */
    override fun getResourceFetcher(model: GlideUrl, width: Int, height: Int): DataFetcher<InputStream> {
        return OkHttpsStreamFetcher(client, model)
    }
}