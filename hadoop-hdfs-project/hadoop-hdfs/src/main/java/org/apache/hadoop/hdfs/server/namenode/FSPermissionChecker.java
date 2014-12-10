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
name|Arrays
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
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|util
operator|.
name|ReadOnlyList
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
name|security
operator|.
name|AccessControlException
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**   * Class that helps in checking file system permission.  * The state of this class need not be synchronized as it has data structures that  * are read-only.  *   * Some of the helper methods are gaurded by {@link FSNamesystem#readLock()}.  */
end_comment

begin_class
DECL|class|FSPermissionChecker
class|class
name|FSPermissionChecker
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** @return a string for throwing {@link AccessControlException} */
DECL|method|toAccessControlString (INode inode, int snapshotId, FsAction access, FsPermission mode)
specifier|private
name|String
name|toAccessControlString
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsPermission
name|mode
parameter_list|)
block|{
return|return
name|toAccessControlString
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** @return a string for throwing {@link AccessControlException} */
DECL|method|toAccessControlString (INode inode, int snapshotId, FsAction access, FsPermission mode, boolean deniedFromAcl)
specifier|private
name|String
name|toAccessControlString
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsPermission
name|mode
parameter_list|,
name|boolean
name|deniedFromAcl
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Permission denied: "
argument_list|)
operator|.
name|append
argument_list|(
literal|"user="
argument_list|)
operator|.
name|append
argument_list|(
name|user
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"access="
argument_list|)
operator|.
name|append
argument_list|(
name|access
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"inode=\""
argument_list|)
operator|.
name|append
argument_list|(
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":"
argument_list|)
operator|.
name|append
argument_list|(
name|inode
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|inode
operator|.
name|getGroupName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|inode
operator|.
name|isDirectory
argument_list|()
condition|?
literal|'d'
else|:
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|mode
argument_list|)
decl_stmt|;
if|if
condition|(
name|deniedFromAcl
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"+"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
comment|/** A set with group namess. Not synchronized since it is unmodifiable */
DECL|field|groups
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
DECL|field|isSuper
specifier|private
specifier|final
name|boolean
name|isSuper
decl_stmt|;
DECL|method|FSPermissionChecker (String fsOwner, String supergroup, UserGroupInformation callerUgi)
name|FSPermissionChecker
parameter_list|(
name|String
name|fsOwner
parameter_list|,
name|String
name|supergroup
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|callerUgi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|groups
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|user
operator|=
name|callerUgi
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|isSuper
operator|=
name|user
operator|.
name|equals
argument_list|(
name|fsOwner
argument_list|)
operator|||
name|groups
operator|.
name|contains
argument_list|(
name|supergroup
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if the callers group contains the required values.    * @param group group to check    */
DECL|method|containsGroup (String group)
specifier|public
name|boolean
name|containsGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
return|return
name|groups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|isSuperUser ()
specifier|public
name|boolean
name|isSuperUser
parameter_list|()
block|{
return|return
name|isSuper
return|;
block|}
comment|/**    * Verify if the caller has the required permission. This will result into     * an exception if the caller is not allowed to access the resource.    */
DECL|method|checkSuperuserPrivilege ()
specifier|public
name|void
name|checkSuperuserPrivilege
parameter_list|()
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
name|isSuper
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Access denied for user "
operator|+
name|user
operator|+
literal|". Superuser privilege is required"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check whether current user have permissions to access the path.    * Traverse is always checked.    *    * Parent path means the parent directory for the path.    * Ancestor path means the last (the closest) existing ancestor directory    * of the path.    * Note that if the parent path exists,    * then the parent path and the ancestor path are the same.    *    * For example, suppose the path is "/foo/bar/baz".    * No matter baz is a file or a directory,    * the parent path is "/foo/bar".    * If bar exists, then the ancestor path is also "/foo/bar".    * If bar does not exist and foo exists,    * then the ancestor path is "/foo".    * Further, if both foo and bar do not exist,    * then the ancestor path is "/".    *    * @param doCheckOwner Require user to be the owner of the path?    * @param ancestorAccess The access required by the ancestor of the path.    * @param parentAccess The access required by the parent of the path.    * @param access The access required by the path.    * @param subAccess If path is a directory,    * it is the access required of the path and all the sub-directories.    * If path is not a directory, there is no effect.    * @param ignoreEmptyDir Ignore permission checking for empty directory?    * @throws AccessControlException    *     * Guarded by {@link FSNamesystem#readLock()}    * Caller of this method must hold that lock.    */
DECL|method|checkPermission (INodesInPath inodesInPath, boolean doCheckOwner, FsAction ancestorAccess, FsAction parentAccess, FsAction access, FsAction subAccess, boolean ignoreEmptyDir)
name|void
name|checkPermission
parameter_list|(
name|INodesInPath
name|inodesInPath
parameter_list|,
name|boolean
name|doCheckOwner
parameter_list|,
name|FsAction
name|ancestorAccess
parameter_list|,
name|FsAction
name|parentAccess
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsAction
name|subAccess
parameter_list|,
name|boolean
name|ignoreEmptyDir
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ACCESS CHECK: "
operator|+
name|this
operator|+
literal|", doCheckOwner="
operator|+
name|doCheckOwner
operator|+
literal|", ancestorAccess="
operator|+
name|ancestorAccess
operator|+
literal|", parentAccess="
operator|+
name|parentAccess
operator|+
literal|", access="
operator|+
name|access
operator|+
literal|", subAccess="
operator|+
name|subAccess
operator|+
literal|", ignoreEmptyDir="
operator|+
name|ignoreEmptyDir
argument_list|)
expr_stmt|;
block|}
comment|// check if (parentAccess != null)&& file exists, then check sb
comment|// If resolveLink, the check is performed on the link target.
specifier|final
name|int
name|snapshotId
init|=
name|inodesInPath
operator|.
name|getPathSnapshotId
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|inodesInPath
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|INode
name|last
init|=
name|length
operator|>
literal|0
condition|?
name|inodesInPath
operator|.
name|getLastINode
argument_list|()
else|:
literal|null
decl_stmt|;
specifier|final
name|INode
name|parent
init|=
name|length
operator|>
literal|1
condition|?
name|inodesInPath
operator|.
name|getINode
argument_list|(
operator|-
literal|2
argument_list|)
else|:
literal|null
decl_stmt|;
name|checkTraverse
argument_list|(
name|inodesInPath
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentAccess
operator|!=
literal|null
operator|&&
name|parentAccess
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|WRITE
argument_list|)
operator|&&
name|length
operator|>
literal|1
operator|&&
name|last
operator|!=
literal|null
condition|)
block|{
name|checkStickyBit
argument_list|(
name|parent
argument_list|,
name|last
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ancestorAccess
operator|!=
literal|null
operator|&&
name|length
operator|>
literal|1
condition|)
block|{
name|List
argument_list|<
name|INode
argument_list|>
name|inodes
init|=
name|inodesInPath
operator|.
name|getReadOnlyINodes
argument_list|()
decl_stmt|;
name|INode
name|ancestor
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|inodes
operator|.
name|size
argument_list|()
operator|-
literal|2
init|;
name|i
operator|>=
literal|0
operator|&&
operator|(
name|ancestor
operator|=
name|inodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|==
literal|null
condition|;
name|i
operator|--
control|)
empty_stmt|;
name|check
argument_list|(
name|ancestor
argument_list|,
name|snapshotId
argument_list|,
name|ancestorAccess
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parentAccess
operator|!=
literal|null
operator|&&
name|length
operator|>
literal|1
operator|&&
name|parent
operator|!=
literal|null
condition|)
block|{
name|check
argument_list|(
name|parent
argument_list|,
name|snapshotId
argument_list|,
name|parentAccess
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|access
operator|!=
literal|null
condition|)
block|{
name|check
argument_list|(
name|last
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subAccess
operator|!=
literal|null
condition|)
block|{
name|checkSubAccess
argument_list|(
name|last
argument_list|,
name|snapshotId
argument_list|,
name|subAccess
argument_list|,
name|ignoreEmptyDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doCheckOwner
condition|)
block|{
name|checkOwner
argument_list|(
name|last
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Guarded by {@link FSNamesystem#readLock()} */
DECL|method|checkOwner (INode inode, int snapshotId )
specifier|private
name|void
name|checkOwner
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|inode
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|inode
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Permission denied. user="
operator|+
name|user
operator|+
literal|" is not the owner of inode="
operator|+
name|inode
argument_list|)
throw|;
block|}
comment|/** Guarded by {@link FSNamesystem#readLock()} */
DECL|method|checkTraverse (INodesInPath iip, int snapshotId)
specifier|private
name|void
name|checkTraverse
parameter_list|(
name|INodesInPath
name|iip
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|List
argument_list|<
name|INode
argument_list|>
name|inodes
init|=
name|iip
operator|.
name|getReadOnlyINodes
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
name|inodes
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|INode
name|inode
init|=
name|inodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|check
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|FsAction
operator|.
name|EXECUTE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Guarded by {@link FSNamesystem#readLock()} */
DECL|method|checkSubAccess (INode inode, int snapshotId, FsAction access, boolean ignoreEmptyDir)
specifier|private
name|void
name|checkSubAccess
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|boolean
name|ignoreEmptyDir
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|inode
operator|==
literal|null
operator|||
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return;
block|}
name|Stack
argument_list|<
name|INodeDirectory
argument_list|>
name|directories
init|=
operator|new
name|Stack
argument_list|<
name|INodeDirectory
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|directories
operator|.
name|push
argument_list|(
name|inode
operator|.
name|asDirectory
argument_list|()
argument_list|)
init|;
operator|!
name|directories
operator|.
name|isEmpty
argument_list|()
condition|;
control|)
block|{
name|INodeDirectory
name|d
init|=
name|directories
operator|.
name|pop
argument_list|()
decl_stmt|;
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|cList
init|=
name|d
operator|.
name|getChildrenList
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|cList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|ignoreEmptyDir
operator|)
condition|)
block|{
name|check
argument_list|(
name|d
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|INode
name|child
range|:
name|cList
control|)
block|{
if|if
condition|(
name|child
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|directories
operator|.
name|push
argument_list|(
name|child
operator|.
name|asDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Guarded by {@link FSNamesystem#readLock()} */
DECL|method|check (INode inode, int snapshotId, FsAction access)
specifier|private
name|void
name|check
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|FsPermission
name|mode
init|=
name|inode
operator|.
name|getFsPermission
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
name|AclFeature
name|aclFeature
init|=
name|inode
operator|.
name|getAclFeature
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclFeature
operator|!=
literal|null
condition|)
block|{
comment|// It's possible that the inode has a default ACL but no access ACL.
name|int
name|firstEntry
init|=
name|aclFeature
operator|.
name|getEntryAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|AclEntryStatusFormat
operator|.
name|getScope
argument_list|(
name|firstEntry
argument_list|)
operator|==
name|AclEntryScope
operator|.
name|ACCESS
condition|)
block|{
name|checkAccessAcl
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|,
name|mode
argument_list|,
name|aclFeature
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|checkFsPermission
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFsPermission (INode inode, int snapshotId, FsAction access, FsPermission mode)
specifier|private
name|void
name|checkFsPermission
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsPermission
name|mode
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|inode
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
condition|)
block|{
comment|//user class
if|if
condition|(
name|mode
operator|.
name|getUserAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|inode
operator|.
name|getGroupName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
condition|)
block|{
comment|//group class
if|if
condition|(
name|mode
operator|.
name|getGroupAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
comment|//other class
if|if
condition|(
name|mode
operator|.
name|getOtherAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|toAccessControlString
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|,
name|mode
argument_list|)
argument_list|)
throw|;
block|}
comment|/**    * Checks requested access against an Access Control List.  This method relies    * on finding the ACL data in the relevant portions of {@link FsPermission} and    * {@link AclFeature} as implemented in the logic of {@link AclStorage}.  This    * method also relies on receiving the ACL entries in sorted order.  This is    * assumed to be true, because the ACL modification methods in    * {@link AclTransformation} sort the resulting entries.    *    * More specifically, this method depends on these invariants in an ACL:    * - The list must be sorted.    * - Each entry in the list must be unique by scope + type + name.    * - There is exactly one each of the unnamed user/group/other entries.    * - The mask entry must not have a name.    * - The other entry must not have a name.    * - Default entries may be present, but they are ignored during enforcement.    *    * @param inode INode accessed inode    * @param snapshotId int snapshot ID    * @param access FsAction requested permission    * @param mode FsPermission mode from inode    * @param aclFeature AclFeature of inode    * @throws AccessControlException if the ACL denies permission    */
DECL|method|checkAccessAcl (INode inode, int snapshotId, FsAction access, FsPermission mode, AclFeature aclFeature)
specifier|private
name|void
name|checkAccessAcl
parameter_list|(
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsPermission
name|mode
parameter_list|,
name|AclFeature
name|aclFeature
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|boolean
name|foundMatch
init|=
literal|false
decl_stmt|;
comment|// Use owner entry from permission bits if user is owner.
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|inode
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|mode
operator|.
name|getUserAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
name|foundMatch
operator|=
literal|true
expr_stmt|;
block|}
comment|// Check named user and group entries if user was not denied by owner entry.
if|if
condition|(
operator|!
name|foundMatch
condition|)
block|{
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
if|if
condition|(
name|AclEntryStatusFormat
operator|.
name|getScope
argument_list|(
name|entry
argument_list|)
operator|==
name|AclEntryScope
operator|.
name|DEFAULT
condition|)
block|{
break|break;
block|}
name|AclEntryType
name|type
init|=
name|AclEntryStatusFormat
operator|.
name|getType
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|AclEntryStatusFormat
operator|.
name|getName
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|USER
condition|)
block|{
comment|// Use named user entry with mask from permission bits applied if user
comment|// matches name.
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|FsAction
name|masked
init|=
name|AclEntryStatusFormat
operator|.
name|getPermission
argument_list|(
name|entry
argument_list|)
operator|.
name|and
argument_list|(
name|mode
operator|.
name|getGroupAction
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|masked
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
name|foundMatch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|AclEntryType
operator|.
name|GROUP
condition|)
block|{
comment|// Use group entry (unnamed or named) with mask from permission bits
comment|// applied if user is a member and entry grants access.  If user is a
comment|// member of multiple groups that have entries that grant access, then
comment|// it doesn't matter which is chosen, so exit early after first match.
name|String
name|group
init|=
name|name
operator|==
literal|null
condition|?
name|inode
operator|.
name|getGroupName
argument_list|(
name|snapshotId
argument_list|)
else|:
name|name
decl_stmt|;
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|FsAction
name|masked
init|=
name|AclEntryStatusFormat
operator|.
name|getPermission
argument_list|(
name|entry
argument_list|)
operator|.
name|and
argument_list|(
name|mode
operator|.
name|getGroupAction
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|masked
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
name|foundMatch
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Use other entry if user was not denied by an earlier match.
if|if
condition|(
operator|!
name|foundMatch
operator|&&
name|mode
operator|.
name|getOtherAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|toAccessControlString
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|,
name|mode
argument_list|,
literal|true
argument_list|)
argument_list|)
throw|;
block|}
comment|/** Guarded by {@link FSNamesystem#readLock()} */
DECL|method|checkStickyBit (INode parent, INode inode, int snapshotId )
specifier|private
name|void
name|checkStickyBit
parameter_list|(
name|INode
name|parent
parameter_list|,
name|INode
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
name|parent
operator|.
name|getFsPermission
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getStickyBit
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// If this user is the directory owner, return
if|if
condition|(
name|parent
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// if this user is the file owner, return
if|if
condition|(
name|inode
operator|.
name|getUserName
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Permission denied by sticky bit setting:"
operator|+
literal|" user="
operator|+
name|user
operator|+
literal|", inode="
operator|+
name|inode
argument_list|)
throw|;
block|}
comment|/**    * Whether a cache pool can be accessed by the current context    *    * @param pool CachePool being accessed    * @param access type of action being performed on the cache pool    * @throws AccessControlException if pool cannot be accessed    */
DECL|method|checkPermission (CachePool pool, FsAction access)
specifier|public
name|void
name|checkPermission
parameter_list|(
name|CachePool
name|pool
parameter_list|,
name|FsAction
name|access
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|FsPermission
name|mode
init|=
name|pool
operator|.
name|getMode
argument_list|()
decl_stmt|;
if|if
condition|(
name|isSuperUser
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|pool
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|&&
name|mode
operator|.
name|getUserAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|pool
operator|.
name|getGroupName
argument_list|()
argument_list|)
operator|&&
name|mode
operator|.
name|getGroupAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|mode
operator|.
name|getOtherAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Permission denied while accessing pool "
operator|+
name|pool
operator|.
name|getPoolName
argument_list|()
operator|+
literal|": user "
operator|+
name|user
operator|+
literal|" does not have "
operator|+
name|access
operator|.
name|toString
argument_list|()
operator|+
literal|" permissions."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

