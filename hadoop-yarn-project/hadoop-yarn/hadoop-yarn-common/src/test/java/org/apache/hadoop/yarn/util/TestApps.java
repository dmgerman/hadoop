begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
DECL|class|TestApps
specifier|public
class|class
name|TestApps
block|{
annotation|@
name|Test
DECL|method|testSetEnvFromInputString ()
specifier|public
name|void
name|testSetEnvFromInputString
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"JAVA_HOME"
argument_list|,
literal|"/path/jdk"
argument_list|)
expr_stmt|;
name|String
name|goodEnv
init|=
literal|"a1=1,b_2=2,_c=3,d=4,e=,f_win=%JAVA_HOME%"
operator|+
literal|",g_nix=$JAVA_HOME"
decl_stmt|;
name|Apps
operator|.
name|setEnvFromInputString
argument_list|(
name|environment
argument_list|,
name|goodEnv
argument_list|,
name|File
operator|.
name|pathSeparator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"a1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"b_2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"_c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|assertEquals
argument_list|(
literal|"$JAVA_HOME"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"g_nix"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/path/jdk"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"f_win"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"/path/jdk"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"g_nix"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"%JAVA_HOME%"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"f_win"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|badEnv
init|=
literal|"1,,2=a=b,3=a=,4==,5==a,==,c-3=3,="
decl_stmt|;
name|environment
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Apps
operator|.
name|setEnvFromInputString
argument_list|(
name|environment
argument_list|,
name|badEnv
argument_list|,
name|File
operator|.
name|pathSeparator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|environment
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Test "=" in the value part
name|environment
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Apps
operator|.
name|setEnvFromInputString
argument_list|(
name|environment
argument_list|,
literal|"b1,e1==,e2=a1=a2,b2"
argument_list|,
name|File
operator|.
name|pathSeparator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"="
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"e1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a1=a2"
argument_list|,
name|environment
operator|.
name|get
argument_list|(
literal|"e2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

