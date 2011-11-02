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
name|TestCase
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
name|TestStagingCleanup
operator|.
name|class
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
name|MRAppMaster
name|appMaster
init|=
operator|new
name|TestMRApp
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
name|EventHandler
argument_list|<
name|JobFinishEvent
argument_list|>
name|handler
init|=
name|appMaster
operator|.
name|createJobFinishEventHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handle
argument_list|(
operator|new
name|JobFinishEvent
argument_list|(
name|jobid
argument_list|)
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
DECL|class|TestMRApp
specifier|private
class|class
name|TestMRApp
extends|extends
name|MRAppMaster
block|{
DECL|method|TestMRApp (ApplicationAttemptId applicationAttemptId)
specifier|public
name|TestMRApp
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
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
argument_list|)
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
DECL|method|sysexit ()
specifier|protected
name|void
name|sysexit
parameter_list|()
block|{           }
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
block|}
block|}
end_class

end_unit

