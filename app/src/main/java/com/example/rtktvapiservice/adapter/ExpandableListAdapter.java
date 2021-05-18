package com.example.rtktvapiservice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.rtktvapiservice.R;
import com.example.rtktvapiservice.model.Menu;
import com.example.rtktvapiservice.model.SubMenu;

import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Menu> menus;

    public ExpandableListAdapter(Context context, ArrayList<Menu> menus) {
        this.context = context;
        this.menus = menus;
    }

    @Override
    public int getGroupCount() {
        return menus.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return menus.get(groupPosition).getSubMenus().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return menus.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return menus.get(groupPosition).getSubMenus().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        try{
            Menu menu = (Menu) getGroup(groupPosition);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_menu, null);
            }
            TextView lblMenuTitle = convertView.findViewById(R.id.lblMenu);
            lblMenuTitle.setText(String.format(context.getString(R.string.regex_menu),menu.getTitle() != null? menu.getTitle(): "-", getChildrenCount(groupPosition)));
            return convertView;
        }catch (Exception e){
            Log.e("getGroupView", e.getMessage());
        }
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            SubMenu subMenu = (SubMenu)getChild(groupPosition, childPosition);
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_sub_menu, null);
            }
            TextView lblSubMenuTitle = convertView.findViewById(R.id.lblTitle);
            lblSubMenuTitle.setText(subMenu.getTitle() != null ? subMenu.getTitle(): "-");
            return convertView;
        }catch (Exception e){
            Log.e("getChildView", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
