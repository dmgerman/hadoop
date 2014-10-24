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
name|util
operator|.
name|ArrayList
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|AllocateResponsePBImpl
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
name|AMCommand
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
name|Container
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
name|ContainerResourceDecrease
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
name|ContainerResourceIncrease
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
name|ContainerStatus
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
name|NMToken
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
name|NodeReport
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
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|AllocateResponseProto
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|TestAllocateResponse
specifier|public
class|class
name|TestAllocateResponse
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testAllocateResponseWithIncDecContainers ()
specifier|public
name|void
name|testAllocateResponseWithIncDecContainers
parameter_list|()
block|{
name|List
argument_list|<
name|ContainerResourceIncrease
argument_list|>
name|incContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerResourceIncrease
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerResourceDecrease
argument_list|>
name|decContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerResourceDecrease
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|incContainers
operator|.
name|add
argument_list|(
name|ContainerResourceIncrease
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
name|i
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|decContainers
operator|.
name|add
argument_list|(
name|ContainerResourceDecrease
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AllocateResponse
name|r
init|=
name|AllocateResponse
operator|.
name|newInstance
argument_list|(
literal|3
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
name|AMCommand
operator|.
name|AM_RESYNC
argument_list|,
literal|3
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|NMToken
argument_list|>
argument_list|()
argument_list|,
name|incContainers
argument_list|,
name|decContainers
argument_list|)
decl_stmt|;
comment|// serde
name|AllocateResponseProto
name|p
init|=
operator|(
operator|(
name|AllocateResponsePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|r
operator|=
operator|new
name|AllocateResponsePBImpl
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// check value
name|Assert
operator|.
name|assertEquals
argument_list|(
name|incContainers
operator|.
name|size
argument_list|()
argument_list|,
name|r
operator|.
name|getIncreasedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|decContainers
operator|.
name|size
argument_list|()
argument_list|,
name|r
operator|.
name|getDecreasedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|incContainers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|r
operator|.
name|getIncreasedContainers
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|decContainers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|r
operator|.
name|getDecreasedContainers
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testAllocateResponseWithoutIncDecContainers ()
specifier|public
name|void
name|testAllocateResponseWithoutIncDecContainers
parameter_list|()
block|{
name|AllocateResponse
name|r
init|=
name|AllocateResponse
operator|.
name|newInstance
argument_list|(
literal|3
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
name|AMCommand
operator|.
name|AM_RESYNC
argument_list|,
literal|3
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|NMToken
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// serde
name|AllocateResponseProto
name|p
init|=
operator|(
operator|(
name|AllocateResponsePBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|r
operator|=
operator|new
name|AllocateResponsePBImpl
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// check value
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|getIncreasedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|getDecreasedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

