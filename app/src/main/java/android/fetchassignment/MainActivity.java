package android.fetchassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    RecyclerView recyclerView;
    JsonAdapter jsonAdapter;
    List<JSONObject> jObjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        read_button = findViewById(R.id.read_button);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // Initialize the adapter with the empty list
        jsonAdapter = new JsonAdapter(jObjectList);
        recyclerView.setAdapter(jsonAdapter);

        read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
    }

    public void updateRecyclerViewData(List<JSONObject> newList) {
        jObjectList.clear();
        jObjectList.addAll(newList);
        jsonAdapter.notifyDataSetChanged();
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
                                String name = jsonObject.optString("name", "null");

                                //If name is null, skip this json object
                                if (name == "null" || name.trim().isEmpty()) {
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

                                        String s1 = name1.substring(5, name1.length());
                                        String s2 = name2.substring(5, name2.length());

                                        int num1 = Integer.parseInt(s1);
                                        int num2 = Integer.parseInt(s2);

                                        return Integer.compare(num1, num2);
                                    }
                                }
                            });

                            // Update the RecyclerView with the sorted list
                            updateRecyclerViewData(jObjectList);

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