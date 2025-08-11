package com.example.f1_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class insideWeb extends AppCompatActivity {
    WebView webView;
    GoogleSignInClient googleSignInClient;
    String baseUrl = "https://cf331c205643.ngrok-free.app/";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inside_web);
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.swipeRefresh), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                if (url.equals(baseUrl)) {
                    FirebaseAuth.getInstance().signOut();

                    googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(insideWeb.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(insideWeb.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                    });
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> {
                    if (request.getResources() != null) {
                        request.grant(request.getResources());
                    }
                });
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }
    }

}
