package com.te.UI;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import com.example.terminalemulation.R;
import android.content.Context;
import android.support.graphics.drawable.BuildConfig;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SessionsView extends ListView {
	//Inner classes begin
	private class SessionItem {
		String mTitle;
		SessionItem (String title) {
			mTitle = title;
		}
	}
	
	public class SessionItemsAdapter extends BaseAdapter {
		private class SwipeDetector implements View.OnTouchListener {
			private static final int MIN_DISTANCE = 50;
			private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept
			private boolean mBMotionInterceptDisallowed = false;
			private float mDownX, mUpX;
			private SessionItemsAdapter.SessionItemViewHolder holder;
			private int position;

			public SwipeDetector(SessionItemsAdapter.SessionItemViewHolder h, int pos) {
				holder = h;
				position = pos;
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						mDownX = event.getX();
						return true; // allow other events like Click to be processed
					}

					case MotionEvent.ACTION_MOVE: {
						mUpX = event.getX();
						float deltaX = mDownX - mUpX;

						if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && !mBMotionInterceptDisallowed) {
							requestDisallowInterceptTouchEvent(true);
							mBMotionInterceptDisallowed = true;
						}

						if (deltaX > 0) {
							holder.mDeleteItemView.setVisibility(View.GONE);
						} else {
							// if first swiped left and then swiped right
							holder.mDeleteItemView.setVisibility(View.VISIBLE);
						}

						swipe(holder.mSessionItemView, -(int) deltaX);
						return true;
					}

					case MotionEvent.ACTION_UP:
						mUpX = event.getX();
						float deltaX = mUpX - mDownX;
						if (Math.abs(deltaX) > MIN_DISTANCE) {
							holder.mDeleteItemView.setVisibility(View.VISIBLE);
						} else {
							holder.mDeleteItemView.setVisibility(View.GONE);
							swipe(holder.mSessionItemView, 0);
						}
						requestDisallowInterceptTouchEvent(false);
						mBMotionInterceptDisallowed = false;
						return true;

					case MotionEvent.ACTION_CANCEL:
						holder.mDeleteItemView.setVisibility(View.VISIBLE);
						return false;
				}

				return true;
			}
		}
		public SessionItemsAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		private void swipe(View view, int distance) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
			params.rightMargin = -distance;
			params.leftMargin = distance;
			view.setLayoutParams(params);
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
				holder.mSessionItemView = (LinearLayout) convertView.findViewById(R.id.session_layout);
				holder.mDeleteItemView = (RelativeLayout) convertView.findViewById(R.id.delete_layout);
				convertView.setTag(holder);
			} else {
				holder = (SessionItemViewHolder) convertView.getTag();
			}
			
			SessionItem item = mSessionItems.get(position);
			holder.mTvTitle.setText(item.mTitle);
			swipe(holder.mSessionItemView, 0); //rollback session item
			convertView.setOnTouchListener(new SwipeDetector(holder, position));

			return convertView;
		}
		
		private class SessionItemViewHolder {
			public TextView mTvTitle;
			public LinearLayout mSessionItemView;
			public RelativeLayout mDeleteItemView;
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
