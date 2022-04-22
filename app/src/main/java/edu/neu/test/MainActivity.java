package edu.neu.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "d137c858782648f7b8b9e8c87d4de56d";
    private static final String REDIRECT_URI = "comspotifytestsdk://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private final ErrorCallback mErrorCallback = this::logError;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessToken2;
    private Call mCall;


    ImageView a;
    Button b1;
    TextView t1;
    ImageView a2;
    TextView t2;
    ImageView a3;
    TextView t3;
    TextView tt1;
    //ImageView tt2;
    TextView tt100;
    TextView tt101;
    String playlistID ="";

    TextView artist1;
    TextView artist2;
    TextView artist3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        a= findViewById(R.id.imageTest1);
        b1= findViewById(R.id.buttonTest1);
        t1 = findViewById(R.id.textView1);
        artist2 = findViewById(R.id.textViewArtist2);

        a2 =findViewById(R.id.imageTest3);
        t2 = findViewById(R.id.textView4);
        artist3 = findViewById(R.id.textViewartist3);

        a3=findViewById(R.id.imageView2);
        t3= findViewById(R.id.textView3);
        artist1 = findViewById(R.id.textViewArtist1);

        tt1= findViewById(R.id.textView6);

        tt100 = findViewById(R.id.test100);
        tt101 = findViewById(R.id.test101);
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
        //getFeaturedList();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            //tt100.setText("Something: "+mAccessToken);
            getIntent().putExtra("token",mAccessToken);
            getFeaturedList();
        }
    }
    private void getFeaturedList(){
        //Log.e("Good ",mAccessToken);
        //mAccessToken2 = getIntent().getExtras().getString("token");
        if(mAccessToken ==null) {
            return;
        }
        String query = "https://api.spotify.com/v1/browse/featured-playlists?limit=1";
        final Request request = new Request.Builder()
                .url(query)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setResponse(jsonObject.get("playlists").toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void getPlayList(){
        if(mAccessToken == null){
            return;
        }
        String query2 = "https://api.spotify.com/v1/playlists/"+playlistID;
        final Request request2 = new Request.Builder()
                .url(query2)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request2);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setResponse2(jsonObject.get("tracks").toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setResponse(final String text) {
        runOnUiThread(() -> {
            JSONObject object1 = null;
            JSONArray array1 =null;
            JSONObject object2 =null;
            String test1="";
            try {
                object1 = new JSONObject(text);
                //object2 = object1.getString("items");
                test1 = object1.get("items").toString();
                array1 = new JSONArray(test1);
                object2 =array1.getJSONObject(0);
                playlistID = object2.get("id").toString();
                getPlayList();
            }catch (JSONException e){
                e.printStackTrace();
            }
            tt101.setText(playlistID);
        });
    }
    private void setResponse2(final String text){
        runOnUiThread(()->{
            JSONObject object1 =null;
            JSONArray array1 = null;
            JSONObject object2 = null;
            JSONObject object3 = null;
            String test  ="";
            try {
                object1 = new JSONObject(text);
                array1 = object1.getJSONArray("items");
                object2 = array1.getJSONObject(1);
                object3 = object2.getJSONObject("track");
                test = getImageURL(object3);
                Picasso.get().load(test).into(a3);
                t3.setText(getSongName(object3));
                artist1.setText(getArtistName(object3));
                JSONObject object4 = array1.getJSONObject(2);
                Picasso.get().load(getImageURL(object4.getJSONObject("track"))).into(a);
                t1.setText(getSongName(object4.getJSONObject("track")));
                artist2.setText(getArtistName(object4.getJSONObject("track")));
                object4 = array1.getJSONObject(3);
                Picasso.get().load(getImageURL(object4.getJSONObject("track"))).into(a2);
                t2.setText(getSongName(object4.getJSONObject("track")));
                artist3.setText(getArtistName(object4.getJSONObject("track")));


            }catch (JSONException e){
                e.printStackTrace();
            }
           // tt1.setText(test);

        });
    }

    private String getSongName(JSONObject obj1) throws JSONException {
        String test = obj1.get("name").toString();
        return test;
    }
    private  String getURI(JSONObject obj1) throws JSONException{
        return obj1.get("uri").toString();
    }
    private String getPreviewURL(JSONObject obj1) throws JSONException{
        return obj1.get("preview_url").toString();
    }

    private String getArtistName(JSONObject obj1) throws JSONException{
        JSONObject  obj2= obj1.getJSONObject("album");
        JSONArray array1 = obj2.getJSONArray("artists");
        JSONObject obj3 = array1.getJSONObject(0);
        String test = obj3.get("name").toString();
        return test;
    }

    private String getImageURL(JSONObject obj1) throws  JSONException{
        JSONObject object1 = obj1.getJSONObject("album");
        JSONArray array1 = object1.getJSONArray("images");
        JSONObject obj2 = array1.getJSONObject(0);
        String test = obj2.get("url").toString();
        return test;
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }
        private void cancelCall() {
            if (mCall != null) {
                mCall.cancel();
            }
    }
        private void logError(Throwable throwable) {
            Toast.makeText(this, "R.string.err_generic_toast", Toast.LENGTH_SHORT).show();
            Log.e(MainActivity.class.getSimpleName(), "", throwable);
        }
        private Uri getRedirectUri() {
            return Uri.parse(REDIRECT_URI);
        }

}