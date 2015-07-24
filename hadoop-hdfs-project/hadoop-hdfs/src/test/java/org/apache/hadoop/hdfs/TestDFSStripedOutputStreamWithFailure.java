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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|DatanodeInfo
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
name|protocol
operator|.
name|HdfsConstants
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
name|LocatedBlock
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
name|LocatedBlocks
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
name|LocatedStripedBlock
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenSecretManager
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|SecurityTestUtil
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
name|blockmanagement
operator|.
name|BlockManager
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
name|NameNode
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
name|util
operator|.
name|StripedBlockUtil
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|log4j
operator|.
name|Level
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
DECL|class|TestDFSStripedOutputStreamWithFailure
specifier|public
class|class
name|TestDFSStripedOutputStreamWithFailure
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDFSStripedOutputStreamWithFailure
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSOutputStream
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataStreamer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|NUM_DATA_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATA_BLOCKS
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|NUM_PARITY_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_PARITY_BLOCKS
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|CELL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CELL_SIZE
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|STRIPES_PER_BLOCK
specifier|private
specifier|static
specifier|final
name|int
name|STRIPES_PER_BLOCK
init|=
literal|4
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
name|CELL_SIZE
operator|*
name|STRIPES_PER_BLOCK
decl_stmt|;
DECL|field|BLOCK_GROUP_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_GROUP_SIZE
init|=
name|BLOCK_SIZE
operator|*
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|FLUSH_POS
specifier|private
specifier|static
specifier|final
name|int
name|FLUSH_POS
init|=
literal|9
operator|*
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
operator|+
literal|1
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|TestDFSStripedOutputStreamWithFailure
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|setup (Configuration conf)
specifier|private
name|void
name|setup
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numDNs
init|=
name|NUM_DATA_BLOCKS
operator|+
name|NUM_PARITY_BLOCKS
decl_stmt|;
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
name|numDNs
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createErasureCodingZone
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|private
name|void
name|tearDown
parameter_list|()
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
DECL|method|getByte (long pos)
specifier|private
specifier|static
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|pos
return|;
block|}
DECL|method|initConf (Configuration conf)
specifier|private
name|void
name|initConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|initConfWithBlockToken (Configuration conf)
specifier|private
name|void
name|initConfWithBlockToken
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"ipc.client.connect.max.retries"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|240000
argument_list|)
DECL|method|testDatanodeFailure ()
specifier|public
name|void
name|testDatanodeFailure
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|length
init|=
name|NUM_DATA_BLOCKS
operator|*
operator|(
name|BLOCK_SIZE
operator|-
name|CELL_SIZE
operator|)
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|dn
init|=
literal|0
init|;
name|dn
operator|<
literal|9
condition|;
name|dn
operator|++
control|)
block|{
try|try
block|{
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
operator|+
name|dn
argument_list|)
argument_list|,
name|length
argument_list|,
name|length
operator|/
literal|2
argument_list|,
name|dn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed, dn="
operator|+
name|dn
operator|+
literal|", length="
operator|+
name|length
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|tearDown
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
literal|240000
argument_list|)
DECL|method|testBlockTokenExpired ()
specifier|public
name|void
name|testBlockTokenExpired
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|length
init|=
name|NUM_DATA_BLOCKS
operator|*
operator|(
name|BLOCK_SIZE
operator|-
name|CELL_SIZE
operator|)
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|initConfWithBlockToken
argument_list|(
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|dn
init|=
literal|0
init|;
name|dn
operator|<
literal|9
condition|;
name|dn
operator|+=
literal|2
control|)
block|{
try|try
block|{
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
operator|+
name|dn
argument_list|)
argument_list|,
name|length
argument_list|,
name|length
operator|/
literal|2
argument_list|,
name|dn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed, dn="
operator|+
name|dn
operator|+
literal|", length="
operator|+
name|length
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|runTest (final Path p, final int length, final int killPos, final int dnIndex, final boolean tokenExpire)
specifier|private
name|void
name|runTest
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|int
name|killPos
parameter_list|,
specifier|final
name|int
name|dnIndex
parameter_list|,
specifier|final
name|boolean
name|tokenExpire
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"p="
operator|+
name|p
operator|+
literal|", length="
operator|+
name|length
operator|+
literal|", killPos="
operator|+
name|killPos
operator|+
literal|", dnIndex="
operator|+
name|dnIndex
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|killPos
operator|<
name|length
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|killPos
operator|>
name|FLUSH_POS
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fullPath
init|=
name|p
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|BlockTokenSecretManager
name|sm
init|=
name|bm
operator|.
name|getBlockTokenSecretManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenExpire
condition|)
block|{
comment|// set a short token lifetime (1 second)
name|SecurityTestUtil
operator|.
name|setBlockTokenLifetime
argument_list|(
name|sm
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicInteger
name|pos
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|DFSStripedOutputStream
name|stripedOut
init|=
operator|(
name|DFSStripedOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|long
name|oldGS
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|killed
init|=
literal|false
decl_stmt|;
for|for
control|(
init|;
name|pos
operator|.
name|get
argument_list|()
operator|<
name|length
condition|;
control|)
block|{
specifier|final
name|int
name|i
init|=
name|pos
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|killPos
condition|)
block|{
specifier|final
name|long
name|gs
init|=
name|getGenerationStamp
argument_list|(
name|stripedOut
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|oldGS
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|oldGS
argument_list|,
name|gs
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenExpire
condition|)
block|{
name|DFSTestUtil
operator|.
name|flushInternal
argument_list|(
name|stripedOut
argument_list|)
expr_stmt|;
name|waitTokenExpires
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|killDatanode
argument_list|(
name|cluster
argument_list|,
name|stripedOut
argument_list|,
name|dnIndex
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|killed
operator|=
literal|true
expr_stmt|;
block|}
name|write
argument_list|(
name|out
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|FLUSH_POS
condition|)
block|{
name|oldGS
operator|=
name|getGenerationStamp
argument_list|(
name|stripedOut
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|killed
argument_list|)
expr_stmt|;
comment|// check file length
specifier|final
name|FileStatus
name|status
init|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|length
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|checkData
argument_list|(
name|dfs
argument_list|,
name|fullPath
argument_list|,
name|length
argument_list|,
name|dnIndex
argument_list|,
name|oldGS
argument_list|)
expr_stmt|;
block|}
DECL|method|write (FSDataOutputStream out, int i)
specifier|static
name|void
name|write
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|getByte
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed at i="
operator|+
name|i
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|getGenerationStamp (DFSStripedOutputStream out)
specifier|static
name|long
name|getGenerationStamp
parameter_list|(
name|DFSStripedOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|gs
init|=
name|DFSTestUtil
operator|.
name|flushInternal
argument_list|(
name|out
argument_list|)
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"getGenerationStamp returns "
operator|+
name|gs
argument_list|)
expr_stmt|;
return|return
name|gs
return|;
block|}
DECL|method|getDatanodes (StripedDataStreamer streamer)
specifier|static
name|DatanodeInfo
name|getDatanodes
parameter_list|(
name|StripedDataStreamer
name|streamer
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|streamer
operator|.
name|getNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodes
operator|!=
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|datanodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|datanodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|datanodes
index|[
literal|0
index|]
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
DECL|method|killDatanode (MiniDFSCluster cluster, DFSStripedOutputStream out, final int dnIndex, final AtomicInteger pos)
specifier|static
name|void
name|killDatanode
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|DFSStripedOutputStream
name|out
parameter_list|,
specifier|final
name|int
name|dnIndex
parameter_list|,
specifier|final
name|AtomicInteger
name|pos
parameter_list|)
block|{
specifier|final
name|StripedDataStreamer
name|s
init|=
name|out
operator|.
name|getStripedDataStreamer
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
name|datanode
init|=
name|getDatanodes
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"killDatanode "
operator|+
name|dnIndex
operator|+
literal|": "
operator|+
name|datanode
operator|+
literal|", pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|datanode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkData (DistributedFileSystem dfs, String src, int length, int killedDnIndex, long oldGS)
specifier|static
name|void
name|checkData
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|,
name|String
name|src
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|killedDnIndex
parameter_list|,
name|long
name|oldGS
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|blockGroupList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|src
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
specifier|final
name|int
name|expectedNumGroup
init|=
operator|(
name|length
operator|-
literal|1
operator|)
operator|/
name|BLOCK_GROUP_SIZE
operator|+
literal|1
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedNumGroup
argument_list|,
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LocatedBlock
name|firstBlock
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|firstBlock
operator|instanceof
name|LocatedStripedBlock
argument_list|)
expr_stmt|;
specifier|final
name|long
name|gs
init|=
name|firstBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
specifier|final
name|String
name|s
init|=
literal|"gs="
operator|+
name|gs
operator|+
literal|", oldGS="
operator|+
name|oldGS
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
argument_list|,
name|gs
operator|>
name|oldGS
argument_list|)
expr_stmt|;
name|LocatedBlock
index|[]
name|blocks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
operator|(
name|LocatedStripedBlock
operator|)
name|firstBlock
argument_list|,
name|CELL_SIZE
argument_list|,
name|NUM_DATA_BLOCKS
argument_list|,
name|NUM_PARITY_BLOCKS
argument_list|)
decl_stmt|;
name|blockGroupList
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|blocks
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test each block group
for|for
control|(
name|int
name|group
init|=
literal|0
init|;
name|group
operator|<
name|blockGroupList
operator|.
name|size
argument_list|()
condition|;
name|group
operator|++
control|)
block|{
specifier|final
name|boolean
name|isLastGroup
init|=
name|group
operator|==
name|blockGroupList
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
specifier|final
name|int
name|groupSize
init|=
operator|!
name|isLastGroup
condition|?
name|BLOCK_GROUP_SIZE
else|:
name|length
operator|-
operator|(
name|blockGroupList
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|*
name|BLOCK_GROUP_SIZE
decl_stmt|;
specifier|final
name|int
name|numCellInGroup
init|=
operator|(
name|groupSize
operator|-
literal|1
operator|)
operator|/
name|CELL_SIZE
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
name|lastCellIndex
init|=
operator|(
name|numCellInGroup
operator|-
literal|1
operator|)
operator|%
name|NUM_DATA_BLOCKS
decl_stmt|;
specifier|final
name|int
name|lastCellSize
init|=
name|groupSize
operator|-
operator|(
name|numCellInGroup
operator|-
literal|1
operator|)
operator|*
name|CELL_SIZE
decl_stmt|;
comment|//get the data of this block
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blockList
init|=
name|blockGroupList
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|dataBlockBytes
init|=
operator|new
name|byte
index|[
name|NUM_DATA_BLOCKS
index|]
index|[]
decl_stmt|;
name|byte
index|[]
index|[]
name|parityBlockBytes
init|=
operator|new
name|byte
index|[
name|NUM_PARITY_BLOCKS
index|]
index|[]
decl_stmt|;
comment|// for each block, use BlockReader to read data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|j
init|=
name|i
operator|>=
name|NUM_DATA_BLOCKS
condition|?
literal|0
else|:
name|i
decl_stmt|;
specifier|final
name|int
name|numCellInBlock
init|=
operator|(
name|numCellInGroup
operator|-
literal|1
operator|)
operator|/
name|NUM_DATA_BLOCKS
operator|+
operator|(
name|j
operator|<=
name|lastCellIndex
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
name|numCellInBlock
operator|*
name|CELL_SIZE
operator|+
operator|(
name|isLastGroup
operator|&&
name|i
operator|==
name|lastCellIndex
condition|?
name|lastCellSize
operator|-
name|CELL_SIZE
else|:
literal|0
operator|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|blockBytes
init|=
operator|new
name|byte
index|[
name|blockSize
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|NUM_DATA_BLOCKS
condition|)
block|{
name|dataBlockBytes
index|[
name|i
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
else|else
block|{
name|parityBlockBytes
index|[
name|i
operator|-
name|NUM_DATA_BLOCKS
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
specifier|final
name|LocatedBlock
name|lb
init|=
name|blockList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"XXX i="
operator|+
name|i
operator|+
literal|", lb="
operator|+
name|lb
argument_list|)
expr_stmt|;
if|if
condition|(
name|lb
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ExtendedBlock
name|block
init|=
name|lb
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blockSize
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|block
operator|.
name|getNumBytes
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|i
operator|!=
name|killedDnIndex
condition|)
block|{
specifier|final
name|BlockReader
name|blockReader
init|=
name|BlockReaderTestUtil
operator|.
name|getBlockReader
argument_list|(
name|dfs
argument_list|,
name|lb
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
decl_stmt|;
name|blockReader
operator|.
name|readAll
argument_list|(
name|blockBytes
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|blockReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// check data
specifier|final
name|int
name|groupPosInFile
init|=
name|group
operator|*
name|BLOCK_GROUP_SIZE
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dataBlockBytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
index|[]
name|actual
init|=
name|dataBlockBytes
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|posInBlk
init|=
literal|0
init|;
name|posInBlk
operator|<
name|actual
operator|.
name|length
condition|;
name|posInBlk
operator|++
control|)
block|{
specifier|final
name|long
name|posInFile
init|=
name|StripedBlockUtil
operator|.
name|offsetInBlkToOffsetInBG
argument_list|(
name|CELL_SIZE
argument_list|,
name|NUM_DATA_BLOCKS
argument_list|,
name|posInBlk
argument_list|,
name|i
argument_list|)
operator|+
name|groupPosInFile
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|posInFile
operator|<
name|length
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|expected
init|=
name|getByte
argument_list|(
name|posInFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|killedDnIndex
condition|)
block|{
name|actual
index|[
name|posInBlk
index|]
operator|=
name|expected
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
literal|"expected="
operator|+
name|expected
operator|+
literal|" but actual="
operator|+
name|actual
index|[
name|posInBlk
index|]
operator|+
literal|", posInFile="
operator|+
name|posInFile
operator|+
literal|", posInBlk="
operator|+
name|posInBlk
operator|+
literal|". group="
operator|+
name|group
operator|+
literal|", i="
operator|+
name|i
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
argument_list|,
name|expected
argument_list|,
name|actual
index|[
name|posInBlk
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// check parity
name|TestDFSStripedOutputStream
operator|.
name|verifyParity
argument_list|(
name|dfs
operator|.
name|getConf
argument_list|()
argument_list|,
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|get
argument_list|(
name|group
argument_list|)
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|CELL_SIZE
argument_list|,
name|dataBlockBytes
argument_list|,
name|parityBlockBytes
argument_list|,
name|killedDnIndex
operator|-
name|dataBlockBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitTokenExpires (FSDataOutputStream out)
specifier|private
name|void
name|waitTokenExpires
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
init|=
name|DFSTestUtil
operator|.
name|getBlockToken
argument_list|(
name|out
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|SecurityTestUtil
operator|.
name|isBlockTokenExpired
argument_list|(
name|token
argument_list|)
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
block|}
block|}
block|}
end_class

end_unit

