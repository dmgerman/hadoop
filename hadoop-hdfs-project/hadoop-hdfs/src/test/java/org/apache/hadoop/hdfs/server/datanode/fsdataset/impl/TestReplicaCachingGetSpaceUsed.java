begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
operator|.
name|fsdataset
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|math
operator|.
name|RandomUtils
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
name|CachingGetSpaceUsed
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
name|DFSInputStream
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|Replica
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
name|FsDatasetSpi
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
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
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
name|Set
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DU_INTERVAL_KEY
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
comment|/**  * Unit test for ReplicaCachingGetSpaceUsed class.  */
end_comment

begin_class
DECL|class|TestReplicaCachingGetSpaceUsed
specifier|public
class|class
name|TestReplicaCachingGetSpaceUsed
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|dataNode
specifier|private
name|DataNode
name|dataNode
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
throws|,
name|NoSuchMethodException
throws|,
name|InterruptedException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.getspaceused.classname"
argument_list|,
name|ReplicaCachingGetSpaceUsed
operator|.
name|class
argument_list|,
name|CachingGetSpaceUsed
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FS_DU_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
literal|"fs.getspaceused.jitterMillis"
argument_list|,
literal|0
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
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|dataNode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
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
throws|throws
name|IOException
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReplicaCachingGetSpaceUsedByFINALIZEDReplica ()
specifier|public
name|void
name|testReplicaCachingGetSpaceUsedByFINALIZEDReplica
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"/testReplicaCachingGetSpaceUsedByFINALIZEDReplica"
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|20480
index|]
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|DFSInputStream
name|dfsInputStream
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|open
argument_list|(
literal|"/testReplicaCachingGetSpaceUsedByFINALIZEDReplica"
argument_list|)
decl_stmt|;
name|long
name|blockLength
init|=
literal|0
decl_stmt|;
name|long
name|metaLength
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|dfsInputStream
operator|.
name|getAllBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locatedBlock
range|:
name|locatedBlocks
control|)
block|{
name|ExtendedBlock
name|extendedBlock
init|=
name|locatedBlock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|blockLength
operator|+=
name|extendedBlock
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
name|metaLength
operator|+=
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getMetaDataInputStream
argument_list|(
name|extendedBlock
argument_list|)
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|// Guarantee ReplicaCachingGetSpaceUsed#refresh() is called after replica
comment|// has been written to disk.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockLength
operator|+
name|metaLength
argument_list|,
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testReplicaCachingGetSpaceUsedByFINALIZEDReplica"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplicaCachingGetSpaceUsedByRBWReplica ()
specifier|public
name|void
name|testReplicaCachingGetSpaceUsedByRBWReplica
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"/testReplicaCachingGetSpaceUsedByRBWReplica"
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|20480
index|]
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|DFSInputStream
name|dfsInputStream
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|open
argument_list|(
literal|"/testReplicaCachingGetSpaceUsedByRBWReplica"
argument_list|)
decl_stmt|;
name|long
name|blockLength
init|=
literal|0
decl_stmt|;
name|long
name|metaLength
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|dfsInputStream
operator|.
name|getAllBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locatedBlock
range|:
name|locatedBlocks
control|)
block|{
name|ExtendedBlock
name|extendedBlock
init|=
name|locatedBlock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|blockLength
operator|+=
name|extendedBlock
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
name|metaLength
operator|+=
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getMetaDataInputStream
argument_list|(
name|extendedBlock
argument_list|)
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|// Guarantee ReplicaCachingGetSpaceUsed#refresh() is called after replica
comment|// has been written to disk.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockLength
operator|+
name|metaLength
argument_list|,
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Guarantee ReplicaCachingGetSpaceUsed#refresh() is called, dfsspaceused is
comment|// recalculated
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// After close operation, the replica state will be transformed from RBW to
comment|// finalized. But the space used of these replicas are all included and the
comment|// dfsUsed value should be same.
name|assertEquals
argument_list|(
name|blockLength
operator|+
name|metaLength
argument_list|,
name|dataNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testReplicaCachingGetSpaceUsedByRBWReplica"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testFsDatasetImplDeepCopyReplica ()
specifier|public
name|void
name|testFsDatasetImplDeepCopyReplica
parameter_list|()
block|{
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsDataset
init|=
name|dataNode
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
name|ModifyThread
name|modifyThread
init|=
operator|new
name|ModifyThread
argument_list|()
decl_stmt|;
name|modifyThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|int
name|retryTimes
init|=
literal|10
decl_stmt|;
while|while
condition|(
name|retryTimes
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Set
argument_list|<
name|?
extends|extends
name|Replica
argument_list|>
name|replicas
init|=
name|fsDataset
operator|.
name|deepCopyReplica
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicas
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|retryTimes
operator|--
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|modifyThread
operator|.
name|setShouldRun
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Encounter IOException when deep copy replica."
argument_list|)
expr_stmt|;
block|}
block|}
name|modifyThread
operator|.
name|setShouldRun
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|ModifyThread
specifier|private
class|class
name|ModifyThread
extends|extends
name|Thread
block|{
DECL|field|shouldRun
specifier|private
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|FSDataOutputStream
name|os
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|int
name|id
init|=
name|RandomUtils
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|os
operator|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFsDatasetImplDeepCopyReplica/"
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|os
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|os
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|os
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
block|{}
block|}
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testFsDatasetImplDeepCopyReplica"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{}
block|}
DECL|method|setShouldRun (boolean shouldRun)
specifier|private
name|void
name|setShouldRun
parameter_list|(
name|boolean
name|shouldRun
parameter_list|)
block|{
name|this
operator|.
name|shouldRun
operator|=
name|shouldRun
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

