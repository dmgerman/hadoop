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
name|checkNotNull
import|;
end_import

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
name|Iterator
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
name|util
operator|.
name|ResourceBundles
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

begin_comment
comment|/**  * An abstract class to provide common implementation for the framework  * counter group in both mapred and mapreduce packages.  *  * @param<T> type of the counter enum class  * @param<C> type of the counter  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FrameworkCounterGroup
specifier|public
specifier|abstract
class|class
name|FrameworkCounterGroup
parameter_list|<
name|T
extends|extends
name|Enum
parameter_list|<
name|T
parameter_list|>
parameter_list|,
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
name|FrameworkCounterGroup
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|enumClass
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|enumClass
decl_stmt|;
comment|// for Enum.valueOf
DECL|field|counters
specifier|private
specifier|final
name|Object
index|[]
name|counters
decl_stmt|;
comment|// local casts are OK and save a class ref
DECL|field|displayName
specifier|private
name|String
name|displayName
init|=
literal|null
decl_stmt|;
comment|/**    * A counter facade for framework counters.    * Use old (which extends new) interface to make compatibility easier.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FrameworkCounter
specifier|public
specifier|static
class|class
name|FrameworkCounter
parameter_list|<
name|T
extends|extends
name|Enum
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|AbstractCounter
block|{
DECL|field|key
specifier|final
name|T
name|key
decl_stmt|;
DECL|field|groupName
specifier|final
name|String
name|groupName
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|method|FrameworkCounter (T ref, String groupName)
specifier|public
name|FrameworkCounter
parameter_list|(
name|T
name|ref
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
name|key
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getKey ()
specifier|public
name|T
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Private
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
name|groupName
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
name|key
operator|.
name|name
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
return|return
name|ResourceBundles
operator|.
name|getCounterName
argument_list|(
name|groupName
argument_list|,
name|getName
argument_list|()
argument_list|,
name|getName
argument_list|()
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|FrameworkCounterGroup (Class<T> enumClass)
specifier|public
name|FrameworkCounterGroup
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|enumClass
parameter_list|)
block|{
name|this
operator|.
name|enumClass
operator|=
name|enumClass
expr_stmt|;
name|T
index|[]
name|enums
init|=
name|enumClass
operator|.
name|getEnumConstants
argument_list|()
decl_stmt|;
name|counters
operator|=
operator|new
name|Object
index|[
name|enums
operator|.
name|length
index|]
expr_stmt|;
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
name|enumClass
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
name|getName
argument_list|()
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
DECL|method|valueOf (String name)
specifier|private
name|T
name|valueOf
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Enum
operator|.
name|valueOf
argument_list|(
name|enumClass
argument_list|,
name|name
argument_list|)
return|;
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
init|=
name|findCounter
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
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
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
operator|+
literal|"is not a known counter."
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
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|name
operator|+
literal|"is not a known counter."
argument_list|)
expr_stmt|;
block|}
return|return
name|counter
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
return|return
name|findCounter
argument_list|(
name|valueOf
argument_list|(
name|counterName
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
try|try
block|{
name|T
name|enumValue
init|=
name|valueOf
argument_list|(
name|counterName
argument_list|)
decl_stmt|;
return|return
name|findCounter
argument_list|(
name|enumValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|findCounter (T key)
specifier|private
name|C
name|findCounter
parameter_list|(
name|T
name|key
parameter_list|)
block|{
name|int
name|i
init|=
name|key
operator|.
name|ordinal
argument_list|()
decl_stmt|;
if|if
condition|(
name|counters
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|counters
index|[
name|i
index|]
operator|=
name|newCounter
argument_list|(
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
name|i
index|]
return|;
block|}
comment|/**    * Abstract factory method for new framework counter    * @param key for the enum value of a counter    * @return a new counter for the key    */
DECL|method|newCounter (T key)
specifier|protected
specifier|abstract
name|C
name|newCounter
parameter_list|(
name|T
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|counters
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|counters
index|[
name|i
index|]
operator|!=
literal|null
condition|)
operator|++
name|n
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
literal|"rawtypes"
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
argument_list|,
literal|"other counter group"
argument_list|)
operator|instanceof
name|FrameworkCounterGroup
argument_list|<
name|?
argument_list|,
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
name|C
name|c
init|=
name|findCounter
argument_list|(
operator|(
operator|(
name|FrameworkCounter
operator|)
name|counter
operator|)
operator|.
name|key
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|c
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
block|}
end_class

begin_comment
comment|/**    * FrameworkGroup ::= #counter (key value)*    */
end_comment

begin_function
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|counters
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Counter
name|counter
init|=
operator|(
name|C
operator|)
name|counters
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|clear
argument_list|()
expr_stmt|;
name|int
name|len
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|T
index|[]
name|enums
init|=
name|enumClass
operator|.
name|getEnumConstants
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|int
name|ord
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|Counter
name|counter
init|=
name|newCounter
argument_list|(
name|enums
index|[
name|ord
index|]
argument_list|)
decl_stmt|;
name|counter
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
name|counters
index|[
name|ord
index|]
operator|=
name|counter
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|clear ()
specifier|private
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|counters
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|counters
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
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
return|return
name|Arrays
operator|.
name|deepHashCode
argument_list|(
operator|new
name|Object
index|[]
block|{
name|enumClass
block|,
name|counters
block|,
name|displayName
block|}
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

