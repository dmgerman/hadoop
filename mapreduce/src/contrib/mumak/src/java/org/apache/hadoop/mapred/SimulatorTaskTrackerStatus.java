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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This class only exists to pass the current simulation time to the   * JobTracker in the heartbeat() call.  */
end_comment

begin_class
DECL|class|SimulatorTaskTrackerStatus
class|class
name|SimulatorTaskTrackerStatus
extends|extends
name|TaskTrackerStatus
block|{
comment|/**    * The virtual, simulation time, when the hearbeat() call transmitting     * this TaskTrackerSatus occured.     */
DECL|field|currentSimulationTime
specifier|private
specifier|final
name|long
name|currentSimulationTime
decl_stmt|;
comment|/**    * Constructs a SimulatorTaskTrackerStatus object. All parameters are     * the same as in {@link TaskTrackerStatus}. The only extra is    * @param currentSimulationTime the current time in the simulation when the     *                              heartbeat() call transmitting this     *                              TaskTrackerStatus occured.    */
DECL|method|SimulatorTaskTrackerStatus (String trackerName, String host, int httpPort, List<TaskStatus> taskReports, int failures, int maxMapTasks, int maxReduceTasks, long currentSimulationTime)
specifier|public
name|SimulatorTaskTrackerStatus
parameter_list|(
name|String
name|trackerName
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|List
argument_list|<
name|TaskStatus
argument_list|>
name|taskReports
parameter_list|,
name|int
name|failures
parameter_list|,
name|int
name|maxMapTasks
parameter_list|,
name|int
name|maxReduceTasks
parameter_list|,
name|long
name|currentSimulationTime
parameter_list|)
block|{
name|super
argument_list|(
name|trackerName
argument_list|,
name|host
argument_list|,
name|httpPort
argument_list|,
name|taskReports
argument_list|,
name|failures
argument_list|,
name|maxMapTasks
argument_list|,
name|maxReduceTasks
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentSimulationTime
operator|=
name|currentSimulationTime
expr_stmt|;
block|}
comment|/**     * Returns the current time in the simulation.    */
DECL|method|getCurrentSimulationTime ()
specifier|public
name|long
name|getCurrentSimulationTime
parameter_list|()
block|{
return|return
name|currentSimulationTime
return|;
block|}
block|}
end_class

end_unit

