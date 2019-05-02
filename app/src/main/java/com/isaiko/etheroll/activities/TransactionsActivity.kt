package com.isaiko.etheroll.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.isaiko.etheroll.R
import com.isaiko.etheroll.ViewModel.TransactionsViewModel
import com.isaiko.etheroll.utils.Web3jHandler
import kotlinx.android.synthetic.main.activity_transactions.*

class TransactionsActivity: AppCompatActivity() {

    val TAG:String = "TransactionActivity"
//    private lateinit var transactionsViewModel:TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = Web3jHandler.getWalletAddress()
//      transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}