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
name|ReduceTaskAttemptImpl
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
name|event
operator|.
name|EventHandler
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
name|Clock
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|}
argument_list|)
DECL|class|ReduceTaskImpl
specifier|public
class|class
name|ReduceTaskImpl
extends|extends
name|TaskImpl
block|{
DECL|field|numMapTasks
specifier|private
specifier|final
name|int
name|numMapTasks
decl_stmt|;
DECL|method|ReduceTaskImpl (JobId jobId, int partition, EventHandler eventHandler, Path jobFile, JobConf conf, int numMapTasks, TaskAttemptListener taskAttemptListener, Token<JobTokenIdentifier> jobToken, Credentials credentials, Clock clock, int appAttemptId, MRAppMetrics metrics, AppContext appContext)
specifier|public
name|ReduceTaskImpl
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
name|jobFile
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|int
name|numMapTasks
parameter_list|,
name|TaskAttemptListener
name|taskAttemptListener
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
name|int
name|appAttemptId
parameter_list|,
name|MRAppMetrics
name|metrics
parameter_list|,
name|AppContext
name|appContext
parameter_list|)
block|{
name|super
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
name|partition
argument_list|,
name|eventHandler
argument_list|,
name|jobFile
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|jobToken
argument_list|,
name|credentials
argument_list|,
name|clock
argument_list|,
name|appAttemptId
argument_list|,
name|metrics
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
name|REDUCE_MAX_ATTEMPTS
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
name|ReduceTaskAttemptImpl
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
name|numMapTasks
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|jobToken
argument_list|,
name|credentials
argument_list|,
name|clock
argument_list|,
name|appContext
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
name|REDUCE
return|;
block|}
block|}
end_class

end_unit

