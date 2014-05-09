#! /bin/sh

do_apply_patches() {
n=0
for p in $1/*.patch; do
    r=$((n-n/10*10))
    n=$((n+1))
    if [ $r -eq 0 ]; then sleep 3; fi
    echo "VLC <= ${p}"
    git am $p || break
done
}

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
TESTED_HASH=4ec53cf7
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
do_apply_patches ../patches

if [ ${BUILD_PLATFORM} = 'darwin' ]; then
    echo "======================================="
    echo "Applying the patches for build on darwin"
#    git am ../../patches/vlc-on-macosx/*.patch
fi

echo "======================================="
echo "Applying the patches for libvlc_danmaku"
do_apply_patches ../../patches/vlc
echo "======================================="

cd -
