package com.isaiko.etheroll.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.isaiko.etheroll.R;
import com.isaiko.etheroll.ViewModel.EtherollViewModel;
import com.isaiko.etheroll.utils.Web3jHandler;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EtherollActivity extends AppCompatActivity {
    private final String TAG = "EtherollActivity";
    @BindView(R.id.tv_wallet_address)
    TextView walletAddressTextView;
    @BindView(R.id.tv_balance)
    TextView balanceTextView;
    @BindView(R.id.sb_chance_winning)
    SeekBar chanceOfWinningSeekBar;
    @BindView(R.id.et_bet_size)
    EditText betSizeEditText;
    @BindView(R.id.et_chance_winning)
    EditText chanceWinningEditText;
    @BindView(R.id.tv_wager_number)
    TextView wagerNumberTextView;
    @BindView(R.id.tv_wager)
    TextView bidValueTextView;
    @BindView(R.id.tv_profit)
    TextView winningValueTextView;
    @BindView(R.id.btn_roll)
    TextView rollButton;
    @BindView(R.id.tv_min_bet_size)
    TextView minBetSizeTextView;
    @BindView(R.id.tv_max_profit)
    TextView maxProfitTextView;
    @BindView(R.id.tv_max_profit_warning)
    TextView maxProfitExceededWarning;
    EtherollViewModel etherollViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etheroll);
        ButterKnife.bind(this);

        etherollViewModel = ViewModelProviders.of(this).get(EtherollViewModel.class);
        walletAddressTextView.setText(Web3jHandler.getWalletAddress());
        try {
            balanceTextView.setText(String.valueOf(Web3jHandler.getEtherBalance()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Web3jHandler.initEtheroll();
            Log.d(TAG, "Contract was loaded successfully!");
            minBetSizeTextView.setText(Convert.fromWei(etherollViewModel.getMinBet().toString(), Convert.Unit.ETHER).toString()+" eth");
            maxProfitTextView.setText(Convert.fromWei(etherollViewModel.getMaxProfit().toString(), Convert.Unit.ETHER).toString()+" eth");
        } catch (Exception e) {
            Log.d(TAG,"Problem Loading etheroll contract "+e.getMessage());
        }
        chanceOfWinningSeekBar.setProgress(0);
        chanceOfWinningSeekBar.setMax(98);
        betSizeEditText.setText(Convert.fromWei(etherollViewModel.getMinBet().toString(), Convert.Unit.ETHER).toString());
        chanceWinningEditText.setText(String.valueOf(chanceOfWinningSeekBar.getProgress()+1));
        bidValueTextView.setText(betSizeEditText.getText().toString());

        chanceOfWinningSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                chanceWinningEditText.setText(String.valueOf(progress));
                wagerNumberTextView.setText(String.valueOf(Integer.valueOf(chanceWinningEditText.getText().toString())+1));
                winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(double)(progress)/100).toPlainString());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        betSizeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() != 0 && !s.toString().equals("") && !s.toString().equals(".") && !s.toString().endsWith(".")){
                    //if bet value is less than min bet set it to min bet
                    if(Convert.toWei(s.toString(), Convert.Unit.ETHER).toBigInteger().compareTo(etherollViewModel.getMinBet()) == -1)
                        betSizeEditText.setText(Convert.fromWei(etherollViewModel.getMinBet().toString(), Convert.Unit.ETHER).toString());
                    else{
                        bidValueTextView.setText(s);
                        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chanceWinningEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() != 0){
                    if(Integer.valueOf(s.toString())>98)
                        chanceWinningEditText.setText(String.valueOf(98));
                    else if(Integer.valueOf(s.toString())<1)
                        chanceWinningEditText.setText(String.valueOf(1));
                    chanceWinningEditText.setSelection(chanceWinningEditText.getText().length());
                    chanceOfWinningSeekBar.setProgress(Float.valueOf(chanceWinningEditText.getText().toString()).intValue());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        walletAddressTextView.setOnLongClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("address",walletAddressTextView.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(EtherollActivity.this, "Address was copied to clipboard!", Toast.LENGTH_SHORT).show();
            return true;
        });

        wagerNumberTextView.setText(String.valueOf(Integer.valueOf(chanceWinningEditText.getText().toString())+1));


       // winningValueTextView.setText(String.valueOf(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),Integer.valueOf(wagerNumberTextView.getText().toString()))));

    }

    private BigDecimal calculateProfit(BigInteger betValue, Double roll){
        BigDecimal pureProfit, profit ;
        try{
            pureProfit = BigDecimal.valueOf(betValue.doubleValue() * (1-roll)/roll);
            profit = pureProfit.subtract(pureProfit.add(BigDecimal.valueOf(betValue.longValue())).divide(BigDecimal.valueOf(100)));
            if(profit.compareTo(BigDecimal.valueOf(etherollViewModel.getMaxProfit().doubleValue())) == 1)
            {
                Log.d(TAG, "Max Profit reached");
                profit = BigDecimal.valueOf(etherollViewModel.getMaxProfit().doubleValue());
                maxProfitExceededWarning.setVisibility(View.VISIBLE);
                rollButton.setEnabled(false);
            }else{
                maxProfitExceededWarning.setVisibility(View.GONE);
                rollButton.setEnabled(true);
            }
          //profit = profit
          Log.d(TAG, profit.toString());
          return Convert.fromWei(profit.toString(), Convert.Unit.ETHER);
        }catch (ArithmeticException e){
            Log.d("profit",e.getMessage()+ " " + betValue + " " + roll);
            return BigDecimal.valueOf(0);
        }catch(NumberFormatException e){
            Log.d(TAG, "Number format exception");
            return Convert.fromWei(etherollViewModel.getMaxProfit().toString(), Convert.Unit.ETHER);
        }
    }

    @OnClick(R.id.btn_min)
    public void minBet(){
        betSizeEditText.setText(Convert.fromWei(etherollViewModel.getMinBet().toString(), Convert.Unit.ETHER).toString());
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_0_5)
    public void halfBet(){
        betSizeEditText.setText(String.valueOf(0.5f));
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_1)
    public void oneBet(){
        betSizeEditText.setText(String.valueOf(1.0f));
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_2)
    public void twoBet(){
        betSizeEditText.setText(String.valueOf(2.0f));
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_5)
    public void fiveBet(){
        betSizeEditText.setText(String.valueOf(5.0f));
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_10)
    public void tenBet(){
        betSizeEditText.setText(String.valueOf(10.0f));
        winningValueTextView.setText(calculateProfit(Convert.toWei(bidValueTextView.getText().toString(), Convert.Unit.ETHER).toBigInteger(),(Double.valueOf(wagerNumberTextView.getText().toString())-1)/100).toPlainString());
    }

    @OnClick(R.id.btn_roll)
    public void rollButton(){
        BigInteger rollUnder = BigInteger.valueOf(Long.valueOf(chanceWinningEditText.getText().toString()));
        BigInteger weiValue = Convert.toWei(betSizeEditText.getText().toString(), Convert.Unit.ETHER).toBigInteger();
        Log.e(TAG, "rollButton: "+rollUnder+" "+weiValue);
        etherollViewModel.playerRollDice(rollUnder, weiValue,this);
    }
}
