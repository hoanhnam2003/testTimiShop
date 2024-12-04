package com.namha.testtimishop;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {
    EditText txtusername, txtpassword, txtxacnhanpassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtusername = findViewById(R.id.txt_username);
        txtpassword = findViewById(R.id.txt_password);
        txtxacnhanpassword = findViewById(R.id.txt_xacnhanpassword);
        btnRegister = findViewById(R.id.btn_dangky);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    // Xử lý đăng ký
    public void handleRegister() {
        String username = txtusername.getText().toString();
        String password = txtpassword.getText().toString();
        String xacnhanpassword = txtxacnhanpassword.getText().toString();

        // Kiểm tra thông tin đăng ký
        if (!username.isEmpty() && !password.isEmpty() && !xacnhanpassword.isEmpty()) {
            if(password.equals(xacnhanpassword)){
                // Gọi lớp AsyncTask để gửi dữ liệu đăng ký
                HttpRegister httpRegister = new HttpRegister();
                httpRegister.execute(username, password);
            } else {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        }
    }

    // Lớp AsyncTask xử lý đăng ký
    public class HttpRegister extends AsyncTask<String, Void, String> {
        private static final String SERVER = "http://192.168.1.108/API/ReadJSONAPITIMISHOP.php"; // Đổi đường dẫn API

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                String username = params[0];
                String password = params[1];

                // Tạo URL cho yêu cầu POST
                String urlString = SERVER;
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true); // Cho phép gửi dữ liệu
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);

                // Tạo dữ liệu để gửi
                String data = "username=" + username + "&password=" + password;

                // Gửi dữ liệu
                httpURLConnection.getOutputStream().write(data.getBytes("UTF-8"));
                httpURLConnection.connect();

                // Kiểm tra mã phản hồi HTTP
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                } else {
                    result = "Error: " + httpURLConnection.getResponseCode();
                }
            } catch (Exception e) {
                result = "Exception: " + e.getMessage();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    // Chuyển đổi kết quả từ String thành JSONObject
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");  // Kiểm tra trạng thái
                    String message = jsonResponse.getString("message");  // Thông báo chi tiết

                    // Kiểm tra trạng thái và thông báo
                    if (status.equals("success")) {
                        // Đăng ký thành công
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else if (status.equals("error")) {
                        // Tài khoản đã tồn tại hoặc có lỗi
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        // Thông báo lỗi chung
                        Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi khi phân tích dữ liệu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu không có phản hồi từ server
                Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
