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
name|mapreduce
operator|.
name|TaskType
import|;
end_import

begin_comment
comment|/**  * Event to record the normalized map/reduce requirements.  *   */
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
DECL|class|NormalizedResourceEvent
specifier|public
class|class
name|NormalizedResourceEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|memory
specifier|private
name|int
name|memory
decl_stmt|;
DECL|field|taskType
specifier|private
name|TaskType
name|taskType
decl_stmt|;
comment|/**    * Normalized request when sent to the Resource Manager.    * @param taskType the tasktype of the request.    * @param memory the normalized memory requirements.    */
DECL|method|NormalizedResourceEvent (TaskType taskType, int memory)
specifier|public
name|NormalizedResourceEvent
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|int
name|memory
parameter_list|)
block|{
name|this
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
name|this
operator|.
name|taskType
operator|=
name|taskType
expr_stmt|;
block|}
comment|/**    * the tasktype for the event.    * @return the tasktype for the event.    */
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
return|return
name|this
operator|.
name|taskType
return|;
block|}
comment|/**    * the normalized memory    * @return the normalized memory    */
DECL|method|getMemory ()
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
name|this
operator|.
name|memory
return|;
block|}
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
name|NORMALIZED_RESOURCE
return|;
block|}
annotation|@
name|Override
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not a seriable object"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setDatum (Object datum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|datum
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not a seriable object"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

