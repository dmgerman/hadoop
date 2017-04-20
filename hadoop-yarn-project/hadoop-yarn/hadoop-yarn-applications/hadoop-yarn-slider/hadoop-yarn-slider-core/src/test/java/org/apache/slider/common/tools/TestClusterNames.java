begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.tools
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
package|;
end_package

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
name|Arrays
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

begin_comment
comment|/**  * Test cluster name validation.  */
end_comment

begin_class
DECL|class|TestClusterNames
specifier|public
class|class
name|TestClusterNames
block|{
DECL|method|assertValidName (String name)
name|void
name|assertValidName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|boolean
name|valid
init|=
name|SliderUtils
operator|.
name|isClusternameValid
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Clustername '"
operator|+
name|name
operator|+
literal|"' mistakenly declared invalid"
argument_list|,
name|valid
argument_list|)
expr_stmt|;
block|}
DECL|method|assertInvalidName (String name)
name|void
name|assertInvalidName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|boolean
name|valid
init|=
name|SliderUtils
operator|.
name|isClusternameValid
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Clustername '\" + name + \"' mistakenly declared valid"
argument_list|,
name|valid
argument_list|)
expr_stmt|;
block|}
DECL|method|assertInvalid (List<String> names)
name|void
name|assertInvalid
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|assertInvalidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertValid (List<String> names)
name|void
name|assertValid
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|assertValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEmptyName ()
specifier|public
name|void
name|testEmptyName
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpaceName ()
specifier|public
name|void
name|testSpaceName
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeadingHyphen ()
specifier|public
name|void
name|testLeadingHyphen
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|"-hyphen"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTitleLetters ()
specifier|public
name|void
name|testTitleLetters
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|"Title"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCapitalLetters ()
specifier|public
name|void
name|testCapitalLetters
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|"UPPER-CASE-CLUSTER"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInnerBraced ()
specifier|public
name|void
name|testInnerBraced
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|"a[a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeadingBrace ()
specifier|public
name|void
name|testLeadingBrace
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalidName
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonalphaLeadingChars ()
specifier|public
name|void
name|testNonalphaLeadingChars
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalid
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"[a"
argument_list|,
literal|"#"
argument_list|,
literal|"@"
argument_list|,
literal|"="
argument_list|,
literal|"*"
argument_list|,
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonalphaInnerChars ()
specifier|public
name|void
name|testNonalphaInnerChars
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertInvalid
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a[a"
argument_list|,
literal|"b#"
argument_list|,
literal|"c@"
argument_list|,
literal|"d="
argument_list|,
literal|"e*"
argument_list|,
literal|"f."
argument_list|,
literal|"g "
argument_list|,
literal|"h i"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterValid ()
specifier|public
name|void
name|testClusterValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertValidName
argument_list|(
literal|"cluster"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidNames ()
specifier|public
name|void
name|testValidNames
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertValid
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"cluster"
argument_list|,
literal|"cluster1"
argument_list|,
literal|"very-very-very-long-cluster-name"
argument_list|,
literal|"c1234567890"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

