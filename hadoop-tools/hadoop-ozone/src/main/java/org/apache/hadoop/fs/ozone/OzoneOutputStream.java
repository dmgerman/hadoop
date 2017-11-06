begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
package|;
end_package

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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
operator|.
name|Statistics
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
name|ozone
operator|.
name|web
operator|.
name|client
operator|.
name|OzoneBucket
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
name|ozone
operator|.
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|ozone
operator|.
name|Constants
operator|.
name|BUFFER_DIR_KEY
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
name|ozone
operator|.
name|Constants
operator|.
name|BUFFER_TMP_KEY
import|;
end_import

begin_comment
comment|/**  * The output stream for Ozone file system.  *  * Data will be buffered on local disk, then uploaded to Ozone in  * {@link #close()} method.  *  * This class is not thread safe.  */
end_comment

begin_class
DECL|class|OzoneOutputStream
specifier|public
class|class
name|OzoneOutputStream
extends|extends
name|OutputStream
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
name|OzoneOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bucket
specifier|private
name|OzoneBucket
name|bucket
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|keyUri
specifier|private
specifier|final
name|URI
name|keyUri
decl_stmt|;
DECL|field|statistics
specifier|private
name|Statistics
name|statistics
decl_stmt|;
DECL|field|dirAlloc
specifier|private
name|LocalDirAllocator
name|dirAlloc
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|tmpFile
specifier|private
name|File
name|tmpFile
decl_stmt|;
DECL|field|backupStream
specifier|private
name|BufferedOutputStream
name|backupStream
decl_stmt|;
DECL|method|OzoneOutputStream (Configuration conf, URI fsUri, OzoneBucket bucket, String key, Statistics statistics)
name|OzoneOutputStream
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|fsUri
parameter_list|,
name|OzoneBucket
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|Statistics
name|statistics
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
name|keyUri
operator|=
name|fsUri
operator|.
name|resolve
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|BUFFER_DIR_KEY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|BUFFER_DIR_KEY
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|BUFFER_TMP_KEY
argument_list|)
operator|+
literal|"/ozone"
argument_list|)
expr_stmt|;
block|}
name|dirAlloc
operator|=
operator|new
name|LocalDirAllocator
argument_list|(
name|BUFFER_DIR_KEY
argument_list|)
expr_stmt|;
name|tmpFile
operator|=
name|dirAlloc
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
name|backupStream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|false
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
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|backupStream
operator|!=
literal|null
condition|)
block|{
name|backupStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Put tmp-file:"
operator|+
name|tmpFile
operator|+
literal|" to key "
operator|+
name|keyUri
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|putKey
argument_list|(
name|key
argument_list|,
name|tmpFile
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OzoneException
name|oe
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Uploading error: file="
operator|+
name|tmpFile
operator|+
literal|", key="
operator|+
name|key
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|oe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|,
name|oe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|tmpFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can not delete tmpFile: "
operator|+
name|tmpFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
specifier|synchronized
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
DECL|method|write (int b)
specifier|public
specifier|synchronized
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
name|statistics
operator|.
name|incrementBytesWritten
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

