begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|erasurecode
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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|fs
operator|.
name|FileSystem
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
name|Command
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
name|CommandFactory
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
name|PathData
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
name|DistributedFileSystem
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
name|protocol
operator|.
name|ErasureCodingZoneInfo
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
name|server
operator|.
name|namenode
operator|.
name|UnsupportedActionException
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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

begin_comment
comment|/**  * Erasure Coding CLI commands  */
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
DECL|class|ECCommand
specifier|public
specifier|abstract
class|class
name|ECCommand
extends|extends
name|Command
block|{
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
comment|// Register all commands of Erasure CLI, with a '-' at the beginning in name
comment|// of the command.
name|factory
operator|.
name|addClass
argument_list|(
name|CreateECZoneCommand
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|CreateECZoneCommand
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|GetECZoneInfoCommand
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|GetECZoneInfoCommand
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|ListECSchemas
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|ListECSchemas
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
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
literal|"Not suppose to get here"
argument_list|)
throw|;
block|}
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
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|item
operator|.
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedActionException
argument_list|(
literal|"Erasure commands are only supported for the HDFS paths"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create EC encoding zone command. Zones are created to use specific EC    * encoding schema, other than default while encoding the files under some    * specific directory.    */
DECL|class|CreateECZoneCommand
specifier|static
class|class
name|CreateECZoneCommand
extends|extends
name|ECCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"createZone"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-s<schemaName>]<path>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Create a zone to encode files using a specified schema\n"
operator|+
literal|"Options :\n"
operator|+
literal|"  -s<schemaName> : EC schema name to encode files. "
operator|+
literal|"If not passed default schema will be used\n"
operator|+
literal|"<path>  : Path to an empty directory. Under this directory "
operator|+
literal|"files will be encoded using specified schema"
decl_stmt|;
DECL|field|schemaName
specifier|private
name|String
name|schemaName
decl_stmt|;
DECL|field|schema
specifier|private
name|ECSchema
name|schema
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
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
name|schemaName
operator|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-s"
argument_list|,
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"<path> is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many arguments"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|processPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|item
operator|.
name|fs
decl_stmt|;
try|try
block|{
if|if
condition|(
name|schemaName
operator|!=
literal|null
condition|)
block|{
name|ECSchema
index|[]
name|ecSchemas
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getECSchemas
argument_list|()
decl_stmt|;
for|for
control|(
name|ECSchema
name|ecSchema
range|:
name|ecSchemas
control|)
block|{
if|if
condition|(
name|schemaName
operator|.
name|equals
argument_list|(
name|ecSchema
operator|.
name|getSchemaName
argument_list|()
argument_list|)
condition|)
block|{
name|schema
operator|=
name|ecSchema
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Schema '"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|schemaName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"' does not match any of the supported schemas."
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" Please select any one of "
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|schemaNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ECSchema
name|ecSchema
range|:
name|ecSchemas
control|)
block|{
name|schemaNames
operator|.
name|add
argument_list|(
name|ecSchema
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|schemaNames
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|dfs
operator|.
name|createErasureCodingZone
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"EC Zone created successfully at "
operator|+
name|item
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create EC zone for the path "
operator|+
name|item
operator|.
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Get the information about the zone    */
DECL|class|GetECZoneInfoCommand
specifier|static
class|class
name|GetECZoneInfoCommand
extends|extends
name|ECCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"getZoneInfo"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<path>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Get information about the EC zone at specified path\n"
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
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
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"<path> is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many arguments"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|processPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|item
operator|.
name|fs
decl_stmt|;
try|try
block|{
name|ErasureCodingZoneInfo
name|ecZoneInfo
init|=
name|dfs
operator|.
name|getErasureCodingZoneInfo
argument_list|(
name|item
operator|.
name|path
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|ecZoneInfo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create EC zone for the path "
operator|+
name|item
operator|.
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * List all supported EC Schemas    */
DECL|class|ListECSchemas
specifier|static
class|class
name|ListECSchemas
extends|extends
name|ECCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"listSchemas"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|""
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Get the list of ECSchemas supported\n"
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
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
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many parameters"
argument_list|)
throw|;
block|}
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UnsupportedActionException
argument_list|(
literal|"Erasure commands are only supported for the HDFS"
argument_list|)
throw|;
block|}
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
decl_stmt|;
name|ECSchema
index|[]
name|ecSchemas
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getECSchemas
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|ecSchemas
operator|.
name|length
condition|)
block|{
name|ECSchema
name|ecSchema
init|=
name|ecSchemas
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ecSchema
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|ecSchemas
operator|.
name|length
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|println
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

