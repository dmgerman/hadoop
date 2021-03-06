begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|net
operator|.
name|URI
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
name|FileEncryptionInfo
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
name|FileStatus
operator|.
name|AttrFlags
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
name|DFSUtilClient
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
comment|/**  * HDFS metadata for an entity in the filesystem.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|HdfsFileStatus
specifier|public
interface|interface
name|HdfsFileStatus
extends|extends
name|Writable
extends|,
name|Comparable
argument_list|<
name|Object
argument_list|>
extends|,
name|Serializable
extends|,
name|ObjectInputValidation
block|{
DECL|field|EMPTY_NAME
name|byte
index|[]
name|EMPTY_NAME
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/** Set of features potentially active on an instance. */
DECL|enum|Flags
enum|enum
name|Flags
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
comment|/**    * Builder class for HdfsFileStatus instances. Note default values for    * parameters.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Builder
class|class
name|Builder
block|{
comment|// Changing default values will affect cases where values are not
comment|// specified. Be careful!
DECL|field|length
specifier|private
name|long
name|length
init|=
literal|0L
decl_stmt|;
DECL|field|isdir
specifier|private
name|boolean
name|isdir
init|=
literal|false
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
init|=
literal|0
decl_stmt|;
DECL|field|blocksize
specifier|private
name|long
name|blocksize
init|=
literal|0L
decl_stmt|;
DECL|field|mtime
specifier|private
name|long
name|mtime
init|=
literal|0L
decl_stmt|;
DECL|field|atime
specifier|private
name|long
name|atime
init|=
literal|0L
decl_stmt|;
DECL|field|permission
specifier|private
name|FsPermission
name|permission
init|=
literal|null
decl_stmt|;
DECL|field|flags
specifier|private
name|EnumSet
argument_list|<
name|Flags
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Flags
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
init|=
literal|null
decl_stmt|;
DECL|field|group
specifier|private
name|String
name|group
init|=
literal|null
decl_stmt|;
DECL|field|symlink
specifier|private
name|byte
index|[]
name|symlink
init|=
literal|null
decl_stmt|;
DECL|field|path
specifier|private
name|byte
index|[]
name|path
init|=
name|EMPTY_NAME
decl_stmt|;
DECL|field|fileId
specifier|private
name|long
name|fileId
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|childrenNum
specifier|private
name|int
name|childrenNum
init|=
literal|0
decl_stmt|;
DECL|field|feInfo
specifier|private
name|FileEncryptionInfo
name|feInfo
init|=
literal|null
decl_stmt|;
DECL|field|storagePolicy
specifier|private
name|byte
name|storagePolicy
init|=
name|HdfsConstants
operator|.
name|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
decl_stmt|;
DECL|field|ecPolicy
specifier|private
name|ErasureCodingPolicy
name|ecPolicy
init|=
literal|null
decl_stmt|;
DECL|field|locations
specifier|private
name|LocatedBlocks
name|locations
init|=
literal|null
decl_stmt|;
comment|/**      * Set the length of the entity (default = 0).      * @param length Entity length      * @return This Builder instance      */
DECL|method|length (long length)
specifier|public
name|Builder
name|length
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the isDir flag for the entity (default = false).      * @param isdir True if the referent is a directory.      * @return This Builder instance      */
DECL|method|isdir (boolean isdir)
specifier|public
name|Builder
name|isdir
parameter_list|(
name|boolean
name|isdir
parameter_list|)
block|{
name|this
operator|.
name|isdir
operator|=
name|isdir
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the replication of this entity (default = 0).      * @param replication Number of replicas      * @return This Builder instance      */
DECL|method|replication (int replication)
specifier|public
name|Builder
name|replication
parameter_list|(
name|int
name|replication
parameter_list|)
block|{
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the blocksize of this entity (default = 0).      * @param blocksize Target, default blocksize      * @return This Builder instance      */
DECL|method|blocksize (long blocksize)
specifier|public
name|Builder
name|blocksize
parameter_list|(
name|long
name|blocksize
parameter_list|)
block|{
name|this
operator|.
name|blocksize
operator|=
name|blocksize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the modification time of this entity (default = 0).      * @param mtime Last modified time      * @return This Builder instance      */
DECL|method|mtime (long mtime)
specifier|public
name|Builder
name|mtime
parameter_list|(
name|long
name|mtime
parameter_list|)
block|{
name|this
operator|.
name|mtime
operator|=
name|mtime
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the access time of this entity (default = 0).      * @param atime Last accessed time      * @return This Builder instance      */
DECL|method|atime (long atime)
specifier|public
name|Builder
name|atime
parameter_list|(
name|long
name|atime
parameter_list|)
block|{
name|this
operator|.
name|atime
operator|=
name|atime
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the permission mask of this entity (default = null).      * @param permission Permission bitmask      * @return This Builder instance      */
DECL|method|perm (FsPermission permission)
specifier|public
name|Builder
name|perm
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set {@link Flags} for this entity      * (default = {@link EnumSet#noneOf(Class)}).      * @param flags Flags      * @return This builder instance      */
DECL|method|flags (EnumSet<Flags> flags)
specifier|public
name|Builder
name|flags
parameter_list|(
name|EnumSet
argument_list|<
name|Flags
argument_list|>
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the owner for this entity (default = null).      * @param owner Owner      * @return This Builder instance      */
DECL|method|owner (String owner)
specifier|public
name|Builder
name|owner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the group for this entity (default = null).      * @param group Group      * @return This Builder instance      */
DECL|method|group (String group)
specifier|public
name|Builder
name|group
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set symlink bytes for this entity (default = null).      * @param symlink Symlink bytes (see      *                {@link DFSUtilClient#bytes2String(byte[])})      * @return This Builder instance      */
DECL|method|symlink (byte[] symlink)
specifier|public
name|Builder
name|symlink
parameter_list|(
name|byte
index|[]
name|symlink
parameter_list|)
block|{
name|this
operator|.
name|symlink
operator|=
literal|null
operator|==
name|symlink
condition|?
literal|null
else|:
name|Arrays
operator|.
name|copyOf
argument_list|(
name|symlink
argument_list|,
name|symlink
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set path bytes for this entity (default = {@link #EMPTY_NAME}).      * @param path Path bytes (see {@link #makeQualified(URI, Path)}).      * @return This Builder instance      */
DECL|method|path (byte[] path)
specifier|public
name|Builder
name|path
parameter_list|(
name|byte
index|[]
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
literal|null
operator|==
name|path
condition|?
literal|null
else|:
name|Arrays
operator|.
name|copyOf
argument_list|(
name|path
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the fileId for this entity (default = -1).      * @param fileId FileId      * @return This Builder instance      */
DECL|method|fileId (long fileId)
specifier|public
name|Builder
name|fileId
parameter_list|(
name|long
name|fileId
parameter_list|)
block|{
name|this
operator|.
name|fileId
operator|=
name|fileId
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the number of children for this entity (default = 0).      * @param childrenNum Number of children      * @return This Builder instance      */
DECL|method|children (int childrenNum)
specifier|public
name|Builder
name|children
parameter_list|(
name|int
name|childrenNum
parameter_list|)
block|{
name|this
operator|.
name|childrenNum
operator|=
name|childrenNum
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the encryption info for this entity (default = null).      * @param feInfo Encryption info      * @return This Builder instance      */
DECL|method|feInfo (FileEncryptionInfo feInfo)
specifier|public
name|Builder
name|feInfo
parameter_list|(
name|FileEncryptionInfo
name|feInfo
parameter_list|)
block|{
name|this
operator|.
name|feInfo
operator|=
name|feInfo
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the storage policy for this entity      * (default = {@link HdfsConstants#BLOCK_STORAGE_POLICY_ID_UNSPECIFIED}).      * @param storagePolicy Storage policy      * @return This Builder instance      */
DECL|method|storagePolicy (byte storagePolicy)
specifier|public
name|Builder
name|storagePolicy
parameter_list|(
name|byte
name|storagePolicy
parameter_list|)
block|{
name|this
operator|.
name|storagePolicy
operator|=
name|storagePolicy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the erasure coding policy for this entity (default = null).      * @param ecPolicy Erasure coding policy      * @return This Builder instance      */
DECL|method|ecPolicy (ErasureCodingPolicy ecPolicy)
specifier|public
name|Builder
name|ecPolicy
parameter_list|(
name|ErasureCodingPolicy
name|ecPolicy
parameter_list|)
block|{
name|this
operator|.
name|ecPolicy
operator|=
name|ecPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the block locations for this entity (default = null).      * @param locations HDFS locations      *       (see {@link HdfsLocatedFileStatus#makeQualifiedLocated(URI, Path)})      * @return This Builder instance      */
DECL|method|locations (LocatedBlocks locations)
specifier|public
name|Builder
name|locations
parameter_list|(
name|LocatedBlocks
name|locations
parameter_list|)
block|{
name|this
operator|.
name|locations
operator|=
name|locations
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return An {@link HdfsFileStatus} instance from these parameters.      */
DECL|method|build ()
specifier|public
name|HdfsFileStatus
name|build
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|locations
operator|&&
operator|!
name|isdir
operator|&&
literal|null
operator|==
name|symlink
condition|)
block|{
return|return
operator|new
name|HdfsNamedFileStatus
argument_list|(
name|length
argument_list|,
name|isdir
argument_list|,
name|replication
argument_list|,
name|blocksize
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|,
name|permission
argument_list|,
name|flags
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|symlink
argument_list|,
name|path
argument_list|,
name|fileId
argument_list|,
name|childrenNum
argument_list|,
name|feInfo
argument_list|,
name|storagePolicy
argument_list|,
name|ecPolicy
argument_list|)
return|;
block|}
return|return
operator|new
name|HdfsLocatedFileStatus
argument_list|(
name|length
argument_list|,
name|isdir
argument_list|,
name|replication
argument_list|,
name|blocksize
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|,
name|permission
argument_list|,
name|flags
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|symlink
argument_list|,
name|path
argument_list|,
name|fileId
argument_list|,
name|childrenNum
argument_list|,
name|feInfo
argument_list|,
name|storagePolicy
argument_list|,
name|ecPolicy
argument_list|,
name|locations
argument_list|)
return|;
block|}
block|}
comment|///////////////////
comment|// HDFS-specific //
comment|///////////////////
comment|/**    * Inode ID for this entity, if a file.    * @return inode ID.    */
DECL|method|getFileId ()
name|long
name|getFileId
parameter_list|()
function_decl|;
comment|/**    * Get metadata for encryption, if present.    * @return the {@link FileEncryptionInfo} for this stream, or null if not    *         encrypted.    */
DECL|method|getFileEncryptionInfo ()
name|FileEncryptionInfo
name|getFileEncryptionInfo
parameter_list|()
function_decl|;
comment|/**    * Check if the local name is empty.    * @return true if the name is empty    */
DECL|method|isEmptyLocalName ()
specifier|default
name|boolean
name|isEmptyLocalName
parameter_list|()
block|{
return|return
name|getLocalNameInBytes
argument_list|()
operator|.
name|length
operator|==
literal|0
return|;
block|}
comment|/**    * Get the string representation of the local name.    * @return the local name in string    */
DECL|method|getLocalName ()
specifier|default
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|DFSUtilClient
operator|.
name|bytes2String
argument_list|(
name|getLocalNameInBytes
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the Java UTF8 representation of the local name.    * @return the local name in java UTF8    */
DECL|method|getLocalNameInBytes ()
name|byte
index|[]
name|getLocalNameInBytes
parameter_list|()
function_decl|;
comment|/**    * Get the string representation of the full path name.    * @param parent the parent path    * @return the full path in string    */
DECL|method|getFullName (String parent)
specifier|default
name|String
name|getFullName
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
if|if
condition|(
name|isEmptyLocalName
argument_list|()
condition|)
block|{
return|return
name|parent
return|;
block|}
name|StringBuilder
name|fullName
init|=
operator|new
name|StringBuilder
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|endsWith
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
name|fullName
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
expr_stmt|;
block|}
name|fullName
operator|.
name|append
argument_list|(
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|fullName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the full path.    * @param parent the parent path    * @return the full path    */
DECL|method|getFullPath (Path parent)
specifier|default
name|Path
name|getFullPath
parameter_list|(
name|Path
name|parent
parameter_list|)
block|{
if|if
condition|(
name|isEmptyLocalName
argument_list|()
condition|)
block|{
return|return
name|parent
return|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Opaque referant for the symlink, to be resolved at the client.    */
DECL|method|getSymlinkInBytes ()
name|byte
index|[]
name|getSymlinkInBytes
parameter_list|()
function_decl|;
comment|/**    * @return number of children for this inode.    */
DECL|method|getChildrenNum ()
name|int
name|getChildrenNum
parameter_list|()
function_decl|;
comment|/**    * Get the erasure coding policy if it's set.    * @return the erasure coding policy    */
DECL|method|getErasureCodingPolicy ()
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|()
function_decl|;
comment|/** @return the storage policy id */
DECL|method|getStoragePolicy ()
name|byte
name|getStoragePolicy
parameter_list|()
function_decl|;
comment|/**    * Resolve the short name of the Path given the URI, parent provided. This    * FileStatus reference will not contain a valid Path until it is resolved    * by this method.    * @param defaultUri FileSystem to fully qualify HDFS path.    * @param parent Parent path of this element.    * @return Reference to this instance.    */
DECL|method|makeQualified (URI defaultUri, Path parent)
specifier|default
name|FileStatus
name|makeQualified
parameter_list|(
name|URI
name|defaultUri
parameter_list|,
name|Path
name|parent
parameter_list|)
block|{
comment|// fully-qualify path
name|setPath
argument_list|(
name|getFullPath
argument_list|(
name|parent
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|defaultUri
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|FileStatus
operator|)
name|this
return|;
comment|// API compatibility
block|}
comment|////////////////////////////
comment|// FileStatus "overrides" //
comment|////////////////////////////
comment|/**    * See {@link FileStatus#getPath()}.    */
DECL|method|getPath ()
name|Path
name|getPath
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#setPath(Path)}.    */
DECL|method|setPath (Path p)
name|void
name|setPath
parameter_list|(
name|Path
name|p
parameter_list|)
function_decl|;
comment|/**    * See {@link FileStatus#getLen()}.    */
DECL|method|getLen ()
name|long
name|getLen
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isFile()}.    */
DECL|method|isFile ()
name|boolean
name|isFile
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isDirectory()}.    */
DECL|method|isDirectory ()
name|boolean
name|isDirectory
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isDir()}.    */
DECL|method|isDir ()
name|boolean
name|isDir
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isSymlink()}.    */
DECL|method|isSymlink ()
name|boolean
name|isSymlink
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getBlockSize()}.    */
DECL|method|getBlockSize ()
name|long
name|getBlockSize
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getReplication()}.    */
DECL|method|getReplication ()
name|short
name|getReplication
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getModificationTime()}.    */
DECL|method|getModificationTime ()
name|long
name|getModificationTime
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getAccessTime()}.    */
DECL|method|getAccessTime ()
name|long
name|getAccessTime
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getPermission()}.    */
DECL|method|getPermission ()
name|FsPermission
name|getPermission
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#setPermission(FsPermission)}.    */
DECL|method|setPermission (FsPermission permission)
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
function_decl|;
comment|/**    * See {@link FileStatus#getOwner()}.    */
DECL|method|getOwner ()
name|String
name|getOwner
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#setOwner(String)}.    */
DECL|method|setOwner (String owner)
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
function_decl|;
comment|/**    * See {@link FileStatus#getGroup()}.    */
DECL|method|getGroup ()
name|String
name|getGroup
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#setGroup(String)}.    */
DECL|method|setGroup (String group)
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
comment|/**    * See {@link FileStatus#hasAcl()}.    */
DECL|method|hasAcl ()
name|boolean
name|hasAcl
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isEncrypted()}.    */
DECL|method|isEncrypted ()
name|boolean
name|isEncrypted
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isErasureCoded()}.    */
DECL|method|isErasureCoded ()
name|boolean
name|isErasureCoded
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#isSnapshotEnabled()}.    */
DECL|method|isSnapshotEnabled ()
name|boolean
name|isSnapshotEnabled
parameter_list|()
function_decl|;
comment|/**    * See {@link FileStatus#getSymlink()}.    */
DECL|method|getSymlink ()
name|Path
name|getSymlink
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * See {@link FileStatus#setSymlink(Path sym)}.    */
DECL|method|setSymlink (Path sym)
name|void
name|setSymlink
parameter_list|(
name|Path
name|sym
parameter_list|)
function_decl|;
comment|/**    * See {@link FileStatus#compareTo(FileStatus)}.    */
DECL|method|compareTo (FileStatus stat)
name|int
name|compareTo
parameter_list|(
name|FileStatus
name|stat
parameter_list|)
function_decl|;
comment|/**    * Set redundant flags for compatibility with existing applications.    */
DECL|method|convert (boolean isdir, boolean symlink, FsPermission p, Set<Flags> f)
specifier|static
name|FsPermission
name|convert
parameter_list|(
name|boolean
name|isdir
parameter_list|,
name|boolean
name|symlink
parameter_list|,
name|FsPermission
name|p
parameter_list|,
name|Set
argument_list|<
name|Flags
argument_list|>
name|f
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|FsPermissionExtension
condition|)
block|{
comment|// verify flags are set consistently
assert|assert
name|p
operator|.
name|getAclBit
argument_list|()
operator|==
name|f
operator|.
name|contains
argument_list|(
name|HdfsFileStatus
operator|.
name|Flags
operator|.
name|HAS_ACL
argument_list|)
assert|;
assert|assert
name|p
operator|.
name|getEncryptedBit
argument_list|()
operator|==
name|f
operator|.
name|contains
argument_list|(
name|HdfsFileStatus
operator|.
name|Flags
operator|.
name|HAS_CRYPT
argument_list|)
assert|;
assert|assert
name|p
operator|.
name|getErasureCodedBit
argument_list|()
operator|==
name|f
operator|.
name|contains
argument_list|(
name|HdfsFileStatus
operator|.
name|Flags
operator|.
name|HAS_EC
argument_list|)
assert|;
return|return
name|p
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|p
condition|)
block|{
if|if
condition|(
name|isdir
condition|)
block|{
name|p
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
condition|)
block|{
name|p
operator|=
name|FsPermission
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|p
operator|=
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|FsPermissionExtension
argument_list|(
name|p
argument_list|,
name|f
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_ACL
argument_list|)
argument_list|,
name|f
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_CRYPT
argument_list|)
argument_list|,
name|f
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_EC
argument_list|)
argument_list|)
return|;
block|}
DECL|method|convert (Set<Flags> flags)
specifier|static
name|Set
argument_list|<
name|AttrFlags
argument_list|>
name|convert
parameter_list|(
name|Set
argument_list|<
name|Flags
argument_list|>
name|flags
parameter_list|)
block|{
if|if
condition|(
name|flags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|FileStatus
operator|.
name|NONE
return|;
block|}
name|EnumSet
argument_list|<
name|AttrFlags
argument_list|>
name|attr
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
name|flags
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_ACL
argument_list|)
condition|)
block|{
name|attr
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
name|flags
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_EC
argument_list|)
condition|)
block|{
name|attr
operator|.
name|add
argument_list|(
name|AttrFlags
operator|.
name|HAS_EC
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|flags
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|HAS_CRYPT
argument_list|)
condition|)
block|{
name|attr
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
name|flags
operator|.
name|contains
argument_list|(
name|Flags
operator|.
name|SNAPSHOT_ENABLED
argument_list|)
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
return|return
name|attr
return|;
block|}
block|}
end_interface

end_unit

