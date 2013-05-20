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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|NodeId
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
DECL|class|TestNodeId
specifier|public
class|class
name|TestNodeId
block|{
annotation|@
name|Test
DECL|method|testNodeId ()
specifier|public
name|void
name|testNodeId
parameter_list|()
block|{
name|NodeId
name|nodeId1
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"10.18.52.124"
argument_list|,
literal|8041
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId2
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"10.18.52.125"
argument_list|,
literal|8038
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId3
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"10.18.52.124"
argument_list|,
literal|8041
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId4
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"10.18.52.124"
argument_list|,
literal|8039
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeId1
operator|.
name|equals
argument_list|(
name|nodeId3
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nodeId1
operator|.
name|equals
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nodeId3
operator|.
name|equals
argument_list|(
name|nodeId4
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeId1
operator|.
name|compareTo
argument_list|(
name|nodeId3
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeId1
operator|.
name|compareTo
argument_list|(
name|nodeId2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeId3
operator|.
name|compareTo
argument_list|(
name|nodeId4
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeId1
operator|.
name|hashCode
argument_list|()
operator|==
name|nodeId3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nodeId1
operator|.
name|hashCode
argument_list|()
operator|==
name|nodeId2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nodeId3
operator|.
name|hashCode
argument_list|()
operator|==
name|nodeId4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"10.18.52.124:8041"
argument_list|,
name|nodeId1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

