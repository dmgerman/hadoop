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
name|shell
operator|.
name|PathExceptions
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
DECL|class|Tail
class|class
name|Tail
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
name|Tail
operator|.
name|class
argument_list|,
literal|"-tail"
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
literal|"tail"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-f]<file>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Show the last 1KB of the file.\n"
operator|+
literal|"\t\tThe -f option shows appended data as the file grows.\n"
decl_stmt|;
DECL|field|startingOffset
specifier|private
name|long
name|startingOffset
init|=
operator|-
literal|1024
decl_stmt|;
DECL|field|follow
specifier|private
name|boolean
name|follow
init|=
literal|false
decl_stmt|;
DECL|field|followDelay
specifier|private
name|long
name|followDelay
init|=
literal|5000
decl_stmt|;
comment|// milliseconds
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
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
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
name|follow
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: HADOOP-7234 will add glob support; for now, be backwards compat
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
name|arg
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
name|item
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
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|long
name|offset
init|=
name|dumpFromOffset
argument_list|(
name|item
argument_list|,
name|startingOffset
argument_list|)
decl_stmt|;
while|while
condition|(
name|follow
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|followDelay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
break|break;
block|}
name|offset
operator|=
name|dumpFromOffset
argument_list|(
name|item
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dumpFromOffset (PathData item, long offset)
specifier|private
name|long
name|dumpFromOffset
parameter_list|(
name|PathData
name|item
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|fileSize
init|=
name|item
operator|.
name|refreshStatus
argument_list|()
operator|.
name|getLen
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|>
name|fileSize
condition|)
return|return
name|fileSize
return|;
comment|// treat a negative offset as relative to end of the file, floor of 0
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
name|offset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|fileSize
operator|+
name|offset
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|FSDataInputStream
name|in
init|=
name|item
operator|.
name|fs
operator|.
name|open
argument_list|(
name|item
operator|.
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
comment|// use conf so the system configured io block size is used
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|System
operator|.
name|out
argument_list|,
name|getConf
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|offset
operator|=
name|in
operator|.
name|getPos
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|offset
return|;
block|}
block|}
end_class

end_unit

