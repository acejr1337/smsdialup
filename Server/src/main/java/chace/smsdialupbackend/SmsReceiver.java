package chace.smsdialupbackend;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import androidx.annotation.RequiresApi;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Chace Zwagerman 11/23/2021.
 */

/**
 * TODO: We need to fix that some messages may not come through in order and that will be bad. A simple way this can be fixed is by
 * appending a prefix to each part of the message and checking if it was sent and if not retrieve that message and send it back to them,
 * then the client app will need to rebuild that data in the proper order.
 *
 * Another thing is get hyperlinks to work properly.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);

                final String mobileNumber = msgs[i].getOriginatingAddress();
                final String receivedMessage = msgs[i].getMessageBody();

                try {
                    /**
                     * You can define certain messages that are received and send back a response!
                     * Give it a try :D
                     */
                    if (receivedMessage.equalsIgnoreCase("$ping")) {
                        sendSMS(mobileNumber, "pong");
                        return;
                    }
                    List<String> tlds = new ArrayList<>();

                    tlds.add(".com");tlds.add(".net");tlds.add(".org");tlds.add(".gov");tlds.add(".au");tlds.add(".nz");tlds.add(".co");
                    tlds.add(".me");tlds.add(".de");tlds.add(".se");tlds.add(".uk");tlds.add(".ca");tlds.add(".network");tlds.add(".life");tlds.add(".shop");
                    tlds.add(".il");tlds.add(".gr");tlds.add(".eu");tlds.add(".kr");tlds.add(".kp");tlds.add(".in");tlds.add(".info");tlds.add(".ai");
                    tlds.add(".xyz");tlds.add(".io");tlds.add(".top"); tlds.add(".pro"); tlds.add(".pw"); tlds.add(".club"); tlds.add(".cc"); tlds.add(".tech");
                    tlds.add(".tv"); tlds.add(".biz"); tlds.add(".online"); tlds.add(".biz"); tlds.add(".gg"); tlds.add(".store"); tlds.add(".work"); tlds.add(".ru");
                    tlds.add(".it"); tlds.add(".cloud"); tlds.add(".es"); tlds.add(".live");

                    List<String> protcols = Arrays.asList("http://", "https://");
                    // fix it sends it heaps of times.
                    if (tlds.stream().anyMatch(receivedMessage::contains)) {
                        if (protcols.stream().anyMatch(receivedMessage::contains)) {
                            runAsync(receivedMessage, mobileNumber);
                        } else {
                            runAsync("http://" + receivedMessage, mobileNumber);
                        }
                    } else {
                        runAsync("http://frogfind.com/?q=" + receivedMessage, mobileNumber);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * My perfected method for retrieving specific data.
     * @param url
     * @throws Exception
     */
    public static void runAsync(String url, String number) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                @SuppressWarnings("deprecation")
                Whitelist whitelist = Whitelist.relaxed().
                        addAttributes("input", "type").
                        addAttributes("input", "value").
                        addAttributes("input", "name").
                        addAttributes("a", "href").
                        addTags("a","input","form").
                        removeAttributes("img", "tag").
                        removeTags("img").removeTags("logo").
                        removeAttributes("div", "logo").
                        removeTags("policies", "hidden").
                        removeAttributes("input", "hidden").
                        removeAttributes("input", "undefined").
                        removeAttributes("input", "policies");

                String rp = response.body().string();
                String filteredResponseFirstLayer = Jsoup.clean(rp, whitelist);

                String filteredResponse = filteredResponseFirstLayer
                        .replace("<input", "Ξ")
                        .replace("<a href=\"", "Φ")
                        .replace("</a>", "Ω")
                        .replace("<form>", "Ψ")
                        .replace("</form>", "Θ")
                        .replace("\">", "Σ")
                        .replace(">", "Π")
                        .replace("=\"submit\"", "Γ")
                        .replace("=\"hidden\"", "ß")
                        .replace("name=\"", "æ")
                        .replace("type=\"", "Δ")
                        .replace("value=\"", "_")
                        .replace(" the ", " t ")
                        .replace(" and ", " & ")
                        .replace(" that ", " h ")
                        .replace(" with ", " w ");

                List<String> parts = split(filteredResponse, 160);

                sendSMS(number, "✳✳✳START_OF_HTML✳✳✳");

                parts.forEach(part -> sendSMS(number, part));

                sendSMS(number, "✳✳✳END_OF_HTML✳✳✳");
            }
        });
    }

    /**
     * Takes in text and splits it.
     * @param text
     * @param size
     * @return
     */
    public static List<String> split(String text, int size) {
        // Get rid of all newlines because we don't need
        // them and they waste precious bytes.
        text = text.replace("\n", "");

        // Give the list the right capacity to start with.
        // You could use an array instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    /**
     * Sends a message to a phone number.
     * @param number
     * @param message
     */
    public static void sendSMS(String number, String message) {
        SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
    }

    /**
     * Sends multiple messages to a phone number.
     * @param number
     * @param messages
     */
    public static void sendSMS(String number, String[] messages) {
        for (String m : messages) {
            SmsManager.getDefault().sendTextMessage(number, null, m, null, null);
        }
    }
}