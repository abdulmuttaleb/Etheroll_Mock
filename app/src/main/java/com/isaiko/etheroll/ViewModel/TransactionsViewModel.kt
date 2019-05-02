package com.isaiko.etheroll.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.isaiko.etheroll.utils.Web3jHandler
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.Transaction
import org.web3j.protocol.core.methods.response.TransactionReceipt

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        private val TAG = TransactionsViewModel::class.qualifiedName
    }
    private var map:MutableMap<String, Transaction> = mutableMapOf()

//    private var txsList = Web3jHandler.web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST,true).send().block.transactions
//
//    fun getTransactions(): List<EthBlock.TransactionResult<Any>> {
//        return txsList
//    }

    init {

        Thread{
            Web3jHandler.web3.transactionObservable().subscribe {
                tx -> map[tx.hash] = tx
                Log.e(TAG,tx.hash)
            }
        }.start()
    }
}