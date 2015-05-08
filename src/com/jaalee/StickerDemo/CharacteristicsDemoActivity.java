package com.jaalee.StickerDemo;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jaalee.StickerDemo.R;
import com.jaalee.sdk.Sticker;
import com.jaalee.sdk.connection.ReadCallback;
import com.jaalee.sdk.connection.StickerCharacteristics;
import com.jaalee.sdk.connection.StickerConnection;
import com.jaalee.sdk.connection.ConnectionCallback;
import com.jaalee.sdk.connection.WriteCallback;

/**
 * http://www.jaalee.com/
 * @author JAALEE, Inc.
 * We have been trying to provide better services and products! Jaalee Beacon makes 
 * life more simple and cheerful! If you are interested in our product, 
 * please contact us in following ways. We will provide the best service wholeheartedly for you!
 * 
 * Buy Jaalee Beacon: sales@jaalee.com
 * 
 * Technical Support: dev@jaalee.com
 * 
 */

public class CharacteristicsDemoActivity extends Activity {

	private Sticker Sticker;
	private StickerConnection connection;

	private TextView statusView;
	private TextView StickerDetailsView;
	private View afterConnectedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.characteristics_demo);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		statusView = (TextView) findViewById(R.id.status);
		StickerDetailsView = (TextView) findViewById(R.id.Sticker_details);
		afterConnectedView = findViewById(R.id.after_connected);

		Sticker = getIntent().getParcelableExtra(
				ListStickersActivity.EXTRAS_STICKER);
		connection = new StickerConnection(this, Sticker,
				createConnectionCallback());

		findViewById(R.id.Call).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//connection.CallSticker();
				connection.CallSticker();

			}
		});
		
		findViewById(R.id.BatteryLevel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//connection.CallSticker();
				connection.ReadBatteryLevel(new ReadCallback() {
					
					@Override
					public void onReadStringValue(String value) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onReadIntegerValue(Integer value) {
						// TODO Auto-generated method stub
						Log.i("JAALEE Debug", "JAALEE Debug: JAALEE Batt:"+ value + "%");
					}
					
					@Override
					public void onError() {
						// TODO Auto-generated method stub
						Log.i("JAALEE Debug", "JAALEE Debug: JAALEE Batt:Read battery level failed");
					}
				});

			}
		});	
		
		findViewById(R.id.ChangeName).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//connection.CallSticker();
				connection.writeStickerName("JAALEE", new WriteCallback() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.i("JAALEE Debug", "JAALEE Debug: Device name change successfully");
					}
					
					@Override
					public void onError() {
						// TODO Auto-generated method stub
						Log.i("JAALEE Debug", "JAALEE Debug: Device name change Failed");
					}
				});

			}
		});			
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!connection.isConnected()) {
			statusView.setText("Status: Connecting...");
			connection.connectStickerWithPassword("666666");
		}
	}

	@Override
	protected void onDestroy() {
		connection.disconnect();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ConnectionCallback createConnectionCallback() {
		return new ConnectionCallback() {
			@Override
			public void onAuthenticated(
					final StickerCharacteristics paramCharacteristics) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						statusView.setText("Status: Connected to Sticker");
						StringBuilder sb = new StringBuilder().append(
								"Device Name: ").append(
								paramCharacteristics.getStickerName()).append("\n")
								.append("Battery Level: ").append(paramCharacteristics.getBatteryPercent());
						StickerDetailsView.setText(sb.toString());
						afterConnectedView.setVisibility(View.VISIBLE);
					}
				});
			}

			@Override
			public void onAuthenticationError() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						statusView
								.setText("Status: Cannot connect to Sticker. Authentication problems.");
					}
				});
			}

			@Override
			public void onDisconnected() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						statusView.setText("Status: Disconnected from Sticker");
					}
				});
			}

			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi,
					int status) {
				// TODO Auto-generated method stub

			}
		};
	}
}
