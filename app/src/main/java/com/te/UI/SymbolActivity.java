package com.te.UI;

import java.util.Arrays;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.terminalemulation.R;

public class SymbolActivity extends AppCompatActivity implements OnItemClickListener {

	private TextView mtvSendingString;
	private Button mbtnClear;
	private GridView mGridview;
	private int[] mEditInputData = null;
	private int mCount = 0;
	private int mLimit = 0;
	private Bundle mBundle;
	private Intent mIntent;
	private ArrayAdapter<String> adapter1;
	private boolean flag = false;
	private String mEncodeString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbol_activity);

		Toolbar toolbar = (Toolbar) findViewById(R.id.symbol_act_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		RelativeLayout layOK = (RelativeLayout) toolbar.findViewById(R.id.symbol_act_toolbar_ok);
		layOK.setVisibility(View.VISIBLE);
		layOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				byte[] arr = new byte[mCount];
				for (int i = 0; i < mCount; i++) {
					arr[i] = (byte) mEditInputData[i];
				}
				String result = new String(arr);
				Bundle temp = new Bundle();
				temp.putString("data", result);
				temp.putInt("length", mCount);
				temp.putInt("Select", 2);
				mIntent.putExtras(temp);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});

		mIntent = this.getIntent();
		mtvSendingString = (TextView) findViewById(R.id.sending_string);
		mbtnClear = (Button) findViewById(R.id.btn_clear);
		mbtnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCount = 0;
				Arrays.fill(mEditInputData, 0);
				mtvSendingString.setText("");
			}
		});
		mGridview = (GridView) findViewById(R.id.gridview);
		mGridview.setAdapter(new ImageAdapter(this));
		mGridview.setOnItemClickListener(this);

		int SelectIndex = 2;
		mBundle = mIntent.getExtras();
		if (mBundle != null) {
			mLimit = mBundle.getInt("limit");
			SelectIndex = mBundle.getInt("Select");
			mEditInputData = new int[mLimit];
			mEncodeString = mBundle.getString("Encode");

			String str = mBundle.getString("data");
			if (str != null) {
				char[] c = str.toCharArray();
				if (c != null) {
					for (int i = 0; i < c.length; i++) {
						mEditInputData[mCount] = c[i] & 0xFF;
						if (mEditInputData[mCount] != 0x00)
							++mCount;
					}
					mtvSendingString.setText(CipherlabSymbol.TransformMulit(str));
					flag = true;
				}
			}
		}

		final String[] option = {getString(R.string.STR_KeyboardInput),
				getString(R.string.STR_KeyboardInput2),
				getString(R.string.STR_SymbolInput)};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
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
				tv.setLayoutParams(new GridView.LayoutParams(80, 40));
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
					tv.setBackgroundResource(R.drawable.blue);
					break;
				default:
					tv.setBackgroundResource(R.drawable.white);
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
					String currect = mtvSendingString.getText().toString();
					currect += "[" + CipherlabSymbol.ASCIIText[arg2] + "]";
					mtvSendingString.setText(currect);
					mEditInputData[mCount] = CipherlabSymbol.HEXValue[arg2];
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
					String currect = mtvSendingString.getText().toString();
					currect += CipherlabSymbol.ASCIIText[arg2];
					mtvSendingString.setText(currect);
					mEditInputData[mCount] = CipherlabSymbol.HEXValue[arg2];
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