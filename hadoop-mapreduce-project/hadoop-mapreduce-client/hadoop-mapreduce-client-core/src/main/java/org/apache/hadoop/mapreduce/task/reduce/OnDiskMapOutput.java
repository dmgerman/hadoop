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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|mapred
operator|.
name|IFileInputStream
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
name|CryptoUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OnDiskMapOutput
class|class
name|OnDiskMapOutput
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|IFileWrappedMapOutput
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OnDiskMapOutput
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|tmpOutputPath
specifier|private
specifier|final
name|Path
name|tmpOutputPath
decl_stmt|;
DECL|field|outputPath
specifier|private
specifier|final
name|Path
name|outputPath
decl_stmt|;
DECL|field|disk
specifier|private
specifier|final
name|OutputStream
name|disk
decl_stmt|;
DECL|field|compressedSize
specifier|private
name|long
name|compressedSize
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|OnDiskMapOutput (TaskAttemptID mapId, TaskAttemptID reduceId, MergeManagerImpl<K,V> merger, long size, JobConf conf, MapOutputFile mapOutputFile, int fetcher, boolean primaryMapOutput)
specifier|public
name|OnDiskMapOutput
parameter_list|(
name|TaskAttemptID
name|mapId
parameter_list|,
name|TaskAttemptID
name|reduceId
parameter_list|,
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
parameter_list|,
name|long
name|size
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|MapOutputFile
name|mapOutputFile
parameter_list|,
name|int
name|fetcher
parameter_list|,
name|boolean
name|primaryMapOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|mapId
argument_list|,
name|merger
argument_list|,
name|size
argument_list|,
name|conf
argument_list|,
name|fetcher
argument_list|,
name|primaryMapOutput
argument_list|,
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
argument_list|,
name|mapOutputFile
operator|.
name|getInputFileForWrite
argument_list|(
name|mapId
operator|.
name|getTaskID
argument_list|()
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|OnDiskMapOutput (TaskAttemptID mapId, TaskAttemptID reduceId, MergeManagerImpl<K,V> merger, long size, JobConf conf, MapOutputFile mapOutputFile, int fetcher, boolean primaryMapOutput, FileSystem fs, Path outputPath)
name|OnDiskMapOutput
parameter_list|(
name|TaskAttemptID
name|mapId
parameter_list|,
name|TaskAttemptID
name|reduceId
parameter_list|,
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
parameter_list|,
name|long
name|size
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|MapOutputFile
name|mapOutputFile
parameter_list|,
name|int
name|fetcher
parameter_list|,
name|boolean
name|primaryMapOutput
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|outputPath
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|mapId
argument_list|,
name|merger
argument_list|,
name|size
argument_list|,
name|conf
argument_list|,
name|fetcher
argument_list|,
name|primaryMapOutput
argument_list|,
name|fs
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
block|}
DECL|method|OnDiskMapOutput (TaskAttemptID mapId, MergeManagerImpl<K, V> merger, long size, JobConf conf, int fetcher, boolean primaryMapOutput, FileSystem fs, Path outputPath)
name|OnDiskMapOutput
parameter_list|(
name|TaskAttemptID
name|mapId
parameter_list|,
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
parameter_list|,
name|long
name|size
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|int
name|fetcher
parameter_list|,
name|boolean
name|primaryMapOutput
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|outputPath
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|merger
argument_list|,
name|mapId
argument_list|,
name|size
argument_list|,
name|primaryMapOutput
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|outputPath
operator|=
name|outputPath
expr_stmt|;
name|tmpOutputPath
operator|=
name|getTempPath
argument_list|(
name|outputPath
argument_list|,
name|fetcher
argument_list|)
expr_stmt|;
name|disk
operator|=
name|CryptoUtils
operator|.
name|wrapIfNecessary
argument_list|(
name|conf
argument_list|,
name|fs
operator|.
name|create
argument_list|(
name|tmpOutputPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTempPath (Path outPath, int fetcher)
specifier|static
name|Path
name|getTempPath
parameter_list|(
name|Path
name|outPath
parameter_list|,
name|int
name|fetcher
parameter_list|)
block|{
return|return
name|outPath
operator|.
name|suffix
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|fetcher
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doShuffle (MapHost host, IFileInputStream input, long compressedLength, long decompressedLength, ShuffleClientMetrics metrics, Reporter reporter)
specifier|protected
name|void
name|doShuffle
parameter_list|(
name|MapHost
name|host
parameter_list|,
name|IFileInputStream
name|input
parameter_list|,
name|long
name|compressedLength
parameter_list|,
name|long
name|decompressedLength
parameter_list|,
name|ShuffleClientMetrics
name|metrics
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Copy data to local-disk
name|long
name|bytesLeft
init|=
name|compressedLength
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|BYTES_TO_READ
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BYTES_TO_READ
index|]
decl_stmt|;
while|while
condition|(
name|bytesLeft
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|input
operator|.
name|readWithChecksum
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|bytesLeft
argument_list|,
name|BYTES_TO_READ
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past end of stream reading "
operator|+
name|getMapId
argument_list|()
argument_list|)
throw|;
block|}
name|disk
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|bytesLeft
operator|-=
name|n
expr_stmt|;
name|metrics
operator|.
name|inputBytes
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Read "
operator|+
operator|(
name|compressedLength
operator|-
name|bytesLeft
operator|)
operator|+
literal|" bytes from map-output for "
operator|+
name|getMapId
argument_list|()
argument_list|)
expr_stmt|;
name|disk
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
comment|// Close the streams
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|disk
argument_list|)
expr_stmt|;
comment|// Re-throw
throw|throw
name|ioe
throw|;
block|}
comment|// Sanity check
if|if
condition|(
name|bytesLeft
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incomplete map output received for "
operator|+
name|getMapId
argument_list|()
operator|+
literal|" from "
operator|+
name|host
operator|.
name|getHostName
argument_list|()
operator|+
literal|" ("
operator|+
name|bytesLeft
operator|+
literal|" bytes missing of "
operator|+
name|compressedLength
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|compressedSize
operator|=
name|compressedLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit ()
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|rename
argument_list|(
name|tmpOutputPath
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|CompressAwarePath
name|compressAwarePath
init|=
operator|new
name|CompressAwarePath
argument_list|(
name|outputPath
argument_list|,
name|getSize
argument_list|()
argument_list|,
name|this
operator|.
name|compressedSize
argument_list|)
decl_stmt|;
name|getMerger
argument_list|()
operator|.
name|closeOnDiskFile
argument_list|(
name|compressAwarePath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|tmpOutputPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failure to clean up "
operator|+
name|tmpOutputPath
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"DISK"
return|;
block|}
block|}
end_class

end_unit

