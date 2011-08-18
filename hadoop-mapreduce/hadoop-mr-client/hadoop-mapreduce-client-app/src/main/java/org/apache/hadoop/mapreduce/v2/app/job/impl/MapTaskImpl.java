begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.impl
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
name|job
operator|.
name|impl
package|;
end_package

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
name|Set
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
name|MapTaskAttemptImpl
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
name|OutputCommitter
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
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|split
operator|.
name|JobSplit
operator|.
name|TaskSplitMetaInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|metrics
operator|.
name|MRAppMetrics
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
name|TaskAttemptListener
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|Clock
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_class
DECL|class|MapTaskImpl
specifier|public
class|class
name|MapTaskImpl
extends|extends
name|TaskImpl
block|{
DECL|field|taskSplitMetaInfo
specifier|private
specifier|final
name|TaskSplitMetaInfo
name|taskSplitMetaInfo
decl_stmt|;
DECL|method|MapTaskImpl (JobId jobId, int partition, EventHandler eventHandler, Path remoteJobConfFile, Configuration conf, TaskSplitMetaInfo taskSplitMetaInfo, TaskAttemptListener taskAttemptListener, OutputCommitter committer, Token<JobTokenIdentifier> jobToken, Collection<Token<? extends TokenIdentifier>> fsTokens, Clock clock, Set<TaskId> completedTasksFromPreviousRun, int startCount, MRAppMetrics metrics)
specifier|public
name|MapTaskImpl
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|int
name|partition
parameter_list|,
name|EventHandler
name|eventHandler
parameter_list|,
name|Path
name|remoteJobConfFile
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|TaskSplitMetaInfo
name|taskSplitMetaInfo
parameter_list|,
name|TaskAttemptListener
name|taskAttemptListener
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jobToken
parameter_list|,
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|fsTokens
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|Set
argument_list|<
name|TaskId
argument_list|>
name|completedTasksFromPreviousRun
parameter_list|,
name|int
name|startCount
parameter_list|,
name|MRAppMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
name|partition
argument_list|,
name|eventHandler
argument_list|,
name|remoteJobConfFile
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|,
name|completedTasksFromPreviousRun
argument_list|,
name|startCount
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskSplitMetaInfo
operator|=
name|taskSplitMetaInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxAttempts ()
specifier|protected
name|int
name|getMaxAttempts
parameter_list|()
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|4
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createAttempt ()
specifier|protected
name|TaskAttemptImpl
name|createAttempt
parameter_list|()
block|{
return|return
operator|new
name|MapTaskAttemptImpl
argument_list|(
name|getID
argument_list|()
argument_list|,
name|nextAttemptNumber
argument_list|,
name|eventHandler
argument_list|,
name|jobFile
argument_list|,
name|partition
argument_list|,
name|taskSplitMetaInfo
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|TaskType
name|getType
parameter_list|()
block|{
return|return
name|TaskType
operator|.
name|MAP
return|;
block|}
DECL|method|getTaskSplitMetaInfo ()
specifier|protected
name|TaskSplitMetaInfo
name|getTaskSplitMetaInfo
parameter_list|()
block|{
return|return
name|this
operator|.
name|taskSplitMetaInfo
return|;
block|}
comment|/**    * @return a String formatted as a comma-separated list of splits.    */
annotation|@
name|Override
DECL|method|getSplitsAsString ()
specifier|protected
name|String
name|getSplitsAsString
parameter_list|()
block|{
name|String
index|[]
name|splits
init|=
name|getTaskSplitMetaInfo
argument_list|()
operator|.
name|getLocations
argument_list|()
decl_stmt|;
if|if
condition|(
name|splits
operator|==
literal|null
operator|||
name|splits
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|splits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

