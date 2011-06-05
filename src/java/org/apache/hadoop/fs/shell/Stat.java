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
name|java
operator|.
name|util
operator|.
name|TimeZone
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

begin_comment
comment|/**  * Print statistics about path in specified format.  * Format sequences:  *   %b: Size of file in blocks  *   %n: Filename  *   %o: Block size  *   %r: replication  *   %y: UTC date as&quot;yyyy-MM-dd HH:mm:ss&quot;  *   %Y: Milliseconds since January 1, 1970 UTC  */
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
DECL|class|Stat
class|class
name|Stat
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
name|Stat
operator|.
name|class
argument_list|,
literal|"-stat"
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
literal|"stat"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[format]<path> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Print statistics about the file/directory at<path>\n"
operator|+
literal|"in the specified format. Format accepts filesize in blocks (%b), filename (%n),\n"
operator|+
literal|"block size (%o), replication (%r), modification date (%y, %Y)\n"
decl_stmt|;
DECL|field|timeFmt
specifier|protected
specifier|static
specifier|final
name|SimpleDateFormat
name|timeFmt
decl_stmt|;
static|static
block|{
name|timeFmt
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
expr_stmt|;
name|timeFmt
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// default format string
DECL|field|format
specifier|protected
name|String
name|format
init|=
literal|"%y"
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
name|setRecursive
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"R"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getFirst
argument_list|()
operator|.
name|contains
argument_list|(
literal|"%"
argument_list|)
condition|)
name|format
operator|=
name|args
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// make sure there's still at least one arg
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
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|fmt
init|=
name|format
operator|.
name|toCharArray
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
name|fmt
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|fmt
index|[
name|i
index|]
operator|!=
literal|'%'
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|fmt
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this silently drops a trailing %?
if|if
condition|(
name|i
operator|+
literal|1
operator|==
name|fmt
operator|.
name|length
condition|)
break|break;
switch|switch
condition|(
name|fmt
index|[
operator|++
name|i
index|]
condition|)
block|{
case|case
literal|'b'
case|:
name|buf
operator|.
name|append
argument_list|(
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'F'
case|:
name|buf
operator|.
name|append
argument_list|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"directory"
else|:
operator|(
name|stat
operator|.
name|isFile
argument_list|()
condition|?
literal|"regular file"
else|:
literal|"symlink"
operator|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|buf
operator|.
name|append
argument_list|(
name|item
operator|.
name|path
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'o'
case|:
name|buf
operator|.
name|append
argument_list|(
name|stat
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|buf
operator|.
name|append
argument_list|(
name|stat
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'y'
case|:
name|buf
operator|.
name|append
argument_list|(
name|timeFmt
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
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'Y'
case|:
name|buf
operator|.
name|append
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// this leaves %<unknown> alone, which causes the potential for
comment|// future format options to break strings; should use %% to
comment|// escape percents
name|buf
operator|.
name|append
argument_list|(
name|fmt
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|out
operator|.
name|println
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

