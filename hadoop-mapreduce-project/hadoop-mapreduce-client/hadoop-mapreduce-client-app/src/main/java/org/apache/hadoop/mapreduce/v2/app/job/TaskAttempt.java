begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job
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
name|job
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
import|;
end_import

begin_comment
comment|/**  * Read only view of TaskAttempt.  */
end_comment

begin_interface
DECL|interface|TaskAttempt
specifier|public
interface|interface
name|TaskAttempt
block|{
DECL|method|getID ()
name|TaskAttemptId
name|getID
parameter_list|()
function_decl|;
DECL|method|getReport ()
name|TaskAttemptReport
name|getReport
parameter_list|()
function_decl|;
DECL|method|getDiagnostics ()
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|getCounters ()
name|Counters
name|getCounters
parameter_list|()
function_decl|;
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|getState ()
name|TaskAttemptState
name|getState
parameter_list|()
function_decl|;
comment|/**     * Has attempt reached the final state or not.    * @return true if it has finished, else false    */
DECL|method|isFinished ()
name|boolean
name|isFinished
parameter_list|()
function_decl|;
comment|/**    * @return the container ID if a container is assigned, otherwise null.    */
DECL|method|getAssignedContainerID ()
name|ContainerId
name|getAssignedContainerID
parameter_list|()
function_decl|;
comment|/**    * @return container mgr address if a container is assigned, otherwise null.    */
DECL|method|getAssignedContainerMgrAddress ()
name|String
name|getAssignedContainerMgrAddress
parameter_list|()
function_decl|;
comment|/**    * @return node's http address if a container is assigned, otherwise null.    */
DECL|method|getNodeHttpAddress ()
name|String
name|getNodeHttpAddress
parameter_list|()
function_decl|;
comment|/**     * @return time at which container is launched. If container is not launched    * yet, returns 0.    */
DECL|method|getLaunchTime ()
name|long
name|getLaunchTime
parameter_list|()
function_decl|;
comment|/**     * @return attempt's finish time. If attempt is not finished    *  yet, returns 0.    */
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
comment|/**    * @return The attempt's shuffle finish time if the attempt is a reduce. If    * attempt is not finished yet, returns 0.    */
DECL|method|getShuffleFinishTime ()
name|long
name|getShuffleFinishTime
parameter_list|()
function_decl|;
comment|/**    * @return The attempt's sort or merge finish time if the attempt is a reduce.     * If attempt is not finished yet, returns 0.    */
DECL|method|getSortFinishTime ()
name|long
name|getSortFinishTime
parameter_list|()
function_decl|;
comment|/**    * @return the port shuffle is on.    */
DECL|method|getShufflePort ()
specifier|public
name|int
name|getShufflePort
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

