package com.gionee.client.activity.attention;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.HttpConstants;

public final class AddAttentionFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private AddChannelListAdapter mAddChannelListAdapter;
    private ListView mChannelList;

    public static AddAttentionFragment newInstance(List<JSONObject> attentioList) {
        AddAttentionFragment fragment = new AddAttentionFragment();
        ArrayList<String> serializeAttenttionList = new ArrayList<String>();
        for (Iterator<JSONObject> iterator = attentioList.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject) iterator.next();
            serializeAttenttionList.add(obj.toString());
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_CONTENT, serializeAttenttionList);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.add_channel_fragment, null);
        mChannelList = (ListView) contentView.findViewById(R.id.channels_list);
        ArrayList<String> attentionList = (ArrayList<String>) getArguments().getStringArrayList(KEY_CONTENT);
        mAddChannelListAdapter = new AddChannelListAdapter(getActivity());
        mChannelList.setAdapter(mAddChannelListAdapter);
        mAddChannelListAdapter.updataData(attentionList);
        mChannelList.setOnItemClickListener(mChannelItemListener);
        return contentView;
    }

    private OnItemClickListener mChannelItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            try {
                String itemString = (String) mAddChannelListAdapter.getItem(arg2);
                JSONObject itemJson = new JSONObject(itemString);
                gotoWebViewPage(itemJson);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            if (mAddChannelListAdapter != null) {
//                mAddChannelListAdapter.notifyDataSetChanged();
//            }
//        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void gotoWebViewPage(final JSONObject iconJson) {
        if (iconJson == null)
            return;
        StatService.onEvent(getActivity(), BaiduStatConstants.PLATFORM_H5,
                iconJson.optString(HttpConstants.Response.AddChannel.NAME_S));
        ((BaseFragmentActivity) getActivity()).gotoWebPage(
                iconJson.optString(HttpConstants.Response.MyAttentionChannel.LINK_S), true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
