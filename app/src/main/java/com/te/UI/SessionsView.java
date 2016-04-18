package com.te.UI;

import java.util.ArrayList;
import java.util.List;
import com.example.terminalemulation.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SessionsView extends ListView {
	private class SessionItem {
		String mTitle;
		SessionItem (String title) {
			mTitle = title;
		}
	}
	
	public class SessionItemsAdapter extends BaseAdapter {
		public SessionItemsAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mSessionItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mSessionItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final SessionItemViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.left_menu_item, parent, false);
				holder = new SessionItemViewHolder();
				holder.mTvTitle = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (SessionItemViewHolder) convertView.getTag();
			}
			
			SessionItem item = mSessionItems.get(position);
			holder.mTvTitle.setText(item.mTitle);
			
			return convertView;
		}
		
		private class SessionItemViewHolder {
			public TextView mTvTitle;
		}
	}
	
	//Members
	private LayoutInflater mInflater;
	private List<SessionItem> mSessionItems = new ArrayList<SessionItem>();
	
	public SessionsView(Context context) {
		super(context);
		this.setAdapter(new SessionItemsAdapter(context));
	}
	
	public SessionsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setAdapter(new SessionItemsAdapter(context));
	}
		
	public SessionsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setAdapter(new SessionItemsAdapter(context));
	}
	
	//Functions
	public void addSession(String strSessionTitle) {
		mSessionItems.add(new SessionItem(strSessionTitle));
	}
	
	public void setSelected(int nPos) {
		setItemChecked(nPos, true);
	}
	
	public String getSessionTitle(int nPos) {
		return mSessionItems.get(nPos).mTitle;
	}
}
