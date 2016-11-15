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
name|DataOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|EnumSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|CreateFlag
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
name|FsTracer
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|DataStreamer
operator|.
name|LastExceptionInStreamer
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
name|impl
operator|.
name|DfsClientConf
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
name|BlockListAsLongs
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
name|HdfsFileStatus
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
name|datatransfer
operator|.
name|BlockConstructionStage
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|DatanodeManager
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
name|protocol
operator|.
name|DatanodeStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|SpanId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestDFSOutputStream
specifier|public
class|class
name|TestDFSOutputStream
block|{
DECL|field|cluster
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
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
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * The close() method of DFSOutputStream should never throw the same exception    * twice. See HDFS-5335 for details.    */
annotation|@
name|Test
DECL|method|testCloseTwice ()
specifier|public
name|void
name|testCloseTwice
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|)
decl_stmt|;
name|DataStreamer
name|streamer
init|=
operator|(
name|DataStreamer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|dos
argument_list|,
literal|"streamer"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|LastExceptionInStreamer
name|ex
init|=
operator|(
name|LastExceptionInStreamer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|streamer
argument_list|,
literal|"lastException"
argument_list|)
decl_stmt|;
name|Throwable
name|thrown
init|=
operator|(
name|Throwable
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|ex
argument_list|,
literal|"thrown"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|thrown
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOException
name|dummy
init|=
operator|new
name|IOException
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|set
argument_list|(
name|dummy
argument_list|)
expr_stmt|;
try|try
block|{
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
argument_list|,
name|dummy
argument_list|)
expr_stmt|;
block|}
name|thrown
operator|=
operator|(
name|Throwable
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|ex
argument_list|,
literal|"thrown"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|thrown
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * The computePacketChunkSize() method of DFSOutputStream should set the actual    * packet size< 64kB. See HDFS-7308 for details.    */
annotation|@
name|Test
DECL|method|testComputePacketChunkSize ()
specifier|public
name|void
name|testComputePacketChunkSize
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dos
init|=
operator|(
name|DFSOutputStream
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|os
argument_list|,
literal|"wrappedStream"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|packetSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|bytesPerChecksum
init|=
literal|512
decl_stmt|;
name|Method
name|method
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"computePacketChunkSize"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|dos
argument_list|,
name|packetSize
argument_list|,
name|bytesPerChecksum
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
name|dos
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"packetSize"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|field
operator|.
name|get
argument_list|(
name|dos
argument_list|)
operator|+
literal|33
operator|<
name|packetSize
argument_list|)
expr_stmt|;
comment|// If PKT_MAX_HEADER_LEN is 257, actual packet size come to over 64KB
comment|// without a fix on HDFS-7308.
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|Integer
operator|)
name|field
operator|.
name|get
argument_list|(
name|dos
argument_list|)
operator|+
literal|257
operator|<
name|packetSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCongestionBackoff ()
specifier|public
name|void
name|testCongestionBackoff
parameter_list|()
throws|throws
name|IOException
block|{
name|DfsClientConf
name|dfsClientConf
init|=
name|mock
argument_list|(
name|DfsClientConf
operator|.
name|class
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
name|mock
argument_list|(
name|DFSClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dfsClientConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|getTracer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|FsTracer
operator|.
name|get
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|clientRunning
operator|=
literal|true
expr_stmt|;
name|DataStreamer
name|stream
init|=
operator|new
name|DataStreamer
argument_list|(
name|mock
argument_list|(
name|HdfsFileStatus
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|ExtendedBlock
operator|.
name|class
argument_list|)
argument_list|,
name|client
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DataOutputStream
name|blockStream
init|=
name|mock
argument_list|(
name|DataOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|blockStream
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|stream
argument_list|,
literal|"blockStream"
argument_list|,
name|blockStream
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|stream
argument_list|,
literal|"stage"
argument_list|,
name|BlockConstructionStage
operator|.
name|PIPELINE_CLOSE
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|LinkedList
argument_list|<
name|DFSPacket
argument_list|>
name|dataQueue
init|=
operator|(
name|LinkedList
argument_list|<
name|DFSPacket
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|stream
argument_list|,
literal|"dataQueue"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
name|congestedNodes
init|=
operator|(
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|stream
argument_list|,
literal|"congestedNodes"
argument_list|)
decl_stmt|;
name|congestedNodes
operator|.
name|add
argument_list|(
name|mock
argument_list|(
name|DatanodeInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|DFSPacket
name|packet
init|=
name|mock
argument_list|(
name|DFSPacket
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|packet
operator|.
name|getTraceParents
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|SpanId
index|[]
block|{}
argument_list|)
expr_stmt|;
name|dataQueue
operator|.
name|add
argument_list|(
name|packet
argument_list|)
expr_stmt|;
name|stream
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|congestedNodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoLocalWriteFlag ()
specifier|public
name|void
name|testNoLocalWriteFlag
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|NO_LOCAL_WRITE
argument_list|,
name|CreateFlag
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|DatanodeManager
name|dm
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test-no-local"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|,
name|flags
argument_list|,
literal|512
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|512
argument_list|,
literal|null
argument_list|)
init|)
block|{
comment|// Inject a DatanodeManager that returns one DataNode as local node for
comment|// the client.
name|DatanodeManager
name|spyDm
init|=
name|spy
argument_list|(
name|dm
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dn1
init|=
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|dn1
argument_list|)
operator|.
name|when
argument_list|(
name|spyDm
argument_list|)
operator|.
name|getDatanodeByHost
argument_list|(
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|bm
argument_list|,
literal|"datanodeManager"
argument_list|,
name|spyDm
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|512
operator|*
literal|16
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|bm
argument_list|,
literal|"datanodeManager"
argument_list|,
name|dm
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
comment|// Total number of DataNodes is 3.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getAllBlockReports
argument_list|(
name|bpid
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDataNodesWithData
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|dnBlocks
range|:
name|cluster
operator|.
name|getAllBlockReports
argument_list|(
name|bpid
argument_list|)
control|)
block|{
for|for
control|(
name|BlockListAsLongs
name|blocks
range|:
name|dnBlocks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|blocks
operator|.
name|getNumberOfBlocks
argument_list|()
operator|>
literal|0
condition|)
block|{
name|numDataNodesWithData
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// Verify that only one DN has no data.
name|assertEquals
argument_list|(
literal|1
argument_list|,
literal|3
operator|-
name|numDataNodesWithData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEndLeaseCall ()
specifier|public
name|void
name|testEndLeaseCall
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSClient
name|spyClient
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|dfsOutputStream
init|=
name|spyClient
operator|.
name|create
argument_list|(
literal|"/file2"
argument_list|,
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|,
literal|1024
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DFSOutputStream
name|spyDFSOutputStream
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|dfsOutputStream
argument_list|)
decl_stmt|;
name|spyDFSOutputStream
operator|.
name|closeThreads
argument_list|(
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|spyClient
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|endFileLease
argument_list|(
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
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
block|}
end_class

end_unit

