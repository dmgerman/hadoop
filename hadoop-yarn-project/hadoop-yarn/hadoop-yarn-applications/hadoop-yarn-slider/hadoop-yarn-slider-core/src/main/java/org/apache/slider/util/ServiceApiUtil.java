begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.util
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|util
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
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|FileSystem
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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Artifact
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
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
name|slider
operator|.
name|api
operator|.
name|resource
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
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|JsonSerDeser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|AbstractClientProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|SliderProviderFactory
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
name|PropertyNamingStrategy
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ServiceApiUtil
specifier|public
class|class
name|ServiceApiUtil
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
name|ServiceApiUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jsonSerDeser
specifier|private
specifier|static
name|JsonSerDeser
argument_list|<
name|Application
argument_list|>
name|jsonSerDeser
init|=
operator|new
name|JsonSerDeser
argument_list|<>
argument_list|(
name|Application
operator|.
name|class
argument_list|,
name|PropertyNamingStrategy
operator|.
name|CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|setJsonSerDeser (JsonSerDeser jsd)
specifier|public
specifier|static
name|void
name|setJsonSerDeser
parameter_list|(
name|JsonSerDeser
name|jsd
parameter_list|)
block|{
name|jsonSerDeser
operator|=
name|jsd
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|validateAndResolveApplication (Application application, SliderFileSystem fs)
specifier|public
specifier|static
name|void
name|validateAndResolveApplication
parameter_list|(
name|Application
name|application
parameter_list|,
name|SliderFileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|application
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_APPLICATION_NAME_INVALID
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|SliderUtils
operator|.
name|isClusternameValid
argument_list|(
name|application
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_APPLICATION_NAME_INVALID_FORMAT
argument_list|,
name|application
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// If the application has no components do top-level checks
if|if
condition|(
operator|!
name|hasComponent
argument_list|(
name|application
argument_list|)
condition|)
block|{
comment|// If artifact is of type APPLICATION, read other application components
if|if
condition|(
name|application
operator|.
name|getArtifact
argument_list|()
operator|!=
literal|null
operator|&&
name|application
operator|.
name|getArtifact
argument_list|()
operator|.
name|getType
argument_list|()
operator|==
name|Artifact
operator|.
name|TypeEnum
operator|.
name|APPLICATION
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|application
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_ARTIFACT_ID_INVALID
argument_list|)
throw|;
block|}
name|Application
name|otherApplication
init|=
name|loadApplication
argument_list|(
name|fs
argument_list|,
name|application
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|application
operator|.
name|setComponents
argument_list|(
name|otherApplication
operator|.
name|getComponents
argument_list|()
argument_list|)
expr_stmt|;
name|application
operator|.
name|setArtifact
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SliderUtils
operator|.
name|mergeMapsIgnoreDuplicateKeys
argument_list|(
name|application
operator|.
name|getQuicklinks
argument_list|()
argument_list|,
name|otherApplication
operator|.
name|getQuicklinks
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Since it is a simple app with no components, create a default
comment|// component
name|Component
name|comp
init|=
name|createDefaultComponent
argument_list|(
name|application
argument_list|)
decl_stmt|;
name|validateComponent
argument_list|(
name|comp
argument_list|,
name|fs
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
name|application
operator|.
name|getComponents
argument_list|()
operator|.
name|add
argument_list|(
name|comp
argument_list|)
expr_stmt|;
if|if
condition|(
name|application
operator|.
name|getLifetime
argument_list|()
operator|==
literal|null
condition|)
block|{
name|application
operator|.
name|setLifetime
argument_list|(
name|RestApiConstants
operator|.
name|DEFAULT_UNLIMITED_LIFETIME
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
comment|// Validate there are no component name collisions (collisions are not
comment|// currently supported) and add any components from external applications
comment|// TODO allow name collisions? see AppState#roles
comment|// TODO or add prefix to external component names?
name|Configuration
name|globalConf
init|=
name|application
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|componentNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|componentsToRemove
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|componentsToAdd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Component
name|comp
range|:
name|application
operator|.
name|getComponents
argument_list|()
control|)
block|{
if|if
condition|(
name|componentNames
operator|.
name|contains
argument_list|(
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Component name collision: "
operator|+
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|// If artifact is of type APPLICATION (which cannot be filled from
comment|// global), read external application and add its components to this
comment|// application
if|if
condition|(
name|comp
operator|.
name|getArtifact
argument_list|()
operator|!=
literal|null
operator|&&
name|comp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getType
argument_list|()
operator|==
name|Artifact
operator|.
name|TypeEnum
operator|.
name|APPLICATION
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|comp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_ARTIFACT_ID_INVALID
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Marking {} for removal"
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|componentsToRemove
operator|.
name|add
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|externalComponents
init|=
name|getApplicationComponents
argument_list|(
name|fs
argument_list|,
name|comp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Component
name|c
range|:
name|externalComponents
control|)
block|{
name|Component
name|override
init|=
name|application
operator|.
name|getComponent
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|override
operator|!=
literal|null
operator|&&
name|override
operator|.
name|getArtifact
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// allow properties from external components to be overridden /
comment|// augmented by properties in this component, except for artifact
comment|// which must be read from external component
name|override
operator|.
name|mergeFrom
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Merging external component {} from external {}"
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|componentNames
operator|.
name|contains
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Component name collision: "
operator|+
name|c
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|componentNames
operator|.
name|add
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|componentsToAdd
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding component {} from external {}"
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// otherwise handle as a normal component
name|componentNames
operator|.
name|add
argument_list|(
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// configuration
name|comp
operator|.
name|getConfiguration
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|globalConf
argument_list|)
expr_stmt|;
block|}
block|}
name|application
operator|.
name|getComponents
argument_list|()
operator|.
name|removeAll
argument_list|(
name|componentsToRemove
argument_list|)
expr_stmt|;
name|application
operator|.
name|getComponents
argument_list|()
operator|.
name|addAll
argument_list|(
name|componentsToAdd
argument_list|)
expr_stmt|;
comment|// Validate components and let global values take effect if component level
comment|// values are not provided
name|Artifact
name|globalArtifact
init|=
name|application
operator|.
name|getArtifact
argument_list|()
decl_stmt|;
name|Resource
name|globalResource
init|=
name|application
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|Long
name|globalNumberOfContainers
init|=
name|application
operator|.
name|getNumberOfContainers
argument_list|()
decl_stmt|;
name|String
name|globalLaunchCommand
init|=
name|application
operator|.
name|getLaunchCommand
argument_list|()
decl_stmt|;
for|for
control|(
name|Component
name|comp
range|:
name|application
operator|.
name|getComponents
argument_list|()
control|)
block|{
comment|// fill in global artifact unless it is type APPLICATION
if|if
condition|(
name|comp
operator|.
name|getArtifact
argument_list|()
operator|==
literal|null
operator|&&
name|application
operator|.
name|getArtifact
argument_list|()
operator|!=
literal|null
operator|&&
name|application
operator|.
name|getArtifact
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|Artifact
operator|.
name|TypeEnum
operator|.
name|APPLICATION
condition|)
block|{
name|comp
operator|.
name|setArtifact
argument_list|(
name|globalArtifact
argument_list|)
expr_stmt|;
block|}
comment|// fill in global resource
if|if
condition|(
name|comp
operator|.
name|getResource
argument_list|()
operator|==
literal|null
condition|)
block|{
name|comp
operator|.
name|setResource
argument_list|(
name|globalResource
argument_list|)
expr_stmt|;
block|}
comment|// fill in global container count
if|if
condition|(
name|comp
operator|.
name|getNumberOfContainers
argument_list|()
operator|==
literal|null
condition|)
block|{
name|comp
operator|.
name|setNumberOfContainers
argument_list|(
name|globalNumberOfContainers
argument_list|)
expr_stmt|;
block|}
comment|// fill in global launch command
if|if
condition|(
name|comp
operator|.
name|getLaunchCommand
argument_list|()
operator|==
literal|null
condition|)
block|{
name|comp
operator|.
name|setLaunchCommand
argument_list|(
name|globalLaunchCommand
argument_list|)
expr_stmt|;
block|}
name|validateComponent
argument_list|(
name|comp
argument_list|,
name|fs
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Application lifetime if not specified, is set to unlimited lifetime
if|if
condition|(
name|application
operator|.
name|getLifetime
argument_list|()
operator|==
literal|null
condition|)
block|{
name|application
operator|.
name|setLifetime
argument_list|(
name|RestApiConstants
operator|.
name|DEFAULT_UNLIMITED_LIFETIME
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateComponent (Component comp, FileSystem fs)
specifier|public
specifier|static
name|void
name|validateComponent
parameter_list|(
name|Component
name|comp
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractClientProvider
name|compClientProvider
init|=
name|SliderProviderFactory
operator|.
name|getClientProvider
argument_list|(
name|comp
operator|.
name|getArtifact
argument_list|()
argument_list|)
decl_stmt|;
name|compClientProvider
operator|.
name|validateArtifact
argument_list|(
name|comp
operator|.
name|getArtifact
argument_list|()
argument_list|,
name|fs
argument_list|)
expr_stmt|;
if|if
condition|(
name|comp
operator|.
name|getLaunchCommand
argument_list|()
operator|==
literal|null
operator|&&
operator|(
name|comp
operator|.
name|getArtifact
argument_list|()
operator|==
literal|null
operator|||
name|comp
operator|.
name|getArtifact
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_ABSENT_LAUNCH_COMMAND
argument_list|)
throw|;
block|}
name|validateApplicationResource
argument_list|(
name|comp
operator|.
name|getResource
argument_list|()
argument_list|,
name|comp
argument_list|)
expr_stmt|;
if|if
condition|(
name|comp
operator|.
name|getNumberOfContainers
argument_list|()
operator|==
literal|null
operator|||
name|comp
operator|.
name|getNumberOfContainers
argument_list|()
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_CONTAINERS_COUNT_FOR_COMP_INVALID
operator|+
literal|": "
operator|+
name|comp
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|compClientProvider
operator|.
name|validateConfigFiles
argument_list|(
name|comp
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getFiles
argument_list|()
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getApplicationComponents (SliderFileSystem fs, String appName)
specifier|public
specifier|static
name|List
argument_list|<
name|Component
argument_list|>
name|getApplicationComponents
parameter_list|(
name|SliderFileSystem
name|fs
parameter_list|,
name|String
name|appName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|loadApplication
argument_list|(
name|fs
argument_list|,
name|appName
argument_list|)
operator|.
name|getComponents
argument_list|()
return|;
block|}
DECL|method|loadApplication (SliderFileSystem fs, String appName)
specifier|public
specifier|static
name|Application
name|loadApplication
parameter_list|(
name|SliderFileSystem
name|fs
parameter_list|,
name|String
name|appName
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|appJson
init|=
name|getAppJsonPath
argument_list|(
name|fs
argument_list|,
name|appName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading application definition from "
operator|+
name|appJson
argument_list|)
expr_stmt|;
name|Application
name|externalApplication
init|=
name|jsonSerDeser
operator|.
name|load
argument_list|(
name|fs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|appJson
argument_list|)
decl_stmt|;
return|return
name|externalApplication
return|;
block|}
DECL|method|getAppJsonPath (SliderFileSystem fs, String appName)
specifier|public
specifier|static
name|Path
name|getAppJsonPath
parameter_list|(
name|SliderFileSystem
name|fs
parameter_list|,
name|String
name|appName
parameter_list|)
block|{
name|Path
name|appDir
init|=
name|fs
operator|.
name|buildClusterDirPath
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|Path
name|appJson
init|=
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|appName
operator|+
literal|".json"
argument_list|)
decl_stmt|;
return|return
name|appJson
return|;
block|}
DECL|method|validateApplicationResource (Resource resource, Component comp)
specifier|private
specifier|static
name|void
name|validateApplicationResource
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|Component
name|comp
parameter_list|)
block|{
comment|// Only apps/components of type APPLICATION can skip resource requirement
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|comp
operator|==
literal|null
condition|?
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_INVALID
else|:
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_FOR_COMP_INVALID
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// One and only one of profile OR cpus& memory can be specified. Specifying
comment|// both raises validation error.
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|resource
operator|.
name|getProfile
argument_list|()
argument_list|)
operator|&&
operator|(
name|resource
operator|.
name|getCpus
argument_list|()
operator|!=
literal|null
operator|||
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|resource
operator|.
name|getMemory
argument_list|()
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|comp
operator|==
literal|null
condition|?
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_NOT_SUPPORTED
else|:
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_FOR_COMP_NOT_SUPPORTED
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// Currently resource profile is not supported yet, so we will raise
comment|// validation error if only resource profile is specified
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|resource
operator|.
name|getProfile
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_PROFILE_NOT_SUPPORTED_YET
argument_list|)
throw|;
block|}
name|String
name|memory
init|=
name|resource
operator|.
name|getMemory
argument_list|()
decl_stmt|;
name|Integer
name|cpus
init|=
name|resource
operator|.
name|getCpus
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|memory
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|comp
operator|==
literal|null
condition|?
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_MEMORY_INVALID
else|:
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_MEMORY_FOR_COMP_INVALID
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|cpus
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|comp
operator|==
literal|null
condition|?
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_CPUS_INVALID
else|:
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|cpus
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|comp
operator|==
literal|null
condition|?
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_CPUS_INVALID_RANGE
else|:
name|String
operator|.
name|format
argument_list|(
name|RestApiErrorMessages
operator|.
name|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID_RANGE
argument_list|,
name|comp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|hasComponent (Application application)
specifier|public
specifier|static
name|boolean
name|hasComponent
parameter_list|(
name|Application
name|application
parameter_list|)
block|{
if|if
condition|(
name|application
operator|.
name|getComponents
argument_list|()
operator|==
literal|null
operator|||
name|application
operator|.
name|getComponents
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|createDefaultComponent (Application app)
specifier|public
specifier|static
name|Component
name|createDefaultComponent
parameter_list|(
name|Application
name|app
parameter_list|)
block|{
name|Component
name|comp
init|=
operator|new
name|Component
argument_list|()
decl_stmt|;
name|comp
operator|.
name|setName
argument_list|(
name|RestApiConstants
operator|.
name|DEFAULT_COMPONENT_NAME
argument_list|)
expr_stmt|;
name|comp
operator|.
name|setArtifact
argument_list|(
name|app
operator|.
name|getArtifact
argument_list|()
argument_list|)
expr_stmt|;
name|comp
operator|.
name|setResource
argument_list|(
name|app
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|comp
operator|.
name|setNumberOfContainers
argument_list|(
name|app
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
expr_stmt|;
name|comp
operator|.
name|setLaunchCommand
argument_list|(
name|app
operator|.
name|getLaunchCommand
argument_list|()
argument_list|)
expr_stmt|;
name|comp
operator|.
name|setConfiguration
argument_list|(
name|app
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|comp
return|;
block|}
DECL|method|$ (String s)
specifier|public
specifier|static
name|String
name|$
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"${"
operator|+
name|s
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

