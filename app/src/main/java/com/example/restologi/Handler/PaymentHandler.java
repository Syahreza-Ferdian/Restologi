package com.example.restologi.Handler;

import android.content.Context;
import android.widget.Toast;

import com.example.restologi.BuildConfig;
import com.example.restologi.Domain.TransactionModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class PaymentHandler implements TransactionFinishedCallback {
    public Context context;

    private String currentTransactionAmount;

    public PaymentHandler(Context context) {
        this.context = context;
    }

    public void makePayment() {
        SdkUIFlowBuilder.init()
                .setContext(this.context)
                .setMerchantBaseUrl(BuildConfig.MERCHANT_BASE_URL)
                .setClientKey(BuildConfig.CLIENT_KEY)
                .setTransactionFinishedCallback(this)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#777777","#f77474" , "#3f0d0d"))
                .buildSDK();
    }

    public TransactionRequest transactionRequest(String id, double price, int qty, String itemName, String firstName, String lastName, String email, String phone) {
        TransactionRequest request = new TransactionRequest(UUID.randomUUID().toString(), price * qty);
        ItemDetails details = new ItemDetails(id, price, qty, itemName);

        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
        itemDetails.add(details);
        request.setItemDetails(itemDetails);

        CreditCard creditCard = new CreditCard();
        creditCard.setSaveCard(false);
        creditCard.setAuthentication(CreditCard.RBA);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirstName(firstName);
        customerDetails.setLastName(lastName);
        customerDetails.setEmail(email);
        customerDetails.setPhone(phone);

        request.setCreditCard(creditCard);
        request.setCustomerDetails(customerDetails);
        return request;
    }

    public void saveTransactionToFirebase(String orderId, String amount, String paymentMethod) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("transactions");

        String transactionId = myRef.push().getKey();
        TransactionModel newTransaction = new TransactionModel(orderId, amount, paymentMethod);

        assert transactionId != null;
        myRef.child(transactionId).setValue(newTransaction);
    }

    public void clickPay(PaymentMethod paymentMethod,String productID, double productPrice, int qty, String productName, String firstName, String lastName, String email, String phone) {
        TransactionRequest transactionRequest = transactionRequest(productID, productPrice, qty, productName, firstName, lastName, email, phone);

        this.currentTransactionAmount = String.valueOf(transactionRequest.getAmount());

        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
        MidtransSDK.getInstance().startPaymentUiFlow(this.context, paymentMethod);
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        if (transactionResult.getResponse() != null) {
            switch (transactionResult.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    String transaction_id = transactionResult.getResponse().getTransactionId();
                    String amount = this.currentTransactionAmount;
                    String paymentMethod = transactionResult.getResponse().getPaymentType();

                    this.saveTransactionToFirebase(transaction_id, amount, paymentMethod);

                    Toast.makeText(this.context, "Transaction Sukses " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this.context, "Transaction Pending " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this.context, "Transaction Failed " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
            }
            transactionResult.getResponse().getStatusMessage();
        } else if (transactionResult.isTransactionCanceled()) {
            Toast.makeText(this.context, "Transaction Failed", Toast.LENGTH_LONG).show();
        } else {
            if (transactionResult.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this.context, "Transaction Invalid" + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.context, "Something Wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
}
