package com.fyx.adapter;

import java.util.ArrayList;
import java.util.List;

import com.fyx.nongji.R;
import com.nongji.vo.SearchUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	ArrayList<SearchUser> sList = new ArrayList<SearchUser>();

	public UserListAdapter(Context c, ArrayList<SearchUser> list) {
		super();
		inflater = LayoutInflater.from(c);
		sList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.searchuserlist, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.userName);
			viewHolder.userPhone = (TextView) convertView
					.findViewById(R.id.userPhone);
			viewHolder.userState = (TextView) convertView
					.findViewById(R.id.tvstate);
			viewHolder.userID = (TextView) convertView
					.findViewById(R.id.tvID);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.userName.setText(sList.get(position).getUserName());
		viewHolder.userPhone.setText(sList.get(position).getPhone());
		viewHolder.userID.setText(sList.get(position).getUserID());
		int stateInt = sList.get(position).getState();
		if (stateInt == 0) {
			viewHolder.userState.setText("闲");
		} else if (stateInt == 1) {
			viewHolder.userState.setText("忙");
		} else {
			viewHolder.userState.setText("障");
		}

		return convertView;
	}

	private static class ViewHolder {
		private TextView userName, userPhone, userState, userID;
	}

}
