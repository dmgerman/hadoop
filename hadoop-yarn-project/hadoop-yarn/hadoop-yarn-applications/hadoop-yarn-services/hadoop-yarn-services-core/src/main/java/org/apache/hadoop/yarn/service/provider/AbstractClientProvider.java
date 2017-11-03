begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|provider
package|;
end_package

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
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
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
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
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
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|SliderUtils
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConstants
operator|.
name|CONTENT
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
comment|/**    * Validate the config files.    * @param configFiles config file list    * @param fs file system    */
DECL|method|validateConfigFiles (List<ConfigFile> configFiles, FileSystem fs)
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
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|destFileSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ConfigFile
name|file
range|:
name|configFiles
control|)
block|{
if|if
condition|(
name|file
operator|.
name|getType
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File type is empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|file
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|TEMPLATE
argument_list|)
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|file
operator|.
name|getSrcFile
argument_list|()
argument_list|)
operator|&&
operator|!
name|file
operator|.
name|getProperties
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CONTENT
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"For {0} "
operator|+
literal|"format, either src_file must be specified in ConfigFile,"
operator|+
literal|" or the \"{1}\" key must be specified in "
operator|+
literal|"the 'properties' field of ConfigFile. "
argument_list|,
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|TEMPLATE
argument_list|,
name|CONTENT
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|file
operator|.
name|getSrcFile
argument_list|()
argument_list|)
condition|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|file
operator|.
name|getSrcFile
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Src_file does not exist for config file: "
operator|+
name|file
operator|.
name|getSrcFile
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|file
operator|.
name|getDestFile
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Dest_file is empty."
argument_list|)
throw|;
block|}
if|if
condition|(
name|destFileSet
operator|.
name|contains
argument_list|(
name|file
operator|.
name|getDestFile
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicated ConfigFile exists: "
operator|+
name|file
operator|.
name|getDestFile
argument_list|()
argument_list|)
throw|;
block|}
name|destFileSet
operator|.
name|add
argument_list|(
name|file
operator|.
name|getDestFile
argument_list|()
argument_list|)
expr_stmt|;
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
name|destPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|file
operator|.
name|getDestFile
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destPath
operator|.
name|isAbsolute
argument_list|()
operator|&&
name|destPath
operator|.
name|getNameCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Non-absolute dest_file has more "
operator|+
literal|"than one path element"
argument_list|)
throw|;
block|}
comment|// provider-specific validation
name|validateConfigFile
argument_list|(
name|file
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

