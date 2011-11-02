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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|*
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
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|TaskAttemptId
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
name|TaskHeartbeatHandler
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
DECL|class|TestTaskAttemptListenerImpl
specifier|public
class|class
name|TestTaskAttemptListenerImpl
block|{
DECL|class|MockTaskAttemptListenerImpl
specifier|public
specifier|static
class|class
name|MockTaskAttemptListenerImpl
extends|extends
name|TaskAttemptListenerImpl
block|{
DECL|method|MockTaskAttemptListenerImpl (AppContext context, JobTokenSecretManager jobTokenSecretManager, TaskHeartbeatHandler hbHandler)
specifier|public
name|MockTaskAttemptListenerImpl
parameter_list|(
name|AppContext
name|context
parameter_list|,
name|JobTokenSecretManager
name|jobTokenSecretManager
parameter_list|,
name|TaskHeartbeatHandler
name|hbHandler
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|jobTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskHeartbeatHandler
operator|=
name|hbHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerHeartbeatHandler ()
specifier|protected
name|void
name|registerHeartbeatHandler
parameter_list|()
block|{
comment|//Empty
block|}
annotation|@
name|Override
DECL|method|startRpcServer ()
specifier|protected
name|void
name|startRpcServer
parameter_list|()
block|{
comment|//Empty
block|}
annotation|@
name|Override
DECL|method|stopRpcServer ()
specifier|protected
name|void
name|stopRpcServer
parameter_list|()
block|{
comment|//Empty
block|}
block|}
annotation|@
name|Test
DECL|method|testGetTask ()
specifier|public
name|void
name|testGetTask
parameter_list|()
throws|throws
name|IOException
block|{
name|AppContext
name|appCtx
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobTokenSecretManager
name|secret
init|=
name|mock
argument_list|(
name|JobTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|TaskHeartbeatHandler
name|hbHandler
init|=
name|mock
argument_list|(
name|TaskHeartbeatHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|MockTaskAttemptListenerImpl
name|listener
init|=
operator|new
name|MockTaskAttemptListenerImpl
argument_list|(
name|appCtx
argument_list|,
name|secret
argument_list|,
name|hbHandler
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|listener
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|listener
operator|.
name|start
argument_list|()
expr_stmt|;
name|JVMId
name|id
init|=
operator|new
name|JVMId
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|WrappedJvmID
name|wid
init|=
operator|new
name|WrappedJvmID
argument_list|(
name|id
operator|.
name|getJobId
argument_list|()
argument_list|,
name|id
operator|.
name|isMap
argument_list|,
name|id
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|//The JVM ID has not been registered yet so we should kill it.
name|JvmContext
name|context
init|=
operator|new
name|JvmContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|jvmId
operator|=
name|id
expr_stmt|;
name|JvmTask
name|result
init|=
name|listener
operator|.
name|getTask
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|shouldDie
argument_list|)
expr_stmt|;
comment|//Now register the JVM, and see
name|listener
operator|.
name|registerPendingTask
argument_list|(
name|wid
argument_list|)
expr_stmt|;
name|result
operator|=
name|listener
operator|.
name|getTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|TaskAttemptId
name|attemptID
init|=
name|mock
argument_list|(
name|TaskAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Now put a task with the ID
name|listener
operator|.
name|registerLaunchedTask
argument_list|(
name|attemptID
argument_list|,
name|task
argument_list|,
name|wid
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|hbHandler
argument_list|)
operator|.
name|register
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|result
operator|=
name|listener
operator|.
name|getTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|shouldDie
argument_list|)
expr_stmt|;
comment|//Verify that if we call it again a second time we are told to die.
name|result
operator|=
name|listener
operator|.
name|getTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|shouldDie
argument_list|)
expr_stmt|;
name|listener
operator|.
name|unregister
argument_list|(
name|attemptID
argument_list|,
name|wid
argument_list|)
expr_stmt|;
name|listener
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

