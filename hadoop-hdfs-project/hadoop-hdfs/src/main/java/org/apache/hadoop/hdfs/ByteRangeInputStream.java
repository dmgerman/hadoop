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
name|MalformedURLException
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|OffsetParam
import|;
end_import

begin_comment
comment|/**  * To support HTTP byte streams, a new connection to an HTTP server needs to be  * created each time. This class hides the complexity of those multiple   * connections from the client. Whenever seek() is called, a new connection  * is made on the successive read(). The normal input stream functions are   * connected to the currently active input stream.   */
end_comment

begin_class
DECL|class|ByteRangeInputStream
specifier|public
class|class
name|ByteRangeInputStream
extends|extends
name|FSInputStream
block|{
comment|/**    * This class wraps a URL to allow easy mocking when testing. The URL class    * cannot be easily mocked because it is public.    */
DECL|class|URLOpener
specifier|static
class|class
name|URLOpener
block|{
DECL|field|url
specifier|protected
name|URL
name|url
decl_stmt|;
comment|/** The url with offset parameter */
DECL|field|offsetUrl
specifier|private
name|URL
name|offsetUrl
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
DECL|method|openConnection ()
name|HttpURLConnection
name|openConnection
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|HttpURLConnection
operator|)
name|offsetUrl
operator|.
name|openConnection
argument_list|()
return|;
block|}
DECL|method|openConnection (final long offset)
specifier|private
name|HttpURLConnection
name|openConnection
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|offsetUrl
operator|=
name|offset
operator|==
literal|0L
condition|?
name|url
else|:
operator|new
name|URL
argument_list|(
name|url
operator|+
literal|"&"
operator|+
operator|new
name|OffsetParam
argument_list|(
name|offset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|HttpURLConnection
name|conn
init|=
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|0L
condition|)
block|{
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Range"
argument_list|,
literal|"bytes="
operator|+
name|offset
operator|+
literal|"-"
argument_list|)
expr_stmt|;
block|}
return|return
name|conn
return|;
block|}
block|}
DECL|field|OFFSET_PARAM_PREFIX
specifier|static
specifier|private
specifier|final
name|String
name|OFFSET_PARAM_PREFIX
init|=
name|OffsetParam
operator|.
name|NAME
operator|+
literal|"="
decl_stmt|;
comment|/** Remove offset parameter, if there is any, from the url */
DECL|method|removeOffsetParam (final URL url)
specifier|static
name|URL
name|removeOffsetParam
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|String
name|query
init|=
name|url
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
name|url
return|;
block|}
specifier|final
name|String
name|lower
init|=
name|query
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|lower
operator|.
name|startsWith
argument_list|(
name|OFFSET_PARAM_PREFIX
argument_list|)
operator|&&
operator|!
name|lower
operator|.
name|contains
argument_list|(
literal|"&"
operator|+
name|OFFSET_PARAM_PREFIX
argument_list|)
condition|)
block|{
return|return
name|url
return|;
block|}
comment|//rebuild query
name|StringBuilder
name|b
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|query
argument_list|,
literal|"&"
argument_list|)
init|;
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|token
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|token
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
name|OFFSET_PARAM_PREFIX
argument_list|)
condition|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
name|b
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|query
operator|=
name|b
operator|==
literal|null
condition|?
literal|""
else|:
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
specifier|final
name|String
name|urlStr
init|=
name|url
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
name|urlStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|urlStr
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
argument_list|)
operator|+
name|query
argument_list|)
return|;
block|}
DECL|enum|StreamStatus
enum|enum
name|StreamStatus
block|{
DECL|enumConstant|NORMAL
DECL|enumConstant|SEEK
name|NORMAL
block|,
name|SEEK
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
comment|/** Create an input stream with the URL. */
DECL|method|ByteRangeInputStream (final URL url)
specifier|public
name|ByteRangeInputStream
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|URLOpener
argument_list|(
name|url
argument_list|)
argument_list|,
operator|new
name|URLOpener
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteRangeInputStream (URLOpener o, URLOpener r)
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
DECL|method|getInputStream ()
specifier|private
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|status
operator|!=
name|StreamStatus
operator|.
name|NORMAL
condition|)
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
comment|// Use the original url if no resolved url exists, eg. if
comment|// it's the first time a request is made.
specifier|final
name|URLOpener
name|opener
init|=
operator|(
name|resolvedURL
operator|.
name|getURL
argument_list|()
operator|==
literal|null
operator|)
condition|?
name|originalURL
else|:
name|resolvedURL
decl_stmt|;
specifier|final
name|HttpURLConnection
name|connection
init|=
name|opener
operator|.
name|openConnection
argument_list|(
name|startPos
argument_list|)
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
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
name|filelength
operator|=
operator|(
name|cl
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|cl
argument_list|)
expr_stmt|;
if|if
condition|(
name|HftpFileSystem
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|HftpFileSystem
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"filelength = "
operator|+
name|filelength
argument_list|)
expr_stmt|;
block|}
name|in
operator|=
name|connection
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
throw|throw
name|fnfe
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|HftpFileSystem
operator|.
name|throwIOExceptionFromConnection
argument_list|(
name|connection
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|int
name|respCode
init|=
name|connection
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|startPos
operator|!=
literal|0
operator|&&
name|respCode
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_PARTIAL
condition|)
block|{
comment|// We asked for a byte range but did not receive a partial content
comment|// response...
throw|throw
operator|new
name|IOException
argument_list|(
literal|"HTTP_PARTIAL expected, received "
operator|+
name|respCode
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|startPos
operator|==
literal|0
operator|&&
name|respCode
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
comment|// We asked for all bytes from the beginning but didn't receive a 200
comment|// response (none of the other 2xx codes are valid here)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"HTTP_OK expected, received "
operator|+
name|respCode
argument_list|)
throw|;
block|}
name|resolvedURL
operator|.
name|setURL
argument_list|(
name|removeOffsetParam
argument_list|(
name|connection
operator|.
name|getURL
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|status
operator|=
name|StreamStatus
operator|.
name|NORMAL
expr_stmt|;
block|}
return|return
name|in
return|;
block|}
DECL|method|update (final boolean isEOF, final int n)
specifier|private
name|void
name|update
parameter_list|(
specifier|final
name|boolean
name|isEOF
parameter_list|,
specifier|final
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isEOF
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
block|}
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
name|b
operator|==
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/**    * Seek to the given offset from the start of the file.    * The next read() will be from that location.  Can't    * seek past the end of the file.    */
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
name|status
operator|=
name|StreamStatus
operator|.
name|SEEK
expr_stmt|;
block|}
block|}
comment|/**    * Return the current offset from the start of the file    */
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
block|}
end_class

end_unit

