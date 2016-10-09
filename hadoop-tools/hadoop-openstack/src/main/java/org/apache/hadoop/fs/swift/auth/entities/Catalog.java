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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Describes Openstack Swift REST endpoints.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|Catalog
specifier|public
class|class
name|Catalog
block|{
comment|/**    * List of valid swift endpoints    */
DECL|field|endpoints
specifier|private
name|List
argument_list|<
name|Endpoint
argument_list|>
name|endpoints
decl_stmt|;
comment|/**    * endpoint links are additional information description    * which aren't used in Hadoop and Swift integration scope    */
DECL|field|endpoints_links
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|endpoints_links
decl_stmt|;
comment|/**    * Openstack REST service name. In our case name = "keystone"    */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * Type of REST service. In our case type = "identity"    */
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
comment|/**    * @return List of endpoints    */
DECL|method|getEndpoints ()
specifier|public
name|List
argument_list|<
name|Endpoint
argument_list|>
name|getEndpoints
parameter_list|()
block|{
return|return
name|endpoints
return|;
block|}
comment|/**    * @param endpoints list of endpoints    */
DECL|method|setEndpoints (List<Endpoint> endpoints)
specifier|public
name|void
name|setEndpoints
parameter_list|(
name|List
argument_list|<
name|Endpoint
argument_list|>
name|endpoints
parameter_list|)
block|{
name|this
operator|.
name|endpoints
operator|=
name|endpoints
expr_stmt|;
block|}
comment|/**    * @return list of endpoint links    */
DECL|method|getEndpoints_links ()
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getEndpoints_links
parameter_list|()
block|{
return|return
name|endpoints_links
return|;
block|}
comment|/**    * @param endpoints_links list of endpoint links    */
DECL|method|setEndpoints_links (List<Object> endpoints_links)
specifier|public
name|void
name|setEndpoints_links
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|endpoints_links
parameter_list|)
block|{
name|this
operator|.
name|endpoints_links
operator|=
name|endpoints_links
expr_stmt|;
block|}
comment|/**    * @return name of Openstack REST service    */
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
comment|/**    * @param name of Openstack REST service    */
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
comment|/**    * @return type of Openstack REST service    */
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * @param type of REST service    */
DECL|method|setType (String type)
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
block|}
end_class

end_unit

