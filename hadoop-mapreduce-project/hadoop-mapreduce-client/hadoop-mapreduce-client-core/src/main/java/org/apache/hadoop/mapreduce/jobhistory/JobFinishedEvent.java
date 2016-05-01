begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
package|;
end_package

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
name|avro
operator|.
name|util
operator|.
name|Utf8
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
name|JobID
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
name|util
operator|.
name|JobHistoryEventUtils
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
name|util
operator|.
name|StringUtils
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
name|timelineservice
operator|.
name|TimelineEvent
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
name|timelineservice
operator|.
name|TimelineMetric
import|;
end_import

begin_comment
comment|/**  * Event to record successful completion of job  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|JobFinishedEvent
specifier|public
class|class
name|JobFinishedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|JobFinished
name|datum
init|=
literal|null
decl_stmt|;
DECL|field|jobId
specifier|private
name|JobID
name|jobId
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|finishedMaps
specifier|private
name|int
name|finishedMaps
decl_stmt|;
DECL|field|finishedReduces
specifier|private
name|int
name|finishedReduces
decl_stmt|;
DECL|field|failedMaps
specifier|private
name|int
name|failedMaps
decl_stmt|;
DECL|field|failedReduces
specifier|private
name|int
name|failedReduces
decl_stmt|;
DECL|field|mapCounters
specifier|private
name|Counters
name|mapCounters
decl_stmt|;
DECL|field|reduceCounters
specifier|private
name|Counters
name|reduceCounters
decl_stmt|;
DECL|field|totalCounters
specifier|private
name|Counters
name|totalCounters
decl_stmt|;
comment|/**     * Create an event to record successful job completion    * @param id Job ID    * @param finishTime Finish time of the job    * @param finishedMaps The number of finished maps    * @param finishedReduces The number of finished reduces    * @param failedMaps The number of failed maps    * @param failedReduces The number of failed reduces    * @param mapCounters Map Counters for the job    * @param reduceCounters Reduce Counters for the job    * @param totalCounters Total Counters for the job    */
DECL|method|JobFinishedEvent (JobID id, long finishTime, int finishedMaps, int finishedReduces, int failedMaps, int failedReduces, Counters mapCounters, Counters reduceCounters, Counters totalCounters)
specifier|public
name|JobFinishedEvent
parameter_list|(
name|JobID
name|id
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|int
name|finishedMaps
parameter_list|,
name|int
name|finishedReduces
parameter_list|,
name|int
name|failedMaps
parameter_list|,
name|int
name|failedReduces
parameter_list|,
name|Counters
name|mapCounters
parameter_list|,
name|Counters
name|reduceCounters
parameter_list|,
name|Counters
name|totalCounters
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
name|this
operator|.
name|finishedMaps
operator|=
name|finishedMaps
expr_stmt|;
name|this
operator|.
name|finishedReduces
operator|=
name|finishedReduces
expr_stmt|;
name|this
operator|.
name|failedMaps
operator|=
name|failedMaps
expr_stmt|;
name|this
operator|.
name|failedReduces
operator|=
name|failedReduces
expr_stmt|;
name|this
operator|.
name|mapCounters
operator|=
name|mapCounters
expr_stmt|;
name|this
operator|.
name|reduceCounters
operator|=
name|reduceCounters
expr_stmt|;
name|this
operator|.
name|totalCounters
operator|=
name|totalCounters
expr_stmt|;
block|}
DECL|method|JobFinishedEvent ()
name|JobFinishedEvent
parameter_list|()
block|{}
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
if|if
condition|(
name|datum
operator|==
literal|null
condition|)
block|{
name|datum
operator|=
operator|new
name|JobFinished
argument_list|()
expr_stmt|;
name|datum
operator|.
name|setJobid
argument_list|(
operator|new
name|Utf8
argument_list|(
name|jobId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFinishedMaps
argument_list|(
name|finishedMaps
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFinishedReduces
argument_list|(
name|finishedReduces
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFailedMaps
argument_list|(
name|failedMaps
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFailedReduces
argument_list|(
name|failedReduces
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setMapCounters
argument_list|(
name|EventWriter
operator|.
name|toAvro
argument_list|(
name|mapCounters
argument_list|,
literal|"MAP_COUNTERS"
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setReduceCounters
argument_list|(
name|EventWriter
operator|.
name|toAvro
argument_list|(
name|reduceCounters
argument_list|,
literal|"REDUCE_COUNTERS"
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setTotalCounters
argument_list|(
name|EventWriter
operator|.
name|toAvro
argument_list|(
name|totalCounters
argument_list|,
literal|"TOTAL_COUNTERS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object oDatum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|oDatum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|JobFinished
operator|)
name|oDatum
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|JobID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|getJobid
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|datum
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishedMaps
operator|=
name|datum
operator|.
name|getFinishedMaps
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishedReduces
operator|=
name|datum
operator|.
name|getFinishedReduces
argument_list|()
expr_stmt|;
name|this
operator|.
name|failedMaps
operator|=
name|datum
operator|.
name|getFailedMaps
argument_list|()
expr_stmt|;
name|this
operator|.
name|failedReduces
operator|=
name|datum
operator|.
name|getFailedReduces
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapCounters
operator|=
name|EventReader
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getMapCounters
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reduceCounters
operator|=
name|EventReader
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getReduceCounters
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalCounters
operator|=
name|EventReader
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getTotalCounters
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|EventType
operator|.
name|JOB_FINISHED
return|;
block|}
comment|/** Get the Job ID */
DECL|method|getJobid ()
specifier|public
name|JobID
name|getJobid
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
comment|/** Get the job finish time */
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|finishTime
return|;
block|}
comment|/** Get the number of finished maps for the job */
DECL|method|getFinishedMaps ()
specifier|public
name|int
name|getFinishedMaps
parameter_list|()
block|{
return|return
name|finishedMaps
return|;
block|}
comment|/** Get the number of finished reducers for the job */
DECL|method|getFinishedReduces ()
specifier|public
name|int
name|getFinishedReduces
parameter_list|()
block|{
return|return
name|finishedReduces
return|;
block|}
comment|/** Get the number of failed maps for the job */
DECL|method|getFailedMaps ()
specifier|public
name|int
name|getFailedMaps
parameter_list|()
block|{
return|return
name|failedMaps
return|;
block|}
comment|/** Get the number of failed reducers for the job */
DECL|method|getFailedReduces ()
specifier|public
name|int
name|getFailedReduces
parameter_list|()
block|{
return|return
name|failedReduces
return|;
block|}
comment|/** Get the counters for the job */
DECL|method|getTotalCounters ()
specifier|public
name|Counters
name|getTotalCounters
parameter_list|()
block|{
return|return
name|totalCounters
return|;
block|}
comment|/** Get the Map counters for the job */
DECL|method|getMapCounters ()
specifier|public
name|Counters
name|getMapCounters
parameter_list|()
block|{
return|return
name|mapCounters
return|;
block|}
comment|/** Get the reduce counters for the job */
DECL|method|getReduceCounters ()
specifier|public
name|Counters
name|getReduceCounters
parameter_list|()
block|{
return|return
name|reduceCounters
return|;
block|}
annotation|@
name|Override
DECL|method|toTimelineEvent ()
specifier|public
name|TimelineEvent
name|toTimelineEvent
parameter_list|()
block|{
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setId
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|getEventType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FINISH_TIME"
argument_list|,
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"NUM_MAPS"
argument_list|,
name|getFinishedMaps
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"NUM_REDUCES"
argument_list|,
name|getFinishedReduces
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FAILED_MAPS"
argument_list|,
name|getFailedMaps
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FAILED_REDUCES"
argument_list|,
name|getFailedReduces
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FINISHED_MAPS"
argument_list|,
name|getFinishedMaps
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FINISHED_REDUCES"
argument_list|,
name|getFinishedReduces
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO replace SUCCEEDED with JobState.SUCCEEDED.toString()
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"JOB_STATUS"
argument_list|,
literal|"SUCCEEDED"
argument_list|)
expr_stmt|;
return|return
name|tEvent
return|;
block|}
annotation|@
name|Override
DECL|method|getTimelineMetrics ()
specifier|public
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|getTimelineMetrics
parameter_list|()
block|{
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|jobMetrics
init|=
name|JobHistoryEventUtils
operator|.
name|countersToTimelineMetric
argument_list|(
name|getMapCounters
argument_list|()
argument_list|,
name|finishTime
argument_list|)
decl_stmt|;
name|jobMetrics
operator|.
name|addAll
argument_list|(
name|JobHistoryEventUtils
operator|.
name|countersToTimelineMetric
argument_list|(
name|getReduceCounters
argument_list|()
argument_list|,
name|finishTime
argument_list|)
argument_list|)
expr_stmt|;
name|jobMetrics
operator|.
name|addAll
argument_list|(
name|JobHistoryEventUtils
operator|.
name|countersToTimelineMetric
argument_list|(
name|getTotalCounters
argument_list|()
argument_list|,
name|finishTime
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jobMetrics
return|;
block|}
block|}
end_class

end_unit

