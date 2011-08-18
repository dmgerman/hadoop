begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.hamlet
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|hamlet
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
name|*
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
name|webapp
operator|.
name|WebAppException
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
name|webapp
operator|.
name|hamlet
operator|.
name|HamletImpl
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestParseSelector
specifier|public
class|class
name|TestParseSelector
block|{
DECL|method|testNormal ()
annotation|@
name|Test
specifier|public
name|void
name|testNormal
parameter_list|()
block|{
name|String
index|[]
name|res
init|=
name|parseSelector
argument_list|(
literal|"#id.class"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id"
argument_list|,
name|res
index|[
name|S_ID
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"class"
argument_list|,
name|res
index|[
name|S_CLASS
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiClass ()
annotation|@
name|Test
specifier|public
name|void
name|testMultiClass
parameter_list|()
block|{
name|String
index|[]
name|res
init|=
name|parseSelector
argument_list|(
literal|"#id.class1.class2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id"
argument_list|,
name|res
index|[
name|S_ID
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"class1 class2"
argument_list|,
name|res
index|[
name|S_CLASS
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingId ()
annotation|@
name|Test
specifier|public
name|void
name|testMissingId
parameter_list|()
block|{
name|String
index|[]
name|res
init|=
name|parseSelector
argument_list|(
literal|".class"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|res
index|[
name|S_ID
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"class"
argument_list|,
name|res
index|[
name|S_CLASS
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingClass ()
annotation|@
name|Test
specifier|public
name|void
name|testMissingClass
parameter_list|()
block|{
name|String
index|[]
name|res
init|=
name|parseSelector
argument_list|(
literal|"#id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id"
argument_list|,
name|res
index|[
name|S_ID
index|]
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|res
index|[
name|S_CLASS
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingAll ()
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|WebAppException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMissingAll
parameter_list|()
block|{
name|parseSelector
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

