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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|NFS3Response
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
name|oncrpc
operator|.
name|RpcInfo
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
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * RPC procedures as defined in RFC 1813.  */
end_comment

begin_interface
DECL|interface|Nfs3Interface
specifier|public
interface|interface
name|Nfs3Interface
block|{
comment|/**    * NULL: Do nothing.    * @return null NFS procedure    */
DECL|method|nullProcedure ()
specifier|public
name|NFS3Response
name|nullProcedure
parameter_list|()
function_decl|;
comment|/**    * GETATTR: Get file attributes.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|getattr (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|getattr
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * SETATTR: Set file attributes.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|setattr (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|setattr
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * LOOKUP: Lookup filename.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|lookup (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|lookup
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * ACCESS: Check access permission.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|access (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|access
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/** READLINK: Read from symbolic link.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|readlink (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|readlink
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * READ: Read from file.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|read (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|read
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * WRITE: Write to file.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|write (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|write
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * CREATE: Create a file.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|create (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|create
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * MKDIR: Create a directory.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|mkdir (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|mkdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * SYMLINK: Create a symbolic link.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|symlink (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|symlink
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * MKNOD: Create a special device.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|mknod (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|mknod
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * REMOVE: Remove a file.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|remove (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|remove
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * RMDIR: Remove a directory.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|rmdir (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|rmdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * RENAME: Rename a file or directory.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|rename (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|rename
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * LINK: create link to an object.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|link (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|link
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * READDIR: Read From directory.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|readdir (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|readdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * READDIRPLUS: Extended read from directory.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|readdirplus (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|readdirplus
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * FSSTAT: Get dynamic file system information.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|fsstat (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|fsstat
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * FSINFO: Get static file system information.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|fsinfo (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|fsinfo
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * PATHCONF: Retrieve POSIX information.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|pathconf (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|pathconf
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * COMMIT: Commit cached data on a server to stable storage.    * @param xdr XDR message    * @param info context of rpc message    * @return NFSv3 response    */
DECL|method|commit (XDR xdr, RpcInfo info)
specifier|public
name|NFS3Response
name|commit
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|RpcInfo
name|info
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

