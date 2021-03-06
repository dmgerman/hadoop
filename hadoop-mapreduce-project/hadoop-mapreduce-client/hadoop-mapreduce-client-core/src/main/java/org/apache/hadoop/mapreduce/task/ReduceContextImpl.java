begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|Iterator
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
name|io
operator|.
name|BytesWritable
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
name|DataInputBuffer
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
name|RawComparator
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
name|io
operator|.
name|serializer
operator|.
name|Deserializer
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
name|serializer
operator|.
name|SerializationFactory
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
name|serializer
operator|.
name|Serializer
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
name|mapred
operator|.
name|BackupStore
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
name|mapred
operator|.
name|RawKeyValueIterator
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
name|Reducer
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
name|OutputCommitter
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
name|RecordWriter
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
name|ReduceContext
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
name|StatusReporter
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
name|TaskAttemptID
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
name|Progressable
import|;
end_import

begin_comment
comment|/**  * The context passed to the {@link Reducer}.  * @param<KEYIN> the class of the input keys  * @param<VALUEIN> the class of the input values  * @param<KEYOUT> the class of the output keys  * @param<VALUEOUT> the class of the output values  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ReduceContextImpl
specifier|public
class|class
name|ReduceContextImpl
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|,
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
extends|extends
name|TaskInputOutputContextImpl
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
implements|implements
name|ReduceContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
block|{
DECL|field|input
specifier|private
name|RawKeyValueIterator
name|input
decl_stmt|;
DECL|field|inputValueCounter
specifier|private
name|Counter
name|inputValueCounter
decl_stmt|;
DECL|field|inputKeyCounter
specifier|private
name|Counter
name|inputKeyCounter
decl_stmt|;
DECL|field|comparator
specifier|private
name|RawComparator
argument_list|<
name|KEYIN
argument_list|>
name|comparator
decl_stmt|;
DECL|field|key
specifier|private
name|KEYIN
name|key
decl_stmt|;
comment|// current key
DECL|field|value
specifier|private
name|VALUEIN
name|value
decl_stmt|;
comment|// current value
DECL|field|firstValue
specifier|private
name|boolean
name|firstValue
init|=
literal|false
decl_stmt|;
comment|// first value in key
DECL|field|nextKeyIsSame
specifier|private
name|boolean
name|nextKeyIsSame
init|=
literal|false
decl_stmt|;
comment|// more w/ this key
DECL|field|hasMore
specifier|private
name|boolean
name|hasMore
decl_stmt|;
comment|// more in file
DECL|field|reporter
specifier|protected
name|Progressable
name|reporter
decl_stmt|;
DECL|field|keyDeserializer
specifier|private
name|Deserializer
argument_list|<
name|KEYIN
argument_list|>
name|keyDeserializer
decl_stmt|;
DECL|field|valueDeserializer
specifier|private
name|Deserializer
argument_list|<
name|VALUEIN
argument_list|>
name|valueDeserializer
decl_stmt|;
DECL|field|buffer
specifier|private
name|DataInputBuffer
name|buffer
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
DECL|field|currentRawKey
specifier|private
name|BytesWritable
name|currentRawKey
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
DECL|field|iterable
specifier|private
name|ValueIterable
name|iterable
init|=
operator|new
name|ValueIterable
argument_list|()
decl_stmt|;
DECL|field|isMarked
specifier|private
name|boolean
name|isMarked
init|=
literal|false
decl_stmt|;
DECL|field|backupStore
specifier|private
name|BackupStore
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|>
name|backupStore
decl_stmt|;
DECL|field|serializationFactory
specifier|private
specifier|final
name|SerializationFactory
name|serializationFactory
decl_stmt|;
DECL|field|keyClass
specifier|private
specifier|final
name|Class
argument_list|<
name|KEYIN
argument_list|>
name|keyClass
decl_stmt|;
DECL|field|valueClass
specifier|private
specifier|final
name|Class
argument_list|<
name|VALUEIN
argument_list|>
name|valueClass
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|taskid
specifier|private
specifier|final
name|TaskAttemptID
name|taskid
decl_stmt|;
DECL|field|currentKeyLength
specifier|private
name|int
name|currentKeyLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentValueLength
specifier|private
name|int
name|currentValueLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|ReduceContextImpl (Configuration conf, TaskAttemptID taskid, RawKeyValueIterator input, Counter inputKeyCounter, Counter inputValueCounter, RecordWriter<KEYOUT,VALUEOUT> output, OutputCommitter committer, StatusReporter reporter, RawComparator<KEYIN> comparator, Class<KEYIN> keyClass, Class<VALUEIN> valueClass )
specifier|public
name|ReduceContextImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|TaskAttemptID
name|taskid
parameter_list|,
name|RawKeyValueIterator
name|input
parameter_list|,
name|Counter
name|inputKeyCounter
parameter_list|,
name|Counter
name|inputValueCounter
parameter_list|,
name|RecordWriter
argument_list|<
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|output
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|StatusReporter
name|reporter
parameter_list|,
name|RawComparator
argument_list|<
name|KEYIN
argument_list|>
name|comparator
parameter_list|,
name|Class
argument_list|<
name|KEYIN
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|VALUEIN
argument_list|>
name|valueClass
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|,
name|output
argument_list|,
name|committer
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|inputKeyCounter
operator|=
name|inputKeyCounter
expr_stmt|;
name|this
operator|.
name|inputValueCounter
operator|=
name|inputValueCounter
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|serializationFactory
operator|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyDeserializer
operator|=
name|serializationFactory
operator|.
name|getDeserializer
argument_list|(
name|keyClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyDeserializer
operator|.
name|open
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueDeserializer
operator|=
name|serializationFactory
operator|.
name|getDeserializer
argument_list|(
name|valueClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueDeserializer
operator|.
name|open
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|hasMore
operator|=
name|input
operator|.
name|next
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyClass
operator|=
name|keyClass
expr_stmt|;
name|this
operator|.
name|valueClass
operator|=
name|valueClass
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|taskid
operator|=
name|taskid
expr_stmt|;
block|}
comment|/** Start processing next unique key. */
DECL|method|nextKey ()
specifier|public
name|boolean
name|nextKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
while|while
condition|(
name|hasMore
operator|&&
name|nextKeyIsSame
condition|)
block|{
name|nextKeyValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|hasMore
condition|)
block|{
if|if
condition|(
name|inputKeyCounter
operator|!=
literal|null
condition|)
block|{
name|inputKeyCounter
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|nextKeyValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Advance to the next key/value pair.    */
annotation|@
name|Override
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|hasMore
condition|)
block|{
name|key
operator|=
literal|null
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
name|firstValue
operator|=
operator|!
name|nextKeyIsSame
expr_stmt|;
name|DataInputBuffer
name|nextKey
init|=
name|input
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|currentRawKey
operator|.
name|set
argument_list|(
name|nextKey
operator|.
name|getData
argument_list|()
argument_list|,
name|nextKey
operator|.
name|getPosition
argument_list|()
argument_list|,
name|nextKey
operator|.
name|getLength
argument_list|()
operator|-
name|nextKey
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|currentRawKey
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|currentRawKey
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|=
name|keyDeserializer
operator|.
name|deserialize
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|nextVal
init|=
name|input
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|nextVal
operator|.
name|getData
argument_list|()
argument_list|,
name|nextVal
operator|.
name|getPosition
argument_list|()
argument_list|,
name|nextVal
operator|.
name|getLength
argument_list|()
operator|-
name|nextVal
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|valueDeserializer
operator|.
name|deserialize
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|currentKeyLength
operator|=
name|nextKey
operator|.
name|getLength
argument_list|()
operator|-
name|nextKey
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|currentValueLength
operator|=
name|nextVal
operator|.
name|getLength
argument_list|()
operator|-
name|nextVal
operator|.
name|getPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|isMarked
condition|)
block|{
name|backupStore
operator|.
name|write
argument_list|(
name|nextKey
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
block|}
name|hasMore
operator|=
name|input
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasMore
condition|)
block|{
name|nextKey
operator|=
name|input
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|nextKeyIsSame
operator|=
name|comparator
operator|.
name|compare
argument_list|(
name|currentRawKey
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|currentRawKey
operator|.
name|getLength
argument_list|()
argument_list|,
name|nextKey
operator|.
name|getData
argument_list|()
argument_list|,
name|nextKey
operator|.
name|getPosition
argument_list|()
argument_list|,
name|nextKey
operator|.
name|getLength
argument_list|()
operator|-
name|nextKey
operator|.
name|getPosition
argument_list|()
argument_list|)
operator|==
literal|0
expr_stmt|;
block|}
else|else
block|{
name|nextKeyIsSame
operator|=
literal|false
expr_stmt|;
block|}
name|inputValueCounter
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|getCurrentKey ()
specifier|public
name|KEYIN
name|getCurrentKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentValue ()
specifier|public
name|VALUEIN
name|getCurrentValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|getBackupStore ()
name|BackupStore
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|>
name|getBackupStore
parameter_list|()
block|{
return|return
name|backupStore
return|;
block|}
DECL|class|ValueIterator
specifier|protected
class|class
name|ValueIterator
implements|implements
name|ReduceContext
operator|.
name|ValueIterator
argument_list|<
name|VALUEIN
argument_list|>
block|{
DECL|field|inReset
specifier|private
name|boolean
name|inReset
init|=
literal|false
decl_stmt|;
DECL|field|clearMarkFlag
specifier|private
name|boolean
name|clearMarkFlag
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|inReset
operator|&&
name|backupStore
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"hasNext failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|firstValue
operator|||
name|nextKeyIsSame
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|VALUEIN
name|next
parameter_list|()
block|{
if|if
condition|(
name|inReset
condition|)
block|{
try|try
block|{
if|if
condition|(
name|backupStore
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|backupStore
operator|.
name|next
argument_list|()
expr_stmt|;
name|DataInputBuffer
name|next
init|=
name|backupStore
operator|.
name|nextValue
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|(
name|next
operator|.
name|getData
argument_list|()
argument_list|,
name|next
operator|.
name|getPosition
argument_list|()
argument_list|,
name|next
operator|.
name|getLength
argument_list|()
operator|-
name|next
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|valueDeserializer
operator|.
name|deserialize
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
else|else
block|{
name|inReset
operator|=
literal|false
expr_stmt|;
name|backupStore
operator|.
name|exitResetMode
argument_list|()
expr_stmt|;
if|if
condition|(
name|clearMarkFlag
condition|)
block|{
name|clearMarkFlag
operator|=
literal|false
expr_stmt|;
name|isMarked
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"next value iterator failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// if this is the first record, we don't need to advance
if|if
condition|(
name|firstValue
condition|)
block|{
name|firstValue
operator|=
literal|false
expr_stmt|;
return|return
name|value
return|;
block|}
comment|// if this isn't the first record and the next key is different, they
comment|// can't advance it here.
if|if
condition|(
operator|!
name|nextKeyIsSame
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"iterate past last value"
argument_list|)
throw|;
block|}
comment|// otherwise, go to the next key/value pair
try|try
block|{
name|nextKeyValue
argument_list|()
expr_stmt|;
return|return
name|value
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"next value iterator failed"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// this is bad, but we can't modify the exception list of java.util
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"next value iterator interrupted"
argument_list|,
name|ie
argument_list|)
throw|;
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
argument_list|(
literal|"remove not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|mark ()
specifier|public
name|void
name|mark
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|getBackupStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|backupStore
operator|=
operator|new
name|BackupStore
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|>
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|)
expr_stmt|;
block|}
name|isMarked
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|inReset
condition|)
block|{
name|backupStore
operator|.
name|reinitialize
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentKeyLength
operator|==
operator|-
literal|1
condition|)
block|{
comment|// The user has not called next() for this iterator yet, so
comment|// there is no current record to mark and copy to backup store.
return|return;
block|}
assert|assert
operator|(
name|currentValueLength
operator|!=
operator|-
literal|1
operator|)
assert|;
name|int
name|requestedSize
init|=
name|currentKeyLength
operator|+
name|currentValueLength
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|currentKeyLength
argument_list|)
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|currentValueLength
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
name|backupStore
operator|.
name|getOutputStream
argument_list|(
name|requestedSize
argument_list|)
decl_stmt|;
name|writeFirstKeyValueBytes
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|backupStore
operator|.
name|updateCounters
argument_list|(
name|requestedSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|backupStore
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We reached the end of an iteration and user calls a
comment|// reset, but a clearMark was called before, just throw
comment|// an exception
if|if
condition|(
name|clearMarkFlag
condition|)
block|{
name|clearMarkFlag
operator|=
literal|false
expr_stmt|;
name|backupStore
operator|.
name|clearMark
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Reset called without a previous mark"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isMarked
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Reset called without a previous mark"
argument_list|)
throw|;
block|}
name|inReset
operator|=
literal|true
expr_stmt|;
name|backupStore
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearMark ()
specifier|public
name|void
name|clearMark
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|getBackupStore
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|inReset
condition|)
block|{
name|clearMarkFlag
operator|=
literal|true
expr_stmt|;
name|backupStore
operator|.
name|clearMark
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|inReset
operator|=
name|isMarked
operator|=
literal|false
expr_stmt|;
name|backupStore
operator|.
name|reinitialize
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This method is called when the reducer moves from one key to       * another.      * @throws IOException      */
DECL|method|resetBackupStore ()
specifier|public
name|void
name|resetBackupStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|getBackupStore
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|inReset
operator|=
name|isMarked
operator|=
literal|false
expr_stmt|;
name|backupStore
operator|.
name|reinitialize
argument_list|()
expr_stmt|;
name|currentKeyLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**      * This method is called to write the record that was most recently      * served (before a call to the mark). Since the framework reads one      * record in advance, to get this record, we serialize the current key      * and value      * @param out      * @throws IOException      */
DECL|method|writeFirstKeyValueBytes (DataOutputStream out)
specifier|private
name|void
name|writeFirstKeyValueBytes
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|(
name|getCurrentKey
argument_list|()
operator|!=
literal|null
operator|&&
name|getCurrentValue
argument_list|()
operator|!=
literal|null
operator|)
assert|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|currentKeyLength
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|currentValueLength
argument_list|)
expr_stmt|;
name|Serializer
argument_list|<
name|KEYIN
argument_list|>
name|keySerializer
init|=
name|serializationFactory
operator|.
name|getSerializer
argument_list|(
name|keyClass
argument_list|)
decl_stmt|;
name|keySerializer
operator|.
name|open
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|keySerializer
operator|.
name|serialize
argument_list|(
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|Serializer
argument_list|<
name|VALUEIN
argument_list|>
name|valueSerializer
init|=
name|serializationFactory
operator|.
name|getSerializer
argument_list|(
name|valueClass
argument_list|)
decl_stmt|;
name|valueSerializer
operator|.
name|open
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|valueSerializer
operator|.
name|serialize
argument_list|(
name|getCurrentValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ValueIterable
specifier|protected
class|class
name|ValueIterable
implements|implements
name|Iterable
argument_list|<
name|VALUEIN
argument_list|>
block|{
DECL|field|iterator
specifier|private
name|ValueIterator
name|iterator
init|=
operator|new
name|ValueIterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|VALUEIN
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterator
return|;
block|}
block|}
comment|/**    * Iterate through the values for the current key, reusing the same value     * object, which is stored in the context.    * @return the series of values associated with the current key. All of the     * objects returned directly and indirectly from this method are reused.    */
specifier|public
DECL|method|getValues ()
name|Iterable
argument_list|<
name|VALUEIN
argument_list|>
name|getValues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|iterable
return|;
block|}
block|}
end_class

end_unit

