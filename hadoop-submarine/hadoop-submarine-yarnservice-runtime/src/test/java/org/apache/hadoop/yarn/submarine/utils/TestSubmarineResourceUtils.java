begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|service
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
name|util
operator|.
name|resource
operator|.
name|CustomResourceTypesConfigurationProvider
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
name|Test
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
name|*
import|;
end_import

begin_comment
comment|/**  * This class is to test {@link SubmarineResourceUtils}.  */
end_comment

begin_class
DECL|class|TestSubmarineResourceUtils
specifier|public
class|class
name|TestSubmarineResourceUtils
block|{
DECL|field|CUSTOM_RESOURCE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CUSTOM_RESOURCE_NAME
init|=
literal|"a-custom-resource"
decl_stmt|;
DECL|method|initResourceTypes ()
specifier|private
name|void
name|initResourceTypes
parameter_list|()
block|{
name|CustomResourceTypesConfigurationProvider
operator|.
name|initResourceTypes
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CUSTOM_RESOURCE_NAME
argument_list|,
literal|"G"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertResourceWithCustomResource ()
specifier|public
name|void
name|testConvertResourceWithCustomResource
parameter_list|()
block|{
name|initResourceTypes
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|4096
argument_list|,
literal|12
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|CUSTOM_RESOURCE_NAME
argument_list|,
literal|20L
argument_list|)
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
name|serviceResource
init|=
name|SubmarineResourceUtils
operator|.
name|convertYarnResourceToServiceResource
argument_list|(
name|res
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|serviceResource
operator|.
name|getCpus
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4096
argument_list|,
operator|(
name|int
operator|)
name|Integer
operator|.
name|valueOf
argument_list|(
name|serviceResource
operator|.
name|getMemory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|additionalResources
init|=
name|serviceResource
operator|.
name|getAdditional
argument_list|()
decl_stmt|;
comment|// Additional resources also includes vcores and memory
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|additionalResources
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceInformation
name|customResourceRI
init|=
name|additionalResources
operator|.
name|get
argument_list|(
name|CUSTOM_RESOURCE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"G"
argument_list|,
name|customResourceRI
operator|.
name|getUnit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20L
argument_list|,
operator|(
name|long
operator|)
name|customResourceRI
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

