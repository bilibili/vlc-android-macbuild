#! /bin/sh

if [ -z "$BILI_VLC_ROOT" ]; then
    echo "_do_pull.sh is not supposed been executed directly"
    echo "execute _pull_xxx.sh instead"
    exit 1
fi

BILI_VLC_ANDROID_HASH=1c800f86e8
echo "pull "${BILI_VLC_ROOT}":"${BILI_VLC_ANDROID_HASH}

if [ ! -d ${BILI_VLC_ROOT} ]; then
    echo "vlc-ports/android source not found, cloning"
    git clone git://git.videolan.org/vlc-ports/android ${BILI_VLC_ROOT}
    cd ${BILI_VLC_ROOT}   
    git checkout -B bili ${BILI_VLC_ANDROID_HASH}
    cd -
else
    echo "vlc-ports/android source found, pulling from remote master"
    cd ${BILI_VLC_ROOT}
    git fetch origin
    git checkout -B bili ${BILI_VLC_ANDROID_HASH}    
    cd -
fi

echo "patch "${BILI_VLC_MODULE_MODE} ${BILI_VLC_TARGET_MODE}
cd ${BILI_VLC_ROOT}
git am --abort
git am ../patches/ports-android/*.patch
git am ../patches/ports-android/${BILI_VLC_MODULE_MODE}/*.patch
git am ../patches/ports-android/${BILI_VLC_TARGET_MODE}/*.patch

mkdir -p patches
rm patches/100*.patch
cp ../patches/vlc/${BILI_VLC_MODULE_MODE}/100*.patch patches/ 

cd -
