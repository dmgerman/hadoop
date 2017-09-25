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
name|hadoop
operator|.
name|fs
operator|.
name|FSDataOutputStream
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
name|FileStatus
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
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
name|LocalResource
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
name|LocalResourceType
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
name|ServiceContext
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
name|Service
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
name|Component
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
name|api
operator|.
name|records
operator|.
name|ConfigFormat
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
name|service
operator|.
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|conf
operator|.
name|YarnServiceConstants
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
name|conf
operator|.
name|YarnServiceConf
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
name|containerlaunch
operator|.
name|AbstractLauncher
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
name|exceptions
operator|.
name|BadCommandArgumentsException
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
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|PublishedConfiguration
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
name|PublishedConfigurationOutputter
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
name|SliderFileSystem
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
name|FileNotFoundException
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|api
operator|.
name|ServiceApiConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This is a factoring out of methods handy for providers. It's bonded to a log  * at construction time.  */
end_comment

begin_class
DECL|class|ProviderUtils
specifier|public
class|class
name|ProviderUtils
implements|implements
name|YarnServiceConstants
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProviderUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Add oneself to the classpath. This does not work    * on minicluster test runs where the JAR is not built up.    * @param providerResources map of provider resources to add these entries to    * @param providerClass provider to add    * @param jarName name of the jar to use    * @param sliderFileSystem target filesystem    * @param tempPath path in the cluster FS for temp files    * @param libdir relative directory to place resources    * @param miniClusterTestRun true if minicluster is being used    * @return true if the class was found in a JAR    *     * @throws FileNotFoundException if the JAR was not found and this is NOT    * a mini cluster test run    * @throws IOException IO problems    * @throws SliderException any Slider problem    */
DECL|method|addProviderJar ( Map<String, LocalResource> providerResources, Class providerClass, String jarName, SliderFileSystem sliderFileSystem, Path tempPath, String libdir, boolean miniClusterTestRun)
specifier|public
specifier|static
name|boolean
name|addProviderJar
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|providerResources
parameter_list|,
name|Class
name|providerClass
parameter_list|,
name|String
name|jarName
parameter_list|,
name|SliderFileSystem
name|sliderFileSystem
parameter_list|,
name|Path
name|tempPath
parameter_list|,
name|String
name|libdir
parameter_list|,
name|boolean
name|miniClusterTestRun
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
try|try
block|{
name|SliderUtils
operator|.
name|putJar
argument_list|(
name|providerResources
argument_list|,
name|sliderFileSystem
argument_list|,
name|providerClass
argument_list|,
name|tempPath
argument_list|,
name|libdir
argument_list|,
name|jarName
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|miniClusterTestRun
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/**    * Loads all dependency jars from the default path.    * @param providerResources map of provider resources to add these entries to    * @param sliderFileSystem target filesystem    * @param tempPath path in the cluster FS for temp files    * @param libDir relative directory to place resources    * @param libLocalSrcDir explicitly supplied local libs dir    * @throws IOException trouble copying to HDFS    * @throws SliderException trouble copying to HDFS    */
DECL|method|addAllDependencyJars ( Map<String, LocalResource> providerResources, SliderFileSystem sliderFileSystem, Path tempPath, String libDir, String libLocalSrcDir)
specifier|public
specifier|static
name|void
name|addAllDependencyJars
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|providerResources
parameter_list|,
name|SliderFileSystem
name|sliderFileSystem
parameter_list|,
name|Path
name|tempPath
parameter_list|,
name|String
name|libDir
parameter_list|,
name|String
name|libLocalSrcDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
if|if
condition|(
name|SliderUtils
operator|.
name|isSet
argument_list|(
name|libLocalSrcDir
argument_list|)
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|libLocalSrcDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadCommandArgumentsException
argument_list|(
literal|"Supplied lib src dir %s is not valid"
argument_list|,
name|libLocalSrcDir
argument_list|)
throw|;
block|}
block|}
name|SliderUtils
operator|.
name|putAllJars
argument_list|(
name|providerResources
argument_list|,
name|sliderFileSystem
argument_list|,
name|tempPath
argument_list|,
name|libDir
argument_list|,
name|libLocalSrcDir
argument_list|)
expr_stmt|;
block|}
DECL|method|substituteStrWithTokens (String content, Map<String, String> tokensForSubstitution)
specifier|public
specifier|static
name|String
name|substituteStrWithTokens
parameter_list|(
name|String
name|content
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|token
range|:
name|tokensForSubstitution
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|content
operator|=
name|content
operator|.
name|replaceAll
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|token
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|content
return|;
block|}
comment|// configs will be substituted by corresponding env in tokenMap
DECL|method|substituteMapWithTokens (Map<String, String> configs, Map<String, String> tokenMap)
specifier|public
specifier|static
name|void
name|substituteMapWithTokens
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokenMap
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenMap
operator|!=
literal|null
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
name|String
argument_list|>
name|token
range|:
name|tokenMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|value
operator|=
name|value
operator|.
name|replaceAll
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|token
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|entry
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Localize the service keytabs for the service.    * @param launcher container launcher    * @param fileSystem file system    * @throws IOException trouble uploading to HDFS    */
DECL|method|localizeServiceKeytabs (AbstractLauncher launcher, SliderFileSystem fileSystem, Service service)
specifier|public
name|void
name|localizeServiceKeytabs
parameter_list|(
name|AbstractLauncher
name|launcher
parameter_list|,
name|SliderFileSystem
name|fileSystem
parameter_list|,
name|Service
name|service
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|service
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|keytabPathOnHost
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|YarnServiceConf
operator|.
name|KEY_AM_KEYTAB_LOCAL_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|SliderUtils
operator|.
name|isUnset
argument_list|(
name|keytabPathOnHost
argument_list|)
condition|)
block|{
name|String
name|amKeytabName
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|YarnServiceConf
operator|.
name|KEY_AM_LOGIN_KEYTAB_NAME
argument_list|)
decl_stmt|;
name|String
name|keytabDir
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|YarnServiceConf
operator|.
name|KEY_HDFS_KEYTAB_DIR
argument_list|)
decl_stmt|;
comment|// we need to localize the keytab files in the directory
name|Path
name|keytabDirPath
init|=
name|fileSystem
operator|.
name|buildKeytabPath
argument_list|(
name|keytabDir
argument_list|,
literal|null
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|serviceKeytabsDeployed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|fileSystem
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|keytabDirPath
argument_list|)
condition|)
block|{
name|FileStatus
index|[]
name|keytabs
init|=
name|fileSystem
operator|.
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
name|keytabDirPath
argument_list|)
decl_stmt|;
name|LocalResource
name|keytabRes
decl_stmt|;
for|for
control|(
name|FileStatus
name|keytab
range|:
name|keytabs
control|)
block|{
if|if
condition|(
operator|!
name|amKeytabName
operator|.
name|equals
argument_list|(
name|keytab
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|keytab
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".keytab"
argument_list|)
condition|)
block|{
name|serviceKeytabsDeployed
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Localizing keytab {}"
argument_list|,
name|keytab
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|keytabRes
operator|=
name|fileSystem
operator|.
name|createAmResource
argument_list|(
name|keytab
operator|.
name|getPath
argument_list|()
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|addLocalResource
argument_list|(
name|KEYTAB_DIR
operator|+
literal|"/"
operator|+
name|keytab
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|keytabRes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|serviceKeytabsDeployed
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No service keytabs for the service have been localized.  "
operator|+
literal|"If the service requires keytabs for secure operation, "
operator|+
literal|"please ensure that the required keytabs have been uploaded "
operator|+
literal|"to the folder {}"
argument_list|,
name|keytabDirPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// 1. Create all config files for a component on hdfs for localization
comment|// 2. Add the config file to localResource
DECL|method|createConfigFileAndAddLocalResource ( AbstractLauncher launcher, SliderFileSystem fs, Component component, Map<String, String> tokensForSubstitution, ComponentInstance instance, ServiceContext context)
specifier|public
specifier|static
specifier|synchronized
name|void
name|createConfigFileAndAddLocalResource
parameter_list|(
name|AbstractLauncher
name|launcher
parameter_list|,
name|SliderFileSystem
name|fs
parameter_list|,
name|Component
name|component
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|ServiceContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|compDir
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|fs
operator|.
name|getAppDir
argument_list|()
argument_list|,
literal|"components"
argument_list|)
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|compInstanceDir
init|=
operator|new
name|Path
argument_list|(
name|compDir
argument_list|,
name|instance
operator|.
name|getCompInstanceName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|compInstanceDir
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|instance
operator|.
name|getCompInstanceId
argument_list|()
operator|+
literal|": Creating dir on hdfs: "
operator|+
name|compInstanceDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|compInstanceDir
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setCompInstanceDir
argument_list|(
name|compInstanceDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Component instance conf dir already exists: "
operator|+
name|compInstanceDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Tokens substitution for component instance: "
operator|+
name|instance
operator|.
name|getCompInstanceName
argument_list|()
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
name|tokensForSubstitution
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ConfigFile
name|originalFile
range|:
name|component
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getFiles
argument_list|()
control|)
block|{
name|ConfigFile
name|configFile
init|=
name|originalFile
operator|.
name|copy
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
operator|new
name|Path
argument_list|(
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// substitute file name
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|token
range|:
name|tokensForSubstitution
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|configFile
operator|.
name|setDestFile
argument_list|(
name|configFile
operator|.
name|getDestFile
argument_list|()
operator|.
name|replaceAll
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|token
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Path
name|remoteFile
init|=
operator|new
name|Path
argument_list|(
name|compInstanceDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|remoteFile
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Saving config file on hdfs for component "
operator|+
name|instance
operator|.
name|getCompInstanceName
argument_list|()
operator|+
literal|": "
operator|+
name|configFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|configFile
operator|.
name|getSrcFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Load config file template
switch|switch
condition|(
name|configFile
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|HADOOP_XML
case|:
comment|// Hadoop_xml_template
name|resolveHadoopXmlTemplateAndSaveOnHdfs
argument_list|(
name|fs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|tokensForSubstitution
argument_list|,
name|configFile
argument_list|,
name|remoteFile
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|TEMPLATE
case|:
comment|// plain-template
name|resolvePlainTemplateAndSaveOnHdfs
argument_list|(
name|fs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|tokensForSubstitution
argument_list|,
name|configFile
argument_list|,
name|remoteFile
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
default|default:
name|log
operator|.
name|info
argument_list|(
literal|"Not supporting loading src_file for "
operator|+
name|configFile
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
comment|// non-template
name|resolveNonTemplateConfigsAndSaveOnHdfs
argument_list|(
name|fs
argument_list|,
name|tokensForSubstitution
argument_list|,
name|instance
argument_list|,
name|configFile
argument_list|,
name|fileName
argument_list|,
name|remoteFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add resource for localization
name|LocalResource
name|configResource
init|=
name|fs
operator|.
name|createAmResource
argument_list|(
name|remoteFile
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|)
decl_stmt|;
name|File
name|destFile
init|=
operator|new
name|File
argument_list|(
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|symlink
init|=
name|APP_CONF_DIR
operator|+
literal|"/"
operator|+
name|fileName
decl_stmt|;
if|if
condition|(
name|destFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|launcher
operator|.
name|addLocalResource
argument_list|(
name|symlink
argument_list|,
name|configResource
argument_list|,
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Add config file for localization: "
operator|+
name|symlink
operator|+
literal|" -> "
operator|+
name|configResource
operator|.
name|getResource
argument_list|()
operator|.
name|getFile
argument_list|()
operator|+
literal|", dest mount path: "
operator|+
name|configFile
operator|.
name|getDestFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|launcher
operator|.
name|addLocalResource
argument_list|(
name|symlink
argument_list|,
name|configResource
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Add config file for localization: "
operator|+
name|symlink
operator|+
literal|" -> "
operator|+
name|configResource
operator|.
name|getResource
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|resolveNonTemplateConfigsAndSaveOnHdfs (SliderFileSystem fs, Map<String, String> tokensForSubstitution, ComponentInstance instance, ConfigFile configFile, String fileName, Path remoteFile)
specifier|private
specifier|static
name|void
name|resolveNonTemplateConfigsAndSaveOnHdfs
parameter_list|(
name|SliderFileSystem
name|fs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|ConfigFile
name|configFile
parameter_list|,
name|String
name|fileName
parameter_list|,
name|Path
name|remoteFile
parameter_list|)
throws|throws
name|IOException
block|{
comment|// substitute non-template configs
name|substituteMapWithTokens
argument_list|(
name|configFile
operator|.
name|getProps
argument_list|()
argument_list|,
name|tokensForSubstitution
argument_list|)
expr_stmt|;
comment|// write configs onto hdfs
name|PublishedConfiguration
name|publishedConfiguration
init|=
operator|new
name|PublishedConfiguration
argument_list|(
name|fileName
argument_list|,
name|configFile
operator|.
name|getProps
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|remoteFile
argument_list|)
condition|)
block|{
name|PublishedConfigurationOutputter
name|configurationOutputter
init|=
name|PublishedConfigurationOutputter
operator|.
name|createOutputter
argument_list|(
name|ConfigFormat
operator|.
name|resolve
argument_list|(
name|configFile
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|publishedConfiguration
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|remoteFile
argument_list|)
init|)
block|{
name|configurationOutputter
operator|.
name|save
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Component instance = "
operator|+
name|instance
operator|.
name|getCompInstanceName
argument_list|()
operator|+
literal|", config file already exists: "
operator|+
name|remoteFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 1. substitute config template - only handle hadoop_xml format
comment|// 2. save on hdfs
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|resolveHadoopXmlTemplateAndSaveOnHdfs (FileSystem fs, Map<String, String> tokensForSubstitution, ConfigFile configFile, Path remoteFile, ServiceContext context)
specifier|private
specifier|static
name|void
name|resolveHadoopXmlTemplateAndSaveOnHdfs
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|,
name|ConfigFile
name|configFile
parameter_list|,
name|Path
name|remoteFile
parameter_list|,
name|ServiceContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
decl_stmt|;
try|try
block|{
name|conf
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|context
operator|.
name|configCache
operator|.
name|get
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Failed to load config file: "
operator|+
name|configFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// make a copy for substitution
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
name|confCopy
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|confCopy
operator|.
name|set
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
comment|// substitute properties
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configFile
operator|.
name|getProps
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|confCopy
operator|.
name|set
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
comment|// substitute env variables
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|confCopy
control|)
block|{
name|String
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
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
name|String
argument_list|>
name|token
range|:
name|tokensForSubstitution
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|val
operator|=
name|val
operator|.
name|replaceAll
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|token
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|token
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|confCopy
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// save on hdfs
try|try
init|(
name|OutputStream
name|output
init|=
name|fs
operator|.
name|create
argument_list|(
name|remoteFile
argument_list|)
init|)
block|{
name|confCopy
operator|.
name|writeXml
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Reading config from: "
operator|+
name|configFile
operator|.
name|getSrcFile
argument_list|()
operator|+
literal|", writing to: "
operator|+
name|remoteFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 1) read the template as a string
comment|// 2) do token substitution
comment|// 3) save on hdfs
DECL|method|resolvePlainTemplateAndSaveOnHdfs (FileSystem fs, Map<String, String> tokensForSubstitution, ConfigFile configFile, Path remoteFile, ServiceContext context)
specifier|private
specifier|static
name|void
name|resolvePlainTemplateAndSaveOnHdfs
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokensForSubstitution
parameter_list|,
name|ConfigFile
name|configFile
parameter_list|,
name|Path
name|remoteFile
parameter_list|,
name|ServiceContext
name|context
parameter_list|)
block|{
name|String
name|content
decl_stmt|;
try|try
block|{
name|content
operator|=
operator|(
name|String
operator|)
name|context
operator|.
name|configCache
operator|.
name|get
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Failed to load config file: "
operator|+
name|configFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// substitute tokens
name|content
operator|=
name|substituteStrWithTokens
argument_list|(
name|content
argument_list|,
name|tokensForSubstitution
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|output
init|=
name|fs
operator|.
name|create
argument_list|(
name|remoteFile
argument_list|)
init|)
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|write
argument_list|(
name|content
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Failed to create "
operator|+
name|remoteFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get initial component token map to be substituted into config values.    * @return tokens to replace    */
DECL|method|initCompTokensForSubstitute ( ComponentInstance instance)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initCompTokensForSubstitute
parameter_list|(
name|ComponentInstance
name|instance
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokens
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|COMPONENT_NAME
argument_list|,
name|instance
operator|.
name|getCompSpec
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|COMPONENT_NAME_LC
argument_list|,
name|instance
operator|.
name|getCompSpec
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|COMPONENT_INSTANCE_NAME
argument_list|,
name|instance
operator|.
name|getCompInstanceName
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|CONTAINER_ID
argument_list|,
name|instance
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|COMPONENT_ID
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|instance
operator|.
name|getCompInstanceId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|putAll
argument_list|(
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getDependencyHostIpTokens
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tokens
return|;
block|}
block|}
end_class

end_unit

