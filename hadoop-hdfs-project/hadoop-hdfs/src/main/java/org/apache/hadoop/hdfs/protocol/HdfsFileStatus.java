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
name|net
operator|.
name|URI
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
name|DFSUtil
import|;
end_import

begin_comment
comment|/** Interface that represents the over the wire information for a file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HdfsFileStatus
specifier|public
class|class
name|HdfsFileStatus
block|{
DECL|field|path
specifier|private
specifier|final
name|byte
index|[]
name|path
decl_stmt|;
comment|// local name of the inode that's encoded in java UTF8
DECL|field|symlink
specifier|private
specifier|final
name|byte
index|[]
name|symlink
decl_stmt|;
comment|// symlink target encoded in java UTF8 or null
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|isdir
specifier|private
specifier|final
name|boolean
name|isdir
decl_stmt|;
DECL|field|block_replication
specifier|private
specifier|final
name|short
name|block_replication
decl_stmt|;
DECL|field|blocksize
specifier|private
specifier|final
name|long
name|blocksize
decl_stmt|;
DECL|field|modification_time
specifier|private
specifier|final
name|long
name|modification_time
decl_stmt|;
DECL|field|access_time
specifier|private
specifier|final
name|long
name|access_time
decl_stmt|;
DECL|field|permission
specifier|private
specifier|final
name|FsPermission
name|permission
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|String
name|group
decl_stmt|;
DECL|field|fileId
specifier|private
specifier|final
name|long
name|fileId
decl_stmt|;
DECL|field|feInfo
specifier|private
specifier|final
name|FileEncryptionInfo
name|feInfo
decl_stmt|;
comment|// Used by dir, not including dot and dotdot. Always zero for a regular file.
DECL|field|childrenNum
specifier|private
specifier|final
name|int
name|childrenNum
decl_stmt|;
DECL|field|EMPTY_NAME
specifier|public
specifier|static
specifier|final
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
comment|/**    * Constructor    * @param length the number of bytes the file has    * @param isdir if the path is a directory    * @param block_replication the replication factor    * @param blocksize the block size    * @param modification_time modification time    * @param access_time access time    * @param permission permission    * @param owner the owner of the path    * @param group the group of the path    * @param path the local name in java UTF8 encoding the same as that in-memory    * @param fileId the file id    * @param feInfo the file's encryption info    */
DECL|method|HdfsFileStatus (long length, boolean isdir, int block_replication, long blocksize, long modification_time, long access_time, FsPermission permission, String owner, String group, byte[] symlink, byte[] path, long fileId, int childrenNum, FileEncryptionInfo feInfo)
specifier|public
name|HdfsFileStatus
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
name|byte
index|[]
name|symlink
parameter_list|,
name|byte
index|[]
name|path
parameter_list|,
name|long
name|fileId
parameter_list|,
name|int
name|childrenNum
parameter_list|,
name|FileEncryptionInfo
name|feInfo
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
operator|(
operator|(
name|isdir
operator|||
name|symlink
operator|!=
literal|null
operator|)
condition|?
name|FsPermission
operator|.
name|getDefault
argument_list|()
else|:
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
operator|)
else|:
name|permission
expr_stmt|;
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
name|this
operator|.
name|fileId
operator|=
name|fileId
expr_stmt|;
name|this
operator|.
name|childrenNum
operator|=
name|childrenNum
expr_stmt|;
name|this
operator|.
name|feInfo
operator|=
name|feInfo
expr_stmt|;
block|}
comment|/**    * Get the length of this file, in bytes.    * @return the length of this file, in bytes.    */
DECL|method|getLen ()
specifier|public
specifier|final
name|long
name|getLen
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * Is this a directory?    * @return true if this is a directory    */
DECL|method|isDir ()
specifier|public
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
name|access_time
return|;
block|}
comment|/**    * Get FsPermission associated with the file.    * @return permssion    */
DECL|method|getPermission ()
specifier|public
specifier|final
name|FsPermission
name|getPermission
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
comment|/**    * Get the owner of the file.    * @return owner of the file    */
DECL|method|getOwner ()
specifier|public
specifier|final
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Get the group associated with the file.    * @return group for the file.     */
DECL|method|getGroup ()
specifier|public
specifier|final
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
comment|/**    * Check if the local name is empty    * @return true if the name is empty    */
DECL|method|isEmptyLocalName ()
specifier|public
specifier|final
name|boolean
name|isEmptyLocalName
parameter_list|()
block|{
return|return
name|path
operator|.
name|length
operator|==
literal|0
return|;
block|}
comment|/**    * Get the string representation of the local name    * @return the local name in string    */
DECL|method|getLocalName ()
specifier|public
specifier|final
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Get the Java UTF8 representation of the local name    * @return the local name in java UTF8    */
DECL|method|getLocalNameInBytes ()
specifier|public
specifier|final
name|byte
index|[]
name|getLocalNameInBytes
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**    * Get the string representation of the full path name    * @param parent the parent path    * @return the full path in string    */
DECL|method|getFullName (final String parent)
specifier|public
specifier|final
name|String
name|getFullName
parameter_list|(
specifier|final
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
comment|/**    * Get the full path    * @param parent the parent path    * @return the full path    */
DECL|method|getFullPath (final Path parent)
specifier|public
specifier|final
name|Path
name|getFullPath
parameter_list|(
specifier|final
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
comment|/**    * Get the string representation of the symlink.    * @return the symlink as a string.    */
DECL|method|getSymlink ()
specifier|public
specifier|final
name|String
name|getSymlink
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|symlink
argument_list|)
return|;
block|}
DECL|method|getSymlinkInBytes ()
specifier|public
specifier|final
name|byte
index|[]
name|getSymlinkInBytes
parameter_list|()
block|{
return|return
name|symlink
return|;
block|}
DECL|method|getFileId ()
specifier|public
specifier|final
name|long
name|getFileId
parameter_list|()
block|{
return|return
name|fileId
return|;
block|}
DECL|method|getFileEncryptionInfo ()
specifier|public
specifier|final
name|FileEncryptionInfo
name|getFileEncryptionInfo
parameter_list|()
block|{
return|return
name|feInfo
return|;
block|}
DECL|method|getChildrenNum ()
specifier|public
specifier|final
name|int
name|getChildrenNum
parameter_list|()
block|{
return|return
name|childrenNum
return|;
block|}
DECL|method|makeQualified (URI defaultUri, Path path)
specifier|public
specifier|final
name|FileStatus
name|makeQualified
parameter_list|(
name|URI
name|defaultUri
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
name|getLen
argument_list|()
argument_list|,
name|isDir
argument_list|()
argument_list|,
name|getReplication
argument_list|()
argument_list|,
name|getBlockSize
argument_list|()
argument_list|,
name|getModificationTime
argument_list|()
argument_list|,
name|getAccessTime
argument_list|()
argument_list|,
name|getPermission
argument_list|()
argument_list|,
name|getOwner
argument_list|()
argument_list|,
name|getGroup
argument_list|()
argument_list|,
name|isSymlink
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|getSymlink
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
operator|(
name|getFullPath
argument_list|(
name|path
argument_list|)
operator|)
operator|.
name|makeQualified
argument_list|(
name|defaultUri
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
comment|// fully-qualify path
block|}
block|}
end_class

end_unit

