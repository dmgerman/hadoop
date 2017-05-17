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
name|ConfigFile
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
name|exceptions
operator|.
name|SliderException
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
DECL|class|AbstractClientProvider
specifier|public
specifier|abstract
class|class
name|AbstractClientProvider
block|{
DECL|method|AbstractClientProvider ()
specifier|public
name|AbstractClientProvider
parameter_list|()
block|{   }
comment|/**    * Generates a fixed format of application tags given one or more of    * application name, version and description. This allows subsequent query for    * an application with a name only, version only or description only or any    * combination of those as filters.    *    * @param appName name of the application    * @param appVersion version of the application    * @param appDescription brief description of the application    * @return    */
DECL|method|createApplicationTags (String appName, String appVersion, String appDescription)
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|createApplicationTags
parameter_list|(
name|String
name|appName
parameter_list|,
name|String
name|appVersion
parameter_list|,
name|String
name|appDescription
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tags
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|tags
operator|.
name|add
argument_list|(
name|SliderUtils
operator|.
name|createNameTag
argument_list|(
name|appName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|appVersion
operator|!=
literal|null
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|SliderUtils
operator|.
name|createVersionTag
argument_list|(
name|appVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appDescription
operator|!=
literal|null
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|SliderUtils
operator|.
name|createDescriptionTag
argument_list|(
name|appDescription
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tags
return|;
block|}
comment|/**    * Validate the artifact.    * @param artifact    */
DECL|method|validateArtifact (Artifact artifact, FileSystem fileSystem)
specifier|public
specifier|abstract
name|void
name|validateArtifact
parameter_list|(
name|Artifact
name|artifact
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|validateConfigFile (ConfigFile configFile, FileSystem fileSystem)
specifier|protected
specifier|abstract
name|void
name|validateConfigFile
parameter_list|(
name|ConfigFile
name|configFile
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Validate the config files.    * @param configFiles config file list    * @param fileSystem file system    */
DECL|method|validateConfigFiles (List<ConfigFile> configFiles, FileSystem fileSystem)
specifier|public
name|void
name|validateConfigFiles
parameter_list|(
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|configFiles
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|ConfigFile
name|configFile
range|:
name|configFiles
control|)
block|{
name|validateConfigFile
argument_list|(
name|configFile
argument_list|,
name|fileSystem
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Process client operations for applications such as install, configure.    * @param fileSystem    * @param registryOperations    * @param configuration    * @param operation    * @param clientInstallPath    * @param clientPackage    * @param clientConfig    * @param name    * @throws SliderException    */
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

