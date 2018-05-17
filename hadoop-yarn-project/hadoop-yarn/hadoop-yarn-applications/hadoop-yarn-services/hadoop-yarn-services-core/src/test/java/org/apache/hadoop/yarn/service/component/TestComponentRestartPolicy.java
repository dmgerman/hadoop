begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.component
package|package
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
name|component
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerStatus
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
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|when
import|;
end_import

begin_comment
comment|/**  * Tests for ComponentRestartPolicy implementations.  */
end_comment

begin_class
DECL|class|TestComponentRestartPolicy
specifier|public
class|class
name|TestComponentRestartPolicy
block|{
annotation|@
name|Test
DECL|method|testAlwaysRestartPolicy ()
specifier|public
name|void
name|testAlwaysRestartPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|AlwaysRestartPolicy
name|alwaysRestartPolicy
init|=
name|AlwaysRestartPolicy
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|mock
argument_list|(
name|Component
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumReadyInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumDesiredInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ComponentInstance
name|instance
init|=
name|mock
argument_list|(
name|ComponentInstance
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|instance
operator|.
name|getComponent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|alwaysRestartPolicy
operator|.
name|isLongLived
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|alwaysRestartPolicy
operator|.
name|allowUpgrades
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|alwaysRestartPolicy
operator|.
name|hasCompleted
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|alwaysRestartPolicy
operator|.
name|hasCompletedSuccessfully
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|alwaysRestartPolicy
operator|.
name|shouldRelaunchInstance
argument_list|(
name|instance
argument_list|,
name|containerStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|alwaysRestartPolicy
operator|.
name|isReadyForDownStream
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNeverRestartPolicy ()
specifier|public
name|void
name|testNeverRestartPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|NeverRestartPolicy
name|restartPolicy
init|=
name|NeverRestartPolicy
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|mock
argument_list|(
name|Component
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumSucceededInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumFailedInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumDesiredInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ComponentInstance
name|instance
init|=
name|mock
argument_list|(
name|ComponentInstance
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|instance
operator|.
name|getComponent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|isLongLived
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|allowUpgrades
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|hasCompleted
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|hasCompletedSuccessfully
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|shouldRelaunchInstance
argument_list|(
name|instance
argument_list|,
name|containerStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|isReadyForDownStream
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnFailureRestartPolicy ()
specifier|public
name|void
name|testOnFailureRestartPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|OnFailureRestartPolicy
name|restartPolicy
init|=
name|OnFailureRestartPolicy
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|mock
argument_list|(
name|Component
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumSucceededInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumFailedInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumDesiredInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ComponentInstance
name|instance
init|=
name|mock
argument_list|(
name|ComponentInstance
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|instance
operator|.
name|getComponent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|containerStatus
operator|.
name|getExitStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|isLongLived
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|allowUpgrades
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|hasCompleted
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|hasCompletedSuccessfully
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|shouldRelaunchInstance
argument_list|(
name|instance
argument_list|,
name|containerStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|isReadyForDownStream
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumSucceededInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumFailedInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Long
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|component
operator|.
name|getNumDesiredInstances
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|hasCompleted
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|hasCompletedSuccessfully
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|containerStatus
operator|.
name|getExitStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|-
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|restartPolicy
operator|.
name|shouldRelaunchInstance
argument_list|(
name|instance
argument_list|,
name|containerStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|restartPolicy
operator|.
name|isReadyForDownStream
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

