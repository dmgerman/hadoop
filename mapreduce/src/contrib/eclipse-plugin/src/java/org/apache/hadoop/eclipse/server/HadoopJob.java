begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|FileUtil
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
name|fs
operator|.
name|Path
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
name|mapred
operator|.
name|JobConf
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
name|JobID
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
name|JobStatus
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
name|RunningJob
import|;
end_import

begin_comment
comment|/**  * Representation of a Map/Reduce running job on a given location  */
end_comment

begin_class
DECL|class|HadoopJob
specifier|public
class|class
name|HadoopJob
block|{
comment|/**    * Enum representation of a Job state    */
DECL|enum|JobState
specifier|public
enum|enum
name|JobState
block|{
DECL|enumConstant|PREPARE
DECL|enumConstant|RUNNING
DECL|enumConstant|FAILED
name|PREPARE
parameter_list|(
name|JobStatus
operator|.
name|PREP
parameter_list|)
operator|,
constructor|RUNNING(JobStatus.RUNNING
block|)
enum|,
name|FAILED
parameter_list|(
DECL|enumConstant|SUCCEEDED
name|JobStatus
operator|.
name|FAILED
parameter_list|)
operator|,
constructor|SUCCEEDED(JobStatus.SUCCEEDED
block|)
class|;
end_class

begin_decl_stmt
DECL|field|state
specifier|final
name|int
name|state
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|JobState (int state)
name|JobState
argument_list|(
name|int
name|state
argument_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
block|;     }
DECL|method|ofInt (int state)
specifier|static
name|JobState
name|ofInt
argument_list|(
name|int
name|state
argument_list|)
block|{
if|if
condition|(
name|state
operator|==
name|JobStatus
operator|.
name|PREP
condition|)
block|{
return|return
name|PREPARE
return|;
block|}
end_expr_stmt

begin_elseif
elseif|else
if|if
condition|(
name|state
operator|==
name|JobStatus
operator|.
name|RUNNING
condition|)
block|{
return|return
name|RUNNING
return|;
block|}
end_elseif

begin_elseif
elseif|else
if|if
condition|(
name|state
operator|==
name|JobStatus
operator|.
name|FAILED
condition|)
block|{
return|return
name|FAILED
return|;
block|}
end_elseif

begin_elseif
elseif|else
if|if
condition|(
name|state
operator|==
name|JobStatus
operator|.
name|SUCCEEDED
condition|)
block|{
return|return
name|SUCCEEDED
return|;
block|}
end_elseif

begin_else
else|else
block|{
return|return
literal|null
return|;
block|}
end_else

begin_comment
unit|}   }
comment|/**    * Location this Job runs on    */
end_comment

begin_decl_stmt
DECL|field|location
specifier|private
specifier|final
name|HadoopServer
name|location
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Unique identifier of this Job    */
end_comment

begin_decl_stmt
DECL|field|jobId
specifier|final
name|JobID
name|jobId
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Status representation of a running job. This actually contains a    * reference to a JobClient. Its methods might block.    */
end_comment

begin_decl_stmt
DECL|field|running
name|RunningJob
name|running
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Last polled status    *     * @deprecated should apparently not be used    */
end_comment

begin_decl_stmt
DECL|field|status
name|JobStatus
name|status
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Last polled counters    */
end_comment

begin_decl_stmt
DECL|field|counters
name|Counters
name|counters
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Job Configuration    */
end_comment

begin_decl_stmt
DECL|field|jobConf
name|JobConf
name|jobConf
init|=
literal|null
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|completed
name|boolean
name|completed
init|=
literal|false
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|successful
name|boolean
name|successful
init|=
literal|false
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|killed
name|boolean
name|killed
init|=
literal|false
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|totalMaps
name|int
name|totalMaps
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|totalReduces
name|int
name|totalReduces
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|completedMaps
name|int
name|completedMaps
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|completedReduces
name|int
name|completedReduces
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|mapProgress
name|float
name|mapProgress
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|reduceProgress
name|float
name|reduceProgress
decl_stmt|;
end_decl_stmt

begin_comment
comment|/**    * Constructor for a Hadoop job representation    *     * @param location    * @param id    * @param running    * @param status    */
end_comment

begin_constructor
DECL|method|HadoopJob (HadoopServer location, JobID id, RunningJob running, JobStatus status)
specifier|public
name|HadoopJob
parameter_list|(
name|HadoopServer
name|location
parameter_list|,
name|JobID
name|id
parameter_list|,
name|RunningJob
name|running
parameter_list|,
name|JobStatus
name|status
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|running
operator|=
name|running
expr_stmt|;
name|loadJobFile
argument_list|()
expr_stmt|;
name|update
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
end_constructor

begin_comment
comment|/**    * Try to locate and load the JobConf file for this job so to get more    * details on the job (number of maps and of reduces)    */
end_comment

begin_function
DECL|method|loadJobFile ()
specifier|private
name|void
name|loadJobFile
parameter_list|()
block|{
try|try
block|{
name|String
name|jobFile
init|=
name|getJobFile
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|location
operator|.
name|getDFS
argument_list|()
decl_stmt|;
name|File
name|tmp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|jobFile
argument_list|)
argument_list|,
name|tmp
argument_list|,
literal|false
argument_list|,
name|location
operator|.
name|getConfiguration
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|jobConf
operator|=
operator|new
name|JobConf
argument_list|(
name|tmp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalMaps
operator|=
name|jobConf
operator|.
name|getNumMapTasks
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalReduces
operator|=
name|jobConf
operator|.
name|getNumReduceTasks
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/* @inheritDoc */
end_comment

begin_function
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|jobId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|jobId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|location
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|location
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
end_function

begin_comment
comment|/* @inheritDoc */
end_comment

begin_function
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|HadoopJob
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|HadoopJob
name|other
init|=
operator|(
name|HadoopJob
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|jobId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|jobId
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|jobId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|jobId
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|location
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|location
operator|.
name|equals
argument_list|(
name|other
operator|.
name|location
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
end_function

begin_comment
comment|/**    * Get the running status of the Job (@see {@link JobStatus}).    *     * @return    */
end_comment

begin_function
DECL|method|getState ()
specifier|public
name|JobState
name|getState
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|completed
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|successful
condition|)
block|{
return|return
name|JobState
operator|.
name|SUCCEEDED
return|;
block|}
else|else
block|{
return|return
name|JobState
operator|.
name|FAILED
return|;
block|}
block|}
else|else
block|{
return|return
name|JobState
operator|.
name|RUNNING
return|;
block|}
comment|// return JobState.ofInt(this.status.getRunState());
block|}
end_function

begin_comment
comment|/**    * @return    */
end_comment

begin_function
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
block|{
return|return
name|this
operator|.
name|jobId
return|;
block|}
end_function

begin_comment
comment|/**    * @return    */
end_comment

begin_function
DECL|method|getLocation ()
specifier|public
name|HadoopServer
name|getLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|location
return|;
block|}
end_function

begin_comment
comment|/**    * @return    */
end_comment

begin_function
DECL|method|isCompleted ()
specifier|public
name|boolean
name|isCompleted
parameter_list|()
block|{
return|return
name|this
operator|.
name|completed
return|;
block|}
end_function

begin_comment
comment|/**    * @return    */
end_comment

begin_function
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
block|{
return|return
name|this
operator|.
name|running
operator|.
name|getJobName
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**    * @return    */
end_comment

begin_function
DECL|method|getJobFile ()
specifier|public
name|String
name|getJobFile
parameter_list|()
block|{
return|return
name|this
operator|.
name|running
operator|.
name|getJobFile
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**    * Return the tracking URL for this Job.    *     * @return string representation of the tracking URL for this Job    */
end_comment

begin_function
DECL|method|getTrackingURL ()
specifier|public
name|String
name|getTrackingURL
parameter_list|()
block|{
return|return
name|this
operator|.
name|running
operator|.
name|getTrackingURL
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**    * Returns a string representation of this job status    *     * @return string representation of this job status    */
end_comment

begin_function
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
name|StringBuffer
name|s
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
literal|"Maps : "
operator|+
name|completedMaps
operator|+
literal|"/"
operator|+
name|totalMaps
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
literal|" ("
operator|+
name|mapProgress
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
literal|"  Reduces : "
operator|+
name|completedReduces
operator|+
literal|"/"
operator|+
name|totalReduces
argument_list|)
expr_stmt|;
name|s
operator|.
name|append
argument_list|(
literal|" ("
operator|+
name|reduceProgress
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**    * Update this job status according to the given JobStatus    *     * @param status    */
end_comment

begin_function
DECL|method|update (JobStatus status)
name|void
name|update
parameter_list|(
name|JobStatus
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
try|try
block|{
name|this
operator|.
name|counters
operator|=
name|running
operator|.
name|getCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|completed
operator|=
name|running
operator|.
name|isComplete
argument_list|()
expr_stmt|;
name|this
operator|.
name|successful
operator|=
name|running
operator|.
name|isSuccessful
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapProgress
operator|=
name|running
operator|.
name|mapProgress
argument_list|()
expr_stmt|;
name|this
operator|.
name|reduceProgress
operator|=
name|running
operator|.
name|reduceProgress
argument_list|()
expr_stmt|;
comment|// running.getTaskCompletionEvents(fromEvent);
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|completedMaps
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|totalMaps
operator|*
name|this
operator|.
name|mapProgress
argument_list|)
expr_stmt|;
name|this
operator|.
name|completedReduces
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|totalReduces
operator|*
name|this
operator|.
name|reduceProgress
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Print this job counters (for debugging purpose)    */
end_comment

begin_function
DECL|method|printCounters ()
name|void
name|printCounters
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"New Job:\n"
argument_list|,
name|counters
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|groupName
range|:
name|counters
operator|.
name|getGroupNames
argument_list|()
control|)
block|{
name|Counters
operator|.
name|Group
name|group
init|=
name|counters
operator|.
name|getGroup
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"\t%s[%s]\n"
argument_list|,
name|groupName
argument_list|,
name|group
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Counters
operator|.
name|Counter
name|counter
range|:
name|group
control|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"\t\t%s: %s\n"
argument_list|,
name|counter
operator|.
name|getDisplayName
argument_list|()
argument_list|,
name|counter
operator|.
name|getCounter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Kill this job    */
end_comment

begin_function
DECL|method|kill ()
specifier|public
name|void
name|kill
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|running
operator|.
name|killJob
argument_list|()
expr_stmt|;
name|this
operator|.
name|killed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/**    * Print this job status (for debugging purpose)    */
end_comment

begin_function
DECL|method|display ()
specifier|public
name|void
name|display
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Job id=%s, name=%s\n"
argument_list|,
name|getJobID
argument_list|()
argument_list|,
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Configuration file: %s\n"
argument_list|,
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Tracking URL: %s\n"
argument_list|,
name|getTrackingURL
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Completion: map: %f reduce %f\n"
argument_list|,
literal|100.0
operator|*
name|this
operator|.
name|mapProgress
argument_list|,
literal|100.0
operator|*
name|this
operator|.
name|reduceProgress
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job total maps = "
operator|+
name|totalMaps
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job completed maps = "
operator|+
name|completedMaps
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Map percentage complete = "
operator|+
name|mapProgress
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job total reduces = "
operator|+
name|totalReduces
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job completed reduces = "
operator|+
name|completedReduces
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reduce percentage complete = "
operator|+
name|reduceProgress
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
end_function

unit|}
end_unit

