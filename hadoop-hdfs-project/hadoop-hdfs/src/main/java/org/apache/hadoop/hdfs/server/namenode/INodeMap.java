begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|List
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
name|fs
operator|.
name|permission
operator|.
name|PermissionStatus
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
name|BlockStoragePolicySuite
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
name|Quota
operator|.
name|Counts
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
name|GSet
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
name|LightWeightGSet
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

begin_comment
comment|/**  * Storing all the {@link INode}s and maintaining the mapping between INode ID  * and INode.    */
end_comment

begin_class
DECL|class|INodeMap
specifier|public
class|class
name|INodeMap
block|{
DECL|method|newInstance (INodeDirectory rootDir)
specifier|static
name|INodeMap
name|newInstance
parameter_list|(
name|INodeDirectory
name|rootDir
parameter_list|)
block|{
comment|// Compute the map capacity by allocating 1% of total memory
name|int
name|capacity
init|=
name|LightWeightGSet
operator|.
name|computeCapacity
argument_list|(
literal|1
argument_list|,
literal|"INodeMap"
argument_list|)
decl_stmt|;
name|GSet
argument_list|<
name|INode
argument_list|,
name|INodeWithAdditionalFields
argument_list|>
name|map
init|=
operator|new
name|LightWeightGSet
argument_list|<
name|INode
argument_list|,
name|INodeWithAdditionalFields
argument_list|>
argument_list|(
name|capacity
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|rootDir
argument_list|)
expr_stmt|;
return|return
operator|new
name|INodeMap
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|/** Synchronized by external lock. */
DECL|field|map
specifier|private
specifier|final
name|GSet
argument_list|<
name|INode
argument_list|,
name|INodeWithAdditionalFields
argument_list|>
name|map
decl_stmt|;
DECL|method|getMapIterator ()
specifier|public
name|Iterator
argument_list|<
name|INodeWithAdditionalFields
argument_list|>
name|getMapIterator
parameter_list|()
block|{
return|return
name|map
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|INodeMap (GSet<INode, INodeWithAdditionalFields> map)
specifier|private
name|INodeMap
parameter_list|(
name|GSet
argument_list|<
name|INode
argument_list|,
name|INodeWithAdditionalFields
argument_list|>
name|map
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|map
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
comment|/**    * Add an {@link INode} into the {@link INode} map. Replace the old value if     * necessary.     * @param inode The {@link INode} to be added to the map.    */
DECL|method|put (INode inode)
specifier|public
specifier|final
name|void
name|put
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
if|if
condition|(
name|inode
operator|instanceof
name|INodeWithAdditionalFields
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
operator|(
name|INodeWithAdditionalFields
operator|)
name|inode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove a {@link INode} from the map.    * @param inode The {@link INode} to be removed.    */
DECL|method|remove (INode inode)
specifier|public
specifier|final
name|void
name|remove
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|inode
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return The size of the map.    */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Get the {@link INode} with the given id from the map.    * @param id ID of the {@link INode}.    * @return The {@link INode} in the map with the given id. Return null if no     *         such {@link INode} in the map.    */
DECL|method|get (long id)
specifier|public
name|INode
name|get
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|INode
name|inode
init|=
operator|new
name|INodeWithAdditionalFields
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
block|{
annotation|@
name|Override
name|void
name|recordModification
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|destroyAndCollectBlocks
parameter_list|(
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
comment|// Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|Counts
name|computeQuotaUsage
parameter_list|(
name|Counts
name|counts
parameter_list|,
name|boolean
name|useCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ContentSummaryComputationContext
name|computeContentSummary
parameter_list|(
name|ContentSummaryComputationContext
name|summary
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Counts
name|cleanSubtree
parameter_list|(
name|int
name|snapshotId
parameter_list|,
name|int
name|priorSnapshotId
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getStoragePolicyID
parameter_list|()
block|{
return|return
name|BlockStoragePolicySuite
operator|.
name|ID_UNSPECIFIED
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getLocalStoragePolicyID
parameter_list|()
block|{
return|return
name|BlockStoragePolicySuite
operator|.
name|ID_UNSPECIFIED
return|;
block|}
block|}
decl_stmt|;
return|return
name|map
operator|.
name|get
argument_list|(
name|inode
argument_list|)
return|;
block|}
comment|/**    * Clear the {@link #map}    */
DECL|method|clear ()
specifier|public
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
block|}
end_class

end_unit

