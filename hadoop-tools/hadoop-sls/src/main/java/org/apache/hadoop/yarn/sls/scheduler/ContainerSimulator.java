begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Delayed
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
name|TimeUnit
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
name|ContainerId
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
name|ExecutionType
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ContainerSimulator
specifier|public
class|class
name|ContainerSimulator
implements|implements
name|Delayed
block|{
comment|// id
DECL|field|id
specifier|private
name|ContainerId
name|id
decl_stmt|;
comment|// resource allocated
DECL|field|resource
specifier|private
name|Resource
name|resource
decl_stmt|;
comment|// end time
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
comment|// life time (ms)
DECL|field|lifeTime
specifier|private
name|long
name|lifeTime
decl_stmt|;
comment|// host name
DECL|field|hostname
specifier|private
name|String
name|hostname
decl_stmt|;
comment|// priority
DECL|field|priority
specifier|private
name|int
name|priority
decl_stmt|;
comment|// type
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
comment|// execution type
DECL|field|executionType
specifier|private
name|ExecutionType
name|executionType
init|=
name|ExecutionType
operator|.
name|GUARANTEED
decl_stmt|;
comment|// allocation id
DECL|field|allocationId
specifier|private
name|long
name|allocationId
decl_stmt|;
comment|/**    * invoked when AM schedules containers to allocate.    */
DECL|method|ContainerSimulator (Resource resource, long lifeTime, String hostname, int priority, String type)
specifier|public
name|ContainerSimulator
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|long
name|lifeTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|priority
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|resource
argument_list|,
name|lifeTime
argument_list|,
name|hostname
argument_list|,
name|priority
argument_list|,
name|type
argument_list|,
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|)
expr_stmt|;
block|}
comment|/**    * invoked when AM schedules containers to allocate.    */
DECL|method|ContainerSimulator (Resource resource, long lifeTime, String hostname, int priority, String type, ExecutionType executionType)
specifier|public
name|ContainerSimulator
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|long
name|lifeTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|priority
parameter_list|,
name|String
name|type
parameter_list|,
name|ExecutionType
name|executionType
parameter_list|)
block|{
name|this
argument_list|(
name|resource
argument_list|,
name|lifeTime
argument_list|,
name|hostname
argument_list|,
name|priority
argument_list|,
name|type
argument_list|,
name|executionType
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * invoked when AM schedules containers to allocate.    */
DECL|method|ContainerSimulator (Resource resource, long lifeTime, String hostname, int priority, String type, ExecutionType executionType, long allocationId)
specifier|public
name|ContainerSimulator
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|long
name|lifeTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|priority
parameter_list|,
name|String
name|type
parameter_list|,
name|ExecutionType
name|executionType
parameter_list|,
name|long
name|allocationId
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
name|this
operator|.
name|lifeTime
operator|=
name|lifeTime
expr_stmt|;
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|executionType
operator|=
name|executionType
expr_stmt|;
name|this
operator|.
name|allocationId
operator|=
name|allocationId
expr_stmt|;
block|}
comment|/**    * invoke when NM schedules containers to run.    */
DECL|method|ContainerSimulator (ContainerId id, Resource resource, long endTime, long lifeTime, long allocationId)
specifier|public
name|ContainerSimulator
parameter_list|(
name|ContainerId
name|id
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|long
name|endTime
parameter_list|,
name|long
name|lifeTime
parameter_list|,
name|long
name|allocationId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
name|this
operator|.
name|lifeTime
operator|=
name|lifeTime
expr_stmt|;
name|this
operator|.
name|allocationId
operator|=
name|allocationId
expr_stmt|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|getId ()
specifier|public
name|ContainerId
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Delayed o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Delayed
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ContainerSimulator
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Parameter must be a ContainerSimulator instance"
argument_list|)
throw|;
block|}
name|ContainerSimulator
name|other
init|=
operator|(
name|ContainerSimulator
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|endTime
operator|-
name|other
operator|.
name|endTime
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDelay (TimeUnit unit)
specifier|public
name|long
name|getDelay
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|unit
operator|.
name|convert
argument_list|(
name|endTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
DECL|method|getLifeTime ()
specifier|public
name|long
name|getLifeTime
parameter_list|()
block|{
return|return
name|lifeTime
return|;
block|}
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|hostname
return|;
block|}
DECL|method|getEndTime ()
specifier|public
name|long
name|getEndTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|setPriority (int p)
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|p
parameter_list|)
block|{
name|priority
operator|=
name|p
expr_stmt|;
block|}
DECL|method|getExecutionType ()
specifier|public
name|ExecutionType
name|getExecutionType
parameter_list|()
block|{
return|return
name|executionType
return|;
block|}
DECL|method|getAllocationId ()
specifier|public
name|long
name|getAllocationId
parameter_list|()
block|{
return|return
name|allocationId
return|;
block|}
block|}
end_class

end_unit

