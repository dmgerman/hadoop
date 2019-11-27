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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DEAD_NODE_DETECTION_DEAD_NODE_QUEUE_MAX_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_DEAD_NODE_INTERVAL_MS_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_SUSPECT_NODE_INTERVAL_MS_KEY
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_DEAD_NODE_DETECTION_SUSPECT_NODE_QUEUE_MAX_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for dead node detection in DFSClient.  */
end_comment

begin_class
DECL|class|TestDeadNodeDetection
specifier|public
class|class
name|TestDeadNodeDetection
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|cluster
operator|=
literal|null
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
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
annotation|@
name|Test
DECL|method|testDeadNodeDetectionInBackground ()
specifier|public
name|void
name|testDeadNodeDetectionInBackground
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_DEAD_NODE_INTERVAL_MS_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_SUSPECT_NODE_INTERVAL_MS_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes match it too.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
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
literal|3
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testDetectDeadNodeInBackground"
argument_list|)
decl_stmt|;
comment|// 256 bytes data chunk for writes
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|bytes
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
name|bytes
index|[
name|index
index|]
operator|=
literal|'0'
expr_stmt|;
block|}
comment|// File with a 512 bytes block size
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|512
argument_list|)
decl_stmt|;
comment|// Write a block to all 3 DNs (2x256bytes).
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Remove three DNs,
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din
init|=
operator|(
name|DFSInputStream
operator|)
name|in
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|din
operator|.
name|getDFSClient
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{       }
name|waitForDeadNode
argument_list|(
name|dfsClient
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// check the dead node again here, the dead node is expected be removed
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeadNodeDetectionInMultipleDFSInputStream ()
specifier|public
name|void
name|testDeadNodeDetectionInMultipleDFSInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes match it too.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testDeadNodeMultipleDFSInputStream"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
name|String
name|datanodeUuid
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|FSDataInputStream
name|in1
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din1
init|=
operator|(
name|DFSInputStream
operator|)
name|in1
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient1
init|=
name|din1
operator|.
name|getDFSClient
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in2
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din2
init|=
literal|null
decl_stmt|;
name|DFSClient
name|dfsClient2
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|in1
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{       }
name|din2
operator|=
operator|(
name|DFSInputStream
operator|)
name|in1
operator|.
name|getWrappedStream
argument_list|()
expr_stmt|;
name|dfsClient2
operator|=
name|din2
operator|.
name|getDFSClient
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient1
operator|.
name|getDeadNodes
argument_list|(
name|din1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient2
operator|.
name|getDeadNodes
argument_list|(
name|din2
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient1
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dfsClient2
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the dn uuid of dead node to see if its expected dead node
name|assertEquals
argument_list|(
name|datanodeUuid
argument_list|,
operator|(
operator|(
name|DatanodeInfo
operator|)
name|dfsClient1
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
operator|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|datanodeUuid
argument_list|,
operator|(
operator|(
name|DatanodeInfo
operator|)
name|dfsClient2
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
operator|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in1
operator|.
name|close
argument_list|()
expr_stmt|;
name|in2
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
comment|// check the dead node again here, the dead node is expected be removed
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient1
operator|.
name|getDeadNodes
argument_list|(
name|din1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient2
operator|.
name|getDeadNodes
argument_list|(
name|din2
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient1
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient2
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeadNodeDetectionDeadNodeRecovery ()
specifier|public
name|void
name|testDeadNodeDetectionDeadNodeRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_DEAD_NODE_INTERVAL_MS_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes match it too.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
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
literal|3
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testDeadNodeDetectionDeadNodeRecovery"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
comment|// Remove three DNs,
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|one
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din
init|=
operator|(
name|DFSInputStream
operator|)
name|in
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|din
operator|.
name|getDFSClient
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{       }
name|waitForDeadNode
argument_list|(
name|dfsClient
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|one
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForDeadNode
argument_list|(
name|dfsClient
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeadNodeDetectionMaxDeadNodesProbeQueue ()
specifier|public
name|void
name|testDeadNodeDetectionMaxDeadNodesProbeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_PROBE_DEAD_NODE_INTERVAL_MS_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_DEAD_NODE_QUEUE_MAX_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes match it too.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
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
literal|3
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testDeadNodeDetectionMaxDeadNodesProbeQueue"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
comment|// Remove three DNs,
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din
init|=
operator|(
name|DFSInputStream
operator|)
name|in
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|din
operator|.
name|getDFSClient
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{       }
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|getDeadNodesProbeQueue
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
operator|)
operator|<=
literal|4
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDeadNodeDetectionSuspectNode ()
specifier|public
name|void
name|testDeadNodeDetectionSuspectNode
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_CLIENT_DEAD_NODE_DETECTION_SUSPECT_NODE_QUEUE_MAX_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// We'll be using a 512 bytes block size just for tests
comment|// so making sure the checksum bytes match it too.
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|DeadNodeDetector
operator|.
name|disabledProbeThreadForTest
argument_list|()
expr_stmt|;
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
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testDeadNodeDetectionSuspectNode"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|one
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|DFSInputStream
name|din
init|=
operator|(
name|DFSInputStream
operator|)
name|in
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|din
operator|.
name|getDFSClient
argument_list|()
decl_stmt|;
name|DeadNodeDetector
name|deadNodeDetector
init|=
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{       }
name|waitForSuspectNode
argument_list|(
name|din
operator|.
name|getDFSClient
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|one
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|deadNodeDetector
operator|.
name|getSuspectNodesProbeQueue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|deadNodeDetector
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|deadNodeDetector
operator|.
name|startProbeScheduler
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|deadNodeDetector
operator|.
name|getSuspectNodesProbeQueue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|deadNodeDetector
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getDeadNodes
argument_list|(
name|din
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createFile (FileSystem fs, Path filePath)
specifier|private
name|void
name|createFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// 256 bytes data chunk for writes
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|bytes
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
name|bytes
index|[
name|index
index|]
operator|=
literal|'0'
expr_stmt|;
block|}
comment|// File with a 512 bytes block size
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|512
argument_list|)
expr_stmt|;
comment|// Write a block to all 3 DNs (2x256bytes).
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|deleteFile (FileSystem fs, Path filePath)
specifier|private
name|void
name|deleteFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForDeadNode (DFSClient dfsClient, int size)
specifier|private
name|void
name|waitForDeadNode
parameter_list|(
name|DFSClient
name|dfsClient
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|clearAndGetDetectedDeadNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|size
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore the exception
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|5000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForSuspectNode (DFSClient dfsClient)
specifier|private
name|void
name|waitForSuspectNode
parameter_list|(
name|DFSClient
name|dfsClient
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|dfsClient
operator|.
name|getClientContext
argument_list|()
operator|.
name|getDeadNodeDetector
argument_list|()
operator|.
name|getSuspectNodesProbeQueue
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore the exception
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|5000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

