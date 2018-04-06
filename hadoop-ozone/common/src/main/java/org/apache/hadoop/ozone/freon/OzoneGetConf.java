begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
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
name|security
operator|.
name|PrivilegedExceptionAction
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
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|HadoopIllegalArgumentException
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|HddsUtils
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
name|KsmUtils
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
name|SecurityUtil
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
name|StringUtils
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

begin_comment
comment|/**  * CLI utility to print out ozone related configuration.  */
end_comment

begin_class
DECL|class|OzoneGetConf
specifier|public
class|class
name|OzoneGetConf
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|DESCRIPTION
specifier|private
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"ozone getconf is utility for "
operator|+
literal|"getting configuration information from the config file.\n"
decl_stmt|;
DECL|enum|Command
enum|enum
name|Command
block|{
DECL|enumConstant|INCLUDE_FILE
name|INCLUDE_FILE
argument_list|(
literal|"-includeFile"
argument_list|,
literal|"gets the include file path that defines the datanodes "
operator|+
literal|"that can join the cluster."
argument_list|)
block|,
DECL|enumConstant|EXCLUDE_FILE
name|EXCLUDE_FILE
argument_list|(
literal|"-excludeFile"
argument_list|,
literal|"gets the exclude file path that defines the datanodes "
operator|+
literal|"that need to decommissioned."
argument_list|)
block|,
DECL|enumConstant|KEYSPACEMANAGER
name|KEYSPACEMANAGER
argument_list|(
literal|"-keyspacemanagers"
argument_list|,
literal|"gets list of ozone key space manager nodes in the cluster"
argument_list|)
block|,
DECL|enumConstant|STORAGECONTAINERMANAGER
name|STORAGECONTAINERMANAGER
argument_list|(
literal|"-storagecontainermanagers"
argument_list|,
literal|"gets list of ozone storage container manager nodes in the cluster"
argument_list|)
block|,
DECL|enumConstant|CONFKEY
name|CONFKEY
argument_list|(
literal|"-confKey [key]"
argument_list|,
literal|"gets a specific key from the configuration"
argument_list|)
block|;
DECL|field|HANDLERS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneGetConf
operator|.
name|CommandHandler
argument_list|>
name|HANDLERS
decl_stmt|;
static|static
block|{
name|HANDLERS
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|OzoneGetConf
operator|.
name|CommandHandler
argument_list|>
argument_list|()
expr_stmt|;
name|HANDLERS
operator|.
name|put
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|KEYSPACEMANAGER
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|KeySpaceManagersCommandHandler
argument_list|()
argument_list|)
expr_stmt|;
name|HANDLERS
operator|.
name|put
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|STORAGECONTAINERMANAGER
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|StorageContainerManagersCommandHandler
argument_list|()
argument_list|)
expr_stmt|;
name|HANDLERS
operator|.
name|put
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|CONFKEY
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|PrintConfKeyCommandHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|cmd
specifier|private
specifier|final
name|String
name|cmd
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|method|Command (String cmd, String description)
name|Command
parameter_list|(
name|String
name|cmd
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|cmd
operator|=
name|cmd
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|cmd
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
index|[
literal|0
index|]
return|;
block|}
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|cmd
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getHandler (String cmd)
specifier|public
specifier|static
name|OzoneGetConf
operator|.
name|CommandHandler
name|getHandler
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
return|return
name|HANDLERS
operator|.
name|get
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|cmd
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|USAGE
specifier|static
specifier|final
name|String
name|USAGE
decl_stmt|;
static|static
block|{
name|HdfsConfiguration
operator|.
name|init
argument_list|()
expr_stmt|;
comment|/* Initialize USAGE based on Command values */
name|StringBuilder
name|usage
init|=
operator|new
name|StringBuilder
argument_list|(
name|DESCRIPTION
argument_list|)
decl_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"\nozone getconf \n"
argument_list|)
expr_stmt|;
for|for
control|(
name|OzoneGetConf
operator|.
name|Command
name|cmd
range|:
name|OzoneGetConf
operator|.
name|Command
operator|.
name|values
argument_list|()
control|)
block|{
name|usage
operator|.
name|append
argument_list|(
literal|"\t["
operator|+
name|cmd
operator|.
name|getUsage
argument_list|()
operator|+
literal|"]\t\t\t"
operator|+
name|cmd
operator|.
name|getDescription
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|USAGE
operator|=
name|usage
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Handler to return value for key corresponding to the    * {@link OzoneGetConf.Command}.    */
DECL|class|CommandHandler
specifier|static
class|class
name|CommandHandler
block|{
DECL|field|key
name|String
name|key
decl_stmt|;
comment|// Configuration key to lookup
DECL|method|CommandHandler ()
name|CommandHandler
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CommandHandler (String key)
name|CommandHandler
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|doWork (OzoneGetConf tool, String[] args)
specifier|final
name|int
name|doWork
parameter_list|(
name|OzoneGetConf
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|checkArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|doWorkInternal
argument_list|(
name|tool
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tool
operator|.
name|printError
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|checkArgs (String args[])
specifier|protected
name|void
name|checkArgs
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Did not expect argument: "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
block|}
comment|/** Method to be overridden by sub classes for specific behavior */
DECL|method|doWorkInternal (OzoneGetConf tool, String[] args)
name|int
name|doWorkInternal
parameter_list|(
name|OzoneGetConf
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|value
init|=
name|tool
operator|.
name|getConf
argument_list|()
operator|.
name|getTrimmed
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|tool
operator|.
name|printOut
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|tool
operator|.
name|printError
argument_list|(
literal|"Configuration "
operator|+
name|key
operator|+
literal|" is missing."
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|class|PrintConfKeyCommandHandler
specifier|static
class|class
name|PrintConfKeyCommandHandler
extends|extends
name|OzoneGetConf
operator|.
name|CommandHandler
block|{
annotation|@
name|Override
DECL|method|checkArgs (String[] args)
specifier|protected
name|void
name|checkArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"usage: "
operator|+
name|OzoneGetConf
operator|.
name|Command
operator|.
name|CONFKEY
operator|.
name|getUsage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doWorkInternal (OzoneGetConf tool, String[] args)
name|int
name|doWorkInternal
parameter_list|(
name|OzoneGetConf
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|key
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
return|return
name|super
operator|.
name|doWorkInternal
argument_list|(
name|tool
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
DECL|field|out
specifier|private
specifier|final
name|PrintStream
name|out
decl_stmt|;
comment|// Stream for printing command output
DECL|field|err
specifier|private
specifier|final
name|PrintStream
name|err
decl_stmt|;
comment|// Stream for printing error
DECL|method|OzoneGetConf (Configuration conf)
specifier|protected
name|OzoneGetConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|conf
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
DECL|method|OzoneGetConf (Configuration conf, PrintStream out, PrintStream err)
specifier|protected
name|OzoneGetConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|PrintStream
name|err
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
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
block|}
DECL|method|printError (String message)
name|void
name|printError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|err
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|printOut (String message)
name|void
name|printOut
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|printError
argument_list|(
name|USAGE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Main method that runs the tool for given arguments.    * @param args arguments    * @return return status of the command    */
DECL|method|doWork (String[] args)
specifier|private
name|int
name|doWork
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|OzoneGetConf
operator|.
name|CommandHandler
name|handler
init|=
name|OzoneGetConf
operator|.
name|Command
operator|.
name|getHandler
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
return|return
name|handler
operator|.
name|doWork
argument_list|(
name|this
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|args
argument_list|,
literal|1
argument_list|,
name|args
operator|.
name|length
argument_list|)
argument_list|)
return|;
block|}
block|}
name|printUsage
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|run (final String[] args)
specifier|public
name|int
name|run
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|SecurityUtil
operator|.
name|doAsCurrentUser
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|doWork
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Handler for {@link Command#STORAGECONTAINERMANAGER}.    */
DECL|class|StorageContainerManagersCommandHandler
specifier|static
class|class
name|StorageContainerManagersCommandHandler
extends|extends
name|CommandHandler
block|{
annotation|@
name|Override
DECL|method|doWorkInternal (OzoneGetConf tool, String[] args)
specifier|public
name|int
name|doWorkInternal
parameter_list|(
name|OzoneGetConf
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addresses
init|=
name|HddsUtils
operator|.
name|getSCMAddresses
argument_list|(
name|tool
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|addresses
control|)
block|{
name|tool
operator|.
name|printOut
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Handler for {@link Command#KEYSPACEMANAGER}.    */
DECL|class|KeySpaceManagersCommandHandler
specifier|static
class|class
name|KeySpaceManagersCommandHandler
extends|extends
name|CommandHandler
block|{
annotation|@
name|Override
DECL|method|doWorkInternal (OzoneGetConf tool, String[] args)
specifier|public
name|int
name|doWorkInternal
parameter_list|(
name|OzoneGetConf
name|tool
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|tool
operator|.
name|printOut
argument_list|(
name|KsmUtils
operator|.
name|getKsmAddress
argument_list|(
name|tool
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|DFSUtil
operator|.
name|parseHelpArgument
argument_list|(
name|args
argument_list|,
name|USAGE
argument_list|,
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|OzoneGetConf
argument_list|(
name|conf
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

