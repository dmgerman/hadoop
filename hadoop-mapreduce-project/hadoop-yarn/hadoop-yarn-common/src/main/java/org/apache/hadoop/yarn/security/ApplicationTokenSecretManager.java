begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|SecretManager
import|;
end_import

begin_class
DECL|class|ApplicationTokenSecretManager
specifier|public
class|class
name|ApplicationTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
block|{
comment|// TODO: mark as final
DECL|field|masterKey
specifier|private
name|SecretKey
name|masterKey
decl_stmt|;
comment|// For now only one masterKey, for ever.
comment|// TODO: add expiry for masterKey
comment|// TODO: add logic to handle with multiple masterKeys, only one being used for
comment|// creating new tokens at any time.
comment|// TODO: Make he masterKey more secure, non-transferrable etc.
comment|/**    * Default constructor    */
DECL|method|ApplicationTokenSecretManager ()
specifier|public
name|ApplicationTokenSecretManager
parameter_list|()
block|{
name|this
operator|.
name|masterKey
operator|=
name|generateSecret
argument_list|()
expr_stmt|;
block|}
comment|// TODO: this should go away.
DECL|method|setMasterKey (SecretKey mk)
specifier|public
name|void
name|setMasterKey
parameter_list|(
name|SecretKey
name|mk
parameter_list|)
block|{
name|this
operator|.
name|masterKey
operator|=
name|mk
expr_stmt|;
block|}
comment|// TODO: this should go away.
DECL|method|getMasterKey ()
specifier|public
name|SecretKey
name|getMasterKey
parameter_list|()
block|{
return|return
name|masterKey
return|;
block|}
comment|/**    * Convert the byte[] to a secret key    * @param key the byte[] to create the secret key from    * @return the secret key    */
DECL|method|createSecretKey (byte[] key)
specifier|public
specifier|static
name|SecretKey
name|createSecretKey
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
return|return
name|SecretManager
operator|.
name|createSecretKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createPassword (ApplicationTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|createPassword
parameter_list|(
name|ApplicationTokenIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|masterKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|retrievePassword (ApplicationTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|ApplicationTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|SecretManager
operator|.
name|InvalidToken
block|{
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|masterKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|ApplicationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|ApplicationTokenIdentifier
argument_list|()
return|;
block|}
block|}
end_class

end_unit

