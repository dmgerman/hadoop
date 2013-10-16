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
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|XDR
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
name|security
operator|.
name|SecurityHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
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
comment|/** NULL: Do nothing */
DECL|method|nullProcedure ()
specifier|public
name|NFS3Response
name|nullProcedure
parameter_list|()
function_decl|;
comment|/** GETATTR: Get file attributes */
DECL|method|getattr (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|getattr
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** SETATTR: Set file attributes */
DECL|method|setattr (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|setattr
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** LOOKUP: Lookup filename */
DECL|method|lookup (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|lookup
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** ACCESS: Check access permission */
DECL|method|access (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|access
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** READ: Read from file */
DECL|method|read (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|read
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** WRITE: Write to file */
DECL|method|write (XDR xdr, Channel channel, int xid, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|write
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|int
name|xid
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** CREATE: Create a file */
DECL|method|create (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|create
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** MKDIR: Create a directory */
DECL|method|mkdir (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|mkdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** REMOVE: Remove a file */
DECL|method|remove (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|remove
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** RMDIR: Remove a directory */
DECL|method|rmdir (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|rmdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** RENAME: Rename a file or directory */
DECL|method|rename (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|rename
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** SYMLINK: Create a symbolic link */
DECL|method|symlink (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|symlink
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** READDIR: Read From directory */
DECL|method|readdir (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|readdir
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** FSSTAT: Get dynamic file system information */
DECL|method|fsstat (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|fsstat
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** FSINFO: Get static file system information */
DECL|method|fsinfo (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|fsinfo
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** PATHCONF: Retrieve POSIX information */
DECL|method|pathconf (XDR xdr, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|pathconf
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
comment|/** COMMIT: Commit cached data on a server to stable storage */
DECL|method|commit (XDR xdr, Channel channel, int xid, SecurityHandler securityHandler, InetAddress client)
specifier|public
name|NFS3Response
name|commit
parameter_list|(
name|XDR
name|xdr
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|int
name|xid
parameter_list|,
name|SecurityHandler
name|securityHandler
parameter_list|,
name|InetAddress
name|client
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

