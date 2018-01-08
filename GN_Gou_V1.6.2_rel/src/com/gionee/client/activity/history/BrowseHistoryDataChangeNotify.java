package com.gionee.client.activity.history;

import java.util.ArrayList;
import java.util.List;

public interface BrowseHistoryDataChangeNotify {

    public void onBrowseHistoryDataChange(List<String> group, List<ArrayList<BrowseHistoryInfo>> list);

    public void onRemoveBrowseHistoryData(BrowseHistoryInfo deleteData);
}
