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
comment|/**  * Success or error status is reported in NFS3 responses.  */
end_comment

begin_class
DECL|class|Nfs3Status
specifier|public
class|class
name|Nfs3Status
block|{
comment|/** Indicates the call completed successfully. */
DECL|field|NFS3_OK
specifier|public
specifier|final
specifier|static
name|int
name|NFS3_OK
init|=
literal|0
decl_stmt|;
comment|/**    * The operation was not allowed because the caller is either not a    * privileged user (root) or not the owner of the target of the operation.    */
DECL|field|NFS3ERR_PERM
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_PERM
init|=
literal|1
decl_stmt|;
comment|/**    * No such file or directory. The file or directory name specified does not    * exist.    */
DECL|field|NFS3ERR_NOENT
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NOENT
init|=
literal|2
decl_stmt|;
comment|/**    * I/O error. A hard error (for example, a disk error) occurred while    * processing the requested operation.    */
DECL|field|NFS3ERR_IO
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_IO
init|=
literal|5
decl_stmt|;
comment|/** I/O error. No such device or address. */
DECL|field|NFS3ERR_NXIO
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NXIO
init|=
literal|6
decl_stmt|;
comment|/**    * Permission denied. The caller does not have the correct permission to    * perform the requested operation. Contrast this with NFS3ERR_PERM, which    * restricts itself to owner or privileged user permission failures.    */
DECL|field|NFS3ERR_ACCES
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_ACCES
init|=
literal|13
decl_stmt|;
comment|/** File exists. The file specified already exists. */
DECL|field|NFS3ERR_EXIST
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_EXIST
init|=
literal|17
decl_stmt|;
comment|/** Attempt to do a cross-device hard link. */
DECL|field|NFS3ERR_XDEV
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_XDEV
init|=
literal|18
decl_stmt|;
comment|/** No such device. */
DECL|field|NFS3ERR_NODEV
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NODEV
init|=
literal|19
decl_stmt|;
comment|/** The caller specified a non-directory in a directory operation. */
DECL|field|NFS3ERR_NOTDIR
specifier|public
specifier|static
name|int
name|NFS3ERR_NOTDIR
init|=
literal|20
decl_stmt|;
comment|/** The caller specified a directory in a non-directory operation. */
DECL|field|NFS3ERR_ISDIR
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_ISDIR
init|=
literal|21
decl_stmt|;
comment|/**    * Invalid argument or unsupported argument for an operation. Two examples are    * attempting a READLINK on an object other than a symbolic link or attempting    * to SETATTR a time field on a server that does not support this operation.    */
DECL|field|NFS3ERR_INVAL
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_INVAL
init|=
literal|22
decl_stmt|;
comment|/**    * File too large. The operation would have caused a file to grow beyond the    * server's limit.    */
DECL|field|NFS3ERR_FBIG
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_FBIG
init|=
literal|27
decl_stmt|;
comment|/**    * No space left on device. The operation would have caused the server's file    * system to exceed its limit.    */
DECL|field|NFS3ERR_NOSPC
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NOSPC
init|=
literal|28
decl_stmt|;
comment|/**    * Read-only file system. A modifying operation was attempted on a read-only    * file system.    */
DECL|field|NFS3ERR_ROFS
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_ROFS
init|=
literal|30
decl_stmt|;
comment|/** Too many hard links. */
DECL|field|NFS3ERR_MLINK
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_MLINK
init|=
literal|31
decl_stmt|;
comment|/** The filename in an operation was too long. */
DECL|field|NFS3ERR_NAMETOOLONG
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NAMETOOLONG
init|=
literal|63
decl_stmt|;
comment|/** An attempt was made to remove a directory that was not empty. */
DECL|field|NFS3ERR_NOTEMPTY
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NOTEMPTY
init|=
literal|66
decl_stmt|;
comment|/**    * Resource (quota) hard limit exceeded. The user's resource limit on the    * server has been exceeded.    */
DECL|field|NFS3ERR_DQUOT
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_DQUOT
init|=
literal|69
decl_stmt|;
comment|/**    * The file handle given in the arguments was invalid. The file referred to by    * that file handle no longer exists or access to it has been revoked.    */
DECL|field|NFS3ERR_STALE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_STALE
init|=
literal|70
decl_stmt|;
comment|/**    * The file handle given in the arguments referred to a file on a non-local    * file system on the server.    */
DECL|field|NFS3ERR_REMOTE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_REMOTE
init|=
literal|71
decl_stmt|;
comment|/** The file handle failed internal consistency checks */
DECL|field|NFS3ERR_BADHANDLE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_BADHANDLE
init|=
literal|10001
decl_stmt|;
comment|/**    * Update synchronization mismatch was detected during a SETATTR operation.    */
DECL|field|NFS3ERR_NOT_SYNC
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NOT_SYNC
init|=
literal|10002
decl_stmt|;
comment|/** READDIR or READDIRPLUS cookie is stale */
DECL|field|NFS3ERR_BAD_COOKIE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_BAD_COOKIE
init|=
literal|10003
decl_stmt|;
comment|/** Operation is not supported */
DECL|field|NFS3ERR_NOTSUPP
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_NOTSUPP
init|=
literal|10004
decl_stmt|;
comment|/** Buffer or request is too small */
DECL|field|NFS3ERR_TOOSMALL
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_TOOSMALL
init|=
literal|10005
decl_stmt|;
comment|/**    * An error occurred on the server which does not map to any of the legal NFS    * version 3 protocol error values. The client should translate this into an    * appropriate error. UNIX clients may choose to translate this to EIO.    */
DECL|field|NFS3ERR_SERVERFAULT
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_SERVERFAULT
init|=
literal|10006
decl_stmt|;
comment|/**    * An attempt was made to create an object of a type not supported by the    * server.    */
DECL|field|NFS3ERR_BADTYPE
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_BADTYPE
init|=
literal|10007
decl_stmt|;
comment|/**    * The server initiated the request, but was not able to complete it in a    * timely fashion. The client should wait and then try the request with a new    * RPC transaction ID. For example, this error should be returned from a    * server that supports hierarchical storage and receives a request to process    * a file that has been migrated. In this case, the server should start the    * immigration process and respond to client with this error.    */
DECL|field|NFS3ERR_JUKEBOX
specifier|public
specifier|final
specifier|static
name|int
name|NFS3ERR_JUKEBOX
init|=
literal|10008
decl_stmt|;
block|}
end_class

end_unit

