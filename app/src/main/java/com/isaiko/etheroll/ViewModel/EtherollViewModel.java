package com.isaiko.etheroll.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.isaiko.etheroll.utils.Web3jHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static com.isaiko.etheroll.utils.Web3jHandler.EtherollContract;


public class EtherollViewModel extends AndroidViewModel {

    private BigInteger houseEdge;
    private BigInteger maxProfit;
    private BigInteger houseEdgeDivisor;
    private BigInteger minBet;
    private BigDecimal walletBalance;
    public EtherollViewModel(@NonNull Application application) {
        super(application);
        try {
            Web3jHandler.initEtheroll();
            walletBalance = Web3jHandler.getEtherBalance();
            houseEdge = EtherollContract.houseEdge().sendAsync().get();
            maxProfit = EtherollContract.maxProfit().sendAsync().get();
            minBet = EtherollContract.minBet().sendAsync().get();
            houseEdgeDivisor = EtherollContract.houseEdgeDivisor().sendAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigInteger getHouseEdge() {
        return houseEdge;
    }

    public BigInteger getMaxProfit() {
        return maxProfit;
    }

    public BigInteger getHouseEdgeDivisor() {
        return houseEdgeDivisor;
    }

    public BigInteger getMinBet() {
        return minBet;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }
}
