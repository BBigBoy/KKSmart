package com.kksmartcontrol.adapter;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.kksmartcontrol.R;
import com.kksmartcontrol.bean.VideoInfoBean;

public class PlayListAdapter extends BaseAdapter implements SectionIndexer {
	private List<VideoInfoBean> list = null;
	private Context mContext;

	public PlayListAdapter(Context mContext, List<VideoInfoBean> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<VideoInfoBean> list) {
		this.list = list;
		notifyDataSetChanged(); 
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final VideoInfoBean videoInfo = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			Log.d("LayoutInflater", "LayoutInflater");
			view = LayoutInflater.from(mContext).inflate(
					R.layout.xlistview_item, arg2, false);
			viewHolder.source_Name = (TextView) view
					.findViewById(R.id.source_name);
			viewHolder.format = (TextView) view.findViewById(R.id.format);
			viewHolder.timelength = (TextView) view
					.findViewById(R.id.timelength);
			viewHolder.tvLetter = (TextView) view
					.findViewById(R.id.sort_letter);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(videoInfo.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		view.setBackgroundResource(R.color.white);
		viewHolder.source_Name.setText(videoInfo.getName());
		viewHolder.format.setText(videoInfo.getFormat());
		viewHolder.timelength.setText(videoInfo.getTime_Length());
		Log.d("ListVieeItem", videoInfo.getName());
		return view;

	}

	final static class ViewHolder {

		TextView tvLetter;
		TextView source_Name;
		TextView format;
		TextView timelength;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
