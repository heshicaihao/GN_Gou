package com.gionee.client.model;

public class GNDowanload {

    public static final class DownloadStatus {
        /**
         * 下载
         */
        public static final int STATUS_INSTALL = 0;
        /**
         * 下载中
         */
        public static final int STATUS_DOWNLOADING = 1;

        /**
         * 打开
         */
        public static final int STATUS_OPEN = 2;
        /**
         * 升级
         */
        public static final int STATUS_UPDATE = 3;
        /**
         * 安装
         */
        public static final int STATUS_COMPLETE = 4;
        /**
         * 等待中
         */
        public static final int STATUS_WAITTING = 5;
    }

}
