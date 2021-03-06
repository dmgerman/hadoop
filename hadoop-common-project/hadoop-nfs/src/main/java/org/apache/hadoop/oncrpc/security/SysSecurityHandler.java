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
name|Nfs3Constant
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
name|security
operator|.
name|IdMappingConstant
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
name|security
operator|.
name|IdMappingServiceProvider
import|;
end_import

begin_class
DECL|class|SysSecurityHandler
specifier|public
class|class
name|SysSecurityHandler
extends|extends
name|SecurityHandler
block|{
DECL|field|iug
specifier|private
specifier|final
name|IdMappingServiceProvider
name|iug
decl_stmt|;
DECL|field|mCredentialsSys
specifier|private
specifier|final
name|CredentialsSys
name|mCredentialsSys
decl_stmt|;
DECL|method|SysSecurityHandler (CredentialsSys credentialsSys, IdMappingServiceProvider iug)
specifier|public
name|SysSecurityHandler
parameter_list|(
name|CredentialsSys
name|credentialsSys
parameter_list|,
name|IdMappingServiceProvider
name|iug
parameter_list|)
block|{
name|this
operator|.
name|mCredentialsSys
operator|=
name|credentialsSys
expr_stmt|;
name|this
operator|.
name|iug
operator|=
name|iug
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|iug
operator|.
name|getUserName
argument_list|(
name|mCredentialsSys
operator|.
name|getUID
argument_list|()
argument_list|,
name|IdMappingConstant
operator|.
name|UNKNOWN_USER
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shouldSilentlyDrop (RpcCall request)
specifier|public
name|boolean
name|shouldSilentlyDrop
parameter_list|(
name|RpcCall
name|request
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getVerifer (RpcCall request)
specifier|public
name|VerifierNone
name|getVerifer
parameter_list|(
name|RpcCall
name|request
parameter_list|)
block|{
return|return
operator|new
name|VerifierNone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUid ()
specifier|public
name|int
name|getUid
parameter_list|()
block|{
return|return
name|mCredentialsSys
operator|.
name|getUID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getGid ()
specifier|public
name|int
name|getGid
parameter_list|()
block|{
return|return
name|mCredentialsSys
operator|.
name|getGID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAuxGids ()
specifier|public
name|int
index|[]
name|getAuxGids
parameter_list|()
block|{
return|return
name|mCredentialsSys
operator|.
name|getAuxGIDs
argument_list|()
return|;
block|}
block|}
end_class

end_unit

