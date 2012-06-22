#! /bin/sh

BUILD_PLATFORM='unknown'
if [ "$(uname)" == 'Linux' ]; then
    BUILD_PLATFORM='linux'
elif [ "$(uname)" == 'Darwin' ]; then
    BUILD_PLATFORM='darwin'
fi

if [ ! -d "vlc-android" ]; then
    echp "_do_reset_vlc.sh is not supposed been executed directly"
    echo "MUST under vlc-ports/android"
    exit 1
fi

# 1/ libvlc, libvlccore and its plugins
TESTED_HASH=3e140bd0
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

echo "======================================="
echo "Applying the patches"
git am ../patches/*.patch || git am --abort

if [ ${BUILD_PLATFORM} == 'darwin' ]; then
    echo "======================================="
    echo "Applying the patches for build on darwin"
    git am ../../patches/vlc-on-macosx/*.patch || git am --abort
fi

echo "======================================="
echo "Applying the patches for libvlc_danmaku"
git am ../../patches/vlc/000*.patch || git am --abort
git am ../../patches/vlc/001*.patch || git am --abort
git am ../../patches/vlc/002*.patch || git am --abort

echo "======================================="

cd -
