begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|TypeConverter
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
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|impl
operator|.
name|TaskAttemptImpl
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|MapTaskAttemptImpl
specifier|public
class|class
name|MapTaskAttemptImpl
extends|extends
name|TaskAttemptImpl
block|{
DECL|field|splitInfo
specifier|private
specifier|final
name|TaskSplitMetaInfo
name|splitInfo
decl_stmt|;
DECL|method|MapTaskAttemptImpl (TaskId taskId, int attempt, EventHandler eventHandler, Path jobFile, int partition, TaskSplitMetaInfo splitInfo, JobConf conf, TaskAttemptListener taskAttemptListener, OutputCommitter committer, Token<JobTokenIdentifier> jobToken, Collection<Token<? extends TokenIdentifier>> fsTokens, Clock clock, AppContext appContext)
specifier|public
name|MapTaskAttemptImpl
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|int
name|attempt
parameter_list|,
name|EventHandler
name|eventHandler
parameter_list|,
name|Path
name|jobFile
parameter_list|,
name|int
name|partition
parameter_list|,
name|TaskSplitMetaInfo
name|splitInfo
parameter_list|,
name|JobConf
name|conf
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
name|AppContext
name|appContext
parameter_list|)
block|{
name|super
argument_list|(
name|taskId
argument_list|,
name|attempt
argument_list|,
name|eventHandler
argument_list|,
name|taskAttemptListener
argument_list|,
name|jobFile
argument_list|,
name|partition
argument_list|,
name|conf
argument_list|,
name|splitInfo
operator|.
name|getLocations
argument_list|()
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|splitInfo
operator|=
name|splitInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createRemoteTask ()
specifier|public
name|Task
name|createRemoteTask
parameter_list|()
block|{
comment|//job file name is set in TaskAttempt, setting it null here
name|MapTask
name|mapTask
init|=
operator|new
name|MapTask
argument_list|(
literal|""
argument_list|,
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|getID
argument_list|()
argument_list|)
argument_list|,
name|partition
argument_list|,
name|splitInfo
operator|.
name|getSplitIndex
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// YARN doesn't have the concept of slots per task, set it as 1.
name|mapTask
operator|.
name|setUser
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|USER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|mapTask
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|mapTask
return|;
block|}
block|}
end_class

end_unit

