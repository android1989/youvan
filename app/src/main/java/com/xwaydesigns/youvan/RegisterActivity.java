package com.xwaydesigns.youvan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class RegisterActivity extends AppCompatActivity {
    private EditText e_name,e_email,e_pass,e_conf_pass,e_mobile,e_class;
    private Spinner spinner_school_list;
    private Button signup_btn;
    private String student_name,email,mobile,conf_password,password,student_class;
    private String received_student_id;
    private String received_student_name;
    private String add_students_server_url = Constants.BASE_URL+"youvan/add_student.php";
    private String fetch_schools_server_url = Constants.BASE_URL+"youvan/fetch_schools.php";
    private String[] school_id;
    private String[] school_name;
    private List<String> school_id_list = new ArrayList<>();
    private List<String> school_name_list = new ArrayList<>();
    private String School_Name;
    private String School_Id;
    private String type = "Donor";
    private ProgressDialog dialog;
    private String received_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e_name = findViewById(R.id.reg_username1);
        e_email = findViewById(R.id.reg_username2);
        e_pass = findViewById(R.id.reg_username3);
        e_conf_pass = findViewById(R.id.reg_username4);
        e_mobile = findViewById(R.id.reg_username5);
        spinner_school_list = findViewById(R.id.school_list);
        e_class = findViewById(R.id.reg_username6);
        signup_btn = findViewById(R.id.reg_btn);

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("Loading, Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FetchSchool();

        //----------------------------------------------------------------------------------------//
        spinner_school_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                School_Name = school_name_list.get(i);
                School_Id = school_id_list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        //---------------------------------------------------------------------------------------------\\

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                student_name = e_name.getText().toString();
                email = e_email.getText().toString();
                password = e_pass.getText().toString();
                conf_password = e_conf_pass.getText().toString();
                mobile = e_mobile.getText().toString();
                student_class = e_class.getText().toString();
              //  Toast.makeText(RegisterActivity.this, "Input: " + " " + "student_name:"+student_name+", "+"school_id:"+School_Id+ " ,"+"type:"+type + ", " +"mobile:"+mobile+","+"email:"+email+", "+"password:"+password+" ,"+"student_class:"+student_class,  Toast.LENGTH_SHORT).show();
                dialog.setTitle("Loading, Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                final SessionManager manager = new SessionManager(RegisterActivity.this);
                if(!student_name.equals("") || !email.equals("") || !School_Name.equals("") || !mobile.equals("") || !password.equals("")  || !conf_password.equals("") || !student_class.equals(""))
                {
                    if(password.equals(conf_password)) {

                        if(!School_Name.equals("School Name") || !School_Id.equals("1"))
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
                                Toast.makeText(RegisterActivity.this,"No internet Connectivity", Toast.LENGTH_SHORT).show();
                            }
                        //---------------------------------------------------------------------------------------\\
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_students_server_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //----------------------------------------------------------------------------------------------------------\\
                                       // Toast.makeText(RegisterActivity.this, "received Response: " + response, Toast.LENGTH_SHORT).show();
                                        try {
                                            JSONArray result = new JSONArray(response);
                                            for (int i = 0; i < result.length(); i++) {
                                                JSONObject object = result.getJSONObject(i);
                                                received_student_id = object.getString("student_id");
                                                received_student_name = object.getString("student_name");
                                                received_response = object.getString("response");
                                            }

                                            if(received_response.equals("Success"))
                                            {
                                                e_name.setText("");
                                                e_email.setText("");
                                                e_pass.setText("");
                                                e_conf_pass.setText("");
                                                e_mobile.setText("");
                                                e_class.setText("");

                                                SessionManager manager = new SessionManager(RegisterActivity.this);
                                                manager.SignUpSuccessful(email, password, received_student_id);
                                                dialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            else if(received_response.equals("Insertion failed"))
                                            {
                                                dialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Oops..Error while Signup", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(received_response.equals("Email already exists"))
                                            {
                                                dialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Email-id already exists", Toast.LENGTH_SHORT).show();
                                            }



                                        } catch (JSONException e) {
                                            dialog.dismiss();
                                            Log.e("JSONException ", String.valueOf(e));
                                            e.printStackTrace();
                                            displayExceptionMessage1(e.getMessage());
                                        }
                                        //-----------------------------------------------------------------------------------------------------------\\
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        dialog.dismiss();
                                        Log.e("Volley error", String.valueOf(error));
                                        error.printStackTrace();
                                        displayExceptionMessage2(error.getMessage());
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("insert", "user");
                                params.put("student_name", student_name);
                                params.put("school_id", School_Id);
                                params.put("type", type);
                                params.put("mobile", mobile);
                                params.put("email", email);
                                params.put("password", password);
                                params.put("student_class", student_class);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                        requestQueue.add(stringRequest);
                        //----------------------------------------------------------------------------------//
                    }//if(school_name="School name") ends
                    else
                        {
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Please Select The School Name..",Toast.LENGTH_SHORT).show();
                       }
                    }//if(conf_pass==pass) ends
                    else
                    {
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"password and confirm_password doesnot matched..",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please Enter All the Fields", Toast.LENGTH_SHORT).show();
                }

      }

   });//button.setonclickListener end

}//onCreate Ends







    public void FetchSchool()
    {
        StringRequest request = new StringRequest(Request.Method.POST,fetch_schools_server_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                       // Toast.makeText(RegisterActivity.this,"fetchSchool response:"+response,Toast.LENGTH_SHORT);
                        try {
                            JSONArray result = new JSONArray(response);
                            JSONObject object = null ;
                            school_id = new String[result.length()];
                            school_name = new String[result.length()];

                            for(int i = 0; i< result.length() ; i++)
                            {
                                object = result.getJSONObject(i);
                                school_id[i] = object.getString("school_id");
                                school_name[i] = object.getString("school_name");
                            }

                            for(int i = 0; i< school_id.length; i++)
                            {
                                school_id_list.add(school_id[i]);
                                school_name_list.add(school_name[i]);
                            }

                            spinner_school_list.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, school_name_list));
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
                params.put("fetch","schools");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(request);


    }

    public void displayExceptionMessage1(String msg) {
        Toast.makeText(RegisterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }
    public void displayExceptionMessage2(String msg) {
        Toast.makeText(RegisterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
    }

}