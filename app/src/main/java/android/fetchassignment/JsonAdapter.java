package android.fetchassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonAdapter extends RecyclerView.Adapter<JsonAdapter.ViewHolder> {

    private List<JSONObject> itemList;

    public JsonAdapter(List<JSONObject> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize layout views
            textView = itemView.findViewById(R.id.textViewItem);
        }
    }

    @Override
    public JsonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JsonAdapter.ViewHolder holder, int position) {
        try {
            JSONObject item = itemList.get(position);
            String name = item.optString("name", "Default Name");
            int listId = item.optInt("listId");
            // Set the name to the text view
            holder.textView.setText("listId: " + listId + ", Name: " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
