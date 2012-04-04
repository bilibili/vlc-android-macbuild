#! /bin/sh

export BILI_VLC_TARGET="vlc-armv7a-neon"

echo "pull "${BILI_VLC_TARGET}

if [ ! -d ${BILI_VLC_TARGET} ]; then
    echo "vlc-ports/android source not found, cloning"
    git clone git://git.videolan.org/vlc-ports/android ${BILI_VLC_TARGET}
else
    echo "vlc-ports/android source found, pulling from remote master"
    cd ${BILI_VLC_TARGET}
    git pull origin master
    cd -
fi

echo "patch "${BILI_VLC_TARGET}
cd ${BILI_VLC_TARGET}
git am ../patches/ports-android/${BILI_VLC_TARGET}/*.patch || git am --abort
git am ../patches/ports-android/*.patch || git am --abort
cd -
