begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|utils
operator|.
name|JsonUtils
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
name|List
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
comment|/**  * This is the handler that process container list command.  */
end_comment

begin_class
DECL|class|ListContainerHandler
specifier|public
class|class
name|ListContainerHandler
extends|extends
name|OzoneCommandHandler
block|{
DECL|field|CONTAINER_LIST
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_LIST
init|=
literal|"list"
decl_stmt|;
DECL|field|OPT_START_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|OPT_START_CONTAINER
init|=
literal|"start"
decl_stmt|;
DECL|field|OPT_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|OPT_COUNT
init|=
literal|"count"
decl_stmt|;
comment|/**    * Constructs a handler object.    *    * @param scmClient scm client    */
DECL|method|ListContainerHandler (ScmClient scmClient)
specifier|public
name|ListContainerHandler
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
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|CONTAINER_LIST
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting container list"
argument_list|)
throw|;
block|}
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
return|return;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|OPT_COUNT
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
literal|"Expecting container count"
argument_list|)
throw|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|String
name|startID
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|OPT_START_CONTAINER
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|OPT_COUNT
argument_list|)
condition|)
block|{
name|count
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|OPT_COUNT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
name|displayHelp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"-count should not be negative"
argument_list|)
throw|;
block|}
block|}
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containerList
init|=
name|getScmClient
argument_list|()
operator|.
name|listContainer
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|startID
argument_list|)
argument_list|,
name|count
argument_list|)
decl_stmt|;
comment|// Output data list
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containerList
control|)
block|{
name|outputContainerInfo
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|outputContainerInfo (ContainerInfo containerInfo)
specifier|private
name|void
name|outputContainerInfo
parameter_list|(
name|ContainerInfo
name|containerInfo
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Print container report info.
name|logOut
argument_list|(
literal|"%s"
argument_list|,
name|JsonUtils
operator|.
name|toJsonStringWithDefaultPrettyPrinter
argument_list|(
name|containerInfo
operator|.
name|toJsonString
argument_list|()
argument_list|)
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
literal|"hdfs scm -container -list<option>"
argument_list|,
literal|"where<option> can be the following"
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
name|startContainerOpt
init|=
operator|new
name|Option
argument_list|(
name|OPT_START_CONTAINER
argument_list|,
literal|true
argument_list|,
literal|"Specify start container id"
argument_list|)
decl_stmt|;
name|Option
name|countOpt
init|=
operator|new
name|Option
argument_list|(
name|OPT_COUNT
argument_list|,
literal|true
argument_list|,
literal|"Specify count number, required"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|countOpt
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|startContainerOpt
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

