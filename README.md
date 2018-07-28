![Jadepay Miner Logo](https://github.com/jadepay/android-miner/raw/master/ic_launcher-web.png)

Jadepay Android Miner
================

An Android application wrapper for Jadepay's CPUMiner

Compiling libcurl & libjansson
================

To compile libcurl & libjansson, you first need a standalone Android NDK toolchain. This
can be built using the make-standalone-toolchain.sh script in build/tools.
Here I'm going to assume we're putting the standalone toolchain in /opt/.

	# Setup destination directory
	sudo mkdir -p /opt/android-ndk-r9d/arm/
	sudo mkdir -p /opt/android-ndk-r9d/mips/
	sudo mkdir -p /opt/android-ndk-r9d/x86/

	# Get Android NDK
	curl -O http://dl.google.com/android/ndk/android-ndk-r9d-darwin-x86_64.tar.bz2
	tar -xvf android-ndk-r9d-darwin-x86_64.tar.bz2
	cd android-ndk-r9d

	# Build standalone NDK
	cd build/tools
	sudo ./make-standalone-toolchain.sh --arch=arm --ndk-dir=../../ --install-dir=/opt/android-ndk-r9d/arm/ --verbose --system=darwin-x86_64
	sudo ./make-standalone-toolchain.sh --arch=mips --ndk-dir=../../ --install-dir=/opt/android-ndk-r9d/mips/ --verbose --system=darwin-x86_64
	sudo ./make-standalone-toolchain.sh --arch=x86 --ndk-dir=../../ --install-dir=/opt/android-ndk-r9d/x86/ --verbose --system=darwin-x86_64

Once you have the standalone NDK toolchain, you need to download and extract
the source for libcurl.

	# Get libcurl source
	curl -O http://curl.haxx.se/download/curl-7.35.0.tar.bz2
	tar -xvf curl-7.35.0.tar.bz2

The last step is to use the included build-libcurl.sh script to compile libcurl
and move the compiled library into the project jade-android-miner

	sudo ./build-libcurl.sh [path to libcurl]
	sudo ./build-libjansson.sh [path to libjansson]

Compile & install jni library
================

	cd jade-android-miner/app/src/main
	/home/user/android-ndk-r9d/ndk-build

Compile Apk
================

	Import project to android studio and build project
	or using Gradle build:

	`gradle clean build -x test`





