begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.local
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
name|local
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
name|isA
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
name|yarn
operator|.
name|ClusterInfo
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
name|ApplicationMasterProtocol
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|Resource
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
name|exceptions
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|ipc
operator|.
name|RPCUtil
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

begin_class
DECL|class|TestLocalContainerAllocator
specifier|public
class|class
name|TestLocalContainerAllocator
block|{
annotation|@
name|Test
DECL|method|testRMConnectionRetry ()
specifier|public
name|void
name|testRMConnectionRetry
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify the connection exception is thrown
comment|// if we haven't exhausted the retry interval
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LocalContainerAllocator
name|lca
init|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|()
decl_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"heartbeat was supposed to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
comment|// YarnException is expected
block|}
finally|finally
block|{
name|lca
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// verify YarnRuntimeException is thrown when the retry interval has expired
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_TO_RM_WAIT_INTERVAL_MS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|lca
operator|=
operator|new
name|StubbedLocalContainerAllocator
argument_list|()
expr_stmt|;
name|lca
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lca
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|lca
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"heartbeat was supposed to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
comment|// YarnRuntimeException is expected
block|}
finally|finally
block|{
name|lca
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|StubbedLocalContainerAllocator
specifier|private
specifier|static
class|class
name|StubbedLocalContainerAllocator
extends|extends
name|LocalContainerAllocator
block|{
DECL|method|StubbedLocalContainerAllocator ()
specifier|public
name|StubbedLocalContainerAllocator
parameter_list|()
block|{
name|super
argument_list|(
name|mock
argument_list|(
name|ClientService
operator|.
name|class
argument_list|)
argument_list|,
name|createAppContext
argument_list|()
argument_list|,
literal|"nmhost"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register ()
specifier|protected
name|void
name|register
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|startAllocatorThread ()
specifier|protected
name|void
name|startAllocatorThread
parameter_list|()
block|{
name|allocatorThread
operator|=
operator|new
name|Thread
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSchedulerProxy ()
specifier|protected
name|ApplicationMasterProtocol
name|createSchedulerProxy
parameter_list|()
block|{
name|ApplicationMasterProtocol
name|scheduler
init|=
name|mock
argument_list|(
name|ApplicationMasterProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|when
argument_list|(
name|scheduler
operator|.
name|allocate
argument_list|(
name|isA
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"forcefail"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
return|return
name|scheduler
return|;
block|}
DECL|method|createAppContext ()
specifier|private
specifier|static
name|AppContext
name|createAppContext
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
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
name|Job
name|job
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|EventHandler
name|eventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|AppContext
name|ctx
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getJob
argument_list|(
name|isA
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getClusterInfo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ClusterInfo
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|10240
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ctx
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|eventHandler
argument_list|)
expr_stmt|;
return|return
name|ctx
return|;
block|}
block|}
block|}
end_class

end_unit

