package com.te.UI;

import java.util.Arrays;

import com.example.terminalemulation.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import Terminals.CipherlabSymbol;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;
import android.widget.FrameLayout;


public class SymbolActivity extends ActionBarActivity implements OnItemClickListener {

	private EditText mEdit;
	private GridView mGridview;
	private int[] InputData = null;
	private int mCount = 0;
	private int mLimit = 0;
	private Bundle mBundle;
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table);
         
		
		//setHasOptionsMenu(true);
		//ActionBar actionBar = getActionBar();
		//actionBar.show();
		
		mIntent = this.getIntent();

		mEdit = (EditText) findViewById(R.id.selection);
	 
		mEdit.setKeyListener(null);

		mGridview = (GridView) findViewById(R.id.gridview);
		//mGridview.setVisibility(View.INVISIBLE);
		mGridview.setAdapter(new ImageAdapter(this));
		mGridview.setOnItemClickListener(this);
		mBundle = mIntent.getExtras();
		if (mBundle != null) {
			mLimit = mBundle.getInt("limit");
			InputData = new int[mLimit];

			byte[] array = mBundle.getByteArray("data");
			if (array != null) {
				for (byte b : array) {
					InputData[mCount] = (int) (b & 0xFF);
					if (InputData[mCount] != 0x00)
						++mCount;
				}
				mEdit.setText(CipherlabSymbol.TransformMulit(array));
			}
		}
		
		
		LinearLayout ll_gridetableLayout=(LinearLayout)findViewById(R.id.linearLayout_gridtableLayout);  
        ll_gridetableLayout.setLayoutParams(new FrameLayout.LayoutParams(//  
                100*17,  
                LinearLayout.LayoutParams.MATCH_PARENT));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.symbol, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 case R.id.action_Clear:
			mCount = 0;
			Arrays.fill(InputData, 0);
			mEdit.setText("");
			break; 
		case R.id.action_Save:
			byte[] arr = new byte[mCount];
			for (int i = 0; i < mCount; i++) {
				arr[i] = (byte) InputData[i];
			}
			Bundle temp = new Bundle();
			temp.putByteArray("data", arr);
			temp.putInt("length", mCount);
			mIntent.putExtras(temp);
			this.setResult(RESULT_OK, mIntent);
			this.finish();
			break;
		}
		return true;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return CipherlabSymbol.ASCIIText.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(mContext);
				tv.setLayoutParams(new GridView.LayoutParams(180, 80));
				tv.setTextSize(12); // text size in gridview
				tv.setPadding(4, 4, 4, 4);
			} else {
				tv = (TextView) convertView;
			}

			tv.setText(CipherlabSymbol.ASCIIText[position]);
			switch (position) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 18:
			case 27:
			case 36:
			case 45:
			case 54:
			case 63:
			case 72:
			case 81:
			case 90:
			case 99:
			case 108:
			case 117:
			case 126:
			case 135:
			case 144:
				//tv.setBackgroundColor(getResources().getColor(R.drawable.blue));
				break;
			default:
				//tv.setBackgroundColor(getResources().getColor(R.drawable.white));
				break;
			}

			return tv;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		switch (arg2) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 18:
		case 27:
		case 36:
		case 45:
		case 54:
		case 63:
		case 72:
		case 81:
		case 90:
		case 99:
		case 108:
		case 117:
		case 126:
		case 135:
		case 144:
			break;
		case 11:
		case 12:
		case 19:
		case 20:
		case 28:
		case 29:
		case 37:
		case 38:
		case 46:
		case 47:
		case 55:
		case 56:
		case 64:
		case 65:
		case 73:
		case 74:
		case 82:
		case 83:
		case 91:
		case 92:
		case 100:
		case 101:
		case 109:
		case 110:
		case 118:
		case 119:
		case 127:
		case 128:
		case 136:
		case 137:
		case 145:
		case 146:
		case 152:
			if (mCount < mLimit) {
				String currect = mEdit.getText().toString();
				currect += "[" + CipherlabSymbol.ASCIIText[arg2] + "]";
				mEdit.setText(currect);
				InputData[mCount] = CipherlabSymbol.HEXValue[arg2];
				++mCount;
			} else {
				if (mLimit == 10) {
					Toast t = Toast.makeText(SymbolActivity.this,
							getString(R.string.MSG_CharacterTenLimit),
							Toast.LENGTH_SHORT);
					t.show();
				} else {
					Toast t = Toast.makeText(SymbolActivity.this,
							getString(R.string.MSG_CharacterOneLimit),
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
			break;
		default:
			if (mCount < mLimit) {
				String currect = mEdit.getText().toString();
				currect += CipherlabSymbol.ASCIIText[arg2];
				mEdit.setText(currect);
				InputData[mCount] = CipherlabSymbol.HEXValue[arg2];
				++mCount;
			} else {
				if (mLimit == 10) {
					Toast t = Toast.makeText(SymbolActivity.this,
							getString(R.string.MSG_CharacterTenLimit),
							Toast.LENGTH_SHORT);
					t.show();
				} else {
					Toast t = Toast.makeText(SymbolActivity.this,
							getString(R.string.MSG_CharacterOneLimit),
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
			break;
		}
	}
}