begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util.resource
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
operator|.
name|resource
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
name|conf
operator|.
name|YarnConfiguration
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|componentwiseMin
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|componentwiseMax
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|add
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|subtract
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|multiply
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|multiplyAndAddTo
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|multiplyAndRoundDown
import|;
end_import

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
name|util
operator|.
name|resource
operator|.
name|Resources
operator|.
name|fitsIn
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestResources
specifier|public
class|class
name|TestResources
block|{
DECL|class|ExtendedResources
specifier|static
class|class
name|ExtendedResources
extends|extends
name|Resources
block|{
DECL|method|unbounded ()
specifier|public
specifier|static
name|Resource
name|unbounded
parameter_list|()
block|{
return|return
operator|new
name|FixedValueResource
argument_list|(
literal|"UNBOUNDED"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|none ()
specifier|public
specifier|static
name|Resource
name|none
parameter_list|()
block|{
return|return
operator|new
name|FixedValueResource
argument_list|(
literal|"NONE"
argument_list|,
literal|0L
argument_list|)
return|;
block|}
block|}
DECL|field|EXTRA_RESOURCE_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|EXTRA_RESOURCE_TYPE
init|=
literal|"resource2"
decl_stmt|;
DECL|field|resourceTypesFile
specifier|private
name|String
name|resourceTypesFile
decl_stmt|;
DECL|method|setupExtraResourceType ()
specifier|private
name|void
name|setupExtraResourceType
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|resourceTypesFile
operator|=
name|TestResourceUtils
operator|.
name|setupResourceTypes
argument_list|(
name|conf
argument_list|,
literal|"resource-types-3.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|unsetExtraResourceType ()
specifier|private
name|void
name|unsetExtraResourceType
parameter_list|()
block|{
name|deleteResourceTypesFile
argument_list|()
expr_stmt|;
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteResourceTypesFile ()
specifier|private
name|void
name|deleteResourceTypesFile
parameter_list|()
block|{
if|if
condition|(
name|resourceTypesFile
operator|!=
literal|null
operator|&&
operator|!
name|resourceTypesFile
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|File
name|resourceFile
init|=
operator|new
name|File
argument_list|(
name|resourceTypesFile
argument_list|)
decl_stmt|;
name|resourceFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
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
name|setupExtraResourceType
argument_list|()
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
name|deleteResourceTypesFile
argument_list|()
expr_stmt|;
block|}
DECL|method|createResource (long memory, int vCores)
specifier|public
name|Resource
name|createResource
parameter_list|(
name|long
name|memory
parameter_list|,
name|int
name|vCores
parameter_list|)
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
return|;
block|}
DECL|method|createResource (long memory, int vCores, long resource2)
specifier|public
name|Resource
name|createResource
parameter_list|(
name|long
name|memory
parameter_list|,
name|int
name|vCores
parameter_list|,
name|long
name|resource2
parameter_list|)
block|{
name|Resource
name|ret
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
decl_stmt|;
name|ret
operator|.
name|setResourceInformation
argument_list|(
name|EXTRA_RESOURCE_TYPE
argument_list|,
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|EXTRA_RESOURCE_TYPE
argument_list|,
name|resource2
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCompareToWithUnboundedResource ()
specifier|public
name|void
name|testCompareToWithUnboundedResource
parameter_list|()
block|{
name|unsetExtraResourceType
argument_list|()
expr_stmt|;
name|Resource
name|unboundedClone
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|ExtendedResources
operator|.
name|unbounded
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|unboundedClone
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unboundedClone
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unboundedClone
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCompareToWithNoneResource ()
specifier|public
name|void
name|testCompareToWithNoneResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultipleRoundUp ()
specifier|public
name|void
name|testMultipleRoundUp
parameter_list|()
block|{
specifier|final
name|double
name|by
init|=
literal|0.5
decl_stmt|;
specifier|final
name|String
name|memoryErrorMsg
init|=
literal|"Invalid memory size."
decl_stmt|;
specifier|final
name|String
name|vcoreErrorMsg
init|=
literal|"Invalid virtual core number."
decl_stmt|;
name|Resource
name|resource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|result
init|=
name|Resources
operator|.
name|multiplyAndRoundUp
argument_list|(
name|resource
argument_list|,
name|by
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|memoryErrorMsg
argument_list|,
name|result
operator|.
name|getMemorySize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vcoreErrorMsg
argument_list|,
name|result
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|resource
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|Resources
operator|.
name|multiplyAndRoundUp
argument_list|(
name|resource
argument_list|,
name|by
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memoryErrorMsg
argument_list|,
name|result
operator|.
name|getMemorySize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vcoreErrorMsg
argument_list|,
name|result
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|resource
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|result
operator|=
name|Resources
operator|.
name|multiplyAndRoundUp
argument_list|(
name|resource
argument_list|,
name|by
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memoryErrorMsg
argument_list|,
name|result
operator|.
name|getMemorySize
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|vcoreErrorMsg
argument_list|,
name|result
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testFitsIn ()
specifier|public
name|void
name|testFitsIn
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fitsIn
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testComponentwiseMin ()
specifier|public
name|void
name|testComponentwiseMin
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMin
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComponentwiseMax ()
specifier|public
name|void
name|testComponentwiseMax
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|componentwiseMax
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAdd ()
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|add
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|)
argument_list|,
name|add
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|add
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|add
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubtract ()
specifier|public
name|void
name|testSubtract
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|subtract
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|subtract
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|subtract
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|subtract
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClone ()
specifier|public
name|void
name|testClone
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiply ()
specifier|public
name|void
name|testMultiply
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|2
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|6
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|,
name|multiply
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiplyAndRoundDown ()
specifier|public
name|void
name|testMultiplyAndRoundDown
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|7
argument_list|,
literal|7
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|2.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|7
argument_list|)
argument_list|,
name|multiplyAndRoundDown
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|2.5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiplyAndAddTo ()
specifier|public
name|void
name|testMultiplyAndAddTo
parameter_list|()
throws|throws
name|Exception
block|{
name|unsetExtraResourceType
argument_list|()
expr_stmt|;
name|setupExtraResourceType
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|4
argument_list|,
literal|7
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|(
literal|6
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|,
name|multiplyAndAddTo
argument_list|(
name|createResource
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|createResource
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateResourceWithSameLongValue ()
specifier|public
name|void
name|testCreateResourceWithSameLongValue
parameter_list|()
throws|throws
name|Exception
block|{
name|unsetExtraResourceType
argument_list|()
expr_stmt|;
name|setupExtraResourceType
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|ResourceUtils
operator|.
name|createResourceWithSameValue
argument_list|(
literal|11L
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11L
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11L
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|EXTRA_RESOURCE_TYPE
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateResourceWithSameIntValue ()
specifier|public
name|void
name|testCreateResourceWithSameIntValue
parameter_list|()
throws|throws
name|Exception
block|{
name|unsetExtraResourceType
argument_list|()
expr_stmt|;
name|setupExtraResourceType
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|ResourceUtils
operator|.
name|createResourceWithSameValue
argument_list|(
literal|11
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getResourceInformation
argument_list|(
name|EXTRA_RESOURCE_TYPE
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateSimpleResourceWithSameLongValue ()
specifier|public
name|void
name|testCreateSimpleResourceWithSameLongValue
parameter_list|()
block|{
name|Resource
name|res
init|=
name|ResourceUtils
operator|.
name|createResourceWithSameValue
argument_list|(
literal|11L
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11L
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
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
DECL|method|testCreateSimpleResourceWithSameIntValue ()
specifier|public
name|void
name|testCreateSimpleResourceWithSameIntValue
parameter_list|()
block|{
name|Resource
name|res
init|=
name|ResourceUtils
operator|.
name|createResourceWithSameValue
argument_list|(
literal|11
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

