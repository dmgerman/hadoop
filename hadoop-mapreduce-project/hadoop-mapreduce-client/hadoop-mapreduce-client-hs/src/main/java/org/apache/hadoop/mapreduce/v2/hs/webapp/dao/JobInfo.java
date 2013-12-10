begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp.dao
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
name|hs
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
name|ArrayList
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|JobACL
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
name|JobReport
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
name|TaskId
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
name|Job
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
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ConfEntryInfo
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
name|hs
operator|.
name|CompletedJob
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
operator|.
name|TaskAttemptStateUI
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"job"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|JobInfo
specifier|public
class|class
name|JobInfo
block|{
DECL|field|submitTime
specifier|protected
name|long
name|submitTime
decl_stmt|;
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
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|queue
specifier|protected
name|String
name|queue
decl_stmt|;
DECL|field|user
specifier|protected
name|String
name|user
decl_stmt|;
DECL|field|state
specifier|protected
name|String
name|state
decl_stmt|;
DECL|field|mapsTotal
specifier|protected
name|int
name|mapsTotal
decl_stmt|;
DECL|field|mapsCompleted
specifier|protected
name|int
name|mapsCompleted
decl_stmt|;
DECL|field|reducesTotal
specifier|protected
name|int
name|reducesTotal
decl_stmt|;
DECL|field|reducesCompleted
specifier|protected
name|int
name|reducesCompleted
decl_stmt|;
DECL|field|uberized
specifier|protected
name|Boolean
name|uberized
decl_stmt|;
DECL|field|diagnostics
specifier|protected
name|String
name|diagnostics
decl_stmt|;
DECL|field|avgMapTime
specifier|protected
name|Long
name|avgMapTime
decl_stmt|;
DECL|field|avgReduceTime
specifier|protected
name|Long
name|avgReduceTime
decl_stmt|;
DECL|field|avgShuffleTime
specifier|protected
name|Long
name|avgShuffleTime
decl_stmt|;
DECL|field|avgMergeTime
specifier|protected
name|Long
name|avgMergeTime
decl_stmt|;
DECL|field|failedReduceAttempts
specifier|protected
name|Integer
name|failedReduceAttempts
decl_stmt|;
DECL|field|killedReduceAttempts
specifier|protected
name|Integer
name|killedReduceAttempts
decl_stmt|;
DECL|field|successfulReduceAttempts
specifier|protected
name|Integer
name|successfulReduceAttempts
decl_stmt|;
DECL|field|failedMapAttempts
specifier|protected
name|Integer
name|failedMapAttempts
decl_stmt|;
DECL|field|killedMapAttempts
specifier|protected
name|Integer
name|killedMapAttempts
decl_stmt|;
DECL|field|successfulMapAttempts
specifier|protected
name|Integer
name|successfulMapAttempts
decl_stmt|;
DECL|field|acls
specifier|protected
name|ArrayList
argument_list|<
name|ConfEntryInfo
argument_list|>
name|acls
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|numMaps
specifier|protected
name|int
name|numMaps
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|numReduces
specifier|protected
name|int
name|numReduces
decl_stmt|;
DECL|method|JobInfo ()
specifier|public
name|JobInfo
parameter_list|()
block|{   }
DECL|method|JobInfo (Job job)
specifier|public
name|JobInfo
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|JobReport
name|report
init|=
name|job
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|this
operator|.
name|mapsTotal
operator|=
name|job
operator|.
name|getTotalMaps
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapsCompleted
operator|=
name|job
operator|.
name|getCompletedMaps
argument_list|()
expr_stmt|;
name|this
operator|.
name|reducesTotal
operator|=
name|job
operator|.
name|getTotalReduces
argument_list|()
expr_stmt|;
name|this
operator|.
name|reducesCompleted
operator|=
name|job
operator|.
name|getCompletedReduces
argument_list|()
expr_stmt|;
name|this
operator|.
name|submitTime
operator|=
name|report
operator|.
name|getSubmitTime
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
name|name
operator|=
name|job
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|job
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|job
operator|.
name|getUserName
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|job
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|acls
operator|=
operator|new
name|ArrayList
argument_list|<
name|ConfEntryInfo
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|job
operator|instanceof
name|CompletedJob
condition|)
block|{
name|avgMapTime
operator|=
literal|0l
expr_stmt|;
name|avgReduceTime
operator|=
literal|0l
expr_stmt|;
name|avgShuffleTime
operator|=
literal|0l
expr_stmt|;
name|avgMergeTime
operator|=
literal|0l
expr_stmt|;
name|failedReduceAttempts
operator|=
literal|0
expr_stmt|;
name|killedReduceAttempts
operator|=
literal|0
expr_stmt|;
name|successfulReduceAttempts
operator|=
literal|0
expr_stmt|;
name|failedMapAttempts
operator|=
literal|0
expr_stmt|;
name|killedMapAttempts
operator|=
literal|0
expr_stmt|;
name|successfulMapAttempts
operator|=
literal|0
expr_stmt|;
name|countTasksAndAttempts
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|this
operator|.
name|uberized
operator|=
name|job
operator|.
name|isUber
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
literal|""
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
name|job
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
if|if
condition|(
name|diagnostics
operator|!=
literal|null
operator|&&
operator|!
name|diagnostics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|diagnostics
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|diag
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|diagnostics
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|allacls
init|=
name|job
operator|.
name|getJobACLs
argument_list|()
decl_stmt|;
if|if
condition|(
name|allacls
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|entry
range|:
name|allacls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|this
operator|.
name|acls
operator|.
name|add
argument_list|(
operator|new
name|ConfEntryInfo
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getAclName
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAclString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getNumMaps ()
specifier|public
name|long
name|getNumMaps
parameter_list|()
block|{
return|return
name|numMaps
return|;
block|}
DECL|method|getNumReduces ()
specifier|public
name|long
name|getNumReduces
parameter_list|()
block|{
return|return
name|numReduces
return|;
block|}
DECL|method|getAvgMapTime ()
specifier|public
name|Long
name|getAvgMapTime
parameter_list|()
block|{
return|return
name|avgMapTime
return|;
block|}
DECL|method|getAvgReduceTime ()
specifier|public
name|Long
name|getAvgReduceTime
parameter_list|()
block|{
return|return
name|avgReduceTime
return|;
block|}
DECL|method|getAvgShuffleTime ()
specifier|public
name|Long
name|getAvgShuffleTime
parameter_list|()
block|{
return|return
name|avgShuffleTime
return|;
block|}
DECL|method|getAvgMergeTime ()
specifier|public
name|Long
name|getAvgMergeTime
parameter_list|()
block|{
return|return
name|avgMergeTime
return|;
block|}
DECL|method|getFailedReduceAttempts ()
specifier|public
name|Integer
name|getFailedReduceAttempts
parameter_list|()
block|{
return|return
name|failedReduceAttempts
return|;
block|}
DECL|method|getKilledReduceAttempts ()
specifier|public
name|Integer
name|getKilledReduceAttempts
parameter_list|()
block|{
return|return
name|killedReduceAttempts
return|;
block|}
DECL|method|getSuccessfulReduceAttempts ()
specifier|public
name|Integer
name|getSuccessfulReduceAttempts
parameter_list|()
block|{
return|return
name|successfulReduceAttempts
return|;
block|}
DECL|method|getFailedMapAttempts ()
specifier|public
name|Integer
name|getFailedMapAttempts
parameter_list|()
block|{
return|return
name|failedMapAttempts
return|;
block|}
DECL|method|getKilledMapAttempts ()
specifier|public
name|Integer
name|getKilledMapAttempts
parameter_list|()
block|{
return|return
name|killedMapAttempts
return|;
block|}
DECL|method|getSuccessfulMapAttempts ()
specifier|public
name|Integer
name|getSuccessfulMapAttempts
parameter_list|()
block|{
return|return
name|successfulMapAttempts
return|;
block|}
DECL|method|getAcls ()
specifier|public
name|ArrayList
argument_list|<
name|ConfEntryInfo
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
DECL|method|getReducesCompleted ()
specifier|public
name|int
name|getReducesCompleted
parameter_list|()
block|{
return|return
name|this
operator|.
name|reducesCompleted
return|;
block|}
DECL|method|getReducesTotal ()
specifier|public
name|int
name|getReducesTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|reducesTotal
return|;
block|}
DECL|method|getMapsCompleted ()
specifier|public
name|int
name|getMapsCompleted
parameter_list|()
block|{
return|return
name|this
operator|.
name|mapsCompleted
return|;
block|}
DECL|method|getMapsTotal ()
specifier|public
name|int
name|getMapsTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|mapsTotal
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
return|;
block|}
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queue
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
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|submitTime
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
DECL|method|isUber ()
specifier|public
name|Boolean
name|isUber
parameter_list|()
block|{
return|return
name|this
operator|.
name|uberized
return|;
block|}
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
comment|/**    * Go through a job and update the member variables with counts for    * information to output in the page.    *    * @param job    *          the job to get counts for.    */
DECL|method|countTasksAndAttempts (Job job)
specifier|private
name|void
name|countTasksAndAttempts
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|numReduces
operator|=
literal|0
expr_stmt|;
name|numMaps
operator|=
literal|0
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
if|if
condition|(
name|tasks
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Task
name|task
range|:
name|tasks
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Attempts counts
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|task
operator|.
name|getAttempts
argument_list|()
decl_stmt|;
name|int
name|successful
decl_stmt|,
name|failed
decl_stmt|,
name|killed
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|attempts
operator|.
name|values
argument_list|()
control|)
block|{
name|successful
operator|=
literal|0
expr_stmt|;
name|failed
operator|=
literal|0
expr_stmt|;
name|killed
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|NEW
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
comment|// Do Nothing
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|RUNNING
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
comment|// Do Nothing
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|successful
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|failed
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|killed
expr_stmt|;
block|}
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|successfulMapAttempts
operator|+=
name|successful
expr_stmt|;
name|failedMapAttempts
operator|+=
name|failed
expr_stmt|;
name|killedMapAttempts
operator|+=
name|killed
expr_stmt|;
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
name|numMaps
operator|++
expr_stmt|;
name|avgMapTime
operator|+=
operator|(
name|attempt
operator|.
name|getFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getLaunchTime
argument_list|()
operator|)
expr_stmt|;
block|}
break|break;
case|case
name|REDUCE
case|:
name|successfulReduceAttempts
operator|+=
name|successful
expr_stmt|;
name|failedReduceAttempts
operator|+=
name|failed
expr_stmt|;
name|killedReduceAttempts
operator|+=
name|killed
expr_stmt|;
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
name|numReduces
operator|++
expr_stmt|;
name|avgShuffleTime
operator|+=
operator|(
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getLaunchTime
argument_list|()
operator|)
expr_stmt|;
name|avgMergeTime
operator|+=
name|attempt
operator|.
name|getSortFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
expr_stmt|;
name|avgReduceTime
operator|+=
operator|(
name|attempt
operator|.
name|getFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
operator|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
if|if
condition|(
name|numMaps
operator|>
literal|0
condition|)
block|{
name|avgMapTime
operator|=
name|avgMapTime
operator|/
name|numMaps
expr_stmt|;
block|}
if|if
condition|(
name|numReduces
operator|>
literal|0
condition|)
block|{
name|avgReduceTime
operator|=
name|avgReduceTime
operator|/
name|numReduces
expr_stmt|;
name|avgShuffleTime
operator|=
name|avgShuffleTime
operator|/
name|numReduces
expr_stmt|;
name|avgMergeTime
operator|=
name|avgMergeTime
operator|/
name|numReduces
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

