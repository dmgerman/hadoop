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
name|ErasureCodingPolicy
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
name|ErasureCodingZone
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
name|IOUtils
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
name|WritableUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
DECL|method|getErasureCodingPolicy (INodesInPath iip)
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
name|ErasureCodingZone
name|ecZone
init|=
name|getErasureCodingZone
argument_list|(
name|iip
argument_list|)
decl_stmt|;
return|return
name|ecZone
operator|==
literal|null
condition|?
literal|null
else|:
name|ecZone
operator|.
name|getErasureCodingPolicy
argument_list|()
return|;
block|}
DECL|method|getErasureCodingZone (INodesInPath iip)
name|ErasureCodingZone
name|getErasureCodingZone
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
argument_list|,
literal|"INodes cannot be null"
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
name|getPrefixedName
argument_list|(
name|xAttr
argument_list|)
argument_list|)
condition|)
block|{
name|ByteArrayInputStream
name|bIn
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xAttr
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dIn
init|=
operator|new
name|DataInputStream
argument_list|(
name|bIn
argument_list|)
decl_stmt|;
name|String
name|ecPolicyName
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|dIn
argument_list|)
decl_stmt|;
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|dir
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getPolicy
argument_list|(
name|ecPolicyName
argument_list|)
decl_stmt|;
return|return
operator|new
name|ErasureCodingZone
argument_list|(
name|dir
operator|.
name|getInode
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getFullPathName
argument_list|()
argument_list|,
name|ecPolicy
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|createErasureCodingZone (final INodesInPath srcIIP, ErasureCodingPolicy ecPolicy)
name|List
argument_list|<
name|XAttr
argument_list|>
name|createErasureCodingZone
parameter_list|(
specifier|final
name|INodesInPath
name|srcIIP
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|srcIIP
argument_list|,
literal|"INodes cannot be null"
argument_list|)
expr_stmt|;
name|String
name|src
init|=
name|srcIIP
operator|.
name|getPath
argument_list|()
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
literal|"non-empty directory "
operator|+
name|src
argument_list|)
throw|;
block|}
if|if
condition|(
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
literal|"for a file "
operator|+
name|src
argument_list|)
throw|;
block|}
if|if
condition|(
name|getErasureCodingPolicy
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
comment|// System default erasure coding policy will be used since no specified.
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
name|ecPolicy
operator|=
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
expr_stmt|;
block|}
specifier|final
name|XAttr
name|ecXAttr
decl_stmt|;
name|DataOutputStream
name|dOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|dOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|bOut
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|dOut
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ecXAttr
operator|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|XATTR_ERASURECODING_ZONE
argument_list|,
name|bOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|dOut
argument_list|)
expr_stmt|;
block|}
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
name|xattrs
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
name|ErasureCodingZone
name|srcZone
init|=
name|getErasureCodingZone
argument_list|(
name|srcIIP
argument_list|)
decl_stmt|;
specifier|final
name|ErasureCodingZone
name|dstZone
init|=
name|getErasureCodingZone
argument_list|(
name|dstIIP
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcZone
operator|!=
literal|null
operator|&&
name|srcZone
operator|.
name|getDir
argument_list|()
operator|.
name|equals
argument_list|(
name|src
argument_list|)
operator|&&
name|dstZone
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|ErasureCodingPolicy
name|srcECPolicy
init|=
name|srcZone
operator|!=
literal|null
condition|?
name|srcZone
operator|.
name|getErasureCodingPolicy
argument_list|()
else|:
literal|null
decl_stmt|;
specifier|final
name|ErasureCodingPolicy
name|dstECPolicy
init|=
name|dstZone
operator|!=
literal|null
condition|?
name|dstZone
operator|.
name|getErasureCodingPolicy
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|srcECPolicy
operator|!=
literal|null
operator|&&
operator|!
name|srcECPolicy
operator|.
name|equals
argument_list|(
name|dstECPolicy
argument_list|)
operator|||
name|dstECPolicy
operator|!=
literal|null
operator|&&
operator|!
name|dstECPolicy
operator|.
name|equals
argument_list|(
name|srcECPolicy
argument_list|)
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

