begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|io
operator|.
name|input
operator|.
name|BoundedInputStream
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
name|FSInputStream
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
name|namenode
operator|.
name|StreamFile
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

begin_comment
comment|/**  * To support HTTP byte streams, a new connection to an HTTP server needs to be  * created each time. This class hides the complexity of those multiple   * connections from the client. Whenever seek() is called, a new connection  * is made on the successive read(). The normal input stream functions are   * connected to the currently active input stream.   */
end_comment

begin_class
DECL|class|ByteRangeInputStream
specifier|public
specifier|abstract
class|class
name|ByteRangeInputStream
extends|extends
name|FSInputStream
block|{
comment|/**    * This class wraps a URL and provides method to open connection.    * It can be overridden to change how a connection is opened.    */
DECL|class|URLOpener
specifier|public
specifier|static
specifier|abstract
class|class
name|URLOpener
block|{
DECL|field|url
specifier|protected
name|URL
name|url
decl_stmt|;
DECL|method|URLOpener (URL u)
specifier|public
name|URLOpener
parameter_list|(
name|URL
name|u
parameter_list|)
block|{
name|url
operator|=
name|u
expr_stmt|;
block|}
DECL|method|setURL (URL u)
specifier|public
name|void
name|setURL
parameter_list|(
name|URL
name|u
parameter_list|)
block|{
name|url
operator|=
name|u
expr_stmt|;
block|}
DECL|method|getURL ()
specifier|public
name|URL
name|getURL
parameter_list|()
block|{
return|return
name|url
return|;
block|}
comment|/** Connect to server with a data offset. */
DECL|method|connect (final long offset, final boolean resolved)
specifier|protected
specifier|abstract
name|HttpURLConnection
name|connect
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|boolean
name|resolved
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|enum|StreamStatus
enum|enum
name|StreamStatus
block|{
DECL|enumConstant|NORMAL
DECL|enumConstant|SEEK
DECL|enumConstant|CLOSED
name|NORMAL
block|,
name|SEEK
block|,
name|CLOSED
block|}
DECL|field|in
specifier|protected
name|InputStream
name|in
decl_stmt|;
DECL|field|originalURL
specifier|protected
name|URLOpener
name|originalURL
decl_stmt|;
DECL|field|resolvedURL
specifier|protected
name|URLOpener
name|resolvedURL
decl_stmt|;
DECL|field|startPos
specifier|protected
name|long
name|startPos
init|=
literal|0
decl_stmt|;
DECL|field|currentPos
specifier|protected
name|long
name|currentPos
init|=
literal|0
decl_stmt|;
DECL|field|filelength
specifier|protected
name|long
name|filelength
decl_stmt|;
DECL|field|status
name|StreamStatus
name|status
init|=
name|StreamStatus
operator|.
name|SEEK
decl_stmt|;
comment|/**    * Create with the specified URLOpeners. Original url is used to open the     * stream for the first time. Resolved url is used in subsequent requests.    * @param o Original url    * @param r Resolved url    */
DECL|method|ByteRangeInputStream (URLOpener o, URLOpener r)
specifier|public
name|ByteRangeInputStream
parameter_list|(
name|URLOpener
name|o
parameter_list|,
name|URLOpener
name|r
parameter_list|)
block|{
name|this
operator|.
name|originalURL
operator|=
name|o
expr_stmt|;
name|this
operator|.
name|resolvedURL
operator|=
name|r
expr_stmt|;
block|}
DECL|method|getResolvedUrl (final HttpURLConnection connection )
specifier|protected
specifier|abstract
name|URL
name|getResolvedUrl
parameter_list|(
specifier|final
name|HttpURLConnection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|VisibleForTesting
DECL|method|getInputStream ()
specifier|protected
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|NORMAL
case|:
break|break;
case|case
name|SEEK
case|:
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|in
operator|=
name|openInputStream
argument_list|()
expr_stmt|;
name|status
operator|=
name|StreamStatus
operator|.
name|NORMAL
expr_stmt|;
break|break;
case|case
name|CLOSED
case|:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream closed"
argument_list|)
throw|;
block|}
return|return
name|in
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|openInputStream ()
specifier|protected
name|InputStream
name|openInputStream
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Use the original url if no resolved url exists, eg. if
comment|// it's the first time a request is made.
specifier|final
name|boolean
name|resolved
init|=
name|resolvedURL
operator|.
name|getURL
argument_list|()
operator|!=
literal|null
decl_stmt|;
specifier|final
name|URLOpener
name|opener
init|=
name|resolved
condition|?
name|resolvedURL
else|:
name|originalURL
decl_stmt|;
specifier|final
name|HttpURLConnection
name|connection
init|=
name|opener
operator|.
name|connect
argument_list|(
name|startPos
argument_list|,
name|resolved
argument_list|)
decl_stmt|;
specifier|final
name|String
name|cl
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
name|StreamFile
operator|.
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|cl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|StreamFile
operator|.
name|CONTENT_LENGTH
operator|+
literal|" header is missing"
argument_list|)
throw|;
block|}
specifier|final
name|long
name|streamlength
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|cl
argument_list|)
decl_stmt|;
name|filelength
operator|=
name|startPos
operator|+
name|streamlength
expr_stmt|;
comment|// Java has a bug with>2GB request streams.  It won't bounds check
comment|// the reads so the transfer blocks until the server times out
name|InputStream
name|is
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|streamlength
argument_list|)
decl_stmt|;
name|resolvedURL
operator|.
name|setURL
argument_list|(
name|getResolvedUrl
argument_list|(
name|connection
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|is
return|;
block|}
DECL|method|update (final int n)
specifier|private
name|int
name|update
parameter_list|(
specifier|final
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|currentPos
operator|+=
name|n
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentPos
operator|<
name|filelength
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got EOF but currentPos = "
operator|+
name|currentPos
operator|+
literal|"< filelength = "
operator|+
name|filelength
argument_list|)
throw|;
block|}
return|return
name|n
return|;
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
specifier|final
name|int
name|b
init|=
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|()
decl_stmt|;
name|update
argument_list|(
operator|(
name|b
operator|==
operator|-
literal|1
operator|)
condition|?
operator|-
literal|1
else|:
literal|1
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte b[], int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
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
return|return
name|update
argument_list|(
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Seek to the given offset from the start of the file.    * The next read() will be from that location.  Can't    * seek past the end of the file.    */
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
if|if
condition|(
name|pos
operator|!=
name|currentPos
condition|)
block|{
name|startPos
operator|=
name|pos
expr_stmt|;
name|currentPos
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|status
operator|!=
name|StreamStatus
operator|.
name|CLOSED
condition|)
block|{
name|status
operator|=
name|StreamStatus
operator|.
name|SEEK
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return the current offset from the start of the file    */
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
name|currentPos
return|;
block|}
comment|/**    * Seeks a different copy of the data.  Returns true if    * found a new source, false otherwise.    */
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
return|return
literal|false
return|;
block|}
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
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
name|status
operator|=
name|StreamStatus
operator|.
name|CLOSED
expr_stmt|;
block|}
block|}
end_class

end_unit

