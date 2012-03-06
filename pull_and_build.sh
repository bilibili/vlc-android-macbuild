# fetch vlc-ports-android

if [ ! -d "vlc-ports-android" ]; then
    echo "vlc-ports/android source not found, cloning"
    git clone git://git.videolan.org/vlc-ports/android vlc-ports-android
else
    echo "vlc-ports/android source found, pulling from remote master"
    cd vlc-ports-android
    git pull origin master
    cd -
fi



# patch for mac build
cd vlc-ports-android

echo "Applying the patches for MacOS"
git am ../patches/ports-android/*.patch || git am --abort



# build
sh compile.sh
