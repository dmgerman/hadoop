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
name|java
operator|.
name|io
operator|.
name|File
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
name|conf
operator|.
name|YarnConfiguration
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|resource
operator|.
name|ResourceUtils
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
name|util
operator|.
name|resource
operator|.
name|TestResourceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|()
expr_stmt|;
name|String
name|resourceTypesFile
init|=
literal|"resource-types-4.xml"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|TestResourceUtils
operator|.
name|setupResourceTypes
argument_list|(
name|conf
argument_list|,
name|resourceTypesFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|conf
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"resource-types-4.xml"
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|dest
init|=
operator|new
name|File
argument_list|(
name|source
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"resource-types.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dest
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
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
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testGetMemory ()
specifier|public
name|void
name|testGetMemory
parameter_list|()
block|{
name|Resource
name|res
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
name|long
name|memorySize
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1L
decl_stmt|;
name|res
operator|.
name|setMemorySize
argument_list|(
name|memorySize
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No need to cast if both are long"
argument_list|,
name|memorySize
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cast to Integer.MAX_VALUE if the long is greater than "
operator|+
literal|"Integer.MAX_VALUE"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|res
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetVirtualCores ()
specifier|public
name|void
name|testGetVirtualCores
parameter_list|()
block|{
name|Resource
name|res
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
name|long
name|vcores
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1L
decl_stmt|;
name|res
operator|.
name|getResourceInformation
argument_list|(
literal|"vcores"
argument_list|)
operator|.
name|setValue
argument_list|(
name|vcores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No need to cast if both are long"
argument_list|,
name|vcores
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
literal|"vcores"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cast to Integer.MAX_VALUE if the long is greater than "
operator|+
literal|"Integer.MAX_VALUE"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResourcePBWithExtraResources ()
specifier|public
name|void
name|testResourcePBWithExtraResources
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Resource 'resource1' has been passed as 4T
comment|//4T should be converted to 4000G
name|YarnProtos
operator|.
name|ResourceInformationProto
name|riProto
init|=
name|YarnProtos
operator|.
name|ResourceInformationProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypeInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypesProto
operator|.
name|COUNTABLE
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
literal|4
argument_list|)
operator|.
name|setUnits
argument_list|(
literal|"T"
argument_list|)
operator|.
name|setKey
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|addResourceValueMap
argument_list|(
name|riProto
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
literal|4000
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"G"
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
comment|//Resource 'resource2' has been passed as 4M
comment|//4M should be converted to 4000000000m
name|YarnProtos
operator|.
name|ResourceInformationProto
name|riProto1
init|=
name|YarnProtos
operator|.
name|ResourceInformationProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypeInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"resource2"
argument_list|)
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypesProto
operator|.
name|COUNTABLE
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
literal|4
argument_list|)
operator|.
name|setUnits
argument_list|(
literal|"M"
argument_list|)
operator|.
name|setKey
argument_list|(
literal|"resource2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|YarnProtos
operator|.
name|ResourceProto
name|proto1
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
name|addResourceValueMap
argument_list|(
name|riProto1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Resource
name|res1
init|=
operator|new
name|ResourcePBImpl
argument_list|(
name|proto1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4000000000L
argument_list|,
name|res1
operator|.
name|getResourceInformation
argument_list|(
literal|"resource2"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"m"
argument_list|,
name|res1
operator|.
name|getResourceInformation
argument_list|(
literal|"resource2"
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
comment|//Resource 'resource1' has been passed as 3M
comment|//3M should be converted to 0G
name|YarnProtos
operator|.
name|ResourceInformationProto
name|riProto2
init|=
name|YarnProtos
operator|.
name|ResourceInformationProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypeInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|setType
argument_list|(
name|YarnProtos
operator|.
name|ResourceTypesProto
operator|.
name|COUNTABLE
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
literal|3
argument_list|)
operator|.
name|setUnits
argument_list|(
literal|"M"
argument_list|)
operator|.
name|setKey
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|YarnProtos
operator|.
name|ResourceProto
name|proto2
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
name|addResourceValueMap
argument_list|(
name|riProto2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Resource
name|res2
init|=
operator|new
name|ResourcePBImpl
argument_list|(
name|proto2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res2
operator|.
name|getResourceInformation
argument_list|(
literal|"resource1"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"G"
argument_list|,
name|res2
operator|.
name|getResourceInformation
argument_list|(
literal|"resource1"
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

