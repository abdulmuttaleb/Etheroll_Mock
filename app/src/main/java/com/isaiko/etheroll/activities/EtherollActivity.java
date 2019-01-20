package com.isaiko.etheroll.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.isaiko.etheroll.R;
import com.isaiko.etheroll.utils.Web3jHandler;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EtherollActivity extends AppCompatActivity {

    @BindView(R.id.tv_wallet_address)
    TextView walletAddressTextView;
    @BindView(R.id.tv_balance)
    TextView balanceTextView;
    @BindView(R.id.sb_1)
    SeekBar value1SeekBar;
    @BindView(R.id.sb_2)
    SeekBar value2SeekBar;
    @BindView(R.id.tv_slider_1)
    TextView slider1ValueTextView;
    @BindView(R.id.tv_slider_2)
    TextView slider2ValueTextView;
    @BindView(R.id.tv_bid_value)
    TextView bidValueTextView;
    @BindView(R.id.tv_winning_value)
    TextView winningValueTextView;
    @BindView(R.id.btn_bid)
    TextView bidButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etheroll);
        ButterKnife.bind(this);
        walletAddressTextView.setText(Web3jHandler.getWalletAddress());
        try {
            balanceTextView.setText(String.valueOf(Web3jHandler.getEtherBalance()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value1SeekBar.setProgress(0);
        value1SeekBar.setMax(10);
        value2SeekBar.setProgress(0);
        value2SeekBar.setMax(10);
        slider1ValueTextView.setText(String.valueOf(Float.valueOf(value1SeekBar.getProgress()/10.0f)));
        slider2ValueTextView.setText(String.valueOf(value2SeekBar.getProgress() * 10));

        value1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                slider1ValueTextView.setText(String.valueOf(Float.valueOf(progress/10.0f)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        value2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                slider2ValueTextView.setText(String.valueOf(progress * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        walletAddressTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("address",walletAddressTextView.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(EtherollActivity.this, "Address was copied to clipboard!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
