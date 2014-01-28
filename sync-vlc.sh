#! /bin/sh

TARGET_LIST="
vlc-lite-armv7a
vlc-lite-armv5
vlc-lite-armv6
vlc-lite-armv6-vfp
vlc-lite-x86
"

for TARGET in ${TARGET_LIST}
do

echo "sync $TARGET"
cd $TARGET
git checkout master
git pull
echo "sync $TARGET/vlc"
cd vlc
git checkout master
git pull
cd ../..

done

