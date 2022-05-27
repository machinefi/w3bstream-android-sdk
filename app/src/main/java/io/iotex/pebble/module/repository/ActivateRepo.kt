package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.iotex.graphql.smartcontract.RegistrationResultQuery
import io.iotex.pebble.constant.CHAIN_ID
import io.iotex.pebble.constant.REGISTRATION_RESULT_CONTRACT
import io.iotex.pebble.di.annocation.ApolloClientSmartContract
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.http.ApiService
import io.iotex.pebble.module.http.BaseResp
import io.iotex.pebble.module.http.SignPebbleBody
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.module.walletconnect.WcMethod
import io.iotex.pebble.utils.AddressUtil
import io.iotex.pebble.utils.KeystoreUtil
import io.iotex.pebble.utils.RxUtil
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.toHexByteArray
import io.iotex.pebble.utils.extension.toHexString
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.walletconnect.Session
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import wallet.core.jni.Hash
import java.math.BigInteger
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class ActivateRepo @Inject constructor(
    val mApiService: ApiService,
    val mPebbleRepo: PebbleRepo,
    @ApolloClientSmartContract val mApolloClient: ApolloClient
) {

    fun signDevice(request: SignPebbleBody): Observable<BaseResp<SignPebbleResp>> {
        return mApiService.signPebble(request).compose(RxUtil.observableSchedulers())
    }

    suspend fun signMessage(msg: String): String {
        return withContext(Dispatchers.IO) {
//            val hash = Hash.keccak256(msg.toHexByteArray())
//            "hash : ${hash.toHexString()}".i()
            KeystoreUtil.signData(msg.toHexByteArray())?.toHexString() ?: ""
        }
    }

    suspend fun generateSignature(msg: String): String = withContext(Dispatchers.IO) {
        val sigStr = Numeric.cleanHexPrefix(msg)
        if (sigStr.length < 8) throw Exception("Signature message is too short")
        val len = sigStr.substring(6, 8).toBigInteger(16)
            .times(BigInteger.valueOf(2)).toInt()
        val middle = sigStr.substring(8)
        val arg01 = middle.substring(0, len).toBigInteger(16)
        val arg02 = middle.substring(len + 4).toBigInteger(16)
        val arg01Encode = TypeEncoder.encodePacked(Uint256(arg01))
        val arg02Encode = TypeEncoder.encodePacked(Uint256(arg02))
        Numeric.prependHexPrefix(arg01Encode + arg02Encode)
    }

    suspend fun executeContract(params: Map<String, String>): Session.MethodCall.Response {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val id = System.currentTimeMillis()
                WcKit.mWalletConnectKit.session?.performMethodCall(
                    Session.MethodCall.Custom(id, WcMethod.SIGN_TRANSACTION.value, listOf(params))
                ) { response ->
                    handleTransactionResponse(id, response, continuation)
                } ?: continuation.resumeWith(Result.failure(Throwable("Session not found!")))
                WcKit.mWalletConnectKit.openWallet()
            }
        }
    }

    suspend fun queryActivatedResult(imei: String) = withContext(Dispatchers.IO) {
        val contractOpt = Optional.presentIfNotNull(REGISTRATION_RESULT_CONTRACT)
        val chainIdOpt = Optional.presentIfNotNull(CHAIN_ID)
        val imeiOpt = Optional.presentIfNotNull(imei)
        val result = mApolloClient.query(RegistrationResultQuery(contractOpt, chainIdOpt, imeiOpt))
            .execute()
            .data?.result
        if (!result.isNullOrEmpty()) {
            val dataList = result[0]?.find?.split(",")
            if (!dataList.isNullOrEmpty()) {
                val owner = dataList[0]
                val activated = AddressUtil.isValidAddress(owner)
                if (activated) {
                    val device = AppDatabase.mInstance.deviceDao().queryByImei(imei)
                    device?.also {
                        device.owner = owner
                        mPebbleRepo.updateDevice(device)
                    }
                }
                return@withContext activated
            }
        }
        return@withContext false
    }

    private fun handleTransactionResponse(
        id: Long,
        response: Session.MethodCall.Response,
        continuation: Continuation<Session.MethodCall.Response>
    ) {
        if (id != response.id) {
            val throwable = Throwable("The response id is different from the transaction id!")
            continuation.resumeWith(Result.failure(throwable))
            return
        }
        response.error?.let {
            continuation.resumeWith(Result.failure(Throwable(it.message)))
        } ?: continuation.resumeWith(Result.success(response))
    }

}