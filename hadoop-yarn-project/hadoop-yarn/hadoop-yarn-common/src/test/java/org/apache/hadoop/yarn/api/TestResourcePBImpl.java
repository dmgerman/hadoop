begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ResourcePBImpl
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
name|proto
operator|.
name|YarnProtos
import|;
end_import

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

begin_comment
comment|/**  * Test class to handle various proto related tests for resources.  */
end_comment

begin_class
DECL|class|TestResourcePBImpl
specifier|public
class|class
name|TestResourcePBImpl
block|{
annotation|@
name|Test
DECL|method|testEmptyResourcePBInit ()
specifier|public
name|void
name|testEmptyResourcePBInit
parameter_list|()
throws|throws
name|Exception
block|{
name|Resource
name|res
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
comment|// Assert to check it sets resource value and unit to default.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getUnits
argument_list|()
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getUnits
argument_list|()
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResourcePBInitFromOldPB ()
specifier|public
name|void
name|testResourcePBInitFromOldPB
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnProtos
operator|.
name|ResourceProto
name|proto
init|=
name|YarnProtos
operator|.
name|ResourceProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMemory
argument_list|(
literal|1024
argument_list|)
operator|.
name|setVirtualCores
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Assert to check it sets resource value and unit to default.
name|Resource
name|res
init|=
operator|new
name|ResourcePBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getUnits
argument_list|()
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getUnits
argument_list|()
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

