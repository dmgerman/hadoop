begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|hadoop
operator|.
name|mapred
operator|.
name|FakeObjectUtilities
operator|.
name|FakeJobTracker
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
name|TestRackAwareTaskPlacement
operator|.
name|MyFakeJobInProgress
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
name|UtilsForTests
operator|.
name|FakeClock
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test that killing completed jobs does not move them  * to the failed sate - See JIRA HADOOP-2132  */
end_comment

begin_class
DECL|class|TestKillCompletedJob
specifier|public
class|class
name|TestKillCompletedJob
extends|extends
name|TestCase
block|{
DECL|field|job
name|MyFakeJobInProgress
name|job
decl_stmt|;
DECL|field|jobTracker
specifier|static
name|FakeJobTracker
name|jobTracker
decl_stmt|;
DECL|field|clock
specifier|static
name|FakeClock
name|clock
decl_stmt|;
DECL|field|trackers
specifier|static
name|String
name|trackers
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"tracker_tracker1:1000"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_IPC_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_HTTP_ADDRESS
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_TRACKER_EXPIRY_INTERVAL
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|jobTracker
operator|=
operator|new
name|FakeJobTracker
argument_list|(
name|conf
argument_list|,
operator|(
name|clock
operator|=
operator|new
name|FakeClock
argument_list|()
operator|)
argument_list|,
name|trackers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testKillCompletedJob ()
specifier|public
name|void
name|testKillCompletedJob
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|job
operator|=
operator|new
name|MyFakeJobInProgress
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|,
name|jobTracker
argument_list|)
expr_stmt|;
name|jobTracker
operator|.
name|addJob
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
operator|(
name|JobInProgress
operator|)
name|job
argument_list|)
expr_stmt|;
name|job
operator|.
name|status
operator|.
name|setRunState
argument_list|(
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|jobTracker
operator|.
name|killJob
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Run state changed when killing completed job"
argument_list|,
name|job
operator|.
name|status
operator|.
name|getRunState
argument_list|()
operator|==
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

