begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.auth.entities
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
operator|.
name|entities
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
name|JsonIgnoreProperties
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
name|fs
operator|.
name|swift
operator|.
name|auth
operator|.
name|Roles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Describes user entity in Keystone  * In different Swift installations User is represented differently.  * To avoid any JSON deserialization failures this entity is ignored.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|User
specifier|public
class|class
name|User
block|{
comment|/**    * user id in Keystone    */
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/**    * user human readable name    */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * user roles in Keystone    */
DECL|field|roles
specifier|private
name|List
argument_list|<
name|Roles
argument_list|>
name|roles
decl_stmt|;
comment|/**    * links to user roles    */
DECL|field|roles_links
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|roles_links
decl_stmt|;
comment|/**    * human readable username in Keystone    */
DECL|field|username
specifier|private
name|String
name|username
decl_stmt|;
comment|/**    * @return user id    */
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
comment|/**    * @param id user id    */
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
comment|/**    * @return user name    */
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
comment|/**    * @param name user name    */
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
comment|/**    * @return user roles    */
DECL|method|getRoles ()
specifier|public
name|List
argument_list|<
name|Roles
argument_list|>
name|getRoles
parameter_list|()
block|{
return|return
name|roles
return|;
block|}
comment|/**    * @param roles sets user roles    */
DECL|method|setRoles (List<Roles> roles)
specifier|public
name|void
name|setRoles
parameter_list|(
name|List
argument_list|<
name|Roles
argument_list|>
name|roles
parameter_list|)
block|{
name|this
operator|.
name|roles
operator|=
name|roles
expr_stmt|;
block|}
comment|/**    * @return user roles links    */
DECL|method|getRoles_links ()
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getRoles_links
parameter_list|()
block|{
return|return
name|roles_links
return|;
block|}
comment|/**    * @param roles_links user roles links    */
DECL|method|setRoles_links (List<Object> roles_links)
specifier|public
name|void
name|setRoles_links
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|roles_links
parameter_list|)
block|{
name|this
operator|.
name|roles_links
operator|=
name|roles_links
expr_stmt|;
block|}
comment|/**    * @return username    */
DECL|method|getUsername ()
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
comment|/**    * @param username human readable user name    */
DECL|method|setUsername (String username)
specifier|public
name|void
name|setUsername
parameter_list|(
name|String
name|username
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
block|}
block|}
end_class

end_unit

