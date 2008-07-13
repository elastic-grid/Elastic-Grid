#!/bin/sh

# Copyright (C) 2007-2008 Elastic Grid, LLC.
#
# Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.gnu.org/licenses/lgpl-3.0.html
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Create a Elastic Grid AMI. Runs on the EC2 instance.

# Import variables
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
. "$bin"/eg-ec2-env.sh

# Remove previous run
rm -rf /usr/local/eg* /mnt/eg*

# Remove environment script since it contains sensitive information
rm -f "$bin"/eg-ec2-env.sh

# Install additional RPM sources
wget -nv http://ftp.freshrpms.net/pub/freshrpms/fedora/linux/8/freshrpms-release/freshrpms-release-1.1-1.fc.noarch.rpm
sudo rpm -vhi freshrpms-release-1.1-1.fc.noarch.rpm
rm -f freshrpms-release-1.1-1.fc.noarch.rpm

# Install Java
echo "Downloading and installing java binary."
cd /usr/local
wget -nv -O java.bin $JAVA_BINARY_URL
sh java.bin
rm -f java.bin

# Install tools
echo "Installing rpms."
yum -y install rsync lynx screen httpd rlwrap
yum -y clean all

# Install Elastic Grid
echo "Installing Elastic Grid $EG_VERSION."
mkdir -p /usr/local/eg-$EG_VERSION
cd /usr/local/eg-$EG_VERSION
wget -nv http://elastic-grid.s3.amazonaws.com/distributions/eg-$EG_VERSION-bin.tar.gz
tar xzf eg-$EG_VERSION-bin.tar.gz
rm -f eg-$EG_VERSION-bin.tar.gz

# Configure Elastic Grid
sed -i -e "s|# export JAVA_HOME=.*|export JAVA_HOME=/usr/local/jdk${JAVA_VERSION}|" \
       -e 's|# export EG_LOG_DIR=.*|export EG_LOG_DIR=/mnt/eg/logs|' \
       -e 's|# export EG_SLAVE_SLEEP=.*|export EG_SLAVE_SLEEP=1|' \
       -e 's|# export EG_OPTS=.*|export EG_OPTS=-server|' \
      /usr/local/eg-$EG_VERSION/bin/eg

# Do configuration on instance startup
echo "echo 'Starting Elastic Grid...'" >> /etc/rc.d/rc.local
echo "/root/eg-init" >> /etc/rc.d/rc.local
echo "alias eg='rlwrap /usr/local/eg-${EG_VERSION}/bin/eg'" >> /root/.bashrc

# Setup root user bash environment
echo "export JAVA_HOME=/usr/local/jdk${JAVA_VERSION}" >> /root/.bash_profile
echo "export EG_HOME=/usr/local/eg-${EG_VERSION}" >> /root/.bash_profile
echo "export RIO_HOME=/usr/local/eg-${EG_VERSION}" >> /root/.bash_profile
echo 'export PATH=$JAVA_HOME/bin:$EG_HOME/bin:$PATH' >> /root/.bash_profile

# Configure networking.
# Delete SSH authorized_keys since it includes the key it was launched with. (Note that it is re-populated when an instance starts.)
rm -f /root/.ssh/authorized_keys
# Ensure logging in to new hosts is seamless.
echo '    StrictHostKeyChecking no' >> /etc/ssh/ssh_config

# Bundle and upload image
cd ~root
# Don't need to delete .bash_history since it isn't written until exit.
df -h
ec2-bundle-vol -d /mnt -k /mnt/pk*.pem -c /mnt/cert*.pem -u $AWS_ACCOUNT_ID -s 10240 -p eg-$EG_VERSION-$ARCH -r $ARCH

ec2-upload-bundle --retry -b $S3_BUCKET -m /mnt/eg-$EG_VERSION-$ARCH.manifest.xml -a $AWS_ACCESS_KEY_ID -s $AWS_SECRET_ACCESS_KEY

# End
echo Done
