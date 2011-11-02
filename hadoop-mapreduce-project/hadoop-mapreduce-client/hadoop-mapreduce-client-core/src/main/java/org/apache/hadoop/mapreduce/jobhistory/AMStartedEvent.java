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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|util
operator|.
name|ConverterUtils
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

begin_comment
comment|/**  * Event to record start of a task attempt  *   */
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
DECL|class|AMStartedEvent
specifier|public
class|class
name|AMStartedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|AMStarted
name|datum
init|=
operator|new
name|AMStarted
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record the start of an MR AppMaster    *     * @param appAttemptId    *          the application attempt id.    * @param startTime    *          the start time of the AM.    * @param containerId    *          the containerId of the AM.    * @param nodeManagerHost    *          the node on which the AM is running.    * @param nodeManagerPort    *          the port on which the AM is running.    * @param nodeManagerHttpPort    *          the httpPort for the node running the AM.    */
DECL|method|AMStartedEvent (ApplicationAttemptId appAttemptId, long startTime, ContainerId containerId, String nodeManagerHost, int nodeManagerPort, int nodeManagerHttpPort)
specifier|public
name|AMStartedEvent
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|long
name|startTime
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|String
name|nodeManagerHost
parameter_list|,
name|int
name|nodeManagerPort
parameter_list|,
name|int
name|nodeManagerHttpPort
parameter_list|)
block|{
name|datum
operator|.
name|applicationAttemptId
operator|=
operator|new
name|Utf8
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|datum
operator|.
name|containerId
operator|=
operator|new
name|Utf8
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|nodeManagerHost
operator|=
operator|new
name|Utf8
argument_list|(
name|nodeManagerHost
argument_list|)
expr_stmt|;
name|datum
operator|.
name|nodeManagerPort
operator|=
name|nodeManagerPort
expr_stmt|;
name|datum
operator|.
name|nodeManagerHttpPort
operator|=
name|nodeManagerHttpPort
expr_stmt|;
block|}
DECL|method|AMStartedEvent ()
name|AMStartedEvent
parameter_list|()
block|{   }
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object datum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|datum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|AMStarted
operator|)
name|datum
expr_stmt|;
block|}
comment|/**    * @return the ApplicationAttemptId    */
DECL|method|getAppAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
block|{
return|return
name|ConverterUtils
operator|.
name|toApplicationAttemptId
argument_list|(
name|datum
operator|.
name|applicationAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return the start time for the MRAppMaster    */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|startTime
return|;
block|}
comment|/**    * @return the ContainerId for the MRAppMaster.    */
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|datum
operator|.
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return the node manager host.    */
DECL|method|getNodeManagerHost ()
specifier|public
name|String
name|getNodeManagerHost
parameter_list|()
block|{
return|return
name|datum
operator|.
name|nodeManagerHost
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return the node manager port.    */
DECL|method|getNodeManagerPort ()
specifier|public
name|int
name|getNodeManagerPort
parameter_list|()
block|{
return|return
name|datum
operator|.
name|nodeManagerPort
return|;
block|}
comment|/**    * @return the http port for the tracker.    */
DECL|method|getNodeManagerHttpPort ()
specifier|public
name|int
name|getNodeManagerHttpPort
parameter_list|()
block|{
return|return
name|datum
operator|.
name|nodeManagerHttpPort
return|;
block|}
comment|/** Get the attempt id */
annotation|@
name|Override
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|EventType
operator|.
name|AM_STARTED
return|;
block|}
block|}
end_class

end_unit

