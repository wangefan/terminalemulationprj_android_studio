package com.te.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.terminalemulation.R;
import java.util.ArrayList;
import java.util.List;

public class SessionsView extends ListView {
	//Inner classes begin
	private class SessionItem {
		String mTitle;
		SessionItem (String title) {
			mTitle = title;
		}
	}
	
	public class SessionItemsAdapter extends BaseSwipeAdapter {
		private Context mContext;
		public SessionItemsAdapter(Context context) {
			mContext = context;
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
		public int getSwipeLayoutResourceId(int i) {
			return R.id.swipe;
		}

		@Override
		public View generateView(int position, ViewGroup viewGroup) {
			View convertView = mInflater.inflate(R.layout.left_menu_item, null);
			SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
			swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
			swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
			swipeLayout.addSwipeListener(new SimpleSwipeListener() {
				@Override
				public void onOpen(SwipeLayout layout) {
					//YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
				}
			});
			swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
				@Override
				public void onDoubleClick(SwipeLayout layout, boolean surface) {
					Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
				}
			});
			convertView.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
				}
			});

			return convertView;
		}

		@Override
		public void fillValues(int position, View view) {
			SessionItem item = mSessionItems.get(position);
			TextView tvTitle = (TextView) view.findViewById(R.id.title);
			tvTitle.setText(item.mTitle);
		}
	}
	//Inner classes end

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

	public boolean isSNItemShowDelete(int position) {
		View item = getChildAt(position);
		if(item == null)
			return false;

		RelativeLayout layRemove = (RelativeLayout) item.findViewById(R.id.delete_layout);
		if(layRemove != null && layRemove.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}
}
