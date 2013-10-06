begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
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
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenSecretManager
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
DECL|class|MockAppContext
specifier|public
class|class
name|MockAppContext
implements|implements
name|AppContext
block|{
DECL|field|appAttemptID
specifier|final
name|ApplicationAttemptId
name|appAttemptID
decl_stmt|;
DECL|field|appID
specifier|final
name|ApplicationId
name|appID
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
init|=
name|MockJobs
operator|.
name|newUserName
argument_list|()
decl_stmt|;
DECL|field|jobs
specifier|final
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobs
decl_stmt|;
DECL|field|startTime
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|blacklistedNodes
name|Set
argument_list|<
name|String
argument_list|>
name|blacklistedNodes
decl_stmt|;
DECL|method|MockAppContext (int appid)
specifier|public
name|MockAppContext
parameter_list|(
name|int
name|appid
parameter_list|)
block|{
name|appID
operator|=
name|MockJobs
operator|.
name|newAppID
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|appAttemptID
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jobs
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|MockAppContext (int appid, int numTasks, int numAttempts, Path confPath)
specifier|public
name|MockAppContext
parameter_list|(
name|int
name|appid
parameter_list|,
name|int
name|numTasks
parameter_list|,
name|int
name|numAttempts
parameter_list|,
name|Path
name|confPath
parameter_list|)
block|{
name|appID
operator|=
name|MockJobs
operator|.
name|newAppID
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|appAttemptID
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MockJobs
operator|.
name|newJob
argument_list|(
name|appID
argument_list|,
literal|0
argument_list|,
name|numTasks
argument_list|,
name|numAttempts
argument_list|,
name|confPath
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|jobs
operator|=
name|map
expr_stmt|;
block|}
DECL|method|MockAppContext (int appid, int numJobs, int numTasks, int numAttempts)
specifier|public
name|MockAppContext
parameter_list|(
name|int
name|appid
parameter_list|,
name|int
name|numJobs
parameter_list|,
name|int
name|numTasks
parameter_list|,
name|int
name|numAttempts
parameter_list|)
block|{
name|this
argument_list|(
name|appid
argument_list|,
name|numJobs
argument_list|,
name|numTasks
argument_list|,
name|numAttempts
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|MockAppContext (int appid, int numJobs, int numTasks, int numAttempts, boolean hasFailedTasks)
specifier|public
name|MockAppContext
parameter_list|(
name|int
name|appid
parameter_list|,
name|int
name|numJobs
parameter_list|,
name|int
name|numTasks
parameter_list|,
name|int
name|numAttempts
parameter_list|,
name|boolean
name|hasFailedTasks
parameter_list|)
block|{
name|appID
operator|=
name|MockJobs
operator|.
name|newAppID
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|appAttemptID
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jobs
operator|=
name|MockJobs
operator|.
name|newJobs
argument_list|(
name|appID
argument_list|,
name|numJobs
argument_list|,
name|numTasks
argument_list|,
name|numAttempts
argument_list|,
name|hasFailedTasks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|appAttemptID
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationID ()
specifier|public
name|ApplicationId
name|getApplicationID
parameter_list|()
block|{
return|return
name|appID
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|CharSequence
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getJob (JobId jobID)
specifier|public
name|Job
name|getJob
parameter_list|(
name|JobId
name|jobID
parameter_list|)
block|{
return|return
name|jobs
operator|.
name|get
argument_list|(
name|jobID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllJobs ()
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
block|{
return|return
name|jobs
return|;
comment|// OK
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getClock ()
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
block|{
return|return
literal|"TestApp"
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterInfo ()
specifier|public
name|ClusterInfo
name|getClusterInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBlacklistedNodes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklistedNodes
parameter_list|()
block|{
return|return
name|blacklistedNodes
return|;
block|}
DECL|method|setBlacklistedNodes (Set<String> blacklistedNodes)
specifier|public
name|void
name|setBlacklistedNodes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|blacklistedNodes
parameter_list|)
block|{
name|this
operator|.
name|blacklistedNodes
operator|=
name|blacklistedNodes
expr_stmt|;
block|}
DECL|method|getClientToAMTokenSecretManager ()
specifier|public
name|ClientToAMTokenSecretManager
name|getClientToAMTokenSecretManager
parameter_list|()
block|{
comment|// Not implemented
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|isLastAMRetry ()
specifier|public
name|boolean
name|isLastAMRetry
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hasSuccessfullyUnregistered ()
specifier|public
name|boolean
name|hasSuccessfullyUnregistered
parameter_list|()
block|{
comment|// bogus - Not Required
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

