package com.xwaydesigns.youvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CircleImageView profile_image;
    private TextView username;
    private TextView school_name;
    private TextView reward_point;
    private Button donate_btn;
    private Button profile_image_btn;
    private ImageView mnavigation_btn;
    private String student_id;
    private String student_name;
    private HashMap<String,String> user_data;
    private String fetch_main_students_server_url = Constants.BASE_URL+"youvan/fetch_main_students.php";
    private String upload_main_student_image_server_url = Constants.BASE_URL+"youvan/upload_student_image.php";
    private ProgressDialog dialog;
    private String total_rewards;
    private String received_student_image;
    private String received_student_name;
    private final static int CAPTURE_IMAGE_FROM_CAMERA = 1;
    private final static int CHOOSE_IMAGE_FROM_GALLERY = 2;
    private Uri image_uri;
    private String Encoded_images;
    private String image_url;
    private String type = "Donor";
    private String received_school_name;
    private Bitmap bitmap;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        donate_btn = findViewById(R.id.main_donate_btn);
        mnavigation_btn = findViewById(R.id.navigation_btn);
        profile_image = findViewById(R.id.main_profile_image);
        profile_image_btn = findViewById(R.id.main_add_profile_image_btn);
        username = findViewById(R.id.main_username);
        school_name = findViewById(R.id.main_school_name);
        reward_point = findViewById(R.id.main_reward_points);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Loading, Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

         manager = new SessionManager(MainActivity.this);
        user_data = manager.getUserData();
        student_id = user_data.get("student_id");
     //   Toast.makeText(MainActivity.this,"main_student_id:" + student_id,Toast.LENGTH_SHORT).show();
        if(manager.connectivity()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Intent intent = new Intent(DonateNowActivity.this, Dashboard.class);
                    //  startActivity(intent);
                }
            }, TIME_OUT);
        }else if(!manager.connectivity()){
            Toast.makeText(this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
        }
        //-------------------------------------------------------------------------------------------\\
        StringRequest request = new StringRequest(Request.Method.POST,fetch_main_students_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                       // Toast.makeText(MainActivity.this,"Response:" + response,Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray result = new JSONArray(response);
                            for(int i = 0; i< result.length() ; i++)
                            {
                                JSONObject object = result.getJSONObject(i);
                                received_student_name = object.getString("student_name");
                                received_student_image = object.getString("student_image");
                                received_school_name = object.getString("school_name");
                                total_rewards = object.getString("item_rewards");
                                student_name = received_student_name;
                            }
                                if(received_student_image.equals("null") && total_rewards.equals("null"))
                                {
                                    username.setText(received_student_name);
                                    school_name.setText(received_school_name);
                                    profile_image.setImageResource(R.drawable.default_profile);
                                    reward_point.setText("0");
                                    dialog.dismiss();
                                }
                                else
                                   {
                                       username.setText(received_student_name);
                                       school_name.setText(received_school_name);
                                       reward_point.setText(total_rewards);
                                      image_url = Constants.BASE_URL+"youvan/images/"+student_id+"/StudentsImage/"+received_student_image;
                                      // image_url = "ftp://103.83.81.228/images/"+student_id+"/StudentsImage/"+received_student_image;
                                      Picasso.get().load(image_url).resize(120,120).centerCrop().placeholder(R.drawable.default_profile)
                                           .error(R.drawable.default_profile).into(profile_image);
                                      dialog.dismiss();
                                   }
                        }
                        catch (JSONException e)
                        {
                            dialog.dismiss();
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
                        dialog.dismiss();
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
                params.put("fetch","student");
                params.put("student_id",student_id);
                params.put("type",type);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);

        //-------------------------------------------------------------------------------------------\\

        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent donate_intent = new Intent(MainActivity.this, DonateNowActivity.class);
                donate_intent.putExtra("student_id", student_id);
                startActivity(donate_intent);
            }
        });

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if(item.getItemId()== R.id.menu_dashboard)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    return true;
                }
                if(item.getItemId() == R.id.menu_profile)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent profile_intent = new Intent(MainActivity.this, MyProfileActivity.class);
                    startActivity(profile_intent);
                    //finish();
                    return true;
                }
                if(item.getItemId() == R.id.menu_donation)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent donation_intent = new Intent(MainActivity.this, MyDonationsActivity.class);
                    startActivity(donation_intent);
                   // finish();
                    return true;
                }

                if(item.getItemId() == R.id.menu_reward)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent reward_intent = new Intent(MainActivity.this, MyRewardPointsActivity.class);
                    startActivity(reward_intent);
                   // finish();
                    return true;
                }
                if(item.getItemId() == R.id.menu_logout)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.menu_logout))
                            .setCancelable(false)
                            .setMessage("Do you want to LogOut ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SessionManager manager = new SessionManager(MainActivity.this);
                                    manager.Logout();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // do nothing
                                }
                            })
                            .show();
                    return true;
                }
                return false;
            }
        });
//-----------------------------------------------------------------------------------------------------------------\\

        //-------------------------------------------------------------------------------------------\\
        profile_image_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (options[item].equals("Take Photo"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,CAPTURE_IMAGE_FROM_CAMERA);
                        }
                        else if (options[item].equals("Choose from Gallery"))
                        {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                           // startActivityForResult(Intent.createChooser(intent,"select image"),CHOOSE_IMAGE_FROM_GALLERY);
                            startActivityForResult(intent,CHOOSE_IMAGE_FROM_GALLERY);
                        }
                        else if (options[item].equals("Cancel"))
                        {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this,"item image loading cancelled",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
        //-------------------------------------------------------------------------------------------\\
    }//onCreate() ends

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_FROM_CAMERA && resultCode == RESULT_OK)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap =  getResizedBitmap(bitmap,400);
            profile_image.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Encoded_images = Base64.encodeToString(byteArray, Base64.DEFAULT);
            SaveToDatabase();
        }

        if(requestCode == CHOOSE_IMAGE_FROM_GALLERY && resultCode == RESULT_OK)
        {
            image_uri = data.getData();
           // Toast.makeText(MainActivity.this,"gallery Image Uri:" + image_uri ,Toast.LENGTH_SHORT).show();
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image_uri);
                bitmap =  getResizedBitmap(bitmap,400);
                profile_image.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Encoded_images = Base64.encodeToString(byteArray, Base64.DEFAULT);
                SaveToDatabase();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    } //onActivityResult end
    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1)
        {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }//getResizedBitmap() method end

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void SaveToDatabase()
    {
      //  Toast.makeText(MainActivity.this, "Input: " + " " + "student_id:"+student_id+", "+"student_name:"+student_name + ", " +"type:"+type ,
        //        Toast.LENGTH_LONG).show();
        //-------------------------------------------------------------\\
        if(manager.connectivity()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Intent intent = new Intent(DonateNowActivity.this, Dashboard.class);
                    //  startActivity(intent);
                }
            }, TIME_OUT);
        }else if(!manager.connectivity()){
            Toast.makeText(this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
        }
        StringRequest request = new StringRequest(Request.Method.POST, upload_main_student_image_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                       // Toast.makeText(MainActivity.this,"Response:"+ response,Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            String data = obj.getString("response");
                            if(data.equals("success"))
                            {
                               // profile_image.setImageResource(0);
                                Toast.makeText(MainActivity.this,"Profile Image Uploaded Successfully to Database" +
                                        data,Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Error:" + data,Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("Upload JSONException ", String.valueOf(e));
                            e.printStackTrace();
                            displayExceptionMessage3(e.getMessage());
                        }
                    }//onResponse End
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Upload Volley error", String.valueOf(error));
                        error.printStackTrace();
                        displayExceptionMessage4(error.getMessage());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("insert","photo");
                params.put("student_id",student_id);
                params.put("student_name",student_name);
                params.put("student_image",imageToString(bitmap));
                params.put("doc_type","StudentsImage");
                params.put("type",type);
                return params;
            }
        };

        /*request.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); */
        
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
        //----------------------------------------------------------------------------------------------------------------------//

    }//saveToDatabase() ends

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Do you want to Exit App ?")
                .setIcon(R.drawable.youvan_icon)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.main_logout)
        {
            SessionManager manager = new SessionManager(MainActivity.this);
            manager.Logout();
            finish();
        }
        if(item.getItemId()== R.id.main_change_password)
        {
          Intent password_intent = new Intent(MainActivity.this,ChangePasswordActivity.class);
          startActivity(password_intent);
        }

        return true;
    }

    public void displayExceptionMessage1(String msg) {
        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }
    public void displayExceptionMessage2(String msg) {
        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }

    public void displayExceptionMessage3(String msg)
    {
        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }

    private void displayExceptionMessage4(String message)
    {
        Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }

}