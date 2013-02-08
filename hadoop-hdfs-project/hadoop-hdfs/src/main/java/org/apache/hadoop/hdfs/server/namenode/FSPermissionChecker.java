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
name|IOException
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
name|UnresolvedLinkException
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
name|server
operator|.
name|namenode
operator|.
name|INodeDirectory
operator|.
name|INodesInPath
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
comment|/** Perform permission checking in {@link FSNamesystem}. */
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
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|user
specifier|public
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isSuper
specifier|public
specifier|final
name|boolean
name|isSuper
decl_stmt|;
DECL|method|FSPermissionChecker (String fsOwner, String supergroup )
name|FSPermissionChecker
parameter_list|(
name|String
name|fsOwner
parameter_list|,
name|String
name|supergroup
parameter_list|)
throws|throws
name|AccessControlException
block|{
try|try
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|groups
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|user
operator|=
name|ugi
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
comment|/**    * Verify if the caller has the required permission. This will result into     * an exception if the caller is not allowed to access the resource.    * @param owner owner of the system    * @param supergroup supergroup of the system    */
DECL|method|checkSuperuserPrivilege (UserGroupInformation owner, String supergroup)
specifier|public
specifier|static
name|void
name|checkSuperuserPrivilege
parameter_list|(
name|UserGroupInformation
name|owner
parameter_list|,
name|String
name|supergroup
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|FSPermissionChecker
name|checker
init|=
operator|new
name|FSPermissionChecker
argument_list|(
name|owner
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|supergroup
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checker
operator|.
name|isSuper
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Access denied for user "
operator|+
name|checker
operator|.
name|user
operator|+
literal|". Superuser privilege is required"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check whether current user have permissions to access the path.    * Traverse is always checked.    *    * Parent path means the parent directory for the path.    * Ancestor path means the last (the closest) existing ancestor directory    * of the path.    * Note that if the parent path exists,    * then the parent path and the ancestor path are the same.    *    * For example, suppose the path is "/foo/bar/baz".    * No matter baz is a file or a directory,    * the parent path is "/foo/bar".    * If bar exists, then the ancestor path is also "/foo/bar".    * If bar does not exist and foo exists,    * then the ancestor path is "/foo".    * Further, if both foo and bar do not exist,    * then the ancestor path is "/".    *    * @param doCheckOwner Require user to be the owner of the path?    * @param ancestorAccess The access required by the ancestor of the path.    * @param parentAccess The access required by the parent of the path.    * @param access The access required by the path.    * @param subAccess If path is a directory,    * it is the access required of the path and all the sub-directories.    * If path is not a directory, there is no effect.    * @throws AccessControlException    * @throws UnresolvedLinkException    */
DECL|method|checkPermission (String path, INodeDirectory root, boolean doCheckOwner, FsAction ancestorAccess, FsAction parentAccess, FsAction access, FsAction subAccess)
name|void
name|checkPermission
parameter_list|(
name|String
name|path
parameter_list|,
name|INodeDirectory
name|root
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
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|UnresolvedLinkException
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
argument_list|)
expr_stmt|;
block|}
comment|// check if (parentAccess != null)&& file exists, then check sb
comment|// Resolve symlinks, the check is performed on the link target.
specifier|final
name|INodesInPath
name|inodesInPath
init|=
name|root
operator|.
name|getINodesInPath
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Snapshot
name|snapshot
init|=
name|inodesInPath
operator|.
name|getPathSnapshot
argument_list|()
decl_stmt|;
specifier|final
name|INode
index|[]
name|inodes
init|=
name|inodesInPath
operator|.
name|getINodes
argument_list|()
decl_stmt|;
name|int
name|ancestorIndex
init|=
name|inodes
operator|.
name|length
operator|-
literal|2
decl_stmt|;
for|for
control|(
init|;
name|ancestorIndex
operator|>=
literal|0
operator|&&
name|inodes
index|[
name|ancestorIndex
index|]
operator|==
literal|null
condition|;
name|ancestorIndex
operator|--
control|)
empty_stmt|;
name|checkTraverse
argument_list|(
name|inodes
argument_list|,
name|ancestorIndex
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
specifier|final
name|INode
name|last
init|=
name|inodes
index|[
name|inodes
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
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
name|inodes
operator|.
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
name|inodes
index|[
name|inodes
operator|.
name|length
operator|-
literal|2
index|]
argument_list|,
name|last
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ancestorAccess
operator|!=
literal|null
operator|&&
name|inodes
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|check
argument_list|(
name|inodes
argument_list|,
name|ancestorIndex
argument_list|,
name|snapshot
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
name|inodes
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|check
argument_list|(
name|inodes
argument_list|,
name|inodes
operator|.
name|length
operator|-
literal|2
argument_list|,
name|snapshot
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
name|snapshot
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
name|snapshot
argument_list|,
name|subAccess
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
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkOwner (INode inode, Snapshot snapshot )
specifier|private
name|void
name|checkOwner
parameter_list|(
name|INode
name|inode
parameter_list|,
name|Snapshot
name|snapshot
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
name|snapshot
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
literal|"Permission denied"
argument_list|)
throw|;
block|}
DECL|method|checkTraverse (INode[] inodes, int last, Snapshot snapshot )
specifier|private
name|void
name|checkTraverse
parameter_list|(
name|INode
index|[]
name|inodes
parameter_list|,
name|int
name|last
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|)
throws|throws
name|AccessControlException
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|last
condition|;
name|j
operator|++
control|)
block|{
name|check
argument_list|(
name|inodes
index|[
name|j
index|]
argument_list|,
name|snapshot
argument_list|,
name|FsAction
operator|.
name|EXECUTE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkSubAccess (INode inode, Snapshot snapshot, FsAction access )
specifier|private
name|void
name|checkSubAccess
parameter_list|(
name|INode
name|inode
parameter_list|,
name|Snapshot
name|snapshot
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
operator|(
name|INodeDirectory
operator|)
name|inode
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
name|check
argument_list|(
name|d
argument_list|,
name|snapshot
argument_list|,
name|access
argument_list|)
expr_stmt|;
for|for
control|(
name|INode
name|child
range|:
name|d
operator|.
name|getChildrenList
argument_list|(
name|snapshot
argument_list|)
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
operator|(
name|INodeDirectory
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|check (INode[] inodes, int i, Snapshot snapshot, FsAction access )
specifier|private
name|void
name|check
parameter_list|(
name|INode
index|[]
name|inodes
parameter_list|,
name|int
name|i
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|,
name|FsAction
name|access
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|check
argument_list|(
name|i
operator|>=
literal|0
condition|?
name|inodes
index|[
name|i
index|]
else|:
literal|null
argument_list|,
name|snapshot
argument_list|,
name|access
argument_list|)
expr_stmt|;
block|}
DECL|method|check (INode inode, Snapshot snapshot, FsAction access )
specifier|private
name|void
name|check
parameter_list|(
name|INode
name|inode
parameter_list|,
name|Snapshot
name|snapshot
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
name|snapshot
argument_list|)
decl_stmt|;
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
name|snapshot
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
name|snapshot
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
literal|"Permission denied: user="
operator|+
name|user
operator|+
literal|", access="
operator|+
name|access
operator|+
literal|", inode="
operator|+
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|checkStickyBit (INode parent, INode inode, Snapshot snapshot )
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
name|Snapshot
name|snapshot
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
name|snapshot
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
name|snapshot
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
name|snapshot
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
block|}
end_class

end_unit

