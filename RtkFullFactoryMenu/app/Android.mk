LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

ifeq (1,$(filter 1,$(shell echo "$$(( $(PLATFORM_SDK_VERSION) >= 21 ))" )))
LOCAL_32_BIT_ONLY := true
endif

LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_AIDL_INCLUDES += $(LOCAL_PATH)/src/

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call all-Iaidl-files-under, src/com/realtek/tvfactory/api/listener)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

LOCAL_MODULE_TAGS := optional

LOCAL_PACKAGE_NAME := TvFactoryGTV

LOCAL_CERTIFICATE := platform

LOCAL_OVERRIDES_PACKAGES := platform
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := full

LOCAL_DEX_PREOPT := false

# Include all the resources regardless of system supported locales
LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true

date_string := $(shell date +%y%m%d_%H%M%S)

LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_MULTILIB := 32

LOCAL_STATIC_JAVA_LIBRARIES += \
    rtk-framework \
    exttv-framework

LOCAL_JAVA_LIBRARIES := \
    android-support-v4 \

include $(BUILD_PACKAGE)