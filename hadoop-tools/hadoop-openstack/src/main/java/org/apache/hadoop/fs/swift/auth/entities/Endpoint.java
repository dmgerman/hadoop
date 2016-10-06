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
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Openstack Swift endpoint description.  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|Endpoint
specifier|public
class|class
name|Endpoint
block|{
comment|/**    * endpoint id    */
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/**    * Keystone admin URL    */
DECL|field|adminURL
specifier|private
name|URI
name|adminURL
decl_stmt|;
comment|/**    * Keystone internal URL    */
DECL|field|internalURL
specifier|private
name|URI
name|internalURL
decl_stmt|;
comment|/**    * public accessible URL    */
DECL|field|publicURL
specifier|private
name|URI
name|publicURL
decl_stmt|;
comment|/**    * public accessible URL#2    */
DECL|field|publicURL2
specifier|private
name|URI
name|publicURL2
decl_stmt|;
comment|/**    * Openstack region name    */
DECL|field|region
specifier|private
name|String
name|region
decl_stmt|;
comment|/**    * This field is used in RackSpace authentication model    */
DECL|field|tenantId
specifier|private
name|String
name|tenantId
decl_stmt|;
comment|/**    * This field user in RackSpace auth model    */
DECL|field|versionId
specifier|private
name|String
name|versionId
decl_stmt|;
comment|/**    * This field user in RackSpace auth model    */
DECL|field|versionInfo
specifier|private
name|String
name|versionInfo
decl_stmt|;
comment|/**    * This field user in RackSpace auth model    */
DECL|field|versionList
specifier|private
name|String
name|versionList
decl_stmt|;
comment|/**    * @return endpoint id    */
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
comment|/**    * @param id endpoint id    */
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
comment|/**    * @return Keystone admin URL    */
DECL|method|getAdminURL ()
specifier|public
name|URI
name|getAdminURL
parameter_list|()
block|{
return|return
name|adminURL
return|;
block|}
comment|/**    * @param adminURL Keystone admin URL    */
DECL|method|setAdminURL (URI adminURL)
specifier|public
name|void
name|setAdminURL
parameter_list|(
name|URI
name|adminURL
parameter_list|)
block|{
name|this
operator|.
name|adminURL
operator|=
name|adminURL
expr_stmt|;
block|}
comment|/**    * @return internal Keystone    */
DECL|method|getInternalURL ()
specifier|public
name|URI
name|getInternalURL
parameter_list|()
block|{
return|return
name|internalURL
return|;
block|}
comment|/**    * @param internalURL Keystone internal URL    */
DECL|method|setInternalURL (URI internalURL)
specifier|public
name|void
name|setInternalURL
parameter_list|(
name|URI
name|internalURL
parameter_list|)
block|{
name|this
operator|.
name|internalURL
operator|=
name|internalURL
expr_stmt|;
block|}
comment|/**    * @return public accessible URL    */
DECL|method|getPublicURL ()
specifier|public
name|URI
name|getPublicURL
parameter_list|()
block|{
return|return
name|publicURL
return|;
block|}
comment|/**    * @param publicURL public URL    */
DECL|method|setPublicURL (URI publicURL)
specifier|public
name|void
name|setPublicURL
parameter_list|(
name|URI
name|publicURL
parameter_list|)
block|{
name|this
operator|.
name|publicURL
operator|=
name|publicURL
expr_stmt|;
block|}
DECL|method|getPublicURL2 ()
specifier|public
name|URI
name|getPublicURL2
parameter_list|()
block|{
return|return
name|publicURL2
return|;
block|}
DECL|method|setPublicURL2 (URI publicURL2)
specifier|public
name|void
name|setPublicURL2
parameter_list|(
name|URI
name|publicURL2
parameter_list|)
block|{
name|this
operator|.
name|publicURL2
operator|=
name|publicURL2
expr_stmt|;
block|}
comment|/**    * @return Openstack region name    */
DECL|method|getRegion ()
specifier|public
name|String
name|getRegion
parameter_list|()
block|{
return|return
name|region
return|;
block|}
comment|/**    * @param region Openstack region name    */
DECL|method|setRegion (String region)
specifier|public
name|void
name|setRegion
parameter_list|(
name|String
name|region
parameter_list|)
block|{
name|this
operator|.
name|region
operator|=
name|region
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
DECL|method|getVersionId ()
specifier|public
name|String
name|getVersionId
parameter_list|()
block|{
return|return
name|versionId
return|;
block|}
DECL|method|setVersionId (String versionId)
specifier|public
name|void
name|setVersionId
parameter_list|(
name|String
name|versionId
parameter_list|)
block|{
name|this
operator|.
name|versionId
operator|=
name|versionId
expr_stmt|;
block|}
DECL|method|getVersionInfo ()
specifier|public
name|String
name|getVersionInfo
parameter_list|()
block|{
return|return
name|versionInfo
return|;
block|}
DECL|method|setVersionInfo (String versionInfo)
specifier|public
name|void
name|setVersionInfo
parameter_list|(
name|String
name|versionInfo
parameter_list|)
block|{
name|this
operator|.
name|versionInfo
operator|=
name|versionInfo
expr_stmt|;
block|}
DECL|method|getVersionList ()
specifier|public
name|String
name|getVersionList
parameter_list|()
block|{
return|return
name|versionList
return|;
block|}
DECL|method|setVersionList (String versionList)
specifier|public
name|void
name|setVersionList
parameter_list|(
name|String
name|versionList
parameter_list|)
block|{
name|this
operator|.
name|versionList
operator|=
name|versionList
expr_stmt|;
block|}
block|}
end_class

end_unit

