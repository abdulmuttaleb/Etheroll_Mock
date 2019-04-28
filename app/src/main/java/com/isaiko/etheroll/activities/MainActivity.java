package com.isaiko.etheroll.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.isaiko.etheroll.BuildConfig;
import com.isaiko.etheroll.R;
import com.isaiko.etheroll.utils.Web3jHandler;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String INFURA_PRIVATE = BuildConfig.INFURA_PRIVATE;
    public static final String INFURA_PUBLIC = BuildConfig.INFURA_PUBLIC;

    private final static String ETHEROLL_ADDRESS = "0xA52e014B3f5Cc48287c2D483A3E026C32cc76E6d";
    private final static String ROPSTEN_URL = "https://ropsten.infura.io/"+INFURA_PUBLIC;

    private Web3j web3j;
    private Credentials credentials = Credentials.create(INFURA_PRIVATE);
    private int minimumGasLimit = 21000;
    private BigInteger gasLimit = new BigInteger(String.valueOf(minimumGasLimit));

    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.btn_create_wallet)
    Button createWalletButton;
    @BindView(R.id.et_password)
    EditText passwordEditText;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            Web3jHandler.web3Connection();
            loginButton.setClickable(true);
            createWalletButton.setClickable(true);
            passwordEditText.setEnabled(true);
        }catch (IOException e){
            loginButton.setClickable(false);
            createWalletButton.setClickable(false);
            passwordEditText.setEnabled(false);
            Toast.makeText(this, "Connection Error! Try again later", Toast.LENGTH_SHORT).show();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(passwordEditText.getText().toString())){
                    passwordEditText.setError(null);
                    new LoadWalletTask().execute();
                    ShowProgressDialog();
                }else{
                    passwordEditText.setError("Invalid password!");
                }
            }
        });

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(passwordEditText.getText().toString())){
                    passwordEditText.setError(null);
                    new CreateWalletTask().execute();
                    ShowProgressDialog();
                }else{
                    passwordEditText.setError("Invalid password!");
                }
            }
        });
    }

    class LoadWalletTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            UnlockWallet();
            return null;
        }
    }

    class CreateWalletTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            CreateWallet();
            return null;
        }
    }
    private void CreateWallet(){
        new Thread(() -> {
            try {
                String filePath = Web3jHandler.createWallet(passwordEditText.getText().toString());
                if(filePath.isEmpty()){
                    ToastInTask("Wallet wasn't created successfully");
                    HideProgressDialog();
                }else{
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("wallet_path", filePath).apply();
                    ToastInTask("Wallet was created Successfully!");
                    HideProgressDialog();
                }
            } catch (CipherException e) {
                e.printStackTrace();
                ToastInTask(e.getMessage());
                HideProgressDialog();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
                ToastInTask(e.getMessage());
                HideProgressDialog();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                ToastInTask(e.getMessage());
                HideProgressDialog();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                ToastInTask(e.getMessage());
                HideProgressDialog();
            } catch (IOException e) {
                Log.d("io","Invalid file");
                ToastInTask(e.getMessage());
                HideProgressDialog();
                e.printStackTrace();
            }
        }).start();
    }
    private void UnlockWallet(){
        new Thread(() -> {
            try {
                String filePath = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("wallet_path", "defaultStringIfNothingFound");
                if(filePath.equals("defaultStringIfNothingFound")){
                    ToastInTask("No wallets");
                }else{
                    Web3jHandler.loadCredentials(passwordEditText.getText().toString(), filePath);
                    Intent EtherollIntent = new Intent(getApplicationContext(), EtherollActivity.class);
                    startActivity(EtherollIntent);
                }
                HideProgressDialog();
            } catch (IOException e) {
                e.printStackTrace();
                ToastInTask("Connection Error");
                HideProgressDialog();
            } catch (CipherException e) {
                e.printStackTrace();
                ToastInTask("Invalid Password");
                HideProgressDialog();
            }
        }).start();
    }

    private void ToastInTask(final String toastText){
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MainActivity.this, toastText , Toast.LENGTH_LONG).show());
    }

    private void ShowProgressDialog(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void HideProgressDialog(){
        progressDialog.cancel();
    }


}
