begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_class
DECL|class|JobTrackerInstrumentation
class|class
name|JobTrackerInstrumentation
block|{
DECL|field|tracker
specifier|protected
specifier|final
name|JobTracker
name|tracker
decl_stmt|;
DECL|method|JobTrackerInstrumentation (JobTracker jt, JobConf conf)
specifier|public
name|JobTrackerInstrumentation
parameter_list|(
name|JobTracker
name|jt
parameter_list|,
name|JobConf
name|conf
parameter_list|)
block|{
name|tracker
operator|=
name|jt
expr_stmt|;
block|}
DECL|method|launchMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|launchMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|completeMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|completeMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|failedMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|failedMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|launchReduce (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|launchReduce
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|completeReduce (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|completeReduce
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|failedReduce (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|failedReduce
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|submitJob (JobConf conf, JobID id)
specifier|public
name|void
name|submitJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|completeJob (JobConf conf, JobID id)
specifier|public
name|void
name|completeJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|terminateJob (JobConf conf, JobID id)
specifier|public
name|void
name|terminateJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|finalizeJob (JobConf conf, JobID id)
specifier|public
name|void
name|finalizeJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|addWaitingMaps (JobID id, int task)
specifier|public
name|void
name|addWaitingMaps
parameter_list|(
name|JobID
name|id
parameter_list|,
name|int
name|task
parameter_list|)
block|{ }
DECL|method|decWaitingMaps (JobID id, int task)
specifier|public
name|void
name|decWaitingMaps
parameter_list|(
name|JobID
name|id
parameter_list|,
name|int
name|task
parameter_list|)
block|{ }
DECL|method|addWaitingReduces (JobID id, int task)
specifier|public
name|void
name|addWaitingReduces
parameter_list|(
name|JobID
name|id
parameter_list|,
name|int
name|task
parameter_list|)
block|{ }
DECL|method|decWaitingReduces (JobID id, int task)
specifier|public
name|void
name|decWaitingReduces
parameter_list|(
name|JobID
name|id
parameter_list|,
name|int
name|task
parameter_list|)
block|{ }
DECL|method|setMapSlots (int slots)
specifier|public
name|void
name|setMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|setReduceSlots (int slots)
specifier|public
name|void
name|setReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addBlackListedMapSlots (int slots)
specifier|public
name|void
name|addBlackListedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decBlackListedMapSlots (int slots)
specifier|public
name|void
name|decBlackListedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addBlackListedReduceSlots (int slots)
specifier|public
name|void
name|addBlackListedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decBlackListedReduceSlots (int slots)
specifier|public
name|void
name|decBlackListedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addReservedMapSlots (int slots)
specifier|public
name|void
name|addReservedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decReservedMapSlots (int slots)
specifier|public
name|void
name|decReservedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addReservedReduceSlots (int slots)
specifier|public
name|void
name|addReservedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decReservedReduceSlots (int slots)
specifier|public
name|void
name|decReservedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addOccupiedMapSlots (int slots)
specifier|public
name|void
name|addOccupiedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decOccupiedMapSlots (int slots)
specifier|public
name|void
name|decOccupiedMapSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|addOccupiedReduceSlots (int slots)
specifier|public
name|void
name|addOccupiedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|decOccupiedReduceSlots (int slots)
specifier|public
name|void
name|decOccupiedReduceSlots
parameter_list|(
name|int
name|slots
parameter_list|)
block|{ }
DECL|method|failedJob (JobConf conf, JobID id)
specifier|public
name|void
name|failedJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|killedJob (JobConf conf, JobID id)
specifier|public
name|void
name|killedJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|addPrepJob (JobConf conf, JobID id)
specifier|public
name|void
name|addPrepJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|decPrepJob (JobConf conf, JobID id)
specifier|public
name|void
name|decPrepJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|addRunningJob (JobConf conf, JobID id)
specifier|public
name|void
name|addRunningJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|decRunningJob (JobConf conf, JobID id)
specifier|public
name|void
name|decRunningJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobID
name|id
parameter_list|)
block|{ }
DECL|method|addRunningMaps (int tasks)
specifier|public
name|void
name|addRunningMaps
parameter_list|(
name|int
name|tasks
parameter_list|)
block|{ }
DECL|method|decRunningMaps (int tasks)
specifier|public
name|void
name|decRunningMaps
parameter_list|(
name|int
name|tasks
parameter_list|)
block|{ }
DECL|method|addRunningReduces (int tasks)
specifier|public
name|void
name|addRunningReduces
parameter_list|(
name|int
name|tasks
parameter_list|)
block|{ }
DECL|method|decRunningReduces (int tasks)
specifier|public
name|void
name|decRunningReduces
parameter_list|(
name|int
name|tasks
parameter_list|)
block|{ }
DECL|method|killedMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|killedMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|killedReduce (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|killedReduce
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|addTrackers (int trackers)
specifier|public
name|void
name|addTrackers
parameter_list|(
name|int
name|trackers
parameter_list|)
block|{ }
DECL|method|decTrackers (int trackers)
specifier|public
name|void
name|decTrackers
parameter_list|(
name|int
name|trackers
parameter_list|)
block|{ }
DECL|method|addBlackListedTrackers (int trackers)
specifier|public
name|void
name|addBlackListedTrackers
parameter_list|(
name|int
name|trackers
parameter_list|)
block|{ }
DECL|method|decBlackListedTrackers (int trackers)
specifier|public
name|void
name|decBlackListedTrackers
parameter_list|(
name|int
name|trackers
parameter_list|)
block|{ }
DECL|method|setDecommissionedTrackers (int trackers)
specifier|public
name|void
name|setDecommissionedTrackers
parameter_list|(
name|int
name|trackers
parameter_list|)
block|{ }
DECL|method|heartbeat ()
specifier|public
name|void
name|heartbeat
parameter_list|()
block|{   }
DECL|method|speculateMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|speculateMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|speculateReduce (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|speculateReduce
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|launchDataLocalMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|launchDataLocalMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
DECL|method|launchRackLocalMap (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|launchRackLocalMap
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
block|{ }
block|}
end_class

end_unit

