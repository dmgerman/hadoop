begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|auth
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_comment
comment|/**  * Class that represents authentication request to Openstack Keystone.  * Contains basic authentication information.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS  */
end_comment

begin_class
DECL|class|ApiKeyAuthenticationRequest
specifier|public
class|class
name|ApiKeyAuthenticationRequest
extends|extends
name|AuthenticationRequest
block|{
comment|/**    * Credentials for login    */
DECL|field|apiKeyCredentials
specifier|private
name|ApiKeyCredentials
name|apiKeyCredentials
decl_stmt|;
comment|/**    * API key auth    * @param tenantName tenant    * @param apiKeyCredentials credentials    */
DECL|method|ApiKeyAuthenticationRequest (String tenantName, ApiKeyCredentials apiKeyCredentials)
specifier|public
name|ApiKeyAuthenticationRequest
parameter_list|(
name|String
name|tenantName
parameter_list|,
name|ApiKeyCredentials
name|apiKeyCredentials
parameter_list|)
block|{
name|this
operator|.
name|tenantName
operator|=
name|tenantName
expr_stmt|;
name|this
operator|.
name|apiKeyCredentials
operator|=
name|apiKeyCredentials
expr_stmt|;
block|}
comment|/**    * @return credentials for login into Keystone    */
annotation|@
name|JsonProperty
argument_list|(
literal|"RAX-KSKEY:apiKeyCredentials"
argument_list|)
DECL|method|getApiKeyCredentials ()
specifier|public
name|ApiKeyCredentials
name|getApiKeyCredentials
parameter_list|()
block|{
return|return
name|apiKeyCredentials
return|;
block|}
comment|/**    * @param apiKeyCredentials credentials for login into Keystone    */
DECL|method|setApiKeyCredentials (ApiKeyCredentials apiKeyCredentials)
specifier|public
name|void
name|setApiKeyCredentials
parameter_list|(
name|ApiKeyCredentials
name|apiKeyCredentials
parameter_list|)
block|{
name|this
operator|.
name|apiKeyCredentials
operator|=
name|apiKeyCredentials
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Auth as "
operator|+
literal|"tenant '"
operator|+
name|tenantName
operator|+
literal|"' "
operator|+
name|apiKeyCredentials
return|;
block|}
block|}
end_class

end_unit

