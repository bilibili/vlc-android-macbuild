#! /bin/sh

export BILI_VLC_ROOT="vlc-app-armv7a"
export BILI_VLC_MODULE_MODE="module-full"
export BILI_VLC_TARGET_MODE="target-armv7a"

mkdir -p vlc-app
cd vlc-app

rm tarballs
ln -s ../tarballs

rm _do_reset_vlc.sh
ln -s ../_do_reset_vlc.sh _do_reset_vlc.sh

mkdir -p patches/ports-android/${BILI_VLC_TARGET_MODE}
rm patches/ports-android/*.patch
cp ../patches/ports-android-app/*.patch patches/ports-android/
cp ../patches/ports-android/${BILI_VLC_TARGET_MODE}/*.patch patches/ports-android/${BILI_VLC_TARGET_MODE}

mkdir -p patches/vlc
rm patches/vlc/*.patch
cp ../patches/vlc-app/*.patch patches/vlc/

sh ../_do_pull.sh

cd -



