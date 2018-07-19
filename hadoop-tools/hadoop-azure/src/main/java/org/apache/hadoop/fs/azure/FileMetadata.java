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
name|fs
operator|.
name|FileStatus
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
name|Path
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

begin_comment
comment|/**  *<p>  * Holds basic metadata for a file stored in a {@link NativeFileSystemStore}.  *</p>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileMetadata
class|class
name|FileMetadata
extends|extends
name|FileStatus
block|{
comment|// this is not final so that it can be cleared to save memory when not needed.
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|blobMaterialization
specifier|private
specifier|final
name|BlobMaterialization
name|blobMaterialization
decl_stmt|;
comment|/**    * Constructs a FileMetadata object for a file.    *     * @param key    *          The key (path) to the file.    * @param length    *          The length in bytes of the file.    * @param lastModified    *          The last modified date (milliseconds since January 1, 1970 UTC.)    * @param permissionStatus    *          The permission for the file.    * @param blockSize    *          The Hadoop file block size.    */
DECL|method|FileMetadata (String key, long length, long lastModified, PermissionStatus permissionStatus, final long blockSize)
specifier|public
name|FileMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|lastModified
parameter_list|,
name|PermissionStatus
name|permissionStatus
parameter_list|,
specifier|final
name|long
name|blockSize
parameter_list|)
block|{
name|super
argument_list|(
name|length
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
name|blockSize
argument_list|,
name|lastModified
argument_list|,
literal|0
argument_list|,
name|permissionStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|permissionStatus
operator|.
name|getUserName
argument_list|()
argument_list|,
name|permissionStatus
operator|.
name|getGroupName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
comment|// Files are never implicit.
name|this
operator|.
name|blobMaterialization
operator|=
name|BlobMaterialization
operator|.
name|Explicit
expr_stmt|;
block|}
comment|/**    * Constructs a FileMetadata object for a directory.    *     * @param key    *          The key (path) to the directory.    * @param lastModified    *          The last modified date (milliseconds since January 1, 1970 UTC.)    * @param permissionStatus    *          The permission for the directory.    * @param blobMaterialization    *          Whether this is an implicit (no real blob backing it) or explicit    *          directory.    * @param blockSize    *          The Hadoop file block size.    */
DECL|method|FileMetadata (String key, long lastModified, PermissionStatus permissionStatus, BlobMaterialization blobMaterialization, final long blockSize)
specifier|public
name|FileMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|lastModified
parameter_list|,
name|PermissionStatus
name|permissionStatus
parameter_list|,
name|BlobMaterialization
name|blobMaterialization
parameter_list|,
specifier|final
name|long
name|blockSize
parameter_list|)
block|{
name|super
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
name|blockSize
argument_list|,
name|lastModified
argument_list|,
literal|0
argument_list|,
name|permissionStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|permissionStatus
operator|.
name|getUserName
argument_list|()
argument_list|,
name|permissionStatus
operator|.
name|getGroupName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|blobMaterialization
operator|=
name|blobMaterialization
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
name|Path
name|p
init|=
name|super
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
comment|// Don't store this yet to reduce memory usage, as it will
comment|// stay in the Eden Space and later we will update it
comment|// with the full canonicalized path.
name|p
operator|=
name|NativeAzureFileSystem
operator|.
name|keyToPath
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
comment|/**    * Returns the Azure storage key for the file.  Used internally by the framework.    *    * @return The key for the file.    */
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**    * Indicates whether this is an implicit directory (no real blob backing it)    * or an explicit one.    *     * @return Implicit if this is an implicit directory, or Explicit if it's an    *         explicit directory or a file.    */
DECL|method|getBlobMaterialization ()
specifier|public
name|BlobMaterialization
name|getBlobMaterialization
parameter_list|()
block|{
return|return
name|blobMaterialization
return|;
block|}
DECL|method|removeKey ()
name|void
name|removeKey
parameter_list|()
block|{
name|key
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

