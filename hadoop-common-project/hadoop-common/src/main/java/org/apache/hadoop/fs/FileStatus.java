begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|io
operator|.
name|InvalidObjectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputValidation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSProtos
operator|.
name|FileStatusProto
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
name|protocolPB
operator|.
name|PBHelper
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

begin_comment
comment|/** Interface that represents the client side information for a file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FileStatus
specifier|public
class|class
name|FileStatus
implements|implements
name|Writable
implements|,
name|Comparable
argument_list|<
name|Object
argument_list|>
implements|,
name|Serializable
implements|,
name|ObjectInputValidation
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x13caeae8
decl_stmt|;
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
name|Boolean
name|isdir
decl_stmt|;
DECL|field|block_replication
specifier|private
name|short
name|block_replication
decl_stmt|;
DECL|field|blocksize
specifier|private
name|long
name|blocksize
decl_stmt|;
DECL|field|modification_time
specifier|private
name|long
name|modification_time
decl_stmt|;
DECL|field|access_time
specifier|private
name|long
name|access_time
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
DECL|field|symlink
specifier|private
name|Path
name|symlink
decl_stmt|;
DECL|field|attr
specifier|private
name|Set
argument_list|<
name|AttrFlags
argument_list|>
name|attr
decl_stmt|;
DECL|enum|AttrFlags
specifier|private
enum|enum
name|AttrFlags
block|{
DECL|enumConstant|HAS_ACL
name|HAS_ACL
block|,
DECL|enumConstant|HAS_CRYPT
name|HAS_CRYPT
block|,
DECL|enumConstant|HAS_EC
name|HAS_EC
block|,
DECL|enumConstant|SNAPSHOT_ENABLED
name|SNAPSHOT_ENABLED
block|}
DECL|field|NONE
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|AttrFlags
argument_list|>
name|NONE
init|=
name|Collections
operator|.
expr|<
name|AttrFlags
operator|>
name|emptySet
argument_list|()
decl_stmt|;
DECL|method|flags (boolean acl, boolean crypt, boolean ec)
specifier|private
specifier|static
name|Set
argument_list|<
name|AttrFlags
argument_list|>
name|flags
parameter_list|(
name|boolean
name|acl
parameter_list|,
name|boolean
name|crypt
parameter_list|,
name|boolean
name|ec
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|acl
operator|||
name|crypt
operator|||
name|ec
operator|)
condition|)
block|{
return|return
name|NONE
return|;
block|}
name|EnumSet
argument_list|<
name|AttrFlags
argument_list|>
name|ret
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|AttrFlags
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|AttrFlags
operator|.
name|HAS_ACL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|crypt
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|AttrFlags
operator|.
name|HAS_CRYPT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ec
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|AttrFlags
operator|.
name|HAS_EC
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|FileStatus ()
specifier|public
name|FileStatus
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
comment|//We should deprecate this soon?
DECL|method|FileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, Path path)
specifier|public
name|FileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|block_replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modification_time
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
name|block_replication
argument_list|,
name|blocksize
argument_list|,
name|modification_time
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for file systems on which symbolic links are not supported    */
DECL|method|FileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, Path path)
specifier|public
name|FileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|block_replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modification_time
parameter_list|,
name|long
name|access_time
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
name|block_replication
argument_list|,
name|blocksize
argument_list|,
name|modification_time
argument_list|,
name|access_time
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
literal|null
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|FileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, Path symlink, Path path)
specifier|public
name|FileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|block_replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modification_time
parameter_list|,
name|long
name|access_time
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
name|symlink
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
name|block_replication
argument_list|,
name|blocksize
argument_list|,
name|modification_time
argument_list|,
name|access_time
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|symlink
argument_list|,
name|path
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, Path symlink, Path path, boolean hasAcl, boolean isEncrypted, boolean isErasureCoded)
specifier|public
name|FileStatus
parameter_list|(
name|long
name|length
parameter_list|,
name|boolean
name|isdir
parameter_list|,
name|int
name|block_replication
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|long
name|modification_time
parameter_list|,
name|long
name|access_time
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
name|symlink
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|hasAcl
parameter_list|,
name|boolean
name|isEncrypted
parameter_list|,
name|boolean
name|isErasureCoded
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
name|block_replication
operator|=
operator|(
name|short
operator|)
name|block_replication
expr_stmt|;
name|this
operator|.
name|blocksize
operator|=
name|blocksize
expr_stmt|;
name|this
operator|.
name|modification_time
operator|=
name|modification_time
expr_stmt|;
name|this
operator|.
name|access_time
operator|=
name|access_time
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
elseif|else
if|if
condition|(
name|isdir
condition|)
block|{
name|this
operator|.
name|permission
operator|=
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|symlink
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|permission
operator|=
name|FsPermission
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|permission
operator|=
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
name|symlink
operator|=
name|symlink
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|attr
operator|=
name|flags
argument_list|(
name|hasAcl
argument_list|,
name|isEncrypted
argument_list|,
name|isErasureCoded
argument_list|)
expr_stmt|;
comment|// The variables isdir and symlink indicate the type:
comment|// 1. isdir implies directory, in which case symlink must be null.
comment|// 2. !isdir implies a file or symlink, symlink != null implies a
comment|//    symlink, otherwise it's a file.
assert|assert
operator|(
name|isdir
operator|&&
name|symlink
operator|==
literal|null
operator|)
operator|||
operator|!
name|isdir
assert|;
block|}
comment|/**    * Copy constructor.    *    * @param other FileStatus to copy    */
DECL|method|FileStatus (FileStatus other)
specifier|public
name|FileStatus
parameter_list|(
name|FileStatus
name|other
parameter_list|)
throws|throws
name|IOException
block|{
comment|// It's important to call the getters here instead of directly accessing the
comment|// members.  Subclasses like ViewFsFileStatus can override the getters.
name|this
argument_list|(
name|other
operator|.
name|getLen
argument_list|()
argument_list|,
name|other
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|other
operator|.
name|getReplication
argument_list|()
argument_list|,
name|other
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|other
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|other
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|other
operator|.
name|getPermission
argument_list|()
argument_list|,
name|other
operator|.
name|getOwner
argument_list|()
argument_list|,
name|other
operator|.
name|getGroup
argument_list|()
argument_list|,
operator|(
name|other
operator|.
name|isSymlink
argument_list|()
condition|?
name|other
operator|.
name|getSymlink
argument_list|()
else|:
literal|null
operator|)
argument_list|,
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the length of this file, in bytes.    * @return the length of this file, in bytes.    */
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
comment|/**    * Is this a file?    * @return true if this is a file    */
DECL|method|isFile ()
specifier|public
name|boolean
name|isFile
parameter_list|()
block|{
return|return
operator|!
name|isDirectory
argument_list|()
operator|&&
operator|!
name|isSymlink
argument_list|()
return|;
block|}
comment|/**    * Is this a directory?    * @return true if this is a directory    */
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
comment|/**    * Old interface, instead use the explicit {@link FileStatus#isFile()},    * {@link FileStatus#isDirectory()}, and {@link FileStatus#isSymlink()}    * @return true if this is a directory.    * @deprecated Use {@link FileStatus#isFile()},    * {@link FileStatus#isDirectory()}, and {@link FileStatus#isSymlink()}    * instead.    */
annotation|@
name|Deprecated
DECL|method|isDir ()
specifier|public
specifier|final
name|boolean
name|isDir
parameter_list|()
block|{
return|return
name|isDirectory
argument_list|()
return|;
block|}
comment|/**    * Is this a symbolic link?    * @return true if this is a symbolic link    */
DECL|method|isSymlink ()
specifier|public
name|boolean
name|isSymlink
parameter_list|()
block|{
return|return
name|symlink
operator|!=
literal|null
return|;
block|}
comment|/**    * Get the block size of the file.    * @return the number of bytes    */
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
comment|/**    * Get the replication factor of a file.    * @return the replication factor of a file.    */
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|block_replication
return|;
block|}
comment|/**    * Get the modification time of the file.    * @return the modification time of file in milliseconds since January 1, 1970 UTC.    */
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modification_time
return|;
block|}
comment|/**    * Get the access time of the file.    * @return the access time of file in milliseconds since January 1, 1970 UTC.    */
DECL|method|getAccessTime ()
specifier|public
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
name|access_time
return|;
block|}
comment|/**    * Get FsPermission associated with the file.    * @return permission. If a filesystem does not have a notion of permissions    *         or if permissions could not be determined, then default     *         permissions equivalent of "rwxrwxrwx" is returned.    */
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
comment|/**    * Tell whether the underlying file or directory has ACLs set.    *    * @return true if the underlying file or directory has ACLs set.    */
DECL|method|hasAcl ()
specifier|public
name|boolean
name|hasAcl
parameter_list|()
block|{
return|return
name|attr
operator|.
name|contains
argument_list|(
name|AttrFlags
operator|.
name|HAS_ACL
argument_list|)
return|;
block|}
comment|/**    * Tell whether the underlying file or directory is encrypted or not.    *    * @return true if the underlying file is encrypted.    */
DECL|method|isEncrypted ()
specifier|public
name|boolean
name|isEncrypted
parameter_list|()
block|{
return|return
name|attr
operator|.
name|contains
argument_list|(
name|AttrFlags
operator|.
name|HAS_CRYPT
argument_list|)
return|;
block|}
comment|/**    * Tell whether the underlying file or directory is erasure coded or not.    *    * @return true if the underlying file or directory is erasure coded.    */
DECL|method|isErasureCoded ()
specifier|public
name|boolean
name|isErasureCoded
parameter_list|()
block|{
return|return
name|attr
operator|.
name|contains
argument_list|(
name|AttrFlags
operator|.
name|HAS_EC
argument_list|)
return|;
block|}
comment|/**    * Check if directory is Snapshot enabled or not.    *    * @return true if directory is snapshot enabled    */
DECL|method|isSnapshotEnabled ()
specifier|public
name|boolean
name|isSnapshotEnabled
parameter_list|()
block|{
return|return
name|attr
operator|.
name|contains
argument_list|(
name|AttrFlags
operator|.
name|SNAPSHOT_ENABLED
argument_list|)
return|;
block|}
comment|/**    * Get the owner of the file.    * @return owner of the file. The string could be empty if there is no    *         notion of owner of a file in a filesystem or if it could not     *         be determined (rare).    */
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
comment|/**    * Get the group associated with the file.    * @return group for the file. The string could be empty if there is no    *         notion of group of a file in a filesystem or if it could not     *         be determined (rare).    */
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
DECL|method|setPath (final Path p)
specifier|public
name|void
name|setPath
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
block|{
name|path
operator|=
name|p
expr_stmt|;
block|}
comment|/* These are provided so that these values could be loaded lazily     * by a filesystem (e.g. local file system).    */
comment|/**    * Sets permission.    * @param permission if permission is null, default value is set    */
DECL|method|setPermission (FsPermission permission)
specifier|protected
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
name|this
operator|.
name|permission
operator|=
operator|(
name|permission
operator|==
literal|null
operator|)
condition|?
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
else|:
name|permission
expr_stmt|;
block|}
comment|/**    * Sets owner.    * @param owner if it is null, default value is set    */
DECL|method|setOwner (String owner)
specifier|protected
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
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
block|}
comment|/**    * Sets group.    * @param group if it is null, default value is set    */
DECL|method|setGroup (String group)
specifier|protected
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
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
block|}
comment|/**    * Sets Snapshot enabled flag.    *    * @param isSnapShotEnabled When true, SNAPSHOT_ENABLED flag is set    */
DECL|method|setSnapShotEnabledFlag (boolean isSnapShotEnabled)
specifier|public
name|void
name|setSnapShotEnabledFlag
parameter_list|(
name|boolean
name|isSnapShotEnabled
parameter_list|)
block|{
if|if
condition|(
name|isSnapShotEnabled
condition|)
block|{
name|attr
operator|.
name|add
argument_list|(
name|AttrFlags
operator|.
name|SNAPSHOT_ENABLED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attr
operator|.
name|remove
argument_list|(
name|AttrFlags
operator|.
name|SNAPSHOT_ENABLED
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return The contents of the symbolic link.    */
DECL|method|getSymlink ()
specifier|public
name|Path
name|getSymlink
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isSymlink
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Path "
operator|+
name|path
operator|+
literal|" is not a symbolic link"
argument_list|)
throw|;
block|}
return|return
name|symlink
return|;
block|}
DECL|method|setSymlink (final Path p)
specifier|public
name|void
name|setSymlink
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
block|{
name|symlink
operator|=
name|p
expr_stmt|;
block|}
comment|/**    * Compare this FileStatus to another FileStatus    * @param   o the FileStatus to be compared.    * @return  a negative integer, zero, or a positive integer as this object    *   is less than, equal to, or greater than the specified object.    */
DECL|method|compareTo (FileStatus o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|FileStatus
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|getPath
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compare this FileStatus to another FileStatus.    * This method was added back by HADOOP-14683 to keep binary compatibility.    *    * @param   o the FileStatus to be compared.    * @return  a negative integer, zero, or a positive integer as this object    *   is less than, equal to, or greater than the specified object.    * @throws ClassCastException if the specified object is not FileStatus    */
annotation|@
name|Override
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|FileStatus
name|other
init|=
operator|(
name|FileStatus
operator|)
name|o
decl_stmt|;
return|return
name|compareTo
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/** Compare if this object is equal to another object    * @param   o the object to be compared.    * @return  true if two file status has the same path name; false if not.    */
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
operator|!
operator|(
name|o
operator|instanceof
name|FileStatus
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
name|FileStatus
name|other
init|=
operator|(
name|FileStatus
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
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
return|;
block|}
comment|/**    * Returns a hash code value for the object, which is defined as    * the hash code of the path name.    *    * @return  a hash code value for the path name.    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|.
name|hashCode
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"path="
operator|+
name|path
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; isDirectory="
operator|+
name|isdir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isDirectory
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"; length="
operator|+
name|length
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; replication="
operator|+
name|block_replication
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; blocksize="
operator|+
name|blocksize
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"; modification_time="
operator|+
name|modification_time
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; access_time="
operator|+
name|access_time
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; owner="
operator|+
name|owner
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; group="
operator|+
name|group
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; permission="
operator|+
name|permission
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; isSymlink="
operator|+
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSymlink
argument_list|()
condition|)
block|{
try|try
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"; symlink="
operator|+
name|getSymlink
argument_list|()
argument_list|)
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
name|RuntimeException
argument_list|(
literal|"Unexpected exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"; hasAcl="
operator|+
name|hasAcl
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; isEncrypted="
operator|+
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; isErasureCoded="
operator|+
name|isErasureCoded
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Read instance encoded as protobuf from stream.    * @param in Input stream    * @see PBHelper#convert(FileStatus)    * @deprecated Use the {@link PBHelper} and protobuf serialization directly.    */
annotation|@
name|Override
annotation|@
name|Deprecated
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
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't read FileStatusProto with negative "
operator|+
literal|"size of "
operator|+
name|size
argument_list|)
throw|;
block|}
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|FileStatusProto
name|proto
init|=
name|FileStatusProto
operator|.
name|parseFrom
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|FileStatus
name|other
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
argument_list|)
decl_stmt|;
name|isdir
operator|=
name|other
operator|.
name|isDirectory
argument_list|()
expr_stmt|;
name|length
operator|=
name|other
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|block_replication
operator|=
name|other
operator|.
name|getReplication
argument_list|()
expr_stmt|;
name|blocksize
operator|=
name|other
operator|.
name|getBlockSize
argument_list|()
expr_stmt|;
name|modification_time
operator|=
name|other
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|access_time
operator|=
name|other
operator|.
name|getAccessTime
argument_list|()
expr_stmt|;
name|setPermission
argument_list|(
name|other
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|setOwner
argument_list|(
name|other
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|setGroup
argument_list|(
name|other
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|setSymlink
argument_list|(
operator|(
name|other
operator|.
name|isSymlink
argument_list|()
condition|?
name|other
operator|.
name|getSymlink
argument_list|()
else|:
literal|null
operator|)
argument_list|)
expr_stmt|;
name|setPath
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|=
name|flags
argument_list|(
name|other
operator|.
name|hasAcl
argument_list|()
argument_list|,
name|other
operator|.
name|isEncrypted
argument_list|()
argument_list|,
name|other
operator|.
name|isErasureCoded
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|isDirectory
argument_list|()
operator|&&
name|getSymlink
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|!
name|isDirectory
argument_list|()
assert|;
block|}
comment|/**    * Write instance encoded as protobuf to stream.    * @param out Output stream    * @see PBHelper#convert(FileStatus)    * @deprecated Use the {@link PBHelper} and protobuf serialization directly.    */
annotation|@
name|Override
annotation|@
name|Deprecated
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
name|FileStatusProto
name|proto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|proto
operator|.
name|getSerializedSize
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|proto
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validateObject ()
specifier|public
name|void
name|validateObject
parameter_list|()
throws|throws
name|InvalidObjectException
block|{
if|if
condition|(
literal|null
operator|==
name|path
condition|)
block|{
throw|throw
operator|new
name|InvalidObjectException
argument_list|(
literal|"No Path in deserialized FileStatus"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|isdir
condition|)
block|{
throw|throw
operator|new
name|InvalidObjectException
argument_list|(
literal|"No type in deserialized FileStatus"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

