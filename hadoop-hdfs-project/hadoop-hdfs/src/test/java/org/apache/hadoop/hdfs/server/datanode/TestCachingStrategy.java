begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
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
name|datanode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileSystem
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|ExtendedBlock
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
name|EditLogFileOutputStream
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
name|io
operator|.
name|IOUtils
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|CacheManipulator
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|POSIX_FADV_DONTNEED
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCachingStrategy
specifier|public
class|class
name|TestCachingStrategy
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
name|TestCachingStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_TEST_FILE_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_TEST_FILE_LEN
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|WRITE_PACKET_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|WRITE_PACKET_SIZE
init|=
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
decl_stmt|;
DECL|field|tracker
specifier|private
specifier|final
specifier|static
name|TestRecordingCacheTracker
name|tracker
init|=
operator|new
name|TestRecordingCacheTracker
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupTest ()
specifier|public
specifier|static
name|void
name|setupTest
parameter_list|()
block|{
name|EditLogFileOutputStream
operator|.
name|setShouldSkipFsyncForTesting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Track calls to posix_fadvise.
name|NativeIO
operator|.
name|POSIX
operator|.
name|setCacheManipulator
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
comment|// Normally, we wait for a few megabytes of data to be read or written
comment|// before dropping the cache.  This is to avoid an excessive number of
comment|// JNI calls to the posix_fadvise function.  However, for the purpose
comment|// of this test, we want to use small files and see all fadvise calls
comment|// happen.
name|BlockSender
operator|.
name|CACHE_DROP_INTERVAL_BYTES
operator|=
literal|4096
expr_stmt|;
name|BlockReceiver
operator|.
name|CACHE_DROP_LAG_BYTES
operator|=
literal|4096
expr_stmt|;
block|}
DECL|class|Stats
specifier|private
specifier|static
class|class
name|Stats
block|{
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|dropped
specifier|private
specifier|final
name|boolean
name|dropped
index|[]
init|=
operator|new
name|boolean
index|[
name|MAX_TEST_FILE_LEN
index|]
decl_stmt|;
DECL|method|Stats (String fileName)
name|Stats
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
block|}
DECL|method|fadvise (int offset, int len, int flags)
specifier|synchronized
name|void
name|fadvise
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"got fadvise(offset="
operator|+
name|offset
operator|+
literal|", len="
operator|+
name|len
operator|+
literal|",flags="
operator|+
name|flags
operator|+
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|flags
operator|==
name|POSIX_FADV_DONTNEED
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|dropped
index|[
operator|(
name|offset
operator|+
name|i
operator|)
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertNotDroppedInRange (int start, int end)
specifier|synchronized
name|void
name|assertNotDroppedInRange
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dropped
index|[
name|i
index|]
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"in file "
operator|+
name|fileName
operator|+
literal|", we "
operator|+
literal|"dropped the cache at offset "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|assertDroppedInRange (int start, int end)
specifier|synchronized
name|void
name|assertDroppedInRange
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|dropped
index|[
name|i
index|]
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"in file "
operator|+
name|fileName
operator|+
literal|", we "
operator|+
literal|"did not drop the cache at offset "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|clear ()
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|dropped
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestRecordingCacheTracker
specifier|private
specifier|static
class|class
name|TestRecordingCacheTracker
extends|extends
name|CacheManipulator
block|{
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Stats
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|posixFadviseIfPossible (String name, FileDescriptor fd, long offset, long len, int flags)
specifier|public
name|void
name|posixFadviseIfPossible
parameter_list|(
name|String
name|name
parameter_list|,
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|NativeIOException
block|{
if|if
condition|(
operator|(
name|len
operator|<
literal|0
operator|)
operator|||
operator|(
name|len
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid length of "
operator|+
name|len
operator|+
literal|" passed to posixFadviseIfPossible"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|offset
operator|<
literal|0
operator|)
operator|||
operator|(
name|offset
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid offset of "
operator|+
name|offset
operator|+
literal|" passed to posixFadviseIfPossible"
argument_list|)
throw|;
block|}
name|Stats
name|stats
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
operator|new
name|Stats
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
name|stats
operator|.
name|fadvise
argument_list|(
operator|(
name|int
operator|)
name|offset
argument_list|,
operator|(
name|int
operator|)
name|len
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|super
operator|.
name|posixFadviseIfPossible
argument_list|(
name|name
argument_list|,
name|fd
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
DECL|method|clear ()
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getStats (String fileName)
specifier|synchronized
name|Stats
name|getStats
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|synchronized
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"TestRecordingCacheManipulator{"
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|bld
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|bld
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|bld
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|createHdfsFile (FileSystem fs, Path p, long length, Boolean dropBehind)
specifier|static
name|void
name|createHdfsFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|long
name|length
parameter_list|,
name|Boolean
name|dropBehind
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// create file with replication factor of 1
name|fos
operator|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|dropBehind
operator|!=
literal|null
condition|)
block|{
name|fos
operator|.
name|setDropBehind
argument_list|(
name|dropBehind
argument_list|)
expr_stmt|;
block|}
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|8196
index|]
decl_stmt|;
while|while
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|amt
init|=
operator|(
name|length
operator|>
name|buf
operator|.
name|length
operator|)
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|length
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|amt
argument_list|)
expr_stmt|;
name|length
operator|-=
name|amt
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ioexception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|readHdfsFile (FileSystem fs, Path p, long length, Boolean dropBehind)
specifier|static
name|long
name|readHdfsFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|long
name|length
parameter_list|,
name|Boolean
name|dropBehind
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|long
name|totalRead
init|=
literal|0
decl_stmt|;
try|try
block|{
name|fis
operator|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|dropBehind
operator|!=
literal|null
condition|)
block|{
name|fis
operator|.
name|setDropBehind
argument_list|(
name|dropBehind
argument_list|)
expr_stmt|;
block|}
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|8196
index|]
decl_stmt|;
while|while
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|amt
init|=
operator|(
name|length
operator|>
name|buf
operator|.
name|length
operator|)
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|length
decl_stmt|;
name|int
name|ret
init|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|amt
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|totalRead
return|;
block|}
name|totalRead
operator|+=
name|ret
expr_stmt|;
name|length
operator|-=
name|ret
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ioexception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fis
operator|!=
literal|null
condition|)
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unreachable"
argument_list|)
throw|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testFadviseAfterWriteThenRead ()
specifier|public
name|void
name|testFadviseAfterWriteThenRead
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|LOG
operator|.
name|info
argument_list|(
literal|"testFadviseAfterWriteThenRead"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|TEST_PATH
init|=
literal|"/test"
decl_stmt|;
name|int
name|TEST_PATH_LEN
init|=
name|MAX_TEST_FILE_LEN
decl_stmt|;
try|try
block|{
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
literal|1
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create new file
name|createHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|TEST_PATH_LEN
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// verify that we dropped everything from the cache during file creation.
name|ExtendedBlock
name|block
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_PATH
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|String
name|fadvisedFileName
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Stats
name|stats
init|=
name|tracker
operator|.
name|getStats
argument_list|(
name|fadvisedFileName
argument_list|)
decl_stmt|;
name|stats
operator|.
name|assertDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// read file
name|readHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// verify that we dropped everything from the cache.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|stats
operator|.
name|assertDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
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
comment|/***    * Test the scenario where the DataNode defaults to not dropping the cache,    * but our client defaults are set.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testClientDefaults ()
specifier|public
name|void
name|testClientDefaults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|LOG
operator|.
name|info
argument_list|(
literal|"testClientDefaults"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_CACHE_DROP_BEHIND_READS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_CACHE_DROP_BEHIND_WRITES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|TEST_PATH
init|=
literal|"/test"
decl_stmt|;
name|int
name|TEST_PATH_LEN
init|=
name|MAX_TEST_FILE_LEN
decl_stmt|;
try|try
block|{
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
literal|1
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create new file
name|createHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|TEST_PATH_LEN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// verify that we dropped everything from the cache during file creation.
name|ExtendedBlock
name|block
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_PATH
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|String
name|fadvisedFileName
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Stats
name|stats
init|=
name|tracker
operator|.
name|getStats
argument_list|(
name|fadvisedFileName
argument_list|)
decl_stmt|;
name|stats
operator|.
name|assertDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// read file
name|readHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// verify that we dropped everything from the cache.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|stats
operator|.
name|assertDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testFadviseSkippedForSmallReads ()
specifier|public
name|void
name|testFadviseSkippedForSmallReads
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|LOG
operator|.
name|info
argument_list|(
literal|"testFadviseSkippedForSmallReads"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|TEST_PATH
init|=
literal|"/test"
decl_stmt|;
name|int
name|TEST_PATH_LEN
init|=
name|MAX_TEST_FILE_LEN
decl_stmt|;
name|FSDataInputStream
name|fis
init|=
literal|null
decl_stmt|;
try|try
block|{
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
literal|1
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create new file
name|createHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|TEST_PATH_LEN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Since the DataNode was configured with drop-behind, and we didn't
comment|// specify any policy, we should have done drop-behind.
name|ExtendedBlock
name|block
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_PATH
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|String
name|fadvisedFileName
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Stats
name|stats
init|=
name|tracker
operator|.
name|getStats
argument_list|(
name|fadvisedFileName
argument_list|)
decl_stmt|;
name|stats
operator|.
name|assertDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stats
operator|.
name|assertNotDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
argument_list|)
expr_stmt|;
comment|// read file
name|fis
operator|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|17
index|]
decl_stmt|;
name|fis
operator|.
name|readFully
argument_list|(
literal|4096
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// we should not have dropped anything because of the small read.
name|stats
operator|=
name|tracker
operator|.
name|getStats
argument_list|(
name|fadvisedFileName
argument_list|)
expr_stmt|;
name|stats
operator|.
name|assertNotDroppedInRange
argument_list|(
literal|0
argument_list|,
name|TEST_PATH_LEN
operator|-
name|WRITE_PACKET_SIZE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fis
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testNoFadviseAfterWriteThenRead ()
specifier|public
name|void
name|testNoFadviseAfterWriteThenRead
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|LOG
operator|.
name|info
argument_list|(
literal|"testNoFadviseAfterWriteThenRead"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|TEST_PATH
init|=
literal|"/test"
decl_stmt|;
name|int
name|TEST_PATH_LEN
init|=
name|MAX_TEST_FILE_LEN
decl_stmt|;
try|try
block|{
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
literal|1
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// create new file
name|createHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|TEST_PATH_LEN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// verify that we did not drop everything from the cache during file creation.
name|ExtendedBlock
name|block
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|TEST_PATH
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|String
name|fadvisedFileName
init|=
name|cluster
operator|.
name|getBlockFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Stats
name|stats
init|=
name|tracker
operator|.
name|getStats
argument_list|(
name|fadvisedFileName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
comment|// read file
name|readHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|false
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testSeekAfterSetDropBehind ()
specifier|public
name|void
name|testSeekAfterSetDropBehind
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start a cluster
name|LOG
operator|.
name|info
argument_list|(
literal|"testSeekAfterSetDropBehind"
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|TEST_PATH
init|=
literal|"/test"
decl_stmt|;
name|int
name|TEST_PATH_LEN
init|=
name|MAX_TEST_FILE_LEN
decl_stmt|;
try|try
block|{
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
literal|1
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|createHdfsFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|,
name|TEST_PATH_LEN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// verify that we can seek after setDropBehind
try|try
init|(
name|FSDataInputStream
name|fis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
init|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fis
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// create BlockReader
name|fis
operator|.
name|setDropBehind
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// clear BlockReader
name|fis
operator|.
name|seek
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// seek
block|}
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

