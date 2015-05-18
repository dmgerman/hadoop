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
comment|/**  * Test raw Reed-solomon coder implemented in Java.  */
end_comment

begin_class
DECL|class|TestRSRawCoder
specifier|public
class|class
name|TestRSRawCoder
extends|extends
name|TestRSRawCoderBase
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
name|RSRawEncoder
operator|.
name|class
expr_stmt|;
name|this
operator|.
name|decoderClass
operator|=
name|RSRawDecoder
operator|.
name|class
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingNoDirectBuffer_10x4_erasing_d0_p0 ()
specifier|public
name|void
name|testCodingNoDirectBuffer_10x4_erasing_d0_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
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
index|[]
block|{
literal|0
block|}
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
DECL|method|testCodingDirectBuffer_10x4_erasing_p1 ()
specifier|public
name|void
name|testCodingDirectBuffer_10x4_erasing_p1
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
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
DECL|method|testCodingDirectBuffer_10x4_erasing_d2 ()
specifier|public
name|void
name|testCodingDirectBuffer_10x4_erasing_d2
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
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
block|{}
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
DECL|method|testCodingDirectBuffer_10x4_erasing_d0_p0 ()
specifier|public
name|void
name|testCodingDirectBuffer_10x4_erasing_d0_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
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
DECL|method|testCodingBothBuffers_10x4_erasing_d0_p0 ()
specifier|public
name|void
name|testCodingBothBuffers_10x4_erasing_d0_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
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
index|[]
block|{
literal|0
block|}
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
annotation|@
name|Test
DECL|method|testCodingDirectBuffer_10x4_erasure_of_d2_d4_p0 ()
specifier|public
name|void
name|testCodingDirectBuffer_10x4_erasure_of_d2_d4_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
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
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCodingDirectBuffer_10x4_erasing_d0_d1_p0_p1 ()
specifier|public
name|void
name|testCodingDirectBuffer_10x4_erasing_d0_d1_p0_p1
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|,
literal|4
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
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
DECL|method|testCodingNoDirectBuffer_3x3_erasing_d0_p0 ()
specifier|public
name|void
name|testCodingNoDirectBuffer_3x3_erasing_d0_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|3
argument_list|,
literal|3
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
index|[]
block|{
literal|0
block|}
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
DECL|method|testCodingDirectBuffer_3x3_erasing_d0_p0 ()
specifier|public
name|void
name|testCodingDirectBuffer_3x3_erasing_d0_p0
parameter_list|()
block|{
name|prepare
argument_list|(
literal|null
argument_list|,
literal|3
argument_list|,
literal|3
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
block|}
block|}
end_class

end_unit

