begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
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
package|;
end_package

begin_comment
comment|/**  * Constants related to the erasure code feature.  */
end_comment

begin_class
DECL|class|ErasureCodeConstants
specifier|public
specifier|final
class|class
name|ErasureCodeConstants
block|{
DECL|method|ErasureCodeConstants ()
specifier|private
name|ErasureCodeConstants
parameter_list|()
block|{   }
DECL|field|DUMMY_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DUMMY_CODEC_NAME
init|=
literal|"dummy"
decl_stmt|;
DECL|field|RS_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RS_CODEC_NAME
init|=
literal|"rs"
decl_stmt|;
DECL|field|RS_LEGACY_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RS_LEGACY_CODEC_NAME
init|=
literal|"rs-legacy"
decl_stmt|;
DECL|field|XOR_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|XOR_CODEC_NAME
init|=
literal|"xor"
decl_stmt|;
DECL|field|HHXOR_CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HHXOR_CODEC_NAME
init|=
literal|"hhxor"
decl_stmt|;
DECL|field|RS_6_3_SCHEMA
specifier|public
specifier|static
specifier|final
name|ECSchema
name|RS_6_3_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|RS_CODEC_NAME
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|)
decl_stmt|;
DECL|field|RS_3_2_SCHEMA
specifier|public
specifier|static
specifier|final
name|ECSchema
name|RS_3_2_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|RS_CODEC_NAME
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|)
decl_stmt|;
DECL|field|RS_6_3_LEGACY_SCHEMA
specifier|public
specifier|static
specifier|final
name|ECSchema
name|RS_6_3_LEGACY_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|RS_LEGACY_CODEC_NAME
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|)
decl_stmt|;
DECL|field|XOR_2_1_SCHEMA
specifier|public
specifier|static
specifier|final
name|ECSchema
name|XOR_2_1_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|XOR_CODEC_NAME
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|RS_10_4_SCHEMA
specifier|public
specifier|static
specifier|final
name|ECSchema
name|RS_10_4_SCHEMA
init|=
operator|new
name|ECSchema
argument_list|(
name|RS_CODEC_NAME
argument_list|,
literal|10
argument_list|,
literal|4
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

