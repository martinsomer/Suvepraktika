package ee.viimsifotostuudio.apic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setTitle("Payment");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    public void upload(View v) {
        if (isNetworkAvailable()) {
//            Intent uploader = new Intent(this, Uploader.class);
//            startActivity(uploader);

            Intent paymentView = new Intent(this, PaymentWebView.class);
            startActivity(paymentView);

//            Intent intent = new Intent(
//                    Intent.ACTION_VIEW, Uri.parse(
//                            "https://payment-test.maksekeskus.ee/pay/1/link.html?shopId=d873cd28-f594-4b73-87e3-210484bd5318&amount=" +
//                            String.valueOf(new BigDecimal(
//                                    ((Variables) this.getApplication()).getTotalCopies() * ((Variables) this.getApplication()).getPrice()
//                            ))
//                    )
//            );
//            startActivity(intent);
        } else {
            Toast.makeText(this, "Please check your Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
