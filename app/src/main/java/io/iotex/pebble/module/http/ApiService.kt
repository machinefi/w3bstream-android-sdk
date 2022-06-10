package io.iotex.pebble.module.http

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

//const val HTTP_HOST = "http://34.146.117.200:9010/"
const val HTTP_HOST = " https://auth.metapebble.app"

interface ApiService {

    @POST("meta/sign")
    fun signPebble(@Body request: SignPebbleBody): Observable<BaseResp<SignPebbleResp>>

    @POST
    fun uploadMetadata(@Url url: String, @Body request: UploadMetadataBody): Observable<BaseResp<Nothing>>

}


