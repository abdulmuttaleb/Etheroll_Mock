package com.isaiko.etheroll.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.isaiko.etheroll.BuildConfig;
import com.isaiko.etheroll.R;
import com.isaiko.etheroll.utils.Web3jHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.isaiko.etheroll.utils.ExtensionsUtils.ToastInTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
    @BindView(R.id.tv_wallet_file)
    TextView walletFileName;
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

        loginButton.setOnClickListener(v ->
                Dexter.withActivity(this).withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(!report.isAnyPermissionPermanentlyDenied()){
                    login();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check());

        createWalletButton.setOnClickListener(v ->
                Dexter.withActivity(this).withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(!report.isAnyPermissionPermanentlyDenied()){
                            signup();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).onSameThread().check()
        );

    }

    private void checkPermissionsOnStart(){
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(!report.isAnyPermissionPermanentlyDenied()){
                    createWalletButton.setEnabled(true);
                    loginButton.setEnabled(true);
                    checkExistingWallets();
                }else{
                    Toast.makeText(MainActivity.this, "Can't proceed without providing write/reading to storage permissions.\nPlease change that in settings", Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(false);
                    createWalletButton.setEnabled(false);
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    private void login(){
        if(!TextUtils.isEmpty(passwordEditText.getText().toString())){
            passwordEditText.setError(null);
            new LoadWalletTask().execute();
            ShowProgressDialog();
        }else{
            passwordEditText.setError("Invalid password!");
        }
    }

    private void signup(){
        if(!TextUtils.isEmpty(passwordEditText.getText().toString())){
            passwordEditText.setError(null);
            new CreateWalletTask().execute();
            ShowProgressDialog();
        }else{
            passwordEditText.setError("Invalid password!");
        }
    }
    private void checkExistingWallets() {
        File walletFolder = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getAbsolutePath(),"/android/data/com.isaiko.etherollmock/");
        Log.e(TAG, "checkExistingWallets: "+walletFolder.exists());
        File[] wallets = walletFolder.listFiles();
        if(wallets != null) {
            createWalletButton.setVisibility(View.GONE);
            for (File file : wallets) {
                Log.e(TAG, "checkExistingWallets: " + file.getName());
                walletFileName.append(file.getName()+"\n");
            }
        }else{
            walletFileName.setText("No wallet file found");
        }
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
                    ToastInTask("Wallet wasn't created successfully",this);
                    HideProgressDialog();
                }else{
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("wallet_path", filePath).apply();
                    ToastInTask("Wallet was created Successfully!",this);
                    HideProgressDialog();
                }
            } catch (CipherException e) {
                e.printStackTrace();
                Log.e(TAG, "CreateWallet: "+e.getMessage());
                HideProgressDialog();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
                Log.e(TAG, "CreateWallet: "+e.getMessage());
                HideProgressDialog();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(TAG, "CreateWallet: "+e.getMessage());
                HideProgressDialog();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                Log.e(TAG, "CreateWallet: "+e.getMessage());
                HideProgressDialog();
            } catch (IOException e) {
                Log.d("io","Invalid file");
                Log.e(TAG, "CreateWallet: "+e.getMessage());
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
                    ToastInTask("No wallets",this);
                }else{
                    Web3jHandler.loadCredentials(passwordEditText.getText().toString(), filePath);
                    Intent EtherollIntent = new Intent(getApplicationContext(), EtherollActivity.class);
                    EtherollIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(EtherollIntent);
                }
                HideProgressDialog();
            } catch (IOException e) {
                e.printStackTrace();
                ToastInTask("Connection Error",this);
                HideProgressDialog();
            } catch (CipherException e) {
                e.printStackTrace();
                ToastInTask("Invalid Password",this);
                HideProgressDialog();
            }
        }).start();
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

    @Override
    protected void onResume() {
        super.onResume();
        walletFileName.setText("");
        checkPermissionsOnStart();
    }
}
