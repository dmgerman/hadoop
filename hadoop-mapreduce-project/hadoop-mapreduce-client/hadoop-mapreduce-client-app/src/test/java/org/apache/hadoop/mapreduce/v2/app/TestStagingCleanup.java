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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|JobState
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
name|client
operator|.
name|ClientService
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
name|JobStateInternal
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
name|event
operator|.
name|JobFinishEvent
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
name|JobImpl
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
name|rm
operator|.
name|ContainerAllocator
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
name|rm
operator|.
name|ContainerAllocatorEvent
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
name|rm
operator|.
name|RMHeartbeatHandler
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|UserGroupInformation
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
name|factories
operator|.
name|RecordFactory
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
name|service
operator|.
name|AbstractService
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Make sure that the job staging directory clean up happens.  */
end_comment

begin_class
DECL|class|TestStagingCleanup
specifier|public
class|class
name|TestStagingCleanup
extends|extends
name|TestCase
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|stagingJobDir
specifier|private
name|String
name|stagingJobDir
init|=
literal|"tmpJobDir"
decl_stmt|;
DECL|field|stagingJobPath
specifier|private
name|Path
name|stagingJobPath
init|=
operator|new
name|Path
argument_list|(
name|stagingJobDir
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
specifier|static
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testDeletionofStaging ()
specifier|public
name|void
name|testDeletionofStaging
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAPREDUCE_JOB_DIR
argument_list|,
name|stagingJobDir
argument_list|)
expr_stmt|;
name|fs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//Staging Dir exists
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingDir
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|attemptId
operator|.
name|setAttemptId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|JobId
name|jobid
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobid
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ContainerAllocator
name|mockAlloc
init|=
name|mock
argument_list|(
name|ContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|,
name|mockAlloc
argument_list|,
name|JobStateInternal
operator|.
name|RUNNING
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_MAX_ATTEMPTS
argument_list|)
decl_stmt|;
name|appMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|appMaster
operator|.
name|start
argument_list|()
expr_stmt|;
name|appMaster
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
comment|//test whether notifyIsLastAMRetry called
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
operator|(
operator|(
name|TestMRApp
operator|)
name|appMaster
operator|)
operator|.
name|getTestIsLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|delete
argument_list|(
name|stagingJobPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testNoDeletionofStagingOnReboot ()
specifier|public
name|void
name|testNoDeletionofStagingOnReboot
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAPREDUCE_JOB_DIR
argument_list|,
name|stagingJobDir
argument_list|)
expr_stmt|;
name|fs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingDir
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|attemptId
operator|.
name|setAttemptId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ContainerAllocator
name|mockAlloc
init|=
name|mock
argument_list|(
name|ContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|,
name|mockAlloc
argument_list|,
name|JobStateInternal
operator|.
name|REBOOT
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|appMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|appMaster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//shutdown the job, not the lastRetry
name|appMaster
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
comment|//test whether notifyIsLastAMRetry called
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
operator|(
operator|(
name|TestMRApp
operator|)
name|appMaster
operator|)
operator|.
name|getTestIsLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|(
name|stagingJobPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDeletionofStagingOnReboot ()
specifier|public
name|void
name|testDeletionofStagingOnReboot
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAPREDUCE_JOB_DIR
argument_list|,
name|stagingJobDir
argument_list|)
expr_stmt|;
name|fs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingDir
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|attemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ContainerAllocator
name|mockAlloc
init|=
name|mock
argument_list|(
name|ContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|,
name|mockAlloc
argument_list|,
name|JobStateInternal
operator|.
name|REBOOT
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_MAX_ATTEMPTS
argument_list|)
decl_stmt|;
name|appMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|appMaster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//shutdown the job, is lastRetry
name|appMaster
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
comment|//test whether notifyIsLastAMRetry called
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
operator|(
operator|(
name|TestMRApp
operator|)
name|appMaster
operator|)
operator|.
name|getTestIsLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|delete
argument_list|(
name|stagingJobPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDeletionofStagingOnKill ()
specifier|public
name|void
name|testDeletionofStagingOnKill
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAPREDUCE_JOB_DIR
argument_list|,
name|stagingJobDir
argument_list|)
expr_stmt|;
name|fs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//Staging Dir exists
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingDir
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|attemptId
operator|.
name|setAttemptId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|JobId
name|jobid
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobid
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ContainerAllocator
name|mockAlloc
init|=
name|mock
argument_list|(
name|ContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|,
name|mockAlloc
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|appMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//simulate the process being killed
name|MRAppMaster
operator|.
name|MRAppMasterShutdownHook
name|hook
init|=
operator|new
name|MRAppMaster
operator|.
name|MRAppMasterShutdownHook
argument_list|(
name|appMaster
argument_list|)
decl_stmt|;
name|hook
operator|.
name|run
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|(
name|stagingJobPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDeletionofStagingOnKillLastTry ()
specifier|public
name|void
name|testDeletionofStagingOnKillLastTry
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAPREDUCE_JOB_DIR
argument_list|,
name|stagingJobDir
argument_list|)
expr_stmt|;
name|fs
operator|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//Staging Dir exists
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingDir
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|attemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|JobId
name|jobid
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobid
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ContainerAllocator
name|mockAlloc
init|=
name|mock
argument_list|(
name|ContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|,
name|mockAlloc
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_MAX_ATTEMPTS
argument_list|)
decl_stmt|;
name|appMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//simulate the process being killed
name|MRAppMaster
operator|.
name|MRAppMasterShutdownHook
name|hook
init|=
operator|new
name|MRAppMaster
operator|.
name|MRAppMasterShutdownHook
argument_list|(
name|appMaster
argument_list|)
decl_stmt|;
name|hook
operator|.
name|run
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|delete
argument_list|(
name|stagingJobPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|TestMRApp
specifier|private
class|class
name|TestMRApp
extends|extends
name|MRAppMaster
block|{
DECL|field|allocator
name|ContainerAllocator
name|allocator
decl_stmt|;
DECL|field|testIsLastAMRetry
name|boolean
name|testIsLastAMRetry
init|=
literal|false
decl_stmt|;
DECL|field|jobStateInternal
name|JobStateInternal
name|jobStateInternal
decl_stmt|;
DECL|method|TestMRApp (ApplicationAttemptId applicationAttemptId, ContainerAllocator allocator, int maxAppAttempts)
specifier|public
name|TestMRApp
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|ContainerAllocator
name|allocator
parameter_list|,
name|int
name|maxAppAttempts
parameter_list|)
block|{
name|super
argument_list|(
name|applicationAttemptId
argument_list|,
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"testhost"
argument_list|,
literal|2222
argument_list|,
literal|3333
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|maxAppAttempts
argument_list|)
expr_stmt|;
name|this
operator|.
name|allocator
operator|=
name|allocator
expr_stmt|;
block|}
DECL|method|TestMRApp (ApplicationAttemptId applicationAttemptId, ContainerAllocator allocator, JobStateInternal jobStateInternal, int maxAppAttempts)
specifier|public
name|TestMRApp
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|ContainerAllocator
name|allocator
parameter_list|,
name|JobStateInternal
name|jobStateInternal
parameter_list|,
name|int
name|maxAppAttempts
parameter_list|)
block|{
name|this
argument_list|(
name|applicationAttemptId
argument_list|,
name|allocator
argument_list|,
name|maxAppAttempts
argument_list|)
expr_stmt|;
name|this
operator|.
name|jobStateInternal
operator|=
name|jobStateInternal
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileSystem (Configuration conf)
specifier|protected
name|FileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|fs
return|;
block|}
annotation|@
name|Override
DECL|method|createContainerAllocator ( final ClientService clientService, final AppContext context)
specifier|protected
name|ContainerAllocator
name|createContainerAllocator
parameter_list|(
specifier|final
name|ClientService
name|clientService
parameter_list|,
specifier|final
name|AppContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|allocator
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|createContainerAllocator
argument_list|(
name|clientService
argument_list|,
name|context
argument_list|)
return|;
block|}
return|return
name|allocator
return|;
block|}
annotation|@
name|Override
DECL|method|createJob (Configuration conf, JobStateInternal forcedState, String diagnostic)
specifier|protected
name|Job
name|createJob
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobStateInternal
name|forcedState
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|JobImpl
name|jobImpl
init|=
name|mock
argument_list|(
name|JobImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|jobImpl
operator|.
name|getInternalState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|this
operator|.
name|jobStateInternal
argument_list|)
expr_stmt|;
name|JobID
name|jobID
init|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1234567890000_0001"
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|jobImpl
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AppContext
operator|)
name|getContext
argument_list|()
operator|)
operator|.
name|getAllJobs
argument_list|()
operator|.
name|put
argument_list|(
name|jobImpl
operator|.
name|getID
argument_list|()
argument_list|,
name|jobImpl
argument_list|)
expr_stmt|;
return|return
name|jobImpl
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|notifyIsLastAMRetry (boolean isLastAMRetry)
specifier|public
name|void
name|notifyIsLastAMRetry
parameter_list|(
name|boolean
name|isLastAMRetry
parameter_list|)
block|{
name|testIsLastAMRetry
operator|=
name|isLastAMRetry
expr_stmt|;
name|super
operator|.
name|notifyIsLastAMRetry
argument_list|(
name|isLastAMRetry
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMHeartbeatHandler ()
specifier|public
name|RMHeartbeatHandler
name|getRMHeartbeatHandler
parameter_list|()
block|{
return|return
name|getStubbedHeartbeatHandler
argument_list|(
name|getContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sysexit ()
specifier|protected
name|void
name|sysexit
parameter_list|()
block|{            }
annotation|@
name|Override
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|downloadTokensAndSetupUGI (Configuration conf)
specifier|protected
name|void
name|downloadTokensAndSetupUGI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{      }
DECL|method|getTestIsLastAMRetry ()
specifier|public
name|boolean
name|getTestIsLastAMRetry
parameter_list|()
block|{
return|return
name|testIsLastAMRetry
return|;
block|}
block|}
DECL|class|MRAppTestCleanup
specifier|private
specifier|final
class|class
name|MRAppTestCleanup
extends|extends
name|MRApp
block|{
DECL|field|stoppedContainerAllocator
name|boolean
name|stoppedContainerAllocator
decl_stmt|;
DECL|field|cleanedBeforeContainerAllocatorStopped
name|boolean
name|cleanedBeforeContainerAllocatorStopped
decl_stmt|;
DECL|method|MRAppTestCleanup (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart)
specifier|public
name|MRAppTestCleanup
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|boolean
name|autoComplete
parameter_list|,
name|String
name|testName
parameter_list|,
name|boolean
name|cleanOnStart
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
name|autoComplete
argument_list|,
name|testName
argument_list|,
name|cleanOnStart
argument_list|)
expr_stmt|;
name|stoppedContainerAllocator
operator|=
literal|false
expr_stmt|;
name|cleanedBeforeContainerAllocatorStopped
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createJob (Configuration conf, JobStateInternal forcedState, String diagnostic)
specifier|protected
name|Job
name|createJob
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobStateInternal
name|forcedState
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|UserGroupInformation
name|currentUser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Job
name|newJob
init|=
operator|new
name|TestJob
argument_list|(
name|getJobId
argument_list|()
argument_list|,
name|getAttemptID
argument_list|()
argument_list|,
name|conf
argument_list|,
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
argument_list|,
name|getTaskAttemptListener
argument_list|()
argument_list|,
name|getContext
argument_list|()
operator|.
name|getClock
argument_list|()
argument_list|,
name|getCommitter
argument_list|()
argument_list|,
name|isNewApiCommitter
argument_list|()
argument_list|,
name|currentUser
operator|.
name|getUserName
argument_list|()
argument_list|,
name|getContext
argument_list|()
argument_list|,
name|forcedState
argument_list|,
name|diagnostic
argument_list|)
decl_stmt|;
operator|(
operator|(
name|AppContext
operator|)
name|getContext
argument_list|()
operator|)
operator|.
name|getAllJobs
argument_list|()
operator|.
name|put
argument_list|(
name|newJob
operator|.
name|getID
argument_list|()
argument_list|,
name|newJob
argument_list|)
expr_stmt|;
name|getDispatcher
argument_list|()
operator|.
name|register
argument_list|(
name|JobFinishEvent
operator|.
name|Type
operator|.
name|class
argument_list|,
name|createJobFinishEventHandler
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newJob
return|;
block|}
annotation|@
name|Override
DECL|method|createContainerAllocator ( ClientService clientService, AppContext context)
specifier|protected
name|ContainerAllocator
name|createContainerAllocator
parameter_list|(
name|ClientService
name|clientService
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TestCleanupContainerAllocator
argument_list|()
return|;
block|}
DECL|class|TestCleanupContainerAllocator
specifier|private
class|class
name|TestCleanupContainerAllocator
extends|extends
name|AbstractService
implements|implements
name|ContainerAllocator
block|{
DECL|field|allocator
specifier|private
name|MRAppContainerAllocator
name|allocator
decl_stmt|;
DECL|method|TestCleanupContainerAllocator ()
name|TestCleanupContainerAllocator
parameter_list|()
block|{
name|super
argument_list|(
name|TestCleanupContainerAllocator
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|allocator
operator|=
operator|new
name|MRAppContainerAllocator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainerAllocatorEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerAllocatorEvent
name|event
parameter_list|)
block|{
name|allocator
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|stoppedContainerAllocator
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRMHeartbeatHandler ()
specifier|public
name|RMHeartbeatHandler
name|getRMHeartbeatHandler
parameter_list|()
block|{
return|return
name|getStubbedHeartbeatHandler
argument_list|(
name|getContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cleanupStagingDir ()
specifier|public
name|void
name|cleanupStagingDir
parameter_list|()
throws|throws
name|IOException
block|{
name|cleanedBeforeContainerAllocatorStopped
operator|=
operator|!
name|stoppedContainerAllocator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sysexit ()
specifier|protected
name|void
name|sysexit
parameter_list|()
block|{     }
block|}
DECL|method|getStubbedHeartbeatHandler ( final AppContext appContext)
specifier|private
specifier|static
name|RMHeartbeatHandler
name|getStubbedHeartbeatHandler
parameter_list|(
specifier|final
name|AppContext
name|appContext
parameter_list|)
block|{
return|return
operator|new
name|RMHeartbeatHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getLastHeartbeatTime
parameter_list|()
block|{
return|return
name|appContext
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|runOnNextHeartbeat
parameter_list|(
name|Runnable
name|callback
parameter_list|)
block|{
name|callback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testStagingCleanupOrder ()
specifier|public
name|void
name|testStagingCleanupOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|MRAppTestCleanup
name|app
init|=
operator|new
name|MRAppTestCleanup
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|JobImpl
name|job
init|=
operator|(
name|JobImpl
operator|)
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|app
operator|.
name|verifyCompleted
argument_list|()
expr_stmt|;
name|int
name|waitTime
init|=
literal|20
operator|*
literal|1000
decl_stmt|;
while|while
condition|(
name|waitTime
operator|>
literal|0
operator|&&
operator|!
name|app
operator|.
name|cleanedBeforeContainerAllocatorStopped
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|waitTime
operator|-=
literal|100
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Staging directory not cleaned before notifying RM"
argument_list|,
name|app
operator|.
name|cleanedBeforeContainerAllocatorStopped
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

