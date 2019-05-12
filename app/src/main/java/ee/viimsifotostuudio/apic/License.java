package ee.viimsifotostuudio.apic;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class License extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        setTitle("License");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        try {
            webView = findViewById(R.id.licenseView);
            webView.loadUrl("file:///android_asset/license.html");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "License could not be loaded.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
