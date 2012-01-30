begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|spy
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
name|CommonConfigurationKeysPublic
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|TestNodeFencer
operator|.
name|AlwaysSucceedFencer
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
name|ha
operator|.
name|TestNodeFencer
operator|.
name|AlwaysFailFencer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
operator|.
name|TestNodeFencer
operator|.
name|setupFencer
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
name|ipc
operator|.
name|ProtocolSignature
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
name|ipc
operator|.
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|*
import|;
end_import

begin_class
DECL|class|TestFailoverController
specifier|public
class|class
name|TestFailoverController
block|{
DECL|field|svc1Addr
specifier|private
name|InetSocketAddress
name|svc1Addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"svc1"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
DECL|field|svc2Addr
specifier|private
name|InetSocketAddress
name|svc2Addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"svc2"
argument_list|,
literal|5678
argument_list|)
decl_stmt|;
DECL|class|DummyService
specifier|private
class|class
name|DummyService
implements|implements
name|HAServiceProtocol
block|{
DECL|field|state
name|HAServiceState
name|state
decl_stmt|;
DECL|method|DummyService (HAServiceState state)
name|DummyService
parameter_list|(
name|HAServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|monitorHealth ()
specifier|public
name|void
name|monitorHealth
parameter_list|()
throws|throws
name|HealthCheckFailedException
throws|,
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|transitionToActive ()
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
throws|,
name|IOException
block|{
name|state
operator|=
name|HAServiceState
operator|.
name|ACTIVE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transitionToStandby ()
specifier|public
name|void
name|transitionToStandby
parameter_list|()
throws|throws
name|ServiceFailedException
throws|,
name|IOException
block|{
name|state
operator|=
name|HAServiceState
operator|.
name|STANDBY
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServiceState ()
specifier|public
name|HAServiceState
name|getServiceState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|state
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailoverAndFailback ()
specifier|public
name|void
name|testFailoverAndFailback
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|TestNodeFencer
operator|.
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|TestNodeFencer
operator|.
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverFromStandbyToStandby ()
specifier|public
name|void
name|testFailoverFromStandbyToStandby
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverFromActiveToActive ()
specifier|public
name|void
name|testFailoverFromActiveToActive
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Can't failover to an already active service"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverToUnhealthyServiceFailsAndFailsback ()
specifier|public
name|void
name|testFailoverToUnhealthyServiceFailsAndFailsback
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|monitorHealth
parameter_list|()
throws|throws
name|HealthCheckFailedException
block|{
throw|throw
operator|new
name|HealthCheckFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failover to unhealthy service"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverFromFaultyServiceSucceeds ()
specifier|public
name|void
name|testFailoverFromFaultyServiceSucceeds
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToStandby
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Faulty active prevented failover"
argument_list|)
expr_stmt|;
block|}
comment|// svc1 still thinks it's active, that's OK, it was fenced
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"svc1:1234"
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverFromFaultyServiceFencingFailure ()
specifier|public
name|void
name|testFailoverFromFaultyServiceFencingFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToStandby
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysFailFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysFailFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over even though fencing failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysFailFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"svc1:1234"
argument_list|,
name|AlwaysFailFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFencingFailureDuringFailover ()
specifier|public
name|void
name|testFencingFailureDuringFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysFailFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysFailFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over even though fencing requested and failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
comment|// If fencing was requested and it failed we don't try to make
comment|// svc2 active anyway, and we don't failback to svc1.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysFailFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"svc1:1234"
argument_list|,
name|AlwaysFailFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getProtocol (String target)
specifier|private
name|HAServiceProtocol
name|getProtocol
parameter_list|(
name|String
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Lower the timeout so we quickly fail to connect
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
operator|(
name|HAServiceProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|HAServiceProtocol
operator|.
name|class
argument_list|,
name|HAServiceProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testFailoverFromNonExistantServiceWithFencer ()
specifier|public
name|void
name|testFailoverFromNonExistantServiceWithFencer
parameter_list|()
throws|throws
name|Exception
block|{
name|HAServiceProtocol
name|svc1
init|=
name|getProtocol
argument_list|(
literal|"localhost:1234"
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Non-existant active prevented failover"
argument_list|)
expr_stmt|;
block|}
comment|// Don't check svc1 because we can't reach it, but that's OK, it's been fenced.
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverToNonExistantServiceFails ()
specifier|public
name|void
name|testFailoverToNonExistantServiceFails
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|HAServiceProtocol
name|svc2
init|=
name|getProtocol
argument_list|(
literal|"localhost:1234"
argument_list|)
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over to a non-existant standby"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverToFaultyServiceFailsbackOK ()
specifier|public
name|void
name|testFailoverToFaultyServiceFailsbackOK
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
name|spy
argument_list|(
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failover to already active service"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
comment|// svc1 went standby then back to active
name|verify
argument_list|(
name|svc1
argument_list|)
operator|.
name|transitionToStandby
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|svc1
argument_list|)
operator|.
name|transitionToActive
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWeDontFailbackIfActiveWasFenced ()
specifier|public
name|void
name|testWeDontFailbackIfActiveWasFenced
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over to service that won't transition to active"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
comment|// We failed to failover and did not failback because we fenced
comment|// svc1 (we forced it), therefore svc1 and svc2 should be standby.
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWeFenceOnFailbackIfTransitionToActiveFails ()
specifier|public
name|void
name|testWeFenceOnFailbackIfTransitionToActiveFails
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over to service that won't transition to active"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
comment|// We failed to failover. We did not fence svc1 because it cooperated
comment|// and we didn't force it, so we failed back to svc1 and fenced svc2.
comment|// Note svc2 still thinks it's active, that's OK, we fenced it.
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"svc2:5678"
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureToFenceOnFailbackFailsTheFailback ()
specifier|public
name|void
name|testFailureToFenceOnFailbackFailsTheFailback
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysFailFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AlwaysFailFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed over to service that won't transition to active"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
comment|// We did not fence svc1 because it cooperated and we didn't force it,
comment|// we failed to failover so we fenced svc2, we failed to fence svc2
comment|// so we did not failback to svc1, ie it's still standby.
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysFailFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"svc2:5678"
argument_list|,
name|AlwaysFailFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailbackToFaultyServiceFails ()
specifier|public
name|void
name|testFailbackToFaultyServiceFails
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyService
name|svc1
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|DummyService
name|svc2
init|=
operator|new
name|DummyService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transitionToActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed!"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|svc1
argument_list|,
name|svc1Addr
argument_list|,
name|svc2
argument_list|,
name|svc2Addr
argument_list|,
name|fencer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failover to already active service"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
comment|// Expected
block|}
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc1
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|svc2
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

