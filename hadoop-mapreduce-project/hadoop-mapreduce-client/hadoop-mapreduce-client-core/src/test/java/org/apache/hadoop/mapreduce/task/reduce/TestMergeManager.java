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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|net
operator|.
name|URISyntaxException
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
name|LinkedList
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
name|Random
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
name|BrokenBarrierException
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
name|CyclicBarrier
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
name|atomic
operator|.
name|AtomicInteger
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
name|LocalFileSystem
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
name|BoundedByteArrayOutputStream
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
name|IntWritable
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
name|Text
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
name|JobConf
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
name|MROutputFiles
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
name|MapOutputFile
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
name|MRJobConfig
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
name|mapreduce
operator|.
name|task
operator|.
name|reduce
operator|.
name|MergeManagerImpl
operator|.
name|CompressAwarePath
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
name|Whitebox
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
name|Test
import|;
end_import

begin_class
DECL|class|TestMergeManager
specifier|public
class|class
name|TestMergeManager
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMemoryMerge ()
specifier|public
name|void
name|testMemoryMerge
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|TOTAL_MEM_BYTES
init|=
literal|10000
decl_stmt|;
specifier|final
name|int
name|OUTPUT_SIZE
init|=
literal|7950
decl_stmt|;
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_INPUT_BUFFER_PERCENT
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MEMORY_TOTAL_BYTES
argument_list|,
name|TOTAL_MEM_BYTES
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_MEMORY_LIMIT_PERCENT
argument_list|,
literal|0.8f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_MERGE_PERCENT
argument_list|,
literal|0.9f
argument_list|)
expr_stmt|;
name|TestExceptionReporter
name|reporter
init|=
operator|new
name|TestExceptionReporter
argument_list|()
decl_stmt|;
name|CyclicBarrier
name|mergeStart
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|CyclicBarrier
name|mergeComplete
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|StubbedMergeManager
name|mgr
init|=
operator|new
name|StubbedMergeManager
argument_list|(
name|conf
argument_list|,
name|reporter
argument_list|,
name|mergeStart
argument_list|,
name|mergeComplete
argument_list|)
decl_stmt|;
comment|// reserve enough map output to cause a merge when it is committed
name|MapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|out1
init|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should be a memory merge"
argument_list|,
operator|(
name|out1
operator|instanceof
name|InMemoryMapOutput
operator|)
argument_list|)
expr_stmt|;
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mout1
init|=
operator|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
operator|)
name|out1
decl_stmt|;
name|fillOutput
argument_list|(
name|mout1
argument_list|)
expr_stmt|;
name|MapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|out2
init|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should be a memory merge"
argument_list|,
operator|(
name|out2
operator|instanceof
name|InMemoryMapOutput
operator|)
argument_list|)
expr_stmt|;
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mout2
init|=
operator|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
operator|)
name|out2
decl_stmt|;
name|fillOutput
argument_list|(
name|mout2
argument_list|)
expr_stmt|;
comment|// next reservation should be a WAIT
name|MapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|out3
init|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|out3
argument_list|)
operator|.
name|withFailMessage
argument_list|(
literal|"Should be told to wait"
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// trigger the first merge and wait for merge thread to start merging
comment|// and free enough output to reserve more
name|mout1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|mout2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|mergeStart
operator|.
name|await
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mgr
operator|.
name|getNumMerges
argument_list|()
argument_list|)
expr_stmt|;
comment|// reserve enough map output to cause another merge when committed
name|out1
operator|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should be a memory merge"
argument_list|,
operator|(
name|out1
operator|instanceof
name|InMemoryMapOutput
operator|)
argument_list|)
expr_stmt|;
name|mout1
operator|=
operator|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
operator|)
name|out1
expr_stmt|;
name|fillOutput
argument_list|(
name|mout1
argument_list|)
expr_stmt|;
name|out2
operator|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should be a memory merge"
argument_list|,
operator|(
name|out2
operator|instanceof
name|InMemoryMapOutput
operator|)
argument_list|)
expr_stmt|;
name|mout2
operator|=
operator|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
operator|)
name|out2
expr_stmt|;
name|fillOutput
argument_list|(
name|mout2
argument_list|)
expr_stmt|;
comment|// next reservation should be null
name|out3
operator|=
name|mgr
operator|.
name|reserve
argument_list|(
literal|null
argument_list|,
name|OUTPUT_SIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out3
argument_list|)
operator|.
name|withFailMessage
argument_list|(
literal|"Should be told to wait"
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// commit output *before* merge thread completes
name|mout1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|mout2
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// allow the first merge to complete
name|mergeComplete
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// start the second merge and verify
name|mergeStart
operator|.
name|await
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mgr
operator|.
name|getNumMerges
argument_list|()
argument_list|)
expr_stmt|;
comment|// trigger the end of the second merge
name|mergeComplete
operator|.
name|await
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mgr
operator|.
name|getNumMerges
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"exception reporter invoked"
argument_list|,
literal|0
argument_list|,
name|reporter
operator|.
name|getNumExceptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fillOutput (InMemoryMapOutput<Text, Text> output)
specifier|private
name|void
name|fillOutput
parameter_list|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|BoundedByteArrayOutputStream
name|stream
init|=
name|output
operator|.
name|getArrayStream
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|stream
operator|.
name|getLimit
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|StubbedMergeManager
specifier|private
specifier|static
class|class
name|StubbedMergeManager
extends|extends
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|mergeThread
specifier|private
name|TestMergeThread
name|mergeThread
decl_stmt|;
DECL|method|StubbedMergeManager (JobConf conf, ExceptionReporter reporter, CyclicBarrier mergeStart, CyclicBarrier mergeComplete)
specifier|public
name|StubbedMergeManager
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|ExceptionReporter
name|reporter
parameter_list|,
name|CyclicBarrier
name|mergeStart
parameter_list|,
name|CyclicBarrier
name|mergeComplete
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|mock
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|reporter
argument_list|,
literal|null
argument_list|,
name|mock
argument_list|(
name|MapOutputFile
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|mergeThread
operator|.
name|setSyncBarriers
argument_list|(
name|mergeStart
argument_list|,
name|mergeComplete
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInMemoryMerger ()
specifier|protected
name|MergeThread
argument_list|<
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
name|createInMemoryMerger
parameter_list|()
block|{
name|mergeThread
operator|=
operator|new
name|TestMergeThread
argument_list|(
name|this
argument_list|,
name|getExceptionReporter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mergeThread
return|;
block|}
DECL|method|getNumMerges ()
specifier|public
name|int
name|getNumMerges
parameter_list|()
block|{
return|return
name|mergeThread
operator|.
name|getNumMerges
argument_list|()
return|;
block|}
block|}
DECL|class|TestMergeThread
specifier|private
specifier|static
class|class
name|TestMergeThread
extends|extends
name|MergeThread
argument_list|<
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|numMerges
specifier|private
name|AtomicInteger
name|numMerges
decl_stmt|;
DECL|field|mergeStart
specifier|private
name|CyclicBarrier
name|mergeStart
decl_stmt|;
DECL|field|mergeComplete
specifier|private
name|CyclicBarrier
name|mergeComplete
decl_stmt|;
DECL|method|TestMergeThread (MergeManagerImpl<Text, Text> mergeManager, ExceptionReporter reporter)
specifier|public
name|TestMergeThread
parameter_list|(
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mergeManager
parameter_list|,
name|ExceptionReporter
name|reporter
parameter_list|)
block|{
name|super
argument_list|(
name|mergeManager
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|numMerges
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|setSyncBarriers ( CyclicBarrier mergeStart, CyclicBarrier mergeComplete)
specifier|public
specifier|synchronized
name|void
name|setSyncBarriers
parameter_list|(
name|CyclicBarrier
name|mergeStart
parameter_list|,
name|CyclicBarrier
name|mergeComplete
parameter_list|)
block|{
name|this
operator|.
name|mergeStart
operator|=
name|mergeStart
expr_stmt|;
name|this
operator|.
name|mergeComplete
operator|=
name|mergeComplete
expr_stmt|;
block|}
DECL|method|getNumMerges ()
specifier|public
name|int
name|getNumMerges
parameter_list|()
block|{
return|return
name|numMerges
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|merge (List<InMemoryMapOutput<Text, Text>> inputs)
specifier|public
name|void
name|merge
parameter_list|(
name|List
argument_list|<
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|>
name|inputs
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|numMerges
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
for|for
control|(
name|InMemoryMapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|input
range|:
name|inputs
control|)
block|{
name|manager
operator|.
name|unreserve
argument_list|(
name|input
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|mergeStart
operator|.
name|await
argument_list|()
expr_stmt|;
name|mergeComplete
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|class|TestExceptionReporter
specifier|private
specifier|static
class|class
name|TestExceptionReporter
implements|implements
name|ExceptionReporter
block|{
DECL|field|exceptions
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|reportException (Throwable t)
specifier|public
name|void
name|reportException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
DECL|method|getNumExceptions ()
specifier|public
name|int
name|getNumExceptions
parameter_list|()
block|{
return|return
name|exceptions
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIoSortDefaults ()
specifier|public
name|void
name|testIoSortDefaults
parameter_list|()
block|{
specifier|final
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|jobConf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|IO_SORT_FACTOR
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|jobConf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|IO_SORT_MB
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"deprecation"
block|}
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testOnDiskMerger ()
specifier|public
name|void
name|testOnDiskMerger
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
specifier|final
name|int
name|SORT_FACTOR
init|=
literal|5
decl_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|IO_SORT_FACTOR
argument_list|,
name|SORT_FACTOR
argument_list|)
expr_stmt|;
name|MapOutputFile
name|mapOutputFile
init|=
operator|new
name|MROutputFiles
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|jobConf
argument_list|)
decl_stmt|;
name|MergeManagerImpl
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
name|manager
init|=
operator|new
name|MergeManagerImpl
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|(
literal|null
argument_list|,
name|jobConf
argument_list|,
name|fs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mapOutputFile
argument_list|)
decl_stmt|;
name|MergeThread
argument_list|<
name|MapOutput
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
name|onDiskMerger
init|=
operator|(
name|MergeThread
argument_list|<
name|MapOutput
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|manager
argument_list|,
literal|"onDiskMerger"
argument_list|)
decl_stmt|;
name|int
name|mergeFactor
init|=
operator|(
name|Integer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|onDiskMerger
argument_list|,
literal|"mergeFactor"
argument_list|)
decl_stmt|;
comment|// make sure the io.sort.factor is set properly
name|assertEquals
argument_list|(
name|mergeFactor
argument_list|,
name|SORT_FACTOR
argument_list|)
expr_stmt|;
comment|// Stop the onDiskMerger thread so that we can intercept the list of files
comment|// waiting to be merged.
name|onDiskMerger
operator|.
name|suspend
argument_list|()
expr_stmt|;
comment|//Send the list of fake files waiting to be merged
name|Random
name|rand
init|=
operator|new
name|Random
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
literal|2
operator|*
name|SORT_FACTOR
condition|;
operator|++
name|i
control|)
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"somePath"
argument_list|)
decl_stmt|;
name|CompressAwarePath
name|cap
init|=
operator|new
name|CompressAwarePath
argument_list|(
name|path
argument_list|,
literal|1l
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|manager
operator|.
name|closeOnDiskFile
argument_list|(
name|cap
argument_list|)
expr_stmt|;
block|}
comment|//Check that the files pending to be merged are in sorted order.
name|LinkedList
argument_list|<
name|List
argument_list|<
name|CompressAwarePath
argument_list|>
argument_list|>
name|pendingToBeMerged
init|=
operator|(
name|LinkedList
argument_list|<
name|List
argument_list|<
name|CompressAwarePath
argument_list|>
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|onDiskMerger
argument_list|,
literal|"pendingToBeMerged"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No inputs were added to list pending to merge"
argument_list|,
name|pendingToBeMerged
operator|.
name|size
argument_list|()
operator|>
literal|0
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
name|pendingToBeMerged
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|List
argument_list|<
name|CompressAwarePath
argument_list|>
name|inputs
init|=
name|pendingToBeMerged
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|inputs
operator|.
name|size
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|assertTrue
argument_list|(
literal|"Not enough / too many inputs were going to be merged"
argument_list|,
name|inputs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|inputs
operator|.
name|size
argument_list|()
operator|<=
name|SORT_FACTOR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Inputs to be merged were not sorted according to size: "
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getCompressedSize
argument_list|()
operator|>=
name|inputs
operator|.
name|get
argument_list|(
name|j
operator|-
literal|1
argument_list|)
operator|.
name|getCompressedSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testLargeMemoryLimits ()
specifier|public
name|void
name|testLargeMemoryLimits
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
comment|// Xmx in production
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MEMORY_TOTAL_BYTES
argument_list|,
literal|8L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// M1 = Xmx fraction for map outputs
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_INPUT_BUFFER_PERCENT
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
comment|// M2 = max M1 fraction for a single maple output
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_MEMORY_LIMIT_PERCENT
argument_list|,
literal|0.95f
argument_list|)
expr_stmt|;
comment|// M3 = M1 fraction at which in memory merge is triggered
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_MERGE_PERCENT
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
comment|// M4 = M1 fraction of map outputs remaining in memory for a reduce
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_INPUT_BUFFER_PERCENT
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
specifier|final
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mgr
init|=
operator|new
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|mock
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|MROutputFiles
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Large shuffle area unusable: "
operator|+
name|mgr
operator|.
name|memoryLimit
argument_list|,
name|mgr
operator|.
name|memoryLimit
operator|>
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
specifier|final
name|long
name|maxInMemReduce
init|=
name|mgr
operator|.
name|getMaxInMemReduceLimit
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Large in-memory reduce area unusable: "
operator|+
name|maxInMemReduce
argument_list|,
name|maxInMemReduce
operator|>
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maxSingleShuffleLimit to be capped at Integer.MAX_VALUE"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|mgr
operator|.
name|maxSingleShuffleLimit
argument_list|)
expr_stmt|;
name|verifyReservedMapOutputType
argument_list|(
name|mgr
argument_list|,
literal|10L
argument_list|,
literal|"MEMORY"
argument_list|)
expr_stmt|;
name|verifyReservedMapOutputType
argument_list|(
name|mgr
argument_list|,
literal|1L
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"DISK"
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyReservedMapOutputType (MergeManagerImpl<Text, Text> mgr, long size, String expectedShuffleMode)
specifier|private
name|void
name|verifyReservedMapOutputType
parameter_list|(
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mgr
parameter_list|,
name|long
name|size
parameter_list|,
name|String
name|expectedShuffleMode
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TaskAttemptID
name|mapId
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
literal|"attempt_0_1_m_1_1"
argument_list|)
decl_stmt|;
specifier|final
name|MapOutput
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mapOutput
init|=
name|mgr
operator|.
name|reserve
argument_list|(
name|mapId
argument_list|,
name|size
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Shuffled bytes: "
operator|+
name|size
argument_list|,
name|expectedShuffleMode
argument_list|,
name|mapOutput
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|unreserve
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testZeroShuffleMemoryLimitPercent ()
specifier|public
name|void
name|testZeroShuffleMemoryLimitPercent
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|jobConf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|SHUFFLE_MEMORY_LIMIT_PERCENT
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
specifier|final
name|MergeManagerImpl
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|mgr
init|=
operator|new
name|MergeManagerImpl
argument_list|<>
argument_list|(
literal|null
argument_list|,
name|jobConf
argument_list|,
name|mock
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|MROutputFiles
argument_list|()
argument_list|)
decl_stmt|;
name|verifyReservedMapOutputType
argument_list|(
name|mgr
argument_list|,
literal|10L
argument_list|,
literal|"DISK"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

