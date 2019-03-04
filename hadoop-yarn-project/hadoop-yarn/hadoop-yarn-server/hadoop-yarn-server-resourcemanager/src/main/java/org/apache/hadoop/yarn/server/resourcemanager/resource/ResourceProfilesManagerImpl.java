begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resource
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|resource
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|conf
operator|.
name|Configuration
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YARNFeatureNotEnabledException
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
name|exceptions
operator|.
name|YarnException
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
name|resource
operator|.
name|ResourceUtils
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * PBImpl class to handle all proto related implementation for  * ResourceProfilesManager.  */
end_comment

begin_class
DECL|class|ResourceProfilesManagerImpl
specifier|public
class|class
name|ResourceProfilesManagerImpl
implements|implements
name|ResourceProfilesManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ResourceProfilesManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|profiles
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|profiles
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|profileEnabled
specifier|private
name|boolean
name|profileEnabled
init|=
literal|false
decl_stmt|;
DECL|field|MEMORY
specifier|private
specifier|static
specifier|final
name|String
name|MEMORY
init|=
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|VCORES
specifier|private
specifier|static
specifier|final
name|String
name|VCORES
init|=
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PROFILE
init|=
literal|"default"
decl_stmt|;
DECL|field|MINIMUM_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|MINIMUM_PROFILE
init|=
literal|"minimum"
decl_stmt|;
DECL|field|MAXIMUM_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_PROFILE
init|=
literal|"maximum"
decl_stmt|;
DECL|field|readLock
specifier|protected
specifier|final
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|protected
specifier|final
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|MANDATORY_PROFILES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|MANDATORY_PROFILES
init|=
block|{
name|DEFAULT_PROFILE
block|,
name|MINIMUM_PROFILE
block|,
name|MAXIMUM_PROFILE
block|}
decl_stmt|;
DECL|field|FEATURE_NOT_ENABLED_MSG
specifier|private
specifier|static
specifier|final
name|String
name|FEATURE_NOT_ENABLED_MSG
init|=
literal|"Resource profile is not enabled, please "
operator|+
literal|"enable resource profile feature before using its functions."
operator|+
literal|" (by setting "
operator|+
name|YarnConfiguration
operator|.
name|RM_RESOURCE_PROFILES_ENABLED
operator|+
literal|" to true)"
decl_stmt|;
DECL|method|ResourceProfilesManagerImpl ()
specifier|public
name|ResourceProfilesManagerImpl
parameter_list|()
block|{
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
DECL|method|init (Configuration config)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|=
name|config
expr_stmt|;
name|loadProfiles
argument_list|()
expr_stmt|;
block|}
DECL|method|loadProfiles ()
specifier|private
name|void
name|loadProfiles
parameter_list|()
throws|throws
name|IOException
block|{
name|profileEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_PROFILES_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_PROFILES_ENABLED
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|profileEnabled
condition|)
block|{
return|return;
block|}
name|String
name|sourceFile
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_PROFILES_SOURCE_FILE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_PROFILES_SOURCE_FILE
argument_list|)
decl_stmt|;
name|String
name|resourcesFile
init|=
name|sourceFile
decl_stmt|;
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|classLoader
operator|==
literal|null
condition|)
block|{
name|classLoader
operator|=
name|ResourceProfilesManagerImpl
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|classLoader
operator|!=
literal|null
condition|)
block|{
name|URL
name|tmp
init|=
name|classLoader
operator|.
name|getResource
argument_list|(
name|sourceFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|!=
literal|null
condition|)
block|{
name|resourcesFile
operator|=
name|tmp
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Map
name|data
init|=
name|mapper
operator|.
name|readValue
argument_list|(
operator|new
name|File
argument_list|(
name|resourcesFile
argument_list|)
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
name|iterator
init|=
name|data
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|profileName
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Name of resource profile cannot be an empty string"
argument_list|)
throw|;
block|}
if|if
condition|(
name|profileName
operator|.
name|equals
argument_list|(
name|MINIMUM_PROFILE
argument_list|)
operator|||
name|profileName
operator|.
name|equals
argument_list|(
name|MAXIMUM_PROFILE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"profile={%s, %s} is should not be specified "
operator|+
literal|"inside %s, they will be loaded from resource-types.xml"
argument_list|,
name|MINIMUM_PROFILE
argument_list|,
name|MAXIMUM_PROFILE
argument_list|,
name|sourceFile
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|profileInfo
init|=
operator|(
name|Map
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// ensure memory and vcores are specified
if|if
condition|(
operator|!
name|profileInfo
operator|.
name|containsKey
argument_list|(
name|MEMORY
argument_list|)
operator|||
operator|!
name|profileInfo
operator|.
name|containsKey
argument_list|(
name|VCORES
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Illegal resource profile definition; profile '"
operator|+
name|profileName
operator|+
literal|"' must contain '"
operator|+
name|MEMORY
operator|+
literal|"' and '"
operator|+
name|VCORES
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|Resource
name|resource
init|=
name|parseResource
argument_list|(
name|profileInfo
argument_list|)
decl_stmt|;
name|profiles
operator|.
name|put
argument_list|(
name|profileName
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added profile '"
operator|+
name|profileName
operator|+
literal|"' with resources: "
operator|+
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add minimum/maximum profile
name|profiles
operator|.
name|put
argument_list|(
name|MINIMUM_PROFILE
argument_list|,
name|ResourceUtils
operator|.
name|getResourceTypesMinimumAllocation
argument_list|()
argument_list|)
expr_stmt|;
name|profiles
operator|.
name|put
argument_list|(
name|MAXIMUM_PROFILE
argument_list|,
name|ResourceUtils
operator|.
name|getResourceTypesMaximumAllocation
argument_list|()
argument_list|)
expr_stmt|;
comment|// check to make sure mandatory profiles are present
for|for
control|(
name|String
name|profile
range|:
name|MANDATORY_PROFILES
control|)
block|{
if|if
condition|(
operator|!
name|profiles
operator|.
name|containsKey
argument_list|(
name|profile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mandatory profile missing '"
operator|+
name|profile
operator|+
literal|"' missing. "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|MANDATORY_PROFILES
argument_list|)
operator|+
literal|" must be present"
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Loaded profiles: "
operator|+
name|profiles
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|parseResource (Map profileInfo)
specifier|private
name|Resource
name|parseResource
parameter_list|(
name|Map
name|profileInfo
parameter_list|)
throws|throws
name|IOException
block|{
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
name|Iterator
name|iterator
init|=
name|profileInfo
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|resourceTypes
init|=
name|ResourceUtils
operator|.
name|getResourceTypes
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|resourceEntry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|resourceName
init|=
name|resourceEntry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ResourceInformation
name|resourceValue
init|=
name|fromString
argument_list|(
name|resourceName
argument_list|,
name|resourceEntry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|MEMORY
argument_list|)
condition|)
block|{
name|resource
operator|.
name|setMemorySize
argument_list|(
name|resourceValue
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|VCORES
argument_list|)
condition|)
block|{
name|resource
operator|.
name|setVirtualCores
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|resourceValue
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|resourceTypes
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|resource
operator|.
name|setResourceInformation
argument_list|(
name|resourceName
argument_list|,
name|resourceValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unrecognized resource type '"
operator|+
name|resourceName
operator|+
literal|"'. Recognized resource types are '"
operator|+
name|resourceTypes
operator|.
name|keySet
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
return|return
name|resource
return|;
block|}
DECL|method|checkAndThrowExceptionWhenFeatureDisabled ()
specifier|private
name|void
name|checkAndThrowExceptionWhenFeatureDisabled
parameter_list|()
throws|throws
name|YARNFeatureNotEnabledException
block|{
if|if
condition|(
operator|!
name|profileEnabled
condition|)
block|{
throw|throw
operator|new
name|YARNFeatureNotEnabledException
argument_list|(
name|FEATURE_NOT_ENABLED_MSG
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProfile (String profile)
specifier|public
name|Resource
name|getProfile
parameter_list|(
name|String
name|profile
parameter_list|)
throws|throws
name|YarnException
block|{
name|checkAndThrowExceptionWhenFeatureDisabled
argument_list|()
expr_stmt|;
if|if
condition|(
name|profile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Profile name cannot be null"
argument_list|)
throw|;
block|}
name|Resource
name|profileRes
init|=
name|profiles
operator|.
name|get
argument_list|(
name|profile
argument_list|)
decl_stmt|;
if|if
condition|(
name|profileRes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Resource profile '"
operator|+
name|profile
operator|+
literal|"' not found"
argument_list|)
throw|;
block|}
return|return
name|Resources
operator|.
name|clone
argument_list|(
name|profileRes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceProfiles ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getResourceProfiles
parameter_list|()
throws|throws
name|YARNFeatureNotEnabledException
block|{
name|checkAndThrowExceptionWhenFeatureDisabled
argument_list|()
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|profiles
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|VisibleForTesting
DECL|method|reloadProfiles ()
specifier|public
name|void
name|reloadProfiles
parameter_list|()
throws|throws
name|IOException
block|{
name|profiles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|loadProfiles
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultProfile ()
specifier|public
name|Resource
name|getDefaultProfile
parameter_list|()
throws|throws
name|YarnException
block|{
return|return
name|getProfile
argument_list|(
name|DEFAULT_PROFILE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMinimumProfile ()
specifier|public
name|Resource
name|getMinimumProfile
parameter_list|()
throws|throws
name|YarnException
block|{
return|return
name|getProfile
argument_list|(
name|MINIMUM_PROFILE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaximumProfile ()
specifier|public
name|Resource
name|getMaximumProfile
parameter_list|()
throws|throws
name|YarnException
block|{
return|return
name|getProfile
argument_list|(
name|MAXIMUM_PROFILE
argument_list|)
return|;
block|}
DECL|method|fromString (String name, String value)
specifier|private
name|ResourceInformation
name|fromString
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|units
init|=
name|ResourceUtils
operator|.
name|getUnits
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Long
name|resourceValue
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
name|units
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
name|resourceValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

