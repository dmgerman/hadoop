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
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
import|;
end_import

begin_comment
comment|//404
end_comment

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
import|;
end_import

begin_comment
comment|//400
end_comment

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_INTERNAL_ERROR
import|;
end_import

begin_comment
comment|//500
end_comment

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
comment|/**  * An event listener to the ResponseReceived event from Azure Storage that will  * update error metrics appropriately when it gets that event.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErrorMetricUpdater
specifier|public
specifier|final
class|class
name|ErrorMetricUpdater
extends|extends
name|StorageEvent
argument_list|<
name|ResponseReceivedEvent
argument_list|>
block|{
DECL|field|instrumentation
specifier|private
specifier|final
name|AzureFileSystemInstrumentation
name|instrumentation
decl_stmt|;
DECL|field|operationContext
specifier|private
specifier|final
name|OperationContext
name|operationContext
decl_stmt|;
DECL|method|ErrorMetricUpdater (OperationContext operationContext, AzureFileSystemInstrumentation instrumentation)
specifier|private
name|ErrorMetricUpdater
parameter_list|(
name|OperationContext
name|operationContext
parameter_list|,
name|AzureFileSystemInstrumentation
name|instrumentation
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
name|operationContext
operator|=
name|operationContext
expr_stmt|;
block|}
comment|/**    * Hooks a new listener to the given operationContext that will update the    * error metrics for the WASB file system appropriately in response to    * ResponseReceived events.    *    * @param operationContext The operationContext to hook.    * @param instrumentation The metrics source to update.    */
DECL|method|hook ( OperationContext operationContext, AzureFileSystemInstrumentation instrumentation)
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
parameter_list|)
block|{
name|ErrorMetricUpdater
name|listener
init|=
operator|new
name|ErrorMetricUpdater
argument_list|(
name|operationContext
argument_list|,
name|instrumentation
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
name|RequestResult
name|currentResult
init|=
name|operationContext
operator|.
name|getLastResult
argument_list|()
decl_stmt|;
name|int
name|statusCode
init|=
name|currentResult
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
comment|// Check if it's a client-side error: a 4xx status
comment|// We exclude 404 because it happens frequently during the normal
comment|// course of operation (each call to exists() would generate that
comment|// if it's not found).
if|if
condition|(
name|statusCode
operator|>=
name|HTTP_BAD_REQUEST
operator|&&
name|statusCode
operator|<
name|HTTP_INTERNAL_ERROR
operator|&&
name|statusCode
operator|!=
name|HTTP_NOT_FOUND
condition|)
block|{
name|instrumentation
operator|.
name|clientErrorEncountered
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|statusCode
operator|>=
name|HTTP_INTERNAL_ERROR
condition|)
block|{
comment|// It's a server error: a 5xx status. Could be an Azure Storage
comment|// bug or (more likely) throttling.
name|instrumentation
operator|.
name|serverErrorEncountered
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

