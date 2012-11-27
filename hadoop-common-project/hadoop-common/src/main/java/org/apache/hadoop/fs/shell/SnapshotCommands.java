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
name|PathIsNotDirectoryException
import|;
end_import

begin_comment
comment|/**  * Snapshot related operations  */
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
DECL|class|SnapshotCommands
class|class
name|SnapshotCommands
extends|extends
name|FsCommand
block|{
DECL|field|CREATE_SNAPSHOT
specifier|private
specifier|final
specifier|static
name|String
name|CREATE_SNAPSHOT
init|=
literal|"createSnapshot"
decl_stmt|;
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
name|CreateSnapshot
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|CREATE_SNAPSHOT
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Create a snapshot    */
DECL|class|CreateSnapshot
specifier|public
specifier|static
class|class
name|CreateSnapshot
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
name|CREATE_SNAPSHOT
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"<snapshotName><snapshotRoot>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Create a snapshot on a directory"
decl_stmt|;
DECL|field|snapshotName
specifier|private
specifier|static
name|String
name|snapshotName
decl_stmt|;
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
name|PathIsNotDirectoryException
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
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
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"args number not 2:"
operator|+
name|args
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|snapshotName
operator|=
name|args
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
comment|// TODO: name length check
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
assert|assert
operator|(
name|items
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
assert|;
name|PathData
name|sroot
init|=
name|items
operator|.
name|getFirst
argument_list|()
decl_stmt|;
name|String
name|snapshotRoot
init|=
name|sroot
operator|.
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sroot
operator|.
name|fs
operator|.
name|createSnapshot
argument_list|(
name|snapshotName
argument_list|,
name|snapshotRoot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

