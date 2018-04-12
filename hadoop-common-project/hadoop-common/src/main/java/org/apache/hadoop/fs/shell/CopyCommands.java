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
name|FileInputStream
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
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
name|ArrayBlockingQueue
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
name|TimeUnit
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|PathIsDirectoryException
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
name|IOUtils
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
name|factory
operator|.
name|addClass
argument_list|(
name|AppendToFile
operator|.
name|class
argument_list|,
literal|"-appendToFile"
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
literal|"[-nl] [-skip-empty-file] "
operator|+
literal|"<src><localdst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Get all the files in the directories that "
operator|+
literal|"match the source file pattern and merge and sort them to only "
operator|+
literal|"one file on local fs.<src> is kept.\n"
operator|+
literal|"-nl: Add a newline character at the end of each file.\n"
operator|+
literal|"-skip-empty-file: Do not add new line character for empty file."
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
DECL|field|skipEmptyFileDelimiter
specifier|private
name|boolean
name|skipEmptyFileDelimiter
decl_stmt|;
DECL|field|srcs
specifier|protected
name|List
argument_list|<
name|PathData
argument_list|>
name|srcs
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
try|try
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
literal|"nl"
argument_list|,
literal|"skip-empty-file"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|delimiter
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"nl"
argument_list|)
condition|?
literal|"\n"
else|:
literal|null
expr_stmt|;
name|skipEmptyFileDelimiter
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"skip-empty-file"
argument_list|)
expr_stmt|;
name|dst
operator|=
operator|new
name|PathData
argument_list|(
operator|new
name|URI
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
if|if
condition|(
name|dst
operator|.
name|exists
operator|&&
name|dst
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathIsDirectoryException
argument_list|(
name|dst
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|srcs
operator|=
operator|new
name|LinkedList
argument_list|<
name|PathData
argument_list|>
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unexpected URISyntaxException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|processArguments (LinkedList<PathData> items)
specifier|protected
name|void
name|processArguments
parameter_list|(
name|LinkedList
argument_list|<
name|PathData
argument_list|>
name|items
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|processArguments
argument_list|(
name|items
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
comment|// check for error collecting paths
return|return;
block|}
name|FSDataOutputStream
name|out
init|=
name|dst
operator|.
name|fs
operator|.
name|create
argument_list|(
name|dst
operator|.
name|path
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|PathData
name|src
range|:
name|srcs
control|)
block|{
if|if
condition|(
name|src
operator|.
name|stat
operator|.
name|getLen
argument_list|()
operator|!=
literal|0
condition|)
block|{
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|src
operator|.
name|fs
operator|.
name|open
argument_list|(
name|src
operator|.
name|path
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|getConf
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeDelimiter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|skipEmptyFileDelimiter
condition|)
block|{
name|writeDelimiter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeDelimiter (FSDataOutputStream out)
specifier|private
name|void
name|writeDelimiter
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|delimiter
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|delimiter
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processNonexistentPath (PathData item)
specifier|protected
name|void
name|processNonexistentPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|exitCode
operator|=
literal|1
expr_stmt|;
comment|// flag that a path is bad
name|super
operator|.
name|processNonexistentPath
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
comment|// this command is handled a bit differently than others.  the paths
comment|// are batched up instead of actually being processed.  this avoids
comment|// unnecessarily streaming into the merge file and then encountering
comment|// a path error that should abort the merge
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
comment|// for directories, recurse one level to get its files, else skip it
if|if
condition|(
name|src
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|getDepth
argument_list|()
operator|==
literal|0
condition|)
block|{
name|recursePath
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
comment|// skip subdirs
block|}
else|else
block|{
name|srcs
operator|.
name|add
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isSorted ()
specifier|protected
name|boolean
name|isSorted
parameter_list|()
block|{
comment|//Sort the children for merge
return|return
literal|true
return|;
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
literal|"[-f] [-p | -p[topax]] [-d]<src> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files that match the file pattern<src> to a "
operator|+
literal|"destination.  When copying multiple files, the destination "
operator|+
literal|"must be a directory. Passing -p preserves status "
operator|+
literal|"[topax] (timestamps, ownership, permission, ACLs, XAttr). "
operator|+
literal|"If -p is specified with no<arg>, then preserves "
operator|+
literal|"timestamps, ownership, permission. If -pa is specified, "
operator|+
literal|"then preserves permission also because ACL is a super-set of "
operator|+
literal|"permission. Passing -f overwrites the destination if it "
operator|+
literal|"already exists. raw namespace extended attributes are preserved "
operator|+
literal|"if (1) they are supported (HDFS only) and, (2) all of the source and "
operator|+
literal|"target pathnames are in the /.reserved/raw hierarchy. raw namespace "
operator|+
literal|"xattr preservation is determined solely by the presence (or absence) "
operator|+
literal|"of the /.reserved/raw prefix and not by the -p option. Passing -d "
operator|+
literal|"will skip creation of temporary file(<dst>._COPYING_).\n"
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
name|popPreserveOption
argument_list|(
name|args
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"d"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setDirectWrite
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"d"
argument_list|)
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
DECL|method|popPreserveOption (List<String> args)
specifier|private
name|void
name|popPreserveOption
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|args
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|cur
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|cur
operator|.
name|equals
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
comment|// stop parsing arguments when you see --
break|break;
block|}
elseif|else
if|if
condition|(
name|cur
operator|.
name|startsWith
argument_list|(
literal|"-p"
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|cur
operator|.
name|length
argument_list|()
operator|==
literal|2
condition|)
block|{
name|setPreserve
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|attributes
init|=
name|cur
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|attributes
operator|.
name|length
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|preserve
argument_list|(
name|FileAttribute
operator|.
name|getAttribute
argument_list|(
name|attributes
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
block|}
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
literal|"[-f] [-p] [-ignoreCrc] [-crc]<src> ...<localdst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files that match the file pattern<src> "
operator|+
literal|"to the local name.<src> is kept.  When copying multiple "
operator|+
literal|"files, the destination must be a directory. Passing "
operator|+
literal|"-f overwrites the destination if it already exists and "
operator|+
literal|"-p preserves access and modification times, "
operator|+
literal|"ownership and the mode.\n"
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
argument_list|,
literal|"p"
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
name|setWriteChecksum
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"crc"
argument_list|)
argument_list|)
expr_stmt|;
name|setVerifyChecksum
argument_list|(
operator|!
name|cf
operator|.
name|getOpt
argument_list|(
literal|"ignoreCrc"
argument_list|)
argument_list|)
expr_stmt|;
name|setPreserve
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"p"
argument_list|)
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
literal|"[-f] [-p] [-l] [-d]<localsrc> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files from the local file system "
operator|+
literal|"into fs. Copying fails if the file already "
operator|+
literal|"exists, unless the -f flag is given.\n"
operator|+
literal|"Flags:\n"
operator|+
literal|"  -p : Preserves access and modification times, ownership and the mode.\n"
operator|+
literal|"  -f : Overwrites the destination if it already exists.\n"
operator|+
literal|"  -l : Allow DataNode to lazily persist the file to disk. Forces\n"
operator|+
literal|"       replication factor of 1. This flag will result in reduced\n"
operator|+
literal|"       durability. Use with care.\n"
operator|+
literal|"  -d : Skip creation of temporary file(<dst>._COPYING_).\n"
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
argument_list|,
literal|"p"
argument_list|,
literal|"l"
argument_list|,
literal|"d"
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
name|setPreserve
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|setLazyPersist
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"l"
argument_list|)
argument_list|)
expr_stmt|;
name|setDirectWrite
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"d"
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
try|try
block|{
name|items
operator|.
name|add
argument_list|(
operator|new
name|PathData
argument_list|(
operator|new
name|URI
argument_list|(
name|arg
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
comment|// Unlike URI, PathData knows how to parse Windows drive-letter paths.
name|items
operator|.
name|add
argument_list|(
operator|new
name|PathData
argument_list|(
name|arg
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unexpected URISyntaxException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|field|executor
specifier|private
name|ThreadPoolExecutor
name|executor
init|=
literal|null
decl_stmt|;
DECL|field|numThreads
specifier|private
name|int
name|numThreads
init|=
literal|1
decl_stmt|;
DECL|field|MAX_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_THREADS
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|2
decl_stmt|;
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
literal|"[-f] [-p] [-l] [-d] [-t<thread count>]<localsrc> ...<dst>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Copy files from the local file system "
operator|+
literal|"into fs. Copying fails if the file already "
operator|+
literal|"exists, unless the -f flag is given.\n"
operator|+
literal|"Flags:\n"
operator|+
literal|"  -p : Preserves access and modification times, ownership and the"
operator|+
literal|" mode.\n"
operator|+
literal|"  -f : Overwrites the destination if it already exists.\n"
operator|+
literal|"  -t<thread count> : Number of threads to be used, default is 1.\n"
operator|+
literal|"  -l : Allow DataNode to lazily persist the file to disk. Forces"
operator|+
literal|" replication factor of 1. This flag will result in reduced"
operator|+
literal|" durability. Use with care.\n"
operator|+
literal|"  -d : Skip creation of temporary file(<dst>._COPYING_).\n"
decl_stmt|;
DECL|method|setNumberThreads (String numberThreadsString)
specifier|private
name|void
name|setNumberThreads
parameter_list|(
name|String
name|numberThreadsString
parameter_list|)
block|{
if|if
condition|(
name|numberThreadsString
operator|==
literal|null
condition|)
block|{
name|numThreads
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|int
name|parsedValue
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numberThreadsString
argument_list|)
decl_stmt|;
if|if
condition|(
name|parsedValue
operator|<=
literal|1
condition|)
block|{
name|numThreads
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parsedValue
operator|>
name|MAX_THREADS
condition|)
block|{
name|numThreads
operator|=
name|MAX_THREADS
expr_stmt|;
block|}
else|else
block|{
name|numThreads
operator|=
name|parsedValue
expr_stmt|;
block|}
block|}
block|}
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
argument_list|,
literal|"p"
argument_list|,
literal|"l"
argument_list|,
literal|"d"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|addOptionWithValue
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setNumberThreads
argument_list|(
name|cf
operator|.
name|getOptValue
argument_list|(
literal|"t"
argument_list|)
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
name|setPreserve
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|setLazyPersist
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"l"
argument_list|)
argument_list|)
expr_stmt|;
name|setDirectWrite
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"d"
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
DECL|method|copyFile (PathData src, PathData target)
specifier|private
name|void
name|copyFile
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
if|if
condition|(
name|isPathRecursable
argument_list|(
name|src
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PathIsDirectoryException
argument_list|(
name|src
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
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
comment|// if number of thread is 1, mimic put and avoid threading overhead
if|if
condition|(
name|numThreads
operator|==
literal|1
condition|)
block|{
name|copyFile
argument_list|(
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
return|return;
block|}
name|Runnable
name|task
init|=
parameter_list|()
lambda|->
block|{
try|try
block|{
name|copyFile
argument_list|(
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
name|task
argument_list|)
expr_stmt|;
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
name|executor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|numThreads
argument_list|,
name|numThreads
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|ArrayBlockingQueue
argument_list|<>
argument_list|(
literal|1024
argument_list|)
argument_list|,
operator|new
name|ThreadPoolExecutor
operator|.
name|CallerRunsPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// issue the command and then wait for it to finish
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|displayError
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumThreads ()
specifier|public
name|int
name|getNumThreads
parameter_list|()
block|{
return|return
name|numThreads
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getExecutor ()
specifier|public
name|ThreadPoolExecutor
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
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
comment|/**    *  Append the contents of one or more local files to a remote    *  file.    */
DECL|class|AppendToFile
specifier|public
specifier|static
class|class
name|AppendToFile
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
literal|"appendToFile"
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
literal|"Appends the contents of all the given local files to the "
operator|+
literal|"given dst file. The dst file will be created if it does "
operator|+
literal|"not exist. If<localSrc> is -, then the input is read "
operator|+
literal|"from stdin."
decl_stmt|;
DECL|field|DEFAULT_IO_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_IO_LENGTH
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|readStdin
name|boolean
name|readStdin
init|=
literal|false
decl_stmt|;
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
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|readStdin
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|items
operator|.
name|add
argument_list|(
operator|new
name|PathData
argument_list|(
operator|new
name|URI
argument_list|(
name|arg
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
comment|// Unlike URI, PathData knows how to parse Windows drive-letter paths.
name|items
operator|.
name|add
argument_list|(
operator|new
name|PathData
argument_list|(
name|arg
argument_list|,
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected URISyntaxException: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|items
return|;
block|}
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
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"missing destination argument"
argument_list|)
throw|;
block|}
name|getRemoteDestination
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|super
operator|.
name|processOptions
argument_list|(
name|args
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|dst
operator|.
name|exists
condition|)
block|{
name|dst
operator|.
name|fs
operator|.
name|create
argument_list|(
name|dst
operator|.
name|path
argument_list|,
literal|false
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileInputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|fos
init|=
name|dst
operator|.
name|fs
operator|.
name|append
argument_list|(
name|dst
operator|.
name|path
argument_list|)
init|)
block|{
if|if
condition|(
name|readStdin
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|fos
argument_list|,
name|DEFAULT_IO_LENGTH
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"stdin (-) must be the sole input argument when present"
argument_list|)
throw|;
block|}
block|}
comment|// Read in each input file and write to the target.
for|for
control|(
name|PathData
name|source
range|:
name|args
control|)
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|source
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|fos
argument_list|,
name|DEFAULT_IO_LENGTH
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

