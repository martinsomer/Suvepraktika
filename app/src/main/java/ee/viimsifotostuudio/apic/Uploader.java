package ee.viimsifotostuudio.apic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okio.BufferedSink;
import okio.Okio;

//TODO everything
// HTTP client https://github.com/square/okhttp
public class Uploader extends AppCompatActivity {

    final private static String SERVER_URL = "https://mockbin.org/request";
    ProgressBar progressBar;
    private Handler handler;

    static class HttpResponse {
        int code;
        String body;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);

        setTitle("Let's upload your picture");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        handler = new Handler(this.getApplicationContext().getMainLooper());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        Button uploadBtn = findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UploadImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 2);
            } else {
                UploadImage();
            }
        } else {
            UploadImage();
        }
    }

    private String GetFilePath() {
        int counter = 0;
        if (!Environment.getExternalStorageDirectory().exists())
            return null;

        String root = Environment.getExternalStorageDirectory().toString();
        String folder = root + "/" + getApplicationContext().getResources().getString(R.string.app_name);

        File myDir = new File(folder);
        if (!myDir.mkdirs())
            return null;

        String filename = null;

        while (counter < Integer.MAX_VALUE) {
            filename = String.format(Locale.getDefault(), "%1/image_%2$05d.jpg", folder, counter);
            File f = new File(filename);
            if (!f.exists()) {
                break;
            }
            counter++;
        }

        return filename;
    }

    private void UploadImage() {
        final Variables vars = (Variables) getApplication();
        Bitmap img = vars.getFilterImage();

        //String filename = GetFilePath();
        //if (filename.isEmpty())
        //    return false;

        //FileOutputStream outStream = new FileOutputStream(filename);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 90, outStream);

        ArrayList<ConnectionSpec> cs = new ArrayList<>();

        cs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .allEnabledCipherSuites()
                .build());
        cs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.SSL_3_0)
                .allEnabledCipherSuites()
                .build());

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionSpecs(cs);

        if (BuildConfig.DEBUG) {
            try {
                X509TrustManager trustMgr = new OverlyTrustingTrustManager();
                HostnameVerifier hostVerifier = new OverlyTrustyHostnameVerifier();

                final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                //final SSLContext sslContext = SSLContext.getInstance("SSL");
                //final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new X509TrustManager[]{trustMgr}, new java.security.SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                httpClientBuilder.sslSocketFactory(sslSocketFactory, trustMgr)
                        .hostnameVerifier(hostVerifier);

            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to set up \"secure\" connection", Toast.LENGTH_SHORT).show();
            }
        }

        final OkHttpClient httpClient = httpClientBuilder.build();

        final RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("firstName", vars.getFirstName())
                .addFormDataPart("lastName", vars.getLastName())
                .addFormDataPart("email", vars.getEMail())
                .addFormDataPart("phone", vars.getPhone())
                .addFormDataPart("address", vars.getAddress())
                .addFormDataPart("copies", String.valueOf(vars.getNumberOfCopies()))
                /* Set file name so PHP knows to put it in $_FILES */
                .addFormDataPart("filterImage", "pic.jpg",
                        new ProgressingRequestBody(MediaType.parse("image/jpeg"), outStream, new ProgressingRequestBody.ProgressListener() {
                            @Override
                            public void transferred(long total, long length) {
                                final int progress = (int)(total / (float)length * 100);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setIndeterminate(false);
                                        progressBar.setProgress(progress);
                                    }
                                });
                            }
                        }))
                .build();

        final Request req = new Request.Builder()
                .url(SERVER_URL)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();

        final UploadTask asyncTask = new UploadTask();
        asyncTask.setProgressBar(progressBar);
        asyncTask.execute(httpClient, req, this.getApplicationContext());

    }

    class UploadTask extends AsyncTask<Object, Void, HttpResponse> {

        Context ctx;
        ProgressBar progressBar;

        public void setProgressBar(ProgressBar bar) {
            progressBar = bar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected HttpResponse doInBackground(Object... params) {
            try {
                OkHttpClient httpClient = (OkHttpClient) params[0];
                Request req = (Request) params[1];
                ctx = (Context) params[2];

                Response response = httpClient.newCall(req).execute();
                //if (!response.isSuccessful()) {
                Log.d(Uploader.class.getSimpleName(), "Successful: " + response.isSuccessful());
                //   return null;
                //}

                //publishProgress();

                HttpResponse httpResp = new HttpResponse();
                // Eats the response and closes connection
                httpResp.setBody(response.body().string());
                httpResp.setCode(response.code());

                Log.d(Uploader.class.getSimpleName(), httpResp.getBody());
                return httpResp;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse res) {
            super.onPostExecute(res);
            JSONObject json;

            if (progressBar != null) {
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
            }

            if (res != null && res.getCode() == 200) {
                try {
                    json = new JSONObject(res.getBody());
                    ((Variables) ctx).setPaymentToken(json.getString("token"));
                    Intent payment = new Intent(ctx, Payment.class);
                    ctx.startActivity(payment);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,
                            "Problem parsing server response. Try again later.",
                            Toast.LENGTH_SHORT).show();
                }

            }

            Toast.makeText(ctx,
                    "Error occurred while contacting the server. Try again later.",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class OverlyTrustyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    // Ignores all certificate errors, for debugging
    static class OverlyTrustingTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    static class ProgressingRequestBody extends RequestBody {
        private ByteArrayInputStream source;
        private MediaType contentType;
        private long byteCount;
        private ProgressListener listener;

        public ProgressingRequestBody(final MediaType type, final ByteArrayOutputStream bytes, final ProgressListener listener) {
            this.contentType = type;
            this.source = new ByteArrayInputStream(bytes.toByteArray());
            this.byteCount = source.available();
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() {
            return byteCount;
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            long read = 0;
            long total = 0;
            okio.Source reader = Okio.source(source);

            while ((read = reader.read(sink.buffer(), 1024)) != -1) {
                total += read;
                sink.flush();
                listener.transferred(total, byteCount);
            }
        }

        public interface ProgressListener {
            void transferred(long total, long length);
        }
    }
}
