package com.machinefi.metapebble.module.walletconnect

import com.machinefi.metapebble.utils.extension.toHexByteArray
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256

object FunctionSignData {

    fun getApproveRegistrationDate(address: String, tokenId: String): String {
        val methodName = "approve"
        val inputParameters: MutableList<Type<*>> = ArrayList()
        val outputParameters: MutableList<TypeReference<*>> = ArrayList()
        val addressParam = Address(address)
        val tokenIdParam = Uint256(tokenId.toBigInteger())
        inputParameters.add(addressParam)
        inputParameters.add(tokenIdParam)
        val function = Function(methodName, inputParameters, outputParameters)
        return FunctionEncoder.encode(function)
    }

    fun getRegistrationData(tokenId: String, imei: String, pubkey: String, sn: String,
                           timestamp: String, signature: String, authentication: String): String {
        val methodName = "register"
        val inputParameters: MutableList<Type<*>> = ArrayList()
        val outputParameters: MutableList<TypeReference<*>> = ArrayList()
        val tokenIdParam = Uint256(tokenId.toBigInteger())
        val imeiParam = Utf8String(imei)
        val pubkeyParam = DynamicBytes(pubkey.toHexByteArray())
        val snParam = Bytes32(sn.toHexByteArray())
        val timestampParam = Uint256(timestamp.toBigInteger())
        val signatureParam = DynamicBytes(signature.toHexByteArray())
        val authenticationParam = DynamicBytes(authentication.toHexByteArray())
        inputParameters.add(tokenIdParam)
        inputParameters.add(imeiParam)
        inputParameters.add(pubkeyParam)
        inputParameters.add(snParam)
        inputParameters.add(timestampParam)
        inputParameters.add(signatureParam)
        inputParameters.add(authenticationParam)
        val function = Function(methodName, inputParameters, outputParameters)
        return FunctionEncoder.encode(function)
    }

}