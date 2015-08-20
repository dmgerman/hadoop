begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
package|;
end_package

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
name|LinkedList
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|Configuration
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
name|fs
operator|.
name|FsShellPermissions
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|shell
operator|.
name|find
operator|.
name|Find
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_DEFAULT
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SHELL_MISSING_DEFAULT_FS_WARNING_DEFAULT
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SHELL_MISSING_DEFAULT_FS_WARNING_KEY
import|;
end_import

begin_comment
comment|/**  * Base class for all "hadoop fs" commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|// this class may not look useful now, but it's a placeholder for future
comment|// functionality to act as a registry for fs commands.  currently it's being
comment|// used to implement unnecessary abstract methods in the base class
DECL|class|FsCommand
specifier|abstract
specifier|public
class|class
name|FsCommand
extends|extends
name|Command
block|{
comment|/**    * Register the command classes used by the fs subcommand    * @param factory where to register the class    */
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|registerCommands
argument_list|(
name|AclCommands
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|CopyCommands
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Count
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Delete
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Display
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Find
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|FsShellPermissions
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|FsUsage
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Ls
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Mkdir
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|MoveCommands
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|SetReplication
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Stat
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Tail
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Test
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Touch
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|Truncate
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|SnapshotCommands
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|XAttrCommands
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|FsCommand ()
specifier|protected
name|FsCommand
parameter_list|()
block|{}
DECL|method|FsCommand (Configuration conf)
specifier|protected
name|FsCommand
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// historical abstract method in Command
annotation|@
name|Override
DECL|method|getCommandName ()
specifier|public
name|String
name|getCommandName
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
comment|// abstract method that normally is invoked by runall() which is
comment|// overridden below
annotation|@
name|Override
DECL|method|run (Path path)
specifier|protected
name|void
name|run
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not supposed to get here"
argument_list|)
throw|;
block|}
comment|/** @deprecated use {@link Command#run(String...argv)} */
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|runAll ()
specifier|public
name|int
name|runAll
parameter_list|()
block|{
return|return
name|run
argument_list|(
name|args
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|processRawArguments (LinkedList<String> args)
specifier|protected
name|void
name|processRawArguments
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|expendedArgs
init|=
name|expandArguments
argument_list|(
name|args
argument_list|)
decl_stmt|;
comment|// If "fs.defaultFs" is not set appropriately, it warns the user that the
comment|// command is not running against HDFS.
specifier|final
name|boolean
name|displayWarnings
init|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|HADOOP_SHELL_MISSING_DEFAULT_FS_WARNING_KEY
argument_list|,
name|HADOOP_SHELL_MISSING_DEFAULT_FS_WARNING_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|displayWarnings
condition|)
block|{
specifier|final
name|String
name|defaultFs
init|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|FS_DEFAULT_NAME_KEY
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|missingDefaultFs
init|=
name|defaultFs
operator|==
literal|null
operator|||
name|defaultFs
operator|.
name|equals
argument_list|(
name|FS_DEFAULT_NAME_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|missingDefaultFs
condition|)
block|{
name|err
operator|.
name|printf
argument_list|(
literal|"Warning: fs.defaultFs is not set when running \"%s\" command.%n"
argument_list|,
name|getCommandName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|processArguments
argument_list|(
name|expendedArgs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

