LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
			baidutongji \
			appupgrade \
 	 		v13 \
 	 		httpclient\
 	 		youmeng\
 	 		baidupush\
 	 		sinaweibo\
 	 		zxing\
 	 		shareSDK\
 	 		qq\
 	 		qzone\
			ShareSDK-Wechat\
			ShareSDK-Wechat-Core\
			ShareSDK-Wechat-Moments\
			ShareSDK-SinaWeibo\
			ads
			
 	 		
#LOCAL_JNI_SHARED_LIBRARIES := lib_gn_appincupgrade
#LOCAL_REQUIRED_MODULES := lib_gn_appincupgrade
#PRODUCT_COPY_FILES += \
#	$(LOCAL_PATH)lib/armeabi-v7a/lib_gn_appincupgrade.so

LOCAL_JNI_SHARED_LIBRARIES := lib_baidu_push
LOCAL_REQUIRED_MODULES := lib_baidu_push
PRODUCT_COPY_FILES += \
	$(LOCAL_PATH)lib/armeabi/libbdpush_V2_2.so:system/lib/libbdpush_V2_2.so
LOCAL_SRC_FILES := $(call all-java-files-under, src) 
LOCAL_PROGUARD_ENABLED := custom
LOCAL_PROGUARD_FLAG_FILES := proguard-project.txt
LOCAL_PACKAGE_NAME := GN_Gou
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := baidutongji:libs/Baidu_Mobstat_3.6.5.jar\
        								v13:libs/android-support-v13.jar\
        								appupgrade:libs/com.gionee.appupgrade.jar\
        								httpclient:libs/httpmime-4.2.2.jar\
                                        youmeng:libs/umeng-fb-v4.3.2.jar\
                                        baidupush:libs/pushservice-4.4.1.88.jar\
                                        zxing:libs/zxing.jar\
                                        sinaweibo:libs/snia_weibosdkcore.jar\
                                        shareSDK:libs/ShareSDK-Core-2.5.7.jar\
                                        qq:libs/ShareSDK-QQ-2.5.7.jar\
                                        qzone:libs/ShareSDK-QZone-2.5.7.jar\
					ShareSDK-Wechat:libs/ShareSDK-Wechat-2.5.7.jar\
					ShareSDK-Wechat-Core:libs/ShareSDK-Wechat-Core-2.5.7.jar\
					ShareSDK-Wechat-Moments:libs/ShareSDK-Wechat-Moments-2.5.7.jar\
					ShareSDK-SinaWeibo:libs/ShareSDK-SinaWeibo-2.5.7.jar
					ads:libs/com.wayde.ads.jar
