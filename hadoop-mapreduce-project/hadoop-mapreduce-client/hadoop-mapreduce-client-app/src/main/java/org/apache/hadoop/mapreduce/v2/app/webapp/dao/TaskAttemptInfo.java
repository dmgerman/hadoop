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
name|java
operator|.
name|util
operator|.
name|List
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
name|XmlSeeAlso
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
literal|"taskAttempt"
argument_list|)
annotation|@
name|XmlSeeAlso
argument_list|(
block|{
name|ReduceTaskAttemptInfo
operator|.
name|class
block|}
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|TaskAttemptInfo
specifier|public
class|class
name|TaskAttemptInfo
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
DECL|field|rack
specifier|protected
name|String
name|rack
decl_stmt|;
DECL|field|state
specifier|protected
name|TaskAttemptState
name|state
decl_stmt|;
DECL|field|status
specifier|protected
name|String
name|status
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|protected
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|field|diagnostics
specifier|protected
name|String
name|diagnostics
decl_stmt|;
DECL|field|type
specifier|protected
name|String
name|type
decl_stmt|;
DECL|field|assignedContainerId
specifier|protected
name|String
name|assignedContainerId
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|assignedContainer
specifier|protected
name|ContainerId
name|assignedContainer
decl_stmt|;
DECL|method|TaskAttemptInfo ()
specifier|public
name|TaskAttemptInfo
parameter_list|()
block|{   }
DECL|method|TaskAttemptInfo (TaskAttempt ta, Boolean isRunning)
specifier|public
name|TaskAttemptInfo
parameter_list|(
name|TaskAttempt
name|ta
parameter_list|,
name|Boolean
name|isRunning
parameter_list|)
block|{
name|this
argument_list|(
name|ta
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
name|isRunning
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttemptInfo (TaskAttempt ta, TaskType type, Boolean isRunning)
specifier|public
name|TaskAttemptInfo
parameter_list|(
name|TaskAttempt
name|ta
parameter_list|,
name|TaskType
name|type
parameter_list|,
name|Boolean
name|isRunning
parameter_list|)
block|{
specifier|final
name|TaskAttemptReport
name|report
init|=
name|ta
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|this
operator|.
name|type
operator|=
name|type
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|ta
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeHttpAddress
operator|=
name|ta
operator|.
name|getNodeHttpAddress
argument_list|()
expr_stmt|;
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
name|assignedContainerId
operator|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|report
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|assignedContainer
operator|=
name|report
operator|.
name|getContainerId
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
name|status
operator|=
name|report
operator|.
name|getStateString
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|report
operator|.
name|getTaskAttemptState
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
name|isRunning
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
name|diagnostics
operator|=
name|report
operator|.
name|getDiagnosticInfo
argument_list|()
expr_stmt|;
name|this
operator|.
name|rack
operator|=
name|ta
operator|.
name|getNodeRackName
argument_list|()
expr_stmt|;
block|}
DECL|method|getAssignedContainerIdStr ()
specifier|public
name|String
name|getAssignedContainerIdStr
parameter_list|()
block|{
return|return
name|this
operator|.
name|assignedContainerId
return|;
block|}
DECL|method|getAssignedContainerId ()
specifier|public
name|ContainerId
name|getAssignedContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|assignedContainer
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
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
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
DECL|method|getNode ()
specifier|public
name|String
name|getNode
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHttpAddress
return|;
block|}
DECL|method|getRack ()
specifier|public
name|String
name|getRack
parameter_list|()
block|{
return|return
name|this
operator|.
name|rack
return|;
block|}
DECL|method|getNote ()
specifier|public
name|String
name|getNote
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
block|}
end_class

end_unit

