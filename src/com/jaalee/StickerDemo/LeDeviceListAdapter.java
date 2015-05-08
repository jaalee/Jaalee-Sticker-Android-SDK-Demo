package com.jaalee.StickerDemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaalee.StickerDemo.R;
import com.jaalee.sdk.Sticker;

import java.util.ArrayList;
import java.util.Collection;


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
public class LeDeviceListAdapter extends BaseAdapter {

  private ArrayList<Sticker> stickers;
  private LayoutInflater inflater;

  public LeDeviceListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.stickers = new ArrayList<Sticker>();
  }

  public void replaceWith(Collection<Sticker> newStickers) {
    this.stickers.clear();
    this.stickers.addAll(newStickers);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return stickers.size();
  }

  @Override
  public Sticker getItem(int position) {
    return stickers.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

  private void bind(Sticker Sticker, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.macTextView.setText(String.format("MAC: %s", Sticker.getMacAddress()));
    holder.rssiTextView.setText("RSSI: " + Sticker.getRssi());
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.device_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView rssiTextView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
    }
  }
}
