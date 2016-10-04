package com.te.UI;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

import java.util.List;

/**
 * Created by yifan.wang on 2016/9/30.
 */
public class KeyFilesListAdapter extends BaseAdapter {
    final int ITEM_TYPE_KEY_FILE = 0;
    final int ITEM_TYPE_ADD = 1;
    private LayoutInflater mInflater = null;
    private List<String> mFileList = null;
    private View.OnClickListener mAddListener = null;
    private OnDelKeyListener mDelListener = null;

    public interface OnDelKeyListener {
        void onDelKey(int position);
    }

    public KeyFilesListAdapter(Context context, List<String> fileList) {
        mInflater = LayoutInflater.from(context);
        mFileList = fileList;
    }

    public void setClickAddListener(@Nullable View.OnClickListener l) {
        mAddListener = l;
    }
    public void setClickDelListener(@Nullable OnDelKeyListener l) {
        mDelListener = l;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFileList.indexOf(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        final int posAdd = getCount() - 1;
        if(position == posAdd) {
            return ITEM_TYPE_ADD;
        } else {
            return ITEM_TYPE_KEY_FILE;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_key_file, null);
            holder = new ViewHolder(
                    (RelativeLayout) convertView.findViewById(R.id.id_lay_key_file),
                    (TextView) convertView.findViewById(R.id.id_key_file_path),
                    (ImageView) convertView.findViewById(R.id.id_add_key),
                    (RelativeLayout) convertView.findViewById(R.id.id_del_key));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        int nViewType = getItemViewType(position);
        if(nViewType == ITEM_TYPE_ADD) {
            convertView.setEnabled(true);
            convertView.setOnClickListener(mAddListener);
            holder.mAddKeyFile.setVisibility(View.VISIBLE);
            holder.mLayKeyPath.setVisibility(View.GONE);
        } else if(nViewType == ITEM_TYPE_KEY_FILE) {
            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
            holder.mAddKeyFile.setVisibility(View.GONE);
            holder.mLayKeyPath.setVisibility(View.VISIBLE);
            holder.mKeyPath.setText(mFileList.get(position));
            holder.mDelKeyFile.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mDelListener != null) {
                                mDelListener.onDelKey(position);
                            }
                        }
                    }
            );
        }
        return convertView;
    }

    /*private view holder class*/
    private class ViewHolder {
        RelativeLayout mLayKeyPath;
        TextView mKeyPath;
        ImageView mAddKeyFile;
        RelativeLayout mDelKeyFile;
        public ViewHolder(RelativeLayout layKeyPath, TextView keyPath, ImageView addKeyFile, RelativeLayout delete ){
            mLayKeyPath = layKeyPath;
            mKeyPath = keyPath;
            mAddKeyFile = addKeyFile;
            mDelKeyFile = delete;
        }
    }
}
