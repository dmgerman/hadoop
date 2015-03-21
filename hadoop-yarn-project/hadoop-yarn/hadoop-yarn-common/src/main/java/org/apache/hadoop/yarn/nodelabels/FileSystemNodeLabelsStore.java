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
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|FileSystem
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
name|LocalFileSystem
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
name|security
operator|.
name|UserGroupInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
operator|.
name|AddToClusterNodeLabelsRequestProto
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
operator|.
name|RemoveFromClusterNodeLabelsRequestProto
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
operator|.
name|ReplaceLabelsOnNodeRequestProto
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
operator|.
name|UpdateNodeLabelsRequestProto
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AddToClusterNodeLabelsRequest
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RemoveFromClusterNodeLabelsRequest
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ReplaceLabelsOnNodeRequest
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|UpdateNodeLabelsRequest
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|AddToClusterNodeLabelsRequestPBImpl
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RemoveFromClusterNodeLabelsRequestPBImpl
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|ReplaceLabelsOnNodeRequestPBImpl
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|UpdateNodeLabelsRequestPBImpl
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_class
DECL|class|FileSystemNodeLabelsStore
specifier|public
class|class
name|FileSystemNodeLabelsStore
extends|extends
name|NodeLabelsStore
block|{
DECL|method|FileSystemNodeLabelsStore (CommonNodeLabelsManager mgr)
specifier|public
name|FileSystemNodeLabelsStore
parameter_list|(
name|CommonNodeLabelsManager
name|mgr
parameter_list|)
block|{
name|super
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
block|}
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FileSystemNodeLabelsStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_DIR_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_DIR_NAME
init|=
literal|"node-labels"
decl_stmt|;
DECL|field|MIRROR_FILENAME
specifier|protected
specifier|static
specifier|final
name|String
name|MIRROR_FILENAME
init|=
literal|"nodelabel.mirror"
decl_stmt|;
DECL|field|EDITLOG_FILENAME
specifier|protected
specifier|static
specifier|final
name|String
name|EDITLOG_FILENAME
init|=
literal|"nodelabel.editlog"
decl_stmt|;
DECL|enum|SerializedLogType
specifier|protected
enum|enum
name|SerializedLogType
block|{
DECL|enumConstant|ADD_LABELS
DECL|enumConstant|NODE_TO_LABELS
DECL|enumConstant|REMOVE_LABELS
DECL|enumConstant|UPDATE_NODE_LABELS
name|ADD_LABELS
block|,
name|NODE_TO_LABELS
block|,
name|REMOVE_LABELS
block|,
name|UPDATE_NODE_LABELS
block|}
DECL|field|fsWorkingPath
name|Path
name|fsWorkingPath
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
decl_stmt|;
DECL|field|editlogOs
name|FSDataOutputStream
name|editlogOs
decl_stmt|;
DECL|field|editLogPath
name|Path
name|editLogPath
decl_stmt|;
DECL|method|getDefaultFSNodeLabelsRootDir ()
specifier|private
name|String
name|getDefaultFSNodeLabelsRootDir
parameter_list|()
throws|throws
name|IOException
block|{
comment|// default is in local: /tmp/hadoop-yarn-${user}/node-labels/
return|return
literal|"file:///tmp/hadoop-yarn-"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|"/"
operator|+
name|DEFAULT_DIR_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|fsWorkingPath
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_LABELS_STORE_ROOT_DIR
argument_list|,
name|getDefaultFSNodeLabelsRootDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// mkdir of root dir path
name|fs
operator|.
name|mkdirs
argument_list|(
name|fsWorkingPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|editlogOs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception happened whiling shutting down,"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setFileSystem (Configuration conf)
specifier|private
name|void
name|setFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|confCopy
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confCopy
operator|.
name|setBoolean
argument_list|(
literal|"dfs.client.retry.policy.enabled"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|retryPolicy
init|=
name|confCopy
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_LABELS_STORE_RETRY_POLICY_SPEC
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_FS_NODE_LABELS_STORE_RETRY_POLICY_SPEC
argument_list|)
decl_stmt|;
name|confCopy
operator|.
name|set
argument_list|(
literal|"dfs.client.retry.policy.spec"
argument_list|,
name|retryPolicy
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fsWorkingPath
operator|.
name|getFileSystem
argument_list|(
name|confCopy
argument_list|)
expr_stmt|;
comment|// if it's local file system, use RawLocalFileSystem instead of
comment|// LocalFileSystem, the latter one doesn't support append.
if|if
condition|(
name|fs
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|fs
operator|=
operator|(
operator|(
name|LocalFileSystem
operator|)
name|fs
operator|)
operator|.
name|getRaw
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ensureAppendEditlogFile ()
specifier|private
name|void
name|ensureAppendEditlogFile
parameter_list|()
throws|throws
name|IOException
block|{
name|editlogOs
operator|=
name|fs
operator|.
name|append
argument_list|(
name|editLogPath
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureCloseEditlogFile ()
specifier|private
name|void
name|ensureCloseEditlogFile
parameter_list|()
throws|throws
name|IOException
block|{
name|editlogOs
operator|.
name|close
argument_list|()
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
name|ensureAppendEditlogFile
argument_list|()
expr_stmt|;
name|editlogOs
operator|.
name|writeInt
argument_list|(
name|SerializedLogType
operator|.
name|NODE_TO_LABELS
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ReplaceLabelsOnNodeRequestPBImpl
operator|)
name|ReplaceLabelsOnNodeRequest
operator|.
name|newInstance
argument_list|(
name|nodeToLabels
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|editlogOs
argument_list|)
expr_stmt|;
name|ensureCloseEditlogFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeNewClusterNodeLabels (Set<String> labels)
specifier|public
name|void
name|storeNewClusterNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureAppendEditlogFile
argument_list|()
expr_stmt|;
name|editlogOs
operator|.
name|writeInt
argument_list|(
name|SerializedLogType
operator|.
name|ADD_LABELS
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AddToClusterNodeLabelsRequestPBImpl
operator|)
name|AddToClusterNodeLabelsRequest
operator|.
name|newInstance
argument_list|(
name|labels
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|editlogOs
argument_list|)
expr_stmt|;
name|ensureCloseEditlogFile
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
name|ensureAppendEditlogFile
argument_list|()
expr_stmt|;
name|editlogOs
operator|.
name|writeInt
argument_list|(
name|SerializedLogType
operator|.
name|REMOVE_LABELS
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|RemoveFromClusterNodeLabelsRequestPBImpl
operator|)
name|RemoveFromClusterNodeLabelsRequest
operator|.
name|newInstance
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|labels
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|editlogOs
argument_list|)
expr_stmt|;
name|ensureCloseEditlogFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateNodeLabels (List<NodeLabel> updatedNodeLabels)
specifier|public
name|void
name|updateNodeLabels
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|updatedNodeLabels
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureAppendEditlogFile
argument_list|()
expr_stmt|;
name|editlogOs
operator|.
name|writeInt
argument_list|(
name|SerializedLogType
operator|.
name|UPDATE_NODE_LABELS
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|UpdateNodeLabelsRequestPBImpl
operator|)
name|UpdateNodeLabelsRequest
operator|.
name|newInstance
argument_list|(
name|updatedNodeLabels
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|editlogOs
argument_list|)
expr_stmt|;
name|ensureCloseEditlogFile
argument_list|()
expr_stmt|;
block|}
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
comment|/*      * Steps of recover      * 1) Read from last mirror (from mirror or mirror.old)      * 2) Read from last edit log, and apply such edit log      * 3) Write new mirror to mirror.writing      * 4) Rename mirror to mirror.old      * 5) Move mirror.writing to mirror      * 6) Remove mirror.old      * 7) Remove edit log and create a new empty edit log       */
comment|// Open mirror from serialized file
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
name|Path
name|oldMirrorPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
operator|+
literal|".old"
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|mirrorPath
argument_list|)
condition|)
block|{
name|is
operator|=
name|fs
operator|.
name|open
argument_list|(
name|mirrorPath
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|oldMirrorPath
argument_list|)
condition|)
block|{
name|is
operator|=
name|fs
operator|.
name|open
argument_list|(
name|oldMirrorPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|is
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|labels
init|=
operator|new
name|AddToClusterNodeLabelsRequestPBImpl
argument_list|(
name|AddToClusterNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeLabels
argument_list|()
decl_stmt|;
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
init|=
operator|new
name|ReplaceLabelsOnNodeRequestPBImpl
argument_list|(
name|ReplaceLabelsOnNodeRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeToLabels
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|nodeToLabels
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Open and process editlog
name|editLogPath
operator|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|EDITLOG_FILENAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|editLogPath
argument_list|)
condition|)
block|{
name|is
operator|=
name|fs
operator|.
name|open
argument_list|(
name|editLogPath
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
comment|// read edit log one by one
name|SerializedLogType
name|type
init|=
name|SerializedLogType
operator|.
name|values
argument_list|()
index|[
name|is
operator|.
name|readInt
argument_list|()
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ADD_LABELS
case|:
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
init|=
name|AddToClusterNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
operator|.
name|getNodeLabelsList
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|labels
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_LABELS
case|:
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
init|=
name|RemoveFromClusterNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
operator|.
name|getNodeLabelsList
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|NODE_TO_LABELS
case|:
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|ReplaceLabelsOnNodeRequestPBImpl
argument_list|(
name|ReplaceLabelsOnNodeRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeToLabels
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|map
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|UPDATE_NODE_LABELS
case|:
block|{
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|attributes
init|=
operator|new
name|UpdateNodeLabelsRequestPBImpl
argument_list|(
name|UpdateNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeLabels
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|updateNodeLabels
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// EOF hit, break
break|break;
block|}
block|}
block|}
comment|// Serialize current mirror to mirror.writing
name|Path
name|writingMirrorPath
init|=
operator|new
name|Path
argument_list|(
name|fsWorkingPath
argument_list|,
name|MIRROR_FILENAME
operator|+
literal|".writing"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|writingMirrorPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
operator|(
operator|(
name|AddToClusterNodeLabelsRequestPBImpl
operator|)
name|AddToClusterNodeLabelsRequestPBImpl
operator|.
name|newInstance
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ReplaceLabelsOnNodeRequestPBImpl
operator|)
name|ReplaceLabelsOnNodeRequest
operator|.
name|newInstance
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Move mirror to mirror.old
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|mirrorPath
argument_list|)
condition|)
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
name|fs
operator|.
name|rename
argument_list|(
name|mirrorPath
argument_list|,
name|oldMirrorPath
argument_list|)
expr_stmt|;
block|}
comment|// move mirror.writing to mirror
name|fs
operator|.
name|rename
argument_list|(
name|writingMirrorPath
argument_list|,
name|mirrorPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|writingMirrorPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// remove mirror.old
name|fs
operator|.
name|delete
argument_list|(
name|oldMirrorPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create a new editlog file
name|editlogOs
operator|=
name|fs
operator|.
name|create
argument_list|(
name|editLogPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|editlogOs
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished write mirror at:"
operator|+
name|mirrorPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished create editlog file at:"
operator|+
name|editLogPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

