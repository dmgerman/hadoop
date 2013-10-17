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

begin_comment
comment|/**  * Class that represents authentication to OpenStack Keystone.  * Contains basic authentication information.  * Used when {@link ApiKeyAuthenticationRequest} is not applicable.  * (problem with different Keystone installations/versions/modifications)  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|KeyStoneAuthRequest
specifier|public
class|class
name|KeyStoneAuthRequest
extends|extends
name|AuthenticationRequest
block|{
comment|/**    * Credentials for Keystone authentication    */
DECL|field|apiAccessKeyCredentials
specifier|private
name|KeystoneApiKeyCredentials
name|apiAccessKeyCredentials
decl_stmt|;
comment|/**    * @param tenant                  Keystone tenant name for authentication    * @param apiAccessKeyCredentials Credentials for authentication    */
DECL|method|KeyStoneAuthRequest (String tenant, KeystoneApiKeyCredentials apiAccessKeyCredentials)
specifier|public
name|KeyStoneAuthRequest
parameter_list|(
name|String
name|tenant
parameter_list|,
name|KeystoneApiKeyCredentials
name|apiAccessKeyCredentials
parameter_list|)
block|{
name|this
operator|.
name|apiAccessKeyCredentials
operator|=
name|apiAccessKeyCredentials
expr_stmt|;
name|this
operator|.
name|tenantName
operator|=
name|tenant
expr_stmt|;
block|}
DECL|method|getApiAccessKeyCredentials ()
specifier|public
name|KeystoneApiKeyCredentials
name|getApiAccessKeyCredentials
parameter_list|()
block|{
return|return
name|apiAccessKeyCredentials
return|;
block|}
DECL|method|setApiAccessKeyCredentials (KeystoneApiKeyCredentials apiAccessKeyCredentials)
specifier|public
name|void
name|setApiAccessKeyCredentials
parameter_list|(
name|KeystoneApiKeyCredentials
name|apiAccessKeyCredentials
parameter_list|)
block|{
name|this
operator|.
name|apiAccessKeyCredentials
operator|=
name|apiAccessKeyCredentials
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
literal|"KeyStoneAuthRequest as "
operator|+
literal|"tenant '"
operator|+
name|tenantName
operator|+
literal|"' "
operator|+
name|apiAccessKeyCredentials
return|;
block|}
block|}
end_class

end_unit

