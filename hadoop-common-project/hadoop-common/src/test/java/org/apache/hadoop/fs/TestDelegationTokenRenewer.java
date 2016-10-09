begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|DelegationTokenRenewer
operator|.
name|Renewable
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
name|io
operator|.
name|Text
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
name|token
operator|.
name|Token
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
name|util
operator|.
name|Time
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestDelegationTokenRenewer
specifier|public
class|class
name|TestDelegationTokenRenewer
block|{
DECL|class|RenewableFileSystem
specifier|public
specifier|abstract
class|class
name|RenewableFileSystem
extends|extends
name|FileSystem
implements|implements
name|Renewable
block|{ }
DECL|field|RENEW_CYCLE
specifier|private
specifier|static
specifier|final
name|long
name|RENEW_CYCLE
init|=
literal|1000
decl_stmt|;
DECL|field|renewer
specifier|private
name|DelegationTokenRenewer
name|renewer
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|DelegationTokenRenewer
operator|.
name|renewCycle
operator|=
name|RENEW_CYCLE
expr_stmt|;
name|DelegationTokenRenewer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|renewer
operator|=
name|DelegationTokenRenewer
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddRemoveRenewAction ()
specifier|public
name|void
name|testAddRemoveRenewAction
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Text
name|service
init|=
operator|new
name|Text
argument_list|(
literal|"myservice"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|service
argument_list|)
operator|.
name|when
argument_list|(
name|token
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|+
name|RENEW_CYCLE
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|token
argument_list|)
operator|.
name|renew
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|RenewableFileSystem
name|fs
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FileSystem not added to DelegationTokenRenewer"
argument_list|,
literal|1
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|RENEW_CYCLE
operator|*
literal|2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atLeast
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atMost
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|cancel
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|removeRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|)
operator|.
name|cancel
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FileSystem not removed from DelegationTokenRenewer"
argument_list|,
literal|0
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddRenewActionWithNoToken ()
specifier|public
name|void
name|testAddRenewActionWithNoToken
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|RenewableFileSystem
name|fs
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNewTokenOnRenewFailure ()
specifier|public
name|void
name|testGetNewTokenOnRenewFailure
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Text
name|service
init|=
operator|new
name|Text
argument_list|(
literal|"myservice"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token1
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|service
argument_list|)
operator|.
name|when
argument_list|(
name|token1
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"boom"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|token1
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token2
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|service
argument_list|)
operator|.
name|when
argument_list|(
name|token2
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|+
name|RENEW_CYCLE
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|token2
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|RenewableFileSystem
name|fs
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token1
argument_list|)
operator|.
name|doReturn
argument_list|(
name|token2
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token2
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
return|return
operator|new
name|Token
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|token2
block|}
empty_stmt|;
block|}
block|}
block|)
function|.when
parameter_list|(
name|fs
parameter_list|)
function|.addDelegationTokens
parameter_list|(
function|null
operator|,
function|null
block|)
class|;
end_class

begin_expr_stmt
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Thread
operator|.
name|sleep
argument_list|(
name|RENEW_CYCLE
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|token1
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|token1
argument_list|,
name|atMost
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|addDelegationTokens
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setDelegationToken
argument_list|(
name|eq
argument_list|(
name|token2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|renewer
operator|.
name|removeRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|verify
argument_list|(
name|token2
argument_list|)
operator|.
name|cancel
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    @
name|Test
DECL|method|testStopRenewalWhenFsGone ()
specifier|public
name|void
name|testStopRenewalWhenFsGone
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
operator|new
name|Text
argument_list|(
literal|"myservice"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|token
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|+
name|RENEW_CYCLE
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|token
argument_list|)
operator|.
name|renew
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|RenewableFileSystem
name|fs
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|RENEW_CYCLE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atMost
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// drop weak ref
name|fs
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// next renew should detect the fs as gone
name|Thread
operator|.
name|sleep
argument_list|(
name|RENEW_CYCLE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token
argument_list|,
name|atMost
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|4000
argument_list|)
DECL|method|testMultipleTokensDoNotDeadlock ()
specifier|public
name|void
name|testMultipleTokensDoNotDeadlock
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|long
name|distantFuture
init|=
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|3600
operator|*
literal|1000
decl_stmt|;
comment|// 1h
name|Token
argument_list|<
name|?
argument_list|>
name|token1
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
operator|new
name|Text
argument_list|(
literal|"myservice1"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|token1
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|distantFuture
argument_list|)
operator|.
name|when
argument_list|(
name|token1
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token2
init|=
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
operator|new
name|Text
argument_list|(
literal|"myservice2"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|token2
argument_list|)
operator|.
name|getService
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|distantFuture
argument_list|)
operator|.
name|when
argument_list|(
name|token2
argument_list|)
operator|.
name|renew
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|RenewableFileSystem
name|fs1
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs1
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token1
argument_list|)
operator|.
name|when
argument_list|(
name|fs1
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|RenewableFileSystem
name|fs2
init|=
name|mock
argument_list|(
name|RenewableFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|fs2
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|token2
argument_list|)
operator|.
name|when
argument_list|(
name|fs2
argument_list|)
operator|.
name|getRenewToken
argument_list|()
expr_stmt|;
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs1
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|addRenewAction
argument_list|(
name|fs2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|removeRenewAction
argument_list|(
name|fs1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|renewer
operator|.
name|removeRenewAction
argument_list|(
name|fs2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|renewer
operator|.
name|getRenewQueueLength
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token1
argument_list|)
operator|.
name|cancel
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|token2
argument_list|)
operator|.
name|cancel
argument_list|(
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

