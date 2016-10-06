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

begin_comment
comment|/**  * Access token representation of Openstack Keystone authentication.  * Class holds token id, tenant and expiration time.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  *  * Example:  *<pre>  * "token" : {  *   "RAX-AUTH:authenticatedBy" : [ "APIKEY" ],  *   "expires" : "2013-07-12T05:19:24.685-05:00",  *   "id" : "8bbea4215113abdab9d4c8fb0d37",  *   "tenant" : { "id" : "01011970",  *   "name" : "77777"  *   }  *  }  *</pre>  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|AccessToken
specifier|public
class|class
name|AccessToken
block|{
comment|/**    * token expiration time    */
DECL|field|expires
specifier|private
name|String
name|expires
decl_stmt|;
comment|/**    * token id    */
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/**    * tenant name for whom id is attached    */
DECL|field|tenant
specifier|private
name|Tenant
name|tenant
decl_stmt|;
comment|/**    * @return token expiration time    */
DECL|method|getExpires ()
specifier|public
name|String
name|getExpires
parameter_list|()
block|{
return|return
name|expires
return|;
block|}
comment|/**    * @param expires the token expiration time    */
DECL|method|setExpires (String expires)
specifier|public
name|void
name|setExpires
parameter_list|(
name|String
name|expires
parameter_list|)
block|{
name|this
operator|.
name|expires
operator|=
name|expires
expr_stmt|;
block|}
comment|/**    * @return token value    */
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
comment|/**    * @param id token value    */
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
comment|/**    * @return tenant authenticated in Openstack Keystone    */
DECL|method|getTenant ()
specifier|public
name|Tenant
name|getTenant
parameter_list|()
block|{
return|return
name|tenant
return|;
block|}
comment|/**    * @param tenant tenant authenticated in Openstack Keystone    */
DECL|method|setTenant (Tenant tenant)
specifier|public
name|void
name|setTenant
parameter_list|(
name|Tenant
name|tenant
parameter_list|)
block|{
name|this
operator|.
name|tenant
operator|=
name|tenant
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
literal|"AccessToken{"
operator|+
literal|"id='"
operator|+
name|id
operator|+
literal|'\''
operator|+
literal|", tenant="
operator|+
name|tenant
operator|+
literal|", expires='"
operator|+
name|expires
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

