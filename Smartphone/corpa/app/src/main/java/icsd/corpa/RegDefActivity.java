package icsd.corpa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class RegDefActivity extends AppCompatActivity {
    private Button register;
    private EditText title, desc;
    private LatLng latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_def);

        latlng = (LatLng) getIntent().getExtras().get("latlng");

        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        register = findViewById(R.id.regdef);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDef();
            }
        });
    }

    private void addDef() {
        if (isTextInEmpty()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder str = new StringBuilder();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        register.setEnabled(false);
                        findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    }
                });

                try {
                    JSONObject content = new JSONObject()
                            .put("description", desc.getText().toString())
                            .put("lat", String.valueOf(latlng.latitude))
                            .put("long", String.valueOf(latlng.longitude))
                            .put("name", title.getText().toString())
                            .put("photo", "null")
                            .put("problemDescription", "den uparxei kanean provlhma")
                            .put("problemType", "kanena provlhma");


                    Connection.Response loginForm = Jsoup
                            .connect("https://kostas109.pythonanywhere.com/defibrillators")
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .header("Content-Type", "application/json")
                            .requestBody(content.toString())
                            .execute();

                    if (loginForm.statusCode() != 200) str.append("something went wrong");

                } catch (Exception e) {
                    str.append("network error");
                    str.append("\n");
                    str.append(e.toString());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.loading).setVisibility(View.INVISIBLE);
                        if (str.length() > 0) {
                            Toast.makeText(RegDefActivity.this, str.toString(), Toast.LENGTH_LONG).show();
                            register.setEnabled(true);
                        } else {
                            Toast.makeText(RegDefActivity.this, "thanks!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        }).start();

    }

    private boolean isTextInEmpty() {
        boolean empty = false;
        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError("title cannot be empty");
            empty = true;
        }
        if (TextUtils.isEmpty(desc.getText().toString())) {
            desc.setError("description cannot be empty");
            empty = true;
        }
        return empty;

    }
}
