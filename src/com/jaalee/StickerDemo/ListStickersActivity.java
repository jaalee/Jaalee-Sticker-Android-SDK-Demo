package com.jaalee.StickerDemo;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jaalee.StickerDemo.R;
import com.jaalee.sdk.DiscoverListener;
import com.jaalee.sdk.Sticker;
import com.jaalee.sdk.StickerManager;
import com.jaalee.sdk.ServiceReadyCallback;
import com.jaalee.sdk.utils.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class ListStickersActivity extends Activity {

  private static final String TAG = ListStickersActivity.class.getSimpleName();

  public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
  public static final String EXTRAS_STICKER = "extrasSticker";

  private static final int REQUEST_ENABLE_BT = 1234;
  
  private StickerManager stickerManager;
  private LeDeviceListAdapter adapter;
  private ArrayList<Sticker> filteredSticker = new ArrayList<Sticker>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Configure device list.
    adapter = new LeDeviceListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);
    list.setOnItemClickListener(createOnItemClickListener());
        

    // Configure verbose debug logging.
    L.enableDebugLogging(false);

    // Configure StickerManager.
    stickerManager = new StickerManager(this);
    stickerManager.setDiscoverListener(new DiscoverListener() {
		
    	@Override
		public void onStickersDiscovered(final Sticker param) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
	          @Override
	          public void run() {
	            // Note that Stickers reported here are already sorted by estimated
	            // distance between device and Sticker.
	            List<Sticker> Stickers = filterStickers(param);
	            getActionBar().setSubtitle("Found Stickers: " + Stickers.size());
	            adapter.replaceWith(Stickers);
	          }
	        });
		}
    });
  }

  private List<Sticker> filterStickers(Sticker sticker) {
    
	  for (int i = 0; i < filteredSticker.size(); i++)
	  {
		  Sticker temp = filteredSticker.get(i);
		  if (temp.getMacAddress().equalsIgnoreCase(sticker.getMacAddress()))
		  {
			  return filteredSticker;
		  }
	  }
	  
	  filteredSticker.add(sticker);
	  
	  return filteredSticker;
    
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.scan_menu, menu);
    MenuItem refreshItem = menu.findItem(R.id.refresh);
    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
	  stickerManager.disconnect();

    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check if device supports Bluetooth Low Energy.
    if (!stickerManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    // If Bluetooth is not enabled, let user enable it.
    if (!stickerManager.isBluetoothEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      connectToService();
    }
  }

  @Override
  protected void onStop() {
	  try {
		stickerManager.stopDiscover();
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    super.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        connectToService();
      } else {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        getActionBar().setSubtitle("Bluetooth not enabled");
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void connectToService() {
    getActionBar().setSubtitle("Scanning...");
    adapter.replaceWith(Collections.<Sticker>emptyList());
    stickerManager.connect(new ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
    	  try {
			stickerManager.startDiscover();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    });
  }

  private AdapterView.OnItemClickListener createOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
          try {
            Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
            Intent intent = new Intent(ListStickersActivity.this, clazz);
            intent.putExtra(EXTRAS_STICKER, adapter.getItem(position));
            startActivity(intent);
          } catch (ClassNotFoundException e) {
            Log.e(TAG, "Finding class by name failed", e);
          }
        }
      }
    };
  }

}
