begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ConcurrentHashMap
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
name|net
operator|.
name|NetUtils
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
name|HostsFileReader
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
name|event
operator|.
name|EventHandler
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppNodeUpdateEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppNodeUpdateEvent
operator|.
name|RMAppNodeUpdateType
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|NodesListManager
specifier|public
class|class
name|NodesListManager
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|NodesListManagerEvent
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NodesListManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hostsReader
specifier|private
name|HostsFileReader
name|hostsReader
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|unusableRMNodesConcurrentSet
specifier|private
name|Set
argument_list|<
name|RMNode
argument_list|>
name|unusableRMNodesConcurrentSet
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|RMNode
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|NodesListManager (RMContext rmContext)
specifier|public
name|NodesListManager
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|NodesListManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
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
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// Read the hosts/exclude files to restrict access to the RM
try|try
block|{
name|this
operator|.
name|hostsReader
operator|=
operator|new
name|HostsFileReader
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODES_INCLUDE_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE_PATH
argument_list|)
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODES_EXCLUDE_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|printConfiguredHosts
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to init hostsReader, disabling"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|hostsReader
operator|=
operator|new
name|HostsFileReader
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE_PATH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
comment|// Should *never* happen
name|this
operator|.
name|hostsReader
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|ioe2
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|printConfiguredHosts ()
specifier|private
name|void
name|printConfiguredHosts
parameter_list|()
block|{
if|if
condition|(
operator|!
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"hostsReader: in="
operator|+
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODES_INCLUDE_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_INCLUDE_FILE_PATH
argument_list|)
operator|+
literal|" out="
operator|+
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODES_EXCLUDE_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODES_EXCLUDE_FILE_PATH
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|include
range|:
name|hostsReader
operator|.
name|getHosts
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"include: "
operator|+
name|include
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|exclude
range|:
name|hostsReader
operator|.
name|getExcludedHosts
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exclude: "
operator|+
name|exclude
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|refreshNodes ()
specifier|public
name|void
name|refreshNodes
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|hostsReader
init|)
block|{
name|hostsReader
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|printConfiguredHosts
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isValidNode (String hostName)
specifier|public
name|boolean
name|isValidNode
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|hostsReader
init|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|hostsList
init|=
name|hostsReader
operator|.
name|getHosts
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|excludeList
init|=
name|hostsReader
operator|.
name|getExcludedHosts
argument_list|()
decl_stmt|;
name|String
name|ip
init|=
name|NetUtils
operator|.
name|normalizeHostName
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
return|return
operator|(
name|hostsList
operator|.
name|isEmpty
argument_list|()
operator|||
name|hostsList
operator|.
name|contains
argument_list|(
name|hostName
argument_list|)
operator|||
name|hostsList
operator|.
name|contains
argument_list|(
name|ip
argument_list|)
operator|)
operator|&&
operator|!
operator|(
name|excludeList
operator|.
name|contains
argument_list|(
name|hostName
argument_list|)
operator|||
name|excludeList
operator|.
name|contains
argument_list|(
name|ip
argument_list|)
operator|)
return|;
block|}
block|}
comment|/**    * Provides the currently unusable nodes. Copies it into provided collection.    * @param unUsableNodes    *          Collection to which the unusable nodes are added    * @return number of unusable nodes added    */
DECL|method|getUnusableNodes (Collection<RMNode> unUsableNodes)
specifier|public
name|int
name|getUnusableNodes
parameter_list|(
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|unUsableNodes
parameter_list|)
block|{
name|unUsableNodes
operator|.
name|addAll
argument_list|(
name|unusableRMNodesConcurrentSet
argument_list|)
expr_stmt|;
return|return
name|unusableRMNodesConcurrentSet
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|handle (NodesListManagerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|NodesListManagerEvent
name|event
parameter_list|)
block|{
name|RMNode
name|eventNode
init|=
name|event
operator|.
name|getNode
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|NODE_UNUSABLE
case|:
name|LOG
operator|.
name|debug
argument_list|(
name|eventNode
operator|+
literal|" reported unusable"
argument_list|)
expr_stmt|;
name|unusableRMNodesConcurrentSet
operator|.
name|add
argument_list|(
name|eventNode
argument_list|)
expr_stmt|;
for|for
control|(
name|RMApp
name|app
range|:
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppNodeUpdateEvent
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|eventNode
argument_list|,
name|RMAppNodeUpdateType
operator|.
name|NODE_UNUSABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|NODE_USABLE
case|:
if|if
condition|(
name|unusableRMNodesConcurrentSet
operator|.
name|contains
argument_list|(
name|eventNode
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|eventNode
operator|+
literal|" reported usable"
argument_list|)
expr_stmt|;
name|unusableRMNodesConcurrentSet
operator|.
name|remove
argument_list|(
name|eventNode
argument_list|)
expr_stmt|;
for|for
control|(
name|RMApp
name|app
range|:
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppNodeUpdateEvent
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|eventNode
argument_list|,
name|RMAppNodeUpdateType
operator|.
name|NODE_USABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|eventNode
operator|+
literal|" reported usable without first reporting unusable"
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Ignoring invalid eventtype "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

