begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|oauth2
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
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|Utils
operator|.
name|notNull
import|;
end_import

begin_comment
comment|/**  * Obtain an access token via a a credential (provided through the  * Configuration) using the  *<a href="https://tools.ietf.org/html/rfc6749#section-4.4">  *   Client Credentials Grant workflow</a>.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ConfCredentialBasedAccessTokenProvider
specifier|public
class|class
name|ConfCredentialBasedAccessTokenProvider
extends|extends
name|CredentialBasedAccessTokenProvider
block|{
DECL|field|credential
specifier|private
name|String
name|credential
decl_stmt|;
DECL|method|ConfCredentialBasedAccessTokenProvider ()
specifier|public
name|ConfCredentialBasedAccessTokenProvider
parameter_list|()
block|{   }
DECL|method|ConfCredentialBasedAccessTokenProvider (Timer timer)
specifier|public
name|ConfCredentialBasedAccessTokenProvider
parameter_list|(
name|Timer
name|timer
parameter_list|)
block|{
name|super
argument_list|(
name|timer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|credential
operator|=
name|notNull
argument_list|(
name|conf
argument_list|,
name|OAUTH_CREDENTIAL_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCredential ()
specifier|public
name|String
name|getCredential
parameter_list|()
block|{
if|if
condition|(
name|credential
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Credential has not been "
operator|+
literal|"provided in configuration"
argument_list|)
throw|;
block|}
return|return
name|credential
return|;
block|}
block|}
end_class

end_unit

