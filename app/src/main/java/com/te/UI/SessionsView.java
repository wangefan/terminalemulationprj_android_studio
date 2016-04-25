package com.te.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.terminalemulation.R;

import java.util.ArrayList;
import java.util.List;

public class SessionsView extends ListView {


	//Inner classes begin
	private class SessionItem {
		public String mTitle;
		public boolean mIsShowDelete = false;
		SessionItem (String title) {
			mTitle = title;
		}
	}

	public class SessionItemsAdapter extends BaseSwipeAdapter {
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
		public int getSwipeLayoutResourceId(int i) {
			return R.id.swipe;
		}

		@Override
		public View generateView(final int position, ViewGroup viewGroup) {
			View convertView = mInflater.inflate(R.layout.left_menu_item, null);
			SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
			swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
			swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
			swipeLayout.addSwipeListener(new SimpleSwipeListener() {
				@Override
				public void onOpen(SwipeLayout layout) {
					SessionItem item = (SessionItem) getItem(position);
					item.mIsShowDelete = true;
				}
				public void onClose(SwipeLayout layout) {
					SessionItem item = (SessionItem) getItem(position);
					item.mIsShowDelete = false;
				}
			});
			return convertView;
		}

		@Override
		public void fillValues(int position, View view) {
			SessionItem item = mSessionItems.get(position);
			TextView tvTitle = (TextView) view.findViewById(R.id.title);
			tvTitle.setText(item.mTitle);
			final int posPass = position;

			view.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mItemListener != null) {
						mItemListener.onItemClickDelete(posPass);
					}
				}
			});

			view.findViewById(R.id.session_setting_layout).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mItemListener != null) {
						mItemListener.onItemClickSetting(posPass);
					}
				}
			});
		}
	}
	//Inner classes end
	public interface OnItemClickPartListener {
		void onItemClickDelete(int pos);
		void onItemClickSetting(int pos);
	}

	//Members
	private LayoutInflater mInflater;
	private List<SessionItem> mSessionItems = new ArrayList<SessionItem>();
	private OnItemClickPartListener mItemListener;

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
	public void setOnItemClickPartListener(OnItemClickPartListener itemListener) {
		mItemListener = itemListener;
	}

	public void addSession(String strSessionTitle) {
		mSessionItems.add(new SessionItem(strSessionTitle));
	}

	public void removeAllSessions() {
		mSessionItems.clear();
	}

	public void refresh() {
		SessionItemsAdapter adapter = new SessionItemsAdapter(getContext());
		if(adapter != null) {
			setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	public void setSelected(int nPos) {
		setItemChecked(nPos, true);
	}

	public String getSessionTitle(int nPos) {
		return mSessionItems.get(nPos).mTitle;
	}
}
