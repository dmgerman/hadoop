begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
package|;
end_package

begin_comment
comment|/**  * constant definition.  */
end_comment

begin_class
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{   }
DECL|field|BLOCK_TMP_FILE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_TMP_FILE_PREFIX
init|=
literal|"cos_"
decl_stmt|;
DECL|field|BLOCK_TMP_FILE_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_TMP_FILE_SUFFIX
init|=
literal|"_local_block"
decl_stmt|;
comment|// The maximum number of files listed in a single COS list request.
DECL|field|COS_MAX_LISTING_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|COS_MAX_LISTING_LENGTH
init|=
literal|999
decl_stmt|;
comment|// The maximum number of parts supported by a multipart uploading.
DECL|field|MAX_PART_NUM
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PART_NUM
init|=
literal|10000
decl_stmt|;
comment|// The maximum size of a part
DECL|field|MAX_PART_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|MAX_PART_SIZE
init|=
operator|(
name|long
operator|)
literal|2
operator|*
name|Unit
operator|.
name|GB
decl_stmt|;
comment|// The minimum size of a part
DECL|field|MIN_PART_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|MIN_PART_SIZE
init|=
operator|(
name|long
operator|)
name|Unit
operator|.
name|MB
decl_stmt|;
DECL|field|COSN_SECRET_ID_ENV
specifier|public
specifier|static
specifier|final
name|String
name|COSN_SECRET_ID_ENV
init|=
literal|"COSN_SECRET_ID"
decl_stmt|;
DECL|field|COSN_SECRET_KEY_ENV
specifier|public
specifier|static
specifier|final
name|String
name|COSN_SECRET_KEY_ENV
init|=
literal|"COSN_SECRET_KEY"
decl_stmt|;
block|}
end_class

end_unit

