package com.isaiko.etheroll.utils;

import android.os.Environment;

import com.isaiko.etheroll.BuildConfig;

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
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

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

    public static final String ROPSTEN_PRIVATE = BuildConfig.ROPSTEN_PRIVATE;
    public static final String ROPSTEN_PUBLIC = BuildConfig.ROPSTEN_PUBLIC;
    public static final String WALLET_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
    private static Web3j web3;
    private static Credentials credentials;
    private static TransactionReceipt transactionReceipt;

    public static boolean web3Connection() throws IOException {
        web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/"+ROPSTEN_PUBLIC));
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
        return transactionReceipt = Transfer.sendFunds( web3, credentials, address, BigDecimal.valueOf(ethBalance), Convert.Unit.ETHER).send();
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

    public static BigInteger getEtherBalance() throws ExecutionException, InterruptedException {
        return web3.ethGetBalance(getWalletAddress(),DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
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
}
