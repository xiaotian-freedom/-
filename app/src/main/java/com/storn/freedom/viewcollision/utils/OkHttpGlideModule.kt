package com.storn.freedom.viewcollision.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * @Description:
 * @Author: TST
 * @CreateDate: 2021/6/25$ 3:24 下午$
 * @UpdateUser:
 * @UpdateDate: 2021/6/25$ 3:24 下午$
 * @UpdateRemark:
 * @Version: 1.0
 */
@GlideModule
class OkHttpGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(getUnSafeOkHttpClient()))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    private fun getUnSafeOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .sslSocketFactory(
                SSLSocketClient.getSSLSocketFactory(),
                SSLSocketClient.geX509tTrustManager()
            )
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}