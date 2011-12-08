begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

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
name|Collection
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
DECL|class|TestNameNodeResourcePolicy
specifier|public
class|class
name|TestNameNodeResourcePolicy
block|{
annotation|@
name|Test
DECL|method|testSingleRedundantResource ()
specifier|public
name|void
name|testSingleRedundantResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleRequiredResource ()
specifier|public
name|void
name|testSingleRequiredResource
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleRedundantResources ()
specifier|public
name|void
name|testMultipleRedundantResources
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testResourceScenario
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail if there are more minimum redundant resources than "
operator|+
literal|"total redundant resources"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|rte
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|rte
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Need a minimum"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMultipleRequiredResources ()
specifier|public
name|void
name|testMultipleRequiredResources
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRedundantWithRequiredResources ()
specifier|public
name|void
name|testRedundantWithRequiredResources
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testResourceScenario
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testResourceScenario ( int numRedundantResources, int numRequiredResources, int numFailedRedundantResources, int numFailedRequiredResources, int minimumRedundantResources)
specifier|private
specifier|static
name|boolean
name|testResourceScenario
parameter_list|(
name|int
name|numRedundantResources
parameter_list|,
name|int
name|numRequiredResources
parameter_list|,
name|int
name|numFailedRedundantResources
parameter_list|,
name|int
name|numFailedRequiredResources
parameter_list|,
name|int
name|minimumRedundantResources
parameter_list|)
block|{
name|Collection
argument_list|<
name|CheckableNameNodeResource
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<
name|CheckableNameNodeResource
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
name|numRedundantResources
condition|;
name|i
operator|++
control|)
block|{
name|CheckableNameNodeResource
name|r
init|=
name|mock
argument_list|(
name|CheckableNameNodeResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|r
operator|.
name|isRequired
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|r
operator|.
name|isResourceAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|i
operator|>=
name|numFailedRedundantResources
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|r
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
name|numRequiredResources
condition|;
name|i
operator|++
control|)
block|{
name|CheckableNameNodeResource
name|r
init|=
name|mock
argument_list|(
name|CheckableNameNodeResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|r
operator|.
name|isRequired
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|r
operator|.
name|isResourceAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|i
operator|>=
name|numFailedRequiredResources
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|NameNodeResourcePolicy
operator|.
name|areResourcesAvailable
argument_list|(
name|resources
argument_list|,
name|minimumRedundantResources
argument_list|)
return|;
block|}
block|}
end_class

end_unit

