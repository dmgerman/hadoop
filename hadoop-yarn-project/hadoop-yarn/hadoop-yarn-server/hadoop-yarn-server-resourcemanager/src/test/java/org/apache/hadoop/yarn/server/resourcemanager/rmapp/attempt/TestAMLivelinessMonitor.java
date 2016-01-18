begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
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
name|service
operator|.
name|Service
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
name|conf
operator|.
name|YarnConfiguration
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
name|Dispatcher
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
name|server
operator|.
name|resourcemanager
operator|.
name|MockRM
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|MemoryRMStateStore
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
name|ControlledClock
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
name|SystemClock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_class
DECL|class|TestAMLivelinessMonitor
specifier|public
class|class
name|TestAMLivelinessMonitor
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testResetTimer ()
specifier|public
name|void
name|testResetTimer
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_STORE
argument_list|,
name|MemoryRMStateStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WORK_PRESERVING_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_EXPIRY_INTERVAL_MS
argument_list|,
literal|6000
argument_list|)
expr_stmt|;
specifier|final
name|ControlledClock
name|clock
init|=
operator|new
name|ControlledClock
argument_list|()
decl_stmt|;
name|clock
operator|.
name|setTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RMState
name|loadState
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|setTime
argument_list|(
literal|8000
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|loadState
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|ApplicationAttemptId
name|attemptId
init|=
name|mock
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Dispatcher
name|dispatcher
init|=
name|mock
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|boolean
index|[]
name|expired
init|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|}
decl_stmt|;
specifier|final
name|AMLivelinessMonitor
name|monitor
init|=
operator|new
name|AMLivelinessMonitor
argument_list|(
name|dispatcher
argument_list|,
name|clock
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|expire
parameter_list|(
name|ApplicationAttemptId
name|id
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|id
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|expired
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
decl_stmt|;
name|monitor
operator|.
name|register
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|AMLivelinessMonitor
name|createAMLivelinessMonitor
parameter_list|()
block|{
return|return
name|monitor
return|;
block|}
block|}
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// make sure that monitor has started
while|while
condition|(
name|monitor
operator|.
name|getServiceState
argument_list|()
operator|!=
name|Service
operator|.
name|STATE
operator|.
name|STARTED
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// expired[0] would be set to true without resetTimer
name|Assert
operator|.
name|assertFalse
argument_list|(
name|expired
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

