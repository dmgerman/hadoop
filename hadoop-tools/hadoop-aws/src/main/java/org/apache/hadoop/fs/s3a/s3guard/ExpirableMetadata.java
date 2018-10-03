begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

begin_comment
comment|/**  * Expirable Metadata abstract class is for storing the field needed for  * metadata classes in S3Guard that could be expired with TTL.  */
end_comment

begin_class
DECL|class|ExpirableMetadata
specifier|public
specifier|abstract
class|class
name|ExpirableMetadata
block|{
DECL|field|lastUpdated
specifier|private
name|long
name|lastUpdated
init|=
literal|0
decl_stmt|;
DECL|method|getLastUpdated ()
specifier|public
name|long
name|getLastUpdated
parameter_list|()
block|{
return|return
name|lastUpdated
return|;
block|}
DECL|method|setLastUpdated (long lastUpdated)
specifier|public
name|void
name|setLastUpdated
parameter_list|(
name|long
name|lastUpdated
parameter_list|)
block|{
name|this
operator|.
name|lastUpdated
operator|=
name|lastUpdated
expr_stmt|;
block|}
DECL|method|isExpired (long ttl, long now)
specifier|public
name|boolean
name|isExpired
parameter_list|(
name|long
name|ttl
parameter_list|,
name|long
name|now
parameter_list|)
block|{
return|return
operator|(
name|lastUpdated
operator|+
name|ttl
operator|)
operator|<=
name|now
return|;
block|}
block|}
end_class

end_unit

