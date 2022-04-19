package edu.neu.test;

import androidx.appcompat.app.AppCompatActivity;

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

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "d137c858782648f7b8b9e8c87d4de56d";
    private static final String REDIRECT_URI = "comspotifytestsdk://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private final ErrorCallback mErrorCallback = this::logError;


    ImageView a;
    Button b1;
    TextView t1;
    ImageView a2;
    TextView t2;
    ImageView a3;
    TextView t3;
    TextView tt1;
    ImageView tt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        a= findViewById(R.id.imageTest1);
        b1= findViewById(R.id.buttonTest1);
        t1 = findViewById(R.id.textView1);

        a2 =findViewById(R.id.imageTest3);
        t2 = findViewById(R.id.textView4);

        a3=findViewById(R.id.imageView2);
        t3= findViewById(R.id.textView3);

        tt1= findViewById(R.id.textView6);
        tt2= findViewById(R.id.test001);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        Toast.makeText(getApplicationContext(),"Good!",Toast.LENGTH_LONG).show();

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        // Then we will write some more code here.
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;

                    if (track != null) {
                        Log.d("MainActivity", " "+track.name + " by " + track.artist.name);
                        Toast.makeText(getApplicationContext(),("MainActivity"+" "+track.name + " by " + track.artist.name),Toast.LENGTH_LONG).show();



                        mSpotifyAppRemote
                                .getContentApi()
                                .getRecommendedContentItems("Default")
                                .setResultCallback(data -> {
                                    Log.i("MainActivity", "getRecommendedContentItems="+data.items[1].uri);
                                    mSpotifyAppRemote.getContentApi().getChildrenOfItem(data.items[2],5,0 ).setResultCallback(data1 ->{
                                        tt1.setText(data.items[2].title);
                                        Log.d("Good:", (Integer.toString(data.total)+"vs"+ Integer.toString(data1.total)));
                                        mSpotifyAppRemote.getContentApi().getChildrenOfItem(data1.items[0],5,0 ).setResultCallback(data2 -> {
                                            mSpotifyAppRemote.getImagesApi()
                                                    .getImage(data2.items[0].imageUri,Image.Dimension.MEDIUM)
                                                    .setResultCallback(bitmap->{
                                                        a.setImageBitmap(bitmap);
                                                        t1.setText(data2.items[0].title);

                                                    });
                                            mSpotifyAppRemote.getImagesApi()
                                                    .getImage(data2.items[1].imageUri,Image.Dimension.MEDIUM)
                                                    .setResultCallback(bitmap->{
                                                        a2.setImageBitmap(bitmap);
                                                        t2.setText(data2.items[1].title);
                                                    });
                                            mSpotifyAppRemote.getImagesApi()
                                                    .getImage(data2.items[2].imageUri,Image.Dimension.MEDIUM)
                                                    .setResultCallback(bitmap->{
                                                        a3.setImageBitmap(bitmap);
                                                        t3.setText(data2.items[2].title);
                                                    });

                                        });

                                    });

                                });
                        /**
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(track.imageUri,Image.Dimension.MEDIUM)
                                .setResultCallback(bitmap -> {
                                    a.setImageBitmap(bitmap);
                                    //t1.setText(
                                           // track.name);
                                    a2.setImageBitmap(bitmap);
                                    t2.setText(track.artist.name);

                                    a3.setImageBitmap(bitmap);
                                    t3.setText(track.album.name);

                                    //tt2.setImageBitmap(bitmap);

                                });
*/
                    }
                    else if(track == null) {
                        Log.d("Main Activity","Not playing any song at all");
                    }
                });
    }

    public void onConnectClicked(View v) {
        connected();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
    private void logError(Throwable throwable) {
        Toast.makeText(this, "R.string.err_generic_toast", Toast.LENGTH_SHORT).show();
        Log.e(MainActivity.class.getSimpleName(), "", throwable);
    }

}