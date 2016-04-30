begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configured
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
name|Tool
import|;
end_import

begin_comment
comment|/**  * This program is a CLI utility base class utilizing hadoop Tool class.  */
end_comment

begin_class
DECL|class|CommandShell
specifier|public
specifier|abstract
class|class
name|CommandShell
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|out
specifier|private
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|field|err
specifier|private
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
comment|/** The subcommand instance for this shell command, if any. */
DECL|field|subcommand
specifier|private
name|SubCommand
name|subcommand
init|=
literal|null
decl_stmt|;
comment|/**    * Return usage string for the command including any summary of subcommands.    */
DECL|method|getCommandUsage ()
specifier|public
specifier|abstract
name|String
name|getCommandUsage
parameter_list|()
function_decl|;
DECL|method|setSubCommand (SubCommand cmd)
specifier|public
name|void
name|setSubCommand
parameter_list|(
name|SubCommand
name|cmd
parameter_list|)
block|{
name|subcommand
operator|=
name|cmd
expr_stmt|;
block|}
DECL|method|setOut (PrintStream p)
specifier|public
name|void
name|setOut
parameter_list|(
name|PrintStream
name|p
parameter_list|)
block|{
name|out
operator|=
name|p
expr_stmt|;
block|}
DECL|method|getOut ()
specifier|public
name|PrintStream
name|getOut
parameter_list|()
block|{
return|return
name|out
return|;
block|}
DECL|method|setErr (PrintStream p)
specifier|public
name|void
name|setErr
parameter_list|(
name|PrintStream
name|p
parameter_list|)
block|{
name|err
operator|=
name|p
expr_stmt|;
block|}
DECL|method|getErr ()
specifier|public
name|PrintStream
name|getErr
parameter_list|()
block|{
return|return
name|err
return|;
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
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
try|try
block|{
name|exitCode
operator|=
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
name|printShellUsage
argument_list|()
expr_stmt|;
return|return
name|exitCode
return|;
block|}
if|if
condition|(
name|subcommand
operator|.
name|validate
argument_list|()
condition|)
block|{
name|subcommand
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|printShellUsage
argument_list|()
expr_stmt|;
name|exitCode
operator|=
literal|1
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|printShellUsage
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
name|exitCode
return|;
block|}
comment|/**    * Parse the command line arguments and initialize subcommand instance.    * @param args    * @return 0 if the argument(s) were recognized, 1 otherwise    */
DECL|method|init (String[] args)
specifier|protected
specifier|abstract
name|int
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|printShellUsage ()
specifier|private
name|void
name|printShellUsage
parameter_list|()
block|{
if|if
condition|(
name|subcommand
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|subcommand
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
name|getCommandUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Base class for any subcommands of this shell command.    */
DECL|class|SubCommand
specifier|protected
specifier|abstract
class|class
name|SubCommand
block|{
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|execute ()
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|getUsage ()
specifier|public
specifier|abstract
name|String
name|getUsage
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

