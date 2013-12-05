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
name|ArrayList
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Maintains a list of queues as well as scheduling parameters for each queue,  * such as guaranteed share allocations, from the fair scheduler config file.  *   */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|QueueManager
specifier|public
class|class
name|QueueManager
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|QueueManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ROOT_QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_QUEUE
init|=
literal|"root"
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|FairScheduler
name|scheduler
decl_stmt|;
DECL|field|leafQueues
specifier|private
specifier|final
name|Collection
argument_list|<
name|FSLeafQueue
argument_list|>
name|leafQueues
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|FSLeafQueue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|queues
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FSQueue
argument_list|>
name|queues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FSQueue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rootQueue
specifier|private
name|FSParentQueue
name|rootQueue
decl_stmt|;
DECL|method|QueueManager (FairScheduler scheduler)
specifier|public
name|QueueManager
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
DECL|method|getRootQueue ()
specifier|public
name|FSParentQueue
name|getRootQueue
parameter_list|()
block|{
return|return
name|rootQueue
return|;
block|}
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|AllocationConfigurationException
throws|,
name|ParserConfigurationException
block|{
name|rootQueue
operator|=
operator|new
name|FSParentQueue
argument_list|(
literal|"root"
argument_list|,
name|scheduler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|rootQueue
operator|.
name|getName
argument_list|()
argument_list|,
name|rootQueue
argument_list|)
expr_stmt|;
comment|// Create the default queue
name|getLeafQueue
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get a queue by name, creating it if the create param is true and is necessary.    * If the queue is not or can not be a leaf queue, i.e. it already exists as a    * parent queue, or one of the parents in its name is already a leaf queue,    * null is returned.    *     * The root part of the name is optional, so a queue underneath the root     * named "queue1" could be referred to  as just "queue1", and a queue named    * "queue2" underneath a parent named "parent1" that is underneath the root     * could be referred to as just "parent1.queue2".    */
DECL|method|getLeafQueue (String name, boolean create)
specifier|public
name|FSLeafQueue
name|getLeafQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|ROOT_QUEUE
operator|+
literal|"."
argument_list|)
condition|)
block|{
name|name
operator|=
name|ROOT_QUEUE
operator|+
literal|"."
operator|+
name|name
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|queues
init|)
block|{
name|FSQueue
name|queue
init|=
name|queues
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
operator|&&
name|create
condition|)
block|{
name|FSLeafQueue
name|leafQueue
init|=
name|createLeafQueue
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafQueue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|queue
operator|=
name|leafQueue
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queue
operator|instanceof
name|FSParentQueue
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|FSLeafQueue
operator|)
name|queue
return|;
block|}
block|}
comment|/**    * Creates a leaf queue and places it in the tree. Creates any    * parents that don't already exist.    *     * @return    *    the created queue, if successful. null if not allowed (one of the parent    *    queues in the queue name is already a leaf queue)    */
DECL|method|createLeafQueue (String name)
specifier|private
name|FSLeafQueue
name|createLeafQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|newQueueNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|newQueueNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|int
name|sepIndex
init|=
name|name
operator|.
name|length
argument_list|()
decl_stmt|;
name|FSParentQueue
name|parent
init|=
literal|null
decl_stmt|;
comment|// Move up the queue tree until we reach one that exists.
while|while
condition|(
name|sepIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|sepIndex
operator|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|,
name|sepIndex
operator|-
literal|1
argument_list|)
expr_stmt|;
name|FSQueue
name|queue
decl_stmt|;
name|String
name|curName
init|=
literal|null
decl_stmt|;
name|curName
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sepIndex
argument_list|)
expr_stmt|;
name|queue
operator|=
name|queues
operator|.
name|get
argument_list|(
name|curName
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|newQueueNames
operator|.
name|add
argument_list|(
name|curName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|queue
operator|instanceof
name|FSParentQueue
condition|)
block|{
name|parent
operator|=
operator|(
name|FSParentQueue
operator|)
name|queue
expr_stmt|;
break|break;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|// At this point, parent refers to the deepest existing parent of the
comment|// queue to create.
comment|// Now that we know everything worked out, make all the queues
comment|// and add them to the map.
name|AllocationConfiguration
name|queueConf
init|=
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
decl_stmt|;
name|FSLeafQueue
name|leafQueue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|newQueueNames
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|queueName
init|=
name|newQueueNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// First name added was the leaf queue
name|leafQueue
operator|=
operator|new
name|FSLeafQueue
argument_list|(
name|name
argument_list|,
name|scheduler
argument_list|,
name|parent
argument_list|)
expr_stmt|;
try|try
block|{
name|leafQueue
operator|.
name|setPolicy
argument_list|(
name|queueConf
operator|.
name|getDefaultSchedulingPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AllocationConfigurationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to set default scheduling policy "
operator|+
name|queueConf
operator|.
name|getDefaultSchedulingPolicy
argument_list|()
operator|+
literal|" on new leaf queue."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|addChildQueue
argument_list|(
name|leafQueue
argument_list|)
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|leafQueue
operator|.
name|getName
argument_list|()
argument_list|,
name|leafQueue
argument_list|)
expr_stmt|;
name|leafQueues
operator|.
name|add
argument_list|(
name|leafQueue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FSParentQueue
name|newParent
init|=
operator|new
name|FSParentQueue
argument_list|(
name|queueName
argument_list|,
name|scheduler
argument_list|,
name|parent
argument_list|)
decl_stmt|;
try|try
block|{
name|newParent
operator|.
name|setPolicy
argument_list|(
name|queueConf
operator|.
name|getDefaultSchedulingPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AllocationConfigurationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to set default scheduling policy "
operator|+
name|queueConf
operator|.
name|getDefaultSchedulingPolicy
argument_list|()
operator|+
literal|" on new parent queue."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|addChildQueue
argument_list|(
name|newParent
argument_list|)
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|newParent
operator|.
name|getName
argument_list|()
argument_list|,
name|newParent
argument_list|)
expr_stmt|;
name|parent
operator|=
name|newParent
expr_stmt|;
block|}
block|}
return|return
name|leafQueue
return|;
block|}
comment|/**    * Gets a queue by name.    */
DECL|method|getQueue (String name)
specifier|public
name|FSQueue
name|getQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|ROOT_QUEUE
operator|+
literal|"."
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|ROOT_QUEUE
argument_list|)
condition|)
block|{
name|name
operator|=
name|ROOT_QUEUE
operator|+
literal|"."
operator|+
name|name
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|queues
init|)
block|{
return|return
name|queues
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**    * Return whether a queue exists already.    */
DECL|method|exists (String name)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
name|ROOT_QUEUE
operator|+
literal|"."
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|ROOT_QUEUE
argument_list|)
condition|)
block|{
name|name
operator|=
name|ROOT_QUEUE
operator|+
literal|"."
operator|+
name|name
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|queues
init|)
block|{
return|return
name|queues
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**    * Get a collection of all leaf queues    */
DECL|method|getLeafQueues ()
specifier|public
name|Collection
argument_list|<
name|FSLeafQueue
argument_list|>
name|getLeafQueues
parameter_list|()
block|{
synchronized|synchronized
init|(
name|queues
init|)
block|{
return|return
name|leafQueues
return|;
block|}
block|}
comment|/**    * Get a collection of all queues    */
DECL|method|getQueues ()
specifier|public
name|Collection
argument_list|<
name|FSQueue
argument_list|>
name|getQueues
parameter_list|()
block|{
return|return
name|queues
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|updateAllocationConfiguration (AllocationConfiguration queueConf)
specifier|public
name|void
name|updateAllocationConfiguration
parameter_list|(
name|AllocationConfiguration
name|queueConf
parameter_list|)
block|{
comment|// Make sure all queues exist
for|for
control|(
name|String
name|name
range|:
name|queueConf
operator|.
name|getQueueNames
argument_list|()
control|)
block|{
name|getLeafQueue
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FSQueue
name|queue
range|:
name|queues
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Update queue metrics
name|FSQueueMetrics
name|queueMetrics
init|=
name|queue
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|queueMetrics
operator|.
name|setMinShare
argument_list|(
name|queue
operator|.
name|getMinShare
argument_list|()
argument_list|)
expr_stmt|;
name|queueMetrics
operator|.
name|setMaxShare
argument_list|(
name|queue
operator|.
name|getMaxShare
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set scheduling policies
try|try
block|{
name|SchedulingPolicy
name|policy
init|=
name|queueConf
operator|.
name|getSchedulingPolicy
argument_list|(
name|queue
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|policy
operator|.
name|initialize
argument_list|(
name|scheduler
operator|.
name|getClusterCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setPolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AllocationConfigurationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot apply configured scheduling policy to queue "
operator|+
name|queue
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

