package io.iotex.pebble.utils

import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

object FunctionSignDataUtil {

    val web3j: Web3j by lazy {
        Web3j.build(HttpService("https://babel-api.mainnet.iotex.io", true))
    }

    fun getTransferSignData(toAddress: String, amount: String, decimals: Int): String {
        val methodName = "transfer"
        val inputParameters: MutableList<Type<*>> = ArrayList()
        val outputParameters: MutableList<TypeReference<*>> = ArrayList()
        val tAddress = Address(toAddress)
        val tokenValue = Uint256(Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger())
        inputParameters.add(tAddress)
        inputParameters.add(tokenValue)
        val typeReference: TypeReference<Bool> = object : TypeReference<Bool>() {}
        outputParameters.add(typeReference)
        val function =
            org.web3j.abi.datatypes.Function(methodName, inputParameters, outputParameters)
        return FunctionEncoder.encode(function)
    }

    fun getNonce(fromAddress: String): BigInteger? {
        val ethGetTransactionCount =
            web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send()
                ?: return null
        return ethGetTransactionCount.transactionCount
    }

    fun estimateGasPrice(): String {
        return web3j.ethGasPrice().send().gasPrice?.toBigDecimal()?.toString() ?: "1000000000000"
    }

}