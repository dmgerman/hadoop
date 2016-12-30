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
name|security
operator|.
name|InvalidKeyException
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
name|core
operator|.
name|StorageCredentialsHelper
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
name|StorageCredentials
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
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_comment
comment|/**  * Manages the lifetime of binding on the operation contexts to intercept send  * request events to Azure storage.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SendRequestIntercept
specifier|public
specifier|final
class|class
name|SendRequestIntercept
extends|extends
name|StorageEvent
argument_list|<
name|SendingRequestEvent
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
name|SendRequestIntercept
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ALLOW_ALL_REQUEST_PRECONDITIONS
specifier|private
specifier|static
specifier|final
name|String
name|ALLOW_ALL_REQUEST_PRECONDITIONS
init|=
literal|"*"
decl_stmt|;
DECL|field|storageCreds
specifier|private
specifier|final
name|StorageCredentials
name|storageCreds
decl_stmt|;
DECL|field|allowConcurrentOOBIo
specifier|private
specifier|final
name|boolean
name|allowConcurrentOOBIo
decl_stmt|;
DECL|field|opContext
specifier|private
specifier|final
name|OperationContext
name|opContext
decl_stmt|;
comment|/**    * Getter returning the storage account credentials.    *     * @return storageCreds - account storage credentials.    */
DECL|method|getCredentials ()
specifier|private
name|StorageCredentials
name|getCredentials
parameter_list|()
block|{
return|return
name|storageCreds
return|;
block|}
comment|/**    * Query if out-of-band I/Os are allowed.    *     * return allowConcurrentOOBIo - true if OOB I/O is allowed, and false    * otherwise.    */
DECL|method|isOutOfBandIoAllowed ()
specifier|private
name|boolean
name|isOutOfBandIoAllowed
parameter_list|()
block|{
return|return
name|allowConcurrentOOBIo
return|;
block|}
comment|/**    * Getter returning the operation context.    *     * @return storageCreds - account storage credentials.    */
DECL|method|getOperationContext ()
specifier|private
name|OperationContext
name|getOperationContext
parameter_list|()
block|{
return|return
name|opContext
return|;
block|}
comment|/**    * Constructor for SendRequestThrottle.    *     * @param storageCreds    *          - storage account credentials for signing packets.    *     */
DECL|method|SendRequestIntercept (StorageCredentials storageCreds, boolean allowConcurrentOOBIo, OperationContext opContext)
specifier|private
name|SendRequestIntercept
parameter_list|(
name|StorageCredentials
name|storageCreds
parameter_list|,
name|boolean
name|allowConcurrentOOBIo
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
block|{
comment|// Capture the send delay callback interface.
name|this
operator|.
name|storageCreds
operator|=
name|storageCreds
expr_stmt|;
name|this
operator|.
name|allowConcurrentOOBIo
operator|=
name|allowConcurrentOOBIo
expr_stmt|;
name|this
operator|.
name|opContext
operator|=
name|opContext
expr_stmt|;
block|}
comment|/**    * Binds a new lister to the operation context so the WASB file system can    * appropriately intercept sends. By allowing concurrent OOB I/Os, we bypass    * the blob immutability check when reading streams.    *    * @param storageCreds The credential of blob storage.    * @param opContext    *          The operation context to bind to listener.    *     * @param allowConcurrentOOBIo    *          True if reads are allowed with concurrent OOB writes.    */
DECL|method|bind (StorageCredentials storageCreds, OperationContext opContext, boolean allowConcurrentOOBIo)
specifier|public
specifier|static
name|void
name|bind
parameter_list|(
name|StorageCredentials
name|storageCreds
parameter_list|,
name|OperationContext
name|opContext
parameter_list|,
name|boolean
name|allowConcurrentOOBIo
parameter_list|)
block|{
name|SendRequestIntercept
name|sendListener
init|=
operator|new
name|SendRequestIntercept
argument_list|(
name|storageCreds
argument_list|,
name|allowConcurrentOOBIo
argument_list|,
name|opContext
argument_list|)
decl_stmt|;
name|opContext
operator|.
name|getSendingRequestEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
name|sendListener
argument_list|)
expr_stmt|;
block|}
comment|/**    * Handler which processes the sending request event from Azure SDK. The    * handler simply sets reset the conditional header to make all read requests    * unconditional if reads with concurrent OOB writes are allowed.    *     * @param sendEvent    *          - send event context from Windows Azure SDK.    */
annotation|@
name|Override
DECL|method|eventOccurred (SendingRequestEvent sendEvent)
specifier|public
name|void
name|eventOccurred
parameter_list|(
name|SendingRequestEvent
name|sendEvent
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|sendEvent
operator|.
name|getConnectionObject
argument_list|()
operator|instanceof
name|HttpURLConnection
operator|)
condition|)
block|{
comment|// Pass if there is no HTTP connection associated with this send
comment|// request.
return|return;
block|}
comment|// Capture the HTTP URL connection object and get size of the payload for
comment|// the request.
name|HttpURLConnection
name|urlConnection
init|=
operator|(
name|HttpURLConnection
operator|)
name|sendEvent
operator|.
name|getConnectionObject
argument_list|()
decl_stmt|;
comment|// Determine whether this is a download request by checking that the request
comment|// method
comment|// is a "GET" operation.
if|if
condition|(
name|urlConnection
operator|.
name|getRequestMethod
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"GET"
argument_list|)
operator|&&
name|isOutOfBandIoAllowed
argument_list|()
condition|)
block|{
comment|// If concurrent reads on OOB writes are allowed, reset the if-match
comment|// condition on the conditional header.
name|urlConnection
operator|.
name|setRequestProperty
argument_list|(
name|HeaderConstants
operator|.
name|IF_MATCH
argument_list|,
name|ALLOW_ALL_REQUEST_PRECONDITIONS
argument_list|)
expr_stmt|;
comment|// In the Java AzureSDK the packet is signed before firing the
comment|// SendRequest. Setting
comment|// the conditional packet header property changes the contents of the
comment|// packet, therefore the packet has to be re-signed.
try|try
block|{
comment|// Sign the request. GET's have no payload so the content length is
comment|// zero.
name|StorageCredentialsHelper
operator|.
name|signBlobQueueAndFileRequest
argument_list|(
name|getCredentials
argument_list|()
argument_list|,
name|urlConnection
argument_list|,
operator|-
literal|1L
argument_list|,
name|getOperationContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|e
parameter_list|)
block|{
comment|// Log invalid key exception to track signing error before the send
comment|// fails.
name|String
name|errString
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Received invalid key exception when attempting sign packet."
operator|+
literal|" Cause: %s"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
comment|// Log storage exception to track signing error before the call fails.
name|String
name|errString
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Received storage exception when attempting to sign packet."
operator|+
literal|" Cause: %s"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

