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
name|util
operator|.
name|Collection
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|DFSClientAdapter
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
operator|.
name|DatanodeReportType
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|BlockOpResponseProto
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|Status
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
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetTestUtil
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
name|DatanodeRegistration
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_WRITE_BANDWIDTHPERSEC_KEY
import|;
end_import

begin_comment
comment|/** Test transferring RBW between datanodes */
end_comment

begin_class
DECL|class|TestTransferRbw
specifier|public
class|class
name|TestTransferRbw
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestTransferRbw
operator|.
name|class
argument_list|)
decl_stmt|;
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|DataNode
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
DECL|field|RAN
specifier|private
specifier|static
specifier|final
name|Random
name|RAN
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
operator|(
name|short
operator|)
literal|1
decl_stmt|;
DECL|method|getRbw (final DataNode datanode, String bpid)
specifier|private
specifier|static
name|ReplicaBeingWritten
name|getRbw
parameter_list|(
specifier|final
name|DataNode
name|datanode
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
operator|(
name|ReplicaBeingWritten
operator|)
name|getReplica
argument_list|(
name|datanode
argument_list|,
name|bpid
argument_list|,
name|ReplicaState
operator|.
name|RBW
argument_list|)
return|;
block|}
DECL|method|getReplica (final DataNode datanode, final String bpid, final ReplicaState expectedState)
specifier|private
specifier|static
name|LocalReplicaInPipeline
name|getReplica
parameter_list|(
specifier|final
name|DataNode
name|datanode
parameter_list|,
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|ReplicaState
name|expectedState
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|Collection
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicas
init|=
name|FsDatasetTestUtil
operator|.
name|getReplicas
argument_list|(
name|datanode
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|bpid
argument_list|)
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
literal|5
operator|&&
name|replicas
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"wait since replicas.size() == 0; i="
operator|+
name|i
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ReplicaInfo
name|r
init|=
name|replicas
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedState
argument_list|,
name|r
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|LocalReplicaInPipeline
operator|)
name|r
return|;
block|}
annotation|@
name|Test
DECL|method|testTransferRbw ()
specifier|public
name|void
name|testTransferRbw
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
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
name|REPLICATION
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|//create a file, write some data and leave it open.
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|size
init|=
operator|(
literal|1
operator|<<
literal|16
operator|)
operator|+
name|RAN
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|16
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"size = "
operator|+
name|size
argument_list|)
expr_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
name|REPLICATION
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|remaining
init|=
name|size
init|;
name|remaining
operator|>
literal|0
condition|;
control|)
block|{
name|RAN
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|bytes
operator|.
name|length
operator|<
name|remaining
condition|?
name|bytes
operator|.
name|length
else|:
name|remaining
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|remaining
operator|-=
name|len
expr_stmt|;
block|}
comment|//get the RBW
specifier|final
name|ReplicaBeingWritten
name|oldrbw
decl_stmt|;
specifier|final
name|DataNode
name|newnode
decl_stmt|;
specifier|final
name|DatanodeInfo
name|newnodeinfo
decl_stmt|;
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
block|{
specifier|final
name|DataNode
name|oldnode
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
decl_stmt|;
comment|// DataXceiverServer#writeThrottler is null if
comment|// dfs.datanode.data.write.bandwidthPerSec default value is 0.
name|Assert
operator|.
name|assertNull
argument_list|(
name|oldnode
operator|.
name|xserver
operator|.
name|getWriteThrottler
argument_list|()
argument_list|)
expr_stmt|;
name|oldrbw
operator|=
name|getRbw
argument_list|(
name|oldnode
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"oldrbw = "
operator|+
name|oldrbw
argument_list|)
expr_stmt|;
comment|//add a datanode
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_DATANODE_DATA_WRITE_BANDWIDTHPERSEC_KEY
argument_list|,
literal|1024
operator|*
literal|1024
operator|*
literal|8
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
name|newnode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|REPLICATION
argument_list|)
expr_stmt|;
comment|// DataXceiverServer#writeThrottler#balancer is equal to
comment|// dfs.datanode.data.write.bandwidthPerSec value if
comment|// dfs.datanode.data.write.bandwidthPerSec value is not zero.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|8
argument_list|,
name|newnode
operator|.
name|xserver
operator|.
name|getWriteThrottler
argument_list|()
operator|.
name|getBandwidth
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeInfo
name|oldnodeinfo
decl_stmt|;
block|{
specifier|final
name|DatanodeInfo
index|[]
name|datatnodeinfos
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|(               )
operator|.
name|getDatanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|datatnodeinfos
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DatanodeRegistration
name|dnReg
init|=
name|newnode
operator|.
name|getDNRegistrationForBP
argument_list|(
name|bpid
argument_list|)
init|;
name|i
operator|<
name|datatnodeinfos
operator|.
name|length
operator|&&
operator|!
name|datatnodeinfos
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|dnReg
argument_list|)
condition|;
name|i
operator|++
control|)
empty_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|i
operator|<
name|datatnodeinfos
operator|.
name|length
argument_list|)
expr_stmt|;
name|newnodeinfo
operator|=
name|datatnodeinfos
index|[
name|i
index|]
expr_stmt|;
name|oldnodeinfo
operator|=
name|datatnodeinfos
index|[
literal|1
operator|-
name|i
index|]
expr_stmt|;
block|}
comment|//transfer RBW
specifier|final
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|oldrbw
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|oldrbw
operator|.
name|getBytesAcked
argument_list|()
argument_list|,
name|oldrbw
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BlockOpResponseProto
name|s
init|=
name|DFSTestUtil
operator|.
name|transferRbw
argument_list|(
name|b
argument_list|,
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
name|fs
argument_list|)
argument_list|,
name|oldnodeinfo
argument_list|,
name|newnodeinfo
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|SUCCESS
argument_list|,
name|s
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//check new rbw
specifier|final
name|ReplicaBeingWritten
name|newrbw
init|=
name|getRbw
argument_list|(
name|newnode
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"newrbw = "
operator|+
name|newrbw
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|oldrbw
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|newrbw
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|oldrbw
operator|.
name|getGenerationStamp
argument_list|()
argument_list|,
name|newrbw
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|oldrbw
operator|.
name|getVisibleLength
argument_list|()
argument_list|,
name|newrbw
operator|.
name|getVisibleLength
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
finally|finally
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

