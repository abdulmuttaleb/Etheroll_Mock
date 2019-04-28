package com.isaiko.etheroll.utils;

import android.os.Environment;
import android.util.Log;

import com.isaiko.etheroll.BuildConfig;
import com.isaiko.etheroll.contracts.Etheroll;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Web3jHandler {

    public static final String INFURA_PRIVATE = BuildConfig.INFURA_PRIVATE;
    public static final String INFURA_PUBLIC = BuildConfig.INFURA_PUBLIC;
    public static final String WALLET_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
    //Contract address on Ropsten network
    public static final String ETHEROLL_CONTRACT_ADDRESS = "0xFE8a5f3a7Bb446e1cB4566717691cD3139289ED4";
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    public static Etheroll EtherollContract;
    private static Web3j web3;
    private static Credentials credentials;
    private static TransactionReceipt transactionReceipt;

    public static boolean web3Connection() throws IOException {
        web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/"+INFURA_PUBLIC));
        return  web3 != null;
    }

    public static void loadCredentials(String password, String filePath) throws IOException, CipherException {
        credentials = WalletUtils.loadCredentials(password, filePath);
    }

    public static String createWallet(String password) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        String fileName = WalletUtils.generateLightNewWalletFile(password, new File(WALLET_PATH));
        return WALLET_PATH+"/"+fileName;
    }

    public static TransactionReceipt transaction(String address, double ethBalance) throws Exception {
        return transactionReceipt = Transfer.sendFunds( web3, credentials, address, BigDecimal.valueOf(ethBalance), Unit.ETHER).send();
    }

    public static BigInteger getBalance(){
        Future<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync();
        try {
            return ethGetBalanceFuture.get().getBalance();
        }catch (Exception e){
            return BigInteger.ONE;
        }
    }

    public static String getWalletAddress(){
        return credentials.getAddress();
    }

    public static BigDecimal getEtherBalance() throws ExecutionException, InterruptedException {
        return Convert.fromWei(web3.ethGetBalance(getWalletAddress(),DefaultBlockParameterName.LATEST).sendAsync().get().getBalance().toString(), Unit.ETHER);
    }

    public static String printWeb3Version(){
        Web3ClientVersion web3ClientVersion = null;
        try {
            web3ClientVersion = web3.web3ClientVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String web3jClientVersionString = web3ClientVersion.getWeb3ClientVersion();
        return "Web3 client version: " + web3jClientVersionString;
    }

    public static void initEtheroll() throws Exception {
        EtherollContract = loadEtherollContract();
        //instantiating vars
        EtherollVars vars = new EtherollVars();
    }

    private static Etheroll loadEtherollContract(){
        return Etheroll.load(ETHEROLL_CONTRACT_ADDRESS, web3, credentials, GAS_PRICE, GAS_LIMIT);
    }
}
