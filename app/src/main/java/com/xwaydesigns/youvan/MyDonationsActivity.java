package com.xwaydesigns.youvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.xwaydesigns.youvan.Adapter.DonateNowAdapter;
import com.xwaydesigns.youvan.Adapter.MyDonationAdapter;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;
import com.xwaydesigns.youvan.Model.DonateNow;
import com.xwaydesigns.youvan.Model.Donation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class MyDonationsActivity extends AppCompatActivity {

    private RecyclerView list;
    private LinearLayoutManager linear_Layout;
    private String fetch_donations_server_url = Constants.BASE_URL+"youvan/fetch_donations.php";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView mnavigation_btn;
    private ProgressDialog dialog;
    private String item_name;
    private String item_quantity;
    private String item_image;
    private String item_status;
    private String donation_date;
    private List<Donation> data;
    private HashMap<String, String> user_data;
    private String student_id;
    private String donation_id;
    private SessionManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donation);

        drawer = findViewById(R.id.my_donation_drawer_layout);
        navigationView = findViewById(R.id.my_donation_navigation);
        mnavigation_btn = findViewById(R.id.my_donation_navigation_btn);

        dialog = new ProgressDialog(MyDonationsActivity.this);
        dialog.setTitle("Loading, Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        manager = new SessionManager(MyDonationsActivity.this);
        user_data = manager.getUserData();
        student_id = user_data.get("student_id");

        list = findViewById(R.id.donation_list);
        linear_Layout = new LinearLayoutManager(this);
        list.setHasFixedSize(true);
        list.setLayoutManager(linear_Layout);
        data = new ArrayList<>();
        FetchFromDatabase();

        //---------------------------------------------------------------------\\
        mnavigation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(Gravity.LEFT))
                {
                    drawer.closeDrawers();
                }
                else
                {
                    drawer.openDrawer(Gravity.LEFT);
                }

            }
        });
        //------------------------------------------------------------------------------\\

        //------------------------------------------------------------------------------\\
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if(item.getItemId()== R.id.menu_dashboard)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                     Intent dashboard_intent = new Intent(MyDonationsActivity.this,MainActivity.class);
                      startActivity(dashboard_intent);
                      finish();
                    return true;
                }
                if(item.getItemId() == R.id.menu_profile)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent profile_intent = new Intent(MyDonationsActivity.this, MyProfileActivity.class);
                    startActivity(profile_intent);
                    finish();
                    return true;
                }
                if(item.getItemId() == R.id.menu_donation)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    //do nothing just stay here
                    return true;
                }

                if(item.getItemId() == R.id.menu_reward)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent reward_intent = new Intent(MyDonationsActivity.this, MyRewardPointsActivity.class);
                    startActivity(reward_intent);
                    finish();
                    return true;
                }
                if(item.getItemId() == R.id.menu_logout)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    new AlertDialog.Builder(MyDonationsActivity.this)
                            .setTitle(getString(R.string.menu_logout))
                            .setCancelable(false)
                            .setMessage("Do you want to LogOut ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SessionManager manager = new SessionManager(MyDonationsActivity.this);
                                    manager.Logout();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // do nothing
                                }
                            })
                            //  .setIcon(R.drawable.ic_logout)
                            .show();
                    return true;
                }
                return false;
            }
        });
//-----------------------------------------------------------------------------------------------------------------\\

    }//oncreate

    private void FetchFromDatabase()
    {
        if(manager.connectivity()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Intent intent = new Intent(DonateNowActivity.this, Dashboard.class);
                    //  startActivity(intent);
                }
            }, TIME_OUT);
        }else if(!manager.connectivity()){
            dialog.dismiss();
            Toast.makeText(this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
        }
        //-------------------------------------------------------------------------------------------------\\
        StringRequest request = new StringRequest(Request.Method.POST,fetch_donations_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONArray result = new JSONArray(response);
                            JSONObject object = null ;

                            for(int i = 0; i< result.length() ; i++)
                            {
                                object = result.getJSONObject(i);
                                donation_id = object.getString("donation_id");
                                item_name = object.getString("item_name");
                                item_quantity = object.getString("item_quantity");
                                item_image = object.getString("item_image");
                                item_status = object.getString("item_status");
                                donation_date = object.getString("donation_date");
                                Donation donation = new Donation(donation_id,item_name,item_quantity,item_image,item_status,donation_date);
                                data.add(donation);
                            }
                            if(!donation_id.equals("null") && !item_name.equals("null") && !item_quantity.equals("null") && !donation_date.equals("null"))
                            {
                                MyDonationAdapter adapter = new MyDonationAdapter(MyDonationsActivity.this,data,student_id);
                                list.setAdapter(adapter);
                                dialog.dismiss();
                            }
                            else
                            {
                                dialog.dismiss();
                                Intent intent = new Intent(MyDonationsActivity.this,EmptyDonationActivity.class);
                                intent.putExtra("heading","My Donations");
                                startActivity(intent);
                                finish();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("fetch JSONException ", String.valueOf(e));
                            e.printStackTrace();
                            displayExceptionMessage1(e.getMessage());
                        }



                    }//onResponse End
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("fetch Volley error", String.valueOf(error));
                        error.printStackTrace();
                        displayExceptionMessage2(error.getMessage());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("fetch","donations");
                params.put("student_id",student_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MyDonationsActivity.this);
        requestQueue.add(request);
        //-------------------------------------------------------------------------------------------------\\

    }


    public void displayExceptionMessage1(String msg) {
        Toast.makeText(MyDonationsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }
    public void displayExceptionMessage2(String msg) {
        Toast.makeText(MyDonationsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }


}