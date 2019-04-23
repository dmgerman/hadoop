begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli
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
name|GnuParser
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
name|HelpFormatter
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
name|Options
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|ApplicationId
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
name|param
operator|.
name|ParametersHolder
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
name|RunJobParameters
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
name|RunJobParameters
operator|.
name|UnderscoreConverterPropertyUtils
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
name|common
operator|.
name|ClientContext
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
name|exception
operator|.
name|SubmarineException
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
name|runtimes
operator|.
name|common
operator|.
name|JobMonitor
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
name|runtimes
operator|.
name|common
operator|.
name|JobSubmitter
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
name|runtimes
operator|.
name|common
operator|.
name|StorageKeyConstants
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
name|yaml
operator|.
name|snakeyaml
operator|.
name|Yaml
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|Constructor
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

begin_class
DECL|class|RunJobCli
specifier|public
class|class
name|RunJobCli
extends|extends
name|AbstractCli
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
name|RunJobCli
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|YAML_PARSE_FAILED
specifier|private
specifier|static
specifier|final
name|String
name|YAML_PARSE_FAILED
init|=
literal|"Failed to parse "
operator|+
literal|"YAML config"
decl_stmt|;
DECL|field|options
specifier|private
name|Options
name|options
decl_stmt|;
DECL|field|parameters
specifier|private
name|RunJobParameters
name|parameters
init|=
operator|new
name|RunJobParameters
argument_list|()
decl_stmt|;
DECL|field|jobSubmitter
specifier|private
name|JobSubmitter
name|jobSubmitter
decl_stmt|;
DECL|field|jobMonitor
specifier|private
name|JobMonitor
name|jobMonitor
decl_stmt|;
DECL|method|RunJobCli (ClientContext cliContext)
specifier|public
name|RunJobCli
parameter_list|(
name|ClientContext
name|cliContext
parameter_list|)
block|{
name|this
argument_list|(
name|cliContext
argument_list|,
name|cliContext
operator|.
name|getRuntimeFactory
argument_list|()
operator|.
name|getJobSubmitterInstance
argument_list|()
argument_list|,
name|cliContext
operator|.
name|getRuntimeFactory
argument_list|()
operator|.
name|getJobMonitorInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|RunJobCli (ClientContext cliContext, JobSubmitter jobSubmitter, JobMonitor jobMonitor)
name|RunJobCli
parameter_list|(
name|ClientContext
name|cliContext
parameter_list|,
name|JobSubmitter
name|jobSubmitter
parameter_list|,
name|JobMonitor
name|jobMonitor
parameter_list|)
block|{
name|super
argument_list|(
name|cliContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|generateOptions
argument_list|()
expr_stmt|;
name|this
operator|.
name|jobSubmitter
operator|=
name|jobSubmitter
expr_stmt|;
name|this
operator|.
name|jobMonitor
operator|=
name|jobMonitor
expr_stmt|;
block|}
DECL|method|printUsages ()
specifier|public
name|void
name|printUsages
parameter_list|()
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"job run"
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
DECL|method|generateOptions ()
specifier|private
name|Options
name|generateOptions
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|YAML_CONFIG
argument_list|,
literal|true
argument_list|,
literal|"Config file (in YAML format)"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|NAME
argument_list|,
literal|true
argument_list|,
literal|"Name of the job"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|INPUT_PATH
argument_list|,
literal|true
argument_list|,
literal|"Input of the job, could be local or other FS directory"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|CHECKPOINT_PATH
argument_list|,
literal|true
argument_list|,
literal|"Training output directory of the job, "
operator|+
literal|"could be local or other FS directory. This typically includes "
operator|+
literal|"checkpoint files and exported model "
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|SAVED_MODEL_PATH
argument_list|,
literal|true
argument_list|,
literal|"Model exported path (savedmodel) of the job, which is needed when "
operator|+
literal|"exported model is not placed under ${checkpoint_path}"
operator|+
literal|"could be local or other FS directory. This will be used to serve."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|N_WORKERS
argument_list|,
literal|true
argument_list|,
literal|"Number of worker tasks of the job, by default it's 1"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|N_PS
argument_list|,
literal|true
argument_list|,
literal|"Number of PS tasks of the job, by default it's 0"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|WORKER_RES
argument_list|,
literal|true
argument_list|,
literal|"Resource of each worker, for example "
operator|+
literal|"memory-mb=2048,vcores=2,yarn.io/gpu=2"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|PS_RES
argument_list|,
literal|true
argument_list|,
literal|"Resource of each PS, for example "
operator|+
literal|"memory-mb=2048,vcores=2,yarn.io/gpu=2"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|DOCKER_IMAGE
argument_list|,
literal|true
argument_list|,
literal|"Docker image name/tag"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|QUEUE
argument_list|,
literal|true
argument_list|,
literal|"Name of queue to run the job, by default it uses default queue"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD
argument_list|,
literal|false
argument_list|,
literal|"Should we run TensorBoard"
operator|+
literal|" for this job? By default it's disabled"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD_RESOURCES
argument_list|,
literal|true
argument_list|,
literal|"Specify resources of Tensorboard, by default it is "
operator|+
name|CliConstants
operator|.
name|TENSORBOARD_DEFAULT_RESOURCES
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|TENSORBOARD_DOCKER_IMAGE
argument_list|,
literal|true
argument_list|,
literal|"Specify Tensorboard docker image. when this is not "
operator|+
literal|"specified, Tensorboard "
operator|+
literal|"uses --"
operator|+
name|CliConstants
operator|.
name|DOCKER_IMAGE
operator|+
literal|" as default."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|WORKER_LAUNCH_CMD
argument_list|,
literal|true
argument_list|,
literal|"Commandline of worker, arguments will be "
operator|+
literal|"directly used to launch the worker"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|PS_LAUNCH_CMD
argument_list|,
literal|true
argument_list|,
literal|"Commandline of worker, arguments will be "
operator|+
literal|"directly used to launch the PS"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|ENV
argument_list|,
literal|true
argument_list|,
literal|"Common environment variable of worker/ps"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|VERBOSE
argument_list|,
literal|false
argument_list|,
literal|"Print verbose log for troubleshooting"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|WAIT_JOB_FINISH
argument_list|,
literal|false
argument_list|,
literal|"Specified when user want to wait the job finish"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|PS_DOCKER_IMAGE
argument_list|,
literal|true
argument_list|,
literal|"Specify docker image for PS, when this is not specified, PS uses --"
operator|+
name|CliConstants
operator|.
name|DOCKER_IMAGE
operator|+
literal|" as default."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|WORKER_DOCKER_IMAGE
argument_list|,
literal|true
argument_list|,
literal|"Specify docker image for WORKER, when this is not specified, WORKER "
operator|+
literal|"uses --"
operator|+
name|CliConstants
operator|.
name|DOCKER_IMAGE
operator|+
literal|" as default."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|QUICKLINK
argument_list|,
literal|true
argument_list|,
literal|"Specify quicklink so YARN"
operator|+
literal|"web UI shows link to given role instance and port. When "
operator|+
literal|"--tensorboard is specified, quicklink to tensorboard instance will "
operator|+
literal|"be added automatically. The format of quick link is: "
operator|+
literal|"Quick_link_label=http(or https)://role-name:port. For example, "
operator|+
literal|"if want to link to first worker's 7070 port, and text of quicklink "
operator|+
literal|"is Notebook_UI, user need to specify --quicklink "
operator|+
literal|"Notebook_UI=https://master-0:7070"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|LOCALIZATION
argument_list|,
literal|true
argument_list|,
literal|"Specify"
operator|+
literal|" localization to make remote/local file/directory available to"
operator|+
literal|" all container(Docker)."
operator|+
literal|" Argument format is \"RemoteUri:LocalFilePath[:rw] \" (ro"
operator|+
literal|" permission is not supported yet)"
operator|+
literal|" The RemoteUri can be a file or directory in local or"
operator|+
literal|" HDFS or s3 or abfs or http .etc."
operator|+
literal|" The LocalFilePath can be absolute or relative."
operator|+
literal|" If it's a relative path, it'll be"
operator|+
literal|" under container's implied working directory"
operator|+
literal|" but sub directory is not supported yet."
operator|+
literal|" This option can be set mutiple times."
operator|+
literal|" Examples are \n"
operator|+
literal|"-localization \"hdfs:///user/yarn/mydir2:/opt/data\"\n"
operator|+
literal|"-localization \"s3a:///a/b/myfile1:./\"\n"
operator|+
literal|"-localization \"https:///a/b/myfile2:./myfile\"\n"
operator|+
literal|"-localization \"/user/yarn/mydir3:/opt/mydir3\"\n"
operator|+
literal|"-localization \"./mydir1:.\"\n"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|KEYTAB
argument_list|,
literal|true
argument_list|,
literal|"Specify keytab used by the "
operator|+
literal|"job under security environment"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|PRINCIPAL
argument_list|,
literal|true
argument_list|,
literal|"Specify principal used "
operator|+
literal|"by the job under security environment"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|CliConstants
operator|.
name|DISTRIBUTE_KEYTAB
argument_list|,
literal|false
argument_list|,
literal|"Distribute "
operator|+
literal|"local keytab to cluster machines for service authentication. If not "
operator|+
literal|"specified, pre-distributed keytab of which path specified by"
operator|+
literal|" parameter"
operator|+
name|CliConstants
operator|.
name|KEYTAB
operator|+
literal|" on cluster machines will be "
operator|+
literal|"used"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"Print help"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"insecure"
argument_list|,
literal|false
argument_list|,
literal|"Cluster is not Kerberos enabled."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"conf"
argument_list|,
literal|true
argument_list|,
literal|"User specified configuration, as key=val pairs."
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
DECL|method|replacePatternsInParameters ()
specifier|private
name|void
name|replacePatternsInParameters
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|parameters
operator|.
name|getPSLaunchCmd
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|parameters
operator|.
name|getPSLaunchCmd
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|afterReplace
init|=
name|CliUtils
operator|.
name|replacePatternsInLaunchCommand
argument_list|(
name|parameters
operator|.
name|getPSLaunchCmd
argument_list|()
argument_list|,
name|parameters
argument_list|,
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|setPSLaunchCmd
argument_list|(
name|afterReplace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|afterReplace
init|=
name|CliUtils
operator|.
name|replacePatternsInLaunchCommand
argument_list|(
name|parameters
operator|.
name|getWorkerLaunchCmd
argument_list|()
argument_list|,
name|parameters
argument_list|,
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|setWorkerLaunchCmd
argument_list|(
name|afterReplace
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseCommandLineAndGetRunJobParameters (String[] args)
specifier|private
name|void
name|parseCommandLineAndGetRunJobParameters
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
throws|,
name|YarnException
block|{
try|try
block|{
comment|// Do parsing
name|GnuParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParametersHolder
name|parametersHolder
init|=
name|createParametersHolder
argument_list|(
name|cli
argument_list|)
decl_stmt|;
name|parameters
operator|.
name|updateParameters
argument_list|(
name|parametersHolder
argument_list|,
name|clientContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in parse: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|printUsages
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// Set default job dir / saved model dir, etc.
name|setDefaultDirs
argument_list|()
expr_stmt|;
comment|// replace patterns
name|replacePatternsInParameters
argument_list|()
expr_stmt|;
block|}
DECL|method|createParametersHolder (CommandLine cli)
specifier|private
name|ParametersHolder
name|createParametersHolder
parameter_list|(
name|CommandLine
name|cli
parameter_list|)
block|{
name|String
name|yamlConfigFile
init|=
name|cli
operator|.
name|getOptionValue
argument_list|(
name|CliConstants
operator|.
name|YAML_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|yamlConfigFile
operator|!=
literal|null
condition|)
block|{
name|YamlConfigFile
name|yamlConfig
init|=
name|readYamlConfigFile
argument_list|(
name|yamlConfigFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|yamlConfig
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YamlParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YAML_PARSE_FAILED
operator|+
literal|", file is empty: %s"
argument_list|,
name|yamlConfigFile
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|yamlConfig
operator|.
name|getConfigs
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YamlParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YAML_PARSE_FAILED
operator|+
literal|", config section should be defined, but it cannot be found in "
operator|+
literal|"YAML file '%s'!"
argument_list|,
name|yamlConfigFile
argument_list|)
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using YAML configuration!"
argument_list|)
expr_stmt|;
return|return
name|ParametersHolder
operator|.
name|createWithCmdLineAndYaml
argument_list|(
name|cli
argument_list|,
name|yamlConfig
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using CLI configuration!"
argument_list|)
expr_stmt|;
return|return
name|ParametersHolder
operator|.
name|createWithCmdLine
argument_list|(
name|cli
argument_list|)
return|;
block|}
block|}
DECL|method|readYamlConfigFile (String filename)
specifier|private
name|YamlConfigFile
name|readYamlConfigFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|Constructor
name|constructor
init|=
operator|new
name|Constructor
argument_list|(
name|YamlConfigFile
operator|.
name|class
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|setPropertyUtils
argument_list|(
operator|new
name|UnderscoreConverterPropertyUtils
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading YAML configuration from file: {}"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|Yaml
name|yaml
init|=
operator|new
name|Yaml
argument_list|(
name|constructor
argument_list|)
decl_stmt|;
return|return
name|yaml
operator|.
name|loadAs
argument_list|(
name|FileUtils
operator|.
name|openInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|filename
argument_list|)
argument_list|)
argument_list|,
name|YamlConfigFile
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|logExceptionOfYamlParse
argument_list|(
name|filename
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YamlParseException
argument_list|(
name|YAML_PARSE_FAILED
operator|+
literal|", file does not exist!"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logExceptionOfYamlParse
argument_list|(
name|filename
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YamlParseException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YAML_PARSE_FAILED
operator|+
literal|", details: %s"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|logExceptionOfYamlParse (String filename, Exception e)
specifier|private
name|void
name|logExceptionOfYamlParse
parameter_list|(
name|String
name|filename
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exception while parsing YAML file %s"
argument_list|,
name|filename
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|setDefaultDirs ()
specifier|private
name|void
name|setDefaultDirs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Create directories if needed
name|String
name|jobDir
init|=
name|parameters
operator|.
name|getCheckpointPath
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|jobDir
condition|)
block|{
if|if
condition|(
name|parameters
operator|.
name|getNumWorkers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|jobDir
operator|=
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
operator|.
name|getJobCheckpointDir
argument_list|(
name|parameters
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// when #workers == 0, it means we only launch TB. In that case,
comment|// point job dir to root dir so all job's metrics will be shown.
name|jobDir
operator|=
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
operator|.
name|getUserRootFolder
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|parameters
operator|.
name|setCheckpointPath
argument_list|(
name|jobDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getNumWorkers
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Only do this when #worker> 0
name|String
name|savedModelDir
init|=
name|parameters
operator|.
name|getSavedModelPath
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|savedModelDir
condition|)
block|{
name|savedModelDir
operator|=
name|jobDir
expr_stmt|;
name|parameters
operator|.
name|setSavedModelPath
argument_list|(
name|savedModelDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|storeJobInformation (String jobName, ApplicationId applicationId, String[] args)
specifier|private
name|void
name|storeJobInformation
parameter_list|(
name|String
name|jobName
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|String
index|[]
name|args
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
name|jobInfo
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|JOB_NAME
argument_list|,
name|jobName
argument_list|)
expr_stmt|;
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|APPLICATION_ID
argument_list|,
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|parameters
operator|.
name|getCheckpointPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|CHECKPOINT_PATH
argument_list|,
name|parameters
operator|.
name|getCheckpointPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getInputPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|INPUT_PATH
argument_list|,
name|parameters
operator|.
name|getInputPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parameters
operator|.
name|getSavedModelPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|SAVED_MODEL_PATH
argument_list|,
name|parameters
operator|.
name|getSavedModelPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|joinedArgs
init|=
name|String
operator|.
name|join
argument_list|(
literal|" "
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|jobInfo
operator|.
name|put
argument_list|(
name|StorageKeyConstants
operator|.
name|JOB_RUN_ARGS
argument_list|,
name|joinedArgs
argument_list|)
expr_stmt|;
name|clientContext
operator|.
name|getRuntimeFactory
argument_list|()
operator|.
name|getSubmarineStorage
argument_list|()
operator|.
name|addNewJob
argument_list|(
name|jobName
argument_list|,
name|jobInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
throws|,
name|YarnException
throws|,
name|SubmarineException
block|{
if|if
condition|(
name|CliUtils
operator|.
name|argsForHelp
argument_list|(
name|args
argument_list|)
condition|)
block|{
name|printUsages
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
name|parseCommandLineAndGetRunJobParameters
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|this
operator|.
name|jobSubmitter
operator|.
name|submitJob
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|storeJobInformation
argument_list|(
name|parameters
operator|.
name|getName
argument_list|()
argument_list|,
name|applicationId
argument_list|,
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|parameters
operator|.
name|isWaitJobFinish
argument_list|()
condition|)
block|{
name|this
operator|.
name|jobMonitor
operator|.
name|waitTrainingFinal
argument_list|(
name|parameters
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getJobSubmitter ()
specifier|public
name|JobSubmitter
name|getJobSubmitter
parameter_list|()
block|{
return|return
name|jobSubmitter
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRunJobParameters ()
specifier|public
name|RunJobParameters
name|getRunJobParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
block|}
end_class

end_unit

