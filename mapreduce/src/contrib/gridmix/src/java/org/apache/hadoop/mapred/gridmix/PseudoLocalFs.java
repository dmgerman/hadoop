begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|FileStatus
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
name|fs
operator|.
name|PositionedReadable
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
name|Seekable
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|BytesWritable
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

begin_comment
comment|/**  * Pseudo local file system that generates random data for any file on the fly  * instead of storing files on disk. So opening same file multiple times will  * not give same file content. There are no directories in this file system  * other than the root and all the files are under root i.e. "/". All file URIs  * on pseudo local file system should be of the format<code>  * pseudo:///&lt;name&gt;.&lt;fileSize&gt;</code> where name is a unique name  * and&lt;fileSize&gt; is a number representing the size of the file in bytes.  */
end_comment

begin_class
DECL|class|PseudoLocalFs
class|class
name|PseudoLocalFs
extends|extends
name|FileSystem
block|{
DECL|field|home
name|Path
name|home
decl_stmt|;
comment|/**    * The creation time and modification time of all files in    * {@link PseudoLocalFs} is same.    */
DECL|field|TIME
specifier|private
specifier|static
specifier|final
name|long
name|TIME
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|HOME_DIR
specifier|private
specifier|static
specifier|final
name|String
name|HOME_DIR
init|=
literal|"/"
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|4
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
comment|// 4 MB
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 1MB
DECL|field|NAME
specifier|static
specifier|final
name|URI
name|NAME
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"pseudo:///"
argument_list|)
decl_stmt|;
DECL|method|PseudoLocalFs ()
name|PseudoLocalFs
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|HOME_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|PseudoLocalFs (Path home)
name|PseudoLocalFs
parameter_list|(
name|Path
name|home
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|home
operator|=
name|home
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getHomeDirectory ()
specifier|public
name|Path
name|getHomeDirectory
parameter_list|()
block|{
return|return
name|home
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|getHomeDirectory
argument_list|()
return|;
block|}
comment|/**    * Generates a valid pseudo local file path from the given<code>fileId</code>    * and<code>fileSize</code>.    * @param fileId unique file id string    * @param fileSize file size    * @return the generated relative path    */
DECL|method|generateFilePath (String fileId, long fileSize)
specifier|static
name|Path
name|generateFilePath
parameter_list|(
name|String
name|fileId
parameter_list|,
name|long
name|fileSize
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|fileId
operator|+
literal|"."
operator|+
name|fileSize
argument_list|)
return|;
block|}
comment|/**    * Creating a pseudo local file is nothing but validating the file path.    * Actual data of the file is generated on the fly when client tries to open    * the file for reading.    * @param path file path to be created    */
annotation|@
name|Override
DECL|method|create (Path path)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|validateFileNameFormat
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File creation failed for "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Validate if the path provided is of expected format of Pseudo Local File    * System based files.    * @param path file path    * @return the file size    * @throws FileNotFoundException    */
DECL|method|validateFileNameFormat (Path path)
name|long
name|validateFileNameFormat
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|path
operator|=
name|path
operator|.
name|makeQualified
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|boolean
name|valid
init|=
literal|true
decl_stmt|;
name|long
name|fileSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|valid
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|parts
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
try|try
block|{
name|fileSize
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|parts
index|[
name|parts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|valid
operator|=
operator|(
name|fileSize
operator|>=
literal|0
operator|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|valid
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|path
operator|+
literal|" does not exist in pseudo local file system"
argument_list|)
throw|;
block|}
return|return
name|fileSize
return|;
block|}
comment|/**    * @See create(Path) for details    */
annotation|@
name|Override
DECL|method|open (Path path, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|fileSize
init|=
name|validateFileNameFormat
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|RandomInputStream
argument_list|(
name|fileSize
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
return|return
operator|new
name|FSDataInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**    * @See create(Path) for details    */
annotation|@
name|Override
DECL|method|open (Path path)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|path
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path path)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|fileSize
init|=
name|validateFileNameFormat
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|FileStatus
argument_list|(
name|fileSize
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|TIME
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exists (Path path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
try|try
block|{
name|validateFileNameFormat
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|create (Path path, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|path
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|create
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (Path path)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
return|return
operator|new
name|FileStatus
index|[]
block|{
name|getFileStatus
argument_list|(
name|path
argument_list|)
block|}
return|;
block|}
comment|/**    * Input Stream that generates specified number of random bytes.    */
DECL|class|RandomInputStream
specifier|static
class|class
name|RandomInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
implements|,
name|PositionedReadable
block|{
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|val
specifier|private
name|BytesWritable
name|val
init|=
literal|null
decl_stmt|;
DECL|field|positionInVal
specifier|private
name|int
name|positionInVal
init|=
literal|0
decl_stmt|;
comment|// current position in the buffer 'val'
DECL|field|totalSize
specifier|private
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
comment|// total number of random bytes to be generated
DECL|field|curPos
specifier|private
name|long
name|curPos
init|=
literal|0
decl_stmt|;
comment|// current position in this stream
comment|/**      * @param size total number of random bytes to be generated in this stream      * @param bufferSize the buffer size. An internal buffer array of length      *<code>bufferSize</code> is created. If<code>bufferSize</code> is not a      * positive number, then a default value of 1MB is used.      */
DECL|method|RandomInputStream (long size, int bufferSize)
name|RandomInputStream
parameter_list|(
name|long
name|size
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|totalSize
operator|=
name|size
expr_stmt|;
if|if
condition|(
name|bufferSize
operator|<=
literal|0
condition|)
block|{
name|bufferSize
operator|=
name|DEFAULT_BUFFER_SIZE
expr_stmt|;
block|}
name|val
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[
name|bufferSize
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|curPos
operator|<
name|totalSize
condition|)
block|{
if|if
condition|(
name|positionInVal
operator|<
name|val
operator|.
name|getLength
argument_list|()
condition|)
block|{
comment|// use buffered byte
name|b
index|[
literal|0
index|]
operator|=
name|val
operator|.
name|getBytes
argument_list|()
index|[
name|positionInVal
operator|++
index|]
expr_stmt|;
operator|++
name|curPos
expr_stmt|;
block|}
else|else
block|{
comment|// generate data
name|int
name|num
init|=
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|<
literal|0
condition|)
block|{
return|return
name|num
return|;
block|}
block|}
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|b
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] bytes)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] bytes, int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|bytes
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
if|if
condition|(
name|curPos
operator|==
name|totalSize
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// EOF
block|}
name|int
name|numBytes
init|=
name|len
decl_stmt|;
if|if
condition|(
name|numBytes
operator|>
operator|(
name|totalSize
operator|-
name|curPos
operator|)
condition|)
block|{
comment|// position in file is close to EOF
name|numBytes
operator|=
call|(
name|int
call|)
argument_list|(
name|totalSize
operator|-
name|curPos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numBytes
operator|>
operator|(
name|val
operator|.
name|getLength
argument_list|()
operator|-
name|positionInVal
operator|)
condition|)
block|{
comment|// need to generate data into val
name|r
operator|.
name|nextBytes
argument_list|(
name|val
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|positionInVal
operator|=
literal|0
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
name|positionInVal
argument_list|,
name|bytes
argument_list|,
name|off
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|curPos
operator|+=
name|numBytes
expr_stmt|;
name|positionInVal
operator|+=
name|numBytes
expr_stmt|;
return|return
name|numBytes
return|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|val
operator|.
name|getLength
argument_list|()
operator|-
name|positionInVal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read (long position, byte[] buffer, int offset, int length)
specifier|public
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|readFully (long position, byte[] buffer)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|readFully (long position, byte[] buffer, int offset, int length)
specifier|public
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Get the current position in this stream/pseudo-file      * @return the position in this stream/pseudo-file      * @throws IOException      */
annotation|@
name|Override
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|curPos
return|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|append (Path path, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Append is not supported"
operator|+
literal|" in pseudo local file system."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Mkdirs is not supported"
operator|+
literal|" in pseudo local file system."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|rename (Path src, Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Rename is not supported"
operator|+
literal|" in pseudo local file system."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|delete (Path path, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"File deletion is not supported "
operator|+
literal|"in pseudo local file system."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (Path newDir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|newDir
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"SetWorkingDirectory "
operator|+
literal|"is not supported in pseudo local file system."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

