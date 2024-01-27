package android.fetchassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button read_button;
    TextView results;
    String url = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        read_button = findViewById(R.id.read_button);
        results = findViewById(R.id.results);

        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
    }

    public void retrieveData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<JSONObject> jObjectList = new ArrayList<>();
                        try {
                            // Iterate the jsonArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.optString("name", null);

                                //If name is null, skip this json object
                                if (name == null || name.trim().isEmpty()) {
                                    continue;
                                }

                                //Add all the eligible objects to the list
                                jObjectList.add(jsonObject);
                            }


                            Collections.sort(jObjectList, new Comparator<JSONObject>() {
                                @Override
                                public int compare(JSONObject o1, JSONObject o2) {
                                    int listId1 = o1.optInt("listId");
                                    int listId2 = o2.optInt("listId");
                                    if (listId1 != listId2) {
                                        return Integer.compare(listId1, listId2);
                                    } else {
                                        String name1 = o1.optString("name");
                                        String name2 = o2.optString("name");
                                        return name1.compareToIgnoreCase(name2);
                                    }
                                }
                            });

                            //Update UI

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(jRequest); // Start the request
    }


}