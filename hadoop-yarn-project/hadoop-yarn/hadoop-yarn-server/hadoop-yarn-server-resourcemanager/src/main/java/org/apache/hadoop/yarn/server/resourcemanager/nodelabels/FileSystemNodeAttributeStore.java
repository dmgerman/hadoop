begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.nodelabels
package|package
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
name|resourcemanager
operator|.
name|nodelabels
package|;
end_package

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
name|nodelabels
operator|.
name|NodeAttributeStore
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
name|NodeAttributesManager
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
name|AbstractFSNodeStore
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
name|op
operator|.
name|AddNodeToAttributeLogOp
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
name|op
operator|.
name|RemoveNodeToAttributeLogOp
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
name|op
operator|.
name|ReplaceNodeToAttributeLogOp
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
name|NodeToAttributes
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
name|List
import|;
end_import

begin_comment
comment|/**  * File system node attribute implementation.  */
end_comment

begin_class
DECL|class|FileSystemNodeAttributeStore
specifier|public
class|class
name|FileSystemNodeAttributeStore
extends|extends
name|AbstractFSNodeStore
argument_list|<
name|NodeAttributesManager
argument_list|>
implements|implements
name|NodeAttributeStore
block|{
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
name|FileSystemNodeAttributeStore
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
literal|"node-attribute"
decl_stmt|;
DECL|field|MIRROR_FILENAME
specifier|protected
specifier|static
specifier|final
name|String
name|MIRROR_FILENAME
init|=
literal|"nodeattribute.mirror"
decl_stmt|;
DECL|field|EDITLOG_FILENAME
specifier|protected
specifier|static
specifier|final
name|String
name|EDITLOG_FILENAME
init|=
literal|"nodeattribute.editlog"
decl_stmt|;
DECL|method|FileSystemNodeAttributeStore ()
specifier|public
name|FileSystemNodeAttributeStore
parameter_list|()
block|{
name|super
argument_list|(
name|FSStoreOpHandler
operator|.
name|StoreType
operator|.
name|NODE_ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultFSNodeAttributeRootDir ()
specifier|private
name|String
name|getDefaultFSNodeAttributeRootDir
parameter_list|()
throws|throws
name|IOException
block|{
comment|// default is in local: /tmp/hadoop-yarn-${user}/node-attribute/
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
DECL|method|init (Configuration conf, NodeAttributesManager mgr)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NodeAttributesManager
name|mgr
parameter_list|)
throws|throws
name|Exception
block|{
name|StoreSchema
name|schema
init|=
operator|new
name|StoreSchema
argument_list|(
name|EDITLOG_FILENAME
argument_list|,
name|MIRROR_FILENAME
argument_list|)
decl_stmt|;
name|initStore
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|FS_NODE_ATTRIBUTE_STORE_ROOT_DIR
argument_list|,
name|getDefaultFSNodeAttributeRootDir
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|schema
argument_list|,
name|mgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replaceNodeAttributes (List<NodeToAttributes> nodeToAttribute)
specifier|public
name|void
name|replaceNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeToAttribute
parameter_list|)
throws|throws
name|IOException
block|{
name|ReplaceNodeToAttributeLogOp
name|op
init|=
operator|new
name|ReplaceNodeToAttributeLogOp
argument_list|()
decl_stmt|;
name|writeToLog
argument_list|(
name|op
operator|.
name|setAttributes
argument_list|(
name|nodeToAttribute
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNodeAttributes (List<NodeToAttributes> nodeAttributeMapping)
specifier|public
name|void
name|addNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeAttributeMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|AddNodeToAttributeLogOp
name|op
init|=
operator|new
name|AddNodeToAttributeLogOp
argument_list|()
decl_stmt|;
name|writeToLog
argument_list|(
name|op
operator|.
name|setAttributes
argument_list|(
name|nodeAttributeMapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeNodeAttributes (List<NodeToAttributes> nodeAttributeMapping)
specifier|public
name|void
name|removeNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeAttributeMapping
parameter_list|)
throws|throws
name|IOException
block|{
name|RemoveNodeToAttributeLogOp
name|op
init|=
operator|new
name|RemoveNodeToAttributeLogOp
argument_list|()
decl_stmt|;
name|writeToLog
argument_list|(
name|op
operator|.
name|setAttributes
argument_list|(
name|nodeAttributeMapping
argument_list|)
argument_list|)
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
name|IOException
throws|,
name|YarnException
block|{
name|super
operator|.
name|recoverFromStore
argument_list|()
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
name|super
operator|.
name|closeFSStore
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

