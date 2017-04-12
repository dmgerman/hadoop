begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|Preconditions
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
name|Strings
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
name|LevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBIterator
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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * An utility class to get a list of filtered keys.  */
end_comment

begin_class
DECL|class|FilteredKeys
specifier|public
class|class
name|FilteredKeys
implements|implements
name|Closeable
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
name|FilteredKeys
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dbIterator
specifier|private
specifier|final
name|DBIterator
name|dbIterator
decl_stmt|;
DECL|field|filters
specifier|private
specifier|final
name|List
argument_list|<
name|KeyFilter
argument_list|>
name|filters
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|1000
decl_stmt|;
DECL|method|FilteredKeys (LevelDBStore db, int count)
specifier|public
name|FilteredKeys
parameter_list|(
name|LevelDBStore
name|db
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"LeveDBStore cannot be null."
argument_list|)
expr_stmt|;
name|this
operator|.
name|dbIterator
operator|=
name|db
operator|.
name|getIterator
argument_list|()
expr_stmt|;
name|dbIterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
name|this
operator|.
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<
name|KeyFilter
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
block|}
comment|/**    * Adds a key filter which filters keys by a certain criteria.    * Valid key filter is an implementation of {@link KeyFilter} class.    *    * @param filter    */
DECL|method|addKeyFilter (KeyFilter filter)
specifier|public
name|void
name|addKeyFilter
parameter_list|(
name|KeyFilter
name|filter
parameter_list|)
block|{
name|filter
operator|.
name|setDbIterator
argument_list|(
name|dbIterator
argument_list|)
expr_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|filter (String keyName)
specifier|private
name|boolean
name|filter
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
if|if
condition|(
name|filters
operator|!=
literal|null
operator|&&
operator|!
name|filters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|KeyFilter
name|filter
range|:
name|filters
control|)
block|{
if|if
condition|(
operator|!
name|filter
operator|.
name|check
argument_list|(
name|keyName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getFilteredKeys ()
specifier|public
name|List
argument_list|<
name|KeyData
argument_list|>
name|getFilteredKeys
parameter_list|()
block|{
name|List
argument_list|<
name|KeyData
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|KeyData
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|dbIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|<
name|count
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|dbIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|KeyUtils
operator|.
name|getKeyName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
argument_list|(
name|keyName
argument_list|)
condition|)
block|{
try|try
block|{
name|KeyData
name|value
init|=
name|KeyUtils
operator|.
name|getKeyData
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|KeyData
name|data
init|=
operator|new
name|KeyData
argument_list|(
name|value
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring adding an invalid entry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|close ()
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|dbIterator
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|dbIterator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to close levelDB connection."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * An abstract class for all key filters.    */
DECL|class|KeyFilter
specifier|public
specifier|static
specifier|abstract
class|class
name|KeyFilter
block|{
DECL|field|dbIterator
specifier|private
name|DBIterator
name|dbIterator
decl_stmt|;
comment|/**      * Returns if this filter is enabled.      *      * @return true if this filter is enabled, false otherwise.      */
DECL|method|isEnabled ()
specifier|abstract
name|boolean
name|isEnabled
parameter_list|()
function_decl|;
comment|/**      * Filters the element by key name. Returns true if the key      * with the given key name complies with the criteria defined      * in this filter.      *      * @param keyName      * @return true if filter passes and false otherwise.      */
DECL|method|filterKey (String keyName)
specifier|abstract
name|boolean
name|filterKey
parameter_list|(
name|String
name|keyName
parameter_list|)
function_decl|;
comment|/**      * If this filter is enabled, returns true if the key with the      * given key name complies with the criteria defined in this filter;      * if this filter is disabled, always returns true.      *      * @param keyName      * @return true if filter passes and false otherwise.      */
DECL|method|check (String keyName)
specifier|public
name|boolean
name|check
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
return|return
name|isEnabled
argument_list|()
condition|?
name|filterKey
argument_list|(
name|keyName
argument_list|)
else|:
literal|true
return|;
block|}
comment|/**      * Set the {@link DBIterator} this filter used to iterate DB entries.      *      * @param dbIterator      */
DECL|method|setDbIterator (DBIterator dbIterator)
specifier|protected
name|void
name|setDbIterator
parameter_list|(
name|DBIterator
name|dbIterator
parameter_list|)
block|{
name|this
operator|.
name|dbIterator
operator|=
name|dbIterator
expr_stmt|;
block|}
DECL|method|getDbIterator ()
specifier|protected
name|DBIterator
name|getDbIterator
parameter_list|()
block|{
return|return
name|this
operator|.
name|dbIterator
return|;
block|}
block|}
comment|/**    * Filters keys with a previous key name,    * returns only the keys that whose position is behind the given key name.    */
DECL|class|PreKeyFilter
specifier|public
specifier|static
class|class
name|PreKeyFilter
extends|extends
name|KeyFilter
block|{
DECL|field|prevKey
specifier|private
specifier|final
name|String
name|prevKey
decl_stmt|;
DECL|field|preKeyFound
specifier|private
name|boolean
name|preKeyFound
init|=
literal|false
decl_stmt|;
DECL|method|PreKeyFilter (LevelDBStore db, String prevKey)
specifier|public
name|PreKeyFilter
parameter_list|(
name|LevelDBStore
name|db
parameter_list|,
name|String
name|prevKey
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"LevelDB store cannot be null."
argument_list|)
expr_stmt|;
name|this
operator|.
name|prevKey
operator|=
name|prevKey
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|protected
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prevKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|filterKey (String keyName)
specifier|protected
name|boolean
name|filterKey
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
if|if
condition|(
name|preKeyFound
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|getDbIterator
argument_list|()
operator|.
name|hasPrev
argument_list|()
condition|)
block|{
name|byte
index|[]
name|prevKeyBytes
init|=
name|getDbIterator
argument_list|()
operator|.
name|peekPrev
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|prevKeyActual
init|=
name|KeyUtils
operator|.
name|getKeyName
argument_list|(
name|prevKeyBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevKeyActual
operator|.
name|equals
argument_list|(
name|prevKey
argument_list|)
condition|)
block|{
name|preKeyFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Filters keys by a key name prefix.    */
DECL|class|KeyPrefixFilter
specifier|public
specifier|static
class|class
name|KeyPrefixFilter
extends|extends
name|KeyFilter
block|{
DECL|field|prefix
specifier|private
name|String
name|prefix
init|=
literal|null
decl_stmt|;
DECL|method|KeyPrefixFilter (String prefix)
specifier|public
name|KeyPrefixFilter
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|protected
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|filterKey (String keyName)
specifier|protected
name|boolean
name|filterKey
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
return|return
name|keyName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|?
literal|true
else|:
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

