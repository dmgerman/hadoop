begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Describes user roles in Openstack system.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|Roles
specifier|public
class|class
name|Roles
block|{
comment|/**    * role name    */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * This field user in RackSpace auth model    */
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/**    * This field user in RackSpace auth model    */
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
comment|/**    * Service id used in HP public Cloud    */
DECL|field|serviceId
specifier|private
name|String
name|serviceId
decl_stmt|;
comment|/**    * Service id used in HP public Cloud    */
DECL|field|tenantId
specifier|private
name|String
name|tenantId
decl_stmt|;
comment|/**    * @return role name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * @param name role name    */
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
DECL|method|getServiceId ()
specifier|public
name|String
name|getServiceId
parameter_list|()
block|{
return|return
name|serviceId
return|;
block|}
DECL|method|setServiceId (String serviceId)
specifier|public
name|void
name|setServiceId
parameter_list|(
name|String
name|serviceId
parameter_list|)
block|{
name|this
operator|.
name|serviceId
operator|=
name|serviceId
expr_stmt|;
block|}
DECL|method|getTenantId ()
specifier|public
name|String
name|getTenantId
parameter_list|()
block|{
return|return
name|tenantId
return|;
block|}
DECL|method|setTenantId (String tenantId)
specifier|public
name|void
name|setTenantId
parameter_list|(
name|String
name|tenantId
parameter_list|)
block|{
name|this
operator|.
name|tenantId
operator|=
name|tenantId
expr_stmt|;
block|}
block|}
end_class

end_unit

