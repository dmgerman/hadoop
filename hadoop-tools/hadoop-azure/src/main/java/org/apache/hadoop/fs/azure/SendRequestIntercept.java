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
comment|/**  * Manages the lifetime of binding on the operation contexts to intercept send  * request events to Azure storage and allow concurrent OOB I/Os.  */
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
comment|/**    * Hidden default constructor for SendRequestIntercept.    */
DECL|method|SendRequestIntercept ()
specifier|private
name|SendRequestIntercept
parameter_list|()
block|{   }
comment|/**    * Binds a new lister to the operation context so the WASB file system can    * appropriately intercept sends and allow concurrent OOB I/Os.  This    * by-passes the blob immutability check when reading streams.    *    * @param opContext the operation context assocated with this request.    */
DECL|method|bind (OperationContext opContext)
specifier|public
specifier|static
name|void
name|bind
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
block|{
name|opContext
operator|.
name|getSendingRequestEventHandler
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|SendRequestIntercept
argument_list|()
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
block|}
block|}
block|}
end_class

end_unit

