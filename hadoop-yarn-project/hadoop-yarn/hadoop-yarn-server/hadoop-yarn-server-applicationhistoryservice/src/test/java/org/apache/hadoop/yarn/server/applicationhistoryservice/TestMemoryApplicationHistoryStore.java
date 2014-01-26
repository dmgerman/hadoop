begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|Assert
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|Priority
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptHistoryData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationHistoryData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerHistoryData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|TestMemoryApplicationHistoryStore
specifier|public
class|class
name|TestMemoryApplicationHistoryStore
extends|extends
name|ApplicationHistoryStoreTestUtils
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|store
operator|=
operator|new
name|MemoryApplicationHistoryStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteApplicationHistory ()
specifier|public
name|void
name|testReadWriteApplicationHistory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Out of order
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|writeApplicationFinishData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is stored before the start information"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Normal
name|int
name|numApps
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numApps
condition|;
operator|++
name|i
control|)
block|{
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|writeApplicationFinishData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numApps
argument_list|,
name|store
operator|.
name|getAllApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numApps
condition|;
operator|++
name|i
control|)
block|{
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ApplicationHistoryData
name|data
init|=
name|store
operator|.
name|getApplication
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
operator|.
name|getApplicationName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Write again
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|writeApplicationStartData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writeApplicationFinishData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadWriteApplicationAttemptHistory ()
specifier|public
name|void
name|testReadWriteApplicationAttemptHistory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Out of order
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|writeApplicationAttemptFinishData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is stored before the start information"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Normal
name|int
name|numAppAttempts
init|=
literal|5
decl_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numAppAttempts
condition|;
operator|++
name|i
control|)
block|{
name|appAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|writeApplicationAttemptStartData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|writeApplicationAttemptFinishData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numAppAttempts
argument_list|,
name|store
operator|.
name|getApplicationAttempts
argument_list|(
name|appId
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numAppAttempts
condition|;
operator|++
name|i
control|)
block|{
name|appAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ApplicationAttemptHistoryData
name|data
init|=
name|store
operator|.
name|getApplicationAttempt
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeApplicationFinishData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
comment|// Write again
name|appAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|writeApplicationAttemptStartData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writeApplicationAttemptFinishData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadWriteContainerHistory ()
specifier|public
name|void
name|testReadWriteContainerHistory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Out of order
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|writeContainerFinishData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is stored before the start information"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Normal
name|writeApplicationAttemptStartData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|int
name|numContainers
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numContainers
condition|;
operator|++
name|i
control|)
block|{
name|containerId
operator|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|writeContainerStartData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|writeContainerFinishData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numContainers
argument_list|,
name|store
operator|.
name|getContainers
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numContainers
condition|;
operator|++
name|i
control|)
block|{
name|containerId
operator|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|ContainerHistoryData
name|data
init|=
name|store
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
name|containerId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|data
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ContainerHistoryData
name|masterContainer
init|=
name|store
operator|.
name|getAMContainer
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|masterContainer
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
argument_list|,
name|masterContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|writeApplicationAttemptFinishData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
comment|// Write again
name|containerId
operator|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|writeContainerStartData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writeContainerFinishData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is already stored"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMassiveWriteContainerHistory ()
specifier|public
name|void
name|testMassiveWriteContainerHistory
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|mb
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|long
name|usedMemoryBefore
init|=
operator|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
operator|)
operator|/
name|mb
decl_stmt|;
name|int
name|numContainers
init|=
literal|100000
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numContainers
condition|;
operator|++
name|i
control|)
block|{
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|writeContainerStartData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|writeContainerFinishData
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
name|long
name|usedMemoryAfter
init|=
operator|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
operator|)
operator|/
name|mb
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|usedMemoryAfter
operator|-
name|usedMemoryBefore
operator|)
operator|<
literal|200
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

