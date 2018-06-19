package ee.viimsifotostuudio.apic;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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

import okhttp3.Call;
import okhttp3.Callback;
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

// HTTP client https://github.com/square/okhttp
public class Uploader extends AppCompatActivity {

    final private static String SERVER_URL = "https://kalmerr.planet.ee/apic/";
    final public static String NOTIFICATION_UPLOADING = "uploading";
    final public static String NOTIFICATION_COMPLETED = "upload_complete";

    ProgressBar progressBar;
    NotificationManager mNotificationManager;
    Notification.Builder mNotiBuilder;

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

        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        Button uploadBtn = findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission();
            }
        });

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotiBuilder = new Notification.Builder(getApplicationContext())
                //.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                //.setWhen(System.currentTimeMillis())
                .setContentTitle("Äpic")
                .setContentText("Äpic picture uploader")
                .setContentInfo("It will be uploaded")
                .setOnlyAlertOnce(false)
                //.setProgress(100, 0, true)
                .setSmallIcon(R.mipmap.ic_launcher)
        //.setLargeIcon(R.mipmap.ic_launcher_round)
        ;

        /*if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for(NotificationChannel n :  mNotificationManager.getNotificationChannels()){
                mNotificationManager.deleteNotificationChannel(n.getId());
            }
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc1 = new NotificationChannel(NOTIFICATION_UPLOADING, "Upload in progress", NotificationManager.IMPORTANCE_LOW);

            // Configure the notification channel.
            nc1.setDescription("Upload in progress");
            nc1.enableLights(false);
            nc1.setSound(null, null);
            //nc1.setLightColor(Color.BLUE);
            nc1.setVibrationPattern(new long[]{0});
            nc1.enableVibration(false);

            NotificationChannel nc2 = new NotificationChannel(NOTIFICATION_COMPLETED, "Upload completed", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            nc2.setDescription("Uploading has been completed");
            nc2.enableLights(true);
            nc2.setLightColor(Color.GREEN);
            nc2.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            nc2.enableVibration(true);
            mNotificationManager.createNotificationChannels(Arrays.asList(nc1, nc2));

            mNotiBuilder.setChannelId(NOTIFICATION_UPLOADING);
        } else
            mNotiBuilder.setPriority(Notification.PRIORITY_DEFAULT);

        if (BuildConfig.DEBUG) {
            //Notification m = mNotiBuilder.build();
            //mNotificationManager.notify(1, m);
        }
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
            filename = String.format(Locale.getDefault(), "%1s/image_%2$05d.jpg", folder, counter);
            File f = new File(filename);
            if (!f.exists()) {
                break;
            }
            counter++;
        }

        return filename;
    }

    private void UploadImage() {
        //mNotiBuilder.setProgress(100, 0, true);
        //mNotificationManager.notify(1, mNotiBuilder.build());

        final Variables vars = (Variables) getApplication();
        //Bitmap img = vars.getFilterImageArray(0);

        //String filename = GetFilePath();
        //if (filename.isEmpty())
        //    return false;

        //FileOutputStream outStream = new FileOutputStream(filename);
        //ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //img.compress(Bitmap.CompressFormat.JPEG, 90, outStream);

        ArrayList<ConnectionSpec> cs = new ArrayList<>();

        cs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .allEnabledCipherSuites()
                .build());
        cs.add(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.SSL_3_0)
                .allEnabledCipherSuites()
                .build());

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
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
                sslContext.init(null, new X509TrustManager[]{trustMgr}, new SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                httpBuilder.sslSocketFactory(sslSocketFactory, trustMgr)
                        .hostnameVerifier(hostVerifier);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }

        final OkHttpClient httpClient = httpBuilder.build();

        final MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("firstName", vars.getFirstName())
                .addFormDataPart("lastName", vars.getLastName())
                .addFormDataPart("email", vars.getEMail())
                .addFormDataPart("phone", vars.getPhone())
                .addFormDataPart("address", vars.getAddress());
        for (int i = 0; i<vars.getFilterImageArrayLength(); i++) {
            Bitmap img = vars.getFilterImageArray(i);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 90, outStream);

            body.addFormDataPart(String.format(Locale.getDefault(), "copies[%1$d]", i), String.valueOf(vars.getFilterImageQuantity(i)))
                    .addFormDataPart(String.format(Locale.getDefault(), "filterImage[%1$d]", i), "pic.jpg",

                            //.addFormDataPart("copies", String.valueOf(vars.getFilterImageQuantity(0)))
                            /* Set file name so PHP knows to put it in $_FILES */
                            //.addFormDataPart("filterImage", "pic.jpg",
                            new ProgressingRequestBody(MediaType.parse("image/jpeg"), outStream, new ProgressingRequestBody.ProgressListener() {
                                //@Override
                                public void transferred(long total, long length) {
                                    mNotiBuilder.setProgress(100, (int) (total / (float) length * 100), false);
                                    mNotiBuilder.setOngoing(false)
                                            .setAutoCancel(true)
                                            .setContentInfo("Uploading pic %d of %d");
                                    mNotificationManager.notify(0, mNotiBuilder.build());
                                }
                            }));
        }
        //.build();




        final Request req = new Request.Builder()
                .url(SERVER_URL)
                .addHeader("Accept", "application/json")
                .post(body.build())
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotiBuilder.setChannelId(NOTIFICATION_UPLOADING);
        } else {
            mNotiBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        httpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(),
                        "Error occurred while contacting the server. Try again later.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Uploader", "In onResponse");

                mNotiBuilder.setAutoCancel(true)
                        .setProgress(0, 0, false)
                        .setContentText("Upload completed!")
                        .setOngoing(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("Uploader", "Set NotificationChannel to " + NOTIFICATION_COMPLETED);
                    mNotiBuilder.setChannelId(NOTIFICATION_COMPLETED);
                } else {
                    mNotiBuilder.setPriority(Notification.PRIORITY_HIGH);
                }

                mNotificationManager.notify(1, mNotiBuilder.build());

                JSONObject json;
                Context ctx = getApplicationContext();

                if (response.code() == 200) {
                    try {
                        json = new JSONObject(response.body().string());
                        ((Variables) ctx).setPaymentToken(json.getString("token"));
                        Intent payment = new Intent(ctx, Payment.class);
                        startActivity(payment);
                        return;
                    } catch (NullPointerException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(ctx,
                        "Problem parsing server response. Try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    class OverlyTrustyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    // Ignores all certificate errors, for debugging
    class OverlyTrustingTrustManager implements X509TrustManager {
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

