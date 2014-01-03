begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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

begin_comment
comment|// Per-job counters
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|JobCounter
specifier|public
enum|enum
name|JobCounter
block|{
DECL|enumConstant|NUM_FAILED_MAPS
name|NUM_FAILED_MAPS
block|,
DECL|enumConstant|NUM_FAILED_REDUCES
name|NUM_FAILED_REDUCES
block|,
DECL|enumConstant|NUM_KILLED_MAPS
name|NUM_KILLED_MAPS
block|,
DECL|enumConstant|NUM_KILLED_REDUCES
name|NUM_KILLED_REDUCES
block|,
DECL|enumConstant|TOTAL_LAUNCHED_MAPS
name|TOTAL_LAUNCHED_MAPS
block|,
DECL|enumConstant|TOTAL_LAUNCHED_REDUCES
name|TOTAL_LAUNCHED_REDUCES
block|,
DECL|enumConstant|OTHER_LOCAL_MAPS
name|OTHER_LOCAL_MAPS
block|,
DECL|enumConstant|DATA_LOCAL_MAPS
name|DATA_LOCAL_MAPS
block|,
DECL|enumConstant|RACK_LOCAL_MAPS
name|RACK_LOCAL_MAPS
block|,
DECL|enumConstant|Deprecated
annotation|@
name|Deprecated
DECL|enumConstant|SLOTS_MILLIS_MAPS
name|SLOTS_MILLIS_MAPS
block|,
DECL|enumConstant|Deprecated
annotation|@
name|Deprecated
DECL|enumConstant|SLOTS_MILLIS_REDUCES
name|SLOTS_MILLIS_REDUCES
block|,
DECL|enumConstant|Deprecated
annotation|@
name|Deprecated
DECL|enumConstant|FALLOW_SLOTS_MILLIS_MAPS
name|FALLOW_SLOTS_MILLIS_MAPS
block|,
DECL|enumConstant|Deprecated
annotation|@
name|Deprecated
DECL|enumConstant|FALLOW_SLOTS_MILLIS_REDUCES
name|FALLOW_SLOTS_MILLIS_REDUCES
block|,
DECL|enumConstant|TOTAL_LAUNCHED_UBERTASKS
name|TOTAL_LAUNCHED_UBERTASKS
block|,
DECL|enumConstant|NUM_UBER_SUBMAPS
name|NUM_UBER_SUBMAPS
block|,
DECL|enumConstant|NUM_UBER_SUBREDUCES
name|NUM_UBER_SUBREDUCES
block|,
DECL|enumConstant|NUM_FAILED_UBERTASKS
name|NUM_FAILED_UBERTASKS
block|,
DECL|enumConstant|TASKS_REQ_PREEMPT
name|TASKS_REQ_PREEMPT
block|,
DECL|enumConstant|CHECKPOINTS
name|CHECKPOINTS
block|,
DECL|enumConstant|CHECKPOINT_BYTES
name|CHECKPOINT_BYTES
block|,
DECL|enumConstant|CHECKPOINT_TIME
name|CHECKPOINT_TIME
block|}
end_enum

end_unit

