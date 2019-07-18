begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|db
operator|.
name|cache
operator|.
name|CacheResult
operator|.
name|CacheStatus
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
name|db
operator|.
name|cache
operator|.
name|TableCacheImpl
operator|.
name|CacheCleanupPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * Cache used for RocksDB tables.  * @param<CACHEKEY>  * @param<CACHEVALUE>  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Evolving
DECL|interface|TableCache
specifier|public
interface|interface
name|TableCache
parameter_list|<
name|CACHEKEY
extends|extends
name|CacheKey
parameter_list|,
name|CACHEVALUE
extends|extends
name|CacheValue
parameter_list|>
block|{
comment|/**    * Return the value for the key if it is present, otherwise return null.    * @param cacheKey    * @return CACHEVALUE    */
DECL|method|get (CACHEKEY cacheKey)
name|CACHEVALUE
name|get
parameter_list|(
name|CACHEKEY
name|cacheKey
parameter_list|)
function_decl|;
comment|/**    * Add an entry to the cache, if the key already exists it overrides.    * @param cacheKey    * @param value    */
DECL|method|put (CACHEKEY cacheKey, CACHEVALUE value)
name|void
name|put
parameter_list|(
name|CACHEKEY
name|cacheKey
parameter_list|,
name|CACHEVALUE
name|value
parameter_list|)
function_decl|;
comment|/**    * Removes all the entries from the cache which are having epoch value less    * than or equal to specified epoch value.    *    * If clean up policy is NEVER, this is a do nothing operation.    * If clean up policy is MANUAL, it is caller responsibility to cleanup the    * cache before calling cleanup.    * @param epoch    */
DECL|method|cleanup (long epoch)
name|void
name|cleanup
parameter_list|(
name|long
name|epoch
parameter_list|)
function_decl|;
comment|/**    * Return the size of the cache.    * @return size    */
DECL|method|size ()
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Return an iterator for the cache.    * @return iterator of the underlying cache for the table.    */
DECL|method|iterator ()
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|CACHEKEY
argument_list|,
name|CACHEVALUE
argument_list|>
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Check key exist in cache or not.    *    * If it exists return CacheResult with value and status as    * {@link CacheStatus#EXISTS}    *    * If it does not exist:    *  If cache clean up policy is    *  {@link TableCacheImpl.CacheCleanupPolicy#NEVER} it means table cache is    *  full cache. It return's {@link CacheResult} with null    *  and status as {@link CacheStatus#NOT_EXIST}.    *    *  If cache clean up policy is {@link CacheCleanupPolicy#MANUAL} it means    *  table cache is partial cache. It return's {@link CacheResult} with    *  null and status as MAY_EXIST.    *    * @param cachekey    */
DECL|method|lookup (CACHEKEY cachekey)
name|CacheResult
argument_list|<
name|CACHEVALUE
argument_list|>
name|lookup
parameter_list|(
name|CACHEKEY
name|cachekey
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

