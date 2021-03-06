begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

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
name|SequenceFile
operator|.
name|CompressionType
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
name|util
operator|.
name|Progressable
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
name|bloom
operator|.
name|DynamicBloomFilter
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
name|bloom
operator|.
name|Filter
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
name|bloom
operator|.
name|Key
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
name|hash
operator|.
name|Hash
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_MAPFILE_BLOOM_SIZE_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_MAPFILE_BLOOM_SIZE_KEY
import|;
end_import

begin_comment
comment|/**  * This class extends {@link MapFile} and provides very much the same  * functionality. However, it uses dynamic Bloom filters to provide  * quick membership test for keys, and it offers a fast version of   * {@link Reader#get(WritableComparable, Writable)} operation, especially in  * case of sparsely populated MapFile-s.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|BloomMapFile
specifier|public
class|class
name|BloomMapFile
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
name|BloomMapFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOOM_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|BLOOM_FILE_NAME
init|=
literal|"bloom"
decl_stmt|;
DECL|field|HASH_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|HASH_COUNT
init|=
literal|5
decl_stmt|;
DECL|method|delete (FileSystem fs, String name)
specifier|public
specifier|static
name|void
name|delete
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Path
name|data
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|MapFile
operator|.
name|DATA_FILE_NAME
argument_list|)
decl_stmt|;
name|Path
name|index
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|MapFile
operator|.
name|INDEX_FILE_NAME
argument_list|)
decl_stmt|;
name|Path
name|bloom
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|BLOOM_FILE_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|index
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|bloom
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|byteArrayForBloomKey (DataOutputBuffer buf)
specifier|private
specifier|static
name|byte
index|[]
name|byteArrayForBloomKey
parameter_list|(
name|DataOutputBuffer
name|buf
parameter_list|)
block|{
name|int
name|cleanLength
init|=
name|buf
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|byte
index|[]
name|ba
init|=
name|buf
operator|.
name|getData
argument_list|()
decl_stmt|;
if|if
condition|(
name|cleanLength
operator|!=
name|ba
operator|.
name|length
condition|)
block|{
name|ba
operator|=
operator|new
name|byte
index|[
name|cleanLength
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ba
argument_list|,
literal|0
argument_list|,
name|cleanLength
argument_list|)
expr_stmt|;
block|}
return|return
name|ba
return|;
block|}
DECL|class|Writer
specifier|public
specifier|static
class|class
name|Writer
extends|extends
name|MapFile
operator|.
name|Writer
block|{
DECL|field|bloomFilter
specifier|private
name|DynamicBloomFilter
name|bloomFilter
decl_stmt|;
DECL|field|numKeys
specifier|private
name|int
name|numKeys
decl_stmt|;
DECL|field|vectorSize
specifier|private
name|int
name|vectorSize
decl_stmt|;
DECL|field|bloomKey
specifier|private
name|Key
name|bloomKey
init|=
operator|new
name|Key
argument_list|()
decl_stmt|;
DECL|field|buf
specifier|private
name|DataOutputBuffer
name|buf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|dir
specifier|private
name|Path
name|dir
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass, Class<? extends Writable> valClass, CompressionType compress, CompressionCodec codec, Progressable progress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|keyClass
argument_list|(
name|keyClass
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|,
name|codec
argument_list|)
argument_list|,
name|progressable
argument_list|(
name|progress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass, Class valClass, CompressionType compress, Progressable progress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|,
name|Class
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|keyClass
argument_list|(
name|keyClass
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|)
argument_list|,
name|progressable
argument_list|(
name|progress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass, Class valClass, CompressionType compress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|,
name|Class
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|keyClass
argument_list|(
name|keyClass
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, WritableComparator comparator, Class valClass, CompressionType compress, CompressionCodec codec, Progressable progress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Class
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|,
name|codec
argument_list|)
argument_list|,
name|progressable
argument_list|(
name|progress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, WritableComparator comparator, Class valClass, CompressionType compress, Progressable progress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Class
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|)
argument_list|,
name|progressable
argument_list|(
name|progress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, WritableComparator comparator, Class valClass, CompressionType compress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Class
name|valClass
parameter_list|,
name|CompressionType
name|compress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, WritableComparator comparator, Class valClass)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Class
name|valClass
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass, Class valClass)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|,
name|Class
name|valClass
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|keyClass
argument_list|(
name|keyClass
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|valClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|Writer (Configuration conf, Path dir, SequenceFile.Writer.Option... options)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|dir
parameter_list|,
name|SequenceFile
operator|.
name|Writer
operator|.
name|Option
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|dir
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|dir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|initBloomFilter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|initBloomFilter (Configuration conf)
specifier|private
specifier|synchronized
name|void
name|initBloomFilter
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|numKeys
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|IO_MAPFILE_BLOOM_SIZE_KEY
argument_list|,
name|IO_MAPFILE_BLOOM_SIZE_DEFAULT
argument_list|)
expr_stmt|;
comment|// vector size should be<code>-kn / (ln(1 - c^(1/k)))</code> bits for
comment|// single key, where<code> is the number of hash functions,
comment|//<code>n</code> is the number of keys and<code>c</code> is the desired
comment|// max. error rate.
comment|// Our desired error rate is by default 0.005, i.e. 0.5%
name|float
name|errorRate
init|=
name|conf
operator|.
name|getFloat
argument_list|(
name|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
argument_list|,
name|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
argument_list|)
decl_stmt|;
name|vectorSize
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
call|(
name|double
call|)
argument_list|(
operator|-
name|HASH_COUNT
operator|*
name|numKeys
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|1.0
operator|-
name|Math
operator|.
name|pow
argument_list|(
name|errorRate
argument_list|,
literal|1.0
operator|/
name|HASH_COUNT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bloomFilter
operator|=
operator|new
name|DynamicBloomFilter
argument_list|(
name|vectorSize
argument_list|,
name|HASH_COUNT
argument_list|,
name|Hash
operator|.
name|getHashType
argument_list|(
name|conf
argument_list|)
argument_list|,
name|numKeys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|append (WritableComparable key, Writable val)
specifier|public
specifier|synchronized
name|void
name|append
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|append
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|buf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|key
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|bloomKey
operator|.
name|set
argument_list|(
name|byteArrayForBloomKey
argument_list|(
name|buf
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|bloomFilter
operator|.
name|add
argument_list|(
name|bloomKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|BLOOM_FILE_NAME
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|bloomFilter
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|MapFile
operator|.
name|Reader
block|{
DECL|field|bloomFilter
specifier|private
name|DynamicBloomFilter
name|bloomFilter
decl_stmt|;
DECL|field|buf
specifier|private
name|DataOutputBuffer
name|buf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
DECL|field|bloomKey
specifier|private
name|Key
name|bloomKey
init|=
operator|new
name|Key
argument_list|()
decl_stmt|;
DECL|method|Reader (Path dir, Configuration conf, SequenceFile.Reader.Option... options)
specifier|public
name|Reader
parameter_list|(
name|Path
name|dir
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SequenceFile
operator|.
name|Reader
operator|.
name|Option
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|initBloomFilter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Reader (FileSystem fs, String dirName, Configuration conf)
specifier|public
name|Reader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Reader (FileSystem fs, String dirName, WritableComparator comparator, Configuration conf, boolean open)
specifier|public
name|Reader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|boolean
name|open
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|conf
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|Reader (FileSystem fs, String dirName, WritableComparator comparator, Configuration conf)
specifier|public
name|Reader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|conf
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initBloomFilter (Path dirName, Configuration conf)
specifier|private
name|void
name|initBloomFilter
parameter_list|(
name|Path
name|dirName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|dirName
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|dirName
argument_list|,
name|BLOOM_FILE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|bloomFilter
operator|=
operator|new
name|DynamicBloomFilter
argument_list|()
expr_stmt|;
name|bloomFilter
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't open BloomFilter: "
operator|+
name|ioe
operator|+
literal|" - fallback to MapFile."
argument_list|)
expr_stmt|;
name|bloomFilter
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Checks if this MapFile has the indicated key. The membership test is      * performed using a Bloom filter, so the result has always non-zero      * probability of false positives.      * @param key key to check      * @return  false iff key doesn't exist, true if key probably exists.      * @throws IOException      */
DECL|method|probablyHasKey (WritableComparable key)
specifier|public
name|boolean
name|probablyHasKey
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bloomFilter
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|buf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|key
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|bloomKey
operator|.
name|set
argument_list|(
name|byteArrayForBloomKey
argument_list|(
name|buf
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
return|return
name|bloomFilter
operator|.
name|membershipTest
argument_list|(
name|bloomKey
argument_list|)
return|;
block|}
comment|/**      * Fast version of the      * {@link MapFile.Reader#get(WritableComparable, Writable)} method. First      * it checks the Bloom filter for the existence of the key, and only if      * present it performs the real get operation. This yields significant      * performance improvements for get operations on sparsely populated files.      */
annotation|@
name|Override
DECL|method|get (WritableComparable key, Writable val)
specifier|public
specifier|synchronized
name|Writable
name|get
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|val
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|probablyHasKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
return|;
block|}
comment|/**      * Retrieve the Bloom filter used by this instance of the Reader.      * @return a Bloom filter (see {@link Filter})      */
DECL|method|getBloomFilter ()
specifier|public
name|Filter
name|getBloomFilter
parameter_list|()
block|{
return|return
name|bloomFilter
return|;
block|}
block|}
block|}
end_class

end_unit

