begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.rawcoder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
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

begin_comment
comment|/**  * Test base for raw XOR coders.  */
end_comment

begin_class
DECL|class|TestXORRawCoderBase
specifier|public
specifier|abstract
class|class
name|TestXORRawCoderBase
extends|extends
name|TestRawCoderBase
block|{
annotation|@
name|Test
DECL|method|testCoding_10x1_erasing_d0 ()
specifier|public
name|void
name|testCoding_10x1_erasing_d0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|testCodingDoMixAndTwice
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCoding_10x1_erasing_p0 ()
specifier|public
name|void
name|testCoding_10x1_erasing_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
name|testCodingDoMixAndTwice
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCoding_10x1_erasing_d5 ()
specifier|public
name|void
name|testCoding_10x1_erasing_d5
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|}
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|testCodingDoMixAndTwice
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingNegative_10x1_erasing_too_many ()
specifier|public
name|void
name|testCodingNegative_10x1_erasing_too_many
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
name|testCodingWithErasingTooMany
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingNegative_10x1_erasing_d5 ()
specifier|public
name|void
name|testCodingNegative_10x1_erasing_d5
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|}
argument_list|,
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|testCodingWithBadInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCodingWithBadOutput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testCodingWithBadInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCodingWithBadOutput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

