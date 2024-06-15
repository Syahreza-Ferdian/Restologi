package com.example.restologi.Activity.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.restologi.Activity.BaseActivity;
import com.example.restologi.Activity.MainActivity;
import com.example.restologi.Adapter.CartAdapter;
import com.example.restologi.Handler.CurrencyFormatter;
import com.example.restologi.Handler.ManagmentCart;
import com.example.restologi.Handler.ChangeNumberItemsListener;
import com.example.restologi.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private ManagmentCart managmentCart;

    private double tax, totalFee, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateCart();
        initCartList();

//        binding.checkOutBtn.setOnClickListener(v -> {
//            double
//        });
    }

    private void initCartList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), managmentCart, new ChangeNumberItemsListener(){
            public void change(){
                calculateCart();
            }
        }));
    }

    private void calculateCart() {
//        double percentTax = 0.02;
//        double delivery = 10_000; // 10000 rupiah
//        double tax = (double) Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100;
//        double total = Math.round((managmentCart.getTotalFee() + tax + delivery)) * 100;
//        double itemTotal = (double) Math.round(managmentCart.getTotalFee() * 100) / 100;

        double percentTax = 0.11; // 11% PPN
        double delivery = 10_000; // 10000 rupiah

        this.totalFee = managmentCart.getTotalFee();
        this.tax = Math.round(totalFee * percentTax * 100.0) / 100.0;
//        double itemTotal = Math.round(totalFee * 100.0) / 100.0;
        this.total = totalFee + tax + delivery;

        binding.totalFeeTxt.setText(CurrencyFormatter.formatCurrency(this.totalFee));
        binding.taxTxt.setText(CurrencyFormatter.formatCurrency(this.tax));
        binding.deliveryTxt.setText(CurrencyFormatter.formatCurrency(delivery));
        binding.totalTxt.setText(CurrencyFormatter.formatCurrency(this.total));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this, MainActivity.class));
            }
        });
    }

    public double getTax() {
        return tax;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public double getTotal() {
        return total;
    }
}