begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
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
operator|.
name|reduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|IFile
operator|.
name|Reader
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

begin_comment
comment|/**  *<code>IFile.InMemoryReader</code> to read map-outputs present in-memory.  */
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
DECL|class|InMemoryReader
specifier|public
class|class
name|InMemoryReader
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Reader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|taskAttemptId
specifier|private
specifier|final
name|TaskAttemptID
name|taskAttemptId
decl_stmt|;
DECL|field|merger
specifier|private
specifier|final
name|MergeManager
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
decl_stmt|;
DECL|field|memDataIn
name|DataInputBuffer
name|memDataIn
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|method|InMemoryReader (MergeManager<K,V> merger, TaskAttemptID taskAttemptId, byte[] data, int start, int length)
specifier|public
name|InMemoryReader
parameter_list|(
name|MergeManager
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
parameter_list|,
name|TaskAttemptID
name|taskAttemptId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|length
operator|-
name|start
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|merger
operator|=
name|merger
expr_stmt|;
name|this
operator|.
name|taskAttemptId
operator|=
name|taskAttemptId
expr_stmt|;
name|buffer
operator|=
name|data
expr_stmt|;
name|bufferSize
operator|=
operator|(
name|int
operator|)
name|fileLength
expr_stmt|;
name|memDataIn
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset (int offset)
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|memDataIn
operator|.
name|reset
argument_list|(
name|buffer
argument_list|,
name|start
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bytesRead
operator|=
name|offset
expr_stmt|;
name|eof
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPosition ()
specifier|public
name|long
name|getPosition
parameter_list|()
throws|throws
name|IOException
block|{
comment|// InMemoryReader does not initialize streams like Reader, so in.getPos()
comment|// would not work. Instead, return the number of uncompressed bytes read,
comment|// which will be correct since in-memory data is not compressed.
return|return
name|bytesRead
return|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|fileLength
return|;
block|}
DECL|method|dumpOnError ()
specifier|private
name|void
name|dumpOnError
parameter_list|()
block|{
name|File
name|dumpFile
init|=
operator|new
name|File
argument_list|(
literal|"../output/"
operator|+
name|taskAttemptId
operator|+
literal|".dump"
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Dumping corrupt map-output of "
operator|+
name|taskAttemptId
operator|+
literal|" to "
operator|+
name|dumpFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dumpFile
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to dump map-output of "
operator|+
name|taskAttemptId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nextRawKey (DataInputBuffer key)
specifier|public
name|boolean
name|nextRawKey
parameter_list|(
name|DataInputBuffer
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|positionToNextRecord
argument_list|(
name|memDataIn
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Setup the key
name|int
name|pos
init|=
name|memDataIn
operator|.
name|getPosition
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|memDataIn
operator|.
name|getData
argument_list|()
decl_stmt|;
name|key
operator|.
name|reset
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|currentKeyLength
argument_list|)
expr_stmt|;
comment|// Position for the next value
name|long
name|skipped
init|=
name|memDataIn
operator|.
name|skip
argument_list|(
name|currentKeyLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipped
operator|!=
name|currentKeyLength
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Rec# "
operator|+
name|recNo
operator|+
literal|": Failed to skip past key of length: "
operator|+
name|currentKeyLength
argument_list|)
throw|;
block|}
comment|// Record the byte
name|bytesRead
operator|+=
name|currentKeyLength
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|dumpOnError
argument_list|()
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
DECL|method|nextRawValue (DataInputBuffer value)
specifier|public
name|void
name|nextRawValue
parameter_list|(
name|DataInputBuffer
name|value
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|pos
init|=
name|memDataIn
operator|.
name|getPosition
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|memDataIn
operator|.
name|getData
argument_list|()
decl_stmt|;
name|value
operator|.
name|reset
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|currentValueLength
argument_list|)
expr_stmt|;
comment|// Position for the next record
name|long
name|skipped
init|=
name|memDataIn
operator|.
name|skip
argument_list|(
name|currentValueLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipped
operator|!=
name|currentValueLength
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Rec# "
operator|+
name|recNo
operator|+
literal|": Failed to skip past value of length: "
operator|+
name|currentValueLength
argument_list|)
throw|;
block|}
comment|// Record the byte
name|bytesRead
operator|+=
name|currentValueLength
expr_stmt|;
operator|++
name|recNo
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|dumpOnError
argument_list|()
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Release
name|dataIn
operator|=
literal|null
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// Inform the MergeManager
if|if
condition|(
name|merger
operator|!=
literal|null
condition|)
block|{
name|merger
operator|.
name|unreserve
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

