begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.netty
package|package
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
name|netty
package|;
end_package

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_LENGTH
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONNECTION
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|TRANSFER_ENCODING
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|HOST
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Values
operator|.
name|KEEP_ALIVE
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Values
operator|.
name|CLOSE
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpVersion
operator|.
name|HTTP_1_1
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|CancellationException
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
name|CountDownLatch
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
name|ExecutionException
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
name|Future
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
name|TimeoutException
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

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|core
operator|.
name|header
operator|.
name|InBoundHeaders
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|ContainerRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|ContainerResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|ContainerResponseWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|spi
operator|.
name|container
operator|.
name|WebApplication
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|DefaultHttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaderUtil
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
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
name|ozone
operator|.
name|web
operator|.
name|interfaces
operator|.
name|StorageHandler
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
name|handlers
operator|.
name|StorageHandlerBuilder
import|;
end_import

begin_comment
comment|/**  * This is a custom Jersey container that hosts the Object Store web  * application. It supports dispatching an inbound Netty {@link HttpRequest}  * to the Object Store Jersey application.  Request dispatching must run  * asynchronously, because the Jersey application must consume the inbound  * HTTP request from a  piped stream and produce the outbound HTTP response  * for another piped stream.The Netty channel handlers consume the connected  * ends of these piped streams. Request dispatching cannot run directly on  * the Netty threads, or there would be a risk of deadlock (one thread  * producing/consuming its end of the pipe  while no other thread is  * producing/consuming the opposite end).  */
end_comment

begin_class
DECL|class|ObjectStoreJerseyContainer
specifier|public
specifier|final
class|class
name|ObjectStoreJerseyContainer
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
name|ObjectStoreJerseyContainer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|webapp
specifier|private
specifier|final
name|WebApplication
name|webapp
decl_stmt|;
DECL|field|storageHandler
specifier|private
name|StorageHandler
name|storageHandler
decl_stmt|;
comment|/**    * Creates a new ObjectStoreJerseyContainer.    *    * @param webapp web application    */
DECL|method|ObjectStoreJerseyContainer (WebApplication webapp)
specifier|public
name|ObjectStoreJerseyContainer
parameter_list|(
name|WebApplication
name|webapp
parameter_list|)
block|{
name|this
operator|.
name|webapp
operator|=
name|webapp
expr_stmt|;
block|}
comment|/**    * Sets the {@link StorageHandler}. This must be called before dispatching any    * requests.    *    * @param newStorageHandler {@link StorageHandler} implementation    */
DECL|method|setStorageHandler (StorageHandler newStorageHandler)
specifier|public
name|void
name|setStorageHandler
parameter_list|(
name|StorageHandler
name|newStorageHandler
parameter_list|)
block|{
name|this
operator|.
name|storageHandler
operator|=
name|newStorageHandler
expr_stmt|;
block|}
comment|/**    * Asynchronously executes an HTTP request.    *    * @param nettyReq HTTP request    * @param reqIn input stream for reading request body    * @param respOut output stream for writing response body    */
DECL|method|dispatch (HttpRequest nettyReq, InputStream reqIn, OutputStream respOut)
specifier|public
name|Future
argument_list|<
name|HttpResponse
argument_list|>
name|dispatch
parameter_list|(
name|HttpRequest
name|nettyReq
parameter_list|,
name|InputStream
name|reqIn
parameter_list|,
name|OutputStream
name|respOut
parameter_list|)
block|{
comment|// The request executes on a separate background thread.  As soon as enough
comment|// processing has completed to bootstrap the outbound response, the thread
comment|// counts down on a latch.  This latch also unblocks callers trying to get
comment|// the asynchronous response out of the returned future.
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RequestRunner
name|runner
init|=
operator|new
name|RequestRunner
argument_list|(
name|nettyReq
argument_list|,
name|reqIn
argument_list|,
name|respOut
argument_list|,
name|latch
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
operator|new
name|Future
argument_list|<
name|HttpResponse
argument_list|>
argument_list|()
block|{
specifier|private
specifier|volatile
name|boolean
name|isCancelled
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|cancel
parameter_list|(
name|boolean
name|mayInterruptIfRunning
parameter_list|)
block|{
if|if
condition|(
name|latch
operator|.
name|getCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|mayInterruptIfRunning
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|thread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted while attempting to cancel dispatch thread."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|isCancelled
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|HttpResponse
name|get
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|checkCancelled
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|getOrThrow
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|HttpResponse
name|get
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|checkCancelled
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|latch
operator|.
name|await
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Timed out waiting for HttpResponse after %d %s."
argument_list|,
name|timeout
argument_list|,
name|unit
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|getOrThrow
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|isCancelled
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
operator|!
name|isCancelled
operator|&&
name|latch
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|private
name|void
name|checkCancelled
parameter_list|()
block|{
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CancellationException
argument_list|()
throw|;
block|}
block|}
specifier|private
name|HttpResponse
name|getOrThrow
parameter_list|()
throws|throws
name|ExecutionException
block|{
try|try
block|{
return|return
name|runner
operator|.
name|getResponse
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Runs the actual handling of the HTTP request.    */
DECL|class|RequestRunner
specifier|private
specifier|final
class|class
name|RequestRunner
implements|implements
name|Runnable
implements|,
name|ContainerResponseWriter
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|nettyReq
specifier|private
specifier|final
name|HttpRequest
name|nettyReq
decl_stmt|;
DECL|field|reqIn
specifier|private
specifier|final
name|InputStream
name|reqIn
decl_stmt|;
DECL|field|respOut
specifier|private
specifier|final
name|OutputStream
name|respOut
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
DECL|field|nettyResp
specifier|private
name|HttpResponse
name|nettyResp
decl_stmt|;
comment|/**      * Creates a new RequestRunner.      *      * @param nettyReq HTTP request      * @param reqIn input stream for reading request body      * @param respOut output stream for writing response body      * @param latch for coordinating asynchronous return of HTTP response      */
DECL|method|RequestRunner (HttpRequest nettyReq, InputStream reqIn, OutputStream respOut, CountDownLatch latch)
specifier|public
name|RequestRunner
parameter_list|(
name|HttpRequest
name|nettyReq
parameter_list|,
name|InputStream
name|reqIn
parameter_list|,
name|OutputStream
name|respOut
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
name|this
operator|.
name|nettyReq
operator|=
name|nettyReq
expr_stmt|;
name|this
operator|.
name|reqIn
operator|=
name|reqIn
expr_stmt|;
name|this
operator|.
name|respOut
operator|=
name|respOut
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"begin RequestRunner, nettyReq = {}"
argument_list|,
name|this
operator|.
name|nettyReq
argument_list|)
expr_stmt|;
name|StorageHandlerBuilder
operator|.
name|setStorageHandler
argument_list|(
name|ObjectStoreJerseyContainer
operator|.
name|this
operator|.
name|storageHandler
argument_list|)
expr_stmt|;
try|try
block|{
name|ContainerRequest
name|jerseyReq
init|=
name|nettyRequestToJerseyRequest
argument_list|(
name|ObjectStoreJerseyContainer
operator|.
name|this
operator|.
name|webapp
argument_list|,
name|this
operator|.
name|nettyReq
argument_list|,
name|this
operator|.
name|reqIn
argument_list|)
decl_stmt|;
name|ObjectStoreJerseyContainer
operator|.
name|this
operator|.
name|webapp
operator|.
name|handleRequest
argument_list|(
name|jerseyReq
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|e
expr_stmt|;
name|this
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|reqIn
argument_list|,
name|this
operator|.
name|respOut
argument_list|)
expr_stmt|;
name|StorageHandlerBuilder
operator|.
name|removeStorageHandler
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"end RequestRunner, nettyReq = {}"
argument_list|,
name|this
operator|.
name|nettyReq
argument_list|)
expr_stmt|;
block|}
comment|/**      * This is a callback triggered by Jersey as soon as dispatch has completed      * to the point of knowing what kind of response to return.  We save the      * response and trigger the latch to unblock callers waiting on the      * asynchronous return of the response.  Our response always sets a      * Content-Length header.  (We do not support Transfer-Encoding: chunked.)      * We also return the output stream for Jersey to use for writing the      * response body.      *      * @param contentLength length of response      * @param jerseyResp HTTP response returned by Jersey      * @return OutputStream for Jersey to use for writing the response body      */
annotation|@
name|Override
DECL|method|writeStatusAndHeaders (long contentLength, ContainerResponse jerseyResp)
specifier|public
name|OutputStream
name|writeStatusAndHeaders
parameter_list|(
name|long
name|contentLength
parameter_list|,
name|ContainerResponse
name|jerseyResp
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"begin writeStatusAndHeaders, contentLength = {}, jerseyResp = {}."
argument_list|,
name|contentLength
argument_list|,
name|jerseyResp
argument_list|)
expr_stmt|;
name|this
operator|.
name|nettyResp
operator|=
name|jerseyResponseToNettyResponse
argument_list|(
name|jerseyResp
argument_list|)
expr_stmt|;
name|this
operator|.
name|nettyResp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|contentLength
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|nettyResp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONNECTION
argument_list|,
name|HttpHeaderUtil
operator|.
name|isKeepAlive
argument_list|(
name|this
operator|.
name|nettyReq
argument_list|)
condition|?
name|KEEP_ALIVE
else|:
name|CLOSE
argument_list|)
expr_stmt|;
name|this
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"end writeStatusAndHeaders, contentLength = {}, jerseyResp = {}."
argument_list|,
name|contentLength
argument_list|,
name|jerseyResp
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|respOut
return|;
block|}
comment|/**      * This is a callback triggered by Jersey after it has completed writing the      * response body to the stream.  We must close the stream here to unblock      * the Netty thread consuming the last chunk of the response from the input      * end of the piped stream.      *      * @throws IOException if there is an I/O error      */
annotation|@
name|Override
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|respOut
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the HTTP response calculated by the Jersey application, or throws an      * exception if an error occurred during processing.  It only makes sense to      * call this method after waiting on the latch to trigger.      *      * @return HTTP response      * @throws Exception if there was an error executing the request      */
DECL|method|getResponse ()
specifier|public
name|HttpResponse
name|getResponse
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|this
operator|.
name|exception
throw|;
block|}
return|return
name|this
operator|.
name|nettyResp
return|;
block|}
block|}
comment|/**    * Converts a Jersey HTTP response object to a Netty HTTP response object.    *    * @param jerseyResp Jersey HTTP response    * @return Netty HTTP response    */
DECL|method|jerseyResponseToNettyResponse ( ContainerResponse jerseyResp)
specifier|private
specifier|static
name|HttpResponse
name|jerseyResponseToNettyResponse
parameter_list|(
name|ContainerResponse
name|jerseyResp
parameter_list|)
block|{
name|HttpResponse
name|nettyResp
init|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|HttpResponseStatus
operator|.
name|valueOf
argument_list|(
name|jerseyResp
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|header
range|:
name|jerseyResp
operator|.
name|getHttpHeaders
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|header
operator|.
name|getKey
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|CONTENT_LENGTH
operator|.
name|toString
argument_list|()
argument_list|)
operator|&&
operator|!
name|header
operator|.
name|getKey
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|TRANSFER_ENCODING
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|nettyResp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|header
operator|.
name|getKey
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nettyResp
return|;
block|}
comment|/**    * Converts a Netty HTTP request object to a Jersey HTTP request object.    *    * @param webapp web application    * @param nettyReq Netty HTTP request    * @param reqIn input stream for reading request body    * @return Jersey HTTP request    * @throws URISyntaxException if there is an error handling the request URI    */
DECL|method|nettyRequestToJerseyRequest ( WebApplication webapp, HttpRequest nettyReq, InputStream reqIn)
specifier|private
specifier|static
name|ContainerRequest
name|nettyRequestToJerseyRequest
parameter_list|(
name|WebApplication
name|webapp
parameter_list|,
name|HttpRequest
name|nettyReq
parameter_list|,
name|InputStream
name|reqIn
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|HttpHeaders
name|nettyHeaders
init|=
name|nettyReq
operator|.
name|headers
argument_list|()
decl_stmt|;
name|InBoundHeaders
name|jerseyHeaders
init|=
operator|new
name|InBoundHeaders
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|nettyHeaders
operator|.
name|names
argument_list|()
control|)
block|{
name|jerseyHeaders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|nettyHeaders
operator|.
name|getAll
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|host
init|=
name|nettyHeaders
operator|.
name|get
argument_list|(
name|HOST
argument_list|)
decl_stmt|;
name|String
name|scheme
init|=
name|host
operator|.
name|startsWith
argument_list|(
literal|"https"
argument_list|)
condition|?
literal|"https://"
else|:
literal|"http://"
decl_stmt|;
name|String
name|baseUri
init|=
name|scheme
operator|+
name|host
operator|+
literal|"/"
decl_stmt|;
name|String
name|reqUri
init|=
name|scheme
operator|+
name|host
operator|+
name|nettyReq
operator|.
name|uri
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"baseUri = {}, reqUri = {}"
argument_list|,
name|baseUri
argument_list|,
name|reqUri
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContainerRequest
argument_list|(
name|webapp
argument_list|,
name|nettyReq
operator|.
name|method
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|baseUri
argument_list|)
argument_list|,
operator|new
name|URI
argument_list|(
name|reqUri
argument_list|)
argument_list|,
name|jerseyHeaders
argument_list|,
name|reqIn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

