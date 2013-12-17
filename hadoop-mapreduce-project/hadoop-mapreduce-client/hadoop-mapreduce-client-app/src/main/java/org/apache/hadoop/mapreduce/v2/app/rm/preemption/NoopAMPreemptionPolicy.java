begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm.preemption
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
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
name|mapred
operator|.
name|TaskAttemptID
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
name|mapred
operator|.
name|TaskID
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
name|mapreduce
operator|.
name|checkpoint
operator|.
name|TaskCheckpointID
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|PreemptionMessage
import|;
end_import

begin_comment
comment|/**  * NoOp policy that ignores all the requests for preemption.  */
end_comment

begin_class
DECL|class|NoopAMPreemptionPolicy
specifier|public
class|class
name|NoopAMPreemptionPolicy
implements|implements
name|AMPreemptionPolicy
block|{
annotation|@
name|Override
DECL|method|init (AppContext context)
specifier|public
name|void
name|init
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|preempt (Context ctxt, PreemptionMessage preemptionRequests)
specifier|public
name|void
name|preempt
parameter_list|(
name|Context
name|ctxt
parameter_list|,
name|PreemptionMessage
name|preemptionRequests
parameter_list|)
block|{
comment|// do nothing, ignore all requeusts
block|}
annotation|@
name|Override
DECL|method|handleFailedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleFailedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|isPreempted (TaskAttemptId yarnAttemptID)
specifier|public
name|boolean
name|isPreempted
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reportSuccessfulPreemption (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|reportSuccessfulPreemption
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|getCheckpointID (TaskID taskId)
specifier|public
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskID
name|taskId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setCheckpointID (TaskID taskId, TaskCheckpointID cid)
specifier|public
name|void
name|setCheckpointID
parameter_list|(
name|TaskID
name|taskId
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
DECL|method|handleCompletedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleCompletedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
comment|// ignore
block|}
block|}
end_class

end_unit

