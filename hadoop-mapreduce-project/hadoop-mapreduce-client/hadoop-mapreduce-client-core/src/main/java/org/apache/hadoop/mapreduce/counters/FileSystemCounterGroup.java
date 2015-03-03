begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.counters
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|counters
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|Arrays
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
name|ConcurrentMap
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
name|ConcurrentSkipListMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
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
name|collect
operator|.
name|AbstractIterator
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
name|collect
operator|.
name|Iterators
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
name|collect
operator|.
name|Maps
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
name|io
operator|.
name|WritableUtils
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
name|mapreduce
operator|.
name|Counter
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
name|mapreduce
operator|.
name|FileSystemCounter
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
name|mapreduce
operator|.
name|util
operator|.
name|ResourceBundles
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * An abstract class to provide common implementation of the filesystem  * counter group in both mapred and mapreduce packages.  *  * @param<C> the type of the Counter for the group  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileSystemCounterGroup
specifier|public
specifier|abstract
class|class
name|FileSystemCounterGroup
parameter_list|<
name|C
extends|extends
name|Counter
parameter_list|>
implements|implements
name|CounterGroupBase
argument_list|<
name|C
argument_list|>
block|{
DECL|field|MAX_NUM_SCHEMES
specifier|static
specifier|final
name|int
name|MAX_NUM_SCHEMES
init|=
literal|100
decl_stmt|;
comment|// intern/sanity check
DECL|field|schemes
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|schemes
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
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
name|FileSystemCounterGroup
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// C[] would need Array.newInstance which requires a Class<C> reference.
comment|// Just a few local casts probably worth not having to carry it around.
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
name|map
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|displayName
specifier|private
name|String
name|displayName
decl_stmt|;
DECL|field|NAME_JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|NAME_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
decl_stmt|;
DECL|field|DISP_JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|DISP_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|": "
argument_list|)
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FSCounter
specifier|public
specifier|static
class|class
name|FSCounter
extends|extends
name|AbstractCounter
block|{
DECL|field|scheme
specifier|final
name|String
name|scheme
decl_stmt|;
DECL|field|key
specifier|final
name|FileSystemCounter
name|key
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|method|FSCounter (String scheme, FileSystemCounter ref)
specifier|public
name|FSCounter
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystemCounter
name|ref
parameter_list|)
block|{
name|this
operator|.
name|scheme
operator|=
name|scheme
expr_stmt|;
name|key
operator|=
name|ref
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|scheme
return|;
block|}
annotation|@
name|Private
DECL|method|getFileSystemCounter ()
specifier|public
name|FileSystemCounter
name|getFileSystemCounter
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME_JOINER
operator|.
name|join
argument_list|(
name|scheme
argument_list|,
name|key
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDisplayName ()
specifier|public
name|String
name|getDisplayName
parameter_list|()
block|{
return|return
name|DISP_JOINER
operator|.
name|join
argument_list|(
name|scheme
argument_list|,
name|localizeCounterName
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|localizeCounterName (String counterName)
specifier|protected
name|String
name|localizeCounterName
parameter_list|(
name|String
name|counterName
parameter_list|)
block|{
return|return
name|ResourceBundles
operator|.
name|getCounterName
argument_list|(
name|FileSystemCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|counterName
argument_list|,
name|counterName
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|setValue (long value)
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|increment (long incr)
specifier|public
name|void
name|increment
parameter_list|(
name|long
name|incr
parameter_list|)
block|{
name|value
operator|+=
name|incr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
literal|false
operator|:
literal|"shouldn't be called"
assert|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
literal|false
operator|:
literal|"shouldn't be called"
assert|;
block|}
annotation|@
name|Override
DECL|method|getUnderlyingCounter ()
specifier|public
name|Counter
name|getUnderlyingCounter
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|FileSystemCounter
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDisplayName ()
specifier|public
name|String
name|getDisplayName
parameter_list|()
block|{
if|if
condition|(
name|displayName
operator|==
literal|null
condition|)
block|{
name|displayName
operator|=
name|ResourceBundles
operator|.
name|getCounterGroupName
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|"File System Counters"
argument_list|)
expr_stmt|;
block|}
return|return
name|displayName
return|;
block|}
annotation|@
name|Override
DECL|method|setDisplayName (String displayName)
specifier|public
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
block|{
name|this
operator|.
name|displayName
operator|=
name|displayName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addCounter (C counter)
specifier|public
name|void
name|addCounter
parameter_list|(
name|C
name|counter
parameter_list|)
block|{
name|C
name|ours
decl_stmt|;
if|if
condition|(
name|counter
operator|instanceof
name|FileSystemCounterGroup
operator|.
name|FSCounter
condition|)
block|{
name|FSCounter
name|c
init|=
operator|(
name|FSCounter
operator|)
name|counter
decl_stmt|;
name|ours
operator|=
name|findCounter
argument_list|(
name|c
operator|.
name|scheme
argument_list|,
name|c
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ours
operator|=
name|findCounter
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ours
operator|!=
literal|null
condition|)
block|{
name|ours
operator|.
name|setValue
argument_list|(
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addCounter (String name, String displayName, long value)
specifier|public
name|C
name|addCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|displayName
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|C
name|counter
init|=
name|findCounter
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
name|counter
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|counter
return|;
block|}
comment|// Parse generic counter name into [scheme, key]
DECL|method|parseCounterName (String counterName)
specifier|private
name|String
index|[]
name|parseCounterName
parameter_list|(
name|String
name|counterName
parameter_list|)
block|{
name|int
name|schemeEnd
init|=
name|counterName
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemeEnd
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bad fs counter name"
argument_list|)
throw|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|counterName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|schemeEnd
argument_list|)
block|,
name|counterName
operator|.
name|substring
argument_list|(
name|schemeEnd
operator|+
literal|1
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|findCounter (String counterName, String displayName)
specifier|public
name|C
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|,
name|String
name|displayName
parameter_list|)
block|{
return|return
name|findCounter
argument_list|(
name|counterName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|findCounter (String counterName, boolean create)
specifier|public
name|C
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
try|try
block|{
name|String
index|[]
name|pair
init|=
name|parseCounterName
argument_list|(
name|counterName
argument_list|)
decl_stmt|;
return|return
name|findCounter
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|FileSystemCounter
operator|.
name|valueOf
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|create
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
name|LOG
operator|.
name|warn
argument_list|(
name|counterName
operator|+
literal|" is not a recognized counter."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|findCounter (String counterName)
specifier|public
name|C
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|)
block|{
return|return
name|findCounter
argument_list|(
name|counterName
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|findCounter (String scheme, FileSystemCounter key)
specifier|public
specifier|synchronized
name|C
name|findCounter
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystemCounter
name|key
parameter_list|)
block|{
specifier|final
name|String
name|canonicalScheme
init|=
name|checkScheme
argument_list|(
name|scheme
argument_list|)
decl_stmt|;
name|Object
index|[]
name|counters
init|=
name|map
operator|.
name|get
argument_list|(
name|canonicalScheme
argument_list|)
decl_stmt|;
name|int
name|ord
init|=
name|key
operator|.
name|ordinal
argument_list|()
decl_stmt|;
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
block|{
name|counters
operator|=
operator|new
name|Object
index|[
name|FileSystemCounter
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|canonicalScheme
argument_list|,
name|counters
argument_list|)
expr_stmt|;
name|counters
index|[
name|ord
index|]
operator|=
name|newCounter
argument_list|(
name|canonicalScheme
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|counters
index|[
name|ord
index|]
operator|==
literal|null
condition|)
block|{
name|counters
index|[
name|ord
index|]
operator|=
name|newCounter
argument_list|(
name|canonicalScheme
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|C
operator|)
name|counters
index|[
name|ord
index|]
return|;
block|}
DECL|method|checkScheme (String scheme)
specifier|private
name|String
name|checkScheme
parameter_list|(
name|String
name|scheme
parameter_list|)
block|{
name|String
name|fixed
init|=
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|scheme
argument_list|)
decl_stmt|;
name|String
name|interned
init|=
name|schemes
operator|.
name|putIfAbsent
argument_list|(
name|fixed
argument_list|,
name|fixed
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemes
operator|.
name|size
argument_list|()
operator|>
name|MAX_NUM_SCHEMES
condition|)
block|{
comment|// mistakes or abuses
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"too many schemes? "
operator|+
name|schemes
operator|.
name|size
argument_list|()
operator|+
literal|" when process scheme: "
operator|+
name|scheme
argument_list|)
throw|;
block|}
return|return
name|interned
operator|==
literal|null
condition|?
name|fixed
else|:
name|interned
return|;
block|}
comment|/**    * Abstract factory method to create a file system counter    * @param scheme of the file system    * @param key the enum of the file system counter    * @return a new file system counter    */
DECL|method|newCounter (String scheme, FileSystemCounter key)
specifier|protected
specifier|abstract
name|C
name|newCounter
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystemCounter
name|key
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
index|[]
name|counters
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
name|n
operator|+=
name|numSetCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|incrAllCounters (CounterGroupBase<C> other)
specifier|public
name|void
name|incrAllCounters
parameter_list|(
name|CounterGroupBase
argument_list|<
name|C
argument_list|>
name|other
parameter_list|)
block|{
if|if
condition|(
name|checkNotNull
argument_list|(
name|other
operator|.
name|getUnderlyingGroup
argument_list|()
argument_list|,
literal|"other group"
argument_list|)
operator|instanceof
name|FileSystemCounterGroup
argument_list|<
name|?
argument_list|>
condition|)
block|{
for|for
control|(
name|Counter
name|counter
operator|:
name|other
control|)
block|{
name|FSCounter
name|c
init|=
call|(
name|FSCounter
call|)
argument_list|(
operator|(
name|Counter
operator|)
name|counter
argument_list|)
operator|.
name|getUnderlyingCounter
argument_list|()
decl_stmt|;
name|findCounter
argument_list|(
name|c
operator|.
name|scheme
argument_list|,
name|c
operator|.
name|key
argument_list|)
operator|.
name|increment
argument_list|(
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**    * FileSystemGroup ::= #scheme (scheme #counter (key value)*)*    */
end_comment

begin_function
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// #scheme
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// scheme
comment|// #counter for the above scheme
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|numSetCounters
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|counter
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|counter
operator|==
literal|null
condition|)
continue|continue;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|FSCounter
name|c
init|=
call|(
name|FSCounter
call|)
argument_list|(
operator|(
name|Counter
operator|)
name|counter
argument_list|)
operator|.
name|getUnderlyingCounter
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|c
operator|.
name|key
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
comment|// key
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// value
block|}
block|}
block|}
end_function

begin_function
DECL|method|numSetCounters (Object[] counters)
specifier|private
name|int
name|numSetCounters
parameter_list|(
name|Object
index|[]
name|counters
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|counter
range|:
name|counters
control|)
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
operator|++
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numSchemes
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// #scheme
name|FileSystemCounter
index|[]
name|enums
init|=
name|FileSystemCounter
operator|.
name|values
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
name|numSchemes
condition|;
operator|++
name|i
control|)
block|{
name|String
name|scheme
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// scheme
name|int
name|numCounters
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// #counter
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numCounters
condition|;
operator|++
name|j
control|)
block|{
name|findCounter
argument_list|(
name|scheme
argument_list|,
name|enums
index|[
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
index|]
argument_list|)
comment|// key
operator|.
name|setValue
argument_list|(
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
comment|// value
block|}
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|C
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|C
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|Object
index|[]
argument_list|>
name|it
init|=
name|map
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Object
index|[]
name|counters
init|=
name|it
operator|.
name|hasNext
argument_list|()
condition|?
name|it
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|C
name|computeNext
parameter_list|()
block|{
while|while
condition|(
name|counters
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|i
operator|<
name|counters
operator|.
name|length
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|C
name|counter
init|=
operator|(
name|C
operator|)
name|counters
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
return|return
name|counter
return|;
block|}
name|i
operator|=
literal|0
expr_stmt|;
name|counters
operator|=
name|it
operator|.
name|hasNext
argument_list|()
condition|?
name|it
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|equals (Object genericRight)
specifier|public
specifier|synchronized
name|boolean
name|equals
parameter_list|(
name|Object
name|genericRight
parameter_list|)
block|{
if|if
condition|(
name|genericRight
operator|instanceof
name|CounterGroupBase
argument_list|<
name|?
argument_list|>
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|CounterGroupBase
argument_list|<
name|C
argument_list|>
name|right
init|=
operator|(
name|CounterGroupBase
argument_list|<
name|C
argument_list|>
operator|)
name|genericRight
decl_stmt|;
return|return
name|Iterators
operator|.
name|elementsEqual
argument_list|(
name|iterator
argument_list|()
argument_list|,
name|right
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
specifier|synchronized
name|int
name|hashCode
parameter_list|()
block|{
comment|// need to be deep as counters is an array
name|int
name|hash
init|=
name|FileSystemCounter
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
index|[]
name|counters
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|counters
operator|!=
literal|null
condition|)
name|hash
operator|^=
name|Arrays
operator|.
name|hashCode
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
end_function

unit|}
end_unit

