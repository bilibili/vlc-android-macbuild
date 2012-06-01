#! /bin/sh

export BILI_VLC_TARGET="vlc-app-armv7a-neon"
export BILI_VLC_MODULE_MODE="full"

mkdir -p vlc-app
cd vlc-app

rm tarballs
ln -s ../tarballs

mkdir -p patches/ports-android/${BILI_VLC_TARGET}
rm patches/ports-android/*.patch
cp ../patches/ports-android/0001-*.patch patches/ports-android/
cp ../patches/ports-android/0003-*.patch patches/ports-android/

mkdir -p patches/vlc-on-macosx
rm patches/vlc-on-macosx/*.patch
cp ../patches/vlc-on-macosx/0002-*.patch patches/vlc-on-macosx/

mkdir -p patches/vlc
rm patches/vlc/*.patch
cp ../patches/vlc/0002-*.patch patches/vlc/

sh ../_do_pull.sh

cd -



