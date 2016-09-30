package com.te.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    public KeyFilesListAdapter(Context context, List<String> fileList) {
        mInflater = LayoutInflater.from(context);
        mFileList = fileList;
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
    public int getViewTypeCount() {
        return 2;//ITEM_TYPE_KEY_FILE, ITEM_TYPE_ADD, ITEM_TYPE_EXIT
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_key_file, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.id_key_file_path),
                    (ImageView) convertView.findViewById(R.id.id_add_key));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        int nViewType = getItemViewType(position);
        if(nViewType == ITEM_TYPE_ADD) {
            holder.mAddKeyFile.setVisibility(View.VISIBLE);
            holder.mKeyPath.setVisibility(View.GONE);
        } else if(nViewType == ITEM_TYPE_KEY_FILE) {
            holder.mAddKeyFile.setVisibility(View.GONE);
            holder.mKeyPath.setVisibility(View.VISIBLE);
            holder.mKeyPath.setText(mFileList.get(position));
        }
        return convertView;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView mKeyPath;
        ImageView mAddKeyFile;
        public ViewHolder(TextView keyPath, ImageView delete ){
            mKeyPath = keyPath;
            mAddKeyFile = delete;
        }
    }
}
