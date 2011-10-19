begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
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
name|hs
package|;
end_package

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
name|List
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
name|TypeConverter
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|TaskAttemptInfo
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
name|Counters
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
name|api
operator|.
name|records
operator|.
name|TaskAttemptReport
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
name|TaskAttemptState
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
name|TaskId
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
name|job
operator|.
name|TaskAttempt
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
import|;
end_import

begin_class
DECL|class|CompletedTaskAttempt
specifier|public
class|class
name|CompletedTaskAttempt
implements|implements
name|TaskAttempt
block|{
DECL|field|attemptInfo
specifier|private
specifier|final
name|TaskAttemptInfo
name|attemptInfo
decl_stmt|;
DECL|field|attemptId
specifier|private
specifier|final
name|TaskAttemptId
name|attemptId
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|TaskAttemptState
name|state
decl_stmt|;
DECL|field|report
specifier|private
specifier|final
name|TaskAttemptReport
name|report
decl_stmt|;
DECL|field|diagnostics
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|localDiagMessage
specifier|private
name|String
name|localDiagMessage
decl_stmt|;
DECL|method|CompletedTaskAttempt (TaskId taskId, TaskAttemptInfo attemptInfo)
name|CompletedTaskAttempt
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskAttemptInfo
name|attemptInfo
parameter_list|)
block|{
name|this
operator|.
name|attemptInfo
operator|=
name|attemptInfo
expr_stmt|;
name|this
operator|.
name|attemptId
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|attemptInfo
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attemptInfo
operator|.
name|getCounters
argument_list|()
operator|!=
literal|null
condition|)
name|this
operator|.
name|counters
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|attemptInfo
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attemptInfo
operator|.
name|getTaskStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|TaskAttemptState
operator|.
name|valueOf
argument_list|(
name|attemptInfo
operator|.
name|getTaskStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|state
operator|=
name|TaskAttemptState
operator|.
name|KILLED
expr_stmt|;
name|localDiagMessage
operator|=
literal|"Attmpt state missing from History : marked as KILLED"
expr_stmt|;
name|diagnostics
operator|.
name|add
argument_list|(
name|localDiagMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attemptInfo
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|diagnostics
operator|.
name|add
argument_list|(
name|attemptInfo
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|report
operator|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|TaskAttemptReport
operator|.
name|class
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTaskAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTaskAttemptState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|report
operator|.
name|setProgress
argument_list|(
name|getProgress
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setStartTime
argument_list|(
name|attemptInfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinishTime
argument_list|(
name|attemptInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setShuffleFinishTime
argument_list|(
name|attemptInfo
operator|.
name|getShuffleFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setSortFinishTime
argument_list|(
name|attemptInfo
operator|.
name|getSortFinishTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|localDiagMessage
operator|!=
literal|null
condition|)
block|{
name|report
operator|.
name|setDiagnosticInfo
argument_list|(
name|attemptInfo
operator|.
name|getError
argument_list|()
operator|+
literal|", "
operator|+
name|localDiagMessage
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|report
operator|.
name|setDiagnosticInfo
argument_list|(
name|attemptInfo
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//    report.setPhase(attemptInfo.get); //TODO
name|report
operator|.
name|setStateString
argument_list|(
name|attemptInfo
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCounters
argument_list|(
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAssignedContainerID ()
specifier|public
name|ContainerId
name|getAssignedContainerID
parameter_list|()
block|{
return|return
name|attemptInfo
operator|.
name|getContainerId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAssignedContainerMgrAddress ()
specifier|public
name|String
name|getAssignedContainerMgrAddress
parameter_list|()
block|{
return|return
name|attemptInfo
operator|.
name|getHostname
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
return|return
name|attemptInfo
operator|.
name|getTrackerName
argument_list|()
operator|+
literal|":"
operator|+
name|attemptInfo
operator|.
name|getHttpPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
annotation|@
name|Override
DECL|method|getID ()
specifier|public
name|TaskAttemptId
name|getID
parameter_list|()
block|{
return|return
name|attemptId
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|public
name|TaskAttemptReport
name|getReport
parameter_list|()
block|{
return|return
name|report
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|TaskAttemptState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|isFinished ()
specifier|public
name|boolean
name|isFinished
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
annotation|@
name|Override
DECL|method|getLaunchTime ()
specifier|public
name|long
name|getLaunchTime
parameter_list|()
block|{
return|return
name|report
operator|.
name|getStartTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|report
operator|.
name|getFinishTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getShuffleFinishTime ()
specifier|public
name|long
name|getShuffleFinishTime
parameter_list|()
block|{
return|return
name|report
operator|.
name|getShuffleFinishTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSortFinishTime ()
specifier|public
name|long
name|getSortFinishTime
parameter_list|()
block|{
return|return
name|report
operator|.
name|getSortFinishTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getShufflePort ()
specifier|public
name|int
name|getShufflePort
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

