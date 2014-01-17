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

begin_comment
comment|/**  * AclStorage contains utility methods that define how ACL data is stored in the  * namespace.  *  * If an inode has an ACL, then the ACL bit is set in the inode's  * {@link FsPermission} and the inode also contains an {@link AclFeature}.  For  * the access ACL, the owner and other entries are identical to the owner and  * other bits stored in FsPermission, so we reuse those.  The access mask entry  * is stored into the group permission bits of FsPermission.  This is consistent  * with other file systems' implementations of ACLs and eliminates the need for  * special handling in various parts of the codebase.  For example, if a user  * calls chmod to change group permission bits on a file with an ACL, then the  * expected behavior is to change the ACL's mask entry.  By saving the mask entry  * into the group permission bits, chmod continues to work correctly without  * special handling.  All remaining access entries (named users and named groups)  * are stored as explicit {@link AclEntry} instances in a list inside the  * AclFeature.  Additionally, all default entries are stored in the AclFeature.  *  * The methods in this class encapsulate these rules for reading or writing the  * ACL entries to the appropriate location.  *  * The methods in this class assume that input ACL entry lists have already been  * validated and sorted according to the rules enforced by  * {@link AclTransformation}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AclStorage
specifier|final
class|class
name|AclStorage
block|{
comment|/**    * Reads the existing extended ACL entries of an inode.  This method returns    * only the extended ACL entries stored in the AclFeature.  If the inode does    * not have an ACL, then this method returns an empty list.    *    * @param inode INodeWithAdditionalFields to read    * @param snapshotId int latest snapshot ID of inode    * @return List<AclEntry> containing extended inode ACL entries    */
DECL|method|readINodeAcl (INodeWithAdditionalFields inode, int snapshotId)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|readINodeAcl
parameter_list|(
name|INodeWithAdditionalFields
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
block|{
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getPermissionStatus
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
return|return
name|inode
operator|.
name|getAclFeature
argument_list|()
operator|.
name|getEntries
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
comment|/**    * Reads the existing ACL of an inode.  This method always returns the full    * logical ACL of the inode after reading relevant data from the inode's    * {@link FsPermission} and {@link AclFeature}.  Note that every inode    * logically has an ACL, even if no ACL has been set explicitly.  If the inode    * does not have an extended ACL, then the result is a minimal ACL consising of    * exactly 3 entries that correspond to the owner, group and other permissions.    *    * @param inode INodeWithAdditionalFields to read    * @param snapshotId int latest snapshot ID of inode    * @return List<AclEntry> containing all logical inode ACL entries    */
DECL|method|readINodeLogicalAcl ( INodeWithAdditionalFields inode, int snapshotId)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|readINodeLogicalAcl
parameter_list|(
name|INodeWithAdditionalFields
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
decl_stmt|;
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getPermissionStatus
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
comment|// Split ACL entries stored in the feature into access vs. default.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|inode
operator|.
name|getAclFeature
argument_list|()
operator|.
name|getEntries
argument_list|()
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
comment|// It's possible that there is a default ACL but no access ACL.  In this
comment|// case, add the minimal access ACL implied by the permission bits.
name|existingAcl
operator|.
name|addAll
argument_list|(
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
block|}
else|else
block|{
comment|// If the inode doesn't have an extended ACL, then return a minimal ACL.
name|existingAcl
operator|=
name|getMinimalAcl
argument_list|(
name|perm
argument_list|)
expr_stmt|;
block|}
comment|// The above adds entries in the correct order, so no need to sort here.
return|return
name|existingAcl
return|;
block|}
comment|/**    * Completely removes the ACL from an inode.    *    * @param inode INodeWithAdditionalFields to update    * @param snapshotId int latest snapshot ID of inode    * @throws QuotaExceededException if quota limit is exceeded    */
DECL|method|removeINodeAcl (INodeWithAdditionalFields inode, int snapshotId)
specifier|public
specifier|static
name|void
name|removeINodeAcl
parameter_list|(
name|INodeWithAdditionalFields
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getPermissionStatus
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
comment|// Restore group permissions from the feature's entry to permission bits,
comment|// overwriting the mask, which is not part of a minimal ACL.
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|inode
operator|.
name|getAclFeature
argument_list|()
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|AclEntry
name|groupEntryKey
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
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|int
name|groupEntryIndex
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|featureEntries
argument_list|,
name|groupEntryKey
argument_list|,
name|AclTransformation
operator|.
name|ACL_ENTRY_COMPARATOR
argument_list|)
decl_stmt|;
assert|assert
name|groupEntryIndex
operator|>=
literal|0
assert|;
comment|// Remove the feature and turn off the ACL bit.
name|inode
operator|.
name|removeAclFeature
argument_list|()
expr_stmt|;
name|FsPermission
name|newPerm
init|=
operator|new
name|FsPermission
argument_list|(
name|perm
operator|.
name|getUserAction
argument_list|()
argument_list|,
name|featureEntries
operator|.
name|get
argument_list|(
name|groupEntryIndex
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|perm
operator|.
name|getOtherAction
argument_list|()
argument_list|,
name|perm
operator|.
name|getStickyBit
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
block|}
comment|/**    * Updates an inode with a new ACL.  This method takes a full logical ACL and    * stores the entries to the inode's {@link FsPermission} and    * {@link AclFeature}.    *    * @param inode INodeWithAdditionalFields to update    * @param newAcl List<AclEntry> containing new ACL entries    * @param snapshotId int latest snapshot ID of inode    * @throws AclException if the ACL is invalid for the given inode    * @throws QuotaExceededException if quota limit is exceeded    */
DECL|method|updateINodeAcl (INodeWithAdditionalFields inode, List<AclEntry> newAcl, int snapshotId)
specifier|public
specifier|static
name|void
name|updateINodeAcl
parameter_list|(
name|INodeWithAdditionalFields
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
name|getPermissionStatus
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
specifier|final
name|FsPermission
name|newPerm
decl_stmt|;
if|if
condition|(
name|newAcl
operator|.
name|size
argument_list|()
operator|>
literal|3
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
literal|"Invalid ACL: only directories may have a default ACL."
argument_list|)
throw|;
block|}
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
comment|// Calculate new permission bits.  For a correctly sorted ACL, the first
comment|// entry is the owner and the last 2 entries are the mask and other entries
comment|// respectively.  Also preserve sticky bit and toggle ACL bit on.
name|newPerm
operator|=
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
name|perm
operator|.
name|getStickyBit
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// For the access ACL, the feature only needs to hold the named user and
comment|// group entries.  For a correctly sorted ACL, these will be in a
comment|// predictable range.
if|if
condition|(
name|accessEntries
operator|.
name|size
argument_list|()
operator|>
literal|3
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
comment|// Attach entries to the feature, creating a new feature if needed.
name|AclFeature
name|aclFeature
init|=
name|inode
operator|.
name|getAclFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|aclFeature
operator|==
literal|null
condition|)
block|{
name|aclFeature
operator|=
operator|new
name|AclFeature
argument_list|()
expr_stmt|;
name|inode
operator|.
name|addAclFeature
argument_list|(
name|aclFeature
argument_list|)
expr_stmt|;
block|}
name|aclFeature
operator|.
name|setEntries
argument_list|(
name|featureEntries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This is a minimal ACL.  Remove the ACL feature if it previously had one.
if|if
condition|(
name|perm
operator|.
name|getAclBit
argument_list|()
condition|)
block|{
name|inode
operator|.
name|removeAclFeature
argument_list|()
expr_stmt|;
block|}
comment|// Calculate new permission bits.  For a correctly sorted ACL, the owner,
comment|// group and other permissions are in order.  Also preserve sticky bit and
comment|// toggle ACL bit off.
name|newPerm
operator|=
operator|new
name|FsPermission
argument_list|(
name|newAcl
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|newAcl
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|newAcl
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|,
name|perm
operator|.
name|getStickyBit
argument_list|()
argument_list|,
literal|false
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
comment|/**    * Translates the given permission bits to the equivalent minimal ACL.    *    * @param perm FsPermission to translate    * @return List<AclEntry> containing exactly 3 entries representing the owner,    *   group and other permissions    */
DECL|method|getMinimalAcl (FsPermission perm)
specifier|private
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getMinimalAcl
parameter_list|(
name|FsPermission
name|perm
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
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
argument_list|,
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
name|GROUP
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
argument_list|,
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
return|;
block|}
block|}
end_class

end_unit

