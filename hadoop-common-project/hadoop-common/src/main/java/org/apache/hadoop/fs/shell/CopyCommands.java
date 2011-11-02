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
name|File
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
name|ChecksumFileSystem
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
name|FileUtil
import|;
end_import

begin_comment
comment|/** Various commands for copy files */
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
DECL|class|CopyCommands
class|class
name|CopyCommands
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
name|factory
operator|.
name|addClass
argument_list|(
name|Merge
operator|.
name|class
argument_list|,
literal|"-getmerge"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Cp
operator|.
name|class
argument_list|,
literal|"-cp"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|CopyFromLocal
operator|.
name|class
argument_list|,
literal|"-copyFromLocal"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|CopyToLocal
operator|.
name|class
argument_list|,
literal|"-copyToLocal"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Get
operator|.
name|class
argument_list|,
literal|"-get"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Put
operator|.
name|class
argument_list|,
literal|"-put"
argument_list|)
expr_stmt|;
block|}
comment|/** merge multiple files together */
DECL|class|Merge
specifier|public
specifier|static
class|class
name|Merge
extends|extends
name|FsCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"getmerge"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<src><localdst> [addnl]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Get all the files in the directories that\n"
operator|+
literal|"match the source file pattern and merge and sort them to only\n"
operator|+
literal|"one file on local fs.<src> is kept."
decl_stmt|;
DECL|field|dst
specifier|protected
name|PathData
name|dst
init|=
literal|null
decl_stmt|;
DECL|field|delimiter
specifier|protected
name|String
name|delimiter
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// TODO: this really should be a -nl option
if|if
condition|(
operator|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|2
operator|)
operator|&&
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|args
operator|.
name|removeLast
argument_list|()
argument_list|)
condition|)
block|{
name|delimiter
operator|=
literal|"\n"
expr_stmt|;
block|}
else|else
block|{
name|delimiter
operator|=
literal|null
expr_stmt|;
block|}
name|dst
operator|=
operator|new
name|PathData
argument_list|(
operator|new
name|File
argument_list|(
name|args
operator|.
name|removeLast
argument_list|()
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData src)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|copyMerge
argument_list|(
name|src
operator|.
name|fs
argument_list|,
name|src
operator|.
name|path
argument_list|,
name|dst
operator|.
name|fs
argument_list|,
name|dst
operator|.
name|path
argument_list|,
literal|false
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Cp
specifier|static
class|class
name|Cp
extends|extends
name|CommandWithDestination
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"cp"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<src> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files that match the file pattern<src> to a\n"
operator|+
literal|"destination.  When copying multiple files, the destination\n"
operator|+
literal|"must be a directory."
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|2
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"f"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setOverwrite
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// should have a -r option
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getRemoteDestination
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Copy local files to a remote filesystem    */
DECL|class|Get
specifier|public
specifier|static
class|class
name|Get
extends|extends
name|CommandWithDestination
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"get"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-ignoreCrc] [-crc]<src> ...<localdst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files that match the file pattern<src>\n"
operator|+
literal|"to the local name.<src> is kept.  When copying multiple,\n"
operator|+
literal|"files, the destination must be a directory."
decl_stmt|;
comment|/**      * The prefix for the tmp file used in copyToLocal.      * It must be at least three characters long, required by      * {@link java.io.File#createTempFile(String, String, File)}.      */
DECL|field|copyCrc
specifier|private
name|boolean
name|copyCrc
decl_stmt|;
DECL|field|verifyChecksum
specifier|private
name|boolean
name|verifyChecksum
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"crc"
argument_list|,
literal|"ignoreCrc"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|copyCrc
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"crc"
argument_list|)
expr_stmt|;
name|verifyChecksum
operator|=
operator|!
name|cf
operator|.
name|getOpt
argument_list|(
literal|"ignoreCrc"
argument_list|)
expr_stmt|;
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getLocalDestination
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyFileToTarget (PathData src, PathData target)
specifier|protected
name|void
name|copyFileToTarget
parameter_list|(
name|PathData
name|src
parameter_list|,
name|PathData
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|src
operator|.
name|fs
operator|.
name|setVerifyChecksum
argument_list|(
name|verifyChecksum
argument_list|)
expr_stmt|;
if|if
condition|(
name|copyCrc
operator|&&
operator|!
operator|(
name|src
operator|.
name|fs
operator|instanceof
name|ChecksumFileSystem
operator|)
condition|)
block|{
name|displayWarning
argument_list|(
name|src
operator|.
name|fs
operator|+
literal|": Does not support checksums"
argument_list|)
expr_stmt|;
name|copyCrc
operator|=
literal|false
expr_stmt|;
block|}
name|super
operator|.
name|copyFileToTarget
argument_list|(
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|copyCrc
condition|)
block|{
comment|// should we delete real file if crc copy fails?
name|super
operator|.
name|copyFileToTarget
argument_list|(
name|src
operator|.
name|getChecksumFile
argument_list|()
argument_list|,
name|target
operator|.
name|getChecksumFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    *  Copy local files to a remote filesystem    */
DECL|class|Put
specifier|public
specifier|static
class|class
name|Put
extends|extends
name|CommandWithDestination
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"put"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<localsrc> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files from the local file system\n"
operator|+
literal|"into fs."
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"f"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setOverwrite
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|getRemoteDestination
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// should have a -r option
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// commands operating on local paths have no need for glob expansion
annotation|@
name|Override
DECL|method|expandArgument (String arg)
specifier|protected
name|List
argument_list|<
name|PathData
argument_list|>
name|expandArgument
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|PathData
argument_list|>
name|items
init|=
operator|new
name|LinkedList
argument_list|<
name|PathData
argument_list|>
argument_list|()
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
operator|new
name|PathData
argument_list|(
operator|new
name|File
argument_list|(
name|arg
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|items
return|;
block|}
annotation|@
name|Override
DECL|method|processArguments (LinkedList<PathData> args)
specifier|protected
name|void
name|processArguments
parameter_list|(
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE: this logic should be better, mimics previous implementation
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|copyStreamToTarget
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|getTargetPath
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CopyFromLocal
specifier|public
specifier|static
class|class
name|CopyFromLocal
extends|extends
name|Put
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"copyFromLocal"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
name|Put
operator|.
name|USAGE
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Identical to the -put command."
decl_stmt|;
block|}
DECL|class|CopyToLocal
specifier|public
specifier|static
class|class
name|CopyToLocal
extends|extends
name|Get
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"copyToLocal"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
name|Get
operator|.
name|USAGE
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Identical to the -get command."
decl_stmt|;
block|}
block|}
end_class

end_unit

