package ee.viimsifotostuudio.apic;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Objects;

public class TermsAndConditions extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        setTitle("Terms and Conditions");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        try {
            webView = findViewById(R.id.termsView);
            webView.loadUrl("file:///android_asset/termsandconditions.html");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Terms and Conditions could not be loaded.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
