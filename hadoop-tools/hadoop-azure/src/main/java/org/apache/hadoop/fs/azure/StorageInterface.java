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
name|ArrayList
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|AccessCondition
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
name|CloudStorageAccount
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
name|RetryPolicyFactory
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
name|StorageException
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
name|blob
operator|.
name|BlobListingDetails
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
name|blob
operator|.
name|BlobProperties
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
name|blob
operator|.
name|BlockEntry
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
name|blob
operator|.
name|BlockListingFilter
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
name|blob
operator|.
name|BlobRequestOptions
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
name|blob
operator|.
name|CloudBlob
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
name|blob
operator|.
name|CopyState
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
name|blob
operator|.
name|ListBlobItem
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
name|blob
operator|.
name|PageRange
import|;
end_import

begin_comment
comment|/**  * This is a very thin layer over the methods exposed by the Windows Azure  * Storage SDK that we need for WASB implementation. This base class has a real  * implementation that just simply redirects to the SDK, and a memory-backed one  * that's used for unit tests.  *  * IMPORTANT: all the methods here must remain very simple redirects since code  * written here can't be properly unit tested.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageInterface
specifier|abstract
class|class
name|StorageInterface
block|{
comment|/**    * Sets the timeout to use when making requests to the storage service.    *<p>    * The server timeout interval begins at the time that the complete request    * has been received by the service, and the server begins processing the    * response. If the timeout interval elapses before the response is returned    * to the client, the operation times out. The timeout interval resets with    * each retry, if the request is retried.    *     * The default timeout interval for a request made via the service client is    * 90 seconds. You can change this value on the service client by setting this    * property, so that all subsequent requests made via the service client will    * use the new timeout interval. You can also change this value for an    * individual request, by setting the    * {@link com.microsoft.azure.storage.RequestOptions#timeoutIntervalInMs}    * property.    *     * If you are downloading a large blob, you should increase the value of the    * timeout beyond the default value.    *     * @param timeoutInMs    *          The timeout, in milliseconds, to use when making requests to the    *          storage service.    */
DECL|method|setTimeoutInMs (int timeoutInMs)
specifier|public
specifier|abstract
name|void
name|setTimeoutInMs
parameter_list|(
name|int
name|timeoutInMs
parameter_list|)
function_decl|;
comment|/**    * Sets the RetryPolicyFactory object to use when making service requests.    *     * @param retryPolicyFactory    *          the RetryPolicyFactory object to use when making service requests.    */
DECL|method|setRetryPolicyFactory ( final RetryPolicyFactory retryPolicyFactory)
specifier|public
specifier|abstract
name|void
name|setRetryPolicyFactory
parameter_list|(
specifier|final
name|RetryPolicyFactory
name|retryPolicyFactory
parameter_list|)
function_decl|;
comment|/**    * Creates a new Blob service client.    *    * @param account cloud storage account.    */
DECL|method|createBlobClient (CloudStorageAccount account)
specifier|public
specifier|abstract
name|void
name|createBlobClient
parameter_list|(
name|CloudStorageAccount
name|account
parameter_list|)
function_decl|;
comment|/**    * Creates an instance of the<code>CloudBlobClient</code> class using the    * specified Blob service endpoint.    *     * @param baseUri    *          A<code>java.net.URI</code> object that represents the Blob    *          service endpoint used to create the client.    */
DECL|method|createBlobClient (URI baseUri)
specifier|public
specifier|abstract
name|void
name|createBlobClient
parameter_list|(
name|URI
name|baseUri
parameter_list|)
function_decl|;
comment|/**    * Creates an instance of the<code>CloudBlobClient</code> class using the    * specified Blob service endpoint and account credentials.    *     * @param baseUri    *          A<code>java.net.URI</code> object that represents the Blob    *          service endpoint used to create the client.    * @param credentials    *          A {@link StorageCredentials} object that represents the account    *          credentials.    */
DECL|method|createBlobClient (URI baseUri, StorageCredentials credentials)
specifier|public
specifier|abstract
name|void
name|createBlobClient
parameter_list|(
name|URI
name|baseUri
parameter_list|,
name|StorageCredentials
name|credentials
parameter_list|)
function_decl|;
comment|/**    * Returns the credentials for the Blob service, as configured for the storage    * account.    *     * @return A {@link StorageCredentials} object that represents the credentials    *         for this storage account.    */
DECL|method|getCredentials ()
specifier|public
specifier|abstract
name|StorageCredentials
name|getCredentials
parameter_list|()
function_decl|;
comment|/**    * Returns a reference to a {@link CloudBlobContainerWrapper} object that    * represents the cloud blob container for the specified address.    *     * @param name    *          A<code>String</code> that represents the name of the container.    * @return A {@link CloudBlobContainerWrapper} object that represents a    *         reference to the cloud blob container.    *     * @throws URISyntaxException    *           If the resource URI is invalid.    * @throws StorageException    *           If a storage service error occurred.    */
DECL|method|getContainerReference (String name)
specifier|public
specifier|abstract
name|CloudBlobContainerWrapper
name|getContainerReference
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
function_decl|;
comment|/**    * A thin wrapper over the    * {@link com.microsoft.azure.storage.blob.CloudBlobDirectory} class    * that simply redirects calls to the real object except in unit tests.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CloudBlobDirectoryWrapper
specifier|public
specifier|abstract
specifier|static
class|class
name|CloudBlobDirectoryWrapper
implements|implements
name|ListBlobItem
block|{
comment|/**      * Returns the URI for this directory.      *       * @return A<code>java.net.URI</code> object that represents the URI for      *         this directory.      */
DECL|method|getUri ()
specifier|public
specifier|abstract
name|URI
name|getUri
parameter_list|()
function_decl|;
comment|/**      * Returns an enumerable collection of blob items whose names begin with the      * specified prefix, using the specified flat or hierarchical option,      * listing details options, request options, and operation context.      *       * @param prefix      *          A<code>String</code> that represents the prefix of the blob      *          name.      * @param useFlatBlobListing      *<code>true</code> to indicate that the returned list will be      *          flat;<code>false</code> to indicate that the returned list will      *          be hierarchical.      * @param listingDetails      *          A<code>java.util.EnumSet</code> object that contains      *          {@link BlobListingDetails} values that indicate whether      *          snapshots, metadata, and/or uncommitted blocks are returned.      *          Committed blocks are always returned.      * @param options      *          A {@link BlobRequestOptions} object that specifies any      *          additional options for the request. Specifying<code>null</code>      *          will use the default request options from the associated service      *          client ({@link com.microsoft.azure.storage.blob.CloudBlobClient}).      * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @return An enumerable collection of {@link ListBlobItem} objects that      *         represent the block items whose names begin with the specified      *         prefix in this directory.      *       * @throws StorageException      *           If a storage service error occurred.      * @throws URISyntaxException      *           If the resource URI is invalid.      */
DECL|method|listBlobs (String prefix, boolean useFlatBlobListing, EnumSet<BlobListingDetails> listingDetails, BlobRequestOptions options, OperationContext opContext)
specifier|public
specifier|abstract
name|Iterable
argument_list|<
name|ListBlobItem
argument_list|>
name|listBlobs
parameter_list|(
name|String
name|prefix
parameter_list|,
name|boolean
name|useFlatBlobListing
parameter_list|,
name|EnumSet
argument_list|<
name|BlobListingDetails
argument_list|>
name|listingDetails
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
function_decl|;
block|}
comment|/**    * A thin wrapper over the    * {@link com.microsoft.azure.storage.blob.CloudBlobContainer} class    * that simply redirects calls to the real object except in unit tests.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CloudBlobContainerWrapper
specifier|public
specifier|abstract
specifier|static
class|class
name|CloudBlobContainerWrapper
block|{
comment|/**      * Returns the name of the container.      *       * @return A<code>String</code> that represents the name of the container.      */
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns a value that indicates whether the container exists, using the      * specified operation context.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @return<code>true</code> if the container exists, otherwise      *<code>false</code>.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|exists (OperationContext opContext)
specifier|public
specifier|abstract
name|boolean
name|exists
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Returns the metadata for the container.      *       * @return A<code>java.util.HashMap</code> object that represents the      *         metadata for the container.      */
DECL|method|getMetadata ()
specifier|public
specifier|abstract
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
function_decl|;
comment|/**      * Sets the metadata for the container.      *       * @param metadata      *          A<code>java.util.HashMap</code> object that represents the      *          metadata being assigned to the container.      */
DECL|method|setMetadata (HashMap<String, String> metadata)
specifier|public
specifier|abstract
name|void
name|setMetadata
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
function_decl|;
comment|/**      * Downloads the container's attributes, which consist of metadata and      * properties, using the specified operation context.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|downloadAttributes (OperationContext opContext)
specifier|public
specifier|abstract
name|void
name|downloadAttributes
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Uploads the container's metadata using the specified operation context.      *      * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *      * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|uploadMetadata (OperationContext opContext)
specifier|public
specifier|abstract
name|void
name|uploadMetadata
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Creates the container using the specified operation context.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|create (OperationContext opContext)
specifier|public
specifier|abstract
name|void
name|create
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Returns a wrapper for a CloudBlobDirectory.      *       * @param relativePath      *          A<code>String</code> that represents the name of the directory,      *          relative to the container      *       * @throws StorageException      *           If a storage service error occurred.      *       * @throws URISyntaxException      *           If URI syntax exception occurred.      */
DECL|method|getDirectoryReference ( String relativePath)
specifier|public
specifier|abstract
name|CloudBlobDirectoryWrapper
name|getDirectoryReference
parameter_list|(
name|String
name|relativePath
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
function_decl|;
comment|/**      * Returns a wrapper for a CloudBlockBlob.      *       * @param relativePath      *          A<code>String</code> that represents the name of the blob,      *          relative to the container      *       * @throws StorageException      *           If a storage service error occurred.      *       * @throws URISyntaxException      *           If URI syntax exception occurred.      */
DECL|method|getBlockBlobReference ( String relativePath)
specifier|public
specifier|abstract
name|CloudBlobWrapper
name|getBlockBlobReference
parameter_list|(
name|String
name|relativePath
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
function_decl|;
comment|/**      * Returns a wrapper for a CloudPageBlob.      *      * @param relativePath      *            A<code>String</code> that represents the name of the blob, relative to the container       *      * @throws StorageException      *             If a storage service error occurred.      *       * @throws URISyntaxException      *             If URI syntax exception occurred.                  */
DECL|method|getPageBlobReference (String relativePath)
specifier|public
specifier|abstract
name|CloudBlobWrapper
name|getPageBlobReference
parameter_list|(
name|String
name|relativePath
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
function_decl|;
block|}
comment|/**    * A thin wrapper over the {@link CloudBlob} class that simply redirects calls    * to the real object except in unit tests.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|CloudBlobWrapper
specifier|public
interface|interface
name|CloudBlobWrapper
extends|extends
name|ListBlobItem
block|{
comment|/**      * Returns the URI for this blob.      *       * @return A<code>java.net.URI</code> object that represents the URI for      *         the blob.      */
DECL|method|getUri ()
name|URI
name|getUri
parameter_list|()
function_decl|;
comment|/**      * Returns the metadata for the blob.      *       * @return A<code>java.util.HashMap</code> object that represents the      *         metadata for the blob.      */
DECL|method|getMetadata ()
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
function_decl|;
comment|/**      * Sets the metadata for the blob.      *       * @param metadata      *          A<code>java.util.HashMap</code> object that contains the      *          metadata being assigned to the blob.      */
DECL|method|setMetadata (HashMap<String, String> metadata)
name|void
name|setMetadata
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
function_decl|;
comment|/**      * Copies an existing blob's contents, properties, and metadata to this instance of the<code>CloudBlob</code>      * class, using the specified operation context.      *      * @param sourceBlob      *            A<code>CloudBlob</code> object that represents the source blob to copy.      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *      * @throws StorageException      *             If a storage service error occurred.      * @throws URISyntaxException      *      */
DECL|method|startCopyFromBlob (CloudBlobWrapper sourceBlob, BlobRequestOptions options, OperationContext opContext)
specifier|public
specifier|abstract
name|void
name|startCopyFromBlob
parameter_list|(
name|CloudBlobWrapper
name|sourceBlob
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
throws|,
name|URISyntaxException
function_decl|;
comment|/**      * Returns the blob's copy state.      *       * @return A {@link CopyState} object that represents the copy state of the      *         blob.      */
DECL|method|getCopyState ()
name|CopyState
name|getCopyState
parameter_list|()
function_decl|;
comment|/**      * Downloads a range of bytes from the blob to the given byte buffer, using the specified request options and      * operation context.      *      * @param offset      *            The byte offset to use as the starting point for the source.      * @param length      *            The number of bytes to read.      * @param buffer      *            The byte buffer, as an array of bytes, to which the blob bytes are downloaded.      * @param bufferOffset      *            The byte offset to use as the starting point for the target.      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *      * @throws StorageException      *             If a storage service error occurred.      */
DECL|method|downloadRange (final long offset, final long length, final OutputStream outStream, final BlobRequestOptions options, final OperationContext opContext)
name|void
name|downloadRange
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
specifier|final
name|OutputStream
name|outStream
parameter_list|,
specifier|final
name|BlobRequestOptions
name|options
parameter_list|,
specifier|final
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
throws|,
name|IOException
function_decl|;
comment|/**      * Deletes the blob using the specified operation context.      *<p>      * A blob that has snapshots cannot be deleted unless the snapshots are also      * deleted. If a blob has snapshots, use the      * {@link DeleteSnapshotsOption#DELETE_SNAPSHOTS_ONLY} or      * {@link DeleteSnapshotsOption#INCLUDE_SNAPSHOTS} value in the      *<code>deleteSnapshotsOption</code> parameter to specify how the snapshots      * should be handled when the blob is deleted.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|delete (OperationContext opContext, SelfRenewingLease lease)
name|void
name|delete
parameter_list|(
name|OperationContext
name|opContext
parameter_list|,
name|SelfRenewingLease
name|lease
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Checks to see if the blob exists, using the specified operation context.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @return<code>true</code> if the blob exists, otherwise      *<code>false</code>.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|exists (OperationContext opContext)
name|boolean
name|exists
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Populates a blob's properties and metadata using the specified operation      * context.      *<p>      * This method populates the blob's system properties and user-defined      * metadata. Before reading a blob's properties or metadata, call this      * method or its overload to retrieve the latest values for the blob's      * properties and metadata from the Windows Azure storage service.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|downloadAttributes (OperationContext opContext)
name|void
name|downloadAttributes
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Returns the blob's properties.      *       * @return A {@link BlobProperties} object that represents the properties of      *         the blob.      */
DECL|method|getProperties ()
name|BlobProperties
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Opens a blob input stream to download the blob using the specified      * operation context.      *<p>      * Use {@link CloudBlobClient#setStreamMinimumReadSizeInBytes} to configure      * the read size.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @return An<code>InputStream</code> object that represents the stream to      *         use for reading from the blob.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|openInputStream (BlobRequestOptions options, OperationContext opContext)
name|InputStream
name|openInputStream
parameter_list|(
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Uploads the blob's metadata to the storage service using the specified      * lease ID, request options, and operation context.      *       * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *       * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|uploadMetadata (OperationContext opContext)
name|void
name|uploadMetadata
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Uploads the blob's metadata to the storage service using the specified      * lease ID, request options, and operation context.      *      * @param accessCondition      *           A {@link AccessCondition} object that represents the access conditions for the blob.      *      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      *      * @param opContext      *          An {@link OperationContext} object that represents the context      *          for the current operation. This object is used to track requests      *          to the storage service, and to provide additional runtime      *          information about the operation.      *      * @throws StorageException      *           If a storage service error occurred.      */
DECL|method|uploadMetadata (AccessCondition accessCondition, BlobRequestOptions options, OperationContext opContext)
name|void
name|uploadMetadata
parameter_list|(
name|AccessCondition
name|accessCondition
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
DECL|method|uploadProperties (OperationContext opContext, SelfRenewingLease lease)
name|void
name|uploadProperties
parameter_list|(
name|OperationContext
name|opContext
parameter_list|,
name|SelfRenewingLease
name|lease
parameter_list|)
throws|throws
name|StorageException
function_decl|;
DECL|method|acquireLease ()
name|SelfRenewingLease
name|acquireLease
parameter_list|()
throws|throws
name|StorageException
function_decl|;
comment|/**      * Gets the minimum read block size to use with this Blob.      *      * @return The minimum block size, in bytes, for reading from a block blob.      */
DECL|method|getStreamMinimumReadSizeInBytes ()
name|int
name|getStreamMinimumReadSizeInBytes
parameter_list|()
function_decl|;
comment|/**      * Sets the minimum read block size to use with this Blob.      *      * @param minimumReadSizeBytes      *          The maximum block size, in bytes, for reading from a block blob      *          while using a {@link BlobInputStream} object, ranging from 512      *          bytes to 64 MB, inclusive.      */
DECL|method|setStreamMinimumReadSizeInBytes ( int minimumReadSizeBytes)
name|void
name|setStreamMinimumReadSizeInBytes
parameter_list|(
name|int
name|minimumReadSizeBytes
parameter_list|)
function_decl|;
comment|/**      * Sets the write block size to use with this Blob.      *       * @param writeBlockSizeBytes      *          The maximum block size, in bytes, for writing to a block blob      *          while using a {@link BlobOutputStream} object, ranging from 1 MB      *          to 4 MB, inclusive.      *       * @throws IllegalArgumentException      *           If<code>writeBlockSizeInBytes</code> is less than 1 MB or      *           greater than 4 MB.      */
DECL|method|setWriteBlockSizeInBytes (int writeBlockSizeBytes)
name|void
name|setWriteBlockSizeInBytes
parameter_list|(
name|int
name|writeBlockSizeBytes
parameter_list|)
function_decl|;
DECL|method|getBlob ()
name|CloudBlob
name|getBlob
parameter_list|()
function_decl|;
block|}
comment|/**    * A thin wrapper over the    * {@link com.microsoft.azure.storage.blob.CloudBlockBlob} class    * that simply redirects calls to the real object except in unit tests.    */
DECL|interface|CloudBlockBlobWrapper
specifier|public
specifier|abstract
interface|interface
name|CloudBlockBlobWrapper
extends|extends
name|CloudBlobWrapper
block|{
comment|/**      * Creates and opens an output stream to write data to the block blob using the specified       * operation context.      *       * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *       * @return A {@link BlobOutputStream} object used to write data to the blob.      *       * @throws StorageException      *             If a storage service error occurred.      */
DECL|method|openOutputStream ( BlobRequestOptions options, OperationContext opContext)
name|OutputStream
name|openOutputStream
parameter_list|(
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      *      * @param filter    A {@link BlockListingFilter} value that specifies whether to download      *                  committed blocks, uncommitted blocks, or all blocks.      * @param options   A {@link BlobRequestOptions} object that specifies any additional options for      *                  the request. Specifying null will use the default request options from      *                  the associated service client ( CloudBlobClient).      * @param opContext An {@link OperationContext} object that represents the context for the current      *                  operation. This object is used to track requests to the storage service,      *                  and to provide additional runtime information about the operation.      * @return          An ArrayList object of {@link BlockEntry} objects that represent the list      *                  block items downloaded from the block blob.      * @throws IOException  If an I/O error occurred.      * @throws StorageException If a storage service error occurred.      */
DECL|method|downloadBlockList (BlockListingFilter filter, BlobRequestOptions options, OperationContext opContext)
name|List
argument_list|<
name|BlockEntry
argument_list|>
name|downloadBlockList
parameter_list|(
name|BlockListingFilter
name|filter
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|StorageException
function_decl|;
comment|/**      *      * @param blockId      A String that represents the Base-64 encoded block ID. Note for a given blob      *                     the length of all Block IDs must be identical.      * @param accessCondition An {@link AccessCondition} object that represents the access conditions for the blob.      * @param sourceStream An {@link InputStream} object that represents the input stream to write to the      *                     block blob.      * @param length       A long which represents the length, in bytes, of the stream data,      *                     or -1 if unknown.      * @param options      A {@link BlobRequestOptions} object that specifies any additional options for the      *                     request. Specifying null will use the default request options from the      *                     associated service client ( CloudBlobClient).      * @param opContext    An {@link OperationContext} object that represents the context for the current operation.      *                     This object is used to track requests to the storage service, and to provide      *                     additional runtime information about the operation.      * @throws IOException  If an I/O error occurred.      * @throws StorageException If a storage service error occurred.      */
DECL|method|uploadBlock (String blockId, AccessCondition accessCondition, InputStream sourceStream, long length, BlobRequestOptions options, OperationContext opContext)
name|void
name|uploadBlock
parameter_list|(
name|String
name|blockId
parameter_list|,
name|AccessCondition
name|accessCondition
parameter_list|,
name|InputStream
name|sourceStream
parameter_list|,
name|long
name|length
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|StorageException
function_decl|;
comment|/**      *      * @param blockList       An enumerable collection of {@link BlockEntry} objects that represents the list      *                        block items being committed. The size field is ignored.      * @param accessCondition An {@link AccessCondition} object that represents the access conditions for the blob.      * @param options         A {@link BlobRequestOptions} object that specifies any additional options for the      *                        request. Specifying null will use the default request options from the associated      *                        service client ( CloudBlobClient).      * @param opContext       An {@link OperationContext} object that represents the context for the current operation.      *                        This object is used to track requests to the storage service, and to provide additional      *                        runtime information about the operation.      * @throws IOException      If an I/O error occurred.      * @throws StorageException If a storage service error occurred.      */
DECL|method|commitBlockList (List<BlockEntry> blockList, AccessCondition accessCondition, BlobRequestOptions options, OperationContext opContext)
name|void
name|commitBlockList
parameter_list|(
name|List
argument_list|<
name|BlockEntry
argument_list|>
name|blockList
parameter_list|,
name|AccessCondition
name|accessCondition
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|StorageException
function_decl|;
block|}
comment|/**    * A thin wrapper over the    * {@link com.microsoft.azure.storage.blob.CloudPageBlob}    * class that simply redirects calls to the real object except in unit tests.    */
DECL|interface|CloudPageBlobWrapper
specifier|public
specifier|abstract
interface|interface
name|CloudPageBlobWrapper
extends|extends
name|CloudBlobWrapper
block|{
comment|/**      * Creates a page blob using the specified request options and operation context.      *      * @param length      *            The size, in bytes, of the page blob.      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *      * @throws IllegalArgumentException      *             If the length is not a multiple of 512.      *      * @throws StorageException      *             If a storage service error occurred.      */
DECL|method|create (final long length, BlobRequestOptions options, OperationContext opContext)
name|void
name|create
parameter_list|(
specifier|final
name|long
name|length
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
comment|/**      * Uploads a range of contiguous pages, up to 4 MB in size, at the specified offset in the page blob, using the      * specified lease ID, request options, and operation context.      *       * @param sourceStream      *            An<code>InputStream</code> object that represents the input stream to write to the page blob.      * @param offset      *            The offset, in number of bytes, at which to begin writing the data. This value must be a multiple of      *            512.      * @param length      *            The length, in bytes, of the data to write. This value must be a multiple of 512.      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *       * @throws IllegalArgumentException      *             If the offset or length are not multiples of 512, or if the length is greater than 4 MB.      * @throws IOException      *             If an I/O exception occurred.      * @throws StorageException      *             If a storage service error occurred.      */
DECL|method|uploadPages (final InputStream sourceStream, final long offset, final long length, BlobRequestOptions options, OperationContext opContext)
name|void
name|uploadPages
parameter_list|(
specifier|final
name|InputStream
name|sourceStream
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
throws|,
name|IOException
function_decl|;
comment|/**      * Returns a collection of page ranges and their starting and ending byte offsets using the specified request      * options and operation context.      *      * @param options      *            A {@link BlobRequestOptions} object that specifies any additional options for the request. Specifying      *<code>null</code> will use the default request options from the associated service client (      *            {@link CloudBlobClient}).      * @param opContext      *            An {@link OperationContext} object that represents the context for the current operation. This object      *            is used to track requests to the storage service, and to provide additional runtime information about      *            the operation.      *      * @return An<code>ArrayList</code> object that represents the set of page ranges and their starting and ending      *         byte offsets.      *      * @throws StorageException      *             If a storage service error occurred.      */
DECL|method|downloadPageRanges (BlobRequestOptions options, OperationContext opContext)
name|ArrayList
argument_list|<
name|PageRange
argument_list|>
name|downloadPageRanges
parameter_list|(
name|BlobRequestOptions
name|options
parameter_list|,
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
DECL|method|uploadMetadata (OperationContext opContext)
name|void
name|uploadMetadata
parameter_list|(
name|OperationContext
name|opContext
parameter_list|)
throws|throws
name|StorageException
function_decl|;
block|}
block|}
end_class

end_unit

