package ee.viimsifotostuudio.apic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.math.BigDecimal;

public class PaymentWebView extends AppCompatActivity {

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        myWebView = findViewById(R.id.paymentView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.requestFocus(View.FOCUS_DOWN);
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.loadUrl(
                "https://payment-test.maksekeskus.ee/pay/1/link.html?shopId=d873cd28-f594-4b73-87e3-210484bd5318&amount=" +
                String.valueOf(new BigDecimal(
                    ((Variables) this.getApplication()).getTotalCopies() * ((Variables) this.getApplication()).getPrice())
                ) +
                "&reference=REF_TEST&country=ee&locale=et");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "Finished loading:" + url);
                super.onPageFinished(view, url);

                if(url.equals("http://payment.getapic.ee/success.php")) {
                    // start success activity and upload
                } else {
                    // start failure activity
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("Clicked on:" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
