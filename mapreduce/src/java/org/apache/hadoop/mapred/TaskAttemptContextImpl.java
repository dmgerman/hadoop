begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Counter
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
name|Progressable
import|;
end_import

begin_comment
comment|/**  * @deprecated Use {@link org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl}  *   instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TaskAttemptContextImpl
specifier|public
class|class
name|TaskAttemptContextImpl
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|TaskAttemptContextImpl
implements|implements
name|TaskAttemptContext
block|{
DECL|field|reporter
specifier|private
name|Reporter
name|reporter
decl_stmt|;
DECL|method|TaskAttemptContextImpl (JobConf conf, TaskAttemptID taskid)
specifier|public
name|TaskAttemptContextImpl
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|TaskAttemptID
name|taskid
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttemptContextImpl (JobConf conf, TaskAttemptID taskid, Reporter reporter)
name|TaskAttemptContextImpl
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|TaskAttemptID
name|taskid
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|)
expr_stmt|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
block|}
comment|/**    * Get the taskAttemptID.    *      * @return TaskAttemptID    */
DECL|method|getTaskAttemptID ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptID
parameter_list|()
block|{
return|return
operator|(
name|TaskAttemptID
operator|)
name|super
operator|.
name|getTaskAttemptID
argument_list|()
return|;
block|}
DECL|method|getProgressible ()
specifier|public
name|Progressable
name|getProgressible
parameter_list|()
block|{
return|return
name|reporter
return|;
block|}
DECL|method|getJobConf ()
specifier|public
name|JobConf
name|getJobConf
parameter_list|()
block|{
return|return
operator|(
name|JobConf
operator|)
name|getConfiguration
argument_list|()
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
name|reporter
operator|.
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (Enum<?> counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|counterName
parameter_list|)
block|{
return|return
name|reporter
operator|.
name|getCounter
argument_list|(
name|counterName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (String groupName, String counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|counterName
parameter_list|)
block|{
return|return
name|reporter
operator|.
name|getCounter
argument_list|(
name|groupName
argument_list|,
name|counterName
argument_list|)
return|;
block|}
comment|/**    * Report progress.    */
annotation|@
name|Override
DECL|method|progress ()
specifier|public
name|void
name|progress
parameter_list|()
block|{
name|reporter
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the current status of the task to the given string.    */
annotation|@
name|Override
DECL|method|setStatus (String status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|setStatusString
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

