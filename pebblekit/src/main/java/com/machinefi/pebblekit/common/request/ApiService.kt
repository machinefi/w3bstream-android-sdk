package com.machinefi.pebblekit.common.request

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

internal interface ApiService {

    @POST("meta/sign")
    fun signPebble(@Body request: SignPebbleBody): Observable<BaseResp<SignPebbleResult>>

    @POST
    fun uploadMetadata(@Url url: String, @Body request: RequestBody): Observable<BaseResp<Nothing>>

}