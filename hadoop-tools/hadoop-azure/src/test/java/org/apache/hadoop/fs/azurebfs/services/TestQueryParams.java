begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|oauth2
operator|.
name|QueryParams
import|;
end_import

begin_comment
comment|/**  * Test query params serialization.  */
end_comment

begin_class
DECL|class|TestQueryParams
specifier|public
class|class
name|TestQueryParams
block|{
DECL|field|SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"&"
decl_stmt|;
DECL|field|PARAM_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
index|[]
name|PARAM_ARRAY
init|=
block|{
block|{
literal|"K0"
block|,
literal|"V0"
block|}
block|,
block|{
literal|"K1"
block|,
literal|"V1"
block|}
block|,
block|{
literal|"K2"
block|,
literal|"V2"
block|}
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testOneParam ()
specifier|public
name|void
name|testOneParam
parameter_list|()
block|{
name|String
name|key
init|=
name|PARAM_ARRAY
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
name|String
name|value
init|=
name|PARAM_ARRAY
index|[
literal|0
index|]
index|[
literal|1
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|QueryParams
name|qp
init|=
operator|new
name|QueryParams
argument_list|()
decl_stmt|;
name|qp
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|key
operator|+
literal|"="
operator|+
name|value
argument_list|,
name|qp
operator|.
name|serialize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleParams ()
specifier|public
name|void
name|testMultipleParams
parameter_list|()
block|{
name|QueryParams
name|qp
init|=
operator|new
name|QueryParams
argument_list|()
decl_stmt|;
for|for
control|(
name|String
index|[]
name|entry
range|:
name|PARAM_ARRAY
control|)
block|{
name|qp
operator|.
name|add
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|,
name|entry
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramMap
init|=
name|constructMap
argument_list|(
name|qp
operator|.
name|serialize
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PARAM_ARRAY
operator|.
name|length
argument_list|,
name|paramMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
index|[]
name|entry
range|:
name|PARAM_ARRAY
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|paramMap
operator|.
name|containsKey
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entry
index|[
literal|1
index|]
argument_list|,
name|paramMap
operator|.
name|get
argument_list|(
name|entry
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|constructMap (String input)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|constructMap
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|String
index|[]
name|entries
init|=
name|input
operator|.
name|split
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|entries
control|)
block|{
name|String
index|[]
name|keyValue
init|=
name|entry
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
name|keyValue
index|[
literal|0
index|]
argument_list|,
name|keyValue
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|paramMap
return|;
block|}
block|}
end_class

end_unit

