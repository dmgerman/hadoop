begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util.timeline
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
name|timeline
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Test case for limiting flow name size.  */
end_comment

begin_class
DECL|class|TestShortenedFlowName
specifier|public
class|class
name|TestShortenedFlowName
block|{
DECL|field|TEST_FLOW_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FLOW_NAME
init|=
literal|"TestFlowName"
decl_stmt|;
annotation|@
name|Test
DECL|method|testRemovingUUID ()
specifier|public
name|void
name|testRemovingUUID
parameter_list|()
block|{
name|String
name|flowName
init|=
name|TEST_FLOW_NAME
operator|+
literal|"-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|flowName
operator|=
name|TimelineUtils
operator|.
name|removeUUID
argument_list|(
name|flowName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TEST_FLOW_NAME
argument_list|,
name|flowName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortenedFlowName ()
specifier|public
name|void
name|testShortenedFlowName
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|flowName
init|=
name|TEST_FLOW_NAME
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|FLOW_NAME_MAX_SIZE
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|String
name|shortenedFlowName
init|=
name|TimelineUtils
operator|.
name|shortenFlowName
argument_list|(
name|flowName
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"TestFlow"
argument_list|,
name|shortenedFlowName
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|FLOW_NAME_MAX_SIZE
argument_list|,
name|YarnConfiguration
operator|.
name|FLOW_NAME_DEFAULT_MAX_SIZE
argument_list|)
expr_stmt|;
name|shortenedFlowName
operator|=
name|TimelineUtils
operator|.
name|shortenFlowName
argument_list|(
name|flowName
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TEST_FLOW_NAME
argument_list|,
name|shortenedFlowName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

