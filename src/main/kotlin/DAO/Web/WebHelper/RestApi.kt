package DAO.Web.WebHelper

import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import Util.Const.*
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object RestApi {

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else "{}"
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                var newUrl = SingURL.signingUrl(it.request().url())
                println(URLDecoder.decode(newUrl.toString(), StandardCharsets.UTF_8.toString()))
                it.proceed(it.request().newBuilder().url(newUrl).build())
            }.build()
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    val webApi = retrofit.create(Api::class.java)!!


}