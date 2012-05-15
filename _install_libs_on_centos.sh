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



# update config.sub and config.guess on centos
# mkdir -p tmp
# cd tmp
# git clone http://git.savannah.gnu.org/cgit/config.git gnu-config
#
#      - if you want to compile directly from our GIT repo you might run into
#       this issue with older automake stuff:
#       checking host system type...
#       Invalid configuration `arm-linux-androideabi':
#       system `androideabi' not recognized
#       configure: error: /bin/sh ./config.sub arm-linux-androideabi failed
#       this issue can be fixed with using more recent versions of config.sub
#       and config.guess which can be obtained here:
#       http://git.savannah.gnu.org/gitweb/?p=config.git;a=tree
#       you need to replace your system-own versions which usually can be
#       found in your automake folder:
#       find /usr -name config.sub
