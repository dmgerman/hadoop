begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerDynamicEditException
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
name|scheduler
operator|.
name|common
operator|.
name|QueueEntitlement
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
name|io
operator|.
name|IOException
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
import|;
end_import

begin_comment
comment|/**  * Abstract class for dynamic auto created queues managed by an implementation  * of AbstractManagedParentQueue  */
end_comment

begin_class
DECL|class|AbstractAutoCreatedLeafQueue
specifier|public
class|class
name|AbstractAutoCreatedLeafQueue
extends|extends
name|LeafQueue
block|{
DECL|field|parent
specifier|protected
name|AbstractManagedParentQueue
name|parent
decl_stmt|;
DECL|method|AbstractAutoCreatedLeafQueue (CapacitySchedulerContext cs, String queueName, AbstractManagedParentQueue parent, CSQueue old)
specifier|public
name|AbstractAutoCreatedLeafQueue
parameter_list|(
name|CapacitySchedulerContext
name|cs
parameter_list|,
name|String
name|queueName
parameter_list|,
name|AbstractManagedParentQueue
name|parent
parameter_list|,
name|CSQueue
name|old
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cs
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|old
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
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
name|AbstractAutoCreatedLeafQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AbstractAutoCreatedLeafQueue (CapacitySchedulerContext cs, CapacitySchedulerConfiguration leafQueueConfigs, String queueName, AbstractManagedParentQueue parent, CSQueue old)
specifier|public
name|AbstractAutoCreatedLeafQueue
parameter_list|(
name|CapacitySchedulerContext
name|cs
parameter_list|,
name|CapacitySchedulerConfiguration
name|leafQueueConfigs
parameter_list|,
name|String
name|queueName
parameter_list|,
name|AbstractManagedParentQueue
name|parent
parameter_list|,
name|CSQueue
name|old
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cs
argument_list|,
name|leafQueueConfigs
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|old
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**    * This methods to change capacity for a queue and adjusts its    * absoluteCapacity    *    * @param entitlement the new entitlement for the queue (capacity,    *                    maxCapacity, etc..)    * @throws SchedulerDynamicEditException    */
DECL|method|setEntitlement (QueueEntitlement entitlement)
specifier|public
name|void
name|setEntitlement
parameter_list|(
name|QueueEntitlement
name|entitlement
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
name|setEntitlement
argument_list|(
name|NO_LABEL
argument_list|,
name|entitlement
argument_list|)
expr_stmt|;
block|}
comment|/**    * This methods to change capacity for a queue and adjusts its    * absoluteCapacity    *    * @param entitlement the new entitlement for the queue (capacity,    *                    maxCapacity, etc..)    * @throws SchedulerDynamicEditException    */
DECL|method|setEntitlement (String nodeLabel, QueueEntitlement entitlement)
specifier|public
name|void
name|setEntitlement
parameter_list|(
name|String
name|nodeLabel
parameter_list|,
name|QueueEntitlement
name|entitlement
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|float
name|capacity
init|=
name|entitlement
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
if|if
condition|(
name|capacity
argument_list|<
literal|0
operator|||
name|capacity
argument_list|>
literal|1.0f
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Capacity demand is not in the [0,1] range: "
operator|+
name|capacity
argument_list|)
throw|;
block|}
name|setCapacity
argument_list|(
name|nodeLabel
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
name|setAbsoluteCapacity
argument_list|(
name|nodeLabel
argument_list|,
name|getParent
argument_list|()
operator|.
name|getQueueCapacities
argument_list|()
operator|.
name|getAbsoluteCapacity
argument_list|(
name|nodeLabel
argument_list|)
operator|*
name|getQueueCapacities
argument_list|()
operator|.
name|getCapacity
argument_list|(
name|nodeLabel
argument_list|)
argument_list|)
expr_stmt|;
comment|// note: we currently set maxCapacity to capacity
comment|// this might be revised later
name|setMaxCapacity
argument_list|(
name|nodeLabel
argument_list|,
name|entitlement
operator|.
name|getMaxCapacity
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"successfully changed to {} for queue {}"
argument_list|,
name|capacity
argument_list|,
name|this
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//update queue used capacity etc
name|CSQueueUtils
operator|.
name|updateQueueStatistics
argument_list|(
name|resourceCalculator
argument_list|,
name|csContext
operator|.
name|getClusterResource
argument_list|()
argument_list|,
name|this
argument_list|,
name|labelManager
argument_list|,
name|nodeLabel
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setupConfigurableCapacities (QueueCapacities queueCapacities)
specifier|protected
name|void
name|setupConfigurableCapacities
parameter_list|(
name|QueueCapacities
name|queueCapacities
parameter_list|)
block|{
name|CSQueueUtils
operator|.
name|updateAndCheckCapacitiesByLabel
argument_list|(
name|getQueuePath
argument_list|()
argument_list|,
name|queueCapacities
argument_list|,
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
operator|.
name|getQueueCapacities
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

