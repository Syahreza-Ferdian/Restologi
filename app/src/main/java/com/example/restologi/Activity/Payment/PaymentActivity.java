package com.example.restologi.Activity.Payment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restologi.Handler.PaymentHandler;
import com.example.restologi.R;
import com.midtrans.sdk.corekit.core.PaymentMethod;

public class PaymentActivity extends AppCompatActivity {
    private RadioButton gopay_radio_btn;
    private RadioButton shopeepay_radio_btn;
    private RadioButton credit_card_radio_btn;
    private RadioButton bca_radio_btn;
    private RadioButton bri_radio_btn;
    private LinearLayout gopay_card;
    private LinearLayout shopeepay_card;
    private LinearLayout cc_card;
    private LinearLayout bca_card;
    private LinearLayout bri_card;
    private Button btn_continue;

    private PaymentMethod selected_payment_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        PaymentHandler paymentHandler = new PaymentHandler(PaymentActivity.this);
        paymentHandler.makePayment();

        credit_card_radio_btn = findViewById(R.id.cc_radio);
        gopay_radio_btn = findViewById(R.id.gopay_radio);
        bca_radio_btn = findViewById(R.id.bca_radio);
        bri_radio_btn = findViewById(R.id.bri_radio);
        shopeepay_radio_btn = findViewById(R.id.shopeepay_radio);

        cc_card = findViewById(R.id.cc_pay_card);
        gopay_card = findViewById(R.id.gopay_pay_card);
        bca_card = findViewById(R.id.bca_pay_card);
        bri_card = findViewById(R.id.bri_card);
        shopeepay_card = findViewById(R.id.shopeepay_card);

        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(v -> {
            if (selected_payment_method == null) {
                Toast.makeText(this, "Pilih salah satu metode pembayaran!", Toast.LENGTH_LONG).show();
                return;
            }
            paymentHandler.clickPay(selected_payment_method, "10", 15000, 1, "Ayam", "Syahreza", "Ferdian", "syahrezaferdian@gmail.com", "0895414949161");
        });

        View.OnClickListener cc_on_click_listener = v -> {
            credit_card_radio_btn.setChecked(true);
            gopay_radio_btn.setChecked(false);
            bca_radio_btn.setChecked(false);
            bri_radio_btn.setChecked(false);
            shopeepay_radio_btn.setChecked(false);

            cc_card.setBackgroundResource(R.drawable.payment_opt_card_item_selected);
            gopay_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bca_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bri_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            shopeepay_card.setBackgroundResource(R.drawable.payment_opt_card_item);

            selected_payment_method = PaymentMethod.CREDIT_CARD;
        };

        View.OnClickListener gopay_on_click_listener = v -> {
            credit_card_radio_btn.setChecked(false);
            gopay_radio_btn.setChecked(true);
            bca_radio_btn.setChecked(false);
            bri_radio_btn.setChecked(false);
            shopeepay_radio_btn.setChecked(false);

            cc_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            gopay_card.setBackgroundResource(R.drawable.payment_opt_card_item_selected);
            bca_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bri_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            shopeepay_card.setBackgroundResource(R.drawable.payment_opt_card_item);

            selected_payment_method = PaymentMethod.GO_PAY;
        };

        View.OnClickListener bca_on_click_listener = v -> {
            credit_card_radio_btn.setChecked(false);
            gopay_radio_btn.setChecked(false);
            bca_radio_btn.setChecked(true);
            bri_radio_btn.setChecked(false);
            shopeepay_radio_btn.setChecked(false);

            cc_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            gopay_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bca_card.setBackgroundResource(R.drawable.payment_opt_card_item_selected);
            bri_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            shopeepay_card.setBackgroundResource(R.drawable.payment_opt_card_item);

            selected_payment_method = PaymentMethod.BANK_TRANSFER_BCA;
        };

        View.OnClickListener bri_on_click_listener = v -> {
            credit_card_radio_btn.setChecked(false);
            gopay_radio_btn.setChecked(false);
            bca_radio_btn.setChecked(false);
            bri_radio_btn.setChecked(true);
            shopeepay_radio_btn.setChecked(false);

            cc_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            gopay_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bca_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bri_card.setBackgroundResource(R.drawable.payment_opt_card_item_selected);
            shopeepay_card.setBackgroundResource(R.drawable.payment_opt_card_item);

            selected_payment_method = PaymentMethod.BANK_TRANSFER_BRI;
        };

        View.OnClickListener shopeepay_on_click_listener = v -> {
            credit_card_radio_btn.setChecked(false);
            gopay_radio_btn.setChecked(false);
            bca_radio_btn.setChecked(false);
            bri_radio_btn.setChecked(false);
            shopeepay_radio_btn.setChecked(true);

            cc_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            gopay_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bca_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            bri_card.setBackgroundResource(R.drawable.payment_opt_card_item);
            shopeepay_card.setBackgroundResource(R.drawable.payment_opt_card_item_selected);

            selected_payment_method = PaymentMethod.SHOPEEPAY;
        };

        cc_card.setOnClickListener(cc_on_click_listener);
        credit_card_radio_btn.setOnClickListener(cc_on_click_listener);

        gopay_card.setOnClickListener(gopay_on_click_listener);
        gopay_radio_btn.setOnClickListener(gopay_on_click_listener);

        bca_card.setOnClickListener(bca_on_click_listener);
        bca_radio_btn.setOnClickListener(bca_on_click_listener);

        bri_card.setOnClickListener(bri_on_click_listener);
        bri_radio_btn.setOnClickListener(bri_on_click_listener);

        shopeepay_card.setOnClickListener(shopeepay_on_click_listener);
        shopeepay_radio_btn.setOnClickListener(shopeepay_on_click_listener);
    }
}