begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|scheduler
operator|.
name|fair
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
name|HashMap
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
name|hadoop
operator|.
name|security
operator|.
name|Groups
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
DECL|class|QueuePlacementRule
specifier|public
specifier|abstract
class|class
name|QueuePlacementRule
block|{
DECL|field|create
specifier|protected
name|boolean
name|create
decl_stmt|;
comment|/**    * Initializes the rule with any arguments.    *     * @param args    *    Additional attributes of the rule's xml element other than create.    */
DECL|method|initialize (boolean create, Map<String, String> args)
specifier|public
name|QueuePlacementRule
name|initialize
parameter_list|(
name|boolean
name|create
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|create
operator|=
name|create
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    *     * @param requestedQueue    *    The queue explicitly requested.    * @param user    *    The user submitting the app.    * @param groups    *    The groups of the user submitting the app.    * @param configuredQueues    *    The queues specified in the scheduler configuration.    * @return    *    The queue to place the app into. An empty string indicates that we should    *    continue to the next rule, and null indicates that the app should be rejected.    */
DECL|method|assignAppToQueue (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|public
name|String
name|assignAppToQueue
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|queue
init|=
name|getQueueForApp
argument_list|(
name|requestedQueue
argument_list|,
name|user
argument_list|,
name|groups
argument_list|,
name|configuredQueues
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
operator|||
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|contains
argument_list|(
name|queue
argument_list|)
operator|||
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|contains
argument_list|(
name|queue
argument_list|)
condition|)
block|{
return|return
name|queue
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
DECL|method|initializeFromXml (Element el)
specifier|public
name|void
name|initializeFromXml
parameter_list|(
name|Element
name|el
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
name|boolean
name|create
init|=
literal|true
decl_stmt|;
name|NamedNodeMap
name|attributes
init|=
name|el
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|attributes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|node
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|node
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"create"
argument_list|)
condition|)
block|{
name|create
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|args
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|initialize
argument_list|(
name|create
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if this rule never tells the policy to continue.    */
DECL|method|isTerminal ()
specifier|public
specifier|abstract
name|boolean
name|isTerminal
parameter_list|()
function_decl|;
comment|/**    * Applies this rule to an app with the given requested queue and user/group    * information.    *     * @param requestedQueue    *    The queue specified in the ApplicationSubmissionContext    * @param user    *    The user submitting the app.    * @param groups    *    The groups of the user submitting the app.    * @return    *    The name of the queue to assign the app to, or null to empty string    *    continue to the next rule.    */
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
specifier|abstract
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Places apps in queues by username of the submitter    */
DECL|class|User
specifier|public
specifier|static
class|class
name|User
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
block|{
return|return
literal|"root."
operator|+
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
name|create
return|;
block|}
block|}
comment|/**    * Places apps in queues by primary group of the submitter    */
DECL|class|PrimaryGroup
specifier|public
specifier|static
class|class
name|PrimaryGroup
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|"root."
operator|+
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
name|create
return|;
block|}
block|}
comment|/**    * Places apps in queues by secondary group of the submitter    *     * Match will be made on first secondary group that exist in    * queues    */
DECL|class|SecondaryGroupExistingQueue
specifier|public
specifier|static
class|class
name|SecondaryGroupExistingQueue
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groupNames
init|=
name|groups
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|groupNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|group
init|=
name|groupNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|contains
argument_list|(
literal|"root."
operator|+
name|group
argument_list|)
operator|||
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|PARENT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"root."
operator|+
name|group
argument_list|)
condition|)
block|{
return|return
literal|"root."
operator|+
name|groupNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Places apps in queues with name of the submitter under the queue    * returned by the nested rule.    */
DECL|class|NestedUserQueue
specifier|public
specifier|static
class|class
name|NestedUserQueue
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|VisibleForTesting
DECL|field|nestedRule
name|QueuePlacementRule
name|nestedRule
decl_stmt|;
comment|/**      * Parse xml and instantiate the nested rule       */
annotation|@
name|Override
DECL|method|initializeFromXml (Element el)
specifier|public
name|void
name|initializeFromXml
parameter_list|(
name|Element
name|el
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
name|NodeList
name|elements
init|=
name|el
operator|.
name|getChildNodes
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
name|elements
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|elements
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|element
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
literal|"rule"
operator|.
name|equals
argument_list|(
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|QueuePlacementRule
name|rule
init|=
name|QueuePlacementPolicy
operator|.
name|createAndInitializeRule
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|rule
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"Unable to create nested rule in nestedUserQueue rule"
argument_list|)
throw|;
block|}
name|this
operator|.
name|nestedRule
operator|=
name|rule
expr_stmt|;
break|break;
block|}
else|else
block|{
continue|continue;
block|}
block|}
block|}
if|if
condition|(
name|this
operator|.
name|nestedRule
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"No nested rule specified in<nestedUserQueue> rule"
argument_list|)
throw|;
block|}
name|super
operator|.
name|initializeFromXml
argument_list|(
name|el
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Apply the nested rule
name|String
name|queueName
init|=
name|nestedRule
operator|.
name|assignAppToQueue
argument_list|(
name|requestedQueue
argument_list|,
name|user
argument_list|,
name|groups
argument_list|,
name|configuredQueues
argument_list|)
decl_stmt|;
if|if
condition|(
name|queueName
operator|!=
literal|null
operator|&&
name|queueName
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|queueName
operator|.
name|startsWith
argument_list|(
literal|"root."
argument_list|)
condition|)
block|{
name|queueName
operator|=
literal|"root."
operator|+
name|queueName
expr_stmt|;
block|}
comment|// Verify if the queue returned by the nested rule is an configured leaf queue,
comment|// if yes then skip to next rule in the queue placement policy
if|if
condition|(
name|configuredQueues
operator|.
name|get
argument_list|(
name|FSQueueType
operator|.
name|LEAF
argument_list|)
operator|.
name|contains
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|queueName
operator|+
literal|"."
operator|+
name|user
return|;
block|}
return|return
name|queueName
return|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Places apps in queues by requested queue of the submitter    */
DECL|class|Specified
specifier|public
specifier|static
class|class
name|Specified
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
block|{
if|if
condition|(
name|requestedQueue
operator|.
name|equals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|requestedQueue
operator|.
name|startsWith
argument_list|(
literal|"root."
argument_list|)
condition|)
block|{
name|requestedQueue
operator|=
literal|"root."
operator|+
name|requestedQueue
expr_stmt|;
block|}
return|return
name|requestedQueue
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Places all apps in the default queue    */
DECL|class|Default
specifier|public
specifier|static
class|class
name|Default
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
block|{
return|return
literal|"root."
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Rejects all apps    */
DECL|class|Reject
specifier|public
specifier|static
class|class
name|Reject
extends|extends
name|QueuePlacementRule
block|{
annotation|@
name|Override
DECL|method|assignAppToQueue (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|public
name|String
name|assignAppToQueue
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueForApp (String requestedQueue, String user, Groups groups, Map<FSQueueType, Set<String>> configuredQueues)
specifier|protected
name|String
name|getQueueForApp
parameter_list|(
name|String
name|requestedQueue
parameter_list|,
name|String
name|user
parameter_list|,
name|Groups
name|groups
parameter_list|,
name|Map
argument_list|<
name|FSQueueType
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|configuredQueues
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|isTerminal ()
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

