begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|ErasureCodeNative
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
comment|/**  * Tests for the raw erasure coder benchmark tool.  */
end_comment

begin_class
DECL|class|TestRawErasureCoderBenchmark
specifier|public
class|class
name|TestRawErasureCoderBenchmark
block|{
annotation|@
name|Test
DECL|method|testDummyCoder ()
specifier|public
name|void
name|testDummyCoder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Dummy coder
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"encode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|DUMMY_CODER
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"decode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|DUMMY_CODER
argument_list|,
literal|5
argument_list|,
literal|150
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLegacyRSCoder ()
specifier|public
name|void
name|testLegacyRSCoder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Legacy RS Java coder
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"encode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|LEGACY_RS_CODER
argument_list|,
literal|2
argument_list|,
literal|80
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"decode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|LEGACY_RS_CODER
argument_list|,
literal|5
argument_list|,
literal|300
argument_list|,
literal|350
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRSCoder ()
specifier|public
name|void
name|testRSCoder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// RS Java coder
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"encode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|RS_CODER
argument_list|,
literal|3
argument_list|,
literal|200
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"decode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|RS_CODER
argument_list|,
literal|4
argument_list|,
literal|135
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testISALCoder ()
specifier|public
name|void
name|testISALCoder
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|ErasureCodeNative
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
comment|// ISA-L coder
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"encode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|ISAL_CODER
argument_list|,
literal|5
argument_list|,
literal|300
argument_list|,
literal|64
argument_list|)
expr_stmt|;
name|RawErasureCoderBenchmark
operator|.
name|performBench
argument_list|(
literal|"decode"
argument_list|,
name|RawErasureCoderBenchmark
operator|.
name|CODER
operator|.
name|ISAL_CODER
argument_list|,
literal|6
argument_list|,
literal|200
argument_list|,
literal|128
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

