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
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestResources
specifier|public
class|class
name|TestResources
block|{
DECL|method|createResource (long memory, long vCores)
specifier|public
name|Resource
name|createResource
parameter_list|(
name|long
name|memory
parameter_list|,
name|long
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testCompareToWithUnboundedResource ()
specifier|public
name|void
name|testCompareToWithUnboundedResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Resources
operator|.
name|unbounded
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Long
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
name|Resources
operator|.
name|unbounded
argument_list|()
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
name|Resources
operator|.
name|unbounded
argument_list|()
operator|.
name|compareTo
argument_list|(
name|createResource
argument_list|(
literal|0
argument_list|,
name|Long
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
literal|1000
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
block|}
block|}
end_class

end_unit

