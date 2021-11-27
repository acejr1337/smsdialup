package chace.smsbackend.hbrowser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.dial_up.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Website extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        List<String> htmlContents = MainActivity.htmlContents;

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : htmlContents) {
            stringBuilder.append(s);
        }
        String sms = stringBuilder.toString();
        this.loadHTML(sms);
    }

    public void loadHTML(String sms) {

        String html = sms.
                replace("Ξ", "<input")
                .replace("Φ", "<a href=\"")
                .replace("Ω","</a>")
                .replace("Ψ", "<form>")
                .replace("Θ","</form>")
                .replace("Σ","\">")
                .replace("Π",">")
                .replace("Γ","=\"submit\"")
                .replace("ß","=\"hidden\"")
                .replace("æ","name=\"")
                .replace("Δ","type=\"")
                .replace("_","value=\"")
                .replace(" t "," the ")
                .replace(" & ", " and ")
                .replace(" h ", " that ")
                .replace(" w ", " with ");

        WebView webView = findViewById(R.id.website);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                String clickedUrl = webView.getUrl();
                webView.clearFormData();
                Toast.makeText(getApplicationContext(), "Loading: " + clickedUrl, Toast.LENGTH_SHORT).show();
                /*SmsManager.getDefault().sendTextMessage("+61474343871", null, url, null, null);

                String html = sms.
                        replace("Ξ", "<input")
                        .replace("Φ", "<a href=\"")
                        .replace("Ω","</a>")
                        .replace("Ψ", "<form>")
                        .replace("Θ","</form>")
                        .replace("Σ","\">")
                        .replace("Π",">")
                        .replace("Γ","=\"submit\"")
                        .replace("ß","=\"hidden\"")
                        .replace("æ","name=\"")
                        .replace("Δ","type=\"")
                        .replace("_","value=\"")
                        .replace(" t "," the ")
                        .replace(" & ", " and ")
                        .replace(" h ", " that ")
                        .replace(" w ", " with ");
                // Load up the website!
                webView.loadData(html, "text/html", "UTF-8");*/
                return true;
            }
        });
        webView.loadData(html, "text/html", "UTF-8");
    }
}
