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
name|protocol
operator|.
name|ErasureCodingZoneInfo
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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
name|ArrayList
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
name|XATTR_ERASURECODING_ZONE
import|;
end_import

begin_comment
comment|/**  * Manages the list of erasure coding zones in the filesystem.  *<p/>  * The ErasureCodingZoneManager has its own lock, but relies on the FSDirectory  * lock being held for many operations. The FSDirectory lock should not be  * taken if the manager lock is already held.  * TODO: consolidate zone logic w/ encrypt. zones {@link EncryptionZoneManager}  */
end_comment

begin_class
DECL|class|ErasureCodingZoneManager
specifier|public
class|class
name|ErasureCodingZoneManager
block|{
DECL|field|dir
specifier|private
specifier|final
name|FSDirectory
name|dir
decl_stmt|;
comment|/**    * Construct a new ErasureCodingZoneManager.    *    * @param dir Enclosing FSDirectory    */
DECL|method|ErasureCodingZoneManager (FSDirectory dir)
specifier|public
name|ErasureCodingZoneManager
parameter_list|(
name|FSDirectory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
DECL|method|getECSchema (INodesInPath iip)
name|ECSchema
name|getECSchema
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
name|ErasureCodingZoneInfo
name|ecZoneInfo
init|=
name|getECZoneInfo
argument_list|(
name|iip
argument_list|)
decl_stmt|;
return|return
name|ecZoneInfo
operator|==
literal|null
condition|?
literal|null
else|:
name|ecZoneInfo
operator|.
name|getSchema
argument_list|()
return|;
block|}
DECL|method|getECZoneInfo (INodesInPath iip)
name|ErasureCodingZoneInfo
name|getECZoneInfo
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|iip
argument_list|)
expr_stmt|;
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
name|inodes
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
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
continue|continue;
block|}
comment|// We don't allow symlinks in an EC zone, or pointing to a file/dir in
comment|// an EC. Therefore if a symlink is encountered, the dir shouldn't have
comment|// EC
comment|// TODO: properly support symlinks in EC zones
if|if
condition|(
name|inode
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|inode
operator|.
name|getXAttrFeature
argument_list|()
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<
name|XAttr
argument_list|>
argument_list|(
literal|0
argument_list|)
else|:
name|inode
operator|.
name|getXAttrFeature
argument_list|()
operator|.
name|getXAttrs
argument_list|()
decl_stmt|;
for|for
control|(
name|XAttr
name|xAttr
range|:
name|xAttrs
control|)
block|{
if|if
condition|(
name|XATTR_ERASURECODING_ZONE
operator|.
name|equals
argument_list|(
name|XAttrHelper
operator|.
name|getPrefixName
argument_list|(
name|xAttr
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|schemaName
init|=
operator|new
name|String
argument_list|(
name|xAttr
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|ECSchema
name|schema
init|=
name|dir
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getECSchemaManager
argument_list|()
operator|.
name|getSchema
argument_list|(
name|schemaName
argument_list|)
decl_stmt|;
return|return
operator|new
name|ErasureCodingZoneInfo
argument_list|(
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|,
name|schema
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|createErasureCodingZone (String src, ECSchema schema)
name|XAttr
name|createErasureCodingZone
parameter_list|(
name|String
name|src
parameter_list|,
name|ECSchema
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasWriteLock
argument_list|()
assert|;
specifier|final
name|INodesInPath
name|srcIIP
init|=
name|dir
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|isNonEmptyDirectory
argument_list|(
name|srcIIP
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempt to create an erasure coding zone for a "
operator|+
literal|"non-empty directory."
argument_list|)
throw|;
block|}
if|if
condition|(
name|srcIIP
operator|!=
literal|null
operator|&&
name|srcIIP
operator|.
name|getLastINode
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|srcIIP
operator|.
name|getLastINode
argument_list|()
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempt to create an erasure coding zone "
operator|+
literal|"for a file."
argument_list|)
throw|;
block|}
if|if
condition|(
name|getECSchema
argument_list|(
name|srcIIP
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Directory "
operator|+
name|src
operator|+
literal|" is already in an "
operator|+
literal|"erasure coding zone."
argument_list|)
throw|;
block|}
comment|// System default schema will be used since no specified.
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
name|schema
operator|=
name|ErasureCodingSchemaManager
operator|.
name|getSystemDefaultSchema
argument_list|()
expr_stmt|;
block|}
comment|// Now persist the schema name in xattr
name|byte
index|[]
name|schemaBytes
init|=
name|schema
operator|.
name|getSchemaName
argument_list|()
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|XAttr
name|ecXAttr
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|XATTR_ERASURECODING_ZONE
argument_list|,
name|schemaBytes
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xattrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xattrs
operator|.
name|add
argument_list|(
name|ecXAttr
argument_list|)
expr_stmt|;
name|FSDirXAttrOp
operator|.
name|unprotectedSetXAttrs
argument_list|(
name|dir
argument_list|,
name|src
argument_list|,
name|xattrs
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
expr_stmt|;
return|return
name|ecXAttr
return|;
block|}
DECL|method|checkMoveValidity (INodesInPath srcIIP, INodesInPath dstIIP, String src)
name|void
name|checkMoveValidity
parameter_list|(
name|INodesInPath
name|srcIIP
parameter_list|,
name|INodesInPath
name|dstIIP
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
specifier|final
name|ECSchema
name|srcSchema
init|=
name|getECSchema
argument_list|(
name|srcIIP
argument_list|)
decl_stmt|;
specifier|final
name|ECSchema
name|dstSchema
init|=
name|getECSchema
argument_list|(
name|dstIIP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|srcSchema
operator|!=
literal|null
operator|&&
operator|!
name|srcSchema
operator|.
name|equals
argument_list|(
name|dstSchema
argument_list|)
operator|)
operator|||
operator|(
name|dstSchema
operator|!=
literal|null
operator|&&
operator|!
name|dstSchema
operator|.
name|equals
argument_list|(
name|srcSchema
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|src
operator|+
literal|" can't be moved because the source and destination have "
operator|+
literal|"different erasure coding policies."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

