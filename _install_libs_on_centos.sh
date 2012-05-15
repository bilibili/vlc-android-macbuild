# if your git does not support -B
# yum -install zlib-devel openssl-devel cpio expat-devel gettext-devel
mkdir -p tmp
cd tmp
wget http://git-core.googlecode.com/files/git-1.7.10.2.tar.gz
tar -xzvf ./git-1.7.10.2.tar.gz
cd ./git-1.7.10.2
./configure
make
make install
cd -
