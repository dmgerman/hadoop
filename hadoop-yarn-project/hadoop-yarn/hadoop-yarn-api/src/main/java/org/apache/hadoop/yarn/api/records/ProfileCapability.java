begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Class to capture capability requirements when using resource profiles. The  * ProfileCapability is meant to be used as part of the ResourceRequest. A  * profile capability has two pieces - the resource profile name and the  * overrides. The resource profile specifies the name of the resource profile  * to be used and the capability override is the overrides desired on specific  * resource types.  *  * For example, if you have a resource profile "small" that maps to  * {@literal<4096M, 2 cores, 1 gpu>} and you set the capability override to  * {@literal<8192M, 0 cores, 0 gpu>}, then the actual resource allocation on  * the ResourceManager will be {@literal<8192M, 2 cores, 1 gpu>}.  *  * Note that the conversion from the ProfileCapability to the Resource class  * with the actual resource requirements will be done by the ResourceManager,  * which has the actual profile to Resource mapping.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ProfileCapability
specifier|public
specifier|abstract
class|class
name|ProfileCapability
block|{
DECL|field|DEFAULT_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PROFILE
init|=
literal|"default"
decl_stmt|;
DECL|method|newInstance (Resource override)
specifier|public
specifier|static
name|ProfileCapability
name|newInstance
parameter_list|(
name|Resource
name|override
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|DEFAULT_PROFILE
argument_list|,
name|override
argument_list|)
return|;
block|}
DECL|method|newInstance (String profile)
specifier|public
specifier|static
name|ProfileCapability
name|newInstance
parameter_list|(
name|String
name|profile
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|profile
operator|!=
literal|null
argument_list|,
literal|"The profile name cannot be null"
argument_list|)
expr_stmt|;
name|ProfileCapability
name|obj
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ProfileCapability
operator|.
name|class
argument_list|)
decl_stmt|;
name|obj
operator|.
name|setProfileName
argument_list|(
name|profile
argument_list|)
expr_stmt|;
name|obj
operator|.
name|setProfileCapabilityOverride
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|obj
return|;
block|}
DECL|method|newInstance (String profile, Resource override)
specifier|public
specifier|static
name|ProfileCapability
name|newInstance
parameter_list|(
name|String
name|profile
parameter_list|,
name|Resource
name|override
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|profile
operator|!=
literal|null
argument_list|,
literal|"The profile name cannot be null"
argument_list|)
expr_stmt|;
name|ProfileCapability
name|obj
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ProfileCapability
operator|.
name|class
argument_list|)
decl_stmt|;
name|obj
operator|.
name|setProfileName
argument_list|(
name|profile
argument_list|)
expr_stmt|;
name|obj
operator|.
name|setProfileCapabilityOverride
argument_list|(
name|override
argument_list|)
expr_stmt|;
return|return
name|obj
return|;
block|}
comment|/**    * Get the profile name.    * @return the profile name    */
DECL|method|getProfileName ()
specifier|public
specifier|abstract
name|String
name|getProfileName
parameter_list|()
function_decl|;
comment|/**    * Get the profile capability override.    * @return Resource object containing the override.    */
DECL|method|getProfileCapabilityOverride ()
specifier|public
specifier|abstract
name|Resource
name|getProfileCapabilityOverride
parameter_list|()
function_decl|;
comment|/**    * Set the resource profile name.    * @param profileName the resource profile name    */
DECL|method|setProfileName (String profileName)
specifier|public
specifier|abstract
name|void
name|setProfileName
parameter_list|(
name|String
name|profileName
parameter_list|)
function_decl|;
comment|/**    * Set the capability override to override specific resource types on the    * resource profile.    *    * For example, if you have a resource profile "small" that maps to    * {@literal<4096M, 2 cores, 1 gpu>} and you set the capability override to    * {@literal<8192M, 0 cores, 0 gpu>}, then the actual resource allocation on    * the ResourceManager will be {@literal<8192M, 2 cores, 1 gpu>}.    *    * Note that the conversion from the ProfileCapability to the Resource class    * with the actual resource requirements will be done by the ResourceManager,    * which has the actual profile to Resource mapping.    *    * @param r Resource object containing the capability override    */
DECL|method|setProfileCapabilityOverride (Resource r)
specifier|public
specifier|abstract
name|void
name|setProfileCapabilityOverride
parameter_list|(
name|Resource
name|r
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|ProfileCapability
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|ProfileCapability
operator|)
name|other
operator|)
operator|.
name|getProfileName
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getProfileName
argument_list|()
argument_list|)
operator|&&
operator|(
operator|(
name|ProfileCapability
operator|)
name|other
operator|)
operator|.
name|getProfileCapabilityOverride
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getProfileCapabilityOverride
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|2153
decl_stmt|;
name|int
name|result
init|=
literal|2459
decl_stmt|;
name|String
name|name
init|=
name|getProfileName
argument_list|()
decl_stmt|;
name|Resource
name|override
init|=
name|getProfileCapabilityOverride
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|name
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|name
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|override
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|override
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
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
literal|"{ profile: "
operator|+
name|this
operator|.
name|getProfileName
argument_list|()
operator|+
literal|", capabilityOverride: "
operator|+
name|this
operator|.
name|getProfileCapabilityOverride
argument_list|()
operator|+
literal|" }"
return|;
block|}
comment|/**    * Get a representation of the capability as a Resource object.    * @param capability the capability we wish to convert    * @param resourceProfilesMap map of profile name to Resource object    * @return Resource object representing the capability    */
DECL|method|toResource (ProfileCapability capability, Map<String, Resource> resourceProfilesMap)
specifier|public
specifier|static
name|Resource
name|toResource
parameter_list|(
name|ProfileCapability
name|capability
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resourceProfilesMap
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|capability
operator|!=
literal|null
argument_list|,
literal|"Capability cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|resourceProfilesMap
operator|!=
literal|null
argument_list|,
literal|"Resource profiles map cannot be null"
argument_list|)
expr_stmt|;
name|Resource
name|none
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|profileName
init|=
name|capability
operator|.
name|getProfileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|profileName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|profileName
operator|=
name|DEFAULT_PROFILE
expr_stmt|;
block|}
if|if
condition|(
name|resourceProfilesMap
operator|.
name|containsKey
argument_list|(
name|profileName
argument_list|)
condition|)
block|{
name|resource
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|resourceProfilesMap
operator|.
name|get
argument_list|(
name|profileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|capability
operator|.
name|getProfileCapabilityOverride
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|capability
operator|.
name|getProfileCapabilityOverride
argument_list|()
operator|.
name|equals
argument_list|(
name|none
argument_list|)
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|entry
range|:
name|capability
operator|.
name|getProfileCapabilityOverride
argument_list|()
operator|.
name|getResources
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getValue
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|resource
operator|.
name|setResourceInformation
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|resource
return|;
block|}
block|}
end_class

end_unit

