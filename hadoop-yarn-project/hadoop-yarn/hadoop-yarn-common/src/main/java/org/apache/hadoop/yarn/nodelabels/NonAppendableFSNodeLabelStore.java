begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeLabel
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|nodelabels
operator|.
name|store
operator|.
name|FSStoreOpHandler
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
name|yarn
operator|.
name|nodelabels
operator|.
name|store
operator|.
name|StoreOp
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
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Store implementation for Non Appendable File Store.  */
end_comment

begin_class
DECL|class|NonAppendableFSNodeLabelStore
specifier|public
class|class
name|NonAppendableFSNodeLabelStore
extends|extends
name|FileSystemNodeLabelsStore
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NonAppendableFSNodeLabelStore
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|recover ()
specifier|public
name|void
name|recover
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Path
name|newMirrorPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
operator|+
literal|".new"
argument_list|)
decl_stmt|;
name|Path
name|oldMirrorPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
argument_list|)
decl_stmt|;
name|loadFromMirror
argument_list|(
name|newMirrorPath
argument_list|,
name|oldMirrorPath
argument_list|)
expr_stmt|;
comment|// if new mirror exists, remove old mirror and rename new mirror
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|newMirrorPath
argument_list|)
condition|)
block|{
comment|// remove old mirror
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|oldMirrorPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// do nothing
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception while removing old mirror"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// rename new to old
name|fs
operator|.
name|rename
argument_list|(
name|newMirrorPath
argument_list|,
name|oldMirrorPath
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Node label store recover is completed"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateNodeToLabelsMappings ( Map<NodeId, Set<String>> nodeToLabels)
specifier|public
name|void
name|updateNodeToLabelsMappings
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodeToLabels
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNewMirror
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeNewClusterNodeLabels (List<NodeLabel> labels)
specifier|public
name|void
name|storeNewClusterNodeLabels
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNewMirror
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeClusterNodeLabels (Collection<String> labels)
specifier|public
name|void
name|removeClusterNodeLabels
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNewMirror
argument_list|()
expr_stmt|;
block|}
DECL|method|writeNewMirror ()
specifier|private
name|void
name|writeNewMirror
parameter_list|()
throws|throws
name|IOException
block|{
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
init|=
name|manager
operator|.
name|readLock
decl_stmt|;
comment|// Acquire readlock to make sure we get cluster node labels and
comment|// node-to-labels mapping atomically.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Write mirror to mirror.new.tmp file
name|Path
name|newTmpPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
operator|+
literal|".new.tmp"
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|newTmpPath
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|StoreOp
name|op
init|=
name|FSStoreOpHandler
operator|.
name|getMirrorOp
argument_list|(
name|getStoreType
argument_list|()
argument_list|)
decl_stmt|;
name|op
operator|.
name|write
argument_list|(
name|os
argument_list|,
name|manager
argument_list|)
expr_stmt|;
block|}
comment|// Rename mirror.new.tmp to mirror.new (will remove .new if it's existed)
name|Path
name|newPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
operator|+
literal|".new"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|newPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|newTmpPath
argument_list|,
name|newPath
argument_list|)
expr_stmt|;
comment|// Remove existing mirror and rename mirror.new to mirror
name|Path
name|mirrorPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|mirrorPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|newPath
argument_list|,
name|mirrorPath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

