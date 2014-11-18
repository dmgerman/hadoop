begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|reservation
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|RMContext
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
name|QueueMetrics
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
name|ResourceScheduler
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
name|capacity
operator|.
name|CSQueue
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
name|capacity
operator|.
name|CapacityScheduler
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
name|util
operator|.
name|resource
operator|.
name|ResourceCalculator
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

begin_comment
comment|/**  * This is the implementation of {@link ReservationSystem} based on the  * {@link CapacityScheduler}  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|class|CapacityReservationSystem
specifier|public
class|class
name|CapacityReservationSystem
extends|extends
name|AbstractReservationSystem
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
name|CapacityReservationSystem
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|capScheduler
specifier|private
name|CapacityScheduler
name|capScheduler
decl_stmt|;
DECL|method|CapacityReservationSystem ()
specifier|public
name|CapacityReservationSystem
parameter_list|()
block|{
name|super
argument_list|(
name|CapacityReservationSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reinitialize (Configuration conf, RMContext rmContext)
specifier|public
name|void
name|reinitialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// Validate if the scheduler is capacity based
name|ResourceScheduler
name|scheduler
init|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|scheduler
operator|instanceof
name|CapacityScheduler
operator|)
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class "
operator|+
name|scheduler
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" not instance of "
operator|+
name|CapacityScheduler
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
name|capScheduler
operator|=
operator|(
name|CapacityScheduler
operator|)
name|scheduler
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|super
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinAllocation ()
specifier|protected
name|Resource
name|getMinAllocation
parameter_list|()
block|{
return|return
name|capScheduler
operator|.
name|getMinimumResourceCapability
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxAllocation ()
specifier|protected
name|Resource
name|getMaxAllocation
parameter_list|()
block|{
return|return
name|capScheduler
operator|.
name|getMaximumResourceCapability
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceCalculator ()
specifier|protected
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
block|{
return|return
name|capScheduler
operator|.
name|getResourceCalculator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRootQueueMetrics ()
specifier|protected
name|QueueMetrics
name|getRootQueueMetrics
parameter_list|()
block|{
return|return
name|capScheduler
operator|.
name|getRootQueueMetrics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPlanQueuePath (String planQueueName)
specifier|protected
name|String
name|getPlanQueuePath
parameter_list|(
name|String
name|planQueueName
parameter_list|)
block|{
return|return
name|capScheduler
operator|.
name|getQueue
argument_list|(
name|planQueueName
argument_list|)
operator|.
name|getQueuePath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPlanQueueCapacity (String planQueueName)
specifier|protected
name|Resource
name|getPlanQueueCapacity
parameter_list|(
name|String
name|planQueueName
parameter_list|)
block|{
name|Resource
name|minAllocation
init|=
name|getMinAllocation
argument_list|()
decl_stmt|;
name|ResourceCalculator
name|rescCalc
init|=
name|getResourceCalculator
argument_list|()
decl_stmt|;
name|CSQueue
name|planQueue
init|=
name|capScheduler
operator|.
name|getQueue
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
return|return
name|rescCalc
operator|.
name|multiplyAndNormalizeDown
argument_list|(
name|capScheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|,
name|planQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|minAllocation
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ReservationSchedulerConfiguration
DECL|method|getReservationSchedulerConfiguration ()
name|getReservationSchedulerConfiguration
parameter_list|()
block|{
return|return
name|capScheduler
operator|.
name|getConfiguration
argument_list|()
return|;
block|}
block|}
end_class

end_unit

