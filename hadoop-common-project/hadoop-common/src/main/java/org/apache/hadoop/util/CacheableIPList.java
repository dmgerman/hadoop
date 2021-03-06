begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * CacheableIPList loads a list of subnets from a file.  * The list is cached and the cache can be refreshed by specifying cache timeout.  * A negative value of cache timeout disables any caching.  *  * Thread safe.  */
end_comment

begin_class
DECL|class|CacheableIPList
specifier|public
class|class
name|CacheableIPList
implements|implements
name|IPList
block|{
DECL|field|cacheTimeout
specifier|private
specifier|final
name|long
name|cacheTimeout
decl_stmt|;
DECL|field|cacheExpiryTimeStamp
specifier|private
specifier|volatile
name|long
name|cacheExpiryTimeStamp
decl_stmt|;
DECL|field|ipList
specifier|private
specifier|volatile
name|FileBasedIPList
name|ipList
decl_stmt|;
DECL|method|CacheableIPList (FileBasedIPList ipList, long cacheTimeout)
specifier|public
name|CacheableIPList
parameter_list|(
name|FileBasedIPList
name|ipList
parameter_list|,
name|long
name|cacheTimeout
parameter_list|)
block|{
name|this
operator|.
name|cacheTimeout
operator|=
name|cacheTimeout
expr_stmt|;
name|this
operator|.
name|ipList
operator|=
name|ipList
expr_stmt|;
name|updateCacheExpiryTime
argument_list|()
expr_stmt|;
block|}
comment|/**    * Reloads the ip list    */
DECL|method|reset ()
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|ipList
operator|=
name|ipList
operator|.
name|reload
argument_list|()
expr_stmt|;
name|updateCacheExpiryTime
argument_list|()
expr_stmt|;
block|}
DECL|method|updateCacheExpiryTime ()
specifier|private
name|void
name|updateCacheExpiryTime
parameter_list|()
block|{
if|if
condition|(
name|cacheTimeout
operator|<
literal|0
condition|)
block|{
name|cacheExpiryTimeStamp
operator|=
operator|-
literal|1
expr_stmt|;
comment|// no automatic cache expiry.
block|}
else|else
block|{
name|cacheExpiryTimeStamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|cacheTimeout
expr_stmt|;
block|}
block|}
comment|/**    * Refreshes the ip list    */
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|cacheExpiryTimeStamp
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isIn (String ipAddress)
specifier|public
name|boolean
name|isIn
parameter_list|(
name|String
name|ipAddress
parameter_list|)
block|{
comment|//is cache expired
comment|//Uses Double Checked Locking using volatile
if|if
condition|(
name|cacheExpiryTimeStamp
operator|>=
literal|0
operator|&&
name|cacheExpiryTimeStamp
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//check if cache expired again
if|if
condition|(
name|cacheExpiryTimeStamp
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|ipList
operator|.
name|isIn
argument_list|(
name|ipAddress
argument_list|)
return|;
block|}
block|}
end_class

end_unit

