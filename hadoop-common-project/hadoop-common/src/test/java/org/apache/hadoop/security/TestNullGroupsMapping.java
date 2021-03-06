begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2015 Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test that the {@link NullGroupsMapping} really does nothing.  */
end_comment

begin_class
DECL|class|TestNullGroupsMapping
specifier|public
class|class
name|TestNullGroupsMapping
block|{
DECL|field|ngm
specifier|private
name|NullGroupsMapping
name|ngm
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|this
operator|.
name|ngm
operator|=
operator|new
name|NullGroupsMapping
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test of getGroups method, of class {@link NullGroupsMapping}.    */
annotation|@
name|Test
DECL|method|testGetGroups ()
specifier|public
name|void
name|testGetGroups
parameter_list|()
block|{
name|String
name|user
init|=
literal|"user"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expResult
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|ngm
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No groups should be returned"
argument_list|,
name|expResult
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|ngm
operator|.
name|cacheGroupsAdd
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"group1"
block|,
literal|"group2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|ngm
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No groups should be returned"
argument_list|,
name|expResult
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|ngm
operator|.
name|cacheGroupsRefresh
argument_list|()
expr_stmt|;
name|result
operator|=
name|ngm
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No groups should be returned"
argument_list|,
name|expResult
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

