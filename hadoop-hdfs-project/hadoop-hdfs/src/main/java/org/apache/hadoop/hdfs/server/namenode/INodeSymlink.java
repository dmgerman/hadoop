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
name|protocol
operator|.
name|QuotaExceededException
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
name|Content
operator|.
name|CountsMap
operator|.
name|Key
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
DECL|method|recordModification (Snapshot latest)
name|INode
name|recordModification
parameter_list|(
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
return|return
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
condition|?
name|getParent
argument_list|()
operator|.
name|saveChild2Snapshot
argument_list|(
name|this
argument_list|,
name|latest
argument_list|,
operator|new
name|INodeSymlink
argument_list|(
name|this
argument_list|)
argument_list|)
else|:
name|this
return|;
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
DECL|method|cleanSubtree (final Snapshot snapshot, Snapshot prior, final BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|==
literal|null
operator|&&
name|prior
operator|==
literal|null
condition|)
block|{
name|destroyAndCollectBlocks
argument_list|(
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
block|}
return|return
name|Quota
operator|.
name|Counts
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|destroyAndCollectBlocks (final BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|void
name|destroyAndCollectBlocks
parameter_list|(
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
name|removedINodes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (Quota.Counts counts, boolean updateCache, int lastSnapshotId)
specifier|public
name|Quota
operator|.
name|Counts
name|computeQuotaUsage
parameter_list|(
name|Quota
operator|.
name|Counts
name|counts
parameter_list|,
name|boolean
name|updateCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary ( final Content.CountsMap countsMap)
specifier|public
name|Content
operator|.
name|CountsMap
name|computeContentSummary
parameter_list|(
specifier|final
name|Content
operator|.
name|CountsMap
name|countsMap
parameter_list|)
block|{
name|computeContentSummary
argument_list|(
name|countsMap
operator|.
name|getCounts
argument_list|(
name|Key
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|countsMap
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (final Content.Counts counts)
specifier|public
name|Content
operator|.
name|Counts
name|computeContentSummary
parameter_list|(
specifier|final
name|Content
operator|.
name|Counts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|SYMLINK
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix, final Snapshot snapshot)
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
name|Snapshot
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
block|}
end_class

end_unit

