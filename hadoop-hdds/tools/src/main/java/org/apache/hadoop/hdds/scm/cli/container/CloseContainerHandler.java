begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.cli.container
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|cli
operator|.
name|container
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
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|cli
operator|.
name|OzoneCommandHandler
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
name|hdds
operator|.
name|scm
operator|.
name|cli
operator|.
name|SCMCLI
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
name|hdds
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
import|;
end_import

begin_comment
comment|/**  * The handler of close container command.  */
end_comment

begin_class
DECL|class|CloseContainerHandler
specifier|public
class|class
name|CloseContainerHandler
extends|extends
name|OzoneCommandHandler
block|{
DECL|field|CONTAINER_CLOSE
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_CLOSE
init|=
literal|"close"
decl_stmt|;
DECL|field|OPT_CONTAINER_ID
specifier|public
specifier|static
specifier|final
name|String
name|OPT_CONTAINER_ID
init|=
literal|"c"
decl_stmt|;
annotation|@
name|Override
DECL|method|execute (CommandLine cmd)
specifier|public
name|void
name|execute
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|CONTAINER_CLOSE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting container close"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|OPT_CONTAINER_ID
argument_list|)
condition|)
block|{
name|displayHelp
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|SCMCLI
operator|.
name|HELP_OP
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting container id"
argument_list|)
throw|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|String
name|containerID
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|OPT_CONTAINER_ID
argument_list|)
decl_stmt|;
name|ContainerWithPipeline
name|container
init|=
name|getScmClient
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|containerID
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot close an non-exist container "
operator|+
name|containerID
argument_list|)
throw|;
block|}
name|logOut
argument_list|(
literal|"Closing container : %s."
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|getScmClient
argument_list|()
operator|.
name|closeContainer
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|logOut
argument_list|(
literal|"Container closed."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|displayHelp ()
specifier|public
name|void
name|displayHelp
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|addOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
name|SCMCLI
operator|.
name|CMD_WIDTH
argument_list|,
literal|"hdfs scm -container -close<option>"
argument_list|,
literal|"where<option> is"
argument_list|,
name|options
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|addOptions (Options options)
specifier|public
specifier|static
name|void
name|addOptions
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|Option
name|containerNameOpt
init|=
operator|new
name|Option
argument_list|(
name|OPT_CONTAINER_ID
argument_list|,
literal|true
argument_list|,
literal|"Specify container ID"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|containerNameOpt
argument_list|)
expr_stmt|;
block|}
DECL|method|CloseContainerHandler (ScmClient client)
name|CloseContainerHandler
parameter_list|(
name|ScmClient
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

