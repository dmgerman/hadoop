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
name|io
operator|.
name|ByteArrayOutputStream
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
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|base
operator|.
name|Joiner
import|;
end_import

begin_class
DECL|class|TestHAAdmin
specifier|public
class|class
name|TestHAAdmin
block|{
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
name|TestHAAdmin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tool
specifier|private
name|HAAdmin
name|tool
decl_stmt|;
DECL|field|errOutBytes
specifier|private
name|ByteArrayOutputStream
name|errOutBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errOutput
specifier|private
name|String
name|errOutput
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|tool
operator|=
operator|new
name|HAAdmin
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|HAServiceTarget
name|resolveTarget
parameter_list|(
name|String
name|target
parameter_list|)
block|{
return|return
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"dummy"
argument_list|,
literal|12345
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|tool
operator|.
name|errOut
operator|=
operator|new
name|PrintStream
argument_list|(
name|errOutBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOutputContains (String string)
specifier|private
name|void
name|assertOutputContains
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
operator|!
name|errOutput
operator|.
name|contains
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Expected output to contain '"
operator|+
name|string
operator|+
literal|"' but was:\n"
operator|+
name|errOutput
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAdminUsage ()
specifier|public
name|void
name|testAdminUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|()
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Usage:"
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"-transitionToActive"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"badCommand"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Bad command 'badCommand'"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-badCommand"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"badCommand: Unknown"
argument_list|)
expr_stmt|;
comment|// valid command but not enough arguments
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"transitionToActive: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"transitionToActive: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"failover: incorrect arguments"
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"failover: incorrect arguments"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"foo:1234"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"failover: incorrect arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-help"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-help"
argument_list|,
literal|"transitionToActive"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Transitions the service into Active"
argument_list|)
expr_stmt|;
block|}
DECL|method|runTool (String .... args)
specifier|private
name|Object
name|runTool
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|errOutBytes
operator|.
name|reset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running: HAAdmin "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|tool
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|errOutput
operator|=
operator|new
name|String
argument_list|(
name|errOutBytes
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Output:\n"
operator|+
name|errOutput
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

