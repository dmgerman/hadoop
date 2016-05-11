begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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

begin_comment
comment|/**  * StorageStatistics contains statistics data for a FileSystem or FileContext  * instance.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|StorageStatistics
specifier|public
specifier|abstract
class|class
name|StorageStatistics
block|{
comment|/**    * A 64-bit storage statistic.    */
DECL|class|LongStatistic
specifier|public
specifier|static
class|class
name|LongStatistic
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|long
name|value
decl_stmt|;
DECL|method|LongStatistic (String name, long value)
specifier|public
name|LongStatistic
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * @return    The name of this statistic.      */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @return    The value of this statistic.      */
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|StorageStatistics (String name)
specifier|public
name|StorageStatistics
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Get the name of this StorageStatistics object.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Get an iterator over all the currently tracked long statistics.    *    * The values returned will depend on the type of FileSystem or FileContext    * object.  The values do not necessarily reflect a snapshot in time.    */
DECL|method|getLongStatistics ()
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
name|getLongStatistics
parameter_list|()
function_decl|;
comment|/**    * Get the value of a statistic.    *    * @return         null if the statistic is not being tracked or is not a    *                     long statistic.    *                 The value of the statistic, otherwise.    */
DECL|method|getLong (String key)
specifier|public
specifier|abstract
name|Long
name|getLong
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Return true if a statistic is being tracked.    *    * @return         True only if the statistic is being tracked.    */
DECL|method|isTracked (String key)
specifier|public
specifier|abstract
name|boolean
name|isTracked
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_class

end_unit

