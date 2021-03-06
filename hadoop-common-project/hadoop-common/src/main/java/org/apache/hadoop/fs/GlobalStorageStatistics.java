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
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

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
name|Preconditions
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
import|;
end_import

begin_comment
comment|/**  * Stores global storage statistics objects.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|enum|GlobalStorageStatistics
specifier|public
enum|enum
name|GlobalStorageStatistics
block|{
comment|/**    * The GlobalStorageStatistics singleton.    */
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
comment|/**    * A map of all global StorageStatistics objects, indexed by name.    */
DECL|field|map
specifier|private
specifier|final
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|StorageStatistics
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * A callback API for creating new StorageStatistics instances.    */
DECL|interface|StorageStatisticsProvider
specifier|public
interface|interface
name|StorageStatisticsProvider
block|{
DECL|method|provide ()
name|StorageStatistics
name|provide
parameter_list|()
function_decl|;
block|}
comment|/**    * Get the StorageStatistics object with the given name.    *    * @param name        The storage statistics object name.    * @return            The StorageStatistics object with the given name, or    *                      null if there is none.    */
DECL|method|get (String name)
specifier|public
specifier|synchronized
name|StorageStatistics
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|==
literal|null
condition|?
literal|null
else|:
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Create or return the StorageStatistics object with the given name.    *    * @param name        The storage statistics object name.    * @param provider    An object which can create a new StorageStatistics    *                      object if needed.    * @return            The StorageStatistics object with the given name.    * @throws RuntimeException  If the StorageStatisticsProvider provides a null    *                           object or a new StorageStatistics object with the    *                           wrong name.    */
DECL|method|put (String name, StorageStatisticsProvider provider)
specifier|public
specifier|synchronized
name|StorageStatistics
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|StorageStatisticsProvider
name|provider
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"Storage statistics can not have a null name!"
argument_list|)
expr_stmt|;
name|StorageStatistics
name|stats
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|!=
literal|null
condition|)
block|{
return|return
name|stats
return|;
block|}
name|stats
operator|=
name|provider
operator|.
name|provide
argument_list|()
expr_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"StorageStatisticsProvider for "
operator|+
name|name
operator|+
literal|" should not provide a null StorageStatistics object."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|stats
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"StorageStatisticsProvider for "
operator|+
name|name
operator|+
literal|" provided a StorageStatistics object for "
operator|+
name|stats
operator|.
name|getName
argument_list|()
operator|+
literal|" instead."
argument_list|)
throw|;
block|}
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|stats
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
comment|/**    * Reset all global storage statistics.    */
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|StorageStatistics
name|statistics
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
name|statistics
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get an iterator that we can use to iterate throw all the global storage    * statistics objects.    */
DECL|method|iterator ()
specifier|synchronized
specifier|public
name|Iterator
argument_list|<
name|StorageStatistics
argument_list|>
name|iterator
parameter_list|()
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|StorageStatistics
argument_list|>
name|first
init|=
name|map
operator|.
name|firstEntry
argument_list|()
decl_stmt|;
return|return
operator|new
name|StorageIterator
argument_list|(
operator|(
name|first
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|first
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|class|StorageIterator
specifier|private
class|class
name|StorageIterator
implements|implements
name|Iterator
argument_list|<
name|StorageStatistics
argument_list|>
block|{
DECL|field|next
specifier|private
name|StorageStatistics
name|next
init|=
literal|null
decl_stmt|;
DECL|method|StorageIterator (StorageStatistics first)
name|StorageIterator
parameter_list|(
name|StorageStatistics
name|first
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|first
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|next
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|StorageStatistics
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
synchronized|synchronized
init|(
name|GlobalStorageStatistics
operator|.
name|this
init|)
block|{
name|StorageStatistics
name|cur
init|=
name|next
decl_stmt|;
name|Entry
argument_list|<
name|String
argument_list|,
name|StorageStatistics
argument_list|>
name|nextEntry
init|=
name|map
operator|.
name|higherEntry
argument_list|(
name|cur
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|next
operator|=
operator|(
name|nextEntry
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|nextEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
return|return
name|cur
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_enum

end_unit

