begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|utils
operator|.
name|MetadataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Benchmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Param
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Setup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|State
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|CACHE_10MB_TYPE
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
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|CACHE_1GB_TYPE
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
name|ozone
operator|.
name|genesis
operator|.
name|GenesisUtil
operator|.
name|DEFAULT_TYPE
import|;
end_import

begin_comment
comment|/**  * Measure default metadatastore put performance.  */
end_comment

begin_class
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
DECL|class|BenchMarkMetadataStoreWrites
specifier|public
class|class
name|BenchMarkMetadataStoreWrites
block|{
DECL|field|DATA_LEN
specifier|private
specifier|static
specifier|final
name|int
name|DATA_LEN
init|=
literal|1024
decl_stmt|;
DECL|field|MAX_KEYS
specifier|private
specifier|static
specifier|final
name|long
name|MAX_KEYS
init|=
literal|1024
operator|*
literal|10
decl_stmt|;
DECL|field|store
specifier|private
name|MetadataStore
name|store
decl_stmt|;
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
annotation|@
name|Param
argument_list|(
block|{
name|DEFAULT_TYPE
block|,
name|CACHE_10MB_TYPE
block|,
name|CACHE_1GB_TYPE
block|}
argument_list|)
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
annotation|@
name|Setup
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|data
operator|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
name|DATA_LEN
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|=
name|GenesisUtil
operator|.
name|getMetadataStore
argument_list|(
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|x
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
operator|.
name|nextLong
argument_list|(
literal|0L
argument_list|,
name|MAX_KEYS
argument_list|)
decl_stmt|;
name|store
operator|.
name|put
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
name|x
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

