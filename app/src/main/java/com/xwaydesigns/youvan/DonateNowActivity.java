package com.xwaydesigns.youvan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.xwaydesigns.youvan.Adapter.DonateNowAdapter;
import com.xwaydesigns.youvan.Adapter.MyDonationAdapter;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;
import com.xwaydesigns.youvan.Model.DonateNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class DonateNowActivity extends AppCompatActivity
{
    private Spinner item_list;
    private Spinner item_quantity_list;
    private CircleImageView item_image;
    private Button add_item_image_btn;
    private Button add_item_btn;
    private RecyclerView donation_list;
    private String[] item_id;
    private String[] item_name;
    private String[] item_rewards;
    private String[] item_quantity;
    private List<String> item_id_list = new ArrayList<>();
    private List<String> item_name_list = new ArrayList<>();
    private List<String> item_rewards_list = new ArrayList<>();
    private String item;
    private String quantity;
    private String item_key;
    private LinearLayoutManager linear_Layout;
    private ArrayList<DonateNow> data;
    private final static int CAPTURE_IMAGE_FROM_CAMERA = 1;
    private final static int CHOOSE_IMAGE_FROM_GALLERY = 2;
    private Uri image_uri;
    private Bitmap bitmap;
    private String Encoded_images;
    private String insert_donation_server_url = Constants.BASE_URL+"youvan/add_donation.php";
    private String fetch_items_server_url = Constants.BASE_URL+"youvan/fetch_items.php";
    private String student_id;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private ProgressDialog dialog;
    private String date;
    private boolean ImageSelected  ;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_now);
        dialog = new ProgressDialog(DonateNowActivity.this);
        dialog.setTitle("Loading, Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        item_list = findViewById(R.id.donation_item_name);
        item_quantity_list = findViewById(R.id.donation_item_quantity);
        add_item_btn = findViewById(R.id.donation_add_item_btn);
        item_image =findViewById(R.id.donation_item_image);
        add_item_image_btn = findViewById(R.id.donation_add_item_image_btn);
         item_quantity = getResources().getStringArray(R.array.item_quantity);

         Intent intent = getIntent();
         student_id =  intent.getStringExtra("student_id");

         manager = new SessionManager(DonateNowActivity.this);

        donation_list = findViewById(R.id.donation_item_list);
        linear_Layout = new LinearLayoutManager(DonateNowActivity.this);
        donation_list.setHasFixedSize(true);
        donation_list.setLayoutManager(linear_Layout);
        data = new ArrayList<>();

        //----------------------------------------------------------\\
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
            Toast.makeText(DonateNowActivity.this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
        }
        //----------------------------------------------------------\\


        //-------------------------------------------------------------------------------------------\\
        StringRequest request = new StringRequest(Request.Method.POST,fetch_items_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONArray result = new JSONArray(response);
                            JSONObject object = null ;
                            item_id = new String[result.length()];
                            item_name = new String[result.length()];
                            item_rewards = new String[result.length()];

                            for(int i = 0; i< result.length() ; i++)
                            {
                                object = result.getJSONObject(i);
                                item_id[i] = object.getString("item_id");
                                item_name[i] = object.getString("item_name");
                                item_rewards[i] = object.getString("item_rewards");
                            }

                            for(int i = 0; i< item_id.length; i++)
                            {
                                item_id_list.add(item_id[i]);
                                item_name_list.add(item_name[i]);
                                item_rewards_list.add(item_rewards[i]);
                            }

                            item_list.setAdapter(new ArrayAdapter<String>(DonateNowActivity.this, android.R.layout.simple_spinner_dropdown_item, item_name_list));
                            item_quantity_list.setAdapter(new ArrayAdapter<String>(DonateNowActivity.this, android.R.layout.simple_spinner_dropdown_item, item_quantity));
                            dialog.dismiss();

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fetch","items");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DonateNowActivity.this);
        requestQueue.add(request);

        //-------------------------------------------------------------------------------------------\\

        //----------------------------------------------------------------------------------------//
        item_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                item = item_name_list.get(i);
                item_key = item_id_list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
   //---------------------------------------------------------------------------------------------\\

        //----------------------------------------------------------------------------------------//
        item_quantity_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                quantity = item_quantity_list.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        //---------------------------------------------------------------------------------------------\\
        add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
              // Drawable d1 = item_image.getDrawable();
              // Drawable d2 = getResources().getDrawable(R.drawable.default_profile);
         if( !(item_image.getDrawable().equals(getResources().getDrawable(R.drawable.default_profile) ) )  )
         {
               if(item_list.getSelectedItemPosition() != 0)
               {
                   if(item_quantity_list.getSelectedItemPosition() != 0)
                   {
                       SaveToDatabase();
                   }
                   else
                   {
                       Toast.makeText(DonateNowActivity.this,"Please Fill In All The Field" ,Toast.LENGTH_SHORT).show();
                   }
               }
               else
               {
                   Toast.makeText(DonateNowActivity.this,"Please Fill In All The Field" ,Toast.LENGTH_SHORT).show();
               }
         }
         else
         {
             Toast.makeText(DonateNowActivity.this,"Please Fill In All The Field" ,Toast.LENGTH_SHORT).show();
         }

            }
        });

        //----------------------------------------------------------------------------------------------------\\
        add_item_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //-----------AlertDialog Starts------------------//
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(DonateNowActivity.this);
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
                            startActivityForResult(Intent.createChooser(intent,"select image"),CHOOSE_IMAGE_FROM_GALLERY);
                        }
                        else if (options[item].equals("Cancel"))
                        {
                            dialog.dismiss();
                            Toast.makeText(DonateNowActivity.this," Photo loading cancelled",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                builder.show();
                //-----------AlertDialog Ends------------------//
            }

        });
        //----------------------------------------------------------------------------------------------------\\

    }//onCreate ends

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_FROM_CAMERA && resultCode == RESULT_OK)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = getResizedBitmap(bitmap, 500);
            item_image.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Encoded_images = Base64.encodeToString(byteArray, Base64.DEFAULT);

        }

        if(requestCode == CHOOSE_IMAGE_FROM_GALLERY && resultCode == RESULT_OK)
        {
            image_uri = data.getData();
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image_uri);
                bitmap = getResizedBitmap(bitmap, 500);
                item_image.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Encoded_images = Base64.encodeToString(byteArray, Base64.DEFAULT);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }//onActivityResult end

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

    public void SaveToDatabase()
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
            Toast.makeText(DonateNowActivity.this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
        }
        //-------------------------------------------------------------\\
        StringRequest request = new StringRequest(Request.Method.POST, insert_donation_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                      //  Toast.makeText(DonateNowActivity.this,"Response:"+ response,Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            String rec_data = obj.getString("response");
                            if(rec_data.equals("success"))
                            {
                                Toast.makeText(DonateNowActivity.this,"Item Image Uploaded Successfully to Database" + data,Toast.LENGTH_SHORT).show();
                                calendar = Calendar.getInstance();
                                dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                date = dateFormat.format(calendar.getTime());

                                data.add(new DonateNow(bitmap,item,quantity,date));
                                DonateNowAdapter adapter = new DonateNowAdapter(DonateNowActivity.this,data);
                                donation_list.setAdapter(adapter);
                                item_image.setImageResource(R.drawable.item_icon);
                                item_list.setSelection(0);
                                item_quantity_list.setSelection(0);
                                dialog.dismiss();

                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(DonateNowActivity.this,"Error:" + data,Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            dialog.dismiss();
                            e.printStackTrace();
                            Log.e("Upload JSONException ", String.valueOf(e));
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
                        Log.e("Upload Volley error", String.valueOf(error));
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
                params.put("insert","items");
                params.put("item_id",item_key);
                params.put("student_id",student_id);
                params.put("item_quantity",quantity);
                params.put("item_image",Encoded_images);
                params.put("doc_type","Items Image");

                return params;
            }
        };

        //  MySingleTon.getInstance(OTPGenerationActivity.this).AddToRequestQueue(request);
        RequestQueue requestQueue = Volley.newRequestQueue(DonateNowActivity.this);
        requestQueue.add(request);
        //----------------------------------------------------------------------------------------------------------------------//

    }//saveToDatabase() ends


    public void displayExceptionMessage1(String msg) {
        Toast.makeText(DonateNowActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }
    public void displayExceptionMessage2(String msg) {
        Toast.makeText(DonateNowActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }


}