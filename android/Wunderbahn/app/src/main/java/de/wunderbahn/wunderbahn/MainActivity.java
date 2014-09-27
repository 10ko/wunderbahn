package de.wunderbahn.wunderbahn;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.relayr.LoginEventListener;
import io.relayr.RelayrSdk;
import io.relayr.model.DeviceModel;
import io.relayr.model.LightColorProx;
import io.relayr.model.Reading;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends ActionBarActivity implements LoginEventListener {
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("d9d5c83e-b1d5-4569-b659-db416908db77");

    private Subscription mColourDeviceSubscription;
    private Subscription mWebSocketSubscription;
    private Subscription mUserInfoSubscription;
    private TransmitterDevice mDevice;
    private VBBClient mVBBClient = new VBBClient();
    private String mStartColour = null;
    private String mEndColour = null;

    enum STATE {
        RESET,
        FIRST,
        SECOND
    }

    private STATE state = STATE.RESET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);

        if (!RelayrSdk.isUserLoggedIn()) {
            //if the user isn't logged in, we call the logIn method
            RelayrSdk.logIn(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (RelayrSdk.isUserLoggedIn()) {
            updateUiForALoggedInUser();
        } else {
            updateUiForANonLoggedInUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (RelayrSdk.isUserLoggedIn()) {
            getMenuInflater().inflate(R.menu.logged_in, menu);
        } else {
            getMenuInflater().inflate(R.menu.not_logged_in, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_log_in) {

            //we call the login method on the relayr SDK
            RelayrSdk.logIn(this, this);
            return true;
        } else if (item.getItemId() == R.id.action_log_out) {

            //otherwise we call the logout method defined later in this
            //class
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUiForANonLoggedInUser() {
    }

    private void updateUiForALoggedInUser() {
        loadUserInfo();
    }

    private void loadUserInfo() {
        mUserInfoSubscription = RelayrSdk.getRelayrApi()
            .getUserInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<User>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(User user) {
//                    String hello = String.format(getString(R.string.hello_world), user.getName());
//                    mWelcomeTextView.setText(hello);
                    loadColourDevice(user);
                }
            });
    }

    private void logOut() {
        //call the logOut method on the relayr SDK
        RelayrSdk.logOut();

        //call the invalidateOptionsMenu this is defined in the
        //Activity class and is used to reset the menu option
        invalidateOptionsMenu();

        //use the Toast library to display a message to the user
        Toast.makeText(this, R.string.successfully_logged_out, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessUserLogIn() {
        Toast.makeText(this, R.string.successfully_logged_in, Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();
        updateUiForALoggedInUser();
    }

    @Override
    public void onErrorLogin(Throwable throwable) {
        Toast.makeText(this, R.string.unsuccessfully_logged_in, Toast.LENGTH_SHORT).show();
        updateUiForANonLoggedInUser();
    }


    private void loadColourDevice(User user) {
        mColourDeviceSubscription = RelayrSdk.getRelayrApi()
            .getTransmitters(user.id)
            .flatMap(new Func1<List<Transmitter>, Observable<List<TransmitterDevice>>>() {
                @Override
                public Observable<List<TransmitterDevice>> call(List<Transmitter> transmitters) {
                    // This is a naive implementation. Users may own many WunderBars or other
                    // kinds of transmitter.
                    if (transmitters.isEmpty())
                        return Observable.from(new ArrayList<List<TransmitterDevice>>());
                    return RelayrSdk.getRelayrApi().getTransmitterDevices(transmitters.get(0).id);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<List<TransmitterDevice>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(List<TransmitterDevice> devices) {
                    for (TransmitterDevice device : devices) {
                        if (device.model.equals(DeviceModel.LIGHT_PROX_COLOR.getId())) {
                            subscribeToColourUpdates(device);
                            return;
                        }
                    }
                }
            });

    }

    private void subscribeToColourUpdates(TransmitterDevice device) {
        mDevice = device;

        mWebSocketSubscription = RelayrSdk.getWebSocketClient()
            .subscribe(device, new Subscriber<Object>() {

                @Override
                public void onCompleted() {
                    Log.d("WIBBLE", "foo");
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, R.string.something_went_wrong,
                            Toast.LENGTH_SHORT).show();
                    Log.d("WIBBLE", "bar");
                }

                @Override
                public void onNext(Object o) {
                    Log.d("WIBBLE", "baz");
                    Reading reading = new Gson().fromJson(o.toString(), Reading.class);

                    Log.d("COLOUR", reading.clr.toString());

                    boolean state_changed = false;
                    String station_colour = nearest_station(reading.clr);

                    if (station_colour != null) {
                        if (state == STATE.RESET) {
                            state = STATE.FIRST;
                            state_changed = true;
                            mStartColour = station_colour;
                        } else if (state == STATE.FIRST) {
                            state = STATE.SECOND;
                            state_changed = true;
                            mEndColour = station_colour;
                        } else {
                            mStartColour = null;
                            mEndColour = null;
                        }
                    } else if (true /*reading is the reset colour*/) {
                        state = STATE.RESET;
                        mStartColour = null;
                        mEndColour = null;
                        state_changed = true;
                    }

                    if (state_changed) {
                        switch (state) {
                            case RESET:
                                // TODO: Stop strobing.
                                break;

                            case FIRST:
                                // TODO: Keep the colour and carry on.
                                break;

                            case SECOND:
                                // TODO: Perform the route query.
                                VBBClient.getStationListFromColor(mStartColour, mEndColour);
                                update_pebble("17:45");
                                // TODO: Start strobing.
                                break;
                        }
                    }
                }
            });
    }

    private void update_pebble(String s) {
        PebbleDictionary data = new PebbleDictionary();
        data.addString(0, s);
        PebbleKit.sendDataToPebble(getApplicationContext(), PEBBLE_APP_UUID, data);
    }

    private String nearest_station(LightColorProx.Color clr) {
        int nearest_dist = 999999;
        String nearest_station_colour = null;
        int max_dist_sq = 10 * 10;

        for(StationList s : StationList.values()) {
            LightColorProx.Color sc = string_to_colour(s.getColor());
            int diffr = sc.r - clr.r;
            int diffg = sc.g - clr.g;
            int diffb = sc.b - clr.b;
            int dist_sq = (diffr * diffr) + (diffg * diffg) + (diffb * diffb);

            if (dist_sq <= max_dist_sq && dist_sq < nearest_dist) {
                nearest_dist = dist_sq;
                nearest_station_colour = s.getColor();
            }
        }

        return nearest_station_colour;
    }

    private LightColorProx.Color string_to_colour(String c) {
        return new LightColorProx.Color();
    }
}
