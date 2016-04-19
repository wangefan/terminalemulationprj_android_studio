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
import android.widget.TextView;

public class SessionsView extends ListView implements View.OnTouchListener {
	//Inner classes begin
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

	private class MySimpleGesture extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(BuildConfig.DEBUG) {
				Log.d("TE", "onFling");
			}
			final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;
			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				// Fling right
				if(BuildConfig.DEBUG) {
					Log.d("TE-MyGesture", "Fling right");
				}
				int pos = pointToPosition((int)e1.getX(), (int)e1.getY());
				View item = getChildAt(pos);
				if (item != null) {
					if(BuildConfig.DEBUG)  Log.d("TE-MyGesture", "item = " + String.valueOf(pos));
					if(isSNItemShowDelete(pos) == false) {
						if(BuildConfig.DEBUG) Log.d("TE-MyGesture", "show delete");
						snItemShowDelete(pos, true);
						return false;
					}
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	private abstract class ViewAnimationListener implements Animation.AnimationListener {

		private final View mView;

		protected ViewAnimationListener(View view) {
			mView = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			onAnimationStart(mView, animation);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			onAnimationEnd(mView, animation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		protected abstract void onAnimationStart(View view, Animation animation);
		protected abstract void onAnimationEnd(View view, Animation animation);
	}
	//Inner classes end

	//Members
	private LayoutInflater mInflater;
	private List<SessionItem> mSessionItems = new ArrayList<SessionItem>();
	private GestureDetector mGestDetector = new GestureDetector(getContext(), new MySimpleGesture());

	public SessionsView(Context context) {
		super(context);
		this.setAdapter(new SessionItemsAdapter(context));
		setOnTouchListener(this);
	}
	
	public SessionsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setAdapter(new SessionItemsAdapter(context));
		setOnTouchListener(this);
	}
		
	public SessionsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setAdapter(new SessionItemsAdapter(context));
		setOnTouchListener(this);
	}
	
	//Functions
	private void animateShowView(View view, Animation animation) {
		if (animation != null && view != null) {
			animation.setAnimationListener(new ViewAnimationListener(view) {
				@Override
				protected void onAnimationStart(View view, Animation animation) {
					view.setVisibility(View.VISIBLE);
				}

				@Override
				protected void onAnimationEnd(View view, Animation animation) {

				}
			});
			view.startAnimation(animation);
		}
	}

	private void animateHideView(View view, Animation animation) {
		if (animation != null && view != null) {
			animation.setAnimationListener(new ViewAnimationListener(view) {
				@Override
				protected void onAnimationStart(View view, Animation animation) {
				}

				@Override
				protected void onAnimationEnd(View view, Animation animation) {
					view.setVisibility(View.GONE);
				}
			});
			view.startAnimation(animation);
		}
	}

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

		LinearLayout layRemove = (LinearLayout) item.findViewById(R.id.remove_layout);
		if(layRemove != null && layRemove.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}

	public void snItemShowDelete(int position, boolean bShow) {
		View item = getChildAt(position);
		if(item == null)
			return;
		LinearLayout layRemove = (LinearLayout) item.findViewById(R.id.remove_layout);
		LinearLayout laySession = (LinearLayout) item.findViewById(R.id.session_layout);
		if(layRemove != null && laySession != null) {
			if(bShow) {
				animateShowView(layRemove, AnimationUtils.loadAnimation(layRemove.getContext(), R.anim.fade_in));
				animateHideView(laySession, AnimationUtils.loadAnimation(layRemove.getContext(), R.anim.slide_out));
			} else {
				animateHideView(layRemove, AnimationUtils.loadAnimation(layRemove.getContext(), R.anim.fade_out));
				animateShowView(laySession, AnimationUtils.loadAnimation(layRemove.getContext(), R.anim.slide_in));
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(mGestDetector.onTouchEvent(event)) {
			Log.d("TE-SessionView.onTouch", "mGestDetector.onTouchEvent() return true");
			return true;
		}
		Log.d("TE-SessionView.onTouch", "mGestDetector.onTouchEvent() return false");
		return super.onTouchEvent(event);
	}
}
