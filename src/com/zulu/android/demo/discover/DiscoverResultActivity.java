package com.zulu.android.demo.discover;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.util.NearbyUserDetails;

public class DiscoverResultActivity extends Activity
	implements AdapterView.OnItemClickListener{

	private NearbyStatusListAdapter m_adapter;
	private ListView m_NearbyUserListView;

	public ArrayList<NearbyUserDetails> getFilteredUsers(){
		return Zulu.nearbyUsers;
	}

	/**
	 * onCreate
	 */
	protected void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		this.m_adapter = new NearbyStatusListAdapter(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.discoverresult__main);
		this.m_NearbyUserListView = ((ListView)findViewById(R.id.discoverresult_id_list));
		this.m_NearbyUserListView.setAdapter(this.m_adapter);
		this.m_NearbyUserListView.setOnItemClickListener(this);
	}

	/**
	 * Adapter
	 * 
	 * @author nairan
	 *
	 */
	protected class NearbyStatusListAdapter extends BaseAdapter {

		private Context m_context = null;

		public NearbyStatusListAdapter(Context ctx){
			this.m_context = ctx;
		}

		public int getCount(){
			return getFilteredUsers().size();
		}

		public NearbyUserDetails getItem(int position){
			if (getFilteredUsers() != null)
				return getFilteredUsers().get(position);
			return null;
		}

		public long getItemId(int position){
			return getFilteredUsers().get(position).hashCode();
		}

		public View getView(int position, View convertView, ViewGroup container){
			NearbyUserDetails discoverResultItem = (NearbyUserDetails) getItem(position);
			if (convertView == null) // have to create a new view
				convertView = ((LayoutInflater)this.m_context.getSystemService("layout_inflater")).inflate(R.layout.discoverresult_user_list_item, null);

			if (position % 2 == 0){
				convertView.setBackgroundResource(R.drawable.discoverresult_drawable_list_item_even);
			} else {
				convertView.setBackgroundResource(R.drawable.discoverresult_drawable_list_item_odd);
			}
			if (discoverResultItem != null){
				((TextView) convertView.findViewById(R.id.discoverresult_id_list_item_name)).setText(discoverResultItem.getName());
				((TextView) convertView.findViewById(R.id.discoverresult_id_list_item_status)).setText(discoverResultItem.getStatus());
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NearbyUserDetails selectedValue = (NearbyUserDetails) parent.getItemAtPosition(position);
		Zulu.selectedNearbyZulu = selectedValue;
    	Intent myIntent = new Intent(DiscoverResultActivity.this, ShowResultItemActivity.class);
    	DiscoverResultActivity.this.startActivity(myIntent);
	}
}