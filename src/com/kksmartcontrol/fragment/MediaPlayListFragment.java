package com.kksmartcontrol.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.kksmartcontrol.R;
import com.kksmartcontrol.util.FragmentUtil;
import com.kksmartcontrol.util.ToastUtil;
import com.xlistview.adapter.PlayListAdapter;
import com.xlistview.handle.CharacterParser;
import com.xlistview.handle.PinyinComparator;
import com.xlistview.model.PlayListItemModel;
import com.xlistview.sidebar.SideBar;
import com.xlistview.sidebar.SideBar.OnTouchingLetterChangedListener;
import com.xlistview.xlistview.XListView;
import com.xlistview.xlistview.XListView.IXListViewListener;
import com.xml.sax.SaxService;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MediaPlayListFragment extends Fragment implements
		IXListViewListener, OnDragListener {

	private SideBar sideBar;
	private TextView dialog;
	private XListView playListView;
	private Handler mHandler;

	private PlayListAdapter adapter;

	private Context context = null;

	private View selectListViewItem = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = getActivity();
		Log.d("MediaPlayListFragment", "onAttach");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.xlistview_videolist, container,
				false);

		Log.d("MediaPlayListFragment", "onCreateView");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ListView_Init(getView());
		Log.d("MediaPlayListFragment", "onActivityCreated");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("MediaPlayListFragment", "onDestroy");
	}

	/**
	 * 初始化ListView，包括sidebar
	 */
	private void ListView_Init(View view) {

		sideBar = (SideBar) view.findViewById(R.id.sidrbar);
		dialog = (TextView) view.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					playListView.setSelection(position + 1);
				}
			}
		});

		playListView = (XListView) view.findViewById(R.id.list);
		new FillListViewTask().execute();
		playListView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		playListView.setXListViewListener(this);
		mHandler = new Handler();
		playListView.setOnDragListener(this);
		playListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				ToastUtil.showToast(context, ((PlayListItemModel) adapter
						.getItem(position - 1)).getName(), Toast.LENGTH_SHORT);
			}
		});
		playListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				TextView sourceName = (TextView) v
						.findViewById(R.id.source_name);
				View dragView = (LinearLayout) v.findViewById(R.id.itemlayout);
				dragView.setBackgroundResource(R.color.red);
				selectListViewItem = dragView;

				ClipData dragData = ClipData.newPlainText("sourceName",
						sourceName.getText());
				DragShadowBuilder myShadow = new DragShadowBuilder(
						dragView);
				String localState = "fromListViewItem";
				// VideoPreFragment videoPreFragment = (VideoPreFragment)
				// getActivity()
				// .getFragmentManager().findFragmentByTag(
				// "VideoPreFragment");
				// if (videoPreFragment == null)
				// videoPreFragment = new VideoPreFragment();
				// android.app.FragmentTransaction fragmentTransaction =
				// getActivity()
				// .getFragmentManager().beginTransaction();
				// VideoPlayFragment videoPlayFragment = (VideoPlayFragment)
				// getActivity()
				// .getFragmentManager().findFragmentByTag(
				// "VideoPlayFragment");
				// if (videoPreFragment.isAdded()) {
				// if (videoPreFragment.isHidden()) {
				// if (videoPlayFragment != null
				// && videoPlayFragment.isAdded()) {
				// getActivity().getFragmentManager().popBackStack();
				// } else {
				// fragmentTransaction.show(videoPreFragment);
				// }
				// }
				// } else {
				// fragmentTransaction.add(R.id.displaylayout,
				// videoPreFragment, "VideoPreFragment");
				// }
				// fragmentTransaction.commit();
				FragmentUtil.removeVisibleFragmentByTag(context,
						"VideoPlayFragment", R.animator.fragmentexit);
				FragmentUtil.showExistingFragmentByTag(context, "VideoPreFragment");
				// Starts the drag
				v.startDrag(dragData, myShadow, localState, 0);
				return false;
			}
		});
	}

	/** 停止刷新， */
	private void stopLoad() {
		playListView.stopRefresh();
		playListView.stopLoadMore();
		playListView.setRefreshTime("刚刚");
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		// adapter.updateListView(SourceDataList);
		// playListView.setAdapter(adapter);
		new FillListViewTask().execute();

	}

	// 上拉刷新及加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// getData();
				ToastUtil.showToast(context, "上拉刷新在这里更新数据", Toast.LENGTH_LONG);
				stopLoad();
			}
		}, 1000);
	}

	private List<HashMap<String, String>> getListFromXML() {
		// String path = Environment.getExternalStorageDirectory().getPath()
		// + "/videoList.xml";
		AssetManager assetManager = getResources().getAssets();
		try {
			InputStream inputStream = assetManager.open("videoList.xml");

			// InputStream inputStream = new FileInputStream(path);
			List<HashMap<String, String>> list = SaxService.readXML(
					inputStream, "video");
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 为ListView填充数据
	 *
	 * @return
	 */
	private List<PlayListItemModel> filledData(
			List<HashMap<String, String>> videoList) {

		// 汉字转换成拼音的类
		CharacterParser characterParser = CharacterParser.getInstance();
		List<PlayListItemModel> mSortList = new ArrayList<PlayListItemModel>();

		for (HashMap<String, String> listItem : videoList) {
			PlayListItemModel sortModel = new PlayListItemModel();
			String path = listItem.get("path");
			String fileName = path.substring(path.lastIndexOf("\\") + 1);
			String time_length = listItem.get("time_length");
			Log.d(path, path);
			Log.d(time_length, time_length);
			Log.d(fileName, fileName);
			sortModel.setName(fileName.split("\\.")[0]);
			sortModel.setFormat(fileName.split("\\.")[1]);
			sortModel.setTime_Length(time_length);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(fileName);
			String sortString = pinyin.substring(0, 1).toUpperCase(
					Locale.ENGLISH);
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel
						.setSortLetters(sortString.toUpperCase(Locale.ENGLISH));
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {

		case DragEvent.ACTION_DRAG_ENDED:
			selectListViewItem.setBackgroundResource(R.color.white);
			Log.d("onDragonDrag",
					"DragEvent.ACTION_DRAG_ENDEDDragEvent.ACTION_DRAG_ENDED");
			break;

		default:
			break;
		}

		return false;
	}

	class FillListViewTask extends
			AsyncTask<Void, Void, List<PlayListItemModel>> {
		@Override
		protected void onPostExecute(List<PlayListItemModel> SourceDataList) {
			// TODO Auto-generated method stub
			if (adapter == null) {
				adapter = new PlayListAdapter(context, SourceDataList);
				playListView.setAdapter(adapter);
			} else {
				adapter.updateListView(SourceDataList);
				stopLoad();
			}
		}

		@Override
		protected List<PlayListItemModel> doInBackground(Void... params) {
			// 实例化汉字转拼音类
			PinyinComparator pinyinComparator = new PinyinComparator();
			List<PlayListItemModel> SourceDataList = filledData(getListFromXML());
			// 根据a-z进行排序源数据
			Collections.sort(SourceDataList, pinyinComparator);
			return SourceDataList;
		}
	}

}
