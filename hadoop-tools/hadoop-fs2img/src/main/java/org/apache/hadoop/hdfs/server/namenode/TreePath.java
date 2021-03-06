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
name|protobuf
operator|.
name|ByteString
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
name|classification
operator|.
name|InterfaceStability
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
name|FileSystem
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
name|Options
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
name|PathHandle
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockProto
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
name|common
operator|.
name|FileRegion
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
name|common
operator|.
name|blockaliasmap
operator|.
name|BlockAliasMap
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INodeDirectory
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INodeFile
import|;
end_import

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
name|DFSUtil
operator|.
name|LOG
import|;
end_import

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
name|DFSUtil
operator|.
name|string2Bytes
import|;
end_import

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
name|namenode
operator|.
name|DirectoryWithQuotaFeature
operator|.
name|DEFAULT_NAMESPACE_QUOTA
import|;
end_import

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
name|namenode
operator|.
name|DirectoryWithQuotaFeature
operator|.
name|DEFAULT_STORAGE_SPACE_QUOTA
import|;
end_import

begin_comment
comment|/**  * Traversal cursor in external filesystem.  * TODO: generalize, move FS/FileRegion to FSTreePath  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TreePath
specifier|public
class|class
name|TreePath
block|{
DECL|field|id
specifier|private
name|long
name|id
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|parentId
specifier|private
specifier|final
name|long
name|parentId
decl_stmt|;
DECL|field|stat
specifier|private
specifier|final
name|FileStatus
name|stat
decl_stmt|;
DECL|field|i
specifier|private
specifier|final
name|TreeWalk
operator|.
name|TreeIterator
name|i
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|acls
specifier|private
specifier|final
name|AclStatus
name|acls
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|TreePath (FileStatus stat, long parentId, TreeWalk.TreeIterator i)
specifier|public
name|TreePath
parameter_list|(
name|FileStatus
name|stat
parameter_list|,
name|long
name|parentId
parameter_list|,
name|TreeWalk
operator|.
name|TreeIterator
name|i
parameter_list|)
block|{
name|this
argument_list|(
name|stat
argument_list|,
name|parentId
argument_list|,
name|i
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TreePath (FileStatus stat, long parentId, TreeWalk.TreeIterator i, FileSystem fs, AclStatus acls)
specifier|public
name|TreePath
parameter_list|(
name|FileStatus
name|stat
parameter_list|,
name|long
name|parentId
parameter_list|,
name|TreeWalk
operator|.
name|TreeIterator
name|i
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|AclStatus
name|acls
parameter_list|)
block|{
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|stat
operator|=
name|stat
expr_stmt|;
name|this
operator|.
name|parentId
operator|=
name|parentId
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
block|}
DECL|method|getFileStatus ()
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|()
block|{
return|return
name|stat
return|;
block|}
DECL|method|getAclStatus ()
specifier|public
name|AclStatus
name|getAclStatus
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
DECL|method|getParentId ()
specifier|public
name|long
name|getParentId
parameter_list|()
block|{
return|return
name|parentId
return|;
block|}
DECL|method|getIterator ()
specifier|public
name|TreeWalk
operator|.
name|TreeIterator
name|getIterator
parameter_list|()
block|{
return|return
name|i
return|;
block|}
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
if|if
condition|(
name|id
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
return|return
name|id
return|;
block|}
DECL|method|accept (long pathId)
specifier|public
name|void
name|accept
parameter_list|(
name|long
name|pathId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|pathId
expr_stmt|;
name|i
operator|.
name|onAccept
argument_list|(
name|this
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|toINode (UGIResolver ugi, BlockResolver blk, BlockAliasMap.Writer<FileRegion> out)
specifier|public
name|INode
name|toINode
parameter_list|(
name|UGIResolver
name|ugi
parameter_list|,
name|BlockResolver
name|blk
parameter_list|,
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stat
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|toFile
argument_list|(
name|ugi
argument_list|,
name|blk
argument_list|,
name|out
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
name|toDirectory
argument_list|(
name|ugi
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"symlinks not supported"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unknown type: "
operator|+
name|stat
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|TreePath
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TreePath
name|o
init|=
operator|(
name|TreePath
operator|)
name|other
decl_stmt|;
return|return
name|getParentId
argument_list|()
operator|==
name|o
operator|.
name|getParentId
argument_list|()
operator|&&
name|getFileStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getFileStatus
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|long
name|pId
init|=
name|getParentId
argument_list|()
operator|*
name|getFileStatus
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|pId
operator|^
operator|(
name|pId
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
DECL|method|writeBlock (long blockId, long offset, long length, long genStamp, PathHandle pathHandle, BlockAliasMap.Writer<FileRegion> out)
name|void
name|writeBlock
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|PathHandle
name|pathHandle
parameter_list|,
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|s
init|=
name|getFileStatus
argument_list|()
decl_stmt|;
name|out
operator|.
name|store
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|blockId
argument_list|,
name|s
operator|.
name|getPath
argument_list|()
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|,
operator|(
name|pathHandle
operator|!=
literal|null
condition|?
name|pathHandle
operator|.
name|toByteArray
argument_list|()
else|:
operator|new
name|byte
index|[
literal|0
index|]
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toFile (UGIResolver ugi, BlockResolver blk, BlockAliasMap.Writer<FileRegion> out)
name|INode
name|toFile
parameter_list|(
name|UGIResolver
name|ugi
parameter_list|,
name|BlockResolver
name|blk
parameter_list|,
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileStatus
name|s
init|=
name|getFileStatus
argument_list|()
decl_stmt|;
specifier|final
name|AclStatus
name|aclStatus
init|=
name|getAclStatus
argument_list|()
decl_stmt|;
name|long
name|permissions
init|=
name|ugi
operator|.
name|getPermissionsProto
argument_list|(
name|s
argument_list|,
name|aclStatus
argument_list|)
decl_stmt|;
name|INodeFile
operator|.
name|Builder
name|b
init|=
name|INodeFile
operator|.
name|newBuilder
argument_list|()
operator|.
name|setReplication
argument_list|(
name|blk
operator|.
name|getReplication
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|s
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|setAccessTime
argument_list|(
name|s
operator|.
name|getAccessTime
argument_list|()
argument_list|)
operator|.
name|setPreferredBlockSize
argument_list|(
name|blk
operator|.
name|preferredBlockSize
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|setPermission
argument_list|(
name|permissions
argument_list|)
operator|.
name|setStoragePolicyID
argument_list|(
name|HdfsConstants
operator|.
name|PROVIDED_STORAGE_POLICY_ID
argument_list|)
decl_stmt|;
comment|// pathhandle allows match as long as the file matches exactly.
name|PathHandle
name|pathHandle
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|pathHandle
operator|=
name|fs
operator|.
name|getPathHandle
argument_list|(
name|s
argument_list|,
name|Options
operator|.
name|HandleOpt
operator|.
name|exact
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exact path handle not supported by filesystem "
operator|+
name|fs
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|aclStatus
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ACLs not supported by ImageWriter"
argument_list|)
throw|;
block|}
comment|//TODO: storage policy should be configurable per path; use BlockResolver
name|long
name|off
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|BlockProto
name|block
range|:
name|blk
operator|.
name|resolve
argument_list|(
name|s
argument_list|)
control|)
block|{
name|b
operator|.
name|addBlocks
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|writeBlock
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|off
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|block
operator|.
name|getGenStamp
argument_list|()
argument_list|,
name|pathHandle
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|off
operator|+=
name|block
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
name|INode
operator|.
name|Builder
name|ib
init|=
name|INode
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|INode
operator|.
name|Type
operator|.
name|FILE
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setName
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|string2Bytes
argument_list|(
name|s
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setFile
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
name|ib
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|toDirectory (UGIResolver ugi)
name|INode
name|toDirectory
parameter_list|(
name|UGIResolver
name|ugi
parameter_list|)
block|{
specifier|final
name|FileStatus
name|s
init|=
name|getFileStatus
argument_list|()
decl_stmt|;
specifier|final
name|AclStatus
name|aclStatus
init|=
name|getAclStatus
argument_list|()
decl_stmt|;
name|long
name|permissions
init|=
name|ugi
operator|.
name|getPermissionsProto
argument_list|(
name|s
argument_list|,
name|aclStatus
argument_list|)
decl_stmt|;
name|INodeDirectory
operator|.
name|Builder
name|b
init|=
name|INodeDirectory
operator|.
name|newBuilder
argument_list|()
operator|.
name|setModificationTime
argument_list|(
name|s
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|setNsQuota
argument_list|(
name|DEFAULT_NAMESPACE_QUOTA
argument_list|)
operator|.
name|setDsQuota
argument_list|(
name|DEFAULT_STORAGE_SPACE_QUOTA
argument_list|)
operator|.
name|setPermission
argument_list|(
name|permissions
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclStatus
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ACLs not supported by ImageWriter"
argument_list|)
throw|;
block|}
name|INode
operator|.
name|Builder
name|ib
init|=
name|INode
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|INode
operator|.
name|Type
operator|.
name|DIRECTORY
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setName
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|string2Bytes
argument_list|(
name|s
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setDirectory
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
name|ib
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{ stat=\""
argument_list|)
operator|.
name|append
argument_list|(
name|getFileStatus
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", id="
argument_list|)
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", parentId="
argument_list|)
operator|.
name|append
argument_list|(
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", iterObjId="
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|identityHashCode
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

