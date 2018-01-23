begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.sps
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
name|sps
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
name|StorageType
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
name|server
operator|.
name|namenode
operator|.
name|sps
operator|.
name|Context
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
name|sps
operator|.
name|FileIdCollector
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
name|sps
operator|.
name|IntraSPSNameNodeBlockMoveTaskHandler
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
name|sps
operator|.
name|IntraSPSNameNodeContext
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
name|sps
operator|.
name|SPSService
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
name|sps
operator|.
name|StoragePolicySatisfier
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
name|sps
operator|.
name|TestStoragePolicySatisfier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Tests the external sps service plugins.  */
end_comment

begin_class
DECL|class|TestExternalStoragePolicySatisfier
specifier|public
class|class
name|TestExternalStoragePolicySatisfier
extends|extends
name|TestStoragePolicySatisfier
block|{
DECL|field|allDiskTypes
specifier|private
name|StorageType
index|[]
index|[]
name|allDiskTypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|createCluster ()
specifier|public
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|getConf
argument_list|()
operator|.
name|setLong
argument_list|(
literal|"dfs.block.size"
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setCluster
argument_list|(
name|startCluster
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|allDiskTypes
argument_list|,
name|NUM_OF_DATANODES
argument_list|,
name|STORAGES_PER_DATANODE
argument_list|,
name|CAPACITY
argument_list|)
argument_list|)
expr_stmt|;
name|getFS
argument_list|()
expr_stmt|;
name|writeContent
argument_list|(
name|FILE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startCluster (final Configuration conf, StorageType[][] storageTypes, int numberOfDatanodes, int storagesPerDn, long nodeCapacity)
specifier|public
name|MiniDFSCluster
name|startCluster
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|StorageType
index|[]
index|[]
name|storageTypes
parameter_list|,
name|int
name|numberOfDatanodes
parameter_list|,
name|int
name|storagesPerDn
parameter_list|,
name|long
name|nodeCapacity
parameter_list|)
throws|throws
name|IOException
block|{
name|long
index|[]
index|[]
name|capacities
init|=
operator|new
name|long
index|[
name|numberOfDatanodes
index|]
index|[
name|storagesPerDn
index|]
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
name|numberOfDatanodes
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|storagesPerDn
condition|;
name|j
operator|++
control|)
block|{
name|capacities
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|nodeCapacity
expr_stmt|;
block|}
block|}
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
name|numberOfDatanodes
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
name|storagesPerDn
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|storageTypes
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|SPSService
name|spsService
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
operator|.
name|getSPSService
argument_list|()
decl_stmt|;
name|spsService
operator|.
name|stopGracefully
argument_list|()
expr_stmt|;
name|IntraSPSNameNodeContext
name|context
init|=
operator|new
name|IntraSPSNameNodeContext
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
argument_list|,
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
argument_list|,
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
operator|.
name|getSPSService
argument_list|()
argument_list|)
decl_stmt|;
name|spsService
operator|.
name|init
argument_list|(
name|context
argument_list|,
operator|new
name|ExternalSPSFileIDCollector
argument_list|(
name|context
argument_list|,
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
operator|.
name|getSPSService
argument_list|()
argument_list|,
literal|5
argument_list|)
argument_list|,
operator|new
name|IntraSPSNameNodeBlockMoveTaskHandler
argument_list|(
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
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|spsService
operator|.
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
DECL|method|createFileIdCollector (StoragePolicySatisfier sps, Context ctxt)
specifier|public
name|FileIdCollector
name|createFileIdCollector
parameter_list|(
name|StoragePolicySatisfier
name|sps
parameter_list|,
name|Context
name|ctxt
parameter_list|)
block|{
return|return
operator|new
name|ExternalSPSFileIDCollector
argument_list|(
name|ctxt
argument_list|,
name|sps
argument_list|,
literal|5
argument_list|)
return|;
block|}
comment|/**    * This test need not run as external scan is not a batch based scanning right    * now.    */
annotation|@
name|Ignore
argument_list|(
literal|"ExternalFileIdCollector is not batch based right now."
operator|+
literal|" So, ignoring it."
argument_list|)
DECL|method|testBatchProcessingForSPSDirectory ()
specifier|public
name|void
name|testBatchProcessingForSPSDirectory
parameter_list|()
throws|throws
name|Exception
block|{   }
block|}
end_class

end_unit

