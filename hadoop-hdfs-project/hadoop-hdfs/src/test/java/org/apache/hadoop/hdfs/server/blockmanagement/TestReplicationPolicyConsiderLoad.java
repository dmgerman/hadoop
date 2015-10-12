begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|HashSet
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
name|TestBlockStoragePolicy
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestReplicationPolicyConsiderLoad
specifier|public
class|class
name|TestReplicationPolicyConsiderLoad
extends|extends
name|BaseReplicationPolicyTest
block|{
DECL|method|TestReplicationPolicyConsiderLoad (String blockPlacementPolicy)
specifier|public
name|TestReplicationPolicyConsiderLoad
parameter_list|(
name|String
name|blockPlacementPolicy
parameter_list|)
block|{
name|this
operator|.
name|blockPlacementPolicy
operator|=
name|blockPlacementPolicy
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|BlockPlacementPolicyDefault
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
block|,
block|{
name|BlockPlacementPolicyWithUpgradeDomain
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDatanodeDescriptors (Configuration conf)
name|DatanodeDescriptor
index|[]
name|getDatanodeDescriptors
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|racks
init|=
block|{
literal|"/rack1"
block|,
literal|"/rack1"
block|,
literal|"/rack2"
block|,
literal|"/rack2"
block|,
literal|"/rack3"
block|,
literal|"/rack3"
block|}
decl_stmt|;
name|storages
operator|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|racks
argument_list|)
expr_stmt|;
return|return
name|DFSTestUtil
operator|.
name|toDatanodeDescriptor
argument_list|(
name|storages
argument_list|)
return|;
block|}
DECL|field|EPSILON
specifier|private
specifier|final
name|double
name|EPSILON
init|=
literal|0.0001
decl_stmt|;
comment|/**    * Tests that chooseTarget with considerLoad set to true correctly calculates    * load with decommissioned nodes.    */
annotation|@
name|Test
DECL|method|testChooseTargetWithDecomNodes ()
specifier|public
name|void
name|testChooseTargetWithDecomNodes
parameter_list|()
throws|throws
name|IOException
block|{
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|dnManager
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|updateHeartbeat
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|,
name|BlockManagerTestUtil
operator|.
name|getStorageReportsForDatanode
argument_list|(
name|dataNodes
index|[
literal|3
index|]
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|3
index|]
operator|.
name|getCacheCapacity
argument_list|()
argument_list|,
name|dataNodes
index|[
literal|3
index|]
operator|.
name|getCacheUsed
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dnManager
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|updateHeartbeat
argument_list|(
name|dataNodes
index|[
literal|4
index|]
argument_list|,
name|BlockManagerTestUtil
operator|.
name|getStorageReportsForDatanode
argument_list|(
name|dataNodes
index|[
literal|4
index|]
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|4
index|]
operator|.
name|getCacheCapacity
argument_list|()
argument_list|,
name|dataNodes
index|[
literal|4
index|]
operator|.
name|getCacheUsed
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dnManager
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|updateHeartbeat
argument_list|(
name|dataNodes
index|[
literal|5
index|]
argument_list|,
name|BlockManagerTestUtil
operator|.
name|getStorageReportsForDatanode
argument_list|(
name|dataNodes
index|[
literal|5
index|]
argument_list|)
argument_list|,
name|dataNodes
index|[
literal|5
index|]
operator|.
name|getCacheCapacity
argument_list|()
argument_list|,
name|dataNodes
index|[
literal|5
index|]
operator|.
name|getCacheUsed
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// value in the above heartbeats
specifier|final
name|int
name|load
init|=
literal|2
operator|+
literal|4
operator|+
literal|4
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|double
operator|)
name|load
operator|/
literal|6
argument_list|,
name|dnManager
operator|.
name|getFSClusterStats
argument_list|()
operator|.
name|getInServiceXceiverAverage
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
comment|// Decommission DNs so BlockPlacementPolicyDefault.isGoodTarget()
comment|// returns false
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeDescriptor
name|d
init|=
name|dataNodes
index|[
name|i
index|]
decl_stmt|;
name|dnManager
operator|.
name|getDecomManager
argument_list|()
operator|.
name|startDecommission
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|(
name|double
operator|)
name|load
operator|/
literal|3
argument_list|,
name|dnManager
operator|.
name|getFSClusterStats
argument_list|()
operator|.
name|getInServiceXceiverAverage
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|writerDn
init|=
name|dataNodes
index|[
literal|0
index|]
decl_stmt|;
comment|// Call chooseTarget()
name|DatanodeStorageInfo
index|[]
name|targets
init|=
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockPlacementPolicy
argument_list|()
operator|.
name|chooseTarget
argument_list|(
literal|"testFile.txt"
argument_list|,
literal|3
argument_list|,
name|writerDn
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|1024
argument_list|,
name|TestBlockStoragePolicy
operator|.
name|DEFAULT_STORAGE_POLICY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|targets
operator|.
name|length
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|targetSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|targets
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<
name|storages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|targetSet
operator|.
name|contains
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|dataNodes
index|[
literal|0
index|]
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
name|dataNodes
index|[
literal|1
index|]
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
name|dataNodes
index|[
literal|2
index|]
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Done working on it"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

