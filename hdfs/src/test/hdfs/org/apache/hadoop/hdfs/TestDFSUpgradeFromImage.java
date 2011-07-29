begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|FSConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsConstants
operator|.
name|StartupOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSImageTestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * This tests data transfer protocol handling in the Datanode. It sends  * various forms of wrong data and verifies that Datanode handles it well.  *   * This test uses the following two file from src/test/.../dfs directory :  *   1) hadoop-version-dfs-dir.tgz : contains DFS directories.  *   2) hadoop-dfs-dir.txt : checksums that are compared in this test.  * Please read hadoop-dfs-dir.txt for more information.    */
end_comment

begin_class
DECL|class|TestDFSUpgradeFromImage
specifier|public
class|class
name|TestDFSUpgradeFromImage
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDFSUpgradeFromImage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|File
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|HADOOP14_IMAGE
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP14_IMAGE
init|=
literal|"hadoop-14-dfs-dir.tgz"
decl_stmt|;
DECL|field|HADOOP_DFS_DIR_TXT
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP_DFS_DIR_TXT
init|=
literal|"hadoop-dfs-dir.txt"
decl_stmt|;
DECL|field|HADOOP22_IMAGE
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP22_IMAGE
init|=
literal|"hadoop-22-dfs-dir.tgz"
decl_stmt|;
DECL|field|numDataNodes
specifier|public
name|int
name|numDataNodes
init|=
literal|4
decl_stmt|;
DECL|class|ReferenceFileInfo
specifier|private
specifier|static
class|class
name|ReferenceFileInfo
block|{
DECL|field|path
name|String
name|path
decl_stmt|;
DECL|field|checksum
name|long
name|checksum
decl_stmt|;
block|}
DECL|field|refList
name|LinkedList
argument_list|<
name|ReferenceFileInfo
argument_list|>
name|refList
init|=
operator|new
name|LinkedList
argument_list|<
name|ReferenceFileInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|refIter
name|Iterator
argument_list|<
name|ReferenceFileInfo
argument_list|>
name|refIter
decl_stmt|;
DECL|field|printChecksum
name|boolean
name|printChecksum
init|=
literal|false
decl_stmt|;
DECL|method|unpackStorage ()
specifier|public
name|void
name|unpackStorage
parameter_list|()
throws|throws
name|IOException
block|{
name|unpackStorage
argument_list|(
name|HADOOP14_IMAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|unpackStorage (String tarFileName)
specifier|private
name|void
name|unpackStorage
parameter_list|(
name|String
name|tarFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|tarFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
operator|+
literal|"/"
operator|+
name|tarFileName
decl_stmt|;
name|String
name|dataDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
name|File
name|dfsDir
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"dfs"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dfsDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dfsDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete dfs directory '"
operator|+
name|dfsDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Unpacking "
operator|+
name|tarFile
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|unTar
argument_list|(
operator|new
name|File
argument_list|(
name|tarFile
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
argument_list|)
expr_stmt|;
comment|//Now read the reference info
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
operator|+
literal|"/"
operator|+
name|HADOOP_DFS_DIR_TXT
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|<=
literal|0
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|arr
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+\t\\s+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|.
name|length
operator|<
literal|1
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|arr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"printChecksums"
argument_list|)
condition|)
block|{
name|printChecksum
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|arr
operator|.
name|length
operator|<
literal|2
condition|)
block|{
continue|continue;
block|}
name|ReferenceFileInfo
name|info
init|=
operator|new
name|ReferenceFileInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|path
operator|=
name|arr
index|[
literal|0
index|]
expr_stmt|;
name|info
operator|.
name|checksum
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|arr
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|refList
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyChecksum (String path, long checksum)
specifier|private
name|void
name|verifyChecksum
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|checksum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|refIter
operator|==
literal|null
condition|)
block|{
name|refIter
operator|=
name|refList
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|printChecksum
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"CRC info for reference file : "
operator|+
name|path
operator|+
literal|" \t "
operator|+
name|checksum
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|refIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Checking checksum for "
operator|+
name|path
operator|+
literal|"Not enough elements in the refList"
argument_list|)
throw|;
block|}
name|ReferenceFileInfo
name|info
init|=
name|refIter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// The paths are expected to be listed in the same order
comment|// as they are traversed here.
name|assertEquals
argument_list|(
name|info
operator|.
name|path
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Checking checksum for "
operator|+
name|path
argument_list|,
name|info
operator|.
name|checksum
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|overallChecksum
name|CRC32
name|overallChecksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
DECL|method|verifyDir (DistributedFileSystem dfs, Path dir)
specifier|private
name|void
name|verifyDir
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|,
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|fileArr
init|=
name|dfs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TreeMap
argument_list|<
name|Path
argument_list|,
name|Boolean
argument_list|>
name|fileMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Path
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|fileArr
control|)
block|{
name|fileMap
operator|.
name|put
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|file
operator|.
name|isDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Path
argument_list|>
name|it
init|=
name|fileMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Path
name|path
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|boolean
name|isDir
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|pathName
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|overallChecksum
operator|.
name|update
argument_list|(
name|pathName
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDir
condition|)
block|{
name|verifyDir
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this is not a directory. Checksum the file data.
name|CRC32
name|fileCRC
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|FSInputStream
name|in
init|=
name|dfs
operator|.
name|dfs
operator|.
name|open
argument_list|(
name|pathName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|nRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|nRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fileCRC
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|nRead
argument_list|)
expr_stmt|;
block|}
name|verifyChecksum
argument_list|(
name|pathName
argument_list|,
name|fileCRC
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|verifyFileSystem (DistributedFileSystem dfs)
specifier|private
name|void
name|verifyFileSystem
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDir
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|verifyChecksum
argument_list|(
literal|"overallCRC"
argument_list|,
name|overallChecksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|printChecksum
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Checksums are written to log as requested. "
operator|+
literal|"Throwing this exception to force an error "
operator|+
literal|"for this test."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Test that sets up a fake image from Hadoop 0.3.0 and tries to start a    * NN, verifying that the correct error message is thrown.    */
DECL|method|testFailOnPreUpgradeImage ()
specifier|public
name|void
name|testFailOnPreUpgradeImage
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|File
name|namenodeStorage
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"nnimage-0.3.0"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|namenodeStorage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set up a fake NN storage that looks like an ancient Hadoop dir circa 0.3.0
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|namenodeStorage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Make "
operator|+
name|namenodeStorage
argument_list|,
name|namenodeStorage
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|imageDir
init|=
operator|new
name|File
argument_list|(
name|namenodeStorage
argument_list|,
literal|"image"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Make "
operator|+
name|imageDir
argument_list|,
name|imageDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Hex dump of a formatted image from Hadoop 0.3.0
name|File
name|imageFile
init|=
operator|new
name|File
argument_list|(
name|imageDir
argument_list|,
literal|"fsimage"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|imageBytes
init|=
name|StringUtils
operator|.
name|hexStringToByte
argument_list|(
literal|"fffffffee17c0d2700000000"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|imageFile
argument_list|)
decl_stmt|;
try|try
block|{
name|fos
operator|.
name|write
argument_list|(
name|imageBytes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Now try to start an NN from it
try|try
block|{
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|manageDataDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|startupOption
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Was able to start NN from 0.3.0 image"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ioe
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Old layout version is 'too old'"
argument_list|)
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
comment|/**    * Test upgrade from an 0.14 image    */
DECL|method|testUpgradeFromRel14Image ()
specifier|public
name|void
name|testUpgradeFromRel14Image
parameter_list|()
throws|throws
name|IOException
block|{
name|unpackStorage
argument_list|()
expr_stmt|;
name|upgradeAndVerify
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test upgrade from 0.22 image    */
DECL|method|testUpgradeFromRel22Image ()
specifier|public
name|void
name|testUpgradeFromRel22Image
parameter_list|()
throws|throws
name|IOException
block|{
name|unpackStorage
argument_list|(
name|HADOOP22_IMAGE
argument_list|)
expr_stmt|;
name|upgradeAndVerify
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test upgrade from 0.22 image with corrupt md5, make sure it    * fails to upgrade    */
DECL|method|testUpgradeFromCorruptRel22Image ()
specifier|public
name|void
name|testUpgradeFromCorruptRel22Image
parameter_list|()
throws|throws
name|IOException
block|{
name|unpackStorage
argument_list|(
name|HADOOP22_IMAGE
argument_list|)
expr_stmt|;
comment|// Overwrite the md5 stored in the VERSION files
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|FSImageTestUtil
operator|.
name|corruptVersionFile
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"name1/current/VERSION"
argument_list|)
argument_list|,
literal|"imageMD5Digest"
argument_list|,
literal|"22222222222222222222222222222222"
argument_list|)
expr_stmt|;
name|FSImageTestUtil
operator|.
name|corruptVersionFile
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"name2/current/VERSION"
argument_list|)
argument_list|,
literal|"imageMD5Digest"
argument_list|,
literal|"22222222222222222222222222222222"
argument_list|)
expr_stmt|;
comment|// Upgrade should now fail
try|try
block|{
name|upgradeAndVerify
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Upgrade did not fail with bad MD5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|msg
init|=
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|contains
argument_list|(
literal|"is corrupt with MD5 checksum"
argument_list|)
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
DECL|method|upgradeAndVerify ()
specifier|private
name|void
name|upgradeAndVerify
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// to allow test to be run outside of Ant
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// block scanning off
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|startupOption
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
argument_list|)
operator|.
name|clusterId
argument_list|(
literal|"testClusterId"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|dfs
operator|.
name|dfs
decl_stmt|;
comment|//Safemode will be off only after upgrade is complete. Wait for it.
while|while
condition|(
name|dfsClient
operator|.
name|setSafeMode
argument_list|(
name|FSConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_GET
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for SafeMode to be OFF."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
block|}
name|verifyFileSystem
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

