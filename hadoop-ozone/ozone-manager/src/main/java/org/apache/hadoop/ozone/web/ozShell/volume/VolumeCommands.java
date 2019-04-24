begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.volume
package|package
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
name|ozShell
operator|.
name|volume
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
name|GenericParentCommand
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
name|cli
operator|.
name|MissingSubcommandException
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
name|conf
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
name|web
operator|.
name|ozShell
operator|.
name|Shell
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
name|ParentCommand
import|;
end_import

begin_comment
comment|/**  * Subcommand to group volume related operations.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"volume"
argument_list|,
name|aliases
operator|=
literal|"vol"
argument_list|,
name|description
operator|=
literal|"Volume specific operations"
argument_list|,
name|subcommands
operator|=
block|{
name|InfoVolumeHandler
operator|.
name|class
block|,
name|ListVolumeHandler
operator|.
name|class
block|,
name|CreateVolumeHandler
operator|.
name|class
block|,
name|UpdateVolumeHandler
operator|.
name|class
block|,
name|DeleteVolumeHandler
operator|.
name|class
block|}
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
DECL|class|VolumeCommands
specifier|public
class|class
name|VolumeCommands
implements|implements
name|GenericParentCommand
implements|,
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|ParentCommand
DECL|field|shell
specifier|private
name|Shell
name|shell
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
throw|throw
operator|new
name|MissingSubcommandException
argument_list|(
name|this
operator|.
name|shell
operator|.
name|getCmd
argument_list|()
operator|.
name|getSubcommands
argument_list|()
operator|.
name|get
argument_list|(
literal|"volume"
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|isVerbose ()
specifier|public
name|boolean
name|isVerbose
parameter_list|()
block|{
return|return
name|shell
operator|.
name|isVerbose
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createOzoneConfiguration ()
specifier|public
name|OzoneConfiguration
name|createOzoneConfiguration
parameter_list|()
block|{
return|return
name|shell
operator|.
name|createOzoneConfiguration
argument_list|()
return|;
block|}
block|}
end_class

end_unit

