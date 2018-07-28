LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Begin libcurl
LOCAL_MODULE    := curl_shared
LOCAL_MODULE_FILENAME := libcurl
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libcurl.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE    := jansson_shared
LOCAL_MODULE_FILENAME := libjansson
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libjansson.so

include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)

# Begin cpuminer

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/jansson/ \
	$(LOCAL_PATH)/curl/ \
	$(LOCAL_PATH)/x2/ \
	$(LOCAL_PATH)/x5/

LOCAL_SRC_FILES := \
 cpu-miner.c util.c  native.c \
 x2/sha2.c x2/sha2-arm.S x2/sha2-x86.S x2/sha2-x64.S \
 x2/scrypt.c x2/scrypt-arm.S x2/scrypt-x86.S x2/scrypt-x64.S \
 x5/aes_helper.c x5/luffa.c x5/shavite.c \
 x5/bmw.c x5/simd.c x5/echo.c \
 x5/skein.c x5/keccak.c x5/jh.c jad8.c
 
ifeq ($(TARGET_ARCH_ABI),$(filter $(TARGET_ARCH_ABI),armeabi armeabi-v7a))
	LOCAL_ARM_MODE := arm
	LOCAL_ARM_NEON := false
endif

LOCAL_SHARED_LIBRARIES := jansson_shared
LOCAL_SHARED_LIBRARIES += curl_shared
LOCAL_CFLAGS := -O3 -D__ANDROID_API__=24 #-std=gnu99
LOCAL_LDLIBS := -lm -llog -ldl 
LOCAL_DISABLE_FATAL_LINKER_WARNINGS := true

LOCAL_MODULE    := cpuminer

include $(BUILD_SHARED_LIBRARY)
include $(CLEAR_VARS)

# Begin cpuminer-neon (armeabi-v7a only)

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/jansson/ \
	$(LOCAL_PATH)/curl/ \
	$(LOCAL_PATH)/x2/ \
	$(LOCAL_PATH)/x5/

LOCAL_SRC_FILES := \
 cpu-miner.c util.c  native.c \
 x2/sha2.c x2/sha2-arm.S x2/sha2-x86.S x2/sha2-x64.S \
 x2/scrypt.c x2/scrypt-arm.S x2/scrypt-x86.S x2/scrypt-x64.S \
 x5/aes_helper.c x5/luffa.c x5/shavite.c \
 x5/bmw.c x5/simd.c x5/echo.c \
 x5/skein.c x5/keccak.c x5/jh.c jad8.c
 
LOCAL_ARM_MODE := arm
LOCAL_ARM_NEON := true
LOCAL_CFLAGS := -O3 -D__NEON__ -D__ANDROID_API__=24

LOCAL_SHARED_LIBRARIES := jansson_shared
LOCAL_SHARED_LIBRARIES += curl_shared
LOCAL_LDLIBS := -lm -llog -ldl
LOCAL_DISABLE_FATAL_LINKER_WARNINGS := true

LOCAL_MODULE    := cpuminer-neon

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	 include $(BUILD_SHARED_LIBRARY)
endif
include $(CLEAR_VARS)

# Begin neondetect
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include/
LOCAL_SRC_FILES := neon_detect.c

LOCAL_STATIC_LIBRARIES += cpufeatures
LOCAL_CFLAGS := -O3 -D__ANDROID_API__=24
LOCAL_LDLIBS := -llog -ldl

LOCAL_MODULE    := neondetect


include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/cpufeatures)
