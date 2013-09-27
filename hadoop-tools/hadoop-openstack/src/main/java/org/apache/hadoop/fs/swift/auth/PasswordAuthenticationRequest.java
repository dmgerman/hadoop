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
comment|/**  * Class that represents authentication request to Openstack Keystone.  * Contains basic authentication information.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|PasswordAuthenticationRequest
specifier|public
class|class
name|PasswordAuthenticationRequest
extends|extends
name|AuthenticationRequest
block|{
comment|/**    * Credentials for login    */
DECL|field|passwordCredentials
specifier|private
name|PasswordCredentials
name|passwordCredentials
decl_stmt|;
comment|/**    * @param tenantName tenant    * @param passwordCredentials password credentials    */
DECL|method|PasswordAuthenticationRequest (String tenantName, PasswordCredentials passwordCredentials)
specifier|public
name|PasswordAuthenticationRequest
parameter_list|(
name|String
name|tenantName
parameter_list|,
name|PasswordCredentials
name|passwordCredentials
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
name|passwordCredentials
operator|=
name|passwordCredentials
expr_stmt|;
block|}
comment|/**    * @return credentials for login into Keystone    */
DECL|method|getPasswordCredentials ()
specifier|public
name|PasswordCredentials
name|getPasswordCredentials
parameter_list|()
block|{
return|return
name|passwordCredentials
return|;
block|}
comment|/**    * @param passwordCredentials credentials for login into Keystone    */
DECL|method|setPasswordCredentials (PasswordCredentials passwordCredentials)
specifier|public
name|void
name|setPasswordCredentials
parameter_list|(
name|PasswordCredentials
name|passwordCredentials
parameter_list|)
block|{
name|this
operator|.
name|passwordCredentials
operator|=
name|passwordCredentials
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
literal|"Authenticate as "
operator|+
literal|"tenant '"
operator|+
name|tenantName
operator|+
literal|"' "
operator|+
name|passwordCredentials
return|;
block|}
block|}
end_class

end_unit

