begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.util
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
name|util
package|;
end_package

begin_import
import|import static
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
name|StringHelper
operator|.
name|_join
import|;
end_import

begin_import
import|import static
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
name|StringHelper
operator|.
name|_split
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|conf
operator|.
name|Configuration
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|filecache
operator|.
name|DistributedCache
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
name|JobId
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
name|yarn
operator|.
name|ContainerLogAppender
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
name|YarnException
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
name|ApplicationConstants
operator|.
name|Environment
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
name|ApplicationConstants
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
name|ApplicationId
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|Apps
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
name|BuilderUtils
import|;
end_import

begin_comment
comment|/**  * Helper class for MR applications  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|MRApps
specifier|public
class|class
name|MRApps
extends|extends
name|Apps
block|{
DECL|field|JOB
specifier|public
specifier|static
specifier|final
name|String
name|JOB
init|=
literal|"job"
decl_stmt|;
DECL|field|TASK
specifier|public
specifier|static
specifier|final
name|String
name|TASK
init|=
literal|"task"
decl_stmt|;
DECL|field|ATTEMPT
specifier|public
specifier|static
specifier|final
name|String
name|ATTEMPT
init|=
literal|"attempt"
decl_stmt|;
DECL|method|toString (JobId jid)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|JobId
name|jid
parameter_list|)
block|{
return|return
name|_join
argument_list|(
name|JOB
argument_list|,
name|jid
operator|.
name|getAppId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|,
name|jid
operator|.
name|getAppId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|jid
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toJobID (String jid)
specifier|public
specifier|static
name|JobId
name|toJobID
parameter_list|(
name|String
name|jid
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|jid
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
name|toJobID
argument_list|(
name|JOB
argument_list|,
name|jid
argument_list|,
name|it
argument_list|)
return|;
block|}
comment|// mostly useful for parsing task/attempt id like strings
DECL|method|toJobID (String prefix, String s, Iterator<String> it)
specifier|public
specifier|static
name|JobId
name|toJobID
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|s
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|toAppID
argument_list|(
name|prefix
argument_list|,
name|s
argument_list|,
name|it
argument_list|)
decl_stmt|;
name|shouldHaveNext
argument_list|(
name|prefix
argument_list|,
name|s
argument_list|,
name|it
argument_list|)
expr_stmt|;
name|JobId
name|jobId
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobId
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jobId
return|;
block|}
DECL|method|toString (TaskId tid)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|TaskId
name|tid
parameter_list|)
block|{
return|return
name|_join
argument_list|(
literal|"task"
argument_list|,
name|tid
operator|.
name|getJobId
argument_list|()
operator|.
name|getAppId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|,
name|tid
operator|.
name|getJobId
argument_list|()
operator|.
name|getAppId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|tid
operator|.
name|getJobId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|taskSymbol
argument_list|(
name|tid
operator|.
name|getTaskType
argument_list|()
argument_list|)
argument_list|,
name|tid
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toTaskID (String tid)
specifier|public
specifier|static
name|TaskId
name|toTaskID
parameter_list|(
name|String
name|tid
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|tid
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
name|toTaskID
argument_list|(
name|TASK
argument_list|,
name|tid
argument_list|,
name|it
argument_list|)
return|;
block|}
DECL|method|toTaskID (String prefix, String s, Iterator<String> it)
specifier|public
specifier|static
name|TaskId
name|toTaskID
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|s
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
name|JobId
name|jid
init|=
name|toJobID
argument_list|(
name|prefix
argument_list|,
name|s
argument_list|,
name|it
argument_list|)
decl_stmt|;
name|shouldHaveNext
argument_list|(
name|prefix
argument_list|,
name|s
argument_list|,
name|it
argument_list|)
expr_stmt|;
name|TaskId
name|tid
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|TaskId
operator|.
name|class
argument_list|)
decl_stmt|;
name|tid
operator|.
name|setJobId
argument_list|(
name|jid
argument_list|)
expr_stmt|;
name|tid
operator|.
name|setTaskType
argument_list|(
name|taskType
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|shouldHaveNext
argument_list|(
name|prefix
argument_list|,
name|s
argument_list|,
name|it
argument_list|)
expr_stmt|;
name|tid
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tid
return|;
block|}
DECL|method|toString (TaskAttemptId taid)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|TaskAttemptId
name|taid
parameter_list|)
block|{
return|return
name|_join
argument_list|(
literal|"attempt"
argument_list|,
name|taid
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
operator|.
name|getAppId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|,
name|taid
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
operator|.
name|getAppId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|taid
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|taskSymbol
argument_list|(
name|taid
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
argument_list|)
argument_list|,
name|taid
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|taid
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toTaskAttemptID (String taid)
specifier|public
specifier|static
name|TaskAttemptId
name|toTaskAttemptID
parameter_list|(
name|String
name|taid
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_split
argument_list|(
name|taid
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TaskId
name|tid
init|=
name|toTaskID
argument_list|(
name|ATTEMPT
argument_list|,
name|taid
argument_list|,
name|it
argument_list|)
decl_stmt|;
name|shouldHaveNext
argument_list|(
name|ATTEMPT
argument_list|,
name|taid
argument_list|,
name|it
argument_list|)
expr_stmt|;
name|TaskAttemptId
name|taId
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|TaskAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|taId
operator|.
name|setTaskId
argument_list|(
name|tid
argument_list|)
expr_stmt|;
name|taId
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|taId
return|;
block|}
DECL|method|taskSymbol (TaskType type)
specifier|public
specifier|static
name|String
name|taskSymbol
parameter_list|(
name|TaskType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|MAP
case|:
return|return
literal|"m"
return|;
case|case
name|REDUCE
case|:
return|return
literal|"r"
return|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Unknown task type: "
operator|+
name|type
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
DECL|enum|TaskAttemptStateUI
specifier|public
specifier|static
enum|enum
name|TaskAttemptStateUI
block|{
DECL|enumConstant|NEW
name|NEW
argument_list|(
operator|new
name|TaskAttemptState
index|[]
block|{
name|TaskAttemptState
operator|.
name|NEW
block|,
name|TaskAttemptState
operator|.
name|UNASSIGNED
block|,
name|TaskAttemptState
operator|.
name|ASSIGNED
block|}
argument_list|)
block|,
DECL|enumConstant|RUNNING
name|RUNNING
argument_list|(
operator|new
name|TaskAttemptState
index|[]
block|{
name|TaskAttemptState
operator|.
name|RUNNING
block|,
name|TaskAttemptState
operator|.
name|COMMIT_PENDING
block|,
name|TaskAttemptState
operator|.
name|SUCCESS_CONTAINER_CLEANUP
block|,
name|TaskAttemptState
operator|.
name|FAIL_CONTAINER_CLEANUP
block|,
name|TaskAttemptState
operator|.
name|FAIL_TASK_CLEANUP
block|,
name|TaskAttemptState
operator|.
name|KILL_CONTAINER_CLEANUP
block|,
name|TaskAttemptState
operator|.
name|KILL_TASK_CLEANUP
block|}
argument_list|)
block|,
DECL|enumConstant|SUCCESSFUL
name|SUCCESSFUL
argument_list|(
operator|new
name|TaskAttemptState
index|[]
block|{
name|TaskAttemptState
operator|.
name|SUCCEEDED
block|}
argument_list|)
block|,
DECL|enumConstant|FAILED
name|FAILED
argument_list|(
operator|new
name|TaskAttemptState
index|[]
block|{
name|TaskAttemptState
operator|.
name|FAILED
block|}
argument_list|)
block|,
DECL|enumConstant|KILLED
name|KILLED
argument_list|(
operator|new
name|TaskAttemptState
index|[]
block|{
name|TaskAttemptState
operator|.
name|KILLED
block|}
argument_list|)
block|;
DECL|field|correspondingStates
specifier|private
specifier|final
name|List
argument_list|<
name|TaskAttemptState
argument_list|>
name|correspondingStates
decl_stmt|;
DECL|method|TaskAttemptStateUI (TaskAttemptState[] correspondingStates)
specifier|private
name|TaskAttemptStateUI
parameter_list|(
name|TaskAttemptState
index|[]
name|correspondingStates
parameter_list|)
block|{
name|this
operator|.
name|correspondingStates
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|correspondingStates
argument_list|)
expr_stmt|;
block|}
DECL|method|correspondsTo (TaskAttemptState state)
specifier|public
name|boolean
name|correspondsTo
parameter_list|(
name|TaskAttemptState
name|state
parameter_list|)
block|{
return|return
name|this
operator|.
name|correspondingStates
operator|.
name|contains
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
DECL|method|taskType (String symbol)
specifier|public
specifier|static
name|TaskType
name|taskType
parameter_list|(
name|String
name|symbol
parameter_list|)
block|{
comment|// JDK 7 supports switch on strings
if|if
condition|(
name|symbol
operator|.
name|equals
argument_list|(
literal|"m"
argument_list|)
condition|)
return|return
name|TaskType
operator|.
name|MAP
return|;
if|if
condition|(
name|symbol
operator|.
name|equals
argument_list|(
literal|"r"
argument_list|)
condition|)
return|return
name|TaskType
operator|.
name|REDUCE
return|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Unknown task symbol: "
operator|+
name|symbol
argument_list|)
throw|;
block|}
DECL|method|taskAttemptState (String attemptStateStr)
specifier|public
specifier|static
name|TaskAttemptStateUI
name|taskAttemptState
parameter_list|(
name|String
name|attemptStateStr
parameter_list|)
block|{
return|return
name|TaskAttemptStateUI
operator|.
name|valueOf
argument_list|(
name|attemptStateStr
argument_list|)
return|;
block|}
DECL|method|setMRFrameworkClasspath ( Map<String, String> environment)
specifier|private
specifier|static
name|void
name|setMRFrameworkClasspath
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|classpathFileStream
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Get yarn mapreduce-app classpath from generated classpath
comment|// Works if compile time env is same as runtime. Mainly tests.
name|ClassLoader
name|thisClassLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|String
name|mrAppGeneratedClasspathFile
init|=
literal|"mrapp-generated-classpath"
decl_stmt|;
name|classpathFileStream
operator|=
name|thisClassLoader
operator|.
name|getResourceAsStream
argument_list|(
name|mrAppGeneratedClasspathFile
argument_list|)
expr_stmt|;
comment|// Put the file itself on classpath for tasks.
name|String
name|classpathElement
init|=
name|thisClassLoader
operator|.
name|getResource
argument_list|(
name|mrAppGeneratedClasspathFile
argument_list|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|classpathElement
operator|.
name|contains
argument_list|(
literal|"!"
argument_list|)
condition|)
block|{
name|classpathElement
operator|=
name|classpathElement
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|classpathElement
operator|.
name|indexOf
argument_list|(
literal|"!"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classpathElement
operator|=
operator|new
name|File
argument_list|(
name|classpathElement
argument_list|)
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|Apps
operator|.
name|addToEnvironment
argument_list|(
name|environment
argument_list|,
name|Environment
operator|.
name|CLASSPATH
operator|.
name|name
argument_list|()
argument_list|,
name|classpathElement
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|classpathFileStream
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cp
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|null
condition|)
block|{
name|Apps
operator|.
name|addToEnvironment
argument_list|(
name|environment
argument_list|,
name|Environment
operator|.
name|CLASSPATH
operator|.
name|name
argument_list|()
argument_list|,
name|cp
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add standard Hadoop classes
for|for
control|(
name|String
name|c
range|:
name|ApplicationConstants
operator|.
name|APPLICATION_CLASSPATH
control|)
block|{
name|Apps
operator|.
name|addToEnvironment
argument_list|(
name|environment
argument_list|,
name|Environment
operator|.
name|CLASSPATH
operator|.
name|name
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|classpathFileStream
operator|!=
literal|null
condition|)
block|{
name|classpathFileStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: Remove duplicates.
block|}
DECL|method|setClasspath (Map<String, String> environment)
specifier|public
specifier|static
name|void
name|setClasspath
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|Apps
operator|.
name|addToEnvironment
argument_list|(
name|environment
argument_list|,
name|Environment
operator|.
name|CLASSPATH
operator|.
name|name
argument_list|()
argument_list|,
name|MRJobConfig
operator|.
name|JOB_JAR
argument_list|)
expr_stmt|;
name|Apps
operator|.
name|addToEnvironment
argument_list|(
name|environment
argument_list|,
name|Environment
operator|.
name|CLASSPATH
operator|.
name|name
argument_list|()
argument_list|,
name|Environment
operator|.
name|PWD
operator|.
name|$
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"*"
argument_list|)
expr_stmt|;
name|MRApps
operator|.
name|setMRFrameworkClasspath
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
DECL|field|STAGING_CONSTANT
specifier|private
specifier|static
specifier|final
name|String
name|STAGING_CONSTANT
init|=
literal|".staging"
decl_stmt|;
DECL|method|getStagingAreaDir (Configuration conf, String user)
specifier|public
specifier|static
name|Path
name|getStagingAreaDir
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|user
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|STAGING_CONSTANT
argument_list|)
return|;
block|}
DECL|method|getJobFile (Configuration conf, String user, org.apache.hadoop.mapreduce.JobID jobId)
specifier|public
specifier|static
name|String
name|getJobFile
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|jobId
parameter_list|)
block|{
name|Path
name|jobFile
init|=
operator|new
name|Path
argument_list|(
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
argument_list|,
name|jobId
operator|.
name|toString
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|MRJobConfig
operator|.
name|JOB_CONF_FILE
argument_list|)
decl_stmt|;
return|return
name|jobFile
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|parseTimeStamps (String[] strs)
specifier|private
specifier|static
name|long
index|[]
name|parseTimeStamps
parameter_list|(
name|String
index|[]
name|strs
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|strs
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
index|[]
name|result
init|=
operator|new
name|long
index|[
name|strs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|setupDistributedCache ( Configuration conf, Map<String, LocalResource> localResources)
specifier|public
specifier|static
name|void
name|setupDistributedCache
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Cache archives
name|parseDistributedCacheArtifacts
argument_list|(
name|conf
argument_list|,
name|localResources
argument_list|,
name|LocalResourceType
operator|.
name|ARCHIVE
argument_list|,
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|conf
argument_list|)
argument_list|,
name|parseTimeStamps
argument_list|(
name|DistributedCache
operator|.
name|getArchiveTimestamps
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|getFileSizes
argument_list|(
name|conf
argument_list|,
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_SIZES
argument_list|)
argument_list|,
name|DistributedCache
operator|.
name|getArchiveVisibilities
argument_list|(
name|conf
argument_list|)
argument_list|,
name|DistributedCache
operator|.
name|getArchiveClassPaths
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cache files
name|parseDistributedCacheArtifacts
argument_list|(
name|conf
argument_list|,
name|localResources
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|,
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|conf
argument_list|)
argument_list|,
name|parseTimeStamps
argument_list|(
name|DistributedCache
operator|.
name|getFileTimestamps
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|getFileSizes
argument_list|(
name|conf
argument_list|,
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|)
argument_list|,
name|DistributedCache
operator|.
name|getFileVisibilities
argument_list|(
name|conf
argument_list|)
argument_list|,
name|DistributedCache
operator|.
name|getFileClassPaths
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO - Move this to MR!
comment|// Use TaskDistributedCacheManager.CacheFiles.makeCacheFiles(URI[],
comment|// long[], boolean[], Path[], FileType)
DECL|method|parseDistributedCacheArtifacts ( Configuration conf, Map<String, LocalResource> localResources, LocalResourceType type, URI[] uris, long[] timestamps, long[] sizes, boolean visibilities[], Path[] pathsToPutOnClasspath)
specifier|private
specifier|static
name|void
name|parseDistributedCacheArtifacts
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|,
name|LocalResourceType
name|type
parameter_list|,
name|URI
index|[]
name|uris
parameter_list|,
name|long
index|[]
name|timestamps
parameter_list|,
name|long
index|[]
name|sizes
parameter_list|,
name|boolean
name|visibilities
index|[]
parameter_list|,
name|Path
index|[]
name|pathsToPutOnClasspath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|uris
operator|!=
literal|null
condition|)
block|{
comment|// Sanity check
if|if
condition|(
operator|(
name|uris
operator|.
name|length
operator|!=
name|timestamps
operator|.
name|length
operator|)
operator|||
operator|(
name|uris
operator|.
name|length
operator|!=
name|sizes
operator|.
name|length
operator|)
operator|||
operator|(
name|uris
operator|.
name|length
operator|!=
name|visibilities
operator|.
name|length
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid specification for "
operator|+
literal|"distributed-cache artifacts of type "
operator|+
name|type
operator|+
literal|" :"
operator|+
literal|" #uris="
operator|+
name|uris
operator|.
name|length
operator|+
literal|" #timestamps="
operator|+
name|timestamps
operator|.
name|length
operator|+
literal|" #visibilities="
operator|+
name|visibilities
operator|.
name|length
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|classPaths
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathsToPutOnClasspath
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Path
name|p
range|:
name|pathsToPutOnClasspath
control|)
block|{
name|FileSystem
name|remoteFS
init|=
name|p
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|p
operator|=
name|remoteFS
operator|.
name|resolvePath
argument_list|(
name|p
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|classPaths
operator|.
name|put
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|uris
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|URI
name|u
init|=
name|uris
index|[
name|i
index|]
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|u
argument_list|)
decl_stmt|;
name|FileSystem
name|remoteFS
init|=
name|p
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|p
operator|=
name|remoteFS
operator|.
name|resolvePath
argument_list|(
name|p
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add URI fragment or just the filename
name|Path
name|name
init|=
operator|new
name|Path
argument_list|(
operator|(
literal|null
operator|==
name|u
operator|.
name|getFragment
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getName
argument_list|()
else|:
name|u
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Resource name must be relative"
argument_list|)
throw|;
block|}
name|String
name|linkName
init|=
name|name
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|localResources
operator|.
name|put
argument_list|(
name|linkName
argument_list|,
name|BuilderUtils
operator|.
name|newLocalResource
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|type
argument_list|,
name|visibilities
index|[
name|i
index|]
condition|?
name|LocalResourceVisibility
operator|.
name|PUBLIC
else|:
name|LocalResourceVisibility
operator|.
name|PRIVATE
argument_list|,
name|sizes
index|[
name|i
index|]
argument_list|,
name|timestamps
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO - Move this to MR!
DECL|method|getFileSizes (Configuration conf, String key)
specifier|private
specifier|static
name|long
index|[]
name|getFileSizes
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
index|[]
name|strs
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|strs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
index|[]
name|result
init|=
operator|new
name|long
index|[
name|strs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Add the JVM system properties necessary to configure {@link ContainerLogAppender}.    * @param logLevel the desired log level (eg INFO/WARN/DEBUG)    * @param logSize See {@link ContainerLogAppender#setTotalLogFileSize(long)}    * @param vargs the argument list to append to    */
DECL|method|addLog4jSystemProperties ( String logLevel, long logSize, List<String> vargs)
specifier|public
specifier|static
name|void
name|addLog4jSystemProperties
parameter_list|(
name|String
name|logLevel
parameter_list|,
name|long
name|logSize
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|vargs
parameter_list|)
block|{
name|vargs
operator|.
name|add
argument_list|(
literal|"-Dlog4j.configuration=container-log4j.properties"
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"-D"
operator|+
name|MRJobConfig
operator|.
name|TASK_LOG_DIR
operator|+
literal|"="
operator|+
name|ApplicationConstants
operator|.
name|LOG_DIR_EXPANSION_VAR
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"-D"
operator|+
name|MRJobConfig
operator|.
name|TASK_LOG_SIZE
operator|+
literal|"="
operator|+
name|logSize
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"-Dhadoop.root.logger="
operator|+
name|logLevel
operator|+
literal|",CLA"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

