#! /bin/sh

if [ -z "$BILI_VLC_TARGET" ]; then
    echo "_do_pull.sh is not supposed been executed directly"
    echo "execute _pull_xxx.sh instead"
    exit 1
fi

BILI_VLC_ANDROID_HASH=b0d215e9fc
echo "pull "${BILI_VLC_TARGET}":"${BILI_VLC_ANDROID_HASH}

if [ ! -d ${BILI_VLC_TARGET} ]; then
    echo "vlc-ports/android source not found, cloning"
    git clone git://git.videolan.org/vlc-ports/android ${BILI_VLC_TARGET}
    cd ${BILI_VLC_TARGET}   
    git checkout -B bili ${BILI_VLC_ANDROID_HASH}
    cd -
else
    echo "vlc-ports/android source found, pulling from remote master"
    cd ${BILI_VLC_TARGET}
    git fetch origin
    git checkout -B bili ${BILI_VLC_ANDROID_HASH}    
    cd -
fi

echo "patch "${BILI_VLC_TARGET}
cd ${BILI_VLC_TARGET}
git am ../patches/ports-android/${BILI_VLC_TARGET}/*.patch || git am --abort
git am ../patches/ports-android/*.patch || git am --abort

if [[ ${BILI_VLC_MODULE_MODE} == 'lite' ]]; then
    git am ../patches/ports-android/modules-lite/*.patch
elif [[ ${BILI_VLC_MODULE_MODE} == 'live' ]]; then
    git am ../patches/ports-android/modules-live/*.patch
elif [[ ${BILI_VLC_MODULE_MODE} == 'full' ]]; then
    git am ../patches/ports-android/modules-full/*.patch
fi

cd -
