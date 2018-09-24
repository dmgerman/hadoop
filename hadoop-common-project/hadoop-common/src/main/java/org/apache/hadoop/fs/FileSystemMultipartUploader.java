begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|InputStream
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
name|Comparator
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
name|stream
operator|.
name|Collectors
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
name|base
operator|.
name|Charsets
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
name|base
operator|.
name|Preconditions
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
name|compress
operator|.
name|utils
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
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|permission
operator|.
name|FsPermission
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
name|Path
operator|.
name|mergePaths
import|;
end_import

begin_comment
comment|/**  * A MultipartUploader that uses the basic FileSystem commands.  * This is done in three stages:  *<ul>  *<li>Init - create a temp {@code _multipart} directory.</li>  *<li>PutPart - copying the individual parts of the file to the temp  *   directory.</li>  *<li>Complete - use {@link FileSystem#concat} to merge the files;  *   and then delete the temp directory.</li>  *</ul>  */
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
DECL|class|FileSystemMultipartUploader
specifier|public
class|class
name|FileSystemMultipartUploader
extends|extends
name|MultipartUploader
block|{
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|method|FileSystemMultipartUploader (FileSystem fs)
specifier|public
name|FileSystemMultipartUploader
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (Path filePath)
specifier|public
name|UploadHandle
name|initialize
parameter_list|(
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|collectorPath
init|=
name|createCollectorPath
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|collectorPath
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|ByteBuffer
name|byteBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|collectorPath
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|BBUploadHandle
operator|.
name|from
argument_list|(
name|byteBuffer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|putPart (Path filePath, InputStream inputStream, int partNumber, UploadHandle uploadId, long lengthInBytes)
specifier|public
name|PartHandle
name|putPart
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|int
name|partNumber
parameter_list|,
name|UploadHandle
name|uploadId
parameter_list|,
name|long
name|lengthInBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|uploadIdByteArray
init|=
name|uploadId
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|checkUploadId
argument_list|(
name|uploadIdByteArray
argument_list|)
expr_stmt|;
name|Path
name|collectorPath
init|=
operator|new
name|Path
argument_list|(
operator|new
name|String
argument_list|(
name|uploadIdByteArray
argument_list|,
literal|0
argument_list|,
name|uploadIdByteArray
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|partPath
init|=
name|mergePaths
argument_list|(
name|collectorPath
argument_list|,
name|mergePaths
argument_list|(
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|partNumber
argument_list|)
operator|+
literal|".part"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|fsDataOutputStream
init|=
name|fs
operator|.
name|createFile
argument_list|(
name|partPath
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|inputStream
argument_list|,
name|fsDataOutputStream
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|inputStream
argument_list|)
expr_stmt|;
block|}
return|return
name|BBPartHandle
operator|.
name|from
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|partPath
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createCollectorPath (Path filePath)
specifier|private
name|Path
name|createCollectorPath
parameter_list|(
name|Path
name|filePath
parameter_list|)
block|{
return|return
name|mergePaths
argument_list|(
name|filePath
operator|.
name|getParent
argument_list|()
argument_list|,
name|mergePaths
argument_list|(
operator|new
name|Path
argument_list|(
name|filePath
operator|.
name|getName
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|mergePaths
argument_list|(
operator|new
name|Path
argument_list|(
literal|"_multipart"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getPathHandle (Path filePath)
specifier|private
name|PathHandle
name|getPathHandle
parameter_list|(
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
return|return
name|fs
operator|.
name|getPathHandle
argument_list|(
name|status
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// rename w/ OVERWRITE
DECL|method|complete (Path filePath, List<Pair<Integer, PartHandle>> handles, UploadHandle multipartUploadId)
specifier|public
name|PathHandle
name|complete
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|PartHandle
argument_list|>
argument_list|>
name|handles
parameter_list|,
name|UploadHandle
name|multipartUploadId
parameter_list|)
throws|throws
name|IOException
block|{
name|checkUploadId
argument_list|(
name|multipartUploadId
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|handles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Empty upload"
argument_list|)
throw|;
block|}
comment|// If destination already exists, we believe we already completed it.
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|filePath
argument_list|)
condition|)
block|{
return|return
name|getPathHandle
argument_list|(
name|filePath
argument_list|)
return|;
block|}
name|handles
operator|.
name|sort
argument_list|(
name|Comparator
operator|.
name|comparing
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|partHandles
init|=
name|handles
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|pair
lambda|->
block|{
name|byte
index|[]
name|byteArray
init|=
name|pair
operator|.
name|getValue
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Path
argument_list|(
operator|new
name|String
argument_list|(
name|byteArray
argument_list|,
literal|0
argument_list|,
name|byteArray
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|collectorPath
init|=
name|createCollectorPath
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|Path
name|filePathInsideCollector
init|=
name|mergePaths
argument_list|(
name|collectorPath
argument_list|,
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
operator|+
name|filePath
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|filePathInsideCollector
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|concat
argument_list|(
name|filePathInsideCollector
argument_list|,
name|partHandles
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
name|handles
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|filePathInsideCollector
argument_list|,
name|filePath
argument_list|,
name|Options
operator|.
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|collectorPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|getPathHandle
argument_list|(
name|filePath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abort (Path filePath, UploadHandle uploadId)
specifier|public
name|void
name|abort
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|UploadHandle
name|uploadId
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|uploadIdByteArray
init|=
name|uploadId
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|checkUploadId
argument_list|(
name|uploadIdByteArray
argument_list|)
expr_stmt|;
name|Path
name|collectorPath
init|=
operator|new
name|Path
argument_list|(
operator|new
name|String
argument_list|(
name|uploadIdByteArray
argument_list|,
literal|0
argument_list|,
name|uploadIdByteArray
operator|.
name|length
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
comment|// force a check for a file existing; raises FNFE if not found
name|fs
operator|.
name|getFileStatus
argument_list|(
name|collectorPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|collectorPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Factory for creating MultipartUploaderFactory objects for file://    * filesystems.    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|MultipartUploaderFactory
block|{
DECL|method|createMultipartUploader (FileSystem fs, Configuration conf)
specifier|protected
name|MultipartUploader
name|createMultipartUploader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
return|return
operator|new
name|FileSystemMultipartUploader
argument_list|(
name|fs
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

