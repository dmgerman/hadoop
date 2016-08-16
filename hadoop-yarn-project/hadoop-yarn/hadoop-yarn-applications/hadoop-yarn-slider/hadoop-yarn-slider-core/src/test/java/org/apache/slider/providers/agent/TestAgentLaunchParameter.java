begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
package|;
end_package

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
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TestAgentLaunchParameter
specifier|public
class|class
name|TestAgentLaunchParameter
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestAgentLaunchParameter
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testTestAgentLaunchParameter ()
specifier|public
name|void
name|testTestAgentLaunchParameter
parameter_list|()
throws|throws
name|Exception
block|{
name|AgentLaunchParameter
name|alp
init|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|alp
operator|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|"a:1:2:3|b:5:6:NONE"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"6"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|alp
operator|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|"|a:1:3|b::5:NONE:"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|alp
operator|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|"|:"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|alp
operator|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|"HBASE_MASTER:a,b:DO_NOT_REGISTER:"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a,b"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"DO_NOT_REGISTER"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"DO_NOT_REGISTER"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|alp
operator|=
operator|new
name|AgentLaunchParameter
argument_list|(
literal|"HBASE_MASTER:a,b:DO_NOT_REGISTER::c:::"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a,b"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"DO_NOT_REGISTER"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|alp
operator|.
name|getNextLaunchParameter
argument_list|(
literal|"HBASE_MASTER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

