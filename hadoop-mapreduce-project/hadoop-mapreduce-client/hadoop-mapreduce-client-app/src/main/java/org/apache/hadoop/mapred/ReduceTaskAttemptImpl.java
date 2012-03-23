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
name|Credentials
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
DECL|class|ReduceTaskAttemptImpl
specifier|public
class|class
name|ReduceTaskAttemptImpl
extends|extends
name|TaskAttemptImpl
block|{
DECL|field|numMapTasks
specifier|private
specifier|final
name|int
name|numMapTasks
decl_stmt|;
DECL|method|ReduceTaskAttemptImpl (TaskId id, int attempt, EventHandler eventHandler, Path jobFile, int partition, int numMapTasks, JobConf conf, TaskAttemptListener taskAttemptListener, OutputCommitter committer, Token<JobTokenIdentifier> jobToken, Credentials credentials, Clock clock, AppContext appContext)
specifier|public
name|ReduceTaskAttemptImpl
parameter_list|(
name|TaskId
name|id
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
name|int
name|numMapTasks
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
name|Credentials
name|credentials
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
name|id
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
operator|new
name|String
index|[]
block|{}
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|credentials
argument_list|,
name|clock
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|numMapTasks
operator|=
name|numMapTasks
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
name|ReduceTask
name|reduceTask
init|=
operator|new
name|ReduceTask
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
name|numMapTasks
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// YARN doesn't have the concept of slots per task, set it as 1.
name|reduceTask
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
name|reduceTask
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|reduceTask
return|;
block|}
block|}
end_class

end_unit

