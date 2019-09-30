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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|cli
operator|.
name|HddsVersionProvider
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
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|ParentCommand
import|;
end_import

begin_comment
comment|/**  * This is the handler that process delete container command.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"delete"
argument_list|,
name|description
operator|=
literal|"Delete container"
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|)
DECL|class|DeleteSubcommand
specifier|public
class|class
name|DeleteSubcommand
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|Parameters
argument_list|(
name|description
operator|=
literal|"Id of the container to close"
argument_list|)
DECL|field|containerId
specifier|private
name|long
name|containerId
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-f"
block|,
literal|"--force"
block|}
argument_list|,
name|description
operator|=
literal|"forcibly delete the container"
argument_list|)
DECL|field|force
specifier|private
name|boolean
name|force
decl_stmt|;
annotation|@
name|ParentCommand
DECL|field|parent
specifier|private
name|ContainerCommands
name|parent
decl_stmt|;
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ScmClient
name|scmClient
init|=
name|parent
operator|.
name|getParent
argument_list|()
operator|.
name|createScmClient
argument_list|()
init|)
block|{
name|parent
operator|.
name|getParent
argument_list|()
operator|.
name|checkContainerExists
argument_list|(
name|scmClient
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|scmClient
operator|.
name|deleteContainer
argument_list|(
name|containerId
argument_list|,
name|force
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

