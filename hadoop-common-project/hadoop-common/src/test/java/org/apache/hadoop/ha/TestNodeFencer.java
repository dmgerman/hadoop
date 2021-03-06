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
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|conf
operator|.
name|Configured
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
name|Shell
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
name|Mockito
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
DECL|class|TestNodeFencer
specifier|public
class|class
name|TestNodeFencer
block|{
DECL|field|MOCK_TARGET
specifier|private
name|HAServiceTarget
name|MOCK_TARGET
decl_stmt|;
comment|// Fencer shell commands that always return true on Unix and Windows
comment|// respectively. Lacking the POSIX 'true' command on Windows, we use
comment|// the batch command 'rem'.
DECL|field|FENCER_TRUE_COMMAND_UNIX
specifier|private
specifier|static
name|String
name|FENCER_TRUE_COMMAND_UNIX
init|=
literal|"shell(true)"
decl_stmt|;
DECL|field|FENCER_TRUE_COMMAND_WINDOWS
specifier|private
specifier|static
name|String
name|FENCER_TRUE_COMMAND_WINDOWS
init|=
literal|"shell(rem)"
decl_stmt|;
annotation|@
name|Before
DECL|method|clearMockState ()
specifier|public
name|void
name|clearMockState
parameter_list|()
block|{
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
name|AlwaysSucceedFencer
operator|.
name|callArgs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|AlwaysFailFencer
operator|.
name|fenceCalled
operator|=
literal|0
expr_stmt|;
name|AlwaysFailFencer
operator|.
name|callArgs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|MOCK_TARGET
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HAServiceTarget
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"my mock"
argument_list|)
operator|.
name|when
argument_list|(
name|MOCK_TARGET
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"host"
argument_list|,
literal|1234
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|MOCK_TARGET
argument_list|)
operator|.
name|getAddress
argument_list|()
expr_stmt|;
block|}
DECL|method|getFencerTrueCommand ()
specifier|private
specifier|static
name|String
name|getFencerTrueCommand
parameter_list|()
block|{
return|return
name|Shell
operator|.
name|WINDOWS
condition|?
name|FENCER_TRUE_COMMAND_WINDOWS
else|:
name|FENCER_TRUE_COMMAND_UNIX
return|;
block|}
annotation|@
name|Test
DECL|method|testSingleFencer ()
specifier|public
name|void
name|testSingleFencer
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
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
operator|+
literal|"(foo)"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
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
name|assertSame
argument_list|(
name|MOCK_TARGET
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|callArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleFencers ()
specifier|public
name|void
name|testMultipleFencers
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
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
operator|+
literal|"(foo)\n"
operator|+
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"(bar)\n"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
comment|// Only one call, since the first fencer succeeds
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
literal|"foo"
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|callArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWhitespaceAndCommentsInConfig ()
specifier|public
name|void
name|testWhitespaceAndCommentsInConfig
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
literal|"\n"
operator|+
literal|" # the next one will always fail\n"
operator|+
literal|" "
operator|+
name|AlwaysFailFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"(foo) #<- fails\n"
operator|+
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"(bar) \n"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
comment|// One call to each, since top fencer fails
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysFailFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|MOCK_TARGET
argument_list|,
name|AlwaysFailFencer
operator|.
name|fencedSvc
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
name|assertSame
argument_list|(
name|MOCK_TARGET
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|AlwaysFailFencer
operator|.
name|callArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|callArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArglessFencer ()
specifier|public
name|void
name|testArglessFencer
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
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
name|assertTrue
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
comment|// One call to each, since top fencer fails
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fenceCalled
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|MOCK_TARGET
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|fencedSvc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|callArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortNameShell ()
specifier|public
name|void
name|testShortNameShell
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
name|getFencerTrueCommand
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortNameSsh ()
specifier|public
name|void
name|testShortNameSsh
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
literal|"sshfence"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortNameSshWithUser ()
specifier|public
name|void
name|testShortNameSshWithUser
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
literal|"sshfence(user)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortNameSshWithPort ()
specifier|public
name|void
name|testShortNameSshWithPort
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
literal|"sshfence(:123)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortNameSshWithUserPort ()
specifier|public
name|void
name|testShortNameSshWithUserPort
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|NodeFencer
name|fencer
init|=
name|setupFencer
argument_list|(
literal|"sshfence(user:123)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fencer
operator|.
name|fence
argument_list|(
name|MOCK_TARGET
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setupFencer (String confStr)
specifier|public
specifier|static
name|NodeFencer
name|setupFencer
parameter_list|(
name|String
name|confStr
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Testing configuration:\n"
operator|+
name|confStr
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|NodeFencer
argument_list|(
name|conf
argument_list|,
name|confStr
argument_list|)
return|;
block|}
comment|/**    * Mock fencing method that always returns true    */
DECL|class|AlwaysSucceedFencer
specifier|public
specifier|static
class|class
name|AlwaysSucceedFencer
extends|extends
name|Configured
implements|implements
name|FenceMethod
block|{
DECL|field|fenceCalled
specifier|static
name|int
name|fenceCalled
init|=
literal|0
decl_stmt|;
DECL|field|fencedSvc
specifier|static
name|HAServiceTarget
name|fencedSvc
decl_stmt|;
DECL|field|callArgs
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|callArgs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|tryFence (HAServiceTarget target, String args)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|fencedSvc
operator|=
name|target
expr_stmt|;
name|callArgs
operator|.
name|add
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fenceCalled
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|checkArgs (String args)
specifier|public
name|void
name|checkArgs
parameter_list|(
name|String
name|args
parameter_list|)
block|{     }
DECL|method|getLastFencedService ()
specifier|public
specifier|static
name|HAServiceTarget
name|getLastFencedService
parameter_list|()
block|{
return|return
name|fencedSvc
return|;
block|}
block|}
comment|/**    * Identical mock to above, except always returns false    */
DECL|class|AlwaysFailFencer
specifier|public
specifier|static
class|class
name|AlwaysFailFencer
extends|extends
name|Configured
implements|implements
name|FenceMethod
block|{
DECL|field|fenceCalled
specifier|static
name|int
name|fenceCalled
init|=
literal|0
decl_stmt|;
DECL|field|fencedSvc
specifier|static
name|HAServiceTarget
name|fencedSvc
decl_stmt|;
DECL|field|callArgs
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|callArgs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|tryFence (HAServiceTarget target, String args)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|fencedSvc
operator|=
name|target
expr_stmt|;
name|callArgs
operator|.
name|add
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fenceCalled
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|checkArgs (String args)
specifier|public
name|void
name|checkArgs
parameter_list|(
name|String
name|args
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

