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
name|BlockLocation
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
name|PathIOException
import|;
end_import

begin_comment
comment|/**  * Modifies the replication factor  */
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
DECL|class|SetReplication
class|class
name|SetReplication
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
name|SetReplication
operator|.
name|class
argument_list|,
literal|"-setrep"
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
literal|"setrep"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-R] [-w]<rep><path> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Set the replication level of a file. If<path> is a directory "
operator|+
literal|"then the command recursively changes the replication factor of "
operator|+
literal|"all files under the directory tree rooted at<path>. "
operator|+
literal|"The EC files will be ignored here.\n"
operator|+
literal|"-w: It requests that the command waits for the replication "
operator|+
literal|"to complete. This can potentially take a very long time.\n"
operator|+
literal|"-R: It is accepted for backwards compatibility. It has no effect."
decl_stmt|;
DECL|field|newRep
specifier|protected
name|short
name|newRep
init|=
literal|0
decl_stmt|;
DECL|field|waitList
specifier|protected
name|List
argument_list|<
name|PathData
argument_list|>
name|waitList
init|=
operator|new
name|LinkedList
argument_list|<
name|PathData
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|waitOpt
specifier|protected
name|boolean
name|waitOpt
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
literal|2
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"R"
argument_list|,
literal|"w"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|waitOpt
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"w"
argument_list|)
expr_stmt|;
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|newRep
operator|=
name|Short
operator|.
name|parseShort
argument_list|(
name|args
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|displayWarning
argument_list|(
literal|"Illegal replication, a positive integer expected"
argument_list|)
expr_stmt|;
throw|throw
name|nfe
throw|;
block|}
if|if
condition|(
name|newRep
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"replication must be>= 1"
argument_list|)
throw|;
block|}
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
name|super
operator|.
name|processArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitOpt
condition|)
name|waitForReplication
argument_list|()
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
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathIOException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Symlinks unsupported"
argument_list|)
throw|;
block|}
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|isFile
argument_list|()
condition|)
block|{
comment|// Do the checking if the file is erasure coded since
comment|// replication factor for an EC file is meaningless.
if|if
condition|(
operator|!
name|item
operator|.
name|stat
operator|.
name|isErasureCoded
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|item
operator|.
name|fs
operator|.
name|setReplication
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|newRep
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not set replication for: "
operator|+
name|item
argument_list|)
throw|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"Replication "
operator|+
name|newRep
operator|+
literal|" set: "
operator|+
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitOpt
condition|)
block|{
name|waitList
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Did not set replication for: "
operator|+
name|item
operator|+
literal|", because it's an erasure coded file."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Wait for all files in waitList to have replication number equal to rep.    */
DECL|method|waitForReplication ()
specifier|private
name|void
name|waitForReplication
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|PathData
name|item
range|:
name|waitList
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"Waiting for "
operator|+
name|item
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|boolean
name|printedWarning
init|=
literal|false
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|item
operator|.
name|refreshStatus
argument_list|()
expr_stmt|;
name|BlockLocation
index|[]
name|locations
init|=
name|item
operator|.
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|item
operator|.
name|stat
argument_list|,
literal|0
argument_list|,
name|item
operator|.
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|locations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|currentRep
init|=
name|locations
index|[
name|i
index|]
operator|.
name|getHosts
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|currentRep
operator|!=
name|newRep
condition|)
block|{
if|if
condition|(
operator|!
name|printedWarning
operator|&&
name|currentRep
operator|>
name|newRep
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"\nWARNING: the waiting time may be long for "
operator|+
literal|"DECREASING the number of replications."
argument_list|)
expr_stmt|;
name|printedWarning
operator|=
literal|true
expr_stmt|;
block|}
break|break;
block|}
block|}
name|done
operator|=
name|i
operator|==
name|locations
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|done
condition|)
break|break;
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
name|out
operator|.
name|println
argument_list|(
literal|" done"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

