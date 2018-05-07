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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|ContainerInfo
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
import|import static
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
operator|.
name|CMD_WIDTH
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
name|hdds
operator|.
name|scm
operator|.
name|cli
operator|.
name|SCMCLI
operator|.
name|HELP_OP
import|;
end_import

begin_comment
comment|/**  * This is the handler that process delete container command.  */
end_comment

begin_class
DECL|class|DeleteContainerHandler
specifier|public
class|class
name|DeleteContainerHandler
extends|extends
name|OzoneCommandHandler
block|{
DECL|field|CONTAINER_DELETE
specifier|protected
specifier|static
specifier|final
name|String
name|CONTAINER_DELETE
init|=
literal|"delete"
decl_stmt|;
DECL|field|OPT_FORCE
specifier|protected
specifier|static
specifier|final
name|String
name|OPT_FORCE
init|=
literal|"f"
decl_stmt|;
DECL|field|OPT_CONTAINER_ID
specifier|protected
specifier|static
specifier|final
name|String
name|OPT_CONTAINER_ID
init|=
literal|"c"
decl_stmt|;
DECL|method|DeleteContainerHandler (ScmClient scmClient)
specifier|public
name|DeleteContainerHandler
parameter_list|(
name|ScmClient
name|scmClient
parameter_list|)
block|{
name|super
argument_list|(
name|scmClient
argument_list|)
expr_stmt|;
block|}
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|CONTAINER_DELETE
argument_list|)
argument_list|,
literal|"Expecting command delete"
argument_list|)
expr_stmt|;
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
name|HELP_OP
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting container name"
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
name|ContainerInfo
name|container
init|=
name|getScmClient
argument_list|()
operator|.
name|getContainer
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
literal|"Cannot delete an non-exist container "
operator|+
name|containerID
argument_list|)
throw|;
block|}
name|logOut
argument_list|(
literal|"Deleting container : %s."
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
name|getScmClient
argument_list|()
operator|.
name|deleteContainer
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|,
name|cmd
operator|.
name|hasOption
argument_list|(
name|OPT_FORCE
argument_list|)
argument_list|)
expr_stmt|;
name|logOut
argument_list|(
literal|"Container %s deleted."
argument_list|,
name|containerID
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
name|CMD_WIDTH
argument_list|,
literal|"hdfs scm -container -delete<option>"
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
name|forceOpt
init|=
operator|new
name|Option
argument_list|(
name|OPT_FORCE
argument_list|,
literal|false
argument_list|,
literal|"forcibly delete a container"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|forceOpt
argument_list|)
expr_stmt|;
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
literal|"Specify container id"
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
block|}
end_class

end_unit

