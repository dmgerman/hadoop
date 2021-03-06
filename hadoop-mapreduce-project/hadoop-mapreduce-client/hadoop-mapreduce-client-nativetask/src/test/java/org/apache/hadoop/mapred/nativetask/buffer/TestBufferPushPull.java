begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.buffer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|buffer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|mapred
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
name|mapred
operator|.
name|Reporter
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
name|nativetask
operator|.
name|DataReceiver
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
name|nativetask
operator|.
name|NativeDataSource
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
name|nativetask
operator|.
name|NativeDataTarget
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
name|nativetask
operator|.
name|handlers
operator|.
name|BufferPullee
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
name|nativetask
operator|.
name|handlers
operator|.
name|BufferPuller
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
name|nativetask
operator|.
name|handlers
operator|.
name|BufferPushee
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
name|nativetask
operator|.
name|handlers
operator|.
name|BufferPusher
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
name|nativetask
operator|.
name|handlers
operator|.
name|IDataLoader
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
name|nativetask
operator|.
name|testutil
operator|.
name|TestInput
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
name|nativetask
operator|.
name|testutil
operator|.
name|TestInput
operator|.
name|KV
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
name|nativetask
operator|.
name|util
operator|.
name|ReadWriteBuffer
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
name|Progress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|class|TestBufferPushPull
specifier|public
class|class
name|TestBufferPushPull
block|{
DECL|field|BUFFER_LENGTH
specifier|public
specifier|static
name|int
name|BUFFER_LENGTH
init|=
literal|100
decl_stmt|;
comment|// 100 bytes
DECL|field|INPUT_KV_COUNT
specifier|public
specifier|static
name|int
name|INPUT_KV_COUNT
init|=
literal|1000
decl_stmt|;
DECL|field|dataInput
specifier|private
name|KV
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
index|[]
name|dataInput
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|this
operator|.
name|dataInput
operator|=
name|TestInput
operator|.
name|getMapInputs
argument_list|(
name|INPUT_KV_COUNT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPush ()
specifier|public
name|void
name|testPush
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
name|BUFFER_LENGTH
index|]
decl_stmt|;
specifier|final
name|InputBuffer
name|input
init|=
operator|new
name|InputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
specifier|final
name|OutputBuffer
name|out
init|=
operator|new
name|OutputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|BytesWritable
argument_list|>
name|iKClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|BytesWritable
argument_list|>
name|iVClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
specifier|final
name|RecordWriterForPush
name|writer
init|=
operator|new
name|RecordWriterForPush
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|BytesWritable
name|key
parameter_list|,
name|BytesWritable
name|value
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|KV
name|expect
init|=
name|dataInput
index|[
name|count
operator|++
index|]
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expect
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expect
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|BufferPushee
name|pushee
init|=
operator|new
name|BufferPushee
argument_list|(
name|iKClass
argument_list|,
name|iVClass
argument_list|,
name|writer
argument_list|)
decl_stmt|;
specifier|final
name|PushTarget
name|handler
init|=
operator|new
name|PushTarget
argument_list|(
name|out
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendData
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|outputLength
init|=
name|out
operator|.
name|length
argument_list|()
decl_stmt|;
name|input
operator|.
name|rewind
argument_list|(
literal|0
argument_list|,
name|outputLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|pushee
operator|.
name|collect
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|BufferPusher
name|pusher
init|=
operator|new
name|BufferPusher
argument_list|(
name|iKClass
argument_list|,
name|iVClass
argument_list|,
name|handler
argument_list|)
decl_stmt|;
name|writer
operator|.
name|reset
argument_list|()
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
name|INPUT_KV_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|pusher
operator|.
name|collect
argument_list|(
name|dataInput
index|[
name|i
index|]
operator|.
name|key
argument_list|,
name|dataInput
index|[
name|i
index|]
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|pusher
operator|.
name|close
argument_list|()
expr_stmt|;
name|pushee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPull ()
specifier|public
name|void
name|testPull
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
name|BUFFER_LENGTH
index|]
decl_stmt|;
specifier|final
name|InputBuffer
name|input
init|=
operator|new
name|InputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
specifier|final
name|OutputBuffer
name|out
init|=
operator|new
name|OutputBuffer
argument_list|(
name|buff
argument_list|)
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|BytesWritable
argument_list|>
name|iKClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|BytesWritable
argument_list|>
name|iVClass
init|=
name|BytesWritable
operator|.
name|class
decl_stmt|;
specifier|final
name|NativeHandlerForPull
name|handler
init|=
operator|new
name|NativeHandlerForPull
argument_list|(
name|input
argument_list|,
name|out
argument_list|)
decl_stmt|;
specifier|final
name|KeyValueIterator
name|iter
init|=
operator|new
name|KeyValueIterator
argument_list|()
decl_stmt|;
specifier|final
name|BufferPullee
name|pullee
init|=
operator|new
name|BufferPullee
argument_list|(
name|iKClass
argument_list|,
name|iVClass
argument_list|,
name|iter
argument_list|,
name|handler
argument_list|)
decl_stmt|;
name|handler
operator|.
name|setDataLoader
argument_list|(
name|pullee
argument_list|)
expr_stmt|;
specifier|final
name|BufferPuller
name|puller
init|=
operator|new
name|BufferPuller
argument_list|(
name|handler
argument_list|)
decl_stmt|;
name|handler
operator|.
name|setDataReceiver
argument_list|(
name|puller
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|puller
operator|.
name|next
argument_list|()
condition|)
block|{
specifier|final
name|DataInputBuffer
name|key
init|=
name|puller
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|DataInputBuffer
name|value
init|=
name|puller
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|BytesWritable
name|keyBytes
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
specifier|final
name|BytesWritable
name|valueBytes
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|keyBytes
operator|.
name|readFields
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|valueBytes
operator|.
name|readFields
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dataInput
index|[
name|count
index|]
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|keyBytes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dataInput
index|[
name|count
index|]
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|valueBytes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|puller
operator|.
name|close
argument_list|()
expr_stmt|;
name|pullee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|PushTarget
specifier|public
specifier|abstract
class|class
name|PushTarget
implements|implements
name|NativeDataTarget
block|{
DECL|field|out
name|OutputBuffer
name|out
decl_stmt|;
DECL|method|PushTarget (OutputBuffer out)
name|PushTarget
parameter_list|(
name|OutputBuffer
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendData ()
specifier|public
specifier|abstract
name|void
name|sendData
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|finishSendData ()
specifier|public
name|void
name|finishSendData
parameter_list|()
throws|throws
name|IOException
block|{
name|sendData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputBuffer ()
specifier|public
name|OutputBuffer
name|getOutputBuffer
parameter_list|()
block|{
return|return
name|out
return|;
block|}
block|}
DECL|class|RecordWriterForPush
specifier|public
specifier|abstract
class|class
name|RecordWriterForPush
implements|implements
name|RecordWriter
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
block|{
DECL|field|count
specifier|protected
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|RecordWriterForPush ()
name|RecordWriterForPush
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|write (BytesWritable key, BytesWritable value)
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|BytesWritable
name|key
parameter_list|,
name|BytesWritable
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|close (Reporter reporter)
specifier|public
name|void
name|close
parameter_list|(
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|class|NativeHandlerForPull
specifier|public
specifier|static
class|class
name|NativeHandlerForPull
implements|implements
name|NativeDataSource
implements|,
name|NativeDataTarget
block|{
DECL|field|in
name|InputBuffer
name|in
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|OutputBuffer
name|out
decl_stmt|;
DECL|field|dataLoader
specifier|private
name|IDataLoader
name|dataLoader
decl_stmt|;
DECL|field|dataReceiver
specifier|private
name|DataReceiver
name|dataReceiver
decl_stmt|;
DECL|method|NativeHandlerForPull (InputBuffer input, OutputBuffer out)
specifier|public
name|NativeHandlerForPull
parameter_list|(
name|InputBuffer
name|input
parameter_list|,
name|OutputBuffer
name|out
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputBuffer ()
specifier|public
name|InputBuffer
name|getInputBuffer
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|setDataReceiver (DataReceiver handler)
specifier|public
name|void
name|setDataReceiver
parameter_list|(
name|DataReceiver
name|handler
parameter_list|)
block|{
name|this
operator|.
name|dataReceiver
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadData ()
specifier|public
name|void
name|loadData
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|dataLoader
operator|.
name|load
argument_list|()
decl_stmt|;
block|}
DECL|method|setDataLoader (IDataLoader dataLoader)
specifier|public
name|void
name|setDataLoader
parameter_list|(
name|IDataLoader
name|dataLoader
parameter_list|)
block|{
name|this
operator|.
name|dataLoader
operator|=
name|dataLoader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendData ()
specifier|public
name|void
name|sendData
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|out
operator|.
name|length
argument_list|()
decl_stmt|;
name|out
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|in
operator|.
name|rewind
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|dataReceiver
operator|.
name|receiveData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishSendData ()
specifier|public
name|void
name|finishSendData
parameter_list|()
throws|throws
name|IOException
block|{
name|dataReceiver
operator|.
name|receiveData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputBuffer ()
specifier|public
name|OutputBuffer
name|getOutputBuffer
parameter_list|()
block|{
return|return
name|this
operator|.
name|out
return|;
block|}
block|}
DECL|class|KeyValueIterator
specifier|public
class|class
name|KeyValueIterator
implements|implements
name|RawKeyValueIterator
block|{
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|key
name|BytesWritable
name|key
decl_stmt|;
DECL|field|value
name|BytesWritable
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|getKey ()
specifier|public
name|DataInputBuffer
name|getKey
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|convert
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|DataInputBuffer
name|getValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|convert
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|convert (BytesWritable b)
specifier|private
name|DataInputBuffer
name|convert
parameter_list|(
name|BytesWritable
name|b
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|b
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|array
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
specifier|final
name|DataInputBuffer
name|result
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|reset
argument_list|(
name|array
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|<
name|INPUT_KV_COUNT
condition|)
block|{
name|key
operator|=
name|dataInput
index|[
name|count
index|]
operator|.
name|key
expr_stmt|;
name|value
operator|=
name|dataInput
index|[
name|count
index|]
operator|.
name|key
expr_stmt|;
name|count
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|Progress
name|getProgress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

