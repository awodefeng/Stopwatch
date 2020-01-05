LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) 

SRC_ROOT := java/com/xxun/watch/xunstopwatch

LOCAL_PACKAGE_NAME := XunStopwatch

LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES := framework 


LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 
   

LOCAL_SDK_VERSION := current

LOCAL_PROGUARD_ENABLED := full

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
