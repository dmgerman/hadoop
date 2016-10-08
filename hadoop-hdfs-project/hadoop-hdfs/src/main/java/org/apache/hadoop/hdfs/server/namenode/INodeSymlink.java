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
name|io
operator|.
name|PrintWriter
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
name|classification
operator|.
name|InterfaceAudience
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
name|DFSUtil
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
name|snapshot
operator|.
name|Snapshot
import|;
end_import

begin_comment
comment|/**  * An {@link INode} representing a symbolic link.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeSymlink
specifier|public
class|class
name|INodeSymlink
extends|extends
name|INodeWithAdditionalFields
block|{
DECL|field|symlink
specifier|private
specifier|final
name|byte
index|[]
name|symlink
decl_stmt|;
comment|// The target URI
DECL|method|INodeSymlink (long id, byte[] name, PermissionStatus permissions, long mtime, long atime, String symlink)
name|INodeSymlink
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|,
name|String
name|symlink
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|permissions
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
name|this
operator|.
name|symlink
operator|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|symlink
argument_list|)
expr_stmt|;
block|}
DECL|method|INodeSymlink (INodeSymlink that)
name|INodeSymlink
parameter_list|(
name|INodeSymlink
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
argument_list|)
expr_stmt|;
name|this
operator|.
name|symlink
operator|=
name|that
operator|.
name|symlink
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recordModification (int latestSnapshotId)
name|void
name|recordModification
parameter_list|(
name|int
name|latestSnapshotId
parameter_list|)
block|{
if|if
condition|(
name|isInLatestSnapshot
argument_list|(
name|latestSnapshotId
argument_list|)
condition|)
block|{
name|INodeDirectory
name|parent
init|=
name|getParent
argument_list|()
decl_stmt|;
name|parent
operator|.
name|saveChild2Snapshot
argument_list|(
name|this
argument_list|,
name|latestSnapshotId
argument_list|,
operator|new
name|INodeSymlink
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @return true unconditionally. */
annotation|@
name|Override
DECL|method|isSymlink ()
specifier|public
name|boolean
name|isSymlink
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** @return this object. */
annotation|@
name|Override
DECL|method|asSymlink ()
specifier|public
name|INodeSymlink
name|asSymlink
parameter_list|()
block|{
return|return
name|this
return|;
block|}
DECL|method|getSymlinkString ()
specifier|public
name|String
name|getSymlinkString
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|symlink
argument_list|)
return|;
block|}
DECL|method|getSymlink ()
specifier|public
name|byte
index|[]
name|getSymlink
parameter_list|()
block|{
return|return
name|symlink
return|;
block|}
annotation|@
name|Override
DECL|method|cleanSubtree (ReclaimContext reclaimContext, final int snapshotId, int priorSnapshotId)
specifier|public
name|void
name|cleanSubtree
parameter_list|(
name|ReclaimContext
name|reclaimContext
parameter_list|,
specifier|final
name|int
name|snapshotId
parameter_list|,
name|int
name|priorSnapshotId
parameter_list|)
block|{
if|if
condition|(
name|snapshotId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|&&
name|priorSnapshotId
operator|==
name|Snapshot
operator|.
name|NO_SNAPSHOT_ID
condition|)
block|{
name|destroyAndCollectBlocks
argument_list|(
name|reclaimContext
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|destroyAndCollectBlocks (ReclaimContext reclaimContext)
specifier|public
name|void
name|destroyAndCollectBlocks
parameter_list|(
name|ReclaimContext
name|reclaimContext
parameter_list|)
block|{
name|reclaimContext
operator|.
name|removedINodes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|reclaimContext
operator|.
name|quotaDelta
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|nameSpace
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (BlockStoragePolicySuite bsps, byte blockStoragePolicyId, boolean useCache, int lastSnapshotId)
specifier|public
name|QuotaCounts
name|computeQuotaUsage
parameter_list|(
name|BlockStoragePolicySuite
name|bsps
parameter_list|,
name|byte
name|blockStoragePolicyId
parameter_list|,
name|boolean
name|useCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
return|return
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|nameSpace
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (int snapshotId, final ContentSummaryComputationContext summary)
specifier|public
name|ContentSummaryComputationContext
name|computeContentSummary
parameter_list|(
name|int
name|snapshotId
parameter_list|,
specifier|final
name|ContentSummaryComputationContext
name|summary
parameter_list|)
block|{
name|summary
operator|.
name|nodeIncluded
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|summary
operator|.
name|getCounts
argument_list|()
operator|.
name|addContent
argument_list|(
name|Content
operator|.
name|SYMLINK
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|summary
return|;
block|}
annotation|@
name|Override
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix, final int snapshot)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|,
specifier|final
name|int
name|snapshot
parameter_list|)
block|{
name|super
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeAclFeature ()
specifier|public
name|void
name|removeAclFeature
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ACLs are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|addAclFeature (AclFeature f)
specifier|public
name|void
name|addAclFeature
parameter_list|(
name|AclFeature
name|f
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ACLs are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getXAttrFeature (int snapshotId)
specifier|final
name|XAttrFeature
name|getXAttrFeature
parameter_list|(
name|int
name|snapshotId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"XAttrs are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|removeXAttrFeature ()
specifier|public
name|void
name|removeXAttrFeature
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"XAttrs are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|addXAttrFeature (XAttrFeature f)
specifier|public
name|void
name|addXAttrFeature
parameter_list|(
name|XAttrFeature
name|f
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"XAttrs are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getStoragePolicyID ()
specifier|public
name|byte
name|getStoragePolicyID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Storage policy are not supported on symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getLocalStoragePolicyID ()
specifier|public
name|byte
name|getLocalStoragePolicyID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Storage policy are not supported on symlinks"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

