begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|sharedcache
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
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Unstable
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|Path
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * A utility class that contains helper methods for dealing with the internal  * shared cache structure.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SharedCacheUtil
specifier|public
class|class
name|SharedCacheUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SharedCacheUtil
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Private
DECL|method|getCacheDepth (Configuration conf)
specifier|public
specifier|static
name|int
name|getCacheDepth
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|cacheDepth
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_NESTED_LEVEL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_NESTED_LEVEL
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheDepth
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Specified cache depth was less than or equal to zero."
operator|+
literal|" Using default value instead. Default: "
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_NESTED_LEVEL
operator|+
literal|", Specified: "
operator|+
name|cacheDepth
argument_list|)
expr_stmt|;
name|cacheDepth
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_NESTED_LEVEL
expr_stmt|;
block|}
return|return
name|cacheDepth
return|;
block|}
annotation|@
name|Private
DECL|method|getCacheEntryPath (int cacheDepth, String cacheRoot, String checksum)
specifier|public
specifier|static
name|String
name|getCacheEntryPath
parameter_list|(
name|int
name|cacheDepth
parameter_list|,
name|String
name|cacheRoot
parameter_list|,
name|String
name|checksum
parameter_list|)
block|{
if|if
condition|(
name|cacheDepth
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The cache depth must be greater than 0. Passed value: "
operator|+
name|cacheDepth
argument_list|)
throw|;
block|}
if|if
condition|(
name|checksum
operator|.
name|length
argument_list|()
operator|<
name|cacheDepth
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The checksum passed was too short: "
operator|+
name|checksum
argument_list|)
throw|;
block|}
comment|// Build the cache entry path to the specified depth. For example, if the
comment|// depth is 3 and the checksum is 3c4f, the path would be:
comment|// SHARED_CACHE_ROOT/3/c/4/3c4f
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|cacheRoot
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheDepth
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|checksum
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
operator|.
name|append
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Private
DECL|method|getCacheEntryGlobPattern (int depth)
specifier|public
specifier|static
name|String
name|getCacheEntryGlobPattern
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|StringBuilder
name|pattern
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|pattern
operator|.
name|append
argument_list|(
literal|"*/"
argument_list|)
expr_stmt|;
block|}
name|pattern
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
return|return
name|pattern
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

