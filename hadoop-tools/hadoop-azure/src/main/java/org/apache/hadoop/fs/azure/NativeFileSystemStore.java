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
name|DataOutputStream
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|azure
operator|.
name|metrics
operator|.
name|AzureFileSystemInstrumentation
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
name|permission
operator|.
name|PermissionStatus
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
comment|/**  *<p>  * An abstraction for a key-based {@link File} store.  *</p>  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|NativeFileSystemStore
interface|interface
name|NativeFileSystemStore
block|{
DECL|method|initialize (URI uri, Configuration conf, AzureFileSystemInstrumentation instrumentation)
name|void
name|initialize
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|storeEmptyFolder (String key, PermissionStatus permissionStatus)
name|void
name|storeEmptyFolder
parameter_list|(
name|String
name|key
parameter_list|,
name|PermissionStatus
name|permissionStatus
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|retrieveMetadata (String key)
name|FileMetadata
name|retrieveMetadata
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieve (String key)
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|retrieve (String key, long byteRangeStart)
name|InputStream
name|retrieve
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|byteRangeStart
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|storefile (String keyEncoded, PermissionStatus permissionStatus, String key)
name|DataOutputStream
name|storefile
parameter_list|(
name|String
name|keyEncoded
parameter_list|,
name|PermissionStatus
name|permissionStatus
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|isPageBlobKey (String key)
name|boolean
name|isPageBlobKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|isAtomicRenameKey (String key)
name|boolean
name|isAtomicRenameKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Returns the file block size.  This is a fake value used for integration    * of the Azure store with Hadoop.    * @return The file block size.    */
DECL|method|getHadoopBlockSize ()
name|long
name|getHadoopBlockSize
parameter_list|()
function_decl|;
DECL|method|storeEmptyLinkFile (String key, String tempBlobKey, PermissionStatus permissionStatus)
name|void
name|storeEmptyLinkFile
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|tempBlobKey
parameter_list|,
name|PermissionStatus
name|permissionStatus
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|getLinkInFileMetadata (String key)
name|String
name|getLinkInFileMetadata
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|list (String prefix, final int maxListingCount, final int maxListingDepth)
name|FileMetadata
index|[]
name|list
parameter_list|(
name|String
name|prefix
parameter_list|,
specifier|final
name|int
name|maxListingCount
parameter_list|,
specifier|final
name|int
name|maxListingDepth
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|changePermissionStatus (String key, PermissionStatus newPermission)
name|void
name|changePermissionStatus
parameter_list|(
name|String
name|key
parameter_list|,
name|PermissionStatus
name|newPermission
parameter_list|)
throws|throws
name|AzureException
function_decl|;
comment|/**    * API to delete a blob in the back end azure storage.    * @param key - key to the blob being deleted.    * @return return true when delete is successful, false if    * blob cannot be found or delete is not possible without    * exception.    * @throws IOException Exception encountered while deleting in    * azure storage.    */
DECL|method|delete (String key)
name|boolean
name|delete
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|rename (String srcKey, String dstKey)
name|void
name|rename
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|rename (String srcKey, String dstKey, boolean acquireLease, SelfRenewingLease existingLease)
name|void
name|rename
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|,
name|boolean
name|acquireLease
parameter_list|,
name|SelfRenewingLease
name|existingLease
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|rename (String srcKey, String dstKey, boolean acquireLease, SelfRenewingLease existingLease, boolean overwriteDestination)
name|void
name|rename
parameter_list|(
name|String
name|srcKey
parameter_list|,
name|String
name|dstKey
parameter_list|,
name|boolean
name|acquireLease
parameter_list|,
name|SelfRenewingLease
name|existingLease
parameter_list|,
name|boolean
name|overwriteDestination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete all keys with the given prefix. Used for testing.    *    * @param prefix prefix of objects to be deleted.    * @throws IOException Exception encountered while deleting keys.    */
annotation|@
name|VisibleForTesting
DECL|method|purge (String prefix)
name|void
name|purge
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Diagnostic method to dump state to the console.    *    * @throws IOException Exception encountered while dumping to console.    */
DECL|method|dump ()
name|void
name|dump
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
DECL|method|updateFolderLastModifiedTime (String key, SelfRenewingLease folderLease)
name|void
name|updateFolderLastModifiedTime
parameter_list|(
name|String
name|key
parameter_list|,
name|SelfRenewingLease
name|folderLease
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|updateFolderLastModifiedTime (String key, Date lastModified, SelfRenewingLease folderLease)
name|void
name|updateFolderLastModifiedTime
parameter_list|(
name|String
name|key
parameter_list|,
name|Date
name|lastModified
parameter_list|,
name|SelfRenewingLease
name|folderLease
parameter_list|)
throws|throws
name|AzureException
function_decl|;
comment|/**    * API to delete a blob in the back end azure storage.    * @param key - key to the blob being deleted.    * @param lease - Active lease on the blob.    * @return return true when delete is successful, false if    * blob cannot be found or delete is not possible without    * exception.    * @throws IOException Exception encountered while deleting in    * azure storage.    */
DECL|method|delete (String key, SelfRenewingLease lease)
name|boolean
name|delete
parameter_list|(
name|String
name|key
parameter_list|,
name|SelfRenewingLease
name|lease
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|acquireLease (String key)
name|SelfRenewingLease
name|acquireLease
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|AzureException
function_decl|;
DECL|method|retrieveAppendStream (String key, int bufferSize)
name|DataOutputStream
name|retrieveAppendStream
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|explicitFileExists (String key)
name|boolean
name|explicitFileExists
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|AzureException
function_decl|;
block|}
end_interface

end_unit

