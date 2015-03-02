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
name|io
operator|.
name|PrintStream
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
name|ContentSummary
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
name|FsStatus
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/** Base class for commands related to viewing filesystem usage, such as  * du and df  */
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
DECL|class|FsUsage
class|class
name|FsUsage
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
name|Df
operator|.
name|class
argument_list|,
literal|"-df"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Du
operator|.
name|class
argument_list|,
literal|"-du"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|Dus
operator|.
name|class
argument_list|,
literal|"-dus"
argument_list|)
expr_stmt|;
block|}
DECL|field|humanReadable
specifier|protected
name|boolean
name|humanReadable
init|=
literal|false
decl_stmt|;
DECL|field|usagesTable
specifier|protected
name|TableBuilder
name|usagesTable
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
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|size
argument_list|,
literal|""
argument_list|,
literal|1
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
comment|/** Show the size of a partition in the filesystem */
DECL|class|Df
specifier|public
specifier|static
class|class
name|Df
extends|extends
name|FsUsage
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"df"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-h] [<path> ...]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Shows the capacity, free and used space of the filesystem. "
operator|+
literal|"If the filesystem has multiple partitions, and no path to a "
operator|+
literal|"particular partition is specified, then the status of the root "
operator|+
literal|"partitions will be shown.\n"
operator|+
literal|"-h: Formats the sizes of files in a human-readable fashion "
operator|+
literal|"rather than a number of bytes."
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
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"h"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
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
name|SEPARATOR
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
name|usagesTable
operator|=
operator|new
name|TableBuilder
argument_list|(
literal|"Filesystem"
argument_list|,
literal|"Size"
argument_list|,
literal|"Used"
argument_list|,
literal|"Available"
argument_list|,
literal|"Use%"
argument_list|)
expr_stmt|;
name|usagesTable
operator|.
name|setRightAlign
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|usagesTable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|usagesTable
operator|.
name|printToStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|FsStatus
name|fsStats
init|=
name|item
operator|.
name|fs
operator|.
name|getStatus
argument_list|(
name|item
operator|.
name|path
argument_list|)
decl_stmt|;
name|long
name|size
init|=
name|fsStats
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
name|long
name|used
init|=
name|fsStats
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|long
name|free
init|=
name|fsStats
operator|.
name|getRemaining
argument_list|()
decl_stmt|;
name|usagesTable
operator|.
name|addRow
argument_list|(
name|item
operator|.
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|formatSize
argument_list|(
name|size
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|used
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|free
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|formatPercent
argument_list|(
operator|(
name|double
operator|)
name|used
operator|/
operator|(
name|double
operator|)
name|size
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** show disk usage */
DECL|class|Du
specifier|public
specifier|static
class|class
name|Du
extends|extends
name|FsUsage
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"du"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-s] [-h]<path> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Show the amount of space, in bytes, used by the files that "
operator|+
literal|"match the specified file pattern. The following flags are optional:\n"
operator|+
literal|"-s: Rather than showing the size of each individual file that"
operator|+
literal|" matches the pattern, shows the total (summary) size.\n"
operator|+
literal|"-h: Formats the sizes of files in a human-readable fashion"
operator|+
literal|" rather than a number of bytes.\n\n"
operator|+
literal|"Note that, even without the -s option, this only shows size summaries "
operator|+
literal|"one level deep into a directory.\n\n"
operator|+
literal|"The output is in the form \n"
operator|+
literal|"\tsize\tdisk space consumed\tname(full path)\n"
decl_stmt|;
DECL|field|summary
specifier|protected
name|boolean
name|summary
init|=
literal|false
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
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"h"
argument_list|,
literal|"s"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
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
name|summary
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"s"
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
name|usagesTable
operator|=
operator|new
name|TableBuilder
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|usagesTable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|usagesTable
operator|.
name|printToStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
comment|// go one level deep on dirs from cmdline unless in summary mode
if|if
condition|(
operator|!
name|summary
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
name|ContentSummary
name|contentSummary
init|=
name|item
operator|.
name|fs
operator|.
name|getContentSummary
argument_list|(
name|item
operator|.
name|path
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|contentSummary
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|long
name|spaceConsumed
init|=
name|contentSummary
operator|.
name|getSpaceConsumed
argument_list|()
decl_stmt|;
name|usagesTable
operator|.
name|addRow
argument_list|(
name|formatSize
argument_list|(
name|length
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|spaceConsumed
argument_list|)
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** show disk usage summary */
DECL|class|Dus
specifier|public
specifier|static
class|class
name|Dus
extends|extends
name|Du
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"dus"
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
literal|"-s"
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
literal|"du -s"
return|;
block|}
block|}
comment|/**    * Creates a table of aligned values based on the maximum width of each    * column as a string    */
DECL|class|TableBuilder
specifier|private
specifier|static
class|class
name|TableBuilder
block|{
DECL|field|hasHeader
specifier|protected
name|boolean
name|hasHeader
init|=
literal|false
decl_stmt|;
DECL|field|rows
specifier|protected
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|rows
decl_stmt|;
DECL|field|widths
specifier|protected
name|int
index|[]
name|widths
decl_stmt|;
DECL|field|rightAlign
specifier|protected
name|boolean
index|[]
name|rightAlign
decl_stmt|;
comment|/**      * Create a table w/o headers      * @param columns number of columns      */
DECL|method|TableBuilder (int columns)
specifier|public
name|TableBuilder
parameter_list|(
name|int
name|columns
parameter_list|)
block|{
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|widths
operator|=
operator|new
name|int
index|[
name|columns
index|]
expr_stmt|;
name|rightAlign
operator|=
operator|new
name|boolean
index|[
name|columns
index|]
expr_stmt|;
block|}
comment|/**      * Create a table with headers      * @param headers list of headers      */
DECL|method|TableBuilder (Object .... headers)
specifier|public
name|TableBuilder
parameter_list|(
name|Object
modifier|...
name|headers
parameter_list|)
block|{
name|this
argument_list|(
name|headers
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|addRow
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|hasHeader
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Change the default left-align of columns      * @param indexes of columns to right align      */
DECL|method|setRightAlign (int ... indexes)
specifier|public
name|void
name|setRightAlign
parameter_list|(
name|int
modifier|...
name|indexes
parameter_list|)
block|{
for|for
control|(
name|int
name|i
range|:
name|indexes
control|)
name|rightAlign
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Add a row of objects to the table      * @param objects the values      */
DECL|method|addRow (Object .... objects)
specifier|public
name|void
name|addRow
parameter_list|(
name|Object
modifier|...
name|objects
parameter_list|)
block|{
name|String
index|[]
name|row
init|=
operator|new
name|String
index|[
name|widths
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|widths
operator|.
name|length
condition|;
name|col
operator|++
control|)
block|{
name|row
index|[
name|col
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|objects
index|[
name|col
index|]
argument_list|)
expr_stmt|;
name|widths
index|[
name|col
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|widths
index|[
name|col
index|]
argument_list|,
name|row
index|[
name|col
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
comment|/**      * Render the table to a stream       * @param out PrintStream for output      */
DECL|method|printToStream (PrintStream out)
specifier|public
name|void
name|printToStream
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
return|return;
name|StringBuilder
name|fmt
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|widths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fmt
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
name|fmt
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
if|if
condition|(
name|rightAlign
index|[
name|i
index|]
condition|)
block|{
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|widths
index|[
name|i
index|]
operator|+
literal|"s"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|!=
name|widths
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|fmt
operator|.
name|append
argument_list|(
literal|"%-"
operator|+
name|widths
index|[
name|i
index|]
operator|+
literal|"s"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// prevent trailing spaces if the final column is left-aligned
name|fmt
operator|.
name|append
argument_list|(
literal|"%s"
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Object
index|[]
name|row
range|:
name|rows
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|fmt
operator|.
name|toString
argument_list|()
argument_list|,
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Number of rows excluding header       * @return rows      */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|rows
operator|.
name|size
argument_list|()
operator|-
operator|(
name|hasHeader
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
comment|/**      * Does table have any rows       * @return boolean      */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

