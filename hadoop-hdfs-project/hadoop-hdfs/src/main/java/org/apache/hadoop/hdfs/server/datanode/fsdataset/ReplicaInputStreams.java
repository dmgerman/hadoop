begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|FileIoProvider
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIOException
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

begin_comment
comment|/**  * Contains the input streams for the data and checksum of a replica.  */
end_comment

begin_class
DECL|class|ReplicaInputStreams
specifier|public
class|class
name|ReplicaInputStreams
implements|implements
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|dataIn
specifier|private
name|InputStream
name|dataIn
decl_stmt|;
DECL|field|checksumIn
specifier|private
name|InputStream
name|checksumIn
decl_stmt|;
DECL|field|volumeRef
specifier|private
name|FsVolumeReference
name|volumeRef
decl_stmt|;
DECL|field|fileIoProvider
specifier|private
specifier|final
name|FileIoProvider
name|fileIoProvider
decl_stmt|;
DECL|field|dataInFd
specifier|private
name|FileDescriptor
name|dataInFd
init|=
literal|null
decl_stmt|;
comment|/** Create an object with a data input stream and a checksum input stream. */
DECL|method|ReplicaInputStreams ( InputStream dataStream, InputStream checksumStream, FsVolumeReference volumeRef, FileIoProvider fileIoProvider)
specifier|public
name|ReplicaInputStreams
parameter_list|(
name|InputStream
name|dataStream
parameter_list|,
name|InputStream
name|checksumStream
parameter_list|,
name|FsVolumeReference
name|volumeRef
parameter_list|,
name|FileIoProvider
name|fileIoProvider
parameter_list|)
block|{
name|this
operator|.
name|volumeRef
operator|=
name|volumeRef
expr_stmt|;
name|this
operator|.
name|fileIoProvider
operator|=
name|fileIoProvider
expr_stmt|;
name|this
operator|.
name|dataIn
operator|=
name|dataStream
expr_stmt|;
name|this
operator|.
name|checksumIn
operator|=
name|checksumStream
expr_stmt|;
if|if
condition|(
name|dataIn
operator|instanceof
name|FileInputStream
condition|)
block|{
try|try
block|{
name|dataInFd
operator|=
operator|(
operator|(
name|FileInputStream
operator|)
name|dataIn
operator|)
operator|.
name|getFD
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not get file descriptor for inputstream of class "
operator|+
name|this
operator|.
name|dataIn
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not get file descriptor for inputstream of class "
operator|+
name|this
operator|.
name|dataIn
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @return the data input stream. */
DECL|method|getDataIn ()
specifier|public
name|InputStream
name|getDataIn
parameter_list|()
block|{
return|return
name|dataIn
return|;
block|}
comment|/** @return the checksum input stream. */
DECL|method|getChecksumIn ()
specifier|public
name|InputStream
name|getChecksumIn
parameter_list|()
block|{
return|return
name|checksumIn
return|;
block|}
DECL|method|getDataInFd ()
specifier|public
name|FileDescriptor
name|getDataInFd
parameter_list|()
block|{
return|return
name|dataInFd
return|;
block|}
DECL|method|getVolumeRef ()
specifier|public
name|FsVolumeReference
name|getVolumeRef
parameter_list|()
block|{
return|return
name|volumeRef
return|;
block|}
DECL|method|readDataFully (byte[] buf, int off, int len)
specifier|public
name|void
name|readDataFully
parameter_list|(
name|byte
index|[]
name|buf
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
name|IOUtils
operator|.
name|readFully
argument_list|(
name|dataIn
argument_list|,
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|readChecksumFully (byte[] buf, int off, int len)
specifier|public
name|void
name|readChecksumFully
parameter_list|(
name|byte
index|[]
name|buf
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
name|IOUtils
operator|.
name|readFully
argument_list|(
name|checksumIn
argument_list|,
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|skipDataFully (long len)
specifier|public
name|void
name|skipDataFully
parameter_list|(
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|dataIn
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|skipChecksumFully (long len)
specifier|public
name|void
name|skipChecksumFully
parameter_list|(
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|checksumIn
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|closeChecksumStream ()
specifier|public
name|void
name|closeChecksumStream
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|checksumIn
argument_list|)
expr_stmt|;
name|checksumIn
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|dropCacheBehindReads (String identifier, long offset, long len, int flags)
specifier|public
name|void
name|dropCacheBehindReads
parameter_list|(
name|String
name|identifier
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|NativeIOException
block|{
assert|assert
name|this
operator|.
name|dataInFd
operator|!=
literal|null
operator|:
literal|"null dataInFd!"
assert|;
name|fileIoProvider
operator|.
name|posixFadvise
argument_list|(
name|getVolumeRef
argument_list|()
operator|.
name|getVolume
argument_list|()
argument_list|,
name|identifier
argument_list|,
name|dataInFd
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
DECL|method|closeStreams ()
specifier|public
name|void
name|closeStreams
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|checksumIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|checksumIn
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close checksum file
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioe
operator|=
name|e
expr_stmt|;
block|}
name|checksumIn
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|dataIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close data file
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioe
operator|=
name|e
expr_stmt|;
block|}
name|dataIn
operator|=
literal|null
expr_stmt|;
name|dataInFd
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|volumeRef
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|volumeRef
argument_list|)
expr_stmt|;
name|volumeRef
operator|=
literal|null
expr_stmt|;
block|}
comment|// throw IOException if there is any
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|dataIn
operator|=
literal|null
expr_stmt|;
name|dataInFd
operator|=
literal|null
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|checksumIn
argument_list|)
expr_stmt|;
name|checksumIn
operator|=
literal|null
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|volumeRef
argument_list|)
expr_stmt|;
name|volumeRef
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

