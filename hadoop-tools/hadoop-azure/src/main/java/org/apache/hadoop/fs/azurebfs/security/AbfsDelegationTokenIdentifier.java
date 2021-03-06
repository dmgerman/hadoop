begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenIdentifier
import|;
end_import

begin_comment
comment|/**  * Delegation token Identifier for ABFS delegation tokens.  * The token kind from {@link #getKind()} is {@link #TOKEN_KIND}, always.  *  * Subclasses have to very careful when looking up tokens (which will of  * course be registered in the credentials as of this kind), in case the  * incoming credentials are actually of a different subtype.  */
end_comment

begin_class
DECL|class|AbfsDelegationTokenIdentifier
specifier|public
class|class
name|AbfsDelegationTokenIdentifier
extends|extends
name|DelegationTokenIdentifier
block|{
comment|/**    * The token kind of these tokens: ""ABFS delegation".    */
DECL|field|TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
literal|"ABFS delegation"
argument_list|)
decl_stmt|;
DECL|method|AbfsDelegationTokenIdentifier ()
specifier|public
name|AbfsDelegationTokenIdentifier
parameter_list|()
block|{
name|super
argument_list|(
name|TOKEN_KIND
argument_list|)
expr_stmt|;
block|}
DECL|method|AbfsDelegationTokenIdentifier (Text kind)
specifier|public
name|AbfsDelegationTokenIdentifier
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
argument_list|)
expr_stmt|;
block|}
DECL|method|AbfsDelegationTokenIdentifier (Text kind, Text owner, Text renewer, Text realUser)
specifier|public
name|AbfsDelegationTokenIdentifier
parameter_list|(
name|Text
name|kind
parameter_list|,
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
name|super
argument_list|(
name|kind
argument_list|,
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the token kind.    * Returns {@link #TOKEN_KIND} always.    * If a subclass does not want its renew/cancel process to be managed    * by {@link AbfsDelegationTokenManager}, this must be overridden.    * @return the kind of the token.    */
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|TOKEN_KIND
return|;
block|}
block|}
end_class

end_unit

