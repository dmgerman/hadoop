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

begin_comment
comment|/**  * Test XOR encoding and decoding.  */
end_comment

begin_class
DECL|class|TestXORRawCoder
specifier|public
class|class
name|TestXORRawCoder
extends|extends
name|TestRawCoderBase
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|encoderClass
operator|=
name|XORRawEncoder
operator|.
name|class
expr_stmt|;
name|this
operator|.
name|decoderClass
operator|=
name|XORRawDecoder
operator|.
name|class
expr_stmt|;
name|this
operator|.
name|numDataUnits
operator|=
literal|10
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingNoDirectBuffer_erasing_d0 ()
specifier|public
name|void
name|testCodingNoDirectBuffer_erasing_d0
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
comment|/**      * Doing twice to test if the coders can be repeatedly reused. This matters      * as the underlying coding buffers are shared, which may have bugs.      */
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingDirectBuffer_erasing_p0 ()
specifier|public
name|void
name|testCodingDirectBuffer_erasing_p0
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
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingBothBuffers_erasing_d5 ()
specifier|public
name|void
name|testCodingBothBuffers_erasing_d5
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
comment|/**      * Doing in mixed buffer usage model to test if the coders can be repeatedly      * reused with different buffer usage model. This matters as the underlying      * coding buffers are shared, which may have bugs.      */
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

