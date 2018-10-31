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
name|Collections
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|Lists
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
name|AclEntry
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
name|AclEntryScope
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
name|AclEntryType
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
name|AclUtil
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
name|FsAction
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
name|ScopedAclEntries
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
name|AclException
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
name|util
operator|.
name|ReferenceCountMap
import|;
end_import

begin_comment
comment|/**  * AclStorage contains utility methods that define how ACL data is stored in the  * namespace.  *  * If an inode has an ACL, then the ACL bit is set in the inode's  * {@link FsPermission} and the inode also contains an {@link AclFeature}.  For  * the access ACL, the owner and other entries are identical to the owner and  * other bits stored in FsPermission, so we reuse those.  The access mask entry  * is stored into the group permission bits of FsPermission.  This is consistent  * with other file systems' implementations of ACLs and eliminates the need for  * special handling in various parts of the codebase.  For example, if a user  * calls chmod to change group permission bits on a file with an ACL, then the  * expected behavior is to change the ACL's mask entry.  By saving the mask entry  * into the group permission bits, chmod continues to work correctly without  * special handling.  All remaining access entries (named users and named groups)  * are stored as explicit {@link AclEntry} instances in a list inside the  * AclFeature.  Additionally, all default entries are stored in the AclFeature.  *  * The methods in this class encapsulate these rules for reading or writing the  * ACL entries to the appropriate location.  *  * The methods in this class assume that input ACL entry lists have already been  * validated and sorted according to the rules enforced by  * {@link AclTransformation}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AclStorage
specifier|public
specifier|final
class|class
name|AclStorage
block|{
DECL|field|UNIQUE_ACL_FEATURES
specifier|private
specifier|final
specifier|static
name|ReferenceCountMap
argument_list|<
name|AclFeature
argument_list|>
name|UNIQUE_ACL_FEATURES
init|=
operator|new
name|ReferenceCountMap
argument_list|<
name|AclFeature
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * If a default ACL is defined on a parent directory, then copies that default    * ACL to a newly created child file or directory.    *    * @param child INode newly created child    */
DECL|method|copyINodeDefaultAcl (INode child)
specifier|public
specifier|static
name|boolean
name|copyINodeDefaultAcl
parameter_list|(
name|INode
name|child
parameter_list|)
block|{
name|INodeDirectory
name|parent
init|=
name|child
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|AclFeature
name|parentAclFeature
init|=
name|parent
operator|.
name|getAclFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentAclFeature
operator|==
literal|null
operator|||
operator|!
operator|(
name|child
operator|.
name|isFile
argument_list|()
operator|||
name|child
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Split parent's entries into access vs. default.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|getEntriesFromAclFeature
argument_list|(
name|parent
operator|.
name|getAclFeature
argument_list|()
argument_list|)
decl_stmt|;
name|ScopedAclEntries
name|scopedEntries
init|=
operator|new
name|ScopedAclEntries
argument_list|(
name|featureEntries
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|parentDefaultEntries
init|=
name|scopedEntries
operator|.
name|getDefaultEntries
argument_list|()
decl_stmt|;
comment|// The parent may have an access ACL but no default ACL.  If so, exit.
if|if
condition|(
name|parentDefaultEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Pre-allocate list size for access entries to copy from parent.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|parentDefaultEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|FsPermission
name|childPerm
init|=
name|child
operator|.
name|getFsPermission
argument_list|()
decl_stmt|;
comment|// Copy each default ACL entry from parent to new child's access ACL.
name|boolean
name|parentDefaultIsMinimal
init|=
name|AclUtil
operator|.
name|isMinimalAcl
argument_list|(
name|parentDefaultEntries
argument_list|)
decl_stmt|;
for|for
control|(
name|AclEntry
name|entry
range|:
name|parentDefaultEntries
control|)
block|{
name|AclEntryType
name|type
init|=
name|entry
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|AclEntry
operator|.
name|Builder
name|builder
init|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setName
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// The child's initial permission bits are treated as the mode parameter,
comment|// which can filter copied permission values for owner, mask and other.
specifier|final
name|FsAction
name|permission
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|USER
operator|&&
name|name
operator|==
literal|null
condition|)
block|{
name|permission
operator|=
name|entry
operator|.
name|getPermission
argument_list|()
operator|.
name|and
argument_list|(
name|childPerm
operator|.
name|getUserAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|GROUP
operator|&&
name|parentDefaultIsMinimal
condition|)
block|{
comment|// This only happens if the default ACL is a minimal ACL: exactly 3
comment|// entries corresponding to owner, group and other.  In this case,
comment|// filter the group permissions.
name|permission
operator|=
name|entry
operator|.
name|getPermission
argument_list|()
operator|.
name|and
argument_list|(
name|childPerm
operator|.
name|getGroupAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|MASK
condition|)
block|{
comment|// Group bits from mode parameter filter permission of mask entry.
name|permission
operator|=
name|entry
operator|.
name|getPermission
argument_list|()
operator|.
name|and
argument_list|(
name|childPerm
operator|.
name|getGroupAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|OTHER
condition|)
block|{
name|permission
operator|=
name|entry
operator|.
name|getPermission
argument_list|()
operator|.
name|and
argument_list|(
name|childPerm
operator|.
name|getOtherAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|permission
operator|=
name|entry
operator|.
name|getPermission
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|setPermission
argument_list|(
name|permission
argument_list|)
expr_stmt|;
name|accessEntries
operator|.
name|add
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// A new directory also receives a copy of the parent's default ACL.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|defaultEntries
init|=
name|child
operator|.
name|isDirectory
argument_list|()
condition|?
name|parentDefaultEntries
else|:
name|Collections
operator|.
expr|<
name|AclEntry
operator|>
name|emptyList
argument_list|()
decl_stmt|;
specifier|final
name|FsPermission
name|newPerm
decl_stmt|;
if|if
condition|(
operator|!
name|AclUtil
operator|.
name|isMinimalAcl
argument_list|(
name|accessEntries
argument_list|)
operator|||
operator|!
name|defaultEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Save the new ACL to the child.
name|child
operator|.
name|addAclFeature
argument_list|(
name|createAclFeature
argument_list|(
name|accessEntries
argument_list|,
name|defaultEntries
argument_list|)
argument_list|)
expr_stmt|;
name|newPerm
operator|=
name|createFsPermissionForExtendedAcl
argument_list|(
name|accessEntries
argument_list|,
name|childPerm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The child is receiving a minimal ACL.
name|newPerm
operator|=
name|createFsPermissionForMinimalAcl
argument_list|(
name|accessEntries
argument_list|,
name|childPerm
argument_list|)
expr_stmt|;
block|}
name|child
operator|.
name|setPermission
argument_list|(
name|newPerm
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Reads the existing extended ACL entries of an inode.  This method returns    * only the extended ACL entries stored in the AclFeature.  If the inode does    * not have an ACL, then this method returns an empty list.  This method    * supports querying by snapshot ID.    *    * @param inode INode to read    * @param snapshotId int ID of snapshot to read    * @return {@literal List<AclEntry>} containing extended inode ACL entries    */
DECL|method|readINodeAcl (INode inode, int snapshotId)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|readINodeAcl
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
block|{
name|AclFeature
name|f
init|=
name|inode
operator|.
name|getAclFeature
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
return|return
name|getEntriesFromAclFeature
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Reads the existing extended ACL entries of an INodeAttribute object.    *    * @param inodeAttr INode to read    * @return {@code List<AclEntry>} containing extended inode ACL entries    */
DECL|method|readINodeAcl (INodeAttributes inodeAttr)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|readINodeAcl
parameter_list|(
name|INodeAttributes
name|inodeAttr
parameter_list|)
block|{
name|AclFeature
name|f
init|=
name|inodeAttr
operator|.
name|getAclFeature
argument_list|()
decl_stmt|;
return|return
name|getEntriesFromAclFeature
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Build list of AclEntries from the {@link AclFeature}    * @param aclFeature AclFeature    * @return List of entries    */
annotation|@
name|VisibleForTesting
DECL|method|getEntriesFromAclFeature (AclFeature aclFeature)
specifier|static
name|ImmutableList
argument_list|<
name|AclEntry
argument_list|>
name|getEntriesFromAclFeature
parameter_list|(
name|AclFeature
name|aclFeature
parameter_list|)
block|{
if|if
condition|(
name|aclFeature
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|AclEntry
operator|>
name|of
argument_list|()
return|;
block|}
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AclEntry
argument_list|>
name|b
init|=
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AclEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|,
name|entry
init|;
name|pos
operator|<
name|aclFeature
operator|.
name|getEntriesSize
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|entry
operator|=
name|aclFeature
operator|.
name|getEntryAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|b
operator|.
name|add
argument_list|(
name|AclEntryStatusFormat
operator|.
name|toAclEntry
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Reads the existing ACL of an inode.  This method always returns the full    * logical ACL of the inode after reading relevant data from the inode's    * {@link FsPermission} and {@link AclFeature}.  Note that every inode    * logically has an ACL, even if no ACL has been set explicitly.  If the inode    * does not have an extended ACL, then the result is a minimal ACL consising of    * exactly 3 entries that correspond to the owner, group and other permissions.    * This method always reads the inode's current state and does not support    * querying by snapshot ID.  This is because the method is intended to support    * ACL modification APIs, which always apply a delta on top of current state.    *    * @param inode INode to read    * @return {@code List<AclEntry>} containing all logical inode ACL entries    */
DECL|method|readINodeLogicalAcl (INode inode)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|readINodeLogicalAcl
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getFsPermission
argument_list|()
decl_stmt|;
name|AclFeature
name|f
init|=
name|inode
operator|.
name|getAclFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return
name|AclUtil
operator|.
name|getMinimalAcl
argument_list|(
name|perm
argument_list|)
return|;
block|}
specifier|final
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
decl_stmt|;
comment|// Split ACL entries stored in the feature into access vs. default.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|getEntriesFromAclFeature
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|ScopedAclEntries
name|scoped
init|=
operator|new
name|ScopedAclEntries
argument_list|(
name|featureEntries
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
init|=
name|scoped
operator|.
name|getAccessEntries
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|defaultEntries
init|=
name|scoped
operator|.
name|getDefaultEntries
argument_list|()
decl_stmt|;
comment|// Pre-allocate list size for the explicit entries stored in the feature
comment|// plus the 3 implicit entries (owner, group and other) from the permission
comment|// bits.
name|existingAcl
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|featureEntries
operator|.
name|size
argument_list|()
operator|+
literal|3
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|accessEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Add owner entry implied from user permission bits.
name|existingAcl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getUserAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Next add all named user and group entries taken from the feature.
name|existingAcl
operator|.
name|addAll
argument_list|(
name|accessEntries
argument_list|)
expr_stmt|;
comment|// Add mask entry implied from group permission bits.
name|existingAcl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|MASK
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getGroupAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add other entry implied from other permission bits.
name|existingAcl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|OTHER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getOtherAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It's possible that there is a default ACL but no access ACL. In this
comment|// case, add the minimal access ACL implied by the permission bits.
name|existingAcl
operator|.
name|addAll
argument_list|(
name|AclUtil
operator|.
name|getMinimalAcl
argument_list|(
name|perm
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add all default entries after the access entries.
name|existingAcl
operator|.
name|addAll
argument_list|(
name|defaultEntries
argument_list|)
expr_stmt|;
comment|// The above adds entries in the correct order, so no need to sort here.
return|return
name|existingAcl
return|;
block|}
comment|/**    * Updates an inode with a new ACL.  This method takes a full logical ACL and    * stores the entries to the inode's {@link FsPermission} and    * {@link AclFeature}.    *    * @param inode INode to update    * @param newAcl {@code List<AclEntry>} containing new ACL entries    * @param snapshotId int latest snapshot ID of inode    * @throws AclException if the ACL is invalid for the given inode    * @throws QuotaExceededException if quota limit is exceeded    */
DECL|method|updateINodeAcl (INode inode, List<AclEntry> newAcl, int snapshotId)
specifier|public
specifier|static
name|void
name|updateINodeAcl
parameter_list|(
name|INode
name|inode
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|AclException
throws|,
name|QuotaExceededException
block|{
assert|assert
name|newAcl
operator|.
name|size
argument_list|()
operator|>=
literal|3
assert|;
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getFsPermission
argument_list|()
decl_stmt|;
specifier|final
name|FsPermission
name|newPerm
decl_stmt|;
if|if
condition|(
operator|!
name|AclUtil
operator|.
name|isMinimalAcl
argument_list|(
name|newAcl
argument_list|)
condition|)
block|{
comment|// This is an extended ACL.  Split entries into access vs. default.
name|ScopedAclEntries
name|scoped
init|=
operator|new
name|ScopedAclEntries
argument_list|(
name|newAcl
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
init|=
name|scoped
operator|.
name|getAccessEntries
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|defaultEntries
init|=
name|scoped
operator|.
name|getDefaultEntries
argument_list|()
decl_stmt|;
comment|// Only directories may have a default ACL.
if|if
condition|(
operator|!
name|defaultEntries
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AclException
argument_list|(
literal|"Invalid ACL: only directories may have a default ACL. "
operator|+
literal|"Path: "
operator|+
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|)
throw|;
block|}
comment|// Attach entries to the feature.
if|if
condition|(
name|inode
operator|.
name|getAclFeature
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|inode
operator|.
name|removeAclFeature
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
name|inode
operator|.
name|addAclFeature
argument_list|(
name|createAclFeature
argument_list|(
name|accessEntries
argument_list|,
name|defaultEntries
argument_list|)
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
name|newPerm
operator|=
name|createFsPermissionForExtendedAcl
argument_list|(
name|accessEntries
argument_list|,
name|perm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This is a minimal ACL.  Remove the ACL feature if it previously had one.
if|if
condition|(
name|inode
operator|.
name|getAclFeature
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|inode
operator|.
name|removeAclFeature
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
name|newPerm
operator|=
name|createFsPermissionForMinimalAcl
argument_list|(
name|newAcl
argument_list|,
name|perm
argument_list|)
expr_stmt|;
block|}
name|inode
operator|.
name|setPermission
argument_list|(
name|newPerm
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
block|}
comment|/**    * There is no reason to instantiate this class.    */
DECL|method|AclStorage ()
specifier|private
name|AclStorage
parameter_list|()
block|{   }
comment|/**    * Creates an AclFeature from the given ACL entries.    *    * @param accessEntries {@code List<AclEntry>} access ACL entries    * @param defaultEntries {@code List<AclEntry>} default ACL entries    * @return AclFeature containing the required ACL entries    */
DECL|method|createAclFeature (List<AclEntry> accessEntries, List<AclEntry> defaultEntries)
specifier|private
specifier|static
name|AclFeature
name|createAclFeature
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|defaultEntries
parameter_list|)
block|{
comment|// Pre-allocate list size for the explicit entries stored in the feature,
comment|// which is all entries minus the 3 entries implicitly stored in the
comment|// permission bits.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
operator|(
name|accessEntries
operator|.
name|size
argument_list|()
operator|-
literal|3
operator|)
operator|+
name|defaultEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// For the access ACL, the feature only needs to hold the named user and
comment|// group entries.  For a correctly sorted ACL, these will be in a
comment|// predictable range.
if|if
condition|(
operator|!
name|AclUtil
operator|.
name|isMinimalAcl
argument_list|(
name|accessEntries
argument_list|)
condition|)
block|{
name|featureEntries
operator|.
name|addAll
argument_list|(
name|accessEntries
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|accessEntries
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add all default entries to the feature.
name|featureEntries
operator|.
name|addAll
argument_list|(
name|defaultEntries
argument_list|)
expr_stmt|;
return|return
operator|new
name|AclFeature
argument_list|(
name|AclEntryStatusFormat
operator|.
name|toInt
argument_list|(
name|featureEntries
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates the new FsPermission for an inode that is receiving an extended    * ACL, based on its access ACL entries.  For a correctly sorted ACL, the    * first entry is the owner and the last 2 entries are the mask and other    * entries respectively.  Also preserve sticky bit and toggle ACL bit on.    * Note that this method intentionally copies the permissions of the mask    * entry into the FsPermission group permissions.  This is consistent with the    * POSIX ACLs model, which presents the mask as the permissions of the group    * class.    *    * @param accessEntries {@code List<AclEntry>} access ACL entries    * @param existingPerm FsPermission existing permissions    * @return FsPermission new permissions    */
DECL|method|createFsPermissionForExtendedAcl ( List<AclEntry> accessEntries, FsPermission existingPerm)
specifier|private
specifier|static
name|FsPermission
name|createFsPermissionForExtendedAcl
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
parameter_list|,
name|FsPermission
name|existingPerm
parameter_list|)
block|{
return|return
operator|new
name|FsPermission
argument_list|(
name|accessEntries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|accessEntries
operator|.
name|get
argument_list|(
name|accessEntries
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|accessEntries
operator|.
name|get
argument_list|(
name|accessEntries
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|existingPerm
operator|.
name|getStickyBit
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates the new FsPermission for an inode that is receiving a minimal ACL,    * based on its access ACL entries.  For a correctly sorted ACL, the owner,    * group and other permissions are in order.  Also preserve sticky bit and    * toggle ACL bit off.    *    * @param accessEntries {@code List<AclEntry>} access ACL entries    * @param existingPerm FsPermission existing permissions    * @return FsPermission new permissions    */
DECL|method|createFsPermissionForMinimalAcl ( List<AclEntry> accessEntries, FsPermission existingPerm)
specifier|private
specifier|static
name|FsPermission
name|createFsPermissionForMinimalAcl
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessEntries
parameter_list|,
name|FsPermission
name|existingPerm
parameter_list|)
block|{
return|return
operator|new
name|FsPermission
argument_list|(
name|accessEntries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|accessEntries
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|accessEntries
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|existingPerm
operator|.
name|getStickyBit
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getUniqueAclFeatures ()
specifier|public
specifier|static
name|ReferenceCountMap
argument_list|<
name|AclFeature
argument_list|>
name|getUniqueAclFeatures
parameter_list|()
block|{
return|return
name|UNIQUE_ACL_FEATURES
return|;
block|}
comment|/**    * Add reference for the said AclFeature    *     * @param aclFeature    * @return Referenced AclFeature    */
DECL|method|addAclFeature (AclFeature aclFeature)
specifier|public
specifier|static
name|AclFeature
name|addAclFeature
parameter_list|(
name|AclFeature
name|aclFeature
parameter_list|)
block|{
return|return
name|UNIQUE_ACL_FEATURES
operator|.
name|put
argument_list|(
name|aclFeature
argument_list|)
return|;
block|}
comment|/**    * Remove reference to the AclFeature    *     * @param aclFeature    */
DECL|method|removeAclFeature (AclFeature aclFeature)
specifier|public
specifier|static
name|void
name|removeAclFeature
parameter_list|(
name|AclFeature
name|aclFeature
parameter_list|)
block|{
name|UNIQUE_ACL_FEATURES
operator|.
name|remove
argument_list|(
name|aclFeature
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

