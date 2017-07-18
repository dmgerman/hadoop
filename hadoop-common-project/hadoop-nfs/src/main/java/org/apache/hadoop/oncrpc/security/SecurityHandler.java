begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.oncrpc.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
operator|.
name|security
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
operator|.
name|RpcCall
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|SecurityHandler
specifier|public
specifier|abstract
class|class
name|SecurityHandler
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SecurityHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|shouldSilentlyDrop (RpcCall request)
specifier|public
specifier|abstract
name|boolean
name|shouldSilentlyDrop
parameter_list|(
name|RpcCall
name|request
parameter_list|)
function_decl|;
DECL|method|getVerifer (RpcCall request)
specifier|public
specifier|abstract
name|Verifier
name|getVerifer
parameter_list|(
name|RpcCall
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|isUnwrapRequired ()
specifier|public
name|boolean
name|isUnwrapRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|isWrapRequired ()
specifier|public
name|boolean
name|isWrapRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Used by GSS.    * @param request RPC request    * @param data request data    * @throws IOException fail to unwrap RPC call    * @return XDR response    */
DECL|method|unwrap (RpcCall request, byte[] data )
specifier|public
name|XDR
name|unwrap
parameter_list|(
name|RpcCall
name|request
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Used by GSS.    * @param request RPC request    * @param response RPC response    * @throws IOException fail to wrap RPC call    * @return response byte buffer    */
DECL|method|wrap (RpcCall request, XDR response)
specifier|public
name|byte
index|[]
name|wrap
parameter_list|(
name|RpcCall
name|request
parameter_list|,
name|XDR
name|response
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Used by AUTH_SYS.    * Return the uid of the NFS user credential.    * @return uid    */
DECL|method|getUid ()
specifier|public
name|int
name|getUid
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Used by AUTH_SYS.    * Return the gid of the NFS user credential.    * @return gid    */
DECL|method|getGid ()
specifier|public
name|int
name|getGid
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Used by AUTH_SYS.    * Return the auxiliary gids of the NFS user credential.    * @return auxiliary gids    */
DECL|method|getAuxGids ()
specifier|public
name|int
index|[]
name|getAuxGids
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

