begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
package|;
end_package

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
name|component
operator|.
name|Component
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
name|conf
operator|.
name|YarnServiceConf
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import static
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|DEFAULT_NODE_BLACKLIST_THRESHOLD
import|;
end_import

begin_import
import|import static
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
name|conf
operator|.
name|YarnServiceConf
operator|.
name|NODE_BLACKLIST_THRESHOLD
import|;
end_import

begin_comment
comment|/**  * This tracks the container failures per node. If the failure counter exceeds  * the maxFailurePerNode limit, it'll blacklist that node.  *  */
end_comment

begin_class
DECL|class|ContainerFailureTracker
specifier|public
class|class
name|ContainerFailureTracker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerFailureTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Host -> num container failures
DECL|field|failureCountPerNode
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|failureCountPerNode
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|blackListedNodes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|blackListedNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
name|ServiceContext
name|context
decl_stmt|;
DECL|field|maxFailurePerNode
specifier|private
name|int
name|maxFailurePerNode
decl_stmt|;
DECL|field|component
specifier|private
name|Component
name|component
decl_stmt|;
DECL|method|ContainerFailureTracker (ServiceContext context, Component component)
specifier|public
name|ContainerFailureTracker
parameter_list|(
name|ServiceContext
name|context
parameter_list|,
name|Component
name|component
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
name|maxFailurePerNode
operator|=
name|YarnServiceConf
operator|.
name|getInt
argument_list|(
name|NODE_BLACKLIST_THRESHOLD
argument_list|,
name|DEFAULT_NODE_BLACKLIST_THRESHOLD
argument_list|,
name|component
operator|.
name|getComponentSpec
argument_list|()
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|context
operator|.
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|incNodeFailure (String host)
specifier|public
specifier|synchronized
name|void
name|incNodeFailure
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|int
name|num
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|failureCountPerNode
operator|.
name|containsKey
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|num
operator|=
name|failureCountPerNode
operator|.
name|get
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
name|num
operator|++
expr_stmt|;
name|failureCountPerNode
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|num
argument_list|)
expr_stmt|;
comment|// black list the node if exceed max failure
if|if
condition|(
name|num
operator|>
name|maxFailurePerNode
operator|&&
operator|!
name|blackListedNodes
operator|.
name|contains
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|blacklists
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|blacklists
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|blackListedNodes
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|getAmRMClient
argument_list|()
operator|.
name|updateBlacklist
argument_list|(
name|blacklists
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[COMPONENT {}]: Failed {} times on this host, blacklisted {}."
operator|+
literal|" Current list of blacklisted nodes: {}"
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|num
argument_list|,
name|host
argument_list|,
name|blackListedNodes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resetContainerFailures ()
specifier|public
specifier|synchronized
name|void
name|resetContainerFailures
parameter_list|()
block|{
comment|// reset container failure counter per node
name|failureCountPerNode
operator|.
name|clear
argument_list|()
expr_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|getAmRMClient
argument_list|()
operator|.
name|updateBlacklist
argument_list|(
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|blackListedNodes
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[COMPONENT {}]: Clearing blacklisted nodes {} "
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|blackListedNodes
argument_list|)
expr_stmt|;
name|blackListedNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

