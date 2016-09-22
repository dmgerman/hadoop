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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRConfig
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
name|conf
operator|.
name|YarnConfiguration
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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
DECL|class|TestMaster
specifier|public
class|class
name|TestMaster
block|{
annotation|@
name|Test
DECL|method|testGetMasterAddress ()
specifier|public
name|void
name|testGetMasterAddress
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
comment|// Trying invalid master address for classic
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|CLASSIC_FRAMEWORK_NAME
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|,
literal|"local:invalid"
argument_list|)
expr_stmt|;
comment|// should throw an exception for invalid value
try|try
block|{
name|Master
operator|.
name|getMasterAddress
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not reach here as there is a bad master address"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected
block|}
comment|// Change master address to a valid value
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|,
literal|"bar.com:8042"
argument_list|)
expr_stmt|;
name|String
name|masterHostname
init|=
name|Master
operator|.
name|getMasterAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|masterHostname
argument_list|,
literal|"bar.com"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

