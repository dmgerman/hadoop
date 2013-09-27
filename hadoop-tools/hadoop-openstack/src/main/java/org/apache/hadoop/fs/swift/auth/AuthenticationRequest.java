begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
DECL|class|AuthenticationRequest
specifier|public
class|class
name|AuthenticationRequest
block|{
comment|/**    * tenant name    */
DECL|field|tenantName
specifier|protected
name|String
name|tenantName
decl_stmt|;
DECL|method|AuthenticationRequest ()
specifier|public
name|AuthenticationRequest
parameter_list|()
block|{   }
comment|/**    * @return tenant name for Keystone authorization    */
DECL|method|getTenantName ()
specifier|public
name|String
name|getTenantName
parameter_list|()
block|{
return|return
name|tenantName
return|;
block|}
comment|/**    * @param tenantName tenant name for authorization    */
DECL|method|setTenantName (String tenantName)
specifier|public
name|void
name|setTenantName
parameter_list|(
name|String
name|tenantName
parameter_list|)
block|{
name|this
operator|.
name|tenantName
operator|=
name|tenantName
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
literal|"AuthenticationRequest{"
operator|+
literal|"tenantName='"
operator|+
name|tenantName
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

