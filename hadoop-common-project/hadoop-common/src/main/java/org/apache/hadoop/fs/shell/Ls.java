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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|FileStatus
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

begin_comment
comment|/**  * Get a listing of all files in that match the file patterns.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Ls
class|class
name|Ls
extends|extends
name|FsCommand
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
name|Ls
operator|.
name|class
argument_list|,
literal|"-ls"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Lsr
operator|.
name|class
argument_list|,
literal|"-lsr"
argument_list|)
expr_stmt|;
block|}
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"ls"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-d] [-h] [-R] [<path> ...]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"List the contents that match the specified file pattern. If\n"
operator|+
literal|"path is not specified, the contents of /user/<currentUser>\n"
operator|+
literal|"will be listed. Directory entries are of the form \n"
operator|+
literal|"\tdirName (full path)<dir> \n"
operator|+
literal|"and file entries are of the form \n"
operator|+
literal|"\tfileName(full path)<r n> size \n"
operator|+
literal|"where n is the number of replicas specified for the file \n"
operator|+
literal|"and size is the size of the file, in bytes.\n"
operator|+
literal|"  -d  Directories are listed as plain files.\n"
operator|+
literal|"  -h  Formats the sizes of files in a human-readable fashion\n"
operator|+
literal|"      rather than a number of bytes.\n"
operator|+
literal|"  -R  Recursively list the contents of directories."
decl_stmt|;
DECL|field|dateFormat
specifier|protected
specifier|static
specifier|final
name|SimpleDateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
decl_stmt|;
DECL|field|maxRepl
DECL|field|maxLen
DECL|field|maxOwner
DECL|field|maxGroup
specifier|protected
name|int
name|maxRepl
init|=
literal|3
decl_stmt|,
name|maxLen
init|=
literal|10
decl_stmt|,
name|maxOwner
init|=
literal|0
decl_stmt|,
name|maxGroup
init|=
literal|0
decl_stmt|;
DECL|field|lineFormat
specifier|protected
name|String
name|lineFormat
decl_stmt|;
DECL|field|dirRecurse
specifier|protected
name|boolean
name|dirRecurse
decl_stmt|;
DECL|field|humanReadable
specifier|protected
name|boolean
name|humanReadable
init|=
literal|false
decl_stmt|;
DECL|method|formatSize (long size)
specifier|protected
name|String
name|formatSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|humanReadable
condition|?
name|StringUtils
operator|.
name|humanReadableInt
argument_list|(
name|size
argument_list|)
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
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
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"d"
argument_list|,
literal|"h"
argument_list|,
literal|"R"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|dirRecurse
operator|=
operator|!
name|cf
operator|.
name|getOpt
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|setRecursive
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"R"
argument_list|)
operator|&&
name|dirRecurse
argument_list|)
expr_stmt|;
name|humanReadable
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"h"
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
name|args
operator|.
name|add
argument_list|(
name|Path
operator|.
name|CUR_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPathArgument (PathData item)
specifier|protected
name|void
name|processPathArgument
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
comment|// implicitly recurse once for cmdline directories
if|if
condition|(
name|dirRecurse
operator|&&
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|recursePath
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|processPathArgument
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processPaths (PathData parent, PathData ... items)
specifier|protected
name|void
name|processPaths
parameter_list|(
name|PathData
name|parent
parameter_list|,
name|PathData
modifier|...
name|items
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isRecursive
argument_list|()
operator|&&
name|items
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Found "
operator|+
name|items
operator|.
name|length
operator|+
literal|" items"
argument_list|)
expr_stmt|;
block|}
name|adjustColumnWidths
argument_list|(
name|items
argument_list|)
expr_stmt|;
name|super
operator|.
name|processPaths
argument_list|(
name|parent
argument_list|,
name|items
argument_list|)
expr_stmt|;
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
name|FileStatus
name|stat
init|=
name|item
operator|.
name|stat
decl_stmt|;
name|String
name|line
init|=
name|String
operator|.
name|format
argument_list|(
name|lineFormat
argument_list|,
operator|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"d"
else|:
literal|"-"
operator|)
argument_list|,
name|stat
operator|.
name|getPermission
argument_list|()
argument_list|,
operator|(
name|stat
operator|.
name|isFile
argument_list|()
condition|?
name|stat
operator|.
name|getReplication
argument_list|()
else|:
literal|"-"
operator|)
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|,
name|formatSize
argument_list|(
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|,
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|item
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compute column widths and rebuild the format string    * @param items to find the max field width for each column    */
DECL|method|adjustColumnWidths (PathData items[])
specifier|private
name|void
name|adjustColumnWidths
parameter_list|(
name|PathData
name|items
index|[]
parameter_list|)
block|{
for|for
control|(
name|PathData
name|item
range|:
name|items
control|)
block|{
name|FileStatus
name|stat
init|=
name|item
operator|.
name|stat
decl_stmt|;
name|maxRepl
operator|=
name|maxLength
argument_list|(
name|maxRepl
argument_list|,
name|stat
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|maxLen
operator|=
name|maxLength
argument_list|(
name|maxLen
argument_list|,
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|maxOwner
operator|=
name|maxLength
argument_list|(
name|maxOwner
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|maxGroup
operator|=
name|maxLength
argument_list|(
name|maxGroup
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|fmt
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%s%s "
argument_list|)
expr_stmt|;
comment|// permission string
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxRepl
operator|+
literal|"s "
argument_list|)
expr_stmt|;
comment|// Do not use '%-0s' as a formatting conversion, since it will throw a
comment|// a MissingFormatWidthException if it is used in String.format().
comment|// http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Formatter.html#intFlags
name|fmt
operator|.
name|append
argument_list|(
operator|(
name|maxOwner
operator|>
literal|0
operator|)
condition|?
literal|"%-"
operator|+
name|maxOwner
operator|+
literal|"s "
else|:
literal|"%s"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
operator|(
name|maxGroup
operator|>
literal|0
operator|)
condition|?
literal|"%-"
operator|+
name|maxGroup
operator|+
literal|"s "
else|:
literal|"%s"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxLen
operator|+
literal|"s "
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%s %s"
argument_list|)
expr_stmt|;
comment|// mod time& path
name|lineFormat
operator|=
name|fmt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|maxLength (int n, Object value)
specifier|private
name|int
name|maxLength
parameter_list|(
name|int
name|n
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|n
argument_list|,
operator|(
name|value
operator|!=
literal|null
operator|)
condition|?
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|.
name|length
argument_list|()
else|:
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get a recursive listing of all files in that match the file patterns.    * Same as "-ls -R"    */
DECL|class|Lsr
specifier|public
specifier|static
class|class
name|Lsr
extends|extends
name|Ls
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"lsr"
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
name|args
operator|.
name|addFirst
argument_list|(
literal|"-R"
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
DECL|method|getReplacementCommand ()
specifier|public
name|String
name|getReplacementCommand
parameter_list|()
block|{
return|return
literal|"ls -R"
return|;
block|}
block|}
block|}
end_class

end_unit

