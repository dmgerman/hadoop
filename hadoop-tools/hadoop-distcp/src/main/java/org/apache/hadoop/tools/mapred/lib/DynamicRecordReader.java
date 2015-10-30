begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|mapred
operator|.
name|lib
package|;
end_package

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
name|tools
operator|.
name|util
operator|.
name|DistCpUtils
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
name|tools
operator|.
name|DistCpConstants
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
name|*
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * The DynamicRecordReader is used in conjunction with the DynamicInputFormat  * to implement the "Worker pattern" for DistCp.  * The DynamicRecordReader is responsible for:  * 1. Presenting the contents of each chunk to DistCp's mapper.  * 2. Acquiring a new chunk when the current chunk has been completely consumed,  *    transparently.  */
end_comment

begin_class
DECL|class|DynamicRecordReader
specifier|public
class|class
name|DynamicRecordReader
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
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
name|DynamicRecordReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|taskAttemptContext
specifier|private
name|TaskAttemptContext
name|taskAttemptContext
decl_stmt|;
DECL|field|configuration
specifier|private
name|Configuration
name|configuration
decl_stmt|;
DECL|field|chunk
specifier|private
name|DynamicInputChunk
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|chunk
decl_stmt|;
DECL|field|taskId
specifier|private
name|TaskID
name|taskId
decl_stmt|;
comment|// Data required for progress indication.
DECL|field|numRecordsPerChunk
specifier|private
name|int
name|numRecordsPerChunk
decl_stmt|;
comment|// Constant per job.
DECL|field|totalNumRecords
specifier|private
name|int
name|totalNumRecords
decl_stmt|;
comment|// Constant per job.
DECL|field|numRecordsProcessedByThisMap
specifier|private
name|int
name|numRecordsProcessedByThisMap
init|=
literal|0
decl_stmt|;
DECL|field|timeOfLastChunkDirScan
specifier|private
name|long
name|timeOfLastChunkDirScan
init|=
literal|0
decl_stmt|;
DECL|field|isChunkDirAlreadyScanned
specifier|private
name|boolean
name|isChunkDirAlreadyScanned
init|=
literal|false
decl_stmt|;
DECL|field|chunkContext
specifier|private
name|DynamicInputChunkContext
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|chunkContext
decl_stmt|;
DECL|field|TIME_THRESHOLD_FOR_DIR_SCANS
specifier|private
specifier|static
name|long
name|TIME_THRESHOLD_FOR_DIR_SCANS
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|method|DynamicRecordReader (DynamicInputChunkContext<K, V> chunkContext)
name|DynamicRecordReader
parameter_list|(
name|DynamicInputChunkContext
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|chunkContext
parameter_list|)
block|{
name|this
operator|.
name|chunkContext
operator|=
name|chunkContext
expr_stmt|;
block|}
comment|/**    * Implementation for RecordReader::initialize(). Initializes the internal    * RecordReader to read from chunks.    * @param inputSplit The InputSplit for the map. Ignored entirely.    * @param taskAttemptContext The AttemptContext.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|initialize (InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|inputSplit
parameter_list|,
name|TaskAttemptContext
name|taskAttemptContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|numRecordsPerChunk
operator|=
name|DynamicInputFormat
operator|.
name|getNumEntriesPerChunk
argument_list|(
name|taskAttemptContext
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskAttemptContext
operator|=
name|taskAttemptContext
expr_stmt|;
name|configuration
operator|=
name|taskAttemptContext
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|taskId
operator|=
name|taskAttemptContext
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
expr_stmt|;
name|chunk
operator|=
name|chunkContext
operator|.
name|acquire
argument_list|(
name|this
operator|.
name|taskAttemptContext
argument_list|)
expr_stmt|;
name|timeOfLastChunkDirScan
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|isChunkDirAlreadyScanned
operator|=
literal|false
expr_stmt|;
name|totalNumRecords
operator|=
name|getTotalNumRecords
argument_list|()
expr_stmt|;
block|}
DECL|method|getTotalNumRecords ()
specifier|private
name|int
name|getTotalNumRecords
parameter_list|()
block|{
return|return
name|DistCpUtils
operator|.
name|getInt
argument_list|(
name|configuration
argument_list|,
name|DistCpConstants
operator|.
name|CONF_LABEL_TOTAL_NUMBER_OF_RECORDS
argument_list|)
return|;
block|}
comment|/**    * Implementation of RecordReader::nextValue().    * Reads the contents of the current chunk and returns them. When a chunk has    * been completely exhausted, an new chunk is acquired and read,    * transparently.    * @return True, if the nextValue() could be traversed to. False, otherwise.    * @throws IOException    * @throws InterruptedException    */
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
name|chunk
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|taskId
operator|+
literal|": RecordReader is null. No records to be read."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|chunk
operator|.
name|getReader
argument_list|()
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
operator|++
name|numRecordsProcessedByThisMap
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|taskId
operator|+
literal|": Current chunk exhausted. "
operator|+
literal|" Attempting to pick up new one."
argument_list|)
expr_stmt|;
name|chunk
operator|.
name|release
argument_list|()
expr_stmt|;
name|timeOfLastChunkDirScan
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|isChunkDirAlreadyScanned
operator|=
literal|false
expr_stmt|;
name|chunk
operator|=
name|chunkContext
operator|.
name|acquire
argument_list|(
name|taskAttemptContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|chunk
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|chunk
operator|.
name|getReader
argument_list|()
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
operator|++
name|numRecordsProcessedByThisMap
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Implementation of RecordReader::getCurrentKey().    * @return The key of the current record. (i.e. the source-path.)    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|getCurrentKey ()
specifier|public
name|K
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|chunk
operator|.
name|getReader
argument_list|()
operator|.
name|getCurrentKey
argument_list|()
return|;
block|}
comment|/**    * Implementation of RecordReader::getCurrentValue().    * @return The value of the current record. (i.e. the target-path.)    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|getCurrentValue ()
specifier|public
name|V
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|chunk
operator|.
name|getReader
argument_list|()
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
comment|/**    * Implementation of RecordReader::getProgress().    * @return A fraction [0.0,1.0] indicating the progress of a DistCp mapper.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|numChunksLeft
init|=
name|getNumChunksLeft
argument_list|()
decl_stmt|;
if|if
condition|(
name|numChunksLeft
operator|<
literal|0
condition|)
block|{
comment|// Un-initialized. i.e. Before 1st dir-scan.
assert|assert
name|numRecordsProcessedByThisMap
operator|<=
name|numRecordsPerChunk
operator|:
literal|"numRecordsProcessedByThisMap:"
operator|+
name|numRecordsProcessedByThisMap
operator|+
literal|" exceeds numRecordsPerChunk:"
operator|+
name|numRecordsPerChunk
assert|;
return|return
operator|(
operator|(
name|float
operator|)
name|numRecordsProcessedByThisMap
operator|)
operator|/
name|totalNumRecords
return|;
comment|// Conservative estimate, till the first directory scan.
block|}
return|return
operator|(
operator|(
name|float
operator|)
name|numRecordsProcessedByThisMap
operator|)
operator|/
operator|(
name|numRecordsProcessedByThisMap
operator|+
name|numRecordsPerChunk
operator|*
name|numChunksLeft
operator|)
return|;
block|}
DECL|method|getNumChunksLeft ()
specifier|private
name|int
name|getNumChunksLeft
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|boolean
name|tooLongSinceLastDirScan
init|=
name|now
operator|-
name|timeOfLastChunkDirScan
operator|>
name|TIME_THRESHOLD_FOR_DIR_SCANS
decl_stmt|;
if|if
condition|(
name|tooLongSinceLastDirScan
operator|||
operator|(
operator|!
name|isChunkDirAlreadyScanned
operator|&&
name|numRecordsProcessedByThisMap
operator|%
name|numRecordsPerChunk
operator|>
name|numRecordsPerChunk
operator|/
literal|2
operator|)
condition|)
block|{
name|chunkContext
operator|.
name|getListOfChunkFiles
argument_list|()
expr_stmt|;
name|isChunkDirAlreadyScanned
operator|=
literal|true
expr_stmt|;
name|timeOfLastChunkDirScan
operator|=
name|now
expr_stmt|;
block|}
return|return
name|chunkContext
operator|.
name|getNumChunksLeft
argument_list|()
return|;
block|}
comment|/**    * Implementation of RecordReader::close().    * Closes the RecordReader.    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|chunk
operator|!=
literal|null
condition|)
name|chunk
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

