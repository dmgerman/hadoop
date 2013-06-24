begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies
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
operator|.
name|policies
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Comparator
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|Schedulable
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
name|fair
operator|.
name|SchedulingPolicy
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
name|Resources
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FifoPolicy
specifier|public
class|class
name|FifoPolicy
extends|extends
name|SchedulingPolicy
block|{
annotation|@
name|VisibleForTesting
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"FIFO"
decl_stmt|;
DECL|field|comparator
specifier|private
name|FifoComparator
name|comparator
init|=
operator|new
name|FifoComparator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|/**    * Compare Schedulables in order of priority and then submission time, as in    * the default FIFO scheduler in Hadoop.    */
DECL|class|FifoComparator
specifier|static
class|class
name|FifoComparator
implements|implements
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5905036205491177060L
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (Schedulable s1, Schedulable s2)
specifier|public
name|int
name|compare
parameter_list|(
name|Schedulable
name|s1
parameter_list|,
name|Schedulable
name|s2
parameter_list|)
block|{
name|int
name|res
init|=
name|s1
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|s1
operator|.
name|getStartTime
argument_list|()
operator|-
name|s2
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
comment|// In the rare case where jobs were submitted at the exact same time,
comment|// compare them by name (which will be the JobID) to get a deterministic
comment|// ordering, so we don't alternately launch tasks from different jobs.
name|res
operator|=
name|s1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getComparator ()
specifier|public
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
annotation|@
name|Override
DECL|method|computeShares (Collection<? extends Schedulable> schedulables, Resource totalResources)
specifier|public
name|void
name|computeShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|,
name|Resource
name|totalResources
parameter_list|)
block|{
name|Schedulable
name|earliest
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Schedulable
name|schedulable
range|:
name|schedulables
control|)
block|{
if|if
condition|(
name|earliest
operator|==
literal|null
operator|||
name|schedulable
operator|.
name|getStartTime
argument_list|()
operator|<
name|earliest
operator|.
name|getStartTime
argument_list|()
condition|)
block|{
name|earliest
operator|=
name|schedulable
expr_stmt|;
block|}
block|}
name|earliest
operator|.
name|setFairShare
argument_list|(
name|Resources
operator|.
name|clone
argument_list|(
name|totalResources
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicableDepth ()
specifier|public
name|byte
name|getApplicableDepth
parameter_list|()
block|{
return|return
name|SchedulingPolicy
operator|.
name|DEPTH_LEAF
return|;
block|}
block|}
end_class

end_unit

