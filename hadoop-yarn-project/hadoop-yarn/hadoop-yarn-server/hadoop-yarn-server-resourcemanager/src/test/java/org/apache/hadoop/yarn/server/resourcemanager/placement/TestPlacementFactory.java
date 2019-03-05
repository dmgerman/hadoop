begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
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
name|placement
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test for the {@link PlacementFactory}.  */
end_comment

begin_class
DECL|class|TestPlacementFactory
specifier|public
class|class
name|TestPlacementFactory
block|{
comment|/**    * Check that non existing class throws exception.    *    * @throws ClassNotFoundException    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ClassNotFoundException
operator|.
name|class
argument_list|)
DECL|method|testGetNonExistRuleText ()
specifier|public
name|void
name|testGetNonExistRuleText
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
specifier|final
name|String
name|nonExist
init|=
literal|"my.placement.Rule"
decl_stmt|;
name|PlacementFactory
operator|.
name|getPlacementRule
argument_list|(
name|nonExist
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check existing class using the class name.    * Relies on the {@link DefaultPlacementRule} of the FS.    */
annotation|@
name|Test
DECL|method|testGetExistRuleText ()
specifier|public
name|void
name|testGetExistRuleText
parameter_list|()
block|{
specifier|final
name|String
name|exists
init|=
name|DefaultPlacementRule
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
name|PlacementRule
name|rule
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rule
operator|=
name|PlacementFactory
operator|.
name|getPlacementRule
argument_list|(
name|exists
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Class should have been found"
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Rule object is null"
argument_list|,
name|rule
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Names not equal"
argument_list|,
name|rule
operator|.
name|getName
argument_list|()
argument_list|,
name|exists
argument_list|)
expr_stmt|;
block|}
comment|/**    * Existing class using the class reference.    * Relies on the {@link DefaultPlacementRule} of the FS.    */
annotation|@
name|Test
DECL|method|testGetRuleClass ()
specifier|public
name|void
name|testGetRuleClass
parameter_list|()
block|{
name|PlacementRule
name|rule
init|=
name|PlacementFactory
operator|.
name|getPlacementRule
argument_list|(
name|DefaultPlacementRule
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Rule object is null"
argument_list|,
name|rule
argument_list|)
expr_stmt|;
comment|// Should take anything as the second object: ignores unknown types in the
comment|// default implementation.
name|rule
operator|=
name|PlacementFactory
operator|.
name|getPlacementRule
argument_list|(
name|DefaultPlacementRule
operator|.
name|class
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Rule object is null"
argument_list|,
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

