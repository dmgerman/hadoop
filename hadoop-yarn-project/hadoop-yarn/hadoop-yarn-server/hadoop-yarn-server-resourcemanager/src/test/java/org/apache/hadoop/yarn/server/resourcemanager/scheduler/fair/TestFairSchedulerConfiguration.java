begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
package|;
end_package

begin_import
import|import static
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairSchedulerConfiguration
operator|.
name|parseResourceConfigValue
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
name|utils
operator|.
name|BuilderUtils
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
DECL|class|TestFairSchedulerConfiguration
specifier|public
class|class
name|TestFairSchedulerConfiguration
block|{
annotation|@
name|Test
DECL|method|testParseResourceConfigValue ()
specifier|public
name|void
name|testParseResourceConfigValue
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|,
name|parseResourceConfigValue
argument_list|(
literal|"2 vcores, 1024 mb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|,
name|parseResourceConfigValue
argument_list|(
literal|"1024 mb, 2 vcores"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|,
name|parseResourceConfigValue
argument_list|(
literal|"2vcores,1024mb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|,
name|parseResourceConfigValue
argument_list|(
literal|"1024mb,2vcores"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testNoUnits ()
specifier|public
name|void
name|testNoUnits
parameter_list|()
throws|throws
name|Exception
block|{
name|parseResourceConfigValue
argument_list|(
literal|"1024"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testOnlyMemory ()
specifier|public
name|void
name|testOnlyMemory
parameter_list|()
throws|throws
name|Exception
block|{
name|parseResourceConfigValue
argument_list|(
literal|"1024mb"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testOnlyCPU ()
specifier|public
name|void
name|testOnlyCPU
parameter_list|()
throws|throws
name|Exception
block|{
name|parseResourceConfigValue
argument_list|(
literal|"1024vcores"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AllocationConfigurationException
operator|.
name|class
argument_list|)
DECL|method|testGibberish ()
specifier|public
name|void
name|testGibberish
parameter_list|()
throws|throws
name|Exception
block|{
name|parseResourceConfigValue
argument_list|(
literal|"1o24vc0res"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

