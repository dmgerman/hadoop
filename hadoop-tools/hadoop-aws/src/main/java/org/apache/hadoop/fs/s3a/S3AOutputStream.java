begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEventType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|CannedAccessControlList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PutObjectRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|transfer
operator|.
name|TransferManager
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|transfer
operator|.
name|Upload
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
name|lang
operator|.
name|StringUtils
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
name|LocalDirAllocator
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|InterruptedIOException
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
import|import static
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEventType
operator|.
name|TRANSFER_COMPLETED_EVENT
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEventType
operator|.
name|TRANSFER_PART_STARTED_EVENT
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
name|s3a
operator|.
name|Constants
operator|.
name|*
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
name|s3a
operator|.
name|S3AUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Output stream to save data to S3.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3AOutputStream
specifier|public
class|class
name|S3AOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|backupStream
specifier|private
name|OutputStream
name|backupStream
decl_stmt|;
DECL|field|backupFile
specifier|private
name|File
name|backupFile
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|bucket
specifier|private
name|String
name|bucket
decl_stmt|;
DECL|field|transfers
specifier|private
name|TransferManager
name|transfers
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressable
name|progress
decl_stmt|;
DECL|field|partSize
specifier|private
name|long
name|partSize
decl_stmt|;
DECL|field|partSizeThreshold
specifier|private
name|long
name|partSizeThreshold
decl_stmt|;
DECL|field|fs
specifier|private
name|S3AFileSystem
name|fs
decl_stmt|;
DECL|field|cannedACL
specifier|private
name|CannedAccessControlList
name|cannedACL
decl_stmt|;
DECL|field|statistics
specifier|private
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
DECL|field|lDirAlloc
specifier|private
name|LocalDirAllocator
name|lDirAlloc
decl_stmt|;
DECL|field|serverSideEncryptionAlgorithm
specifier|private
name|String
name|serverSideEncryptionAlgorithm
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|S3AFileSystem
operator|.
name|LOG
decl_stmt|;
DECL|method|S3AOutputStream (Configuration conf, TransferManager transfers, S3AFileSystem fs, String bucket, String key, Progressable progress, CannedAccessControlList cannedACL, FileSystem.Statistics statistics, String serverSideEncryptionAlgorithm)
specifier|public
name|S3AOutputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|TransferManager
name|transfers
parameter_list|,
name|S3AFileSystem
name|fs
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|CannedAccessControlList
name|cannedACL
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|statistics
parameter_list|,
name|String
name|serverSideEncryptionAlgorithm
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|transfers
operator|=
name|transfers
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|cannedACL
operator|=
name|cannedACL
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|serverSideEncryptionAlgorithm
operator|=
name|serverSideEncryptionAlgorithm
expr_stmt|;
name|partSize
operator|=
name|fs
operator|.
name|getPartitionSize
argument_list|()
expr_stmt|;
name|partSizeThreshold
operator|=
name|fs
operator|.
name|getMultiPartThreshold
argument_list|()
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|BUFFER_DIR
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|lDirAlloc
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
name|BUFFER_DIR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lDirAlloc
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
literal|"${hadoop.tmp.dir}/s3a"
argument_list|)
expr_stmt|;
block|}
name|backupFile
operator|=
name|lDirAlloc
operator|.
name|createTmpFileForWrite
argument_list|(
literal|"output-"
argument_list|,
name|LocalDirAllocator
operator|.
name|SIZE_UNKNOWN
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"OutputStream for key '{}' writing to tempfile: {}"
argument_list|,
name|key
argument_list|,
name|backupFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|backupStream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|backupFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|backupStream
operator|.
name|flush
argument_list|()
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
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|backupStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"OutputStream for key '{}' closed. Now beginning upload"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Minimum upload part size: {} threshold {}"
argument_list|,
name|partSize
argument_list|,
name|partSizeThreshold
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|ObjectMetadata
name|om
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
condition|)
block|{
name|om
operator|.
name|setSSEAlgorithm
argument_list|(
name|serverSideEncryptionAlgorithm
argument_list|)
expr_stmt|;
block|}
name|PutObjectRequest
name|putObjectRequest
init|=
operator|new
name|PutObjectRequest
argument_list|(
name|bucket
argument_list|,
name|key
argument_list|,
name|backupFile
argument_list|)
decl_stmt|;
name|putObjectRequest
operator|.
name|setCannedAcl
argument_list|(
name|cannedACL
argument_list|)
expr_stmt|;
name|putObjectRequest
operator|.
name|setMetadata
argument_list|(
name|om
argument_list|)
expr_stmt|;
name|Upload
name|upload
init|=
name|transfers
operator|.
name|upload
argument_list|(
name|putObjectRequest
argument_list|)
decl_stmt|;
name|ProgressableProgressListener
name|listener
init|=
operator|new
name|ProgressableProgressListener
argument_list|(
name|upload
argument_list|,
name|progress
argument_list|,
name|statistics
argument_list|)
decl_stmt|;
name|upload
operator|.
name|addProgressListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|upload
operator|.
name|waitForUploadResult
argument_list|()
expr_stmt|;
name|long
name|delta
init|=
name|upload
operator|.
name|getProgress
argument_list|()
operator|.
name|getBytesTransferred
argument_list|()
operator|-
name|listener
operator|.
name|getLastBytesTransferred
argument_list|()
decl_stmt|;
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|delta
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"S3A write delta changed after finished: {} bytes"
argument_list|,
name|delta
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|incrementBytesWritten
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
comment|// This will delete unnecessary fake parent directories
name|fs
operator|.
name|finishedWrite
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|InterruptedIOException
operator|)
operator|new
name|InterruptedIOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
throw|throw
name|translateException
argument_list|(
literal|"saving output"
argument_list|,
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|backupFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not delete temporary s3a file: {}"
argument_list|,
name|backupFile
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"OutputStream for key '{}' upload complete"
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|backupStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
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
name|backupStream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**    * Listener to progress from AWS regarding transfers.    */
DECL|class|ProgressableProgressListener
specifier|public
specifier|static
class|class
name|ProgressableProgressListener
implements|implements
name|ProgressListener
block|{
DECL|field|progress
specifier|private
name|Progressable
name|progress
decl_stmt|;
DECL|field|statistics
specifier|private
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
DECL|field|lastBytesTransferred
specifier|private
name|long
name|lastBytesTransferred
decl_stmt|;
DECL|field|upload
specifier|private
name|Upload
name|upload
decl_stmt|;
DECL|method|ProgressableProgressListener (Upload upload, Progressable progress, FileSystem.Statistics statistics)
specifier|public
name|ProgressableProgressListener
parameter_list|(
name|Upload
name|upload
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|statistics
parameter_list|)
block|{
name|this
operator|.
name|upload
operator|=
name|upload
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
name|this
operator|.
name|lastBytesTransferred
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|progressChanged (ProgressEvent progressEvent)
specifier|public
name|void
name|progressChanged
parameter_list|(
name|ProgressEvent
name|progressEvent
parameter_list|)
block|{
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
block|{
name|progress
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
comment|// There are 3 http ops here, but this should be close enough for now
name|ProgressEventType
name|pet
init|=
name|progressEvent
operator|.
name|getEventType
argument_list|()
decl_stmt|;
if|if
condition|(
name|pet
operator|==
name|TRANSFER_PART_STARTED_EVENT
operator|||
name|pet
operator|==
name|TRANSFER_COMPLETED_EVENT
condition|)
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|long
name|transferred
init|=
name|upload
operator|.
name|getProgress
argument_list|()
operator|.
name|getBytesTransferred
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
name|transferred
operator|-
name|lastBytesTransferred
decl_stmt|;
if|if
condition|(
name|statistics
operator|!=
literal|null
operator|&&
name|delta
operator|!=
literal|0
condition|)
block|{
name|statistics
operator|.
name|incrementBytesWritten
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
name|lastBytesTransferred
operator|=
name|transferred
expr_stmt|;
block|}
DECL|method|getLastBytesTransferred ()
specifier|public
name|long
name|getLastBytesTransferred
parameter_list|()
block|{
return|return
name|lastBytesTransferred
return|;
block|}
block|}
block|}
end_class

end_unit

