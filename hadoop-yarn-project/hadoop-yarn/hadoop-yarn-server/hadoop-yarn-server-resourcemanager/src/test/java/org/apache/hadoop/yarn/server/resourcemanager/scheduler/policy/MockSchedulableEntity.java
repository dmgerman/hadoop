begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
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
name|policy
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
name|api
operator|.
name|records
operator|.
name|Priority
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
name|ResourceUsage
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
name|CommonNodeLabelsManager
import|;
end_import

begin_class
DECL|class|MockSchedulableEntity
specifier|public
class|class
name|MockSchedulableEntity
implements|implements
name|SchedulableEntity
block|{
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|serial
specifier|private
name|long
name|serial
init|=
literal|0
decl_stmt|;
DECL|field|priority
specifier|private
name|Priority
name|priority
decl_stmt|;
DECL|field|isRecovering
specifier|private
name|boolean
name|isRecovering
decl_stmt|;
DECL|field|partition
specifier|private
name|String
name|partition
init|=
literal|""
decl_stmt|;
DECL|method|MockSchedulableEntity ()
specifier|public
name|MockSchedulableEntity
parameter_list|()
block|{ }
DECL|method|MockSchedulableEntity (long serial, int priority, boolean isRecovering)
specifier|public
name|MockSchedulableEntity
parameter_list|(
name|long
name|serial
parameter_list|,
name|int
name|priority
parameter_list|,
name|boolean
name|isRecovering
parameter_list|)
block|{
name|this
operator|.
name|serial
operator|=
name|serial
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|this
operator|.
name|isRecovering
operator|=
name|isRecovering
expr_stmt|;
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setSerial (long serial)
specifier|public
name|void
name|setSerial
parameter_list|(
name|long
name|serial
parameter_list|)
block|{
name|this
operator|.
name|serial
operator|=
name|serial
expr_stmt|;
block|}
DECL|method|getSerial ()
specifier|public
name|long
name|getSerial
parameter_list|()
block|{
return|return
name|serial
return|;
block|}
DECL|method|setUsed (Resource value)
specifier|public
name|void
name|setUsed
parameter_list|(
name|Resource
name|value
parameter_list|)
block|{
name|schedulingResourceUsage
operator|.
name|setUsed
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setPending (Resource value)
specifier|public
name|void
name|setPending
parameter_list|(
name|Resource
name|value
parameter_list|)
block|{
name|schedulingResourceUsage
operator|.
name|setPending
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|field|schedulingResourceUsage
specifier|private
name|ResourceUsage
name|schedulingResourceUsage
init|=
operator|new
name|ResourceUsage
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSchedulingResourceUsage ()
specifier|public
name|ResourceUsage
name|getSchedulingResourceUsage
parameter_list|()
block|{
return|return
name|schedulingResourceUsage
return|;
block|}
annotation|@
name|Override
DECL|method|compareInputOrderTo (SchedulableEntity other)
specifier|public
name|int
name|compareInputOrderTo
parameter_list|(
name|SchedulableEntity
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|MockSchedulableEntity
condition|)
block|{
name|MockSchedulableEntity
name|r2
init|=
operator|(
name|MockSchedulableEntity
operator|)
name|other
decl_stmt|;
name|int
name|res
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|getSerial
argument_list|()
operator|-
name|r2
operator|.
name|getSerial
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|res
return|;
block|}
return|return
literal|1
return|;
comment|//let other types go before this, if any
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|setApplicationPriority (Priority priority)
specifier|public
name|void
name|setApplicationPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isRecovering ()
specifier|public
name|boolean
name|isRecovering
parameter_list|()
block|{
return|return
name|isRecovering
return|;
block|}
DECL|method|setRecovering (boolean entityRecovering)
specifier|protected
name|void
name|setRecovering
parameter_list|(
name|boolean
name|entityRecovering
parameter_list|)
block|{
name|this
operator|.
name|isRecovering
operator|=
name|entityRecovering
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPartition ()
specifier|public
name|String
name|getPartition
parameter_list|()
block|{
return|return
name|partition
return|;
block|}
DECL|method|setPartition (String partition)
specifier|public
name|void
name|setPartition
parameter_list|(
name|String
name|partition
parameter_list|)
block|{
name|this
operator|.
name|partition
operator|=
name|partition
expr_stmt|;
block|}
block|}
end_class

end_unit

