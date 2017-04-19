begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|cli
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
name|cli
operator|.
name|BasicParser
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
name|Option
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
name|hadoop
operator|.
name|ipc
operator|.
name|Client
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|container
operator|.
name|ContainerCommandHandler
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
name|ozone
operator|.
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|client
operator|.
name|ContainerOperationClient
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|security
operator|.
name|UserGroupInformation
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
name|util
operator|.
name|ToolRunner
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|ResultCode
operator|.
name|EXECUTION_ERROR
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|ResultCode
operator|.
name|SUCCESS
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|ResultCode
operator|.
name|UNRECOGNIZED_CMD
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
name|ozone
operator|.
name|scm
operator|.
name|cli
operator|.
name|container
operator|.
name|ContainerCommandHandler
operator|.
name|CONTAINER_CMD
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_KEY
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_GB
import|;
end_import

begin_comment
comment|/**  * This class is the CLI of SCM.  */
end_comment

begin_class
DECL|class|SCMCLI
specifier|public
class|class
name|SCMCLI
extends|extends
name|OzoneBaseCLI
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
name|SCMCLI
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HELP_OP
specifier|public
specifier|static
specifier|final
name|String
name|HELP_OP
init|=
literal|"help"
decl_stmt|;
DECL|field|CMD_WIDTH
specifier|public
specifier|static
specifier|final
name|int
name|CMD_WIDTH
init|=
literal|80
decl_stmt|;
DECL|field|scmClient
specifier|private
specifier|final
name|ScmClient
name|scmClient
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|PrintStream
name|out
decl_stmt|;
DECL|field|err
specifier|private
specifier|final
name|PrintStream
name|err
decl_stmt|;
DECL|field|options
specifier|private
specifier|final
name|Options
name|options
decl_stmt|;
DECL|method|SCMCLI (ScmClient scmClient)
specifier|public
name|SCMCLI
parameter_list|(
name|ScmClient
name|scmClient
parameter_list|)
block|{
name|this
argument_list|(
name|scmClient
argument_list|,
name|System
operator|.
name|out
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
DECL|method|SCMCLI (ScmClient scmClient, PrintStream out, PrintStream err)
specifier|public
name|SCMCLI
parameter_list|(
name|ScmClient
name|scmClient
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|PrintStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|scmClient
operator|=
name|scmClient
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|err
operator|=
name|err
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|getOptions
argument_list|()
expr_stmt|;
block|}
comment|/**    * Main for the scm shell Command handling.    *    * @param argv - System Args Strings[]    * @throws Exception    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ScmClient
name|scmClient
init|=
name|getScmClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SCMCLI
name|shell
init|=
operator|new
name|SCMCLI
argument_list|(
name|scmClient
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|getScmClient (OzoneConfiguration ozoneConf)
specifier|private
specifier|static
name|ScmClient
name|getScmClient
parameter_list|(
name|OzoneConfiguration
name|ozoneConf
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|scmAddress
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
argument_list|,
name|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|scmPort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CLIENT_PORT_KEY
argument_list|,
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|containerSizeGB
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|ContainerOperationClient
operator|.
name|setContainerSizeB
argument_list|(
name|containerSizeGB
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|scmAddress
argument_list|,
name|scmPort
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|ozoneConf
argument_list|,
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|client
init|=
operator|new
name|StorageContainerLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|ozoneConf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|ozoneConf
argument_list|)
argument_list|,
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ScmClient
name|storageClient
init|=
operator|new
name|ContainerOperationClient
argument_list|(
name|client
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|storageClient
return|;
block|}
comment|/**    * Adds ALL the options that hdfs scm command supports. Given the hierarchy    * of commands, the options are added in a cascading manner, e.g.:    * {@link SCMCLI} asks {@link ContainerCommandHandler} to add it's options,    * which then asks it's sub command, such as    * {@link org.apache.hadoop.ozone.scm.cli.container.CreateContainerHandler}    * to add it's own options.    *    * We need to do this because {@link BasicParser} need to take all the options    * when paring args.    * @return ALL the options supported by this CLI.    */
annotation|@
name|Override
DECL|method|getOptions ()
specifier|protected
name|Options
name|getOptions
parameter_list|()
block|{
name|Options
name|newOptions
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
comment|// add the options
name|addTopLevelOptions
argument_list|(
name|newOptions
argument_list|)
expr_stmt|;
name|ContainerCommandHandler
operator|.
name|addOptions
argument_list|(
name|newOptions
argument_list|)
expr_stmt|;
comment|// TODO : add pool, node and pipeline commands.
name|addHelpOption
argument_list|(
name|newOptions
argument_list|)
expr_stmt|;
return|return
name|newOptions
return|;
block|}
DECL|method|addTopLevelOptions (Options options)
specifier|private
specifier|static
name|void
name|addTopLevelOptions
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|Option
name|containerOps
init|=
operator|new
name|Option
argument_list|(
name|CONTAINER_CMD
argument_list|,
literal|false
argument_list|,
literal|"Container related options"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|containerOps
argument_list|)
expr_stmt|;
comment|// TODO : add pool, node and pipeline commands.
block|}
DECL|method|addHelpOption (Options options)
specifier|private
specifier|static
name|void
name|addHelpOption
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|Option
name|helpOp
init|=
operator|new
name|Option
argument_list|(
name|HELP_OP
argument_list|,
literal|false
argument_list|,
literal|"display help message"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|helpOp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|displayHelp ()
specifier|protected
name|void
name|displayHelp
parameter_list|()
block|{
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|Options
name|topLevelOptions
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|addTopLevelOptions
argument_list|(
name|topLevelOptions
argument_list|)
expr_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
name|CMD_WIDTH
argument_list|,
literal|"hdfs scm<commands> [<options>]"
argument_list|,
literal|"where<commands> can be one of the following"
argument_list|,
name|topLevelOptions
argument_list|,
literal|""
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
name|Exception
block|{
name|CommandLine
name|cmd
init|=
name|parseArgs
argument_list|(
name|args
argument_list|,
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Unrecognized options:"
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|displayHelp
argument_list|()
expr_stmt|;
return|return
name|UNRECOGNIZED_CMD
return|;
block|}
return|return
name|dispatch
argument_list|(
name|cmd
argument_list|,
name|options
argument_list|)
return|;
block|}
comment|/**    * This function parses all command line arguments    * and returns the appropriate values.    *    * @param argv - Argv from main    *    * @return CommandLine    */
annotation|@
name|Override
DECL|method|parseArgs (String[] argv, Options opts)
specifier|protected
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|argv
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|ParseException
block|{
try|try
block|{
name|BasicParser
name|parser
init|=
operator|new
name|BasicParser
argument_list|()
decl_stmt|;
return|return
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|argv
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|dispatch (CommandLine cmd, Options opts)
specifier|protected
name|int
name|dispatch
parameter_list|(
name|CommandLine
name|cmd
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
throws|,
name|URISyntaxException
block|{
name|OzoneCommandHandler
name|handler
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|CONTAINER_CMD
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|ContainerCommandHandler
argument_list|(
name|scmClient
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|HELP_OP
argument_list|)
condition|)
block|{
name|displayHelp
argument_list|()
expr_stmt|;
return|return
name|SUCCESS
return|;
block|}
else|else
block|{
name|displayHelp
argument_list|()
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"Unrecognized command: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|cmd
operator|.
name|getArgs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|UNRECOGNIZED_CMD
return|;
block|}
block|}
else|else
block|{
name|handler
operator|.
name|execute
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|SUCCESS
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Error executing command:"
operator|+
name|ioe
argument_list|)
expr_stmt|;
return|return
name|EXECUTION_ERROR
return|;
block|}
block|}
block|}
end_class

end_unit

