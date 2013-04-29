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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

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
name|snapshot
operator|.
name|Snapshot
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
comment|/**  * An anonymous reference to an inode.  *  * This class and its subclasses are used to support multiple access paths.  * A file/directory may have multiple access paths when it is stored in some  * snapshots and it is renamed/moved to other locations.  *   * For example,  * (1) Support we have /abc/foo, say the inode of foo is inode(id=1000,name=foo)  * (2) create snapshot s0 for /abc  * (3) mv /abc/foo /xyz/bar, i.e. inode(id=1000,name=...) is renamed from "foo"  *     to "bar" and its parent becomes /xyz.  *   * Then, /xyz/bar and /abc/.snapshot/s0/foo are two different access paths to  * the same inode, inode(id=1000,name=bar).  *  * With references, we have the following  * - /abc has a child ref(id=1001,name=foo).  * - /xyz has a child ref(id=1002)   * - Both ref(id=1001,name=foo) and ref(id=1002) point to another reference,  *   ref(id=1003,count=2).  * - Finally, ref(id=1003,count=2) points to inode(id=1000,name=bar).  *   * Note 1: For a reference without name, e.g. ref(id=1002), it uses the name  *         of the referred inode.  * Note 2: getParent() always returns the parent in the current state, e.g.  *         inode(id=1000,name=bar).getParent() returns /xyz but not /abc.  */
end_comment

begin_class
DECL|class|INodeReference
specifier|public
specifier|abstract
class|class
name|INodeReference
extends|extends
name|INode
block|{
comment|/**    * Try to remove the given reference and then return the reference count.    * If the given inode is not a reference, return -1;    */
DECL|method|tryRemoveReference (INode inode)
specifier|public
specifier|static
name|int
name|tryRemoveReference
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
if|if
condition|(
operator|!
name|inode
operator|.
name|isReference
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|removeReference
argument_list|(
name|inode
operator|.
name|asReference
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Remove the given reference and then return the reference count.    * If the referred inode is not a WithCount, return -1;    */
DECL|method|removeReference (INodeReference ref)
specifier|private
specifier|static
name|int
name|removeReference
parameter_list|(
name|INodeReference
name|ref
parameter_list|)
block|{
specifier|final
name|INode
name|referred
init|=
name|ref
operator|.
name|getReferredINode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|referred
operator|instanceof
name|WithCount
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|WithCount
name|wc
init|=
operator|(
name|WithCount
operator|)
name|referred
decl_stmt|;
name|wc
operator|.
name|removeReference
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return
name|wc
operator|.
name|getReferenceCount
argument_list|()
return|;
block|}
DECL|field|referred
specifier|private
name|INode
name|referred
decl_stmt|;
DECL|method|INodeReference (INode parent, INode referred)
specifier|public
name|INodeReference
parameter_list|(
name|INode
name|parent
parameter_list|,
name|INode
name|referred
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|referred
operator|=
name|referred
expr_stmt|;
block|}
DECL|method|getReferredINode ()
specifier|public
specifier|final
name|INode
name|getReferredINode
parameter_list|()
block|{
return|return
name|referred
return|;
block|}
DECL|method|setReferredINode (INode referred)
specifier|public
specifier|final
name|void
name|setReferredINode
parameter_list|(
name|INode
name|referred
parameter_list|)
block|{
name|this
operator|.
name|referred
operator|=
name|referred
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isReference ()
specifier|public
specifier|final
name|boolean
name|isReference
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|asReference ()
specifier|public
specifier|final
name|INodeReference
name|asReference
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|isFile ()
specifier|public
specifier|final
name|boolean
name|isFile
parameter_list|()
block|{
return|return
name|referred
operator|.
name|isFile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asFile ()
specifier|public
specifier|final
name|INodeFile
name|asFile
parameter_list|()
block|{
return|return
name|referred
operator|.
name|asFile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDirectory ()
specifier|public
specifier|final
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|referred
operator|.
name|isDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asDirectory ()
specifier|public
specifier|final
name|INodeDirectory
name|asDirectory
parameter_list|()
block|{
return|return
name|referred
operator|.
name|asDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isSymlink ()
specifier|public
specifier|final
name|boolean
name|isSymlink
parameter_list|()
block|{
return|return
name|referred
operator|.
name|isSymlink
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asSymlink ()
specifier|public
specifier|final
name|INodeSymlink
name|asSymlink
parameter_list|()
block|{
return|return
name|referred
operator|.
name|asSymlink
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalNameBytes ()
specifier|public
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
block|{
return|return
name|referred
operator|.
name|getLocalNameBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setLocalName (byte[] name)
specifier|public
name|void
name|setLocalName
parameter_list|(
name|byte
index|[]
name|name
parameter_list|)
block|{
name|referred
operator|.
name|setLocalName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
specifier|final
name|long
name|getId
parameter_list|()
block|{
return|return
name|referred
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPermissionStatus (Snapshot snapshot)
specifier|public
specifier|final
name|PermissionStatus
name|getPermissionStatus
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getPermissionStatus
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUserName (Snapshot snapshot)
specifier|public
specifier|final
name|String
name|getUserName
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getUserName
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|final
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|referred
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGroupName (Snapshot snapshot)
specifier|public
specifier|final
name|String
name|getGroupName
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getGroupName
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setGroup (String group)
specifier|final
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|referred
operator|.
name|setGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFsPermission (Snapshot snapshot)
specifier|public
specifier|final
name|FsPermission
name|getFsPermission
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getFsPermission
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setPermission (FsPermission permission)
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
name|referred
operator|.
name|setPermission
argument_list|(
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getModificationTime (Snapshot snapshot)
specifier|public
specifier|final
name|long
name|getModificationTime
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getModificationTime
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateModificationTime (long mtime, Snapshot latest)
specifier|public
specifier|final
name|INode
name|updateModificationTime
parameter_list|(
name|long
name|mtime
parameter_list|,
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
return|return
name|referred
operator|.
name|updateModificationTime
argument_list|(
name|mtime
argument_list|,
name|latest
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setModificationTime (long modificationTime)
specifier|public
specifier|final
name|void
name|setModificationTime
parameter_list|(
name|long
name|modificationTime
parameter_list|)
block|{
name|referred
operator|.
name|setModificationTime
argument_list|(
name|modificationTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAccessTime (Snapshot snapshot)
specifier|public
specifier|final
name|long
name|getAccessTime
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getAccessTime
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setAccessTime (long accessTime)
specifier|public
specifier|final
name|void
name|setAccessTime
parameter_list|(
name|long
name|accessTime
parameter_list|)
block|{
name|referred
operator|.
name|setAccessTime
argument_list|(
name|accessTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recordModification (Snapshot latest)
specifier|final
name|INode
name|recordModification
parameter_list|(
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|referred
operator|.
name|recordModification
argument_list|(
name|latest
argument_list|)
expr_stmt|;
comment|// reference is never replaced
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|cleanSubtree (Snapshot snapshot, Snapshot prior, BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
parameter_list|,
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
throws|throws
name|QuotaExceededException
block|{
return|return
name|referred
operator|.
name|cleanSubtree
argument_list|(
name|snapshot
argument_list|,
name|prior
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|destroyAndCollectBlocks ( BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
specifier|final
name|void
name|destroyAndCollectBlocks
parameter_list|(
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
name|removeReference
argument_list|(
name|this
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|referred
operator|.
name|destroyAndCollectBlocks
argument_list|(
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (Content.CountsMap countsMap)
specifier|public
specifier|final
name|Content
operator|.
name|CountsMap
name|computeContentSummary
parameter_list|(
name|Content
operator|.
name|CountsMap
name|countsMap
parameter_list|)
block|{
return|return
name|referred
operator|.
name|computeContentSummary
argument_list|(
name|countsMap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (Content.Counts counts)
specifier|public
specifier|final
name|Content
operator|.
name|Counts
name|computeContentSummary
parameter_list|(
name|Content
operator|.
name|Counts
name|counts
parameter_list|)
block|{
return|return
name|referred
operator|.
name|computeContentSummary
argument_list|(
name|counts
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (Quota.Counts counts, boolean useCache, int lastSnapshotId)
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
name|useCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
return|return
name|referred
operator|.
name|computeQuotaUsage
argument_list|(
name|counts
argument_list|,
name|useCache
argument_list|,
name|lastSnapshotId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSnapshotINode (Snapshot snapshot)
specifier|public
specifier|final
name|INode
name|getSnapshotINode
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|referred
operator|.
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNsQuota ()
specifier|public
specifier|final
name|long
name|getNsQuota
parameter_list|()
block|{
return|return
name|referred
operator|.
name|getNsQuota
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDsQuota ()
specifier|public
specifier|final
name|long
name|getDsQuota
parameter_list|()
block|{
return|return
name|referred
operator|.
name|getDsQuota
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clear ()
specifier|public
specifier|final
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|referred
operator|=
literal|null
expr_stmt|;
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
if|if
condition|(
name|this
operator|instanceof
name|DstReference
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", dstSnapshotId="
operator|+
operator|(
operator|(
name|DstReference
operator|)
name|this
operator|)
operator|.
name|dstSnapshotId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|instanceof
name|WithCount
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", count="
operator|+
operator|(
operator|(
name|WithCount
operator|)
name|this
operator|)
operator|.
name|getReferenceCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|prefix
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"->"
argument_list|)
expr_stmt|;
name|getReferredINode
argument_list|()
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|b
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
DECL|method|getDstSnapshotId ()
specifier|public
name|int
name|getDstSnapshotId
parameter_list|()
block|{
return|return
name|Snapshot
operator|.
name|INVALID_ID
return|;
block|}
comment|/** An anonymous reference with reference count. */
DECL|class|WithCount
specifier|public
specifier|static
class|class
name|WithCount
extends|extends
name|INodeReference
block|{
DECL|field|withNameList
specifier|private
specifier|final
name|List
argument_list|<
name|WithName
argument_list|>
name|withNameList
init|=
operator|new
name|ArrayList
argument_list|<
name|WithName
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|WithCount (INodeReference parent, INode referred)
specifier|public
name|WithCount
parameter_list|(
name|INodeReference
name|parent
parameter_list|,
name|INode
name|referred
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|referred
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|referred
operator|.
name|isReference
argument_list|()
argument_list|)
expr_stmt|;
name|referred
operator|.
name|setParentReference
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|getReferenceCount ()
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
name|int
name|count
init|=
name|withNameList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|getParentReference
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** Increment and then return the reference count. */
DECL|method|addReference (INodeReference ref)
specifier|public
name|void
name|addReference
parameter_list|(
name|INodeReference
name|ref
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|instanceof
name|WithName
condition|)
block|{
name|withNameList
operator|.
name|add
argument_list|(
operator|(
name|WithName
operator|)
name|ref
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ref
operator|instanceof
name|DstReference
condition|)
block|{
name|setParentReference
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Decrement and then return the reference count. */
DECL|method|removeReference (INodeReference ref)
specifier|public
name|void
name|removeReference
parameter_list|(
name|INodeReference
name|ref
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|instanceof
name|WithName
condition|)
block|{
name|Iterator
argument_list|<
name|INodeReference
operator|.
name|WithName
argument_list|>
name|iter
init|=
name|withNameList
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|iter
operator|.
name|next
argument_list|()
operator|==
name|ref
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|ref
operator|==
name|getParentReference
argument_list|()
condition|)
block|{
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addSpaceConsumed (long nsDelta, long dsDelta, boolean verify, int snapshotId)
specifier|public
specifier|final
name|void
name|addSpaceConsumed
parameter_list|(
name|long
name|nsDelta
parameter_list|,
name|long
name|dsDelta
parameter_list|,
name|boolean
name|verify
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|INodeReference
name|parentRef
init|=
name|getParentReference
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentRef
operator|!=
literal|null
condition|)
block|{
name|parentRef
operator|.
name|addSpaceConsumed
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|,
name|verify
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
block|}
name|addSpaceConsumedToRenameSrc
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|,
name|verify
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSpaceConsumedToRenameSrc (long nsDelta, long dsDelta, boolean verify, int snapshotId)
specifier|public
specifier|final
name|void
name|addSpaceConsumedToRenameSrc
parameter_list|(
name|long
name|nsDelta
parameter_list|,
name|long
name|dsDelta
parameter_list|,
name|boolean
name|verify
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
name|snapshotId
operator|!=
name|Snapshot
operator|.
name|INVALID_ID
condition|)
block|{
comment|// sort withNameList based on the lastSnapshotId
name|Collections
operator|.
name|sort
argument_list|(
name|withNameList
argument_list|,
operator|new
name|Comparator
argument_list|<
name|WithName
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|WithName
name|w1
parameter_list|,
name|WithName
name|w2
parameter_list|)
block|{
return|return
name|w1
operator|.
name|lastSnapshotId
operator|-
name|w2
operator|.
name|lastSnapshotId
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|INodeReference
operator|.
name|WithName
name|withName
range|:
name|withNameList
control|)
block|{
if|if
condition|(
name|withName
operator|.
name|getLastSnapshotId
argument_list|()
operator|>=
name|snapshotId
condition|)
block|{
name|withName
operator|.
name|addSpaceConsumed
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|,
name|verify
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
comment|/** A reference with a fixed name. */
DECL|class|WithName
specifier|public
specifier|static
class|class
name|WithName
extends|extends
name|INodeReference
block|{
DECL|field|name
specifier|private
specifier|final
name|byte
index|[]
name|name
decl_stmt|;
comment|/**      * The id of the last snapshot in the src tree when this WithName node was       * generated. When calculating the quota usage of the referred node, only       * the files/dirs existing when this snapshot was taken will be counted for       * this WithName node and propagated along its ancestor path.      */
DECL|field|lastSnapshotId
specifier|private
specifier|final
name|int
name|lastSnapshotId
decl_stmt|;
DECL|method|WithName (INodeDirectory parent, WithCount referred, byte[] name, int lastSnapshotId)
specifier|public
name|WithName
parameter_list|(
name|INodeDirectory
name|parent
parameter_list|,
name|WithCount
name|referred
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|referred
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|lastSnapshotId
operator|=
name|lastSnapshotId
expr_stmt|;
name|referred
operator|.
name|addReference
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLocalNameBytes ()
specifier|public
specifier|final
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|setLocalName (byte[] name)
specifier|public
specifier|final
name|void
name|setLocalName
parameter_list|(
name|byte
index|[]
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot set name: "
operator|+
name|getClass
argument_list|()
operator|+
literal|" is immutable."
argument_list|)
throw|;
block|}
DECL|method|getLastSnapshotId ()
specifier|public
name|int
name|getLastSnapshotId
parameter_list|()
block|{
return|return
name|lastSnapshotId
return|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (Quota.Counts counts, boolean useCache, int lastSnapshotId)
specifier|public
specifier|final
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
name|useCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
comment|// if this.lastSnapshotId< lastSnapshotId, the rename of the referred
comment|// node happened before the rename of its ancestor. This should be
comment|// impossible since for WithName node we only count its children at the
comment|// time of the rename.
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|lastSnapshotId
operator|>=
name|lastSnapshotId
argument_list|)
expr_stmt|;
specifier|final
name|INode
name|referred
init|=
name|this
operator|.
name|getReferredINode
argument_list|()
operator|.
name|asReference
argument_list|()
operator|.
name|getReferredINode
argument_list|()
decl_stmt|;
comment|// we cannot use cache for the referred node since its cached quota may
comment|// have already been updated by changes in the current tree
return|return
name|referred
operator|.
name|computeQuotaUsage
argument_list|(
name|counts
argument_list|,
literal|false
argument_list|,
name|this
operator|.
name|lastSnapshotId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cleanSubtree (Snapshot snapshot, Snapshot prior, BlocksMapUpdateInfo collectedBlocks, List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
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
throws|throws
name|QuotaExceededException
block|{
name|Quota
operator|.
name|Counts
name|counts
init|=
name|getReferredINode
argument_list|()
operator|.
name|cleanSubtree
argument_list|(
name|snapshot
argument_list|,
name|prior
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
decl_stmt|;
name|INodeReference
name|ref
init|=
name|getReferredINode
argument_list|()
operator|.
name|getParentReference
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|ref
operator|.
name|addSpaceConsumed
argument_list|(
operator|-
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|)
argument_list|,
operator|-
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|DISKSPACE
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Snapshot
operator|.
name|INVALID_ID
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
block|}
DECL|class|DstReference
specifier|public
specifier|static
class|class
name|DstReference
extends|extends
name|INodeReference
block|{
comment|/**      * Record the latest snapshot of the dst subtree before the rename. For      * later operations on the moved/renamed files/directories, if the latest      * snapshot is after this dstSnapshot, changes will be recorded to the      * latest snapshot. Otherwise changes will be recorded to the snapshot      * belonging to the src of the rename.      *       * {@link Snapshot#INVALID_ID} means no dstSnapshot (e.g., src of the      * first-time rename).      */
DECL|field|dstSnapshotId
specifier|private
specifier|final
name|int
name|dstSnapshotId
decl_stmt|;
annotation|@
name|Override
DECL|method|getDstSnapshotId ()
specifier|public
specifier|final
name|int
name|getDstSnapshotId
parameter_list|()
block|{
return|return
name|dstSnapshotId
return|;
block|}
DECL|method|DstReference (INodeDirectory parent, WithCount referred, final int dstSnapshotId)
specifier|public
name|DstReference
parameter_list|(
name|INodeDirectory
name|parent
parameter_list|,
name|WithCount
name|referred
parameter_list|,
specifier|final
name|int
name|dstSnapshotId
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|referred
argument_list|)
expr_stmt|;
name|this
operator|.
name|dstSnapshotId
operator|=
name|dstSnapshotId
expr_stmt|;
name|referred
operator|.
name|addReference
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanSubtree (Snapshot snapshot, Snapshot prior, BlocksMapUpdateInfo collectedBlocks, List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
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
throws|throws
name|QuotaExceededException
block|{
name|Quota
operator|.
name|Counts
name|counts
init|=
name|getReferredINode
argument_list|()
operator|.
name|cleanSubtree
argument_list|(
name|snapshot
argument_list|,
name|prior
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
comment|// also need to update quota usage along the corresponding WithName node
name|WithCount
name|wc
init|=
operator|(
name|WithCount
operator|)
name|getReferredINode
argument_list|()
decl_stmt|;
name|wc
operator|.
name|addSpaceConsumedToRenameSrc
argument_list|(
operator|-
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|)
argument_list|,
operator|-
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|DISKSPACE
argument_list|)
argument_list|,
literal|true
argument_list|,
name|snapshot
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
block|}
block|}
end_class

end_unit

