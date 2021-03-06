begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.metrics
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
operator|.
name|metrics
package|;
end_package

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
name|classification
operator|.
name|InterfaceAudience
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
name|Constants
operator|.
name|HeaderConstants
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
name|StorageEvent
import|;
end_import

begin_comment
comment|/**  * An event listener to the ResponseReceived event from Azure Storage that will  * update metrics appropriately when it gets that event.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ResponseReceivedMetricUpdater
specifier|public
specifier|final
class|class
name|ResponseReceivedMetricUpdater
extends|extends
name|StorageEvent
argument_list|<
name|ResponseReceivedEvent
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ResponseReceivedMetricUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|instrumentation
specifier|private
specifier|final
name|AzureFileSystemInstrumentation
name|instrumentation
decl_stmt|;
DECL|field|blockUploadGaugeUpdater
specifier|private
specifier|final
name|BandwidthGaugeUpdater
name|blockUploadGaugeUpdater
decl_stmt|;
DECL|method|ResponseReceivedMetricUpdater (OperationContext operationContext, AzureFileSystemInstrumentation instrumentation, BandwidthGaugeUpdater blockUploadGaugeUpdater)
specifier|private
name|ResponseReceivedMetricUpdater
parameter_list|(
name|OperationContext
name|operationContext
parameter_list|,
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|,
name|BandwidthGaugeUpdater
name|blockUploadGaugeUpdater
parameter_list|)
block|{
name|this
operator|.
name|instrumentation
operator|=
name|instrumentation
expr_stmt|;
name|this
operator|.
name|blockUploadGaugeUpdater
operator|=
name|blockUploadGaugeUpdater
expr_stmt|;
block|}
comment|/**    * Hooks a new listener to the given operationContext that will update the    * metrics for the WASB file system appropriately in response to    * ResponseReceived events.    *    * @param operationContext The operationContext to hook.    * @param instrumentation The metrics source to update.    * @param blockUploadGaugeUpdater The blockUploadGaugeUpdater to use.    */
DECL|method|hook ( OperationContext operationContext, AzureFileSystemInstrumentation instrumentation, BandwidthGaugeUpdater blockUploadGaugeUpdater)
specifier|public
specifier|static
name|void
name|hook
parameter_list|(
name|OperationContext
name|operationContext
parameter_list|,
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|,
name|BandwidthGaugeUpdater
name|blockUploadGaugeUpdater
parameter_list|)
block|{
name|ResponseReceivedMetricUpdater
name|listener
init|=
operator|new
name|ResponseReceivedMetricUpdater
argument_list|(
name|operationContext
argument_list|,
name|instrumentation
argument_list|,
name|blockUploadGaugeUpdater
argument_list|)
decl_stmt|;
name|operationContext
operator|.
name|getResponseReceivedEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the content length of the request in the given HTTP connection.    * @param connection The connection.    * @return The content length, or zero if not found.    */
DECL|method|getRequestContentLength (HttpURLConnection connection)
specifier|private
name|long
name|getRequestContentLength
parameter_list|(
name|HttpURLConnection
name|connection
parameter_list|)
block|{
name|String
name|lengthString
init|=
name|connection
operator|.
name|getRequestProperty
argument_list|(
name|HeaderConstants
operator|.
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|lengthString
operator|!=
literal|null
condition|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|lengthString
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Gets the content length of the response in the given HTTP connection.    * @param connection The connection.    * @return The content length.    */
DECL|method|getResponseContentLength (HttpURLConnection connection)
specifier|private
name|long
name|getResponseContentLength
parameter_list|(
name|HttpURLConnection
name|connection
parameter_list|)
block|{
return|return
name|connection
operator|.
name|getContentLength
argument_list|()
return|;
block|}
comment|/**    * Handle the response-received event from Azure SDK.    */
annotation|@
name|Override
DECL|method|eventOccurred (ResponseReceivedEvent eventArg)
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|ResponseReceivedEvent
name|eventArg
parameter_list|)
block|{
name|instrumentation
operator|.
name|webResponse
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|eventArg
operator|.
name|getConnectionObject
argument_list|()
operator|instanceof
name|HttpURLConnection
operator|)
condition|)
block|{
comment|// Typically this shouldn't happen, but just let it pass
return|return;
block|}
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|eventArg
operator|.
name|getConnectionObject
argument_list|()
decl_stmt|;
name|RequestResult
name|currentResult
init|=
name|eventArg
operator|.
name|getRequestResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentResult
operator|==
literal|null
condition|)
block|{
comment|// Again, typically shouldn't happen, but let it pass
return|return;
block|}
name|long
name|requestLatency
init|=
name|currentResult
operator|.
name|getStopDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|currentResult
operator|.
name|getStartDate
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentResult
operator|.
name|getStatusCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_CREATED
operator|&&
name|connection
operator|.
name|getRequestMethod
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"PUT"
argument_list|)
condition|)
block|{
comment|// If it's a PUT with an HTTP_CREATED status then it's a successful
comment|// block upload.
name|long
name|length
init|=
name|getRequestContentLength
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|blockUploadGaugeUpdater
operator|.
name|blockUploaded
argument_list|(
name|currentResult
operator|.
name|getStartDate
argument_list|()
argument_list|,
name|currentResult
operator|.
name|getStopDate
argument_list|()
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|instrumentation
operator|.
name|rawBytesUploaded
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|instrumentation
operator|.
name|blockUploaded
argument_list|(
name|requestLatency
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentResult
operator|.
name|getStatusCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_PARTIAL
operator|&&
name|connection
operator|.
name|getRequestMethod
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"GET"
argument_list|)
condition|)
block|{
comment|// If it's a GET with an HTTP_PARTIAL status then it's a successful
comment|// block download.
name|long
name|length
init|=
name|getResponseContentLength
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|blockUploadGaugeUpdater
operator|.
name|blockDownloaded
argument_list|(
name|currentResult
operator|.
name|getStartDate
argument_list|()
argument_list|,
name|currentResult
operator|.
name|getStopDate
argument_list|()
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|instrumentation
operator|.
name|rawBytesDownloaded
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|instrumentation
operator|.
name|blockDownloaded
argument_list|(
name|requestLatency
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

