package com.example.rtktvapiservice;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtktvapiservice.adapter.ExpandableListAdapter;
import com.example.rtktvapiservice.model.Menu;
import com.example.rtktvapiservice.model.SubMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private TextView lblNoRecord;
    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupUI();
    }

    private void setupUI(){
        expandableListView = findViewById(R.id.expListView);
        lblNoRecord = findViewById(R.id.lblNoRecord);
        prepareListData();
    }

    private void prepareListData(){
        ArrayList<Menu> menus = setupMenuFromJson();
        try {
            listAdapter = new ExpandableListAdapter(this, menus);
            expandableListView.setAdapter(listAdapter);
            if(menus.size() > 0){
                lblNoRecord.setVisibility(View.GONE);
            }else{
                lblNoRecord.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.e("prepareListData", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String loadJSONFromAsset(){
        String json = null;
        try{
            InputStream is = getAssets().open("menu.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }catch (Exception e){
            Log.e("loadJSONFromAsset", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return json;
    }

    private ArrayList<Menu> setupMenuFromJson(){
        ArrayList<Menu> menus = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray menuJArray = obj.getJSONArray("menuItem");

            for (int i = 0; i < menuJArray.length(); i++) {
                ArrayList<SubMenu> subMenus = new ArrayList<>();
                JSONObject jObject = menuJArray.getJSONObject(i);
                String title = jObject.getString("title");
                Log.d("Title -->", title);
                JSONArray subMenuJArray = jObject.getJSONArray("subMenu");
                Log.d("SubMenu Size -->", subMenuJArray.length()+ "!!");

                for (int j = 0; j< subMenuJArray.length(); j++) {
                    JSONObject subMenuJObject = subMenuJArray.getJSONObject(j);
                    String subMenuTitle=  subMenuJObject.getString("title");
                    Log.d("subMenu", subMenuTitle);
                    SubMenu subMenu = new SubMenu();
                    subMenu.setTitle(subMenuTitle);
                    subMenus.add(subMenu);
                }
                Menu menu = new Menu();
                menu.setTitle(title);
                menu.setSubMenus(subMenus);
                menus.add(menu);
            }
        } catch (JSONException e) {
            Log.e("menuJsonExp", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }catch (Exception e1){

            Log.e("menuJsonErr", e1.getMessage());
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
        return menus;
    }

}