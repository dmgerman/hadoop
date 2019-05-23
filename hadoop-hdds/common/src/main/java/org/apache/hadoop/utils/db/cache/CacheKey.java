begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db.cache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * CacheKey for the RocksDB table.  * @param<KEY>  */
end_comment

begin_class
DECL|class|CacheKey
specifier|public
class|class
name|CacheKey
parameter_list|<
name|KEY
parameter_list|>
block|{
DECL|field|key
specifier|private
specifier|final
name|KEY
name|key
decl_stmt|;
DECL|method|CacheKey (KEY key)
specifier|public
name|CacheKey
parameter_list|(
name|KEY
name|key
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|key
argument_list|,
literal|"Key Should not be null in CacheKey"
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|KEY
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CacheKey
argument_list|<
name|?
argument_list|>
name|cacheKey
init|=
operator|(
name|CacheKey
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|key
argument_list|,
name|cacheKey
operator|.
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
end_class

end_unit

