begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|cli
package|;
end_package

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
name|Map
operator|.
name|Entry
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
name|conf
operator|.
name|OzoneConfiguration
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|ExecutionException
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
name|RunLast
import|;
end_import

begin_comment
comment|/**  * This is a generic parent class for all the ozone related cli tools.  */
end_comment

begin_class
DECL|class|GenericCli
specifier|public
class|class
name|GenericCli
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
implements|,
name|GenericParentCommand
block|{
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--verbose"
block|}
argument_list|,
name|description
operator|=
literal|"More verbose output. Show the stack trace of the errors."
argument_list|)
DECL|field|verbose
specifier|private
name|boolean
name|verbose
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-D"
block|,
literal|"--set"
block|}
argument_list|)
DECL|field|configurationOverrides
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configurationOverrides
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|cmd
specifier|private
specifier|final
name|CommandLine
name|cmd
decl_stmt|;
DECL|method|GenericCli ()
specifier|public
name|GenericCli
parameter_list|()
block|{
name|cmd
operator|=
operator|new
name|CommandLine
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|run (String[] argv)
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
try|try
block|{
name|execute
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|printError
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
operator|==
literal|null
condition|?
name|ex
else|:
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|execute (String[] argv)
specifier|public
name|void
name|execute
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
name|cmd
operator|.
name|parseWithHandler
argument_list|(
operator|new
name|RunLast
argument_list|()
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
DECL|method|printError (Throwable error)
specifier|private
name|void
name|printError
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
comment|//message could be null in case of NPE. This is unexpected so we can
comment|//print out the stack trace.
if|if
condition|(
name|verbose
operator|||
name|error
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
condition|)
block|{
name|error
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|instanceof
name|MissingSubcommandException
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
operator|(
operator|(
name|MissingSubcommandException
operator|)
name|error
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|cmd
operator|.
name|getUsageMessage
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createOzoneConfiguration ()
specifier|public
name|OzoneConfiguration
name|createOzoneConfiguration
parameter_list|()
block|{
name|OzoneConfiguration
name|ozoneConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|configurationOverrides
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|configurationOverrides
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ozoneConf
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
block|}
return|return
name|ozoneConf
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCmd ()
specifier|public
name|picocli
operator|.
name|CommandLine
name|getCmd
parameter_list|()
block|{
return|return
name|cmd
return|;
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
name|verbose
return|;
block|}
block|}
end_class

end_unit

