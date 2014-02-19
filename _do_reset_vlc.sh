#! /bin/sh

BUILD_PLATFORM='unknown'
if [ "$(uname)" = 'Linux' ]; then
    BUILD_PLATFORM='linux'
elif [ "$(uname)" = 'Darwin' ]; then
    BUILD_PLATFORM='darwin'
fi

if [ ! -d "vlc-android" ]; then
    echp "_do_reset_vlc.sh is not supposed been executed directly"
    echo "MUST under vlc-ports/android"
    exit 1
fi

# 1/ libvlc, libvlccore and its plugins
TESTED_HASH=75cc1f0e
if [ ! -d "vlc" ]; then
    echo "VLC source not found, cloning"
    git clone git://git.videolan.org/vlc.git vlc
    cd vlc
else
    echo "VLC source found"
    cd vlc
    git reset --hard origin
    git fetch origin
fi

echo "======================================="
echo "Reset to brance android ${TESTED_HASH}"
git checkout -B android ${TESTED_HASH}
git am --abort

echo "======================================="
echo "Applying the patches"
sleep 3
git am ../patches/*.patch

if [ ${BUILD_PLATFORM} = 'darwin' ]; then
    echo "======================================="
    echo "Applying the patches for build on darwin"
#    git am ../../patches/vlc-on-macosx/*.patch
fi

echo "======================================="
echo "Applying the patches for libvlc_danmaku"
sleep 3
git am ../../patches/vlc/000*.patch
sleep 3
git am ../../patches/vlc/001*.patch
sleep 3
git am ../../patches/vlc/002*.patch
sleep 3
git am ../../patches/vlc/003*.patch

echo "======================================="

cd -
