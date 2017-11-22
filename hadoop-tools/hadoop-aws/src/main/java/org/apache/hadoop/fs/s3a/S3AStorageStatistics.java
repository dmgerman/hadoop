begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
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
name|StorageStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumMap
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Storage statistics for S3A.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3AStorageStatistics
specifier|public
class|class
name|S3AStorageStatistics
extends|extends
name|StorageStatistics
implements|implements
name|Iterable
argument_list|<
name|StorageStatistics
operator|.
name|LongStatistic
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|S3AStorageStatistics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"S3AStorageStatistics"
decl_stmt|;
DECL|field|opsCount
specifier|private
specifier|final
name|Map
argument_list|<
name|Statistic
argument_list|,
name|AtomicLong
argument_list|>
name|opsCount
init|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|Statistic
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|S3AStorageStatistics ()
specifier|public
name|S3AStorageStatistics
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
for|for
control|(
name|Statistic
name|opType
range|:
name|Statistic
operator|.
name|values
argument_list|()
control|)
block|{
name|opsCount
operator|.
name|put
argument_list|(
name|opType
argument_list|,
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Increment a specific counter.    * @param op operation    * @param count increment value    * @return the new value    */
DECL|method|incrementCounter (Statistic op, long count)
specifier|public
name|long
name|incrementCounter
parameter_list|(
name|Statistic
name|op
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|long
name|updated
init|=
name|opsCount
operator|.
name|get
argument_list|(
name|op
argument_list|)
operator|.
name|addAndGet
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} += {}  ->  {}"
argument_list|,
name|op
argument_list|,
name|count
argument_list|,
name|updated
argument_list|)
expr_stmt|;
return|return
name|updated
return|;
block|}
DECL|class|LongIterator
specifier|private
class|class
name|LongIterator
implements|implements
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Statistic
argument_list|,
name|AtomicLong
argument_list|>
argument_list|>
name|iterator
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|opsCount
operator|.
name|entrySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|LongStatistic
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Statistic
argument_list|,
name|AtomicLong
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|LongStatistic
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getSymbol
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
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
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"s3a"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongStatistics ()
specifier|public
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
name|getLongStatistics
parameter_list|()
block|{
return|return
operator|new
name|LongIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|getLongStatistics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLong (String key)
specifier|public
name|Long
name|getLong
parameter_list|(
name|String
name|key
parameter_list|)
block|{
specifier|final
name|Statistic
name|type
init|=
name|Statistic
operator|.
name|fromSymbol
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|type
operator|==
literal|null
condition|?
literal|null
else|:
name|opsCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isTracked (String key)
specifier|public
name|boolean
name|isTracked
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|Statistic
operator|.
name|fromSymbol
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|AtomicLong
name|value
range|:
name|opsCount
operator|.
name|values
argument_list|()
control|)
block|{
name|value
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

