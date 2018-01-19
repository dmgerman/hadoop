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
import|import static
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|XATTR_SATISFY_STORAGE_POLICY
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
name|EnumSet
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
name|FileStatus
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
name|XAttr
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
name|XAttrSetFlag
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
name|hdfs
operator|.
name|XAttrHelper
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
name|BlockManager
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
name|FSDirectory
operator|.
name|DirOp
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

begin_comment
comment|/**  * Helper class to perform storage policy satisfier related operations.  */
end_comment

begin_class
DECL|class|FSDirSatisfyStoragePolicyOp
specifier|final
class|class
name|FSDirSatisfyStoragePolicyOp
block|{
comment|/**    * Private constructor for preventing FSDirSatisfyStoragePolicyOp object    * creation. Static-only class.    */
DECL|method|FSDirSatisfyStoragePolicyOp ()
specifier|private
name|FSDirSatisfyStoragePolicyOp
parameter_list|()
block|{   }
DECL|method|satisfyStoragePolicy (FSDirectory fsd, BlockManager bm, String src, boolean logRetryCache)
specifier|static
name|FileStatus
name|satisfyStoragePolicy
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|BlockManager
name|bm
parameter_list|,
name|String
name|src
parameter_list|,
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|INodesInPath
name|iip
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// check operation permission.
name|iip
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|DirOp
operator|.
name|WRITE
argument_list|)
expr_stmt|;
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
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|WRITE
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
name|iip
argument_list|)
decl_stmt|;
if|if
condition|(
name|inodeHasSatisfyXAttr
argument_list|(
name|inode
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot request to call satisfy storage policy on path "
operator|+
name|inode
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|", as this file/dir was already called for satisfying "
operator|+
literal|"storage policy."
argument_list|)
throw|;
block|}
if|if
condition|(
name|unprotectedSatisfyStoragePolicy
argument_list|(
name|inode
argument_list|,
name|fsd
argument_list|)
condition|)
block|{
name|XAttr
name|satisfyXAttr
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|satisfyXAttr
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|existingXAttrs
init|=
name|XAttrStorage
operator|.
name|readINodeXAttrs
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|newXAttrs
init|=
name|FSDirXAttrOp
operator|.
name|setINodeXAttrs
argument_list|(
name|fsd
argument_list|,
name|existingXAttrs
argument_list|,
name|xAttrs
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|XAttrStorage
operator|.
name|updateINodeXAttrs
argument_list|(
name|inode
argument_list|,
name|newXAttrs
argument_list|,
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetXAttrs
argument_list|(
name|src
argument_list|,
name|xAttrs
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
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
name|iip
argument_list|)
return|;
block|}
DECL|method|unprotectedSatisfyStoragePolicy (INode inode, FSDirectory fsd)
specifier|static
name|boolean
name|unprotectedSatisfyStoragePolicy
parameter_list|(
name|INode
name|inode
parameter_list|,
name|FSDirectory
name|fsd
parameter_list|)
block|{
if|if
condition|(
name|inode
operator|.
name|isFile
argument_list|()
operator|&&
name|inode
operator|.
name|asFile
argument_list|()
operator|.
name|numBlocks
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// Adding directory in the pending queue, so FileInodeIdCollector process
comment|// directory child in batch and recursively
name|fsd
operator|.
name|getBlockManager
argument_list|()
operator|.
name|addSPSPathId
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|inodeHasSatisfyXAttr (INode inode)
specifier|private
specifier|static
name|boolean
name|inodeHasSatisfyXAttr
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
specifier|final
name|XAttrFeature
name|f
init|=
name|inode
operator|.
name|getXAttrFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|inode
operator|.
name|isFile
argument_list|()
operator|&&
name|f
operator|!=
literal|null
operator|&&
name|f
operator|.
name|getXAttr
argument_list|(
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|removeSPSXattr (FSDirectory fsd, INode inode, XAttr spsXAttr)
specifier|static
name|void
name|removeSPSXattr
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|INode
name|inode
parameter_list|,
name|XAttr
name|spsXAttr
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|existingXAttrs
init|=
name|XAttrStorage
operator|.
name|readINodeXAttrs
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|existingXAttrs
operator|.
name|remove
argument_list|(
name|spsXAttr
argument_list|)
expr_stmt|;
name|XAttrStorage
operator|.
name|updateINodeXAttrs
argument_list|(
name|inode
argument_list|,
name|existingXAttrs
argument_list|,
name|INodesInPath
operator|.
name|fromINode
argument_list|(
name|inode
argument_list|)
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|spsXAttr
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logRemoveXAttrs
argument_list|(
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|,
name|xAttrs
argument_list|,
literal|false
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
block|}
block|}
end_class

end_unit

