begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

begin_comment
comment|/**  * Some constants for NFSv3  */
end_comment

begin_class
DECL|class|Nfs3Constant
specifier|public
class|class
name|Nfs3Constant
block|{
comment|// The local rpcbind/portmapper port.
DECL|field|SUN_RPCBIND
specifier|public
specifier|final
specifier|static
name|int
name|SUN_RPCBIND
init|=
literal|111
decl_stmt|;
comment|// The RPC program number for NFS.
DECL|field|PROGRAM
specifier|public
specifier|final
specifier|static
name|int
name|PROGRAM
init|=
literal|100003
decl_stmt|;
comment|// The program version number that this server implements.
DECL|field|VERSION
specifier|public
specifier|final
specifier|static
name|int
name|VERSION
init|=
literal|3
decl_stmt|;
comment|// The procedures
DECL|enum|NFSPROC3
specifier|public
enum|enum
name|NFSPROC3
block|{
comment|// the order of the values below are significant.
DECL|enumConstant|NULL
name|NULL
block|,
DECL|enumConstant|GETATTR
name|GETATTR
block|,
DECL|enumConstant|SETATTR
name|SETATTR
block|,
DECL|enumConstant|LOOKUP
name|LOOKUP
block|,
DECL|enumConstant|ACCESS
name|ACCESS
block|,
DECL|enumConstant|READLINK
name|READLINK
block|,
DECL|enumConstant|READ
name|READ
block|,
DECL|enumConstant|WRITE
name|WRITE
block|,
DECL|enumConstant|CREATE
name|CREATE
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|MKDIR
name|MKDIR
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|SYMLINK
name|SYMLINK
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|MKNOD
name|MKNOD
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|REMOVE
name|REMOVE
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|RMDIR
name|RMDIR
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|RENAME
name|RENAME
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|LINK
name|LINK
argument_list|(
literal|false
argument_list|)
block|,
DECL|enumConstant|READDIR
name|READDIR
block|,
DECL|enumConstant|READDIRPLUS
name|READDIRPLUS
block|,
DECL|enumConstant|FSSTAT
name|FSSTAT
block|,
DECL|enumConstant|FSINFO
name|FSINFO
block|,
DECL|enumConstant|PATHCONF
name|PATHCONF
block|,
DECL|enumConstant|COMMIT
name|COMMIT
block|;
DECL|field|isIdempotent
specifier|private
specifier|final
name|boolean
name|isIdempotent
decl_stmt|;
DECL|method|NFSPROC3 (boolean isIdempotent)
specifier|private
name|NFSPROC3
parameter_list|(
name|boolean
name|isIdempotent
parameter_list|)
block|{
name|this
operator|.
name|isIdempotent
operator|=
name|isIdempotent
expr_stmt|;
block|}
DECL|method|NFSPROC3 ()
specifier|private
name|NFSPROC3
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|isIdempotent ()
specifier|public
name|boolean
name|isIdempotent
parameter_list|()
block|{
return|return
name|isIdempotent
return|;
block|}
comment|/** @return the int value representing the procedure. */
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|ordinal
argument_list|()
return|;
block|}
comment|/**      * Convert to NFS procedure.      * @param value specify the index of NFS procedure      * @return the procedure corresponding to the value.      */
DECL|method|fromValue (int value)
specifier|public
specifier|static
name|NFSPROC3
name|fromValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
operator|||
name|value
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
argument_list|()
index|[
name|value
index|]
return|;
block|}
block|}
comment|// The maximum size in bytes of the opaque file handle.
DECL|field|NFS3_FHSIZE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3_FHSIZE
init|=
literal|64
decl_stmt|;
comment|// The byte size of cookie verifier passed by READDIR and READDIRPLUS.
DECL|field|NFS3_COOKIEVERFSIZE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3_COOKIEVERFSIZE
init|=
literal|8
decl_stmt|;
comment|// The size in bytes of the opaque verifier used for exclusive CREATE.
DECL|field|NFS3_CREATEVERFSIZE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3_CREATEVERFSIZE
init|=
literal|8
decl_stmt|;
comment|// The size in bytes of the opaque verifier used for asynchronous WRITE.
DECL|field|NFS3_WRITEVERFSIZE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3_WRITEVERFSIZE
init|=
literal|8
decl_stmt|;
comment|/** Access call request mode */
comment|// File access mode
DECL|field|ACCESS_MODE_READ
specifier|public
specifier|static
specifier|final
name|int
name|ACCESS_MODE_READ
init|=
literal|0x04
decl_stmt|;
DECL|field|ACCESS_MODE_WRITE
specifier|public
specifier|static
specifier|final
name|int
name|ACCESS_MODE_WRITE
init|=
literal|0x02
decl_stmt|;
DECL|field|ACCESS_MODE_EXECUTE
specifier|public
specifier|static
specifier|final
name|int
name|ACCESS_MODE_EXECUTE
init|=
literal|0x01
decl_stmt|;
comment|/** Access call response rights */
comment|// Read data from file or read a directory.
DECL|field|ACCESS3_READ
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_READ
init|=
literal|0x0001
decl_stmt|;
comment|// Look up a name in a directory (no meaning for non-directory objects).
DECL|field|ACCESS3_LOOKUP
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_LOOKUP
init|=
literal|0x0002
decl_stmt|;
comment|// Rewrite existing file data or modify existing directory entries.
DECL|field|ACCESS3_MODIFY
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_MODIFY
init|=
literal|0x0004
decl_stmt|;
comment|// Write new data or add directory entries.
DECL|field|ACCESS3_EXTEND
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_EXTEND
init|=
literal|0x0008
decl_stmt|;
comment|// Delete an existing directory entry.
DECL|field|ACCESS3_DELETE
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_DELETE
init|=
literal|0x0010
decl_stmt|;
comment|// Execute file (no meaning for a directory).
DECL|field|ACCESS3_EXECUTE
specifier|public
specifier|final
specifier|static
name|int
name|ACCESS3_EXECUTE
init|=
literal|0x0020
decl_stmt|;
comment|/** File and directory attribute mode bits */
comment|// Set user ID on execution.
DECL|field|MODE_S_ISUID
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_ISUID
init|=
literal|0x00800
decl_stmt|;
comment|// Set group ID on execution.
DECL|field|MODE_S_ISGID
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_ISGID
init|=
literal|0x00400
decl_stmt|;
comment|// Save swapped text (not defined in POSIX).
DECL|field|MODE_S_ISVTX
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_ISVTX
init|=
literal|0x00200
decl_stmt|;
comment|// Read permission for owner.
DECL|field|MODE_S_IRUSR
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IRUSR
init|=
literal|0x00100
decl_stmt|;
comment|// Write permission for owner.
DECL|field|MODE_S_IWUSR
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IWUSR
init|=
literal|0x00080
decl_stmt|;
comment|// Execute permission for owner on a file. Or lookup (search) permission for
comment|// owner in directory.
DECL|field|MODE_S_IXUSR
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IXUSR
init|=
literal|0x00040
decl_stmt|;
comment|// Read permission for group.
DECL|field|MODE_S_IRGRP
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IRGRP
init|=
literal|0x00020
decl_stmt|;
comment|// Write permission for group.
DECL|field|MODE_S_IWGRP
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IWGRP
init|=
literal|0x00010
decl_stmt|;
comment|// Execute permission for group on a file. Or lookup (search) permission for
comment|// group in directory.
DECL|field|MODE_S_IXGRP
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IXGRP
init|=
literal|0x00008
decl_stmt|;
comment|// Read permission for others.
DECL|field|MODE_S_IROTH
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IROTH
init|=
literal|0x00004
decl_stmt|;
comment|// Write permission for others.
DECL|field|MODE_S_IWOTH
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IWOTH
init|=
literal|0x00002
decl_stmt|;
comment|// Execute permission for others on a file. Or lookup (search) permission for
comment|// others in directory.
DECL|field|MODE_S_IXOTH
specifier|public
specifier|final
specifier|static
name|int
name|MODE_S_IXOTH
init|=
literal|0x00001
decl_stmt|;
DECL|field|MODE_ALL
specifier|public
specifier|final
specifier|static
name|int
name|MODE_ALL
init|=
name|MODE_S_ISUID
operator||
name|MODE_S_ISGID
operator||
name|MODE_S_ISVTX
operator||
name|MODE_S_ISVTX
operator||
name|MODE_S_IRUSR
operator||
name|MODE_S_IRUSR
operator||
name|MODE_S_IWUSR
operator||
name|MODE_S_IXUSR
operator||
name|MODE_S_IRGRP
operator||
name|MODE_S_IWGRP
operator||
name|MODE_S_IXGRP
operator||
name|MODE_S_IROTH
operator||
name|MODE_S_IWOTH
operator||
name|MODE_S_IXOTH
decl_stmt|;
comment|/** Write call flavors */
DECL|enum|WriteStableHow
specifier|public
enum|enum
name|WriteStableHow
block|{
comment|// the order of the values below are significant.
DECL|enumConstant|UNSTABLE
name|UNSTABLE
block|,
DECL|enumConstant|DATA_SYNC
name|DATA_SYNC
block|,
DECL|enumConstant|FILE_SYNC
name|FILE_SYNC
block|;
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|ordinal
argument_list|()
return|;
block|}
DECL|method|fromValue (int id)
specifier|public
specifier|static
name|WriteStableHow
name|fromValue
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|values
argument_list|()
index|[
name|id
index|]
return|;
block|}
block|}
comment|/**    * This is a cookie that the client can use to determine whether the server    * has changed state between a call to WRITE and a subsequent call to either    * WRITE or COMMIT. This cookie must be consistent during a single instance of    * the NFS version 3 protocol service and must be unique between instances of    * the NFS version 3 protocol server, where uncommitted data may be lost.    */
DECL|field|WRITE_COMMIT_VERF
specifier|public
specifier|final
specifier|static
name|long
name|WRITE_COMMIT_VERF
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/** FileSystemProperties */
DECL|field|FSF3_LINK
specifier|public
specifier|final
specifier|static
name|int
name|FSF3_LINK
init|=
literal|0x0001
decl_stmt|;
DECL|field|FSF3_SYMLINK
specifier|public
specifier|final
specifier|static
name|int
name|FSF3_SYMLINK
init|=
literal|0x0002
decl_stmt|;
DECL|field|FSF3_HOMOGENEOUS
specifier|public
specifier|final
specifier|static
name|int
name|FSF3_HOMOGENEOUS
init|=
literal|0x0008
decl_stmt|;
DECL|field|FSF3_CANSETTIME
specifier|public
specifier|final
specifier|static
name|int
name|FSF3_CANSETTIME
init|=
literal|0x0010
decl_stmt|;
comment|/** Create options */
DECL|field|CREATE_UNCHECKED
specifier|public
specifier|final
specifier|static
name|int
name|CREATE_UNCHECKED
init|=
literal|0
decl_stmt|;
DECL|field|CREATE_GUARDED
specifier|public
specifier|final
specifier|static
name|int
name|CREATE_GUARDED
init|=
literal|1
decl_stmt|;
DECL|field|CREATE_EXCLUSIVE
specifier|public
specifier|final
specifier|static
name|int
name|CREATE_EXCLUSIVE
init|=
literal|2
decl_stmt|;
comment|/** Size for nfs exports cache */
DECL|field|NFS_EXPORTS_CACHE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NFS_EXPORTS_CACHE_SIZE_KEY
init|=
literal|"nfs.exports.cache.size"
decl_stmt|;
DECL|field|NFS_EXPORTS_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NFS_EXPORTS_CACHE_SIZE_DEFAULT
init|=
literal|512
decl_stmt|;
comment|/** Expiration time for nfs exports cache entry */
DECL|field|NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_KEY
init|=
literal|"nfs.exports.cache.expirytime.millis"
decl_stmt|;
DECL|field|NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|NFS_EXPORTS_CACHE_EXPIRYTIME_MILLIS_DEFAULT
init|=
literal|15
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 15 min
block|}
end_class

end_unit

