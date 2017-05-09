begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|LinkedBlockingDeque
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
name|TimeUnit
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|TestCodecPool
specifier|public
class|class
name|TestCodecPool
block|{
DECL|field|LEASE_COUNT_ERR
specifier|private
specifier|final
name|String
name|LEASE_COUNT_ERR
init|=
literal|"Incorrect number of leased (de)compressors"
decl_stmt|;
DECL|field|codec
name|DefaultCodec
name|codec
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|codec
operator|=
operator|new
name|DefaultCodec
argument_list|()
expr_stmt|;
name|this
operator|.
name|codec
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCompressorPoolCounts ()
specifier|public
name|void
name|testCompressorPoolCounts
parameter_list|()
block|{
comment|// Get two compressors and return them
name|Compressor
name|comp1
init|=
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|Compressor
name|comp2
init|=
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|2
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|comp2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|1
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|comp1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|comp1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCompressorNotReturnSameInstance ()
specifier|public
name|void
name|testCompressorNotReturnSameInstance
parameter_list|()
block|{
name|Compressor
name|comp
init|=
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Compressor
argument_list|>
name|compressors
init|=
operator|new
name|HashSet
argument_list|<
name|Compressor
argument_list|>
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|compressors
operator|.
name|add
argument_list|(
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|compressors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Compressor
name|compressor
range|:
name|compressors
control|)
block|{
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|compressor
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDecompressorPoolCounts ()
specifier|public
name|void
name|testDecompressorPoolCounts
parameter_list|()
block|{
comment|// Get two decompressors and return them
name|Decompressor
name|decomp1
init|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|Decompressor
name|decomp2
init|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|2
argument_list|,
name|CodecPool
operator|.
name|getLeasedDecompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decomp2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|1
argument_list|,
name|CodecPool
operator|.
name|getLeasedDecompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decomp1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedDecompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decomp1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultiThreadedCompressorPool ()
specifier|public
name|void
name|testMultiThreadedCompressorPool
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|iterations
init|=
literal|4
decl_stmt|;
name|ExecutorService
name|threadpool
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|LinkedBlockingDeque
argument_list|<
name|Compressor
argument_list|>
name|queue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|Compressor
argument_list|>
argument_list|(
literal|2
operator|*
name|iterations
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|consumer
init|=
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Compressor
name|c
init|=
name|queue
operator|.
name|take
argument_list|()
decl_stmt|;
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
operator|!=
literal|null
return|;
block|}
block|}
decl_stmt|;
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|producer
init|=
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Compressor
name|c
init|=
name|CodecPool
operator|.
name|getCompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|queue
operator|.
name|put
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
operator|!=
literal|null
return|;
block|}
block|}
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|threadpool
operator|.
name|submit
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|threadpool
operator|.
name|submit
argument_list|(
name|producer
argument_list|)
expr_stmt|;
block|}
comment|// wait for completion
name|threadpool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|threadpool
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedCompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultiThreadedDecompressorPool ()
specifier|public
name|void
name|testMultiThreadedDecompressorPool
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|iterations
init|=
literal|4
decl_stmt|;
name|ExecutorService
name|threadpool
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|LinkedBlockingDeque
argument_list|<
name|Decompressor
argument_list|>
name|queue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|Decompressor
argument_list|>
argument_list|(
literal|2
operator|*
name|iterations
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|consumer
init|=
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Decompressor
name|dc
init|=
name|queue
operator|.
name|take
argument_list|()
decl_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|dc
argument_list|)
expr_stmt|;
return|return
name|dc
operator|!=
literal|null
return|;
block|}
block|}
decl_stmt|;
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|producer
init|=
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Decompressor
name|c
init|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|queue
operator|.
name|put
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
operator|!=
literal|null
return|;
block|}
block|}
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|threadpool
operator|.
name|submit
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|threadpool
operator|.
name|submit
argument_list|(
name|producer
argument_list|)
expr_stmt|;
block|}
comment|// wait for completion
name|threadpool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|threadpool
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LEASE_COUNT_ERR
argument_list|,
literal|0
argument_list|,
name|CodecPool
operator|.
name|getLeasedDecompressorsCount
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDecompressorNotReturnSameInstance ()
specifier|public
name|void
name|testDecompressorNotReturnSameInstance
parameter_list|()
block|{
name|Decompressor
name|decomp
init|=
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
decl_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decomp
argument_list|)
expr_stmt|;
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decomp
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Decompressor
argument_list|>
name|decompressors
init|=
operator|new
name|HashSet
argument_list|<
name|Decompressor
argument_list|>
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|decompressors
operator|.
name|add
argument_list|(
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|decompressors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Decompressor
name|decompressor
range|:
name|decompressors
control|)
block|{
name|CodecPool
operator|.
name|returnDecompressor
argument_list|(
name|decompressor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

