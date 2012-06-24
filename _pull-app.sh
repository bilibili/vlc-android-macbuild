#! /bin/sh

export BILI_VLC_TARGET="vlc-app-armv7a-neon"
export BILI_VLC_MODULE_MODE="full"

mkdir -p vlc-app
cd vlc-app

rm tarballs
ln -s ../tarballs

mkdir -p patches/ports-android/${BILI_VLC_TARGET}
rm patches/ports-android/*.patch
cp ../patches/ports-android/*-modify-compile.sh-for-MacOS-build.patch patches/ports-android/
cp ../patches/ports-android/*-vlc-repo-reset-script.patch patches/ports-android/

mkdir -p patches/vlc-on-macosx
rm patches/vlc-on-macosx/*.patch
cp ../patches/vlc-on-macosx/*-fix-toolchain-config.patch patches/vlc-on-macosx/

mkdir -p patches/vlc
rm patches/vlc/*.patch
cp ../patches/vlc/*-zlib-fix-zlib-build-on-android.patch patches/vlc/

sh ../_do_pull.sh

cd -



