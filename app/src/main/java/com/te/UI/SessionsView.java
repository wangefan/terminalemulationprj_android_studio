package com.te.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.terminalemulation.R;

import java.util.ArrayList;
import java.util.List;

public class SessionsView extends ListView {


	public void closeAllSessionDeleteView() {
		for (int idxSession = 0; idxSession < mSessionItems.size(); idxSession++) {
			SwipeLayout swlSessionItem = (SwipeLayout)(getChildAt(idxSession - getFirstVisiblePosition()));
			if(swlSessionItem != null)
				swlSessionItem.close(true);
		}
	}

	//Inner classes begin
	private class SessionItem {
		public String mTitle;
		public boolean mIsShowDelete = false;
		SessionItem (String title) {
			mTitle = title;
		}
	}

	public class SessionItemsAdapter extends BaseSwipeAdapter {
		final int ITEM_TYPE_SESSION = 0;
		final int ITEM_TYPE_ABOUT = 1;
		final int ITEM_TYPE_EXIT = 2;

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
		public int getItemViewType(int position) {
			final int posAbout = getCount() - 2;
			final int posExit = getCount() - 1;
			if(position == posAbout) {
				return ITEM_TYPE_ABOUT;
			} else if(position == posExit) {
				return ITEM_TYPE_EXIT;
			} else {
				return ITEM_TYPE_SESSION;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3;//ITEM_TYPE_SESSION, ITEM_TYPE_ABOUT, ITEM_TYPE_EXIT
		}

		@Override
		public View generateView(final int position, ViewGroup viewGroup) {
			int nItemType = getItemViewType(position);
			View convertView;
			if(nItemType == ITEM_TYPE_SESSION) {
				convertView = mInflater.inflate(R.layout.left_menu_item, null);
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
			} else if(nItemType == ITEM_TYPE_ABOUT) {
				convertView = mInflater.inflate(R.layout.left_menu_item_about, null);
			} else { //if(nItemType == ITEM_TYPE_EXIT)
				convertView = mInflater.inflate(R.layout.left_menu_item_exit, null);
			}
			return convertView;
		}

		@Override
		public void fillValues(int position, View view) {
			SessionItem item = mSessionItems.get(position);
			int nItemType = getItemViewType(position);
			if(nItemType == ITEM_TYPE_SESSION) {
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
			} else if(nItemType == ITEM_TYPE_ABOUT) {
				RelativeLayout layAbout = (RelativeLayout) view.findViewById(R.id.about);
				layAbout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mItemListener.onItemClickAbout();
					}
				});
			} else { //if(nItemType == ITEM_TYPE_EXIT)
				RelativeLayout layExit = (RelativeLayout) view.findViewById(R.id.exit);
				layExit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mItemListener.onItemClickExit();
					}
				});
			}
		}
	}
	//Inner classes end
	public interface OnItemClickPartListener {
		void onItemClickDelete(int pos);
		void onItemClickSetting(int pos);
		void onItemClickAbout();
		void onItemClickExit();
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

	public boolean isShowDelete(int pos) {
		return mSessionItems.get(pos).mIsShowDelete;
	}

	public void addSession(String strSessionTitle) {
		mSessionItems.add(new SessionItem(strSessionTitle));
	}

	public void removeSession(int pos) {
		mSessionItems.remove(pos);
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
