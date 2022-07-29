package com.machinefi.w3bstream.common.request

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

internal interface ApiService {

    @POST("meta/sign")
    fun signPebble(@Body request: SignDeviceBody): Observable<BaseResp<SignDeviceResult>>

    @POST
    fun uploadData(@Url url: String, @Body request: RequestBody): Observable<BaseResp<Nothing>>

}