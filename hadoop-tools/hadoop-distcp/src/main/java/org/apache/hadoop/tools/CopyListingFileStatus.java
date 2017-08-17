begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Path
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
name|io
operator|.
name|Text
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
name|Writable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * CopyListingFileStatus is a view of {@link FileStatus}, recording additional  * data members useful to distcp.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CopyListingFileStatus
specifier|public
specifier|final
class|class
name|CopyListingFileStatus
implements|implements
name|Writable
block|{
DECL|field|NO_ACL_ENTRIES
specifier|private
specifier|static
specifier|final
name|byte
name|NO_ACL_ENTRIES
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|NO_XATTRS
specifier|private
specifier|static
specifier|final
name|int
name|NO_XATTRS
init|=
operator|-
literal|1
decl_stmt|;
comment|// FileStatus fields
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|isdir
specifier|private
name|boolean
name|isdir
decl_stmt|;
DECL|field|blockReplication
specifier|private
name|short
name|blockReplication
decl_stmt|;
DECL|field|blocksize
specifier|private
name|long
name|blocksize
decl_stmt|;
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
decl_stmt|;
DECL|field|accessTime
specifier|private
name|long
name|accessTime
decl_stmt|;
DECL|field|permission
specifier|private
name|FsPermission
name|permission
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
name|String
name|group
decl_stmt|;
comment|// Retain static arrays of enum values to prevent repeated allocation of new
comment|// arrays during deserialization.
DECL|field|ACL_ENTRY_TYPES
specifier|private
specifier|static
specifier|final
name|AclEntryType
index|[]
name|ACL_ENTRY_TYPES
init|=
name|AclEntryType
operator|.
name|values
argument_list|()
decl_stmt|;
DECL|field|ACL_ENTRY_SCOPES
specifier|private
specifier|static
specifier|final
name|AclEntryScope
index|[]
name|ACL_ENTRY_SCOPES
init|=
name|AclEntryScope
operator|.
name|values
argument_list|()
decl_stmt|;
DECL|field|FS_ACTIONS
specifier|private
specifier|static
specifier|final
name|FsAction
index|[]
name|FS_ACTIONS
init|=
name|FsAction
operator|.
name|values
argument_list|()
decl_stmt|;
DECL|field|aclEntries
specifier|private
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
decl_stmt|;
DECL|field|xAttrs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xAttrs
decl_stmt|;
comment|//<chunkOffset, chunkLength> represents the offset and length of a file
comment|// chunk in number of bytes.
comment|// used when splitting a large file to chunks to copy in parallel.
comment|// If a file is not large enough to split, chunkOffset would be 0 and
comment|// chunkLength would be the length of the file.
DECL|field|chunkOffset
specifier|private
name|long
name|chunkOffset
init|=
literal|0
decl_stmt|;
DECL|field|chunkLength
specifier|private
name|long
name|chunkLength
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Default constructor.    */
DECL|method|CopyListingFileStatus ()
specifier|public
name|CopyListingFileStatus
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new CopyListingFileStatus by copying the members of the given    * FileStatus.    *    * @param fileStatus FileStatus to copy    */
DECL|method|CopyListingFileStatus (FileStatus fileStatus)
specifier|public
name|CopyListingFileStatus
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|)
block|{
name|this
argument_list|(
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CopyListingFileStatus (FileStatus fileStatus, long chunkOffset, long chunkLength)
specifier|public
name|CopyListingFileStatus
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|,
name|long
name|chunkOffset
parameter_list|,
name|long
name|chunkLength
parameter_list|)
block|{
name|this
argument_list|(
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkOffset
operator|=
name|chunkOffset
expr_stmt|;
name|this
operator|.
name|chunkLength
operator|=
name|chunkLength
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:parameternumber"
argument_list|)
DECL|method|CopyListingFileStatus (long length, boolean isdir, int blockReplication, long blocksize, long modificationTime, long accessTime, FsPermission permission, String owner, String group, Path path)
specifier|public
name|CopyListingFileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|blockReplication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|length
argument_list|,
name|isdir
argument_list|,
name|blockReplication
argument_list|,
name|blocksize
argument_list|,
name|modificationTime
argument_list|,
name|accessTime
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:parameternumber"
argument_list|)
DECL|method|CopyListingFileStatus (long length, boolean isdir, int blockReplication, long blocksize, long modificationTime, long accessTime, FsPermission permission, String owner, String group, Path path, long chunkOffset, long chunkLength)
specifier|public
name|CopyListingFileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|blockReplication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|Path
name|path
parameter_list|,
name|long
name|chunkOffset
parameter_list|,
name|long
name|chunkLength
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|isdir
operator|=
name|isdir
expr_stmt|;
name|this
operator|.
name|blockReplication
operator|=
operator|(
name|short
operator|)
name|blockReplication
expr_stmt|;
name|this
operator|.
name|blocksize
operator|=
name|blocksize
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
name|this
operator|.
name|accessTime
operator|=
name|accessTime
expr_stmt|;
if|if
condition|(
name|permission
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|permission
operator|=
name|isdir
condition|?
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
else|:
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|owner
operator|=
operator|(
name|owner
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
operator|(
name|group
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|group
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|chunkOffset
operator|=
name|chunkOffset
expr_stmt|;
name|this
operator|.
name|chunkLength
operator|=
name|chunkLength
expr_stmt|;
block|}
DECL|method|CopyListingFileStatus (CopyListingFileStatus other)
specifier|public
name|CopyListingFileStatus
parameter_list|(
name|CopyListingFileStatus
name|other
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|isdir
operator|=
name|other
operator|.
name|isdir
expr_stmt|;
name|this
operator|.
name|blockReplication
operator|=
name|other
operator|.
name|blockReplication
expr_stmt|;
name|this
operator|.
name|blocksize
operator|=
name|other
operator|.
name|blocksize
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|other
operator|.
name|modificationTime
expr_stmt|;
name|this
operator|.
name|accessTime
operator|=
name|other
operator|.
name|accessTime
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|other
operator|.
name|permission
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|other
operator|.
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|other
operator|.
name|group
expr_stmt|;
name|this
operator|.
name|path
operator|=
operator|new
name|Path
argument_list|(
name|other
operator|.
name|path
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkOffset
operator|=
name|other
operator|.
name|chunkOffset
expr_stmt|;
name|this
operator|.
name|chunkLength
operator|=
name|other
operator|.
name|chunkLength
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getLen ()
specifier|public
name|long
name|getLen
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getBlockSize ()
specifier|public
name|long
name|getBlockSize
parameter_list|()
block|{
return|return
name|blocksize
return|;
block|}
DECL|method|isDirectory ()
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|isdir
return|;
block|}
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|blockReplication
return|;
block|}
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|getGroup ()
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
DECL|method|getAccessTime ()
specifier|public
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
name|accessTime
return|;
block|}
DECL|method|getPermission ()
specifier|public
name|FsPermission
name|getPermission
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
DECL|method|isErasureCoded ()
specifier|public
name|boolean
name|isErasureCoded
parameter_list|()
block|{
return|return
name|getPermission
argument_list|()
operator|.
name|getErasureCodedBit
argument_list|()
return|;
block|}
comment|/**    * Returns the full logical ACL.    *    * @return List containing full logical ACL    */
DECL|method|getAclEntries ()
specifier|public
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getAclEntries
parameter_list|()
block|{
return|return
name|AclUtil
operator|.
name|getAclFromPermAndEntries
argument_list|(
name|getPermission
argument_list|()
argument_list|,
name|aclEntries
operator|!=
literal|null
condition|?
name|aclEntries
else|:
name|Collections
operator|.
expr|<
name|AclEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Sets optional ACL entries.    *    * @param aclEntries List containing all ACL entries    */
DECL|method|setAclEntries (List<AclEntry> aclEntries)
specifier|public
name|void
name|setAclEntries
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
parameter_list|)
block|{
name|this
operator|.
name|aclEntries
operator|=
name|aclEntries
expr_stmt|;
block|}
comment|/**    * Returns all xAttrs.    *     * @return Map containing all xAttrs    */
DECL|method|getXAttrs ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|getXAttrs
parameter_list|()
block|{
return|return
name|xAttrs
operator|!=
literal|null
condition|?
name|xAttrs
else|:
name|Collections
operator|.
expr|<
name|String
operator|,
name|byte
index|[]
operator|>
name|emptyMap
argument_list|()
return|;
block|}
comment|/**    * Sets optional xAttrs.    *     * @param xAttrs Map containing all xAttrs    */
DECL|method|setXAttrs (Map<String, byte[]> xAttrs)
specifier|public
name|void
name|setXAttrs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xAttrs
parameter_list|)
block|{
name|this
operator|.
name|xAttrs
operator|=
name|xAttrs
expr_stmt|;
block|}
DECL|method|getChunkOffset ()
specifier|public
name|long
name|getChunkOffset
parameter_list|()
block|{
return|return
name|chunkOffset
return|;
block|}
DECL|method|setChunkOffset (long offset)
specifier|public
name|void
name|setChunkOffset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|chunkOffset
operator|=
name|offset
expr_stmt|;
block|}
DECL|method|getChunkLength ()
specifier|public
name|long
name|getChunkLength
parameter_list|()
block|{
return|return
name|chunkLength
return|;
block|}
DECL|method|setChunkLength (long chunkLength)
specifier|public
name|void
name|setChunkLength
parameter_list|(
name|long
name|chunkLength
parameter_list|)
block|{
name|this
operator|.
name|chunkLength
operator|=
name|chunkLength
expr_stmt|;
block|}
DECL|method|isSplit ()
specifier|public
name|boolean
name|isSplit
parameter_list|()
block|{
return|return
name|getChunkLength
argument_list|()
operator|!=
name|Long
operator|.
name|MAX_VALUE
operator|&&
name|getChunkLength
argument_list|()
operator|!=
name|getLen
argument_list|()
return|;
block|}
DECL|method|getSizeToCopy ()
specifier|public
name|long
name|getSizeToCopy
parameter_list|()
block|{
return|return
name|isSplit
argument_list|()
condition|?
name|getChunkLength
argument_list|()
else|:
name|getLen
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|getOwner
argument_list|()
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|getGroup
argument_list|()
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
expr_stmt|;
if|if
condition|(
name|aclEntries
operator|!=
literal|null
condition|)
block|{
comment|// byte is sufficient, because 32 ACL entries is the max enforced by HDFS.
name|out
operator|.
name|writeByte
argument_list|(
name|aclEntries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AclEntry
name|entry
range|:
name|aclEntries
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|entry
operator|.
name|getScope
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|entry
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|entry
operator|.
name|getPermission
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|NO_ACL_ENTRIES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xAttrs
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|iter
init|=
name|xAttrs
operator|.
name|entrySet
argument_list|()
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
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|NO_XATTRS
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeLong
argument_list|(
name|chunkOffset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|chunkLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|strPath
init|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
decl_stmt|;
name|this
operator|.
name|path
operator|=
operator|new
name|Path
argument_list|(
name|strPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|isdir
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockReplication
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|blocksize
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|modificationTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|accessTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|permission
operator|.
name|fromShort
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
name|owner
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
expr_stmt|;
name|group
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|Text
operator|.
name|DEFAULT_MAX_LEN
argument_list|)
expr_stmt|;
name|byte
name|aclEntriesSize
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|aclEntriesSize
operator|!=
name|NO_ACL_ENTRIES
condition|)
block|{
name|aclEntries
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|aclEntriesSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aclEntriesSize
condition|;
operator|++
name|i
control|)
block|{
name|aclEntries
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
name|ACL_ENTRY_SCOPES
index|[
name|in
operator|.
name|readByte
argument_list|()
index|]
argument_list|)
operator|.
name|setType
argument_list|(
name|ACL_ENTRY_TYPES
index|[
name|in
operator|.
name|readByte
argument_list|()
index|]
argument_list|)
operator|.
name|setName
argument_list|(
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FS_ACTIONS
index|[
name|in
operator|.
name|readByte
argument_list|()
index|]
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|aclEntries
operator|=
literal|null
expr_stmt|;
block|}
name|int
name|xAttrsSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|xAttrsSize
operator|!=
name|NO_XATTRS
condition|)
block|{
name|xAttrs
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|xAttrsSize
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|String
name|name
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|final
name|int
name|valueLen
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|value
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|valueLen
operator|>
operator|-
literal|1
condition|)
block|{
name|value
operator|=
operator|new
name|byte
index|[
name|valueLen
index|]
expr_stmt|;
if|if
condition|(
name|valueLen
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|readFully
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|xAttrs
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|xAttrs
operator|=
literal|null
expr_stmt|;
block|}
name|chunkOffset
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|chunkLength
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|o
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CopyListingFileStatus
name|other
init|=
operator|(
name|CopyListingFileStatus
operator|)
name|o
decl_stmt|;
return|return
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|aclEntries
argument_list|,
name|other
operator|.
name|aclEntries
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|xAttrs
argument_list|,
name|other
operator|.
name|xAttrs
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
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|aclEntries
argument_list|,
name|xAttrs
argument_list|)
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
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" length = "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" aclEntries = "
argument_list|)
operator|.
name|append
argument_list|(
name|aclEntries
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", xAttrs = "
argument_list|)
operator|.
name|append
argument_list|(
name|xAttrs
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSplit
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", chunkOffset = "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|getChunkOffset
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", chunkLength = "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|getChunkLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
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

