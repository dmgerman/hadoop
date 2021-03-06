begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|ErrorReceivingResponseEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|OperationContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|RequestResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|ResponseReceivedEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|SendingRequestEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageEvent
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
name|classification
operator|.
name|InterfaceAudience
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

begin_comment
comment|/**  * Throttles Azure Storage read and write operations to achieve maximum  * throughput by minimizing errors.  The errors occur when the account ingress  * or egress limits are exceeded and the server-side throttles requests.  * Server-side throttling causes the retry policy to be used, but the retry  * policy sleeps for long periods of time causing the total ingress or egress  * throughput to be as much as 35% lower than optimal.  The retry policy is also  * after the fact, in that it applies after a request fails.  On the other hand,  * the client-side throttling implemented here happens before requests are made  * and sleeps just enough to minimize errors, allowing optimal ingress and/or  * egress throughput.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ClientThrottlingIntercept
specifier|final
class|class
name|ClientThrottlingIntercept
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
name|ClientThrottlingIntercept
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|singleton
specifier|private
specifier|static
name|ClientThrottlingIntercept
name|singleton
init|=
literal|null
decl_stmt|;
DECL|field|readThrottler
specifier|private
name|ClientThrottlingAnalyzer
name|readThrottler
init|=
literal|null
decl_stmt|;
DECL|field|writeThrottler
specifier|private
name|ClientThrottlingAnalyzer
name|writeThrottler
init|=
literal|null
decl_stmt|;
comment|// Hide default constructor
DECL|method|ClientThrottlingIntercept ()
specifier|private
name|ClientThrottlingIntercept
parameter_list|()
block|{
name|readThrottler
operator|=
operator|new
name|ClientThrottlingAnalyzer
argument_list|(
literal|"read"
argument_list|)
expr_stmt|;
name|writeThrottler
operator|=
operator|new
name|ClientThrottlingAnalyzer
argument_list|(
literal|"write"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Client-side throttling is enabled for the WASB file system."
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeSingleton ()
specifier|static
specifier|synchronized
name|void
name|initializeSingleton
parameter_list|()
block|{
if|if
condition|(
name|singleton
operator|==
literal|null
condition|)
block|{
name|singleton
operator|=
operator|new
name|ClientThrottlingIntercept
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|hook (OperationContext context)
specifier|static
name|void
name|hook
parameter_list|(
name|OperationContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|getErrorReceivingResponseEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|ErrorReceivingResponseEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getSendingRequestEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|SendingRequestEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getResponseReceivedEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|ResponseReceivedEventHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|updateMetrics (HttpURLConnection conn, RequestResult result)
specifier|private
specifier|static
name|void
name|updateMetrics
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|RequestResult
name|result
parameter_list|)
block|{
name|BlobOperationDescriptor
operator|.
name|OperationType
name|operationType
init|=
name|BlobOperationDescriptor
operator|.
name|getOperationType
argument_list|(
name|conn
argument_list|)
decl_stmt|;
name|int
name|status
init|=
name|result
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|long
name|contentLength
init|=
literal|0
decl_stmt|;
comment|// If the socket is terminated prior to receiving a response, the HTTP
comment|// status may be 0 or -1.  A status less than 200 or greater than or equal
comment|// to 500 is considered an error.
name|boolean
name|isFailedOperation
init|=
operator|(
name|status
operator|<
name|HttpURLConnection
operator|.
name|HTTP_OK
operator|||
name|status
operator|>=
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
operator|)
decl_stmt|;
switch|switch
condition|(
name|operationType
condition|)
block|{
case|case
name|AppendBlock
case|:
case|case
name|PutBlock
case|:
case|case
name|PutPage
case|:
name|contentLength
operator|=
name|BlobOperationDescriptor
operator|.
name|getContentLengthIfKnown
argument_list|(
name|conn
argument_list|,
name|operationType
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentLength
operator|>
literal|0
condition|)
block|{
name|singleton
operator|.
name|writeThrottler
operator|.
name|addBytesTransferred
argument_list|(
name|contentLength
argument_list|,
name|isFailedOperation
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|GetBlob
case|:
name|contentLength
operator|=
name|BlobOperationDescriptor
operator|.
name|getContentLengthIfKnown
argument_list|(
name|conn
argument_list|,
name|operationType
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentLength
operator|>
literal|0
condition|)
block|{
name|singleton
operator|.
name|readThrottler
operator|.
name|addBytesTransferred
argument_list|(
name|contentLength
argument_list|,
name|isFailedOperation
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
break|break;
block|}
block|}
comment|/**    * Called when a network error occurs before the HTTP status and response    * headers are received. Client-side throttling uses this to collect metrics.    *    * @param event The connection, operation, and request state.    */
DECL|method|errorReceivingResponse (ErrorReceivingResponseEvent event)
specifier|public
specifier|static
name|void
name|errorReceivingResponse
parameter_list|(
name|ErrorReceivingResponseEvent
name|event
parameter_list|)
block|{
name|updateMetrics
argument_list|(
operator|(
name|HttpURLConnection
operator|)
name|event
operator|.
name|getConnectionObject
argument_list|()
argument_list|,
name|event
operator|.
name|getRequestResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called before the Azure Storage SDK sends a request. Client-side throttling    * uses this to suspend the request, if necessary, to minimize errors and    * maximize throughput.    *    * @param event The connection, operation, and request state.    */
DECL|method|sendingRequest (SendingRequestEvent event)
specifier|public
specifier|static
name|void
name|sendingRequest
parameter_list|(
name|SendingRequestEvent
name|event
parameter_list|)
block|{
name|BlobOperationDescriptor
operator|.
name|OperationType
name|operationType
init|=
name|BlobOperationDescriptor
operator|.
name|getOperationType
argument_list|(
operator|(
name|HttpURLConnection
operator|)
name|event
operator|.
name|getConnectionObject
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operationType
condition|)
block|{
case|case
name|GetBlob
case|:
name|singleton
operator|.
name|readThrottler
operator|.
name|suspendIfNecessary
argument_list|()
expr_stmt|;
break|break;
case|case
name|AppendBlock
case|:
case|case
name|PutBlock
case|:
case|case
name|PutPage
case|:
name|singleton
operator|.
name|writeThrottler
operator|.
name|suspendIfNecessary
argument_list|()
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
comment|/**    * Called after the Azure Storage SDK receives a response. Client-side    * throttling uses this to collect metrics.    *    * @param event The connection, operation, and request state.    */
DECL|method|responseReceived (ResponseReceivedEvent event)
specifier|public
specifier|static
name|void
name|responseReceived
parameter_list|(
name|ResponseReceivedEvent
name|event
parameter_list|)
block|{
name|updateMetrics
argument_list|(
operator|(
name|HttpURLConnection
operator|)
name|event
operator|.
name|getConnectionObject
argument_list|()
argument_list|,
name|event
operator|.
name|getRequestResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * The ErrorReceivingResponseEvent is fired when the Azure Storage SDK    * encounters a network error before the HTTP status and response headers are    * received.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErrorReceivingResponseEventHandler
specifier|static
class|class
name|ErrorReceivingResponseEventHandler
extends|extends
name|StorageEvent
argument_list|<
name|ErrorReceivingResponseEvent
argument_list|>
block|{
comment|/**      * Called when a network error occurs before the HTTP status and response      * headers are received.  Client-side throttling uses this to collect      * metrics.      *      * @param event The connection, operation, and request state.      */
annotation|@
name|Override
DECL|method|eventOccurred (ErrorReceivingResponseEvent event)
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|ErrorReceivingResponseEvent
name|event
parameter_list|)
block|{
name|singleton
operator|.
name|errorReceivingResponse
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The SendingRequestEvent is fired before the Azure Storage SDK sends a    * request.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SendingRequestEventHandler
specifier|static
class|class
name|SendingRequestEventHandler
extends|extends
name|StorageEvent
argument_list|<
name|SendingRequestEvent
argument_list|>
block|{
comment|/**      * Called before the Azure Storage SDK sends a request. Client-side      * throttling uses this to suspend the request, if necessary, to minimize      * errors and maximize throughput.      *      * @param event The connection, operation, and request state.      */
annotation|@
name|Override
DECL|method|eventOccurred (SendingRequestEvent event)
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|SendingRequestEvent
name|event
parameter_list|)
block|{
name|singleton
operator|.
name|sendingRequest
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The ResponseReceivedEvent is fired after the Azure Storage SDK receives a    * response.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ResponseReceivedEventHandler
specifier|static
class|class
name|ResponseReceivedEventHandler
extends|extends
name|StorageEvent
argument_list|<
name|ResponseReceivedEvent
argument_list|>
block|{
comment|/**      * Called after the Azure Storage SDK receives a response. Client-side      * throttling uses this      * to collect metrics.      *      * @param event The connection, operation, and request state.      */
annotation|@
name|Override
DECL|method|eventOccurred (ResponseReceivedEvent event)
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|ResponseReceivedEvent
name|event
parameter_list|)
block|{
name|singleton
operator|.
name|responseReceived
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

