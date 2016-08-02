begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
package|;
end_package

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
name|conf
operator|.
name|Configured
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
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|core
operator|.
name|conf
operator|.
name|AggregateConf
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
name|conf
operator|.
name|ConfTreeOperations
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
name|conf
operator|.
name|MapOperations
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
name|exceptions
operator|.
name|BadClusterStateException
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
name|exceptions
operator|.
name|SliderException
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
name|launch
operator|.
name|AbstractLauncher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|COMPONENT_INSTANCES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|DEF_YARN_CORES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|DEF_YARN_MEMORY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|YARN_CORES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
operator|.
name|YARN_MEMORY
import|;
end_import

begin_class
DECL|class|AbstractClientProvider
specifier|public
specifier|abstract
class|class
name|AbstractClientProvider
extends|extends
name|Configured
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractClientProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|providerUtils
specifier|protected
specifier|static
specifier|final
name|ProviderUtils
name|providerUtils
init|=
operator|new
name|ProviderUtils
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|PROVIDER_RESOURCE_BASE
specifier|public
specifier|static
specifier|final
name|String
name|PROVIDER_RESOURCE_BASE
init|=
literal|"org/apache/slider/providers/"
decl_stmt|;
DECL|field|PROVIDER_RESOURCE_BASE_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|PROVIDER_RESOURCE_BASE_ROOT
init|=
literal|"/"
operator|+
name|PROVIDER_RESOURCE_BASE
decl_stmt|;
DECL|method|AbstractClientProvider (Configuration conf)
specifier|public
name|AbstractClientProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getRoles ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|getRoles
parameter_list|()
function_decl|;
comment|/**    * Verify that an instance definition is considered valid by the provider    * @param instanceDefinition instance definition    * @throws SliderException if the configuration is not valid    */
DECL|method|validateInstanceDefinition (AggregateConf instanceDefinition, SliderFileSystem fs)
specifier|public
name|void
name|validateInstanceDefinition
parameter_list|(
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|SliderFileSystem
name|fs
parameter_list|)
throws|throws
name|SliderException
block|{
name|List
argument_list|<
name|ProviderRole
argument_list|>
name|roles
init|=
name|getRoles
argument_list|()
decl_stmt|;
name|ConfTreeOperations
name|resources
init|=
name|instanceDefinition
operator|.
name|getResourceOperations
argument_list|()
decl_stmt|;
for|for
control|(
name|ProviderRole
name|role
range|:
name|roles
control|)
block|{
name|String
name|name
init|=
name|role
operator|.
name|name
decl_stmt|;
name|MapOperations
name|component
init|=
name|resources
operator|.
name|getComponent
argument_list|(
name|role
operator|.
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|component
operator|!=
literal|null
condition|)
block|{
name|String
name|instances
init|=
name|component
operator|.
name|get
argument_list|(
name|COMPONENT_INSTANCES
argument_list|)
decl_stmt|;
if|if
condition|(
name|instances
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"No instance count provided for "
operator|+
name|name
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"{} with \n{}"
argument_list|,
name|message
argument_list|,
name|resources
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadClusterStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|String
name|ram
init|=
name|component
operator|.
name|get
argument_list|(
name|YARN_MEMORY
argument_list|)
decl_stmt|;
name|String
name|cores
init|=
name|component
operator|.
name|get
argument_list|(
name|YARN_CORES
argument_list|)
decl_stmt|;
name|providerUtils
operator|.
name|getRoleResourceRequirement
argument_list|(
name|ram
argument_list|,
name|DEF_YARN_MEMORY
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|providerUtils
operator|.
name|getRoleResourceRequirement
argument_list|(
name|cores
argument_list|,
name|DEF_YARN_CORES
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Any provider-side alteration of a configuration can take place here.    * @param aggregateConf config to patch    * @throws IOException IO problems    * @throws SliderException Slider-specific issues    */
DECL|method|prepareInstanceConfiguration (AggregateConf aggregateConf)
specifier|public
name|void
name|prepareInstanceConfiguration
parameter_list|(
name|AggregateConf
name|aggregateConf
parameter_list|)
throws|throws
name|SliderException
throws|,
name|IOException
block|{
comment|//default: do nothing
block|}
comment|/**    * Prepare the AM settings for launch    * @param fileSystem filesystem    * @param serviceConf configuration of the client    * @param launcher launcher to set up    * @param instanceDescription instance description being launched    * @param snapshotConfDirPath    * @param generatedConfDirPath    * @param clientConfExtras    * @param libdir    * @param tempPath    * @param miniClusterTestRun flag set to true on a mini cluster run    * @throws IOException    * @throws SliderException    */
DECL|method|prepareAMAndConfigForLaunch (SliderFileSystem fileSystem, Configuration serviceConf, AbstractLauncher launcher, AggregateConf instanceDescription, Path snapshotConfDirPath, Path generatedConfDirPath, Configuration clientConfExtras, String libdir, Path tempPath, boolean miniClusterTestRun)
specifier|public
name|void
name|prepareAMAndConfigForLaunch
parameter_list|(
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|Configuration
name|serviceConf
parameter_list|,
name|AbstractLauncher
name|launcher
parameter_list|,
name|AggregateConf
name|instanceDescription
parameter_list|,
name|Path
name|snapshotConfDirPath
parameter_list|,
name|Path
name|generatedConfDirPath
parameter_list|,
name|Configuration
name|clientConfExtras
parameter_list|,
name|String
name|libdir
parameter_list|,
name|Path
name|tempPath
parameter_list|,
name|boolean
name|miniClusterTestRun
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{        }
comment|/**    * Load in and merge in templates. Null arguments means "no such template"    * @param instanceConf instance to patch     * @param internalTemplate patch to internal.json    * @param resourceTemplate path to resources.json    * @param appConfTemplate path to app_conf.json    * @throws IOException any IO problems    */
DECL|method|mergeTemplates (AggregateConf instanceConf, String internalTemplate, String resourceTemplate, String appConfTemplate)
specifier|protected
name|void
name|mergeTemplates
parameter_list|(
name|AggregateConf
name|instanceConf
parameter_list|,
name|String
name|internalTemplate
parameter_list|,
name|String
name|resourceTemplate
parameter_list|,
name|String
name|appConfTemplate
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|internalTemplate
operator|!=
literal|null
condition|)
block|{
name|ConfTreeOperations
name|template
init|=
name|ConfTreeOperations
operator|.
name|fromResource
argument_list|(
name|internalTemplate
argument_list|)
decl_stmt|;
name|instanceConf
operator|.
name|getInternalOperations
argument_list|()
operator|.
name|mergeWithoutOverwrite
argument_list|(
name|template
operator|.
name|confTree
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resourceTemplate
operator|!=
literal|null
condition|)
block|{
name|ConfTreeOperations
name|resTemplate
init|=
name|ConfTreeOperations
operator|.
name|fromResource
argument_list|(
name|resourceTemplate
argument_list|)
decl_stmt|;
name|instanceConf
operator|.
name|getResourceOperations
argument_list|()
operator|.
name|mergeWithoutOverwrite
argument_list|(
name|resTemplate
operator|.
name|confTree
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appConfTemplate
operator|!=
literal|null
condition|)
block|{
name|ConfTreeOperations
name|template
init|=
name|ConfTreeOperations
operator|.
name|fromResource
argument_list|(
name|appConfTemplate
argument_list|)
decl_stmt|;
name|instanceConf
operator|.
name|getAppConfOperations
argument_list|()
operator|.
name|mergeWithoutOverwrite
argument_list|(
name|template
operator|.
name|confTree
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This is called pre-launch to validate that the cluster specification    * is valid. This can include checking that the security options    * are in the site files prior to launch, that there are no conflicting operations    * etc.    *    * This check is made prior to every launch of the cluster -so can     * pick up problems which manually edited cluster files have added,    * or from specification files from previous versions.    *    * The provider MUST NOT change the remote specification. This is    * purely a pre-launch validation of options.    *    *    * @param sliderFileSystem filesystem    * @param clustername name of the cluster    * @param configuration cluster configuration    * @param instanceDefinition cluster specification    * @param clusterDirPath directory of the cluster    * @param generatedConfDirPath path to place generated artifacts    * @param secure flag to indicate that the cluster is secure    * @throws SliderException on any validation issue    * @throws IOException on any IO problem    */
DECL|method|preflightValidateClusterConfiguration (SliderFileSystem sliderFileSystem, String clustername, Configuration configuration, AggregateConf instanceDefinition, Path clusterDirPath, Path generatedConfDirPath, boolean secure)
specifier|public
name|void
name|preflightValidateClusterConfiguration
parameter_list|(
name|SliderFileSystem
name|sliderFileSystem
parameter_list|,
name|String
name|clustername
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|AggregateConf
name|instanceDefinition
parameter_list|,
name|Path
name|clusterDirPath
parameter_list|,
name|Path
name|generatedConfDirPath
parameter_list|,
name|boolean
name|secure
parameter_list|)
throws|throws
name|SliderException
throws|,
name|IOException
block|{
name|validateInstanceDefinition
argument_list|(
name|instanceDefinition
argument_list|,
name|sliderFileSystem
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a set of application specific string tags.    * @return the set of tags.    */
DECL|method|getApplicationTags (SliderFileSystem fileSystem, String appDef)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|(
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|String
name|appDef
parameter_list|)
throws|throws
name|SliderException
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
comment|/**    * Process client operations for applications such as install, configure    * @param fileSystem    * @param registryOperations    * @param configuration    * @param operation    * @param clientInstallPath    * @param clientPackage    * @param clientConfig    * @param name    * @throws SliderException    */
DECL|method|processClientOperation (SliderFileSystem fileSystem, RegistryOperations registryOperations, Configuration configuration, String operation, File clientInstallPath, File clientPackage, JSONObject clientConfig, String name)
specifier|public
name|void
name|processClientOperation
parameter_list|(
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|RegistryOperations
name|registryOperations
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|String
name|operation
parameter_list|,
name|File
name|clientInstallPath
parameter_list|,
name|File
name|clientPackage
parameter_list|,
name|JSONObject
name|clientConfig
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SliderException
block|{
throw|throw
operator|new
name|SliderException
argument_list|(
literal|"Provider does not support client operations."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

