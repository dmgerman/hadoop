begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.param
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
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
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|ParseException
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|CliConstants
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|Command
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|PyTorchRunJobParameters
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|TensorFlowRunJobParameters
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|Configs
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|Role
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|Roles
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|Scheduling
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|Security
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|TensorBoard
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|YamlConfigFile
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|YamlParseException
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|Framework
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
name|submarine
operator|.
name|common
operator|.
name|ClientContext
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
name|List
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|runjob
operator|.
name|RunJobCli
operator|.
name|YAML_PARSE_FAILED
import|;
end_import

begin_comment
comment|/**  * This class acts as a wrapper of {@code CommandLine} values along with  * YAML configuration values.  * YAML configuration is only stored if the -f&lt;filename&gt;  * option is specified along the CLI arguments.  * Using this wrapper class makes easy to deal with  * any form of configuration source potentially added into Submarine,  * in the future.  * If both YAML and CLI value is found for a config, this is an error case.  */
end_comment

begin_class
DECL|class|ParametersHolder
specifier|public
specifier|final
class|class
name|ParametersHolder
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
name|ParametersHolder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SUPPORTED_FRAMEWORKS_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|SUPPORTED_FRAMEWORKS_MESSAGE
init|=
literal|"TensorFlow and PyTorch are the only supported frameworks for now!"
decl_stmt|;
DECL|field|SUPPORTED_COMMANDS_MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|SUPPORTED_COMMANDS_MESSAGE
init|=
literal|"'Show job' and 'run job' are the only supported commands for now!"
decl_stmt|;
DECL|field|parsedCommandLine
specifier|private
specifier|final
name|CommandLine
name|parsedCommandLine
decl_stmt|;
DECL|field|yamlStringConfigs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlStringConfigs
decl_stmt|;
DECL|field|yamlListConfigs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|yamlListConfigs
decl_stmt|;
DECL|field|configType
specifier|private
specifier|final
name|ConfigType
name|configType
decl_stmt|;
DECL|field|command
specifier|private
name|Command
name|command
decl_stmt|;
DECL|field|onlyDefinedWithCliArgs
specifier|private
specifier|final
name|Set
name|onlyDefinedWithCliArgs
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|CliConstants
operator|.
name|VERBOSE
argument_list|)
decl_stmt|;
DECL|field|framework
specifier|private
specifier|final
name|Framework
name|framework
decl_stmt|;
DECL|field|parameters
specifier|private
specifier|final
name|BaseParameters
name|parameters
decl_stmt|;
DECL|method|ParametersHolder (CommandLine parsedCommandLine, YamlConfigFile yamlConfig, ConfigType configType, Command command)
specifier|private
name|ParametersHolder
parameter_list|(
name|CommandLine
name|parsedCommandLine
parameter_list|,
name|YamlConfigFile
name|yamlConfig
parameter_list|,
name|ConfigType
name|configType
parameter_list|,
name|Command
name|command
parameter_list|)
throws|throws
name|ParseException
throws|,
name|YarnException
block|{
name|this
operator|.
name|parsedCommandLine
operator|=
name|parsedCommandLine
expr_stmt|;
name|this
operator|.
name|yamlStringConfigs
operator|=
name|initStringConfigValues
argument_list|(
name|yamlConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|yamlListConfigs
operator|=
name|initListConfigValues
argument_list|(
name|yamlConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|configType
operator|=
name|configType
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
name|this
operator|.
name|framework
operator|=
name|determineFrameworkType
argument_list|()
expr_stmt|;
name|this
operator|.
name|ensureOnlyValidSectionsAreDefined
argument_list|(
name|yamlConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|createParameters
argument_list|()
expr_stmt|;
block|}
DECL|method|createParameters ()
specifier|private
name|BaseParameters
name|createParameters
parameter_list|()
block|{
if|if
condition|(
name|command
operator|==
name|Command
operator|.
name|RUN_JOB
condition|)
block|{
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|TENSORFLOW
condition|)
block|{
return|return
operator|new
name|TensorFlowRunJobParameters
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|framework
operator|==
name|Framework
operator|.
name|PYTORCH
condition|)
block|{
return|return
operator|new
name|PyTorchRunJobParameters
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|SUPPORTED_FRAMEWORKS_MESSAGE
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|command
operator|==
name|Command
operator|.
name|SHOW_JOB
condition|)
block|{
return|return
operator|new
name|ShowJobParameters
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|SUPPORTED_COMMANDS_MESSAGE
argument_list|)
throw|;
block|}
block|}
DECL|method|ensureOnlyValidSectionsAreDefined (YamlConfigFile yamlConfig)
specifier|private
name|void
name|ensureOnlyValidSectionsAreDefined
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|)
block|{
if|if
condition|(
name|isCommandRunJob
argument_list|()
operator|&&
name|isFrameworkPyTorch
argument_list|()
operator|&&
name|isPsSectionDefined
argument_list|(
name|yamlConfig
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YamlParseException
argument_list|(
literal|"PS section should not be defined when PyTorch "
operator|+
literal|"is the selected framework!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isCommandRunJob
argument_list|()
operator|&&
name|isFrameworkPyTorch
argument_list|()
operator|&&
name|isTensorboardSectionDefined
argument_list|(
name|yamlConfig
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YamlParseException
argument_list|(
literal|"TensorBoard section should not be defined when PyTorch "
operator|+
literal|"is the selected framework!"
argument_list|)
throw|;
block|}
block|}
DECL|method|isCommandRunJob ()
specifier|private
name|boolean
name|isCommandRunJob
parameter_list|()
block|{
return|return
name|command
operator|==
name|Command
operator|.
name|RUN_JOB
return|;
block|}
DECL|method|isFrameworkPyTorch ()
specifier|private
name|boolean
name|isFrameworkPyTorch
parameter_list|()
block|{
return|return
name|framework
operator|==
name|Framework
operator|.
name|PYTORCH
return|;
block|}
DECL|method|isPsSectionDefined (YamlConfigFile yamlConfig)
specifier|private
name|boolean
name|isPsSectionDefined
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|)
block|{
return|return
name|yamlConfig
operator|!=
literal|null
operator|&&
name|yamlConfig
operator|.
name|getRoles
argument_list|()
operator|!=
literal|null
operator|&&
name|yamlConfig
operator|.
name|getRoles
argument_list|()
operator|.
name|getPs
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|isTensorboardSectionDefined (YamlConfigFile yamlConfig)
specifier|private
name|boolean
name|isTensorboardSectionDefined
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|)
block|{
return|return
name|yamlConfig
operator|!=
literal|null
operator|&&
name|yamlConfig
operator|.
name|getTensorBoard
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|determineFrameworkType ()
specifier|private
name|Framework
name|determineFrameworkType
parameter_list|()
throws|throws
name|ParseException
throws|,
name|YarnException
block|{
if|if
condition|(
operator|!
name|isCommandRunJob
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|frameworkStr
init|=
name|getOptionValue
argument_list|(
name|CliConstants
operator|.
name|FRAMEWORK
argument_list|)
decl_stmt|;
if|if
condition|(
name|frameworkStr
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Framework is not defined in config, falling back to "
operator|+
literal|"TensorFlow as a default."
argument_list|)
expr_stmt|;
return|return
name|Framework
operator|.
name|TENSORFLOW
return|;
block|}
name|Framework
name|framework
init|=
name|Framework
operator|.
name|parseByValue
argument_list|(
name|frameworkStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|framework
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|getConfigType
argument_list|()
operator|==
name|ConfigType
operator|.
name|CLI
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Failed to parse Framework type! "
operator|+
literal|"Valid values are: "
operator|+
name|Framework
operator|.
name|getValues
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|YamlParseException
argument_list|(
name|YAML_PARSE_FAILED
operator|+
literal|", framework should is defined, but it has an invalid value! "
operator|+
literal|"Valid values are: "
operator|+
name|Framework
operator|.
name|getValues
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|framework
return|;
block|}
comment|/**    * Maps every value coming from the passed yamlConfig to {@code CliConstants}.    * @param yamlConfig Parsed YAML config    * @return A map of config values, keys are {@code CliConstants}    * and values are Strings.    */
DECL|method|initStringConfigValues ( YamlConfigFile yamlConfig)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initStringConfigValues
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|)
block|{
if|if
condition|(
name|yamlConfig
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigValues
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Roles
name|roles
init|=
name|yamlConfig
operator|.
name|getRoles
argument_list|()
decl_stmt|;
name|initGenericConfigs
argument_list|(
name|yamlConfig
argument_list|,
name|yamlConfigValues
argument_list|)
expr_stmt|;
name|initPs
argument_list|(
name|yamlConfigValues
argument_list|,
name|roles
operator|.
name|getPs
argument_list|()
argument_list|)
expr_stmt|;
name|initWorker
argument_list|(
name|yamlConfigValues
argument_list|,
name|roles
operator|.
name|getWorker
argument_list|()
argument_list|)
expr_stmt|;
name|initScheduling
argument_list|(
name|yamlConfigValues
argument_list|,
name|yamlConfig
operator|.
name|getScheduling
argument_list|()
argument_list|)
expr_stmt|;
name|initSecurity
argument_list|(
name|yamlConfigValues
argument_list|,
name|yamlConfig
operator|.
name|getSecurity
argument_list|()
argument_list|)
expr_stmt|;
name|initTensorBoard
argument_list|(
name|yamlConfigValues
argument_list|,
name|yamlConfig
operator|.
name|getTensorBoard
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|yamlConfigValues
return|;
block|}
DECL|method|initListConfigValues ( YamlConfigFile yamlConfig)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|initListConfigValues
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|)
block|{
if|if
condition|(
name|yamlConfig
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|yamlConfigValues
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Configs
name|configs
init|=
name|yamlConfig
operator|.
name|getConfigs
argument_list|()
decl_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|LOCALIZATION
argument_list|,
name|configs
operator|.
name|getLocalizations
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|ENV
argument_list|,
name|convertToEnvsList
argument_list|(
name|configs
operator|.
name|getEnvs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|QUICKLINK
argument_list|,
name|configs
operator|.
name|getQuicklinks
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|yamlConfigValues
return|;
block|}
DECL|method|initGenericConfigs (YamlConfigFile yamlConfig, Map<String, String> yamlConfigs)
specifier|private
name|void
name|initGenericConfigs
parameter_list|(
name|YamlConfigFile
name|yamlConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigs
parameter_list|)
block|{
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|NAME
argument_list|,
name|yamlConfig
operator|.
name|getSpec
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|FRAMEWORK
argument_list|,
name|yamlConfig
operator|.
name|getSpec
argument_list|()
operator|.
name|getFramework
argument_list|()
argument_list|)
expr_stmt|;
name|Configs
name|configs
init|=
name|yamlConfig
operator|.
name|getConfigs
argument_list|()
decl_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|INPUT_PATH
argument_list|,
name|configs
operator|.
name|getInputPath
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|CHECKPOINT_PATH
argument_list|,
name|configs
operator|.
name|getCheckpointPath
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|SAVED_MODEL_PATH
argument_list|,
name|configs
operator|.
name|getSavedModelPath
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|DOCKER_IMAGE
argument_list|,
name|configs
operator|.
name|getDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|WAIT_JOB_FINISH
argument_list|,
name|configs
operator|.
name|getWaitJobFinish
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initPs (Map<String, String> yamlConfigs, Role ps)
specifier|private
name|void
name|initPs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigs
parameter_list|,
name|Role
name|ps
parameter_list|)
block|{
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|N_PS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ps
operator|.
name|getReplicas
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|PS_RES
argument_list|,
name|ps
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|PS_DOCKER_IMAGE
argument_list|,
name|ps
operator|.
name|getDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|PS_LAUNCH_CMD
argument_list|,
name|ps
operator|.
name|getLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initWorker (Map<String, String> yamlConfigs, Role worker)
specifier|private
name|void
name|initWorker
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigs
parameter_list|,
name|Role
name|worker
parameter_list|)
block|{
if|if
condition|(
name|worker
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|N_WORKERS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|worker
operator|.
name|getReplicas
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|WORKER_RES
argument_list|,
name|worker
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|WORKER_DOCKER_IMAGE
argument_list|,
name|worker
operator|.
name|getDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigs
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|WORKER_LAUNCH_CMD
argument_list|,
name|worker
operator|.
name|getLaunchCmd
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initScheduling (Map<String, String> yamlConfigValues, Scheduling scheduling)
specifier|private
name|void
name|initScheduling
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigValues
parameter_list|,
name|Scheduling
name|scheduling
parameter_list|)
block|{
if|if
condition|(
name|scheduling
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|QUEUE
argument_list|,
name|scheduling
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initSecurity (Map<String, String> yamlConfigValues, Security security)
specifier|private
name|void
name|initSecurity
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigValues
parameter_list|,
name|Security
name|security
parameter_list|)
block|{
if|if
condition|(
name|security
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|KEYTAB
argument_list|,
name|security
operator|.
name|getKeytab
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|PRINCIPAL
argument_list|,
name|security
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|DISTRIBUTE_KEYTAB
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|security
operator|.
name|isDistributeKeytab
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initTensorBoard (Map<String, String> yamlConfigValues, TensorBoard tensorBoard)
specifier|private
name|void
name|initTensorBoard
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|yamlConfigValues
parameter_list|,
name|TensorBoard
name|tensorBoard
parameter_list|)
block|{
if|if
condition|(
name|tensorBoard
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD_DOCKER_IMAGE
argument_list|,
name|tensorBoard
operator|.
name|getDockerImage
argument_list|()
argument_list|)
expr_stmt|;
name|yamlConfigValues
operator|.
name|put
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD_RESOURCES
argument_list|,
name|tensorBoard
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToEnvsList (Map<String, String> envs)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|convertToEnvsList
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envs
parameter_list|)
block|{
if|if
condition|(
name|envs
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|envs
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|e
lambda|->
name|String
operator|.
name|format
argument_list|(
literal|"%s=%s"
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createWithCmdLine (CommandLine cli, Command command)
specifier|public
specifier|static
name|ParametersHolder
name|createWithCmdLine
parameter_list|(
name|CommandLine
name|cli
parameter_list|,
name|Command
name|command
parameter_list|)
throws|throws
name|ParseException
throws|,
name|YarnException
block|{
return|return
operator|new
name|ParametersHolder
argument_list|(
name|cli
argument_list|,
literal|null
argument_list|,
name|ConfigType
operator|.
name|CLI
argument_list|,
name|command
argument_list|)
return|;
block|}
DECL|method|createWithCmdLineAndYaml (CommandLine cli, YamlConfigFile yamlConfig, Command command)
specifier|public
specifier|static
name|ParametersHolder
name|createWithCmdLineAndYaml
parameter_list|(
name|CommandLine
name|cli
parameter_list|,
name|YamlConfigFile
name|yamlConfig
parameter_list|,
name|Command
name|command
parameter_list|)
throws|throws
name|ParseException
throws|,
name|YarnException
block|{
return|return
operator|new
name|ParametersHolder
argument_list|(
name|cli
argument_list|,
name|yamlConfig
argument_list|,
name|ConfigType
operator|.
name|YAML
argument_list|,
name|command
argument_list|)
return|;
block|}
comment|/**    * Gets the option value, either from the CLI arguments or YAML config,    * if present.    * @param option Name of the config.    * @return The value of the config    */
DECL|method|getOptionValue (String option)
specifier|public
name|String
name|getOptionValue
parameter_list|(
name|String
name|option
parameter_list|)
throws|throws
name|YarnException
block|{
name|ensureConfigIsDefinedOnce
argument_list|(
name|option
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|onlyDefinedWithCliArgs
operator|.
name|contains
argument_list|(
name|option
argument_list|)
operator|||
name|parsedCommandLine
operator|.
name|hasOption
argument_list|(
name|option
argument_list|)
condition|)
block|{
return|return
name|getValueFromCLI
argument_list|(
name|option
argument_list|)
return|;
block|}
return|return
name|getValueFromYaml
argument_list|(
name|option
argument_list|)
return|;
block|}
comment|/**    * Gets the option values, either from the CLI arguments or YAML config,    * if present.    * @param option Name of the config.    * @return The values of the config    */
DECL|method|getOptionValues (String option)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getOptionValues
parameter_list|(
name|String
name|option
parameter_list|)
throws|throws
name|YarnException
block|{
name|ensureConfigIsDefinedOnce
argument_list|(
name|option
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|onlyDefinedWithCliArgs
operator|.
name|contains
argument_list|(
name|option
argument_list|)
operator|||
name|parsedCommandLine
operator|.
name|hasOption
argument_list|(
name|option
argument_list|)
condition|)
block|{
return|return
name|getValuesFromCLI
argument_list|(
name|option
argument_list|)
return|;
block|}
return|return
name|getValuesFromYaml
argument_list|(
name|option
argument_list|)
return|;
block|}
DECL|method|ensureConfigIsDefinedOnce (String option, boolean stringValue)
specifier|private
name|void
name|ensureConfigIsDefinedOnce
parameter_list|(
name|String
name|option
parameter_list|,
name|boolean
name|stringValue
parameter_list|)
throws|throws
name|YarnException
block|{
name|boolean
name|definedWithYaml
decl_stmt|;
if|if
condition|(
name|stringValue
condition|)
block|{
name|definedWithYaml
operator|=
name|yamlStringConfigs
operator|.
name|containsKey
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|definedWithYaml
operator|=
name|yamlListConfigs
operator|.
name|containsKey
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedCommandLine
operator|.
name|hasOption
argument_list|(
name|option
argument_list|)
operator|&&
name|definedWithYaml
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Config '%s' is defined both with YAML config"
operator|+
literal|" and with CLI argument, please only use either way!"
argument_list|)
throw|;
block|}
block|}
DECL|method|getValueFromCLI (String option)
specifier|private
name|String
name|getValueFromCLI
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
name|value
init|=
name|parsedCommandLine
operator|.
name|getOptionValue
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found config value {} for key {} "
operator|+
literal|"from CLI configuration."
argument_list|,
name|value
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
DECL|method|getValuesFromCLI (String option)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getValuesFromCLI
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
index|[]
name|optionValues
init|=
name|parsedCommandLine
operator|.
name|getOptionValues
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|optionValues
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|optionValues
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found config values {} for key {} "
operator|+
literal|"from CLI configuration."
argument_list|,
name|values
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No config values found for key {} "
operator|+
literal|"from CLI configuration."
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|Lists
operator|.
name|newArrayList
argument_list|()
return|;
block|}
block|}
DECL|method|getValueFromYaml (String option)
specifier|private
name|String
name|getValueFromYaml
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
name|value
init|=
name|yamlStringConfigs
operator|.
name|get
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found config value {} for key {} "
operator|+
literal|"from YAML configuration."
argument_list|,
name|value
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
DECL|method|getValuesFromYaml (String option)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getValuesFromYaml
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|yamlListConfigs
operator|.
name|get
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found config values {} for key {} "
operator|+
literal|"from YAML configuration."
argument_list|,
name|values
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|/**    * Returns the boolean value of option.    * First, we check if the CLI value is defined for the option.    * If not, then we check the YAML value.    * @param option name of the option    * @return true, if the option is found in the CLI args or in the YAML config,    * false otherwise.    */
DECL|method|hasOption (String option)
specifier|public
name|boolean
name|hasOption
parameter_list|(
name|String
name|option
parameter_list|)
block|{
if|if
condition|(
name|onlyDefinedWithCliArgs
operator|.
name|contains
argument_list|(
name|option
argument_list|)
condition|)
block|{
name|boolean
name|value
init|=
name|parsedCommandLine
operator|.
name|hasOption
argument_list|(
name|option
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found boolean config with value {} for key {} "
operator|+
literal|"from CLI configuration."
argument_list|,
name|value
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
if|if
condition|(
name|parsedCommandLine
operator|.
name|hasOption
argument_list|(
name|option
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found boolean config value for key {} "
operator|+
literal|"from CLI configuration."
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
name|getBooleanValueFromYaml
argument_list|(
name|option
argument_list|)
return|;
block|}
DECL|method|getBooleanValueFromYaml (String option)
specifier|private
name|boolean
name|getBooleanValueFromYaml
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|String
name|stringValue
init|=
name|yamlStringConfigs
operator|.
name|get
argument_list|(
name|option
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|stringValue
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|valueOf
argument_list|(
name|stringValue
argument_list|)
operator|.
name|equals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found config value {} for key {} "
operator|+
literal|"from YAML configuration."
argument_list|,
name|result
argument_list|,
name|option
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getConfigType ()
specifier|public
name|ConfigType
name|getConfigType
parameter_list|()
block|{
return|return
name|configType
return|;
block|}
DECL|method|getFramework ()
specifier|public
name|Framework
name|getFramework
parameter_list|()
block|{
return|return
name|framework
return|;
block|}
DECL|method|updateParameters (ClientContext clientContext)
specifier|public
name|void
name|updateParameters
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
throws|throws
name|ParseException
throws|,
name|YarnException
throws|,
name|IOException
block|{
name|parameters
operator|.
name|updateParameters
argument_list|(
name|this
argument_list|,
name|clientContext
argument_list|)
expr_stmt|;
block|}
DECL|method|getParameters ()
specifier|public
name|BaseParameters
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
block|}
end_class

end_unit

