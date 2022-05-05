package io.iotex.pebble.module.http

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

const val HTTP_HOST = "http://34.146.117.200:9010/"

interface ApiService {

    @POST("meta/sign")
    fun signPebble(@Body request: SignPebbleBody): Observable<BaseResp<SignPebbleResp>>

}


