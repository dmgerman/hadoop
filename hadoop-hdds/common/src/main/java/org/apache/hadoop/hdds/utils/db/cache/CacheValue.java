begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.utils.db.cache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * CacheValue for the RocksDB Table.  * @param<VALUE>  */
end_comment

begin_class
DECL|class|CacheValue
specifier|public
class|class
name|CacheValue
parameter_list|<
name|VALUE
parameter_list|>
block|{
DECL|field|value
specifier|private
name|Optional
argument_list|<
name|VALUE
argument_list|>
name|value
decl_stmt|;
comment|// This value is used for evict entries from cache.
comment|// This value is set with ratis transaction context log entry index.
DECL|field|epoch
specifier|private
name|long
name|epoch
decl_stmt|;
DECL|method|CacheValue (Optional<VALUE> value, long epoch)
specifier|public
name|CacheValue
parameter_list|(
name|Optional
argument_list|<
name|VALUE
argument_list|>
name|value
parameter_list|,
name|long
name|epoch
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|epoch
operator|=
name|epoch
expr_stmt|;
block|}
DECL|method|getCacheValue ()
specifier|public
name|VALUE
name|getCacheValue
parameter_list|()
block|{
return|return
name|value
operator|.
name|orNull
argument_list|()
return|;
block|}
DECL|method|getEpoch ()
specifier|public
name|long
name|getEpoch
parameter_list|()
block|{
return|return
name|epoch
return|;
block|}
block|}
end_class

end_unit

