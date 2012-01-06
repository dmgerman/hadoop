begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|TaskReport
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
name|TaskState
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
name|TaskType
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
name|Task
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|Times
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"task"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|TaskInfo
specifier|public
class|class
name|TaskInfo
block|{
DECL|field|startTime
specifier|protected
name|long
name|startTime
decl_stmt|;
DECL|field|finishTime
specifier|protected
name|long
name|finishTime
decl_stmt|;
DECL|field|elapsedTime
specifier|protected
name|long
name|elapsedTime
decl_stmt|;
DECL|field|progress
specifier|protected
name|float
name|progress
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|state
specifier|protected
name|TaskState
name|state
decl_stmt|;
DECL|field|type
specifier|protected
name|String
name|type
decl_stmt|;
DECL|field|successfulAttempt
specifier|protected
name|String
name|successfulAttempt
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|taskNum
name|int
name|taskNum
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|successful
name|TaskAttempt
name|successful
decl_stmt|;
DECL|method|TaskInfo ()
specifier|public
name|TaskInfo
parameter_list|()
block|{   }
DECL|method|TaskInfo (Task task)
specifier|public
name|TaskInfo
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|TaskType
name|ttype
init|=
name|task
operator|.
name|getType
argument_list|()
decl_stmt|;
name|this
operator|.
name|type
operator|=
name|ttype
operator|.
name|toString
argument_list|()
expr_stmt|;
name|TaskReport
name|report
init|=
name|task
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|this
operator|.
name|startTime
operator|=
name|report
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|report
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|elapsedTime
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
name|this
operator|.
name|startTime
argument_list|,
name|this
operator|.
name|finishTime
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|elapsedTime
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|elapsedTime
operator|=
literal|0
expr_stmt|;
block|}
name|this
operator|.
name|state
operator|=
name|report
operator|.
name|getTaskState
argument_list|()
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|report
operator|.
name|getProgress
argument_list|()
operator|*
literal|100
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskNum
operator|=
name|task
operator|.
name|getID
argument_list|()
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|successful
operator|=
name|getSuccessfulAttempt
argument_list|(
name|task
argument_list|)
expr_stmt|;
if|if
condition|(
name|successful
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|successfulAttempt
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|successful
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|successfulAttempt
operator|=
literal|""
expr_stmt|;
block|}
block|}
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|this
operator|.
name|progress
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getTaskNum ()
specifier|public
name|int
name|getTaskNum
parameter_list|()
block|{
return|return
name|this
operator|.
name|taskNum
return|;
block|}
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|finishTime
return|;
block|}
DECL|method|getElapsedTime ()
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|elapsedTime
return|;
block|}
DECL|method|getSuccessfulAttempt ()
specifier|public
name|String
name|getSuccessfulAttempt
parameter_list|()
block|{
return|return
name|this
operator|.
name|successfulAttempt
return|;
block|}
DECL|method|getSuccessful ()
specifier|public
name|TaskAttempt
name|getSuccessful
parameter_list|()
block|{
return|return
name|this
operator|.
name|successful
return|;
block|}
DECL|method|getSuccessfulAttempt (Task task)
specifier|private
name|TaskAttempt
name|getSuccessfulAttempt
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|attempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|SUCCEEDED
condition|)
block|{
return|return
name|attempt
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

