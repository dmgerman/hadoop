begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
name|ChecksumException
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
operator|.
name|getLogger
import|;
end_import

begin_comment
comment|/**  * Performance tests to compare performance of Crc32|Crc32C implementations  * This can be run from the command line with:  *  *   java -cp path/to/test/classes:path/to/common/classes \  *      'org.apache.hadoop.util.Crc32PerformanceTest'  *  *      or  *  *  hadoop org.apache.hadoop.util.Crc32PerformanceTest  *  * If any argument is provided, this test will run with non-directly buffer.  *  * The output is in JIRA table format.  */
end_comment

begin_class
DECL|class|Crc32PerformanceTest
specifier|public
class|class
name|Crc32PerformanceTest
block|{
DECL|field|MB
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|interface|Crc32
interface|interface
name|Crc32
block|{
DECL|method|verifyChunked (ByteBuffer data, int bytesPerCrc, ByteBuffer crcs, String filename, long basePos)
name|void
name|verifyChunked
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|int
name|bytesPerCrc
parameter_list|,
name|ByteBuffer
name|crcs
parameter_list|,
name|String
name|filename
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
function_decl|;
DECL|method|crcType ()
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
function_decl|;
DECL|class|Native
specifier|final
class|class
name|Native
implements|implements
name|Crc32
block|{
annotation|@
name|Override
DECL|method|verifyChunked (ByteBuffer data, int bytesPerSum, ByteBuffer sums, String fileName, long basePos)
specifier|public
name|void
name|verifyChunked
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|int
name|bytesPerSum
parameter_list|,
name|ByteBuffer
name|sums
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
block|{
name|NativeCrc32
operator|.
name|verifyChunkedSums
argument_list|(
name|bytesPerSum
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
operator|.
name|id
argument_list|,
name|sums
argument_list|,
name|data
argument_list|,
name|fileName
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
block|}
DECL|class|NativeC
specifier|final
class|class
name|NativeC
implements|implements
name|Crc32
block|{
annotation|@
name|Override
DECL|method|verifyChunked (ByteBuffer data, int bytesPerSum, ByteBuffer sums, String fileName, long basePos)
specifier|public
name|void
name|verifyChunked
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|int
name|bytesPerSum
parameter_list|,
name|ByteBuffer
name|sums
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
block|{
if|if
condition|(
name|data
operator|.
name|isDirect
argument_list|()
condition|)
block|{
name|NativeCrc32
operator|.
name|verifyChunkedSums
argument_list|(
name|bytesPerSum
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
operator|.
name|id
argument_list|,
name|sums
argument_list|,
name|data
argument_list|,
name|fileName
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|dataOffset
init|=
name|data
operator|.
name|arrayOffset
argument_list|()
operator|+
name|data
operator|.
name|position
argument_list|()
decl_stmt|;
specifier|final
name|int
name|crcsOffset
init|=
name|sums
operator|.
name|arrayOffset
argument_list|()
operator|+
name|sums
operator|.
name|position
argument_list|()
decl_stmt|;
name|NativeCrc32
operator|.
name|verifyChunkedSumsByteArray
argument_list|(
name|bytesPerSum
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
operator|.
name|id
argument_list|,
name|sums
operator|.
name|array
argument_list|()
argument_list|,
name|crcsOffset
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|dataOffset
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
return|;
block|}
block|}
DECL|class|AbstractCrc32
specifier|abstract
class|class
name|AbstractCrc32
parameter_list|<
name|T
extends|extends
name|Checksum
parameter_list|>
implements|implements
name|Crc32
block|{
DECL|method|newAlgorithm ()
specifier|abstract
name|T
name|newAlgorithm
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|verifyChunked (ByteBuffer data, int bytesPerCrc, ByteBuffer sums, String filename, long basePos)
specifier|public
name|void
name|verifyChunked
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|int
name|bytesPerCrc
parameter_list|,
name|ByteBuffer
name|sums
parameter_list|,
name|String
name|filename
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
block|{
specifier|final
name|Checksum
name|algorithm
init|=
name|newAlgorithm
argument_list|()
decl_stmt|;
specifier|final
name|DataChecksum
operator|.
name|Type
name|type
init|=
name|crcType
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|hasArray
argument_list|()
operator|&&
name|sums
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|DataChecksum
operator|.
name|verifyChunked
argument_list|(
name|type
argument_list|,
name|algorithm
argument_list|,
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|bytesPerCrc
argument_list|,
name|sums
operator|.
name|array
argument_list|()
argument_list|,
name|sums
operator|.
name|position
argument_list|()
argument_list|,
name|filename
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DataChecksum
operator|.
name|verifyChunked
argument_list|(
name|type
argument_list|,
name|algorithm
argument_list|,
name|data
argument_list|,
name|bytesPerCrc
argument_list|,
name|sums
argument_list|,
name|filename
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Zip
specifier|final
class|class
name|Zip
extends|extends
name|AbstractCrc32
argument_list|<
name|CRC32
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newAlgorithm ()
specifier|public
name|CRC32
name|newAlgorithm
parameter_list|()
block|{
return|return
operator|new
name|CRC32
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
block|}
DECL|class|ZipC
specifier|final
class|class
name|ZipC
extends|extends
name|AbstractCrc32
argument_list|<
name|Checksum
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newAlgorithm ()
specifier|public
name|Checksum
name|newAlgorithm
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|newCrc32C
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
return|;
block|}
block|}
DECL|class|PureJava
specifier|final
class|class
name|PureJava
extends|extends
name|AbstractCrc32
argument_list|<
name|PureJavaCrc32
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newAlgorithm ()
specifier|public
name|PureJavaCrc32
name|newAlgorithm
parameter_list|()
block|{
return|return
operator|new
name|PureJavaCrc32
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
block|}
DECL|class|PureJavaC
specifier|final
class|class
name|PureJavaC
extends|extends
name|AbstractCrc32
argument_list|<
name|PureJavaCrc32C
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newAlgorithm ()
specifier|public
name|PureJavaCrc32C
name|newAlgorithm
parameter_list|()
block|{
return|return
operator|new
name|PureJavaCrc32C
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|crcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|crcType
parameter_list|()
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
return|;
block|}
block|}
block|}
DECL|field|dataLengthMB
specifier|final
name|int
name|dataLengthMB
decl_stmt|;
DECL|field|trials
specifier|final
name|int
name|trials
decl_stmt|;
DECL|field|direct
specifier|final
name|boolean
name|direct
decl_stmt|;
DECL|field|out
specifier|final
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|field|crcs
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
argument_list|>
name|crcs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Crc32PerformanceTest (final int dataLengthMB, final int trials, final boolean direct)
name|Crc32PerformanceTest
parameter_list|(
specifier|final
name|int
name|dataLengthMB
parameter_list|,
specifier|final
name|int
name|trials
parameter_list|,
specifier|final
name|boolean
name|direct
parameter_list|)
block|{
name|this
operator|.
name|dataLengthMB
operator|=
name|dataLengthMB
expr_stmt|;
name|this
operator|.
name|trials
operator|=
name|trials
expr_stmt|;
name|this
operator|.
name|direct
operator|=
name|direct
expr_stmt|;
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|Zip
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|Shell
operator|.
name|isJavaVersionAtLeast
argument_list|(
literal|9
argument_list|)
condition|)
block|{
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|ZipC
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|PureJava
operator|.
name|class
argument_list|)
expr_stmt|;
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|PureJavaC
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|NativeCrc32
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
if|if
condition|(
name|direct
condition|)
block|{
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|Native
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|crcs
operator|.
name|add
argument_list|(
name|Crc32
operator|.
name|NativeC
operator|.
name|class
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|getLogger
argument_list|(
name|NativeCodeLoader
operator|.
name|class
argument_list|)
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|run ()
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|printSystemProperties
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Data Length = "
operator|+
name|dataLengthMB
operator|+
literal|" MB"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Trials      = "
operator|+
name|trials
argument_list|)
expr_stmt|;
name|doBench
argument_list|(
name|crcs
argument_list|)
expr_stmt|;
name|out
operator|.
name|printf
argument_list|(
literal|"Elapsed %.1fs\n"
argument_list|,
name|secondsElapsed
argument_list|(
name|startTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|isdirect
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|isdirect
operator|=
literal|false
expr_stmt|;
block|}
operator|new
name|Crc32PerformanceTest
argument_list|(
literal|64
argument_list|,
literal|5
argument_list|,
name|isdirect
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|method|printCell (String s, int width, PrintStream outCrc)
specifier|private
specifier|static
name|void
name|printCell
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|width
parameter_list|,
name|PrintStream
name|outCrc
parameter_list|)
block|{
specifier|final
name|int
name|w
init|=
name|s
operator|.
name|length
argument_list|()
operator|>
name|width
condition|?
name|s
operator|.
name|length
argument_list|()
else|:
name|width
decl_stmt|;
name|outCrc
operator|.
name|printf
argument_list|(
literal|" %"
operator|+
name|w
operator|+
literal|"s |"
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|allocateByteBuffer (int length)
specifier|private
name|ByteBuffer
name|allocateByteBuffer
parameter_list|(
name|int
name|length
parameter_list|)
block|{
return|return
name|direct
condition|?
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|length
argument_list|)
else|:
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
return|;
block|}
DECL|method|newData ()
specifier|private
name|ByteBuffer
name|newData
parameter_list|()
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|dataLengthMB
operator|<<
literal|20
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
specifier|final
name|ByteBuffer
name|dataBufs
init|=
name|allocateByteBuffer
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|dataBufs
operator|.
name|mark
argument_list|()
expr_stmt|;
name|dataBufs
operator|.
name|put
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|dataBufs
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|dataBufs
return|;
block|}
DECL|method|computeCrc (ByteBuffer dataBufs, int bytePerCrc, DataChecksum.Type type)
specifier|private
name|ByteBuffer
name|computeCrc
parameter_list|(
name|ByteBuffer
name|dataBufs
parameter_list|,
name|int
name|bytePerCrc
parameter_list|,
name|DataChecksum
operator|.
name|Type
name|type
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
literal|4
operator|*
operator|(
name|dataBufs
operator|.
name|remaining
argument_list|()
operator|-
literal|1
operator|)
operator|/
name|bytePerCrc
operator|+
literal|1
decl_stmt|;
specifier|final
name|ByteBuffer
name|crcBufs
init|=
name|allocateByteBuffer
argument_list|(
name|size
argument_list|)
decl_stmt|;
specifier|final
name|DataChecksum
name|checksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|type
argument_list|,
name|bytePerCrc
argument_list|)
decl_stmt|;
name|checksum
operator|.
name|calculateChunkedSums
argument_list|(
name|dataBufs
argument_list|,
name|crcBufs
argument_list|)
expr_stmt|;
return|return
name|crcBufs
return|;
block|}
DECL|method|computeCrc (Class<? extends Crc32> clazz, ByteBuffer dataBufs, int bytePerCrc)
specifier|private
name|ByteBuffer
name|computeCrc
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|clazz
parameter_list|,
name|ByteBuffer
name|dataBufs
parameter_list|,
name|int
name|bytePerCrc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Constructor
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
specifier|final
name|Crc32
name|crc
init|=
name|ctor
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
literal|4
operator|*
operator|(
name|dataBufs
operator|.
name|remaining
argument_list|()
operator|-
literal|1
operator|)
operator|/
name|bytePerCrc
operator|+
literal|1
decl_stmt|;
specifier|final
name|ByteBuffer
name|crcBufs
init|=
name|allocateByteBuffer
argument_list|(
name|size
argument_list|)
decl_stmt|;
specifier|final
name|DataChecksum
name|checksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|crc
operator|.
name|crcType
argument_list|()
argument_list|,
name|bytePerCrc
argument_list|)
decl_stmt|;
name|checksum
operator|.
name|calculateChunkedSums
argument_list|(
name|dataBufs
argument_list|,
name|crcBufs
argument_list|)
expr_stmt|;
return|return
name|crcBufs
return|;
block|}
DECL|method|doBench (final List<Class<? extends Crc32>> crcTargets)
specifier|private
name|void
name|doBench
parameter_list|(
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
argument_list|>
name|crcTargets
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ByteBuffer
index|[]
name|dataBufs
init|=
operator|new
name|ByteBuffer
index|[
literal|16
index|]
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
name|dataBufs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dataBufs
index|[
name|i
index|]
operator|=
name|newData
argument_list|()
expr_stmt|;
block|}
comment|// Print header
name|out
operator|.
name|printf
argument_list|(
literal|"\n%s Buffer Performance Table"
argument_list|,
name|direct
condition|?
literal|"Direct"
else|:
literal|"Non-direct"
argument_list|)
expr_stmt|;
name|out
operator|.
name|printf
argument_list|(
literal|" (bpc: byte-per-crc in MB/sec; #T: #Theads)\n"
argument_list|)
expr_stmt|;
comment|// Warm up implementations to get jit going.
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|c
range|:
name|crcTargets
control|)
block|{
specifier|final
name|ByteBuffer
index|[]
name|crc32
init|=
block|{
name|computeCrc
argument_list|(
name|c
argument_list|,
name|dataBufs
index|[
literal|0
index|]
argument_list|,
literal|32
argument_list|)
block|}
decl_stmt|;
specifier|final
name|ByteBuffer
index|[]
name|crc512
init|=
block|{
name|computeCrc
argument_list|(
name|c
argument_list|,
name|dataBufs
index|[
literal|0
index|]
argument_list|,
literal|512
argument_list|)
block|}
decl_stmt|;
name|doBench
argument_list|(
name|c
argument_list|,
literal|1
argument_list|,
name|dataBufs
argument_list|,
name|crc32
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|doBench
argument_list|(
name|c
argument_list|,
literal|1
argument_list|,
name|dataBufs
argument_list|,
name|crc512
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
comment|// Test on a variety of sizes with different number of threads
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<=
literal|16
condition|;
name|i
operator|++
control|)
block|{
name|doBench
argument_list|(
name|crcs
argument_list|,
name|dataBufs
argument_list|,
literal|1
operator|<<
name|i
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doBench (final List<Class<? extends Crc32>> crcTargets, final ByteBuffer[] dataBufs, final int bytePerCrc, final PrintStream outCrc)
specifier|private
name|void
name|doBench
parameter_list|(
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
argument_list|>
name|crcTargets
parameter_list|,
specifier|final
name|ByteBuffer
index|[]
name|dataBufs
parameter_list|,
specifier|final
name|int
name|bytePerCrc
parameter_list|,
specifier|final
name|PrintStream
name|outCrc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ByteBuffer
index|[]
name|crcBufs
init|=
operator|new
name|ByteBuffer
index|[
name|dataBufs
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|ByteBuffer
index|[]
name|crcBufsC
init|=
operator|new
name|ByteBuffer
index|[
name|dataBufs
operator|.
name|length
index|]
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
name|dataBufs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|crcBufs
index|[
name|i
index|]
operator|=
name|computeCrc
argument_list|(
name|dataBufs
index|[
name|i
index|]
argument_list|,
name|bytePerCrc
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
argument_list|)
expr_stmt|;
name|crcBufsC
index|[
name|i
index|]
operator|=
name|computeCrc
argument_list|(
name|dataBufs
index|[
name|i
index|]
argument_list|,
name|bytePerCrc
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|numBytesStr
init|=
literal|" bpc "
decl_stmt|;
specifier|final
name|String
name|numThreadsStr
init|=
literal|"#T"
decl_stmt|;
specifier|final
name|String
name|diffStr
init|=
literal|"% diff"
decl_stmt|;
name|outCrc
operator|.
name|print
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|numBytesStr
argument_list|,
literal|0
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|numThreadsStr
argument_list|,
literal|0
argument_list|,
name|outCrc
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
name|crcTargets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|c
init|=
name|crcTargets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|outCrc
operator|.
name|print
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|c
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|8
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|printCell
argument_list|(
name|diffStr
argument_list|,
name|diffStr
operator|.
name|length
argument_list|()
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
block|}
block|}
name|outCrc
operator|.
name|printf
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|numThreads
init|=
literal|1
init|;
name|numThreads
operator|<=
name|dataBufs
operator|.
name|length
condition|;
name|numThreads
operator|<<=
literal|1
control|)
block|{
name|outCrc
operator|.
name|printf
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|bytePerCrc
argument_list|)
argument_list|,
name|numBytesStr
operator|.
name|length
argument_list|()
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|numThreads
argument_list|)
argument_list|,
name|numThreadsStr
operator|.
name|length
argument_list|()
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|BenchResult
argument_list|>
name|previous
init|=
operator|new
name|ArrayList
argument_list|<
name|BenchResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|c
range|:
name|crcTargets
control|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
specifier|final
name|BenchResult
name|result
decl_stmt|;
specifier|final
name|Constructor
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|ctor
init|=
name|c
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
specifier|final
name|Crc32
name|crc
init|=
name|ctor
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|crc
operator|.
name|crcType
argument_list|()
operator|==
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
condition|)
block|{
name|result
operator|=
name|doBench
argument_list|(
name|c
argument_list|,
name|numThreads
argument_list|,
name|dataBufs
argument_list|,
name|crcBufs
argument_list|,
name|bytePerCrc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|doBench
argument_list|(
name|c
argument_list|,
name|numThreads
argument_list|,
name|dataBufs
argument_list|,
name|crcBufsC
argument_list|,
name|bytePerCrc
argument_list|)
expr_stmt|;
block|}
name|printCell
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%9.1f"
argument_list|,
name|result
operator|.
name|mbps
argument_list|)
argument_list|,
name|c
operator|.
name|getSimpleName
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
comment|//compare result with the last previous.
specifier|final
name|int
name|size
init|=
name|previous
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|BenchResult
name|p
init|=
name|previous
operator|.
name|get
argument_list|(
name|size
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|diff
init|=
operator|(
name|result
operator|.
name|mbps
operator|-
name|p
operator|.
name|mbps
operator|)
operator|/
name|p
operator|.
name|mbps
operator|*
literal|100
decl_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%5.1f%%"
argument_list|,
name|diff
argument_list|)
argument_list|,
name|diffStr
operator|.
name|length
argument_list|()
argument_list|,
name|outCrc
argument_list|)
expr_stmt|;
block|}
name|previous
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|outCrc
operator|.
name|printf
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doBench (Class<? extends Crc32> clazz, final int numThreads, final ByteBuffer[] dataBufs, final ByteBuffer[] crcBufs, final int bytePerCrc)
specifier|private
name|BenchResult
name|doBench
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|int
name|numThreads
parameter_list|,
specifier|final
name|ByteBuffer
index|[]
name|dataBufs
parameter_list|,
specifier|final
name|ByteBuffer
index|[]
name|crcBufs
parameter_list|,
specifier|final
name|int
name|bytePerCrc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
specifier|final
name|BenchResult
index|[]
name|results
init|=
operator|new
name|BenchResult
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|Constructor
argument_list|<
name|?
extends|extends
name|Crc32
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Crc32
name|crc
init|=
name|ctor
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|long
name|byteProcessed
init|=
name|dataBufs
index|[
name|i
index|]
operator|.
name|remaining
argument_list|()
operator|*
name|trials
decl_stmt|;
specifier|final
name|int
name|index
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
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
name|trials
condition|;
name|i
operator|++
control|)
block|{
name|dataBufs
index|[
name|index
index|]
operator|.
name|mark
argument_list|()
expr_stmt|;
name|crcBufs
index|[
name|index
index|]
operator|.
name|mark
argument_list|()
expr_stmt|;
try|try
block|{
name|crc
operator|.
name|verifyChunked
argument_list|(
name|dataBufs
index|[
name|index
index|]
argument_list|,
name|bytePerCrc
argument_list|,
name|crcBufs
index|[
name|index
index|]
argument_list|,
name|crc
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|dataBufs
index|[
name|index
index|]
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|results
index|[
name|index
index|]
operator|=
operator|new
name|BenchResult
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|dataBufs
index|[
name|index
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
name|crcBufs
index|[
name|index
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|secsElapsed
init|=
name|secondsElapsed
argument_list|(
name|startTime
argument_list|)
decl_stmt|;
name|results
index|[
name|index
index|]
operator|=
operator|new
name|BenchResult
argument_list|(
name|byteProcessed
operator|/
name|secsElapsed
operator|/
name|MB
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|double
name|sum
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|results
index|[
name|i
index|]
operator|.
name|getMbps
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|BenchResult
argument_list|(
name|sum
operator|/
name|results
operator|.
name|length
argument_list|)
return|;
block|}
DECL|class|BenchResult
specifier|private
specifier|static
class|class
name|BenchResult
block|{
comment|/** Speed (MB per second). */
DECL|field|mbps
specifier|final
name|double
name|mbps
decl_stmt|;
DECL|field|thrown
specifier|final
name|Throwable
name|thrown
decl_stmt|;
DECL|method|BenchResult (double mbps)
name|BenchResult
parameter_list|(
name|double
name|mbps
parameter_list|)
block|{
name|this
operator|.
name|mbps
operator|=
name|mbps
expr_stmt|;
name|this
operator|.
name|thrown
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|BenchResult (Throwable e)
name|BenchResult
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|this
operator|.
name|mbps
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|thrown
operator|=
name|e
expr_stmt|;
block|}
DECL|method|getMbps ()
name|double
name|getMbps
parameter_list|()
block|{
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|thrown
argument_list|)
throw|;
block|}
return|return
name|mbps
return|;
block|}
block|}
DECL|method|secondsElapsed (final long startTime)
specifier|static
name|double
name|secondsElapsed
parameter_list|(
specifier|final
name|long
name|startTime
parameter_list|)
block|{
return|return
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000000000.0d
return|;
block|}
DECL|method|printSystemProperties (PrintStream outCrc)
specifier|static
name|void
name|printSystemProperties
parameter_list|(
name|PrintStream
name|outCrc
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|names
init|=
block|{
literal|"java.version"
block|,
literal|"java.runtime.name"
block|,
literal|"java.runtime.version"
block|,
literal|"java.vm.version"
block|,
literal|"java.vm.vendor"
block|,
literal|"java.vm.name"
block|,
literal|"java.vm.specification.version"
block|,
literal|"java.specification.version"
block|,
literal|"os.arch"
block|,
literal|"os.name"
block|,
literal|"os.version"
block|}
decl_stmt|;
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
if|if
condition|(
name|n
operator|.
name|length
argument_list|()
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|n
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|Properties
name|p
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|outCrc
operator|.
name|printf
argument_list|(
literal|"%"
operator|+
name|max
operator|+
literal|"s = %s\n"
argument_list|,
name|n
argument_list|,
name|p
operator|.
name|getProperty
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

