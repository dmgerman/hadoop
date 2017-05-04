begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * LRU cache with a configurable maximum cache size and access order.  */
end_comment

begin_class
DECL|class|LRUCacheHashMap
specifier|public
class|class
name|LRUCacheHashMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|LinkedHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|// Maximum size of the cache
DECL|field|maxSize
specifier|private
name|int
name|maxSize
decl_stmt|;
comment|/**    * Constructor.    *    * @param maxSize max size of the cache    * @param accessOrder true for access-order, false for insertion-order    */
DECL|method|LRUCacheHashMap (int maxSize, boolean accessOrder)
specifier|public
name|LRUCacheHashMap
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|boolean
name|accessOrder
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
literal|0.75f
argument_list|,
name|accessOrder
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeEldestEntry (Map.Entry<K, V> eldest)
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|maxSize
return|;
block|}
block|}
end_class

end_unit

