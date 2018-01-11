package toplab18.runcoffee;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toplab18.runcoffee.Network.ModelLoginReq;
import toplab18.runcoffee.Network.ModelUserRes;
import toplab18.runcoffee.Network.ReceivedCookiesInterceptor;
import toplab18.runcoffee.Network.RestService;
import toplab18.runcoffee.Network.ServiceGenerator;


public final class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener {

    GoogleMap map;

    private FusedLocationProviderClient fusedLocationClient;
    boolean mapReady = true;
    GettingCoffeeShopsList gettingCoffeeShopsList;
    LinearLayout llDragView;
    private Button buttonHybryd, buttonSattelate, buttonMap;
    private String LOG_TAG = "RUN_COFFEE_TAG";
    private String JsonCoffeeShopsURL = "http://5.d.reanima.store/rest/spot";
    private String JsonMenuURL = "http://5.d.reanima.store/rest/spot-menu/";
    Typeface tpAppFontRegular, tpAppFontBold, tpAppFontThin;
    Realm realm, realmMenu;
    Integer screenWidth, screenHight, itemCount;
    private SlidingUpPanelLayout slidingPaneLayout;
    LatLng possition;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    String LOGIN = "test";
    String PASSWORD = "test";

    String[] orderList;
    String[] ids;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;
    private String SID_KEY, SID_TAG = "sid";
    RealmResults<CoffeeShop> resultsRealm;
    private static ArrayList<MenuCard> data;
    GettingCoffeeShopsMenu gettingCSMenu;
    String AUTHORIZATION_URL = "http://5.d.reanima.store/rest/auth-session/";
    String USER_ID = "ec954376-3ca0-481a-a979-c6ed5373d0ed";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.slideup);


        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();

        // getting screen size for further elements position
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHight = size.y;

        slidingPaneLayout = findViewById(R.id.sliding_layout);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        llDragView = findViewById(R.id.dragView);
        llDragView.setVisibility(View.GONE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show(); //TODO - create dialog if permission is not granted
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            displayLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("shops")
             //   .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        RealmConfiguration config2 = new RealmConfiguration
                .Builder()
                .name("menus")
               // .deleteRealmIfMigrationNeeded()
                .build();

        realmMenu = Realm.getInstance(config2);

        tpAppFontRegular = Typeface.createFromAsset(getAssets(), "phenomena-regular.otf");
        tpAppFontBold = Typeface.createFromAsset(getAssets(), "phenomena-bold.otf");
        tpAppFontThin = Typeface.createFromAsset(getAssets(), "phenomena-thin.otf");

        buttonSattelate = findViewById(R.id.buttonSattelate);
        buttonSattelate.setOnClickListener(this);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // starting parsing coffeeshops data
        gettingCoffeeShopsList = new GettingCoffeeShopsList();
        gettingCoffeeShopsList.execute();

        ids = new String[200];
        orderList = new String[200];
        itemCount = 0;
    }

    private void loadMenu() {

        gettingCSMenu = new GettingCoffeeShopsMenu();
        gettingCSMenu.execute();

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        data = new ArrayList<MenuCard>();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        llDragView.setVisibility(View.GONE);
        //TODO обноветь гео

    }


    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null)
            if (!realm.isClosed()) {
                realm.close();
            }
        if (realmMenu != null)
            if (!realmMenu.isClosed()) {
                realmMenu.close();
            }
    }


    private void displayLocation(LatLng latLng) {


///        Toast.makeText(this, "Last known position: "+latLng, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        mapReady = true;

        final LatLng[] lastPos = new LatLng[1];


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "check you device for GPS permission", Toast.LENGTH_SHORT).show();

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            displayLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                            possition = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition target = CameraPosition.builder()
                                    .target(possition)
                                    .zoom(15)
                                    .bearing(0)
                                    .build();
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            map.setOnMarkerClickListener(MainActivity.this);
                            map.setOnMapClickListener(MainActivity.this);
                            map.getUiSettings().setZoomControlsEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(true);

                            updateMeMarker(possition);
                        } else {
                            possition = new LatLng(50.992978, 7.314000);
                            CameraPosition target = CameraPosition.builder()
                                    .target(possition)
                                    .zoom(15)
                                    .bearing(0)
                                    .build();
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            map.setOnMarkerClickListener(MainActivity.this);
                            map.setOnMapClickListener(MainActivity.this);
                            map.getUiSettings().setZoomControlsEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(true);

//                            Toast.makeText(MainActivity.this, "Location = "+location.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void updateMeMarker(LatLng possitionl) {


        Integer markerSize = Math.round((screenHight / 15));

        MarkerOptions markerOptions = new MarkerOptions()
                .title("Me")
                .position(possitionl)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker("run_coffee_disign_me_marker", markerSize, markerSize)));

        map.addMarker(markerOptions);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.buttonSattelate:
                if (mapReady) {

                    LatLng possitionl = new LatLng(49.992978, 80.314000);
                    CameraPosition target = CameraPosition.builder()
                            .target(possitionl)
                            .zoom(10)
                            .build();
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                }
                break;

        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        llDragView.setVisibility(View.VISIBLE);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        Toast.makeText(this, "нажал", Toast.LENGTH_SHORT).show();

        //   = realm.where(CoffeeShop.class).findAll();
        //   results.load();

        Toast.makeText(this, resultsRealm.size()+"size of results", Toast.LENGTH_SHORT).show();

        for (CoffeeShop coffeeShop : resultsRealm) {

            if ((coffeeShop.getLat() == marker.getPosition().latitude)
                    && (coffeeShop.getLon() == marker.getPosition().longitude)
                    && (coffeeShop.getName().equals(marker.getTitle())))

            {
                showCard(coffeeShop.getName(), coffeeShop.getAddress(), coffeeShop.getPhone(), coffeeShop.getRating(), marker.getPosition(), coffeeShop.getId());

            } else {

                Log.i(LOG_TAG, coffeeShop.getName()+ "  не совпадает  " + marker.getTitle());
//
            }

        }


        return false;
    }

    private void showCard(String name, String address, String phone, int rating, LatLng markerPosition, String id) {

        Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();

        CameraPosition target = CameraPosition.builder()
                .target(markerPosition)
                .zoom(17)
                .bearing(0)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        map.setPadding(0, 0, 0, 200);


        ImageView imageLogo = findViewById(R.id.ivCoffeeshopLogo);
        imageLogo.setImageResource(R.drawable.run_coffee_disign_empty_icon);


        TextView tvName = findViewById(R.id.tvCoffeeshopName);
        tvName.setText(name);
        tvName.setTypeface(tpAppFontBold);
        tvName.setTextSize(20);

        TextView tvAddress = findViewById(R.id.tvAdress);
        tvAddress.setText(address);
        tvAddress.setTypeface(tpAppFontRegular);
        tvAddress.setTextSize(15);

//        TextView tvPhone = new TextView(this);
//        tvPhone.setText(phone);
//        tvPhone.setTypeface(tpAppFontRegular);
//        tvPhone.setTextSize(20);

        TextView tvRating = findViewById(R.id.tvRating);//new TextView(this);
        tvRating.setText(rating + " ★ ");
        tvRating.setTypeface(tpAppFontRegular);
        tvRating.setTextSize(15);


        Location locationA = new Location("point A");

        locationA.setLatitude(possition.latitude);
        locationA.setLongitude(possition.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(markerPosition.latitude);
        locationB.setLongitude(markerPosition.longitude);

        int distance = Math.round(locationA.distanceTo(locationB));

        //int distance = 100;

        TextView tvDistance = findViewById(R.id.tvDistance);//new TextView(this);
        tvDistance.setText(distance + " m");
        tvDistance.setTypeface(tpAppFontRegular);
        tvDistance.setTextSize(15);


        loadMenu();

    }

    private void makeMarker(String name, String address, Double lat, Double lon) {

        Integer markerSize = Math.round(screenHight / 12);

        MarkerOptions markerOptions = new MarkerOptions()
                .title(name)
                .position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker("run_coffee_disign_marker_xxl", markerSize, markerSize)))
                .snippet(address);

        map.addMarker(markerOptions);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        llDragView.setVisibility(View.GONE);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public Bitmap resizeMarker(String MarkerName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(MarkerName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private class GettingCoffeeShopsList extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";



        @Override
        protected String doInBackground(Void... params) {

            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(JsonCoffeeShopsURL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            String name, address;
            Double lat, lon;

            singIn();
            // getting data from webDB
            try {

                // making cash for coffeshops in entire DB
                realm.beginTransaction();
                realm.deleteAll();
                realm.createAllFromJson(CoffeeShop.class, strJson);
                realm.commitTransaction();

                // counter for coffeeshops in db
                RealmResults<CoffeeShop> countCoffeeshops = realm.where(CoffeeShop.class).findAll();
                resultsRealm = countCoffeeshops;

                Toast.makeText(MainActivity.this, countCoffeeshops.size() + " coffeeShops are online \n", Toast.LENGTH_LONG).show();


                // adding markers for every available coffeshop
                for (int i = 0; i < countCoffeeshops.size(); i++) {

                    CoffeeShop cShop = countCoffeeshops.get(i);

                    name = cShop.getName();
                    address = cShop.getAddress();
                    lat = cShop.getLat();
                    lon = cShop.getLon();

                    makeMarker(name, address, lat, lon);

                    //increase count of coffeeshops

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private class GettingCoffeeShopsMenu extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {

            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(JsonMenuURL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            String name, address;
            Double lat, lon;

            // getting data from webDB
            try {

                // making cash for coffeshops in entire DB
                realmMenu.beginTransaction();
                realmMenu.deleteAll();
                realmMenu.createAllFromJson(MenuCard.class, strJson);
                realmMenu.commitTransaction();

                // counter for coffeeshops in db
                RealmResults<MenuCard> countCoffeeshopsMenu = realmMenu.where(MenuCard.class).findAll();

                // adding markers for every available coffeshop
                for (int i = 0; i < countCoffeeshopsMenu.size(); i++) {


                    MenuCard mShop = countCoffeeshopsMenu.get(i);
                    data.add(mShop);
                    ids[i] = mShop.getId();

                }

                adapter = new CustomAdapter(data);
                recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(adapter);

                recyclerView.addOnItemTouchListener(new RecyclerClickListener(MainActivity.this) {
                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }

                    @Override
                    public void onItemClick(RecyclerView recyclerView, View itemView,
                                            int position) {

                        addPositionToOrder(position);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    private void addPositionToOrder(int position) {

        itemCount++;
        //String id;
        Toast.makeText(this, "Всего элементов: " + itemCount + "\n id = " + ids[position], Toast.LENGTH_SHORT).show();

        orderList[itemCount] = ids[position];

        for (int i = 0; i < orderList.length; i++) {

            if (orderList[i] != null)
                Log.i("-------------------->" + i, orderList[i].toString());
        }
        updateTotalPrice(ids[position]);
    }


    private void updateTotalPrice(String id) {

    }

    public Call<ModelUserRes> loginUser (ModelLoginReq userLoginReq){
        RestService restService = ServiceGenerator.createService(RestService.class);
        return restService.loginUser(userLoginReq);
    }

    private void singIn () {

       Call<ModelUserRes> call  =  loginUser(
               new ModelLoginReq(LOGIN,PASSWORD));

       call.enqueue(new Callback<ModelUserRes>() {
           @Override
           public void onResponse(Call<ModelUserRes> call, Response<ModelUserRes> response) {




        //       String sid = response.headers().value(11).substring(16,52);

               Log.i("header size: ", response.headers().size()+"");//response.headers().size();
                Toast.makeText(MainActivity.this, response.headers().size()+"", Toast.LENGTH_LONG  ).show();


               List<String> values = response.headers().values("Set-Cookie");

                String sid =  values.toString();
               String userID = response.body().getUserId();
               String id = response.body().getId();
               String cookie = response.headers().toString();

               if (id != null) {

                   Toast.makeText(MainActivity.this, "Login correct, Welcome", Toast.LENGTH_LONG).show();


                   Log.i("RUN_-------> userID:", userID + "");
                   Log.i("RUN_-------> ID:", id + "");
                   Log.i("RUN_-------> headers:", cookie + "");
                  Log.i("RUN_-------> sid:", sid + "");
                   Log.i("RUN_------> set-Cookie:" , response.headers().get("Set-Cookie: sid")+ "");

               } else {

                   Toast.makeText(MainActivity.this, "Login or password incorrect, try again", Toast.LENGTH_LONG).show();

               }
           }

           @Override
           public void onFailure(Call<ModelUserRes> call, Throwable t) {
               Log.e("RUN_-------> error:",  t.toString());
           }
       });



    }

}
