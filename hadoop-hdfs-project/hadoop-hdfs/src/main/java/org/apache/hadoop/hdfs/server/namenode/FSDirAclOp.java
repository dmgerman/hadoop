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
name|AclStatus
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
name|HdfsConstants
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
name|HdfsFileStatus
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

begin_class
DECL|class|FSDirAclOp
class|class
name|FSDirAclOp
block|{
DECL|method|modifyAclEntries ( FSDirectory fsd, final String srcArg, List<AclEntry> aclSpec)
specifier|static
name|HdfsFileStatus
name|modifyAclEntries
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|src
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
init|=
name|AclStorage
operator|.
name|readINodeLogicalAcl
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
init|=
name|AclTransformation
operator|.
name|mergeAclEntries
argument_list|(
name|existingAcl
argument_list|,
name|aclSpec
argument_list|)
decl_stmt|;
name|AclStorage
operator|.
name|updateINodeAcl
argument_list|(
name|inode
argument_list|,
name|newAcl
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetAcl
argument_list|(
name|src
argument_list|,
name|newAcl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|removeAclEntries ( FSDirectory fsd, final String srcArg, List<AclEntry> aclSpec)
specifier|static
name|HdfsFileStatus
name|removeAclEntries
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|src
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
init|=
name|AclStorage
operator|.
name|readINodeLogicalAcl
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
init|=
name|AclTransformation
operator|.
name|filterAclEntriesByAclSpec
argument_list|(
name|existingAcl
argument_list|,
name|aclSpec
argument_list|)
decl_stmt|;
name|AclStorage
operator|.
name|updateINodeAcl
argument_list|(
name|inode
argument_list|,
name|newAcl
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetAcl
argument_list|(
name|src
argument_list|,
name|newAcl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|removeDefaultAcl (FSDirectory fsd, final String srcArg)
specifier|static
name|HdfsFileStatus
name|removeDefaultAcl
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|src
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
init|=
name|AclStorage
operator|.
name|readINodeLogicalAcl
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
init|=
name|AclTransformation
operator|.
name|filterDefaultAclEntries
argument_list|(
name|existingAcl
argument_list|)
decl_stmt|;
name|AclStorage
operator|.
name|updateINodeAcl
argument_list|(
name|inode
argument_list|,
name|newAcl
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetAcl
argument_list|(
name|src
argument_list|,
name|newAcl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|removeAcl (FSDirectory fsd, final String srcArg)
specifier|static
name|HdfsFileStatus
name|removeAcl
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
name|unprotectedRemoveAcl
argument_list|(
name|fsd
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetAcl
argument_list|(
name|src
argument_list|,
name|AclFeature
operator|.
name|EMPTY_ENTRY_LIST
argument_list|)
expr_stmt|;
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|setAcl ( FSDirectory fsd, final String srcArg, List<AclEntry> aclSpec)
specifier|static
name|HdfsFileStatus
name|setAcl
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
init|=
name|unprotectedSetAcl
argument_list|(
name|fsd
argument_list|,
name|src
argument_list|,
name|aclSpec
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetAcl
argument_list|(
name|src
argument_list|,
name|newAcl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getAclStatus ( FSDirectory fsd, String src)
specifier|static
name|AclStatus
name|getAclStatus
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|checkAclsConfigFlag
argument_list|(
name|fsd
argument_list|)
expr_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|String
name|srcs
init|=
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// There is no real inode for the path ending in ".snapshot", so return a
comment|// non-null, unpopulated AclStatus.  This is similar to getFileInfo.
if|if
condition|(
name|srcs
operator|.
name|endsWith
argument_list|(
name|HdfsConstants
operator|.
name|SEPARATOR_DOT_SNAPSHOT_DIR
argument_list|)
operator|&&
name|fsd
operator|.
name|getINode4DotSnapshot
argument_list|(
name|srcs
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|owner
argument_list|(
literal|""
argument_list|)
operator|.
name|group
argument_list|(
literal|""
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath
argument_list|(
name|srcs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkTraverse
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|srcs
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getPathSnapshotId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acl
init|=
name|AclStorage
operator|.
name|readINodeAcl
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|)
decl_stmt|;
return|return
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|owner
argument_list|(
name|inode
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|group
argument_list|(
name|inode
operator|.
name|getGroupName
argument_list|()
argument_list|)
operator|.
name|stickyBit
argument_list|(
name|inode
operator|.
name|getFsPermission
argument_list|(
name|snapshotId
argument_list|)
operator|.
name|getStickyBit
argument_list|()
argument_list|)
operator|.
name|addEntries
argument_list|(
name|acl
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|unprotectedSetAcl ( FSDirectory fsd, String src, List<AclEntry> aclSpec)
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|unprotectedSetAcl
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
name|src
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ACL removal is logged to edits as OP_SET_ACL with an empty list.
if|if
condition|(
name|aclSpec
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|unprotectedRemoveAcl
argument_list|(
name|fsd
argument_list|,
name|src
argument_list|)
expr_stmt|;
return|return
name|AclFeature
operator|.
name|EMPTY_ENTRY_LIST
return|;
block|}
assert|assert
name|fsd
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|src
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|existingAcl
init|=
name|AclStorage
operator|.
name|readINodeLogicalAcl
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|newAcl
init|=
name|AclTransformation
operator|.
name|replaceAclEntries
argument_list|(
name|existingAcl
argument_list|,
name|aclSpec
argument_list|)
decl_stmt|;
name|AclStorage
operator|.
name|updateINodeAcl
argument_list|(
name|inode
argument_list|,
name|newAcl
argument_list|,
name|snapshotId
argument_list|)
expr_stmt|;
return|return
name|newAcl
return|;
block|}
DECL|method|checkAclsConfigFlag (FSDirectory fsd)
specifier|private
specifier|static
name|void
name|checkAclsConfigFlag
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|)
throws|throws
name|AclException
block|{
if|if
condition|(
operator|!
name|fsd
operator|.
name|isAclsEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AclException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The ACL operation has been rejected.  "
operator|+
literal|"Support for ACLs has been disabled by setting %s to false."
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|unprotectedRemoveAcl (FSDirectory fsd, String src)
specifier|private
specifier|static
name|void
name|unprotectedRemoveAcl
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsd
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|FSDirectory
operator|.
name|normalizePath
argument_list|(
name|src
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|INode
name|inode
init|=
name|FSDirectory
operator|.
name|resolveLastINode
argument_list|(
name|src
argument_list|,
name|iip
argument_list|)
decl_stmt|;
name|int
name|snapshotId
init|=
name|iip
operator|.
name|getLatestSnapshotId
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
return|return;
block|}
name|FsPermission
name|perm
init|=
name|inode
operator|.
name|getFsPermission
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|featureEntries
init|=
name|AclStorage
operator|.
name|getEntriesFromAclFeature
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|featureEntries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getScope
argument_list|()
operator|==
name|AclEntryScope
operator|.
name|ACCESS
condition|)
block|{
comment|// Restore group permissions from the feature's entry to permission
comment|// bits, overwriting the mask, which is not part of a minimal ACL.
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
name|FsAction
name|groupPerm
init|=
name|featureEntries
operator|.
name|get
argument_list|(
name|groupEntryIndex
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
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
name|groupPerm
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
name|inode
operator|.
name|removeAclFeature
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

