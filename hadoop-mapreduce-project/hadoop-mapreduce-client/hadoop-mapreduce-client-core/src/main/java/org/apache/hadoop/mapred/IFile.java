begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|DataInputStream
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
name|EOFException
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
name|io
operator|.
name|InputStream
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
name|fs
operator|.
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileSystem
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
name|DataOutputBuffer
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
name|IOUtils
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
name|compress
operator|.
name|CodecPool
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
name|compress
operator|.
name|CompressionCodec
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
name|compress
operator|.
name|CompressionOutputStream
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
name|compress
operator|.
name|Compressor
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
name|compress
operator|.
name|Decompressor
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

begin_comment
comment|/**  *<code>IFile</code> is the simple<key-len, value-len, key, value> format  * for the intermediate map-outputs in Map-Reduce.  *  * There is a<code>Writer</code> to write out map-outputs in this format and   * a<code>Reader</code> to read files of this format.  */
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
DECL|class|IFile
specifier|public
class|class
name|IFile
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
name|IFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EOF_MARKER
specifier|public
specifier|static
specifier|final
name|int
name|EOF_MARKER
init|=
operator|-
literal|1
decl_stmt|;
comment|// End of File Marker
comment|/**    *<code>IFile.Writer</code> to write out intermediate map-outputs.     */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Writer
specifier|public
specifier|static
class|class
name|Writer
parameter_list|<
name|K
extends|extends
name|Object
parameter_list|,
name|V
extends|extends
name|Object
parameter_list|>
block|{
DECL|field|out
name|FSDataOutputStream
name|out
decl_stmt|;
DECL|field|ownOutputStream
name|boolean
name|ownOutputStream
init|=
literal|false
decl_stmt|;
DECL|field|start
name|long
name|start
init|=
literal|0
decl_stmt|;
DECL|field|rawOut
name|FSDataOutputStream
name|rawOut
decl_stmt|;
DECL|field|compressedOut
name|CompressionOutputStream
name|compressedOut
decl_stmt|;
DECL|field|compressor
name|Compressor
name|compressor
decl_stmt|;
DECL|field|compressOutput
name|boolean
name|compressOutput
init|=
literal|false
decl_stmt|;
DECL|field|decompressedBytesWritten
name|long
name|decompressedBytesWritten
init|=
literal|0
decl_stmt|;
DECL|field|compressedBytesWritten
name|long
name|compressedBytesWritten
init|=
literal|0
decl_stmt|;
comment|// Count records written to disk
DECL|field|numRecordsWritten
specifier|private
name|long
name|numRecordsWritten
init|=
literal|0
decl_stmt|;
DECL|field|writtenRecordsCounter
specifier|private
specifier|final
name|Counters
operator|.
name|Counter
name|writtenRecordsCounter
decl_stmt|;
DECL|field|checksumOut
name|IFileOutputStream
name|checksumOut
decl_stmt|;
DECL|field|keyClass
name|Class
argument_list|<
name|K
argument_list|>
name|keyClass
decl_stmt|;
DECL|field|valueClass
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
decl_stmt|;
DECL|field|keySerializer
name|Serializer
argument_list|<
name|K
argument_list|>
name|keySerializer
decl_stmt|;
DECL|field|valueSerializer
name|Serializer
argument_list|<
name|V
argument_list|>
name|valueSerializer
decl_stmt|;
DECL|field|buffer
name|DataOutputBuffer
name|buffer
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
DECL|method|Writer (Configuration conf, FSDataOutputStream out, Class<K> keyClass, Class<V> valueClass, CompressionCodec codec, Counters.Counter writesCounter)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSDataOutputStream
name|out
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Counters
operator|.
name|Counter
name|writesCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|out
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|,
name|codec
argument_list|,
name|writesCounter
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Writer (Counters.Counter writesCounter)
specifier|protected
name|Writer
parameter_list|(
name|Counters
operator|.
name|Counter
name|writesCounter
parameter_list|)
block|{
name|writtenRecordsCounter
operator|=
name|writesCounter
expr_stmt|;
block|}
DECL|method|Writer (Configuration conf, FSDataOutputStream out, Class<K> keyClass, Class<V> valueClass, CompressionCodec codec, Counters.Counter writesCounter, boolean ownOutputStream)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSDataOutputStream
name|out
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Counters
operator|.
name|Counter
name|writesCounter
parameter_list|,
name|boolean
name|ownOutputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writtenRecordsCounter
operator|=
name|writesCounter
expr_stmt|;
name|this
operator|.
name|checksumOut
operator|=
operator|new
name|IFileOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|rawOut
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|this
operator|.
name|rawOut
operator|.
name|getPos
argument_list|()
expr_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|compressor
operator|=
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|compressor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|compressedOut
operator|=
name|codec
operator|.
name|createOutputStream
argument_list|(
name|checksumOut
argument_list|,
name|compressor
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|new
name|FSDataOutputStream
argument_list|(
name|this
operator|.
name|compressedOut
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|compressOutput
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not obtain compressor from CodecPool"
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|new
name|FSDataOutputStream
argument_list|(
name|checksumOut
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|out
operator|=
operator|new
name|FSDataOutputStream
argument_list|(
name|checksumOut
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|keyClass
operator|!=
literal|null
condition|)
block|{
name|SerializationFactory
name|serializationFactory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|keySerializer
operator|=
name|serializationFactory
operator|.
name|getSerializer
argument_list|(
name|keyClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|keySerializer
operator|.
name|open
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueSerializer
operator|=
name|serializationFactory
operator|.
name|getSerializer
argument_list|(
name|valueClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueSerializer
operator|.
name|open
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|ownOutputStream
operator|=
name|ownOutputStream
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// When IFile writer is created by BackupStore, we do not have
comment|// Key and Value classes set. So, check before closing the
comment|// serializers
if|if
condition|(
name|keyClass
operator|!=
literal|null
condition|)
block|{
name|keySerializer
operator|.
name|close
argument_list|()
expr_stmt|;
name|valueSerializer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Write EOF_MARKER for key/value length
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|EOF_MARKER
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|EOF_MARKER
argument_list|)
expr_stmt|;
name|decompressedBytesWritten
operator|+=
literal|2
operator|*
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|EOF_MARKER
argument_list|)
expr_stmt|;
comment|//Flush the stream
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|compressOutput
condition|)
block|{
comment|// Flush
name|compressedOut
operator|.
name|finish
argument_list|()
expr_stmt|;
name|compressedOut
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
comment|// Close the underlying stream iff we own it...
if|if
condition|(
name|ownOutputStream
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Write the checksum
name|checksumOut
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
name|compressedBytesWritten
operator|=
name|rawOut
operator|.
name|getPos
argument_list|()
operator|-
name|start
expr_stmt|;
if|if
condition|(
name|compressOutput
condition|)
block|{
comment|// Return back the compressor
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|compressor
argument_list|)
expr_stmt|;
name|compressor
operator|=
literal|null
expr_stmt|;
block|}
name|out
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|writtenRecordsCounter
operator|!=
literal|null
condition|)
block|{
name|writtenRecordsCounter
operator|.
name|increment
argument_list|(
name|numRecordsWritten
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|append (K key, V value)
specifier|public
name|void
name|append
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|.
name|getClass
argument_list|()
operator|!=
name|keyClass
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"wrong key class: "
operator|+
name|key
operator|.
name|getClass
argument_list|()
operator|+
literal|" is not "
operator|+
name|keyClass
argument_list|)
throw|;
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|!=
name|valueClass
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"wrong value class: "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|" is not "
operator|+
name|valueClass
argument_list|)
throw|;
comment|// Append the 'key'
name|keySerializer
operator|.
name|serialize
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|int
name|keyLength
init|=
name|buffer
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative key-length not allowed: "
operator|+
name|keyLength
operator|+
literal|" for "
operator|+
name|key
argument_list|)
throw|;
block|}
comment|// Append the 'value'
name|valueSerializer
operator|.
name|serialize
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|valueLength
init|=
name|buffer
operator|.
name|getLength
argument_list|()
operator|-
name|keyLength
decl_stmt|;
if|if
condition|(
name|valueLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative value-length not allowed: "
operator|+
name|valueLength
operator|+
literal|" for "
operator|+
name|value
argument_list|)
throw|;
block|}
comment|// Write the record out
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|keyLength
argument_list|)
expr_stmt|;
comment|// key length
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
comment|// value length
name|out
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// data
comment|// Reset
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Update bytes written
name|decompressedBytesWritten
operator|+=
name|keyLength
operator|+
name|valueLength
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|keyLength
argument_list|)
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|valueLength
argument_list|)
expr_stmt|;
operator|++
name|numRecordsWritten
expr_stmt|;
block|}
DECL|method|append (DataInputBuffer key, DataInputBuffer value)
specifier|public
name|void
name|append
parameter_list|(
name|DataInputBuffer
name|key
parameter_list|,
name|DataInputBuffer
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|keyLength
init|=
name|key
operator|.
name|getLength
argument_list|()
operator|-
name|key
operator|.
name|getPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative key-length not allowed: "
operator|+
name|keyLength
operator|+
literal|" for "
operator|+
name|key
argument_list|)
throw|;
block|}
name|int
name|valueLength
init|=
name|value
operator|.
name|getLength
argument_list|()
operator|-
name|value
operator|.
name|getPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|valueLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Negative value-length not allowed: "
operator|+
name|valueLength
operator|+
literal|" for "
operator|+
name|value
argument_list|)
throw|;
block|}
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|keyLength
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|key
operator|.
name|getData
argument_list|()
argument_list|,
name|key
operator|.
name|getPosition
argument_list|()
argument_list|,
name|keyLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|value
operator|.
name|getData
argument_list|()
argument_list|,
name|value
operator|.
name|getPosition
argument_list|()
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
comment|// Update bytes written
name|decompressedBytesWritten
operator|+=
name|keyLength
operator|+
name|valueLength
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|keyLength
argument_list|)
operator|+
name|WritableUtils
operator|.
name|getVIntSize
argument_list|(
name|valueLength
argument_list|)
expr_stmt|;
operator|++
name|numRecordsWritten
expr_stmt|;
block|}
comment|// Required for mark/reset
DECL|method|getOutputStream ()
specifier|public
name|DataOutputStream
name|getOutputStream
parameter_list|()
block|{
return|return
name|out
return|;
block|}
comment|// Required for mark/reset
DECL|method|updateCountersForExternalAppend (long length)
specifier|public
name|void
name|updateCountersForExternalAppend
parameter_list|(
name|long
name|length
parameter_list|)
block|{
operator|++
name|numRecordsWritten
expr_stmt|;
name|decompressedBytesWritten
operator|+=
name|length
expr_stmt|;
block|}
DECL|method|getRawLength ()
specifier|public
name|long
name|getRawLength
parameter_list|()
block|{
return|return
name|decompressedBytesWritten
return|;
block|}
DECL|method|getCompressedLength ()
specifier|public
name|long
name|getCompressedLength
parameter_list|()
block|{
return|return
name|compressedBytesWritten
return|;
block|}
block|}
comment|/**    *<code>IFile.Reader</code> to read intermediate map-outputs.     */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
parameter_list|<
name|K
extends|extends
name|Object
parameter_list|,
name|V
extends|extends
name|Object
parameter_list|>
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
DECL|field|MAX_VINT_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_VINT_SIZE
init|=
literal|9
decl_stmt|;
comment|// Count records read from disk
DECL|field|numRecordsRead
specifier|private
name|long
name|numRecordsRead
init|=
literal|0
decl_stmt|;
DECL|field|readRecordsCounter
specifier|private
specifier|final
name|Counters
operator|.
name|Counter
name|readRecordsCounter
decl_stmt|;
DECL|field|in
specifier|final
name|InputStream
name|in
decl_stmt|;
comment|// Possibly decompressed stream that we read
DECL|field|decompressor
name|Decompressor
name|decompressor
decl_stmt|;
DECL|field|bytesRead
specifier|public
name|long
name|bytesRead
init|=
literal|0
decl_stmt|;
DECL|field|fileLength
specifier|protected
specifier|final
name|long
name|fileLength
decl_stmt|;
DECL|field|eof
specifier|protected
name|boolean
name|eof
init|=
literal|false
decl_stmt|;
DECL|field|checksumIn
specifier|final
name|IFileInputStream
name|checksumIn
decl_stmt|;
DECL|field|buffer
specifier|protected
name|byte
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
DECL|field|bufferSize
specifier|protected
name|int
name|bufferSize
init|=
name|DEFAULT_BUFFER_SIZE
decl_stmt|;
DECL|field|dataIn
specifier|protected
name|DataInputStream
name|dataIn
decl_stmt|;
DECL|field|recNo
specifier|protected
name|int
name|recNo
init|=
literal|1
decl_stmt|;
DECL|field|currentKeyLength
specifier|protected
name|int
name|currentKeyLength
decl_stmt|;
DECL|field|currentValueLength
specifier|protected
name|int
name|currentValueLength
decl_stmt|;
DECL|field|keyBytes
name|byte
name|keyBytes
index|[]
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/**      * Construct an IFile Reader.      *       * @param conf Configuration File       * @param fs  FileSystem      * @param file Path of the file to be opened. This file should have      *             checksum bytes for the data at the end of the file.      * @param codec codec      * @param readsCounter Counter for records read from disk      * @throws IOException      */
DECL|method|Reader (Configuration conf, FileSystem fs, Path file, CompressionCodec codec, Counters.Counter readsCounter)
specifier|public
name|Reader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|file
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Counters
operator|.
name|Counter
name|readsCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|fs
operator|.
name|open
argument_list|(
name|file
argument_list|)
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|codec
argument_list|,
name|readsCounter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct an IFile Reader.      *       * @param conf Configuration File       * @param in   The input stream      * @param length Length of the data in the stream, including the checksum      *               bytes.      * @param codec codec      * @param readsCounter Counter for records read from disk      * @throws IOException      */
DECL|method|Reader (Configuration conf, FSDataInputStream in, long length, CompressionCodec codec, Counters.Counter readsCounter)
specifier|public
name|Reader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSDataInputStream
name|in
parameter_list|,
name|long
name|length
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Counters
operator|.
name|Counter
name|readsCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|readRecordsCounter
operator|=
name|readsCounter
expr_stmt|;
name|checksumIn
operator|=
operator|new
name|IFileInputStream
argument_list|(
name|in
argument_list|,
name|length
argument_list|,
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|decompressor
operator|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
expr_stmt|;
if|if
condition|(
name|decompressor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|in
operator|=
name|codec
operator|.
name|createInputStream
argument_list|(
name|checksumIn
argument_list|,
name|decompressor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not obtain decompressor from CodecPool"
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|checksumIn
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|in
operator|=
name|checksumIn
expr_stmt|;
block|}
name|this
operator|.
name|dataIn
operator|=
operator|new
name|DataInputStream
argument_list|(
name|this
operator|.
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileLength
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|bufferSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|fileLength
operator|-
name|checksumIn
operator|.
name|getSize
argument_list|()
return|;
block|}
DECL|method|getPosition ()
specifier|public
name|long
name|getPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|checksumIn
operator|.
name|getPosition
argument_list|()
return|;
block|}
comment|/**      * Read upto len bytes into buf starting at offset off.      *       * @param buf buffer       * @param off offset      * @param len length of buffer      * @return the no. of bytes read      * @throws IOException      */
DECL|method|readData (byte[] buf, int off, int len)
specifier|private
name|int
name|readData
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bytesRead
operator|<
name|len
condition|)
block|{
name|int
name|n
init|=
name|IOUtils
operator|.
name|wrappedReadForCompressedData
argument_list|(
name|in
argument_list|,
name|buf
argument_list|,
name|off
operator|+
name|bytesRead
argument_list|,
name|len
operator|-
name|bytesRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
return|return
name|bytesRead
return|;
block|}
name|bytesRead
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
DECL|method|positionToNextRecord (DataInput dIn)
specifier|protected
name|boolean
name|positionToNextRecord
parameter_list|(
name|DataInput
name|dIn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sanity check
if|if
condition|(
name|eof
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Completed reading "
operator|+
name|bytesRead
argument_list|)
throw|;
block|}
comment|// Read key and value lengths
name|currentKeyLength
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|dIn
argument_list|)
expr_stmt|;
name|currentValueLength
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|dIn
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
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
expr_stmt|;
comment|// Check for EOF
if|if
condition|(
name|currentKeyLength
operator|==
name|EOF_MARKER
operator|&&
name|currentValueLength
operator|==
name|EOF_MARKER
condition|)
block|{
name|eof
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Sanity check
if|if
condition|(
name|currentKeyLength
operator|<
literal|0
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
literal|": Negative key-length: "
operator|+
name|currentKeyLength
argument_list|)
throw|;
block|}
if|if
condition|(
name|currentValueLength
operator|<
literal|0
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
literal|": Negative value-length: "
operator|+
name|currentValueLength
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
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
if|if
condition|(
operator|!
name|positionToNextRecord
argument_list|(
name|dataIn
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|keyBytes
operator|.
name|length
operator|<
name|currentKeyLength
condition|)
block|{
name|keyBytes
operator|=
operator|new
name|byte
index|[
name|currentKeyLength
operator|<<
literal|1
index|]
expr_stmt|;
block|}
name|int
name|i
init|=
name|readData
argument_list|(
name|keyBytes
argument_list|,
literal|0
argument_list|,
name|currentKeyLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
name|currentKeyLength
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Asked for "
operator|+
name|currentKeyLength
operator|+
literal|" Got: "
operator|+
name|i
argument_list|)
throw|;
block|}
name|key
operator|.
name|reset
argument_list|(
name|keyBytes
argument_list|,
name|currentKeyLength
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|currentKeyLength
expr_stmt|;
return|return
literal|true
return|;
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
specifier|final
name|byte
index|[]
name|valBytes
init|=
operator|(
name|value
operator|.
name|getData
argument_list|()
operator|.
name|length
operator|<
name|currentValueLength
operator|)
condition|?
operator|new
name|byte
index|[
name|currentValueLength
operator|<<
literal|1
index|]
else|:
name|value
operator|.
name|getData
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|readData
argument_list|(
name|valBytes
argument_list|,
literal|0
argument_list|,
name|currentValueLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
name|currentValueLength
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Asked for "
operator|+
name|currentValueLength
operator|+
literal|" Got: "
operator|+
name|i
argument_list|)
throw|;
block|}
name|value
operator|.
name|reset
argument_list|(
name|valBytes
argument_list|,
name|currentValueLength
argument_list|)
expr_stmt|;
comment|// Record the bytes read
name|bytesRead
operator|+=
name|currentValueLength
expr_stmt|;
operator|++
name|recNo
expr_stmt|;
operator|++
name|numRecordsRead
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Close the underlying stream
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Release the buffer
name|dataIn
operator|=
literal|null
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|readRecordsCounter
operator|!=
literal|null
condition|)
block|{
name|readRecordsCounter
operator|.
name|increment
argument_list|(
name|numRecordsRead
argument_list|)
expr_stmt|;
block|}
comment|// Return the decompressor
if|if
condition|(
name|decompressor
operator|!=
literal|null
condition|)
block|{
name|decompressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decompressor
argument_list|)
expr_stmt|;
name|decompressor
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|reset (int offset)
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return;
block|}
DECL|method|disableChecksumValidation ()
specifier|public
name|void
name|disableChecksumValidation
parameter_list|()
block|{
name|checksumIn
operator|.
name|disableChecksumValidation
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

