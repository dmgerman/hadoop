begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
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
name|RemoteIterator
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
name|EncryptionZone
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
name|tools
operator|.
name|TableListing
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

begin_comment
comment|/**  * This class implements crypto command-line operations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CryptoAdmin
specifier|public
class|class
name|CryptoAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|method|CryptoAdmin ()
specifier|public
name|CryptoAdmin
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CryptoAdmin (Configuration conf)
specifier|public
name|CryptoAdmin
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
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|AdminHelper
operator|.
name|printUsage
argument_list|(
literal|false
argument_list|,
literal|"crypto"
argument_list|,
name|COMMANDS
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|AdminHelper
operator|.
name|Command
name|command
init|=
name|AdminHelper
operator|.
name|determineCommand
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|COMMANDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand command '"
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Command names must start with dashes."
argument_list|)
expr_stmt|;
block|}
name|AdminHelper
operator|.
name|printUsage
argument_list|(
literal|false
argument_list|,
literal|"crypto"
argument_list|,
name|COMMANDS
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|args
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|argsList
operator|.
name|add
argument_list|(
name|args
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|command
operator|.
name|run
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|argsList
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|main (String[] argsArray)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argsArray
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|CryptoAdmin
name|cryptoAdmin
init|=
operator|new
name|CryptoAdmin
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|cryptoAdmin
operator|.
name|run
argument_list|(
name|argsArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * NN exceptions contain the stack trace as part of the exception message.    * When it's a known error, pretty-print the error and squish the stack trace.    */
DECL|method|prettifyException (Exception e)
specifier|private
specifier|static
name|String
name|prettifyException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
index|[
literal|0
index|]
return|;
block|}
DECL|class|CreateZoneCommand
specifier|private
specifier|static
class|class
name|CreateZoneCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-createZone"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|" -keyName<keyName> -path<path>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
specifier|final
name|TableListing
name|listing
init|=
name|AdminHelper
operator|.
name|getOptionDescriptionListing
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<path>"
argument_list|,
literal|"The path of the encryption zone to create. "
operator|+
literal|"It must be an empty directory."
argument_list|)
expr_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<keyName>"
argument_list|,
literal|"Name of the key to use for the "
operator|+
literal|"encryption zone."
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Create a new encryption zone.\n\n"
operator|+
name|listing
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a path with -path."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|String
name|keyName
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-keyName"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyName
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a key name with -keyName."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|dfs
operator|.
name|createEncryptionZone
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Added encryption zone "
operator|+
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|class|ListZonesCommand
specifier|private
specifier|static
class|class
name|ListZonesCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-listZones"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|"]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"List all encryption zones. Requires superuser permissions.\n\n"
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|TableListing
name|listing
init|=
operator|new
name|TableListing
operator|.
name|Builder
argument_list|()
operator|.
name|addField
argument_list|(
literal|""
argument_list|)
operator|.
name|addField
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|)
operator|.
name|wrapWidth
argument_list|(
name|AdminHelper
operator|.
name|MAX_LINE_WIDTH
argument_list|)
operator|.
name|hideHeaders
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RemoteIterator
argument_list|<
name|EncryptionZone
argument_list|>
name|it
init|=
name|dfs
operator|.
name|listEncryptionZones
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|EncryptionZone
name|ez
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
name|ez
operator|.
name|getPath
argument_list|()
argument_list|,
name|ez
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|listing
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|field|COMMANDS
specifier|private
specifier|static
specifier|final
name|AdminHelper
operator|.
name|Command
index|[]
name|COMMANDS
init|=
block|{
operator|new
name|CreateZoneCommand
argument_list|()
block|,
operator|new
name|ListZonesCommand
argument_list|()
block|}
decl_stmt|;
block|}
end_class

end_unit

