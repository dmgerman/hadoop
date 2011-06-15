#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# This file is used to generate the package-info.java class that
# records the version, revision, branch, user, timestamp, and url
unset LANG
unset LC_CTYPE
unset LC_TIME
version=$1
build_dir=$2
user=`whoami | tr '\n\r' '\n'`
date=`date`
cwd=`pwd`
if git rev-parse HEAD 2>/dev/null > /dev/null ; then
  revision=`git log -1 --pretty=format:"%H"`
  hostname=`hostname`
  branch=`git branch | sed -n -e 's/^* //p'`
  url="git://${hostname}${cwd}"
elif [ -d .svn ]; then
  revision=`svn info | sed -n -e 's/Last Changed Rev: \(.*\)/\1/p'`
  url=`svn info | sed -n -e 's/URL: \(.*\)/\1/p'`
  # Get canonical branch (branches/X, tags/X, or trunk)
  branch=`echo $url | sed -n -e 's,.*\(branches/.*\)$,\1,p' \
                             -e 's,.*\(tags/.*\)$,\1,p' \
                             -e 's,.*trunk$,trunk,p'`
else
  revision="Unknown"
  branch="Unknown"
  url="file://$cwd"
fi
srcChecksum=`find src -name '*.java' | LC_ALL=C sort | xargs md5sum | md5sum | cut -d ' ' -f 1`

mkdir -p $build_dir/src/org/apache/hadoop
cat << EOF | \
  sed -e "s/VERSION/$version/" -e "s/USER/$user/" -e "s/DATE/$date/" \
      -e "s|URL|$url|" -e "s/REV/$revision/" \
      -e "s|BRANCH|$branch|" -e "s/SRCCHECKSUM/$srcChecksum/" \
      > $build_dir/src/org/apache/hadoop/package-info.java
/*
 * Generated by src/saveVersion.sh
 */
@HadoopVersionAnnotation(version="VERSION", revision="REV", branch="BRANCH",
                         user="USER", date="DATE", url="URL",
                         srcChecksum="SRCCHECKSUM")
package org.apache.hadoop;
EOF
