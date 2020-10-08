package com.xwaydesigns.youvan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText e_address;
    private TextView e_dob;
    private Button submit_btn;
    private String date;
    private String student_id;
    private String address;
    private HashMap<String,String> user_data;
    private ProgressDialog dialog1;
    private DatePickerDialog.OnDateSetListener listener;
    private String type="Donor";
    private String update_donor_profile_server_url = Constants.BASE_URL+"youvan/complete_profile.php";
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        e_address = findViewById(R.id.update_profile_address);
        e_dob = findViewById(R.id.update_profile_dob);
        submit_btn = findViewById(R.id.update_profile_btn);

        dialog1 = new ProgressDialog(UpdateProfileActivity.this);

        manager = new SessionManager(UpdateProfileActivity.this);
        user_data = manager.getUserData();
        student_id = user_data.get("student_id");

        e_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(UpdateProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,listener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
               // Toast.makeText(UpdateProfileActivity.this,"Date of Birth:"+" "+year+"/"+month+"/"+day,Toast.LENGTH_SHORT).show();
                month = month + 1;
                date = String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year);
                e_dob.setText(date);
                Toast.makeText(UpdateProfileActivity.this,"Your Selected Date of Birth is:"+ " " +date,Toast.LENGTH_SHORT).show();
            }
        };

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               address =  e_address.getText().toString();
                dialog1.setTitle("Loading, Please wait...");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                if(manager.connectivity()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Intent intent = new Intent(DonateNowActivity.this, Dashboard.class);
                            //  startActivity(intent);
                        }
                    }, TIME_OUT);
                }else if(!manager.connectivity()){
                    dialog1.dismiss();
                    Toast.makeText(UpdateProfileActivity.this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
                }
                 //--------------------------------------------------------------------------------------\\
                //-------------------------------------------------------------\\
                StringRequest request = new StringRequest(Request.Method.POST, update_donor_profile_server_url,
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

                                        Toast.makeText(UpdateProfileActivity.this,"Your Profile Updated Successfully " + data,Toast.LENGTH_SHORT).show();
                                        e_address.setText("");
                                        e_dob.setText("Enter Date Of Birth");
                                        dialog1.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(UpdateProfileActivity.this,"Error:" + data,Toast.LENGTH_SHORT).show();
                                        dialog1.dismiss();
                                    }
                                }
                                catch (JSONException e)
                                {
                                    dialog1.dismiss();
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
                                dialog1.dismiss();
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
                        params.put("update","profile");
                        params.put("student_id",student_id);
                        params.put("address",address);
                        params.put("birth_date",date);
                        params.put("type",type);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(UpdateProfileActivity.this);
                requestQueue.add(request);
                //----------------------------------------------------------------------------------------------------------------------//

                //--------------------------------------------------------------------------------------\\
            }
        });

    }


    public void displayExceptionMessage3(String msg)
    {
        Toast.makeText(UpdateProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }

    private void displayExceptionMessage4(String message)
    {
        Toast.makeText(UpdateProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }
}