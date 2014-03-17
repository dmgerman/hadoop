begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resource
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
name|resource
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

begin_class
DECL|class|TestResourceWeights
specifier|public
class|class
name|TestResourceWeights
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000
argument_list|)
DECL|method|testWeights ()
specifier|public
name|void
name|testWeights
parameter_list|()
block|{
name|ResourceWeights
name|rw1
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Default CPU weight should be 0.0f."
argument_list|,
literal|0.0f
argument_list|,
name|rw1
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Default memory weight should be 0.0f"
argument_list|,
literal|0.0f
argument_list|,
name|rw1
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|ResourceWeights
name|rw2
init|=
operator|new
name|ResourceWeights
argument_list|(
literal|2.0f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The CPU weight should be 2.0f."
argument_list|,
literal|2.0f
argument_list|,
name|rw2
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The memory weight should be 2.0f"
argument_list|,
literal|2.0f
argument_list|,
name|rw2
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
comment|// set each individually
name|ResourceWeights
name|rw3
init|=
operator|new
name|ResourceWeights
argument_list|(
literal|1.5f
argument_list|,
literal|2.0f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The CPU weight should be 2.0f"
argument_list|,
literal|2.0f
argument_list|,
name|rw3
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The memory weight should be 1.5f"
argument_list|,
literal|1.5f
argument_list|,
name|rw3
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
comment|// reset weights
name|rw3
operator|.
name|setWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|,
literal|2.5f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The CPU weight should be set to 2.5f."
argument_list|,
literal|2.5f
argument_list|,
name|rw3
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|CPU
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|rw3
operator|.
name|setWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|,
literal|4.0f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The memory weight should be set to 4.0f."
argument_list|,
literal|4.0f
argument_list|,
name|rw3
operator|.
name|getWeight
argument_list|(
name|ResourceType
operator|.
name|MEMORY
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

