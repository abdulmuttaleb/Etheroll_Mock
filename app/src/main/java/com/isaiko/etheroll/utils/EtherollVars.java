package com.isaiko.etheroll.utils;

import android.util.Log;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static com.isaiko.etheroll.utils.Web3jHandler.EtherollContract;

public class EtherollVars {

    public static final BigInteger MAX_PROFIT_DIVISOR = BigInteger.valueOf(1000000L);
    public static BigInteger HOUSE_EDGE_DIVISOR;
    public static BigInteger HOUSE_EDGE;
    public static BigInteger MAX_PROFIT;
    public static BigInteger MIN_BET;
    public static final int MAX_NUMBER = 99;
    public static final int MIN_NUMBER = 2;

    public EtherollVars() throws ExecutionException, InterruptedException {
        //Init Etheroll Contract game variables from deployed contract
        HOUSE_EDGE = EtherollContract.houseEdge().sendAsync().get();
        MAX_PROFIT = EtherollContract.maxProfit().sendAsync().get();
        MIN_BET = EtherollContract.minBet().sendAsync().get();
        HOUSE_EDGE_DIVISOR = EtherollContract.houseEdgeDivisor().sendAsync().get();
    }
}
