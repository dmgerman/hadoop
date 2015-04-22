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
block|{
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
name|isdir
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
comment|/**    * Old interface, instead use the explicit {@link FileStatus#isFile()},     * {@link FileStatus#isDirectory()}, and {@link FileStatus#isSymlink()}     * @return true if this is a directory.    * @deprecated Use {@link FileStatus#isFile()},      * {@link FileStatus#isDirectory()}, and {@link FileStatus#isSymlink()}     * instead.    */
annotation|@
name|Deprecated
DECL|method|isDir ()
specifier|public
name|boolean
name|isDir
parameter_list|()
block|{
return|return
name|isdir
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
comment|/**    * Tell whether the underlying file or directory is encrypted or not.    *    * @return true if the underlying file is encrypted.    */
DECL|method|isEncrypted ()
specifier|public
name|boolean
name|isEncrypted
parameter_list|()
block|{
return|return
name|permission
operator|.
name|getEncryptedBit
argument_list|()
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
comment|//////////////////////////////////////////////////
comment|// Writable
comment|//////////////////////////////////////////////////
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
name|getPermission
argument_list|()
operator|.
name|write
argument_list|(
name|out
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
name|out
operator|.
name|writeBoolean
argument_list|(
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
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|getSymlink
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
block|}
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
name|block_replication
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
name|modification_time
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|access_time
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|permission
operator|.
name|readFields
argument_list|(
name|in
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|this
operator|.
name|symlink
operator|=
operator|new
name|Path
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|symlink
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Compare this object to another object    *     * @param   o the object to be compared.    * @return  a negative integer, zero, or a positive integer as this object    *   is less than, equal to, or greater than the specified object.    *     * @throws ClassCastException if the specified object's is not of     *         type FileStatus    */
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
name|this
operator|.
name|getPath
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
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
name|o
operator|==
literal|null
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
name|sb
operator|.
name|append
argument_list|(
literal|"; symlink="
operator|+
name|symlink
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

