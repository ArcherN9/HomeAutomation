package com.daksh.homeautomation

import android.support.annotation.NonNull
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroFit private constructor(/* Empty private constructor in private to discourage making instances */) {

    companion object {
        //Create an okHttpClient to handle interceptors
        private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(CustomInterceptor())
                .addInterceptor(getInterceptor())
                .build()

        /**
         * The initialize method used to initialize RetroFit in the project
         */
        private fun getInterceptor(): HttpLoggingInterceptor {
            //Create a new interceptor just to log data on to the console
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return loggingInterceptor
        }

        /**
         * The retrofit client used to interact with web services.
         */
        private var retrofitClient: Retrofit = Retrofit.Builder()
                //For illustration, we've used the server address as CollinsDictionary
                .baseUrl("https://daksh-home-automation.herokuapp.com/")
//                .baseUrl("http://192.168.0.7:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        /**
         * Returns the created RetroFit instance
         * @return RetroFit
         */
        fun getRetrofit() : Retrofit = retrofitClient
    }

    /**
     * A custom interceptor used to inject authorization keys or other user specific data
     * often used to identify the request origination at the server.
     *
     * This approach is usually used to pass on authorization keys to the server so we don't have
     * to manually add it in each request
     */
    private class CustomInterceptor: Interceptor {

        override fun intercept(@NonNull chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()

            //Add authorization params to either headers or URL itself
            //1) In case of headers
            val headers: Headers = originalRequest.headers().newBuilder()
//                    .add("Enter Authorization key here", "Enter value here")
                    .build()

            //2) In case of URL
            val httpUrl: HttpUrl = originalRequest.url().newBuilder()
                    //Query params are usually used to enter authorization keys for users
                    //to uniquely identify origin of service request on the server.
                    //However, it may also be used when certain values are passed on to
                    //every service request. To avoid redundancy of adding same key=value
                    //to every service request, it may be intercepted and added like so.
//                    .addQueryParameter("Enter Authorization key here", "Enter value here")
//                    .addQueryParameter("dictCode", "english")
                    .build()

            //Continue the network execution
            return chain.proceed(originalRequest.newBuilder()
                    .headers(headers)   //Add new headers generated
                    .url(httpUrl)       //Add new URL params generated
                    .build())
        }
    }
}