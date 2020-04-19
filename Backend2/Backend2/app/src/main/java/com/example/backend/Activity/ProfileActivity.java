package com.example.backend.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.backend.Fragment.buttom_fragment;
import com.example.backend.R;
import com.example.backend.Adapter.HistoryAdapter;
import java.util.List;
import DAO.Impl.ItemRepoImpl;
import Model.Item;

// transfer to different specific  history page
public class ProfileActivity extends AppCompatActivity implements buttom_fragment.OnFragmentInteractionListener {
    private HistoryAdapter adapter;
    private List<Item>items;
    private ItemRepoImpl itemService=new ItemRepoImpl();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        adapter = new HistoryAdapter(this.getBaseContext());
        ListView listveiw = (ListView) findViewById(R.id.Profile_list);
        listveiw.setAdapter(adapter);
        listveiw.setOnItemClickListener(new profileListener());

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        System.out.println("inflate the menue");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout){
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
        return true;
    }

    private class profileListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = (String) adapter.getItem(position);
            Intent intent = new Intent(ProfileActivity.this, HistoryItemActivity.class);
            switch (s){
                case "Post Items":
                    System.out.println("Post");
                    intent.putExtra("Title", "Posted");
                    intent.putExtra("ShowEdit", true);
                    break;

                case "Sold Items":
                    System.out.println("Sold");
                    intent.putExtra("Title", "Sold");
                    intent.putExtra("ShowEdit", false);
                    break;

                case "Bought Items":
                    System.out.println("Bought");
                    intent.putExtra("Title", "Bought");
                    intent.putExtra("ShowEdit", false);
                    break;

                case "Favorite Items":
                    System.out.println("Favourite");
                    intent.putExtra("Title", "Favourite");
                    intent.putExtra("ShowEdit", false);
                    break;
            }

            ProfileActivity.this.startActivity(intent);
        }
    }
}
