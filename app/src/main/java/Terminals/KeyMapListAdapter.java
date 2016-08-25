package Terminals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

public class KeyMapListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private KeyMapList mKeyMapItemList = null;

    public KeyMapListAdapter(Context context, KeyMapList keyMapItemList) {
        mInflater = LayoutInflater.from(context);
        mKeyMapItemList = keyMapItemList;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView mtvServerKey;
        TextView mtvPhysicalKey;
        ImageView mShift;
        ImageView mCtrl;
        ImageView mAlt;
        public ViewHolder(TextView tvServerKey, TextView tvPhysicalKey, ImageView shift, ImageView ctrl, ImageView alt ){
            mtvServerKey = tvServerKey;
            mtvPhysicalKey = tvPhysicalKey;
            mShift = shift;
            mCtrl = ctrl;
            mAlt = alt;
        }
    }

    @Override
    public int getCount() {
        return mKeyMapItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mKeyMapItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mKeyMapItemList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_key_map, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.id_server_key),
                    (TextView) convertView.findViewById(R.id.id_physical_key),
                    (ImageView) convertView.findViewById(R.id.id_shift),
                    (ImageView) convertView.findViewById(R.id.id_ctrl),
                    (ImageView) convertView.findViewById(R.id.id_alt));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mtvServerKey.setText(mKeyMapItemList.getServerKeyText(position));
        holder.mtvPhysicalKey.setText(mKeyMapItemList.getPhysicalKeyText(position));
        holder.mShift.setVisibility(mKeyMapItemList.hasShift(position) ? View.VISIBLE : View.GONE);
        holder.mCtrl.setVisibility(mKeyMapItemList.hasCtrl(position) ? View.VISIBLE : View.GONE);
        holder.mAlt.setVisibility(mKeyMapItemList.hasAlt(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }
}
