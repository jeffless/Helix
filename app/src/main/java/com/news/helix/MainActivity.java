package com.news.helix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create array
        String[] list = {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4",
                "Item 5",
                "Item 6"
        };
        ListAdapter mainAdapter = new CustomAdapter(this, list); //convert array to list
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setAdapter(mainAdapter);

        mainListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String list = String.valueOf(adapterView.getItemAtPosition(i));
                        Toast.makeText(MainActivity.this, list, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
