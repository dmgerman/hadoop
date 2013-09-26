begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.jobcontrol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|jobcontrol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|mapred
operator|.
name|jobcontrol
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
name|lib
operator|.
name|jobcontrol
operator|.
name|ControlledJob
operator|.
name|State
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**   *  This class encapsulates a set of MapReduce jobs and its dependency.  *     *  It tracks the states of the jobs by placing them into different tables  *  according to their states.   *    *  This class provides APIs for the client app to add a job to the group   *  and to get the jobs in the group in different states. When a job is   *  added, an ID unique to the group is assigned to the job.   *    *  This class has a thread that submits jobs when they become ready,   *  monitors the states of the running jobs, and updates the states of jobs  *  based on the state changes of their depending jobs states. The class   *  provides APIs for suspending/resuming the thread, and   *  for stopping the thread.  *    */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|JobControl
specifier|public
class|class
name|JobControl
implements|implements
name|Runnable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JobControl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The thread can be in one of the following state
DECL|enum|ThreadState
DECL|enumConstant|RUNNING
DECL|enumConstant|SUSPENDED
DECL|enumConstant|STOPPED
DECL|enumConstant|STOPPING
DECL|enumConstant|READY
specifier|public
specifier|static
enum|enum
name|ThreadState
block|{
name|RUNNING
block|,
name|SUSPENDED
block|,
name|STOPPED
block|,
name|STOPPING
block|,
name|READY
block|}
empty_stmt|;
DECL|field|runnerState
specifier|private
name|ThreadState
name|runnerState
decl_stmt|;
comment|// the thread state
DECL|field|jobsInProgress
specifier|private
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
name|jobsInProgress
init|=
operator|new
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|successfulJobs
specifier|private
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
name|successfulJobs
init|=
operator|new
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|failedJobs
specifier|private
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
name|failedJobs
init|=
operator|new
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nextJobID
specifier|private
name|long
name|nextJobID
decl_stmt|;
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
comment|/**     * Construct a job control for a group of jobs.    * @param groupName a name identifying this group    */
DECL|method|JobControl (String groupName)
specifier|public
name|JobControl
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
name|this
operator|.
name|nextJobID
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|READY
expr_stmt|;
block|}
DECL|method|toList ( LinkedList<ControlledJob> jobs)
specifier|synchronized
specifier|private
specifier|static
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|toList
parameter_list|(
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
name|jobs
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ControlledJob
argument_list|>
name|retv
init|=
operator|new
name|ArrayList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ControlledJob
name|job
range|:
name|jobs
control|)
block|{
name|retv
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
return|return
name|retv
return|;
block|}
DECL|method|getJobsIn (State state)
specifier|synchronized
specifier|private
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getJobsIn
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
name|l
init|=
operator|new
name|LinkedList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ControlledJob
name|j
range|:
name|jobsInProgress
control|)
block|{
if|if
condition|(
name|j
operator|.
name|getJobState
argument_list|()
operator|==
name|state
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
return|;
block|}
comment|/**    * @return the jobs in the waiting state    */
DECL|method|getWaitingJobList ()
specifier|public
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getWaitingJobList
parameter_list|()
block|{
return|return
name|getJobsIn
argument_list|(
name|State
operator|.
name|WAITING
argument_list|)
return|;
block|}
comment|/**    * @return the jobs in the running state    */
DECL|method|getRunningJobList ()
specifier|public
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getRunningJobList
parameter_list|()
block|{
return|return
name|getJobsIn
argument_list|(
name|State
operator|.
name|RUNNING
argument_list|)
return|;
block|}
comment|/**    * @return the jobs in the ready state    */
DECL|method|getReadyJobsList ()
specifier|public
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getReadyJobsList
parameter_list|()
block|{
return|return
name|getJobsIn
argument_list|(
name|State
operator|.
name|READY
argument_list|)
return|;
block|}
comment|/**    * @return the jobs in the success state    */
DECL|method|getSuccessfulJobList ()
specifier|public
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getSuccessfulJobList
parameter_list|()
block|{
return|return
name|toList
argument_list|(
name|this
operator|.
name|successfulJobs
argument_list|)
return|;
block|}
DECL|method|getFailedJobList ()
specifier|public
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|getFailedJobList
parameter_list|()
block|{
return|return
name|toList
argument_list|(
name|this
operator|.
name|failedJobs
argument_list|)
return|;
block|}
DECL|method|getNextJobID ()
specifier|private
name|String
name|getNextJobID
parameter_list|()
block|{
name|nextJobID
operator|+=
literal|1
expr_stmt|;
return|return
name|this
operator|.
name|groupName
operator|+
name|this
operator|.
name|nextJobID
return|;
block|}
comment|/**    * Add a new controlled job.    * @param aJob the new controlled job    */
DECL|method|addJob (ControlledJob aJob)
specifier|synchronized
specifier|public
name|String
name|addJob
parameter_list|(
name|ControlledJob
name|aJob
parameter_list|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getNextJobID
argument_list|()
decl_stmt|;
name|aJob
operator|.
name|setJobID
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|aJob
operator|.
name|setJobState
argument_list|(
name|State
operator|.
name|WAITING
argument_list|)
expr_stmt|;
name|jobsInProgress
operator|.
name|add
argument_list|(
name|aJob
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**    * Add a new job.    * @param aJob the new job    */
DECL|method|addJob (Job aJob)
specifier|synchronized
specifier|public
name|String
name|addJob
parameter_list|(
name|Job
name|aJob
parameter_list|)
block|{
return|return
name|addJob
argument_list|(
operator|(
name|ControlledJob
operator|)
name|aJob
argument_list|)
return|;
block|}
comment|/**    * Add a collection of jobs    *     * @param jobs    */
DECL|method|addJobCollection (Collection<ControlledJob> jobs)
specifier|public
name|void
name|addJobCollection
parameter_list|(
name|Collection
argument_list|<
name|ControlledJob
argument_list|>
name|jobs
parameter_list|)
block|{
for|for
control|(
name|ControlledJob
name|job
range|:
name|jobs
control|)
block|{
name|addJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return the thread state    */
DECL|method|getThreadState ()
specifier|public
name|ThreadState
name|getThreadState
parameter_list|()
block|{
return|return
name|this
operator|.
name|runnerState
return|;
block|}
comment|/**    * set the thread state to STOPPING so that the     * thread will stop when it wakes up.    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|STOPPING
expr_stmt|;
block|}
comment|/**    * suspend the running thread    */
DECL|method|suspend ()
specifier|public
name|void
name|suspend
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|runnerState
operator|==
name|ThreadState
operator|.
name|RUNNING
condition|)
block|{
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|SUSPENDED
expr_stmt|;
block|}
block|}
comment|/**    * resume the suspended thread    */
DECL|method|resume ()
specifier|public
name|void
name|resume
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|runnerState
operator|==
name|ThreadState
operator|.
name|SUSPENDED
condition|)
block|{
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|RUNNING
expr_stmt|;
block|}
block|}
DECL|method|allFinished ()
specifier|synchronized
specifier|public
name|boolean
name|allFinished
parameter_list|()
block|{
return|return
name|jobsInProgress
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    *  The main loop for the thread.    *  The loop does the following:    *  	Check the states of the running jobs    *  	Update the states of waiting jobs    *  	Submit the jobs in ready state    */
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|isCircular
argument_list|(
name|jobsInProgress
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"job control has circular dependency"
argument_list|)
throw|;
block|}
try|try
block|{
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|RUNNING
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
while|while
condition|(
name|this
operator|.
name|runnerState
operator|==
name|ThreadState
operator|.
name|SUSPENDED
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//TODO the thread was interrupted, do something!!!
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Iterator
argument_list|<
name|ControlledJob
argument_list|>
name|it
init|=
name|jobsInProgress
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ControlledJob
name|j
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checking state of job "
operator|+
name|j
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|j
operator|.
name|checkState
argument_list|()
condition|)
block|{
case|case
name|SUCCESS
case|:
name|successfulJobs
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
case|case
name|FAILED
case|:
case|case
name|DEPENDENT_FAILED
case|:
name|failedJobs
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
case|case
name|READY
case|:
name|j
operator|.
name|submit
argument_list|()
expr_stmt|;
break|break;
case|case
name|RUNNING
case|:
case|case
name|WAITING
case|:
comment|//Do Nothing
break|break;
block|}
block|}
block|}
if|if
condition|(
name|this
operator|.
name|runnerState
operator|!=
name|ThreadState
operator|.
name|RUNNING
operator|&&
name|this
operator|.
name|runnerState
operator|!=
name|ThreadState
operator|.
name|SUSPENDED
condition|)
block|{
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//TODO the thread was interrupted, do something!!!
block|}
if|if
condition|(
name|this
operator|.
name|runnerState
operator|!=
name|ThreadState
operator|.
name|RUNNING
operator|&&
name|this
operator|.
name|runnerState
operator|!=
name|ThreadState
operator|.
name|SUSPENDED
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while trying to run jobs."
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|//Mark all jobs as failed because we got something bad.
name|failAllJobs
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|runnerState
operator|=
name|ThreadState
operator|.
name|STOPPED
expr_stmt|;
block|}
DECL|method|failAllJobs (Throwable t)
specifier|synchronized
specifier|private
name|void
name|failAllJobs
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Unexpected System Error Occured: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|ControlledJob
argument_list|>
name|it
init|=
name|jobsInProgress
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ControlledJob
name|j
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|j
operator|.
name|failJob
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while tyring to clean up "
operator|+
name|j
operator|.
name|getJobName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while tyring to clean up "
operator|+
name|j
operator|.
name|getJobName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|failedJobs
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Uses topological sorting algorithm for finding circular dependency    */
DECL|method|isCircular (final List<ControlledJob> jobList)
specifier|private
name|boolean
name|isCircular
parameter_list|(
specifier|final
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|jobList
parameter_list|)
block|{
name|boolean
name|cyclePresent
init|=
literal|false
decl_stmt|;
name|HashSet
argument_list|<
name|ControlledJob
argument_list|>
name|SourceSet
init|=
operator|new
name|HashSet
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|ControlledJob
argument_list|,
name|List
argument_list|<
name|ControlledJob
argument_list|>
argument_list|>
name|processedMap
init|=
operator|new
name|HashMap
argument_list|<
name|ControlledJob
argument_list|,
name|List
argument_list|<
name|ControlledJob
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ControlledJob
name|n
range|:
name|jobList
control|)
block|{
name|processedMap
operator|.
name|put
argument_list|(
name|n
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ControlledJob
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ControlledJob
name|n
range|:
name|jobList
control|)
block|{
if|if
condition|(
operator|!
name|hasInComingEdge
argument_list|(
name|n
argument_list|,
name|jobList
argument_list|,
name|processedMap
argument_list|)
condition|)
block|{
name|SourceSet
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
operator|!
name|SourceSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ControlledJob
name|controlledJob
init|=
name|SourceSet
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|SourceSet
operator|.
name|remove
argument_list|(
name|controlledJob
argument_list|)
expr_stmt|;
if|if
condition|(
name|controlledJob
operator|.
name|getDependentJobs
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|controlledJob
operator|.
name|getDependentJobs
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ControlledJob
name|depenControlledJob
init|=
name|controlledJob
operator|.
name|getDependentJobs
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|processedMap
operator|.
name|get
argument_list|(
name|controlledJob
argument_list|)
operator|.
name|add
argument_list|(
name|depenControlledJob
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hasInComingEdge
argument_list|(
name|controlledJob
argument_list|,
name|jobList
argument_list|,
name|processedMap
argument_list|)
condition|)
block|{
name|SourceSet
operator|.
name|add
argument_list|(
name|depenControlledJob
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
for|for
control|(
name|ControlledJob
name|controlledJob
range|:
name|jobList
control|)
block|{
if|if
condition|(
name|controlledJob
operator|.
name|getDependentJobs
argument_list|()
operator|!=
literal|null
operator|&&
name|controlledJob
operator|.
name|getDependentJobs
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|processedMap
operator|.
name|get
argument_list|(
name|controlledJob
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
name|cyclePresent
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Job control has circular dependency for the  job "
operator|+
name|controlledJob
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|cyclePresent
return|;
block|}
DECL|method|hasInComingEdge (ControlledJob controlledJob, List<ControlledJob> controlledJobList, HashMap<ControlledJob, List<ControlledJob>> processedMap)
specifier|private
name|boolean
name|hasInComingEdge
parameter_list|(
name|ControlledJob
name|controlledJob
parameter_list|,
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|controlledJobList
parameter_list|,
name|HashMap
argument_list|<
name|ControlledJob
argument_list|,
name|List
argument_list|<
name|ControlledJob
argument_list|>
argument_list|>
name|processedMap
parameter_list|)
block|{
name|boolean
name|hasIncomingEdge
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ControlledJob
name|k
range|:
name|controlledJobList
control|)
block|{
if|if
condition|(
name|k
operator|!=
name|controlledJob
operator|&&
name|k
operator|.
name|getDependentJobs
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|processedMap
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|contains
argument_list|(
name|controlledJob
argument_list|)
operator|&&
name|k
operator|.
name|getDependentJobs
argument_list|()
operator|.
name|contains
argument_list|(
name|controlledJob
argument_list|)
condition|)
block|{
name|hasIncomingEdge
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasIncomingEdge
return|;
block|}
block|}
end_class

end_unit

