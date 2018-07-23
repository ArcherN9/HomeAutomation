package android_home_hub.daksh.com.homeautomation_hub.Config

import android.support.annotation.NonNull
import android_home_hub.daksh.com.homeautomation_hub.HomeApplication
import okhttp3.*
import okhttp3.internal.http2.Http2
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP

object RetroFit {

    //The base URL where all retrofit requests will be directed to
    private lateinit var strBaseUrl: String

    init {

        //Setup the default strBaseUrl | would be overriden though
        strBaseUrl = "http://192.168.0.1/"
    }

    /**
     * reconfigures the base URL
     */
    internal fun reconfigureServer(strBaseUrl: String?) {
        strBaseUrl?.let {
            this@RetroFit.strBaseUrl = it

            //Check if retrofitClient is configured with the same URL. If not, reconfigure it
            if(retrofitClient.baseUrl().host().indexOf(it) <= 0) {

                //Check if scheme etc is present in server URL
                if(this@RetroFit.strBaseUrl.indexOf("http://") <= 0 || this@RetroFit.strBaseUrl.indexOf("https://") <= 0)
                    this@RetroFit.strBaseUrl = "http://$strBaseUrl"

                //Check if server URL contains "/" at the end
                if(!this@RetroFit.strBaseUrl.endsWith("/"))
                    this@RetroFit.strBaseUrl += "/"

                retrofitClient = retrofitClient.newBuilder()
                        .baseUrl(this@RetroFit.strBaseUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                HomeApplication.log("Server has been reconfigured with URL : ${retrofitClient.baseUrl()}")
            }
        }
    }

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
    var retrofitClient: Retrofit = Retrofit.Builder()
            //For illustration, we've used the server address as CollinsDictionary
            .baseUrl(strBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

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