begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.file
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|request
operator|.
name|file
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
name|nio
operator|.
name|file
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
name|ozone
operator|.
name|om
operator|.
name|OMMetadataManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Base class for file requests.  */
end_comment

begin_class
DECL|class|OMFileRequest
specifier|public
specifier|final
class|class
name|OMFileRequest
block|{
DECL|method|OMFileRequest ()
specifier|private
name|OMFileRequest
parameter_list|()
block|{   }
comment|/**    * Verify any files exist in the given path in the specified volume/bucket.    * @param omMetadataManager    * @param volumeName    * @param bucketName    * @param keyPath    * @return true - if file exist in the given path, else false.    * @throws IOException    */
DECL|method|verifyFilesInPath ( @onnull OMMetadataManager omMetadataManager, @Nonnull String volumeName, @Nonnull String bucketName, @Nonnull String keyName, @Nonnull Path keyPath)
specifier|public
specifier|static
name|OMDirectoryResult
name|verifyFilesInPath
parameter_list|(
annotation|@
name|Nonnull
name|OMMetadataManager
name|omMetadataManager
parameter_list|,
annotation|@
name|Nonnull
name|String
name|volumeName
parameter_list|,
annotation|@
name|Nonnull
name|String
name|bucketName
parameter_list|,
annotation|@
name|Nonnull
name|String
name|keyName
parameter_list|,
annotation|@
name|Nonnull
name|Path
name|keyPath
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileNameFromDetails
init|=
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|String
name|dirNameFromDetails
init|=
name|omMetadataManager
operator|.
name|getOzoneDirKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
while|while
condition|(
name|keyPath
operator|!=
literal|null
condition|)
block|{
name|String
name|pathName
init|=
name|keyPath
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|dbKeyName
init|=
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|pathName
argument_list|)
decl_stmt|;
name|String
name|dbDirKeyName
init|=
name|omMetadataManager
operator|.
name|getOzoneDirKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|pathName
argument_list|)
decl_stmt|;
if|if
condition|(
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbKeyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Found a file in the given path.
comment|// Check if this is actual file or a file in the given path
if|if
condition|(
name|dbKeyName
operator|.
name|equals
argument_list|(
name|fileNameFromDetails
argument_list|)
condition|)
block|{
return|return
name|OMDirectoryResult
operator|.
name|FILE_EXISTS
return|;
block|}
else|else
block|{
return|return
name|OMDirectoryResult
operator|.
name|FILE_EXISTS_IN_GIVENPATH
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbDirKeyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Found a directory in the given path.
comment|// Check if this is actual directory or a directory in the given path
if|if
condition|(
name|dbDirKeyName
operator|.
name|equals
argument_list|(
name|dirNameFromDetails
argument_list|)
condition|)
block|{
return|return
name|OMDirectoryResult
operator|.
name|DIRECTORY_EXISTS
return|;
block|}
else|else
block|{
return|return
name|OMDirectoryResult
operator|.
name|DIRECTORY_EXISTS_IN_GIVENPATH
return|;
block|}
block|}
name|keyPath
operator|=
name|keyPath
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
comment|// Found no files/ directories in the given path.
return|return
name|OMDirectoryResult
operator|.
name|NONE
return|;
block|}
comment|/**    * Return codes used by verifyFilesInPath method.    */
DECL|enum|OMDirectoryResult
enum|enum
name|OMDirectoryResult
block|{
comment|// In below examples path is assumed as "a/b/c" in volume volume1 and
comment|// bucket b1.
comment|// When a directory exists in given path.
comment|// If we have a directory with name "a/b" we return this enum value.
DECL|enumConstant|DIRECTORY_EXISTS_IN_GIVENPATH
name|DIRECTORY_EXISTS_IN_GIVENPATH
block|,
comment|// When a file exists in given path.
comment|// If we have a file with name "a/b" we return this enum value.
DECL|enumConstant|FILE_EXISTS_IN_GIVENPATH
name|FILE_EXISTS_IN_GIVENPATH
block|,
comment|// When file already exists with the given path.
comment|// If we have a file with name "a/b/c" we return this enum value.
DECL|enumConstant|FILE_EXISTS
name|FILE_EXISTS
block|,
comment|// When directory exists with the given path.
comment|// If we have a file with name "a/b/c" we return this enum value.
DECL|enumConstant|DIRECTORY_EXISTS
name|DIRECTORY_EXISTS
block|,
comment|// If no file/directory exists with the given path.
comment|// If we don't have any file/directory name with "a/b/c" or any
comment|// sub-directory or file name from the given path we return this enum value.
DECL|enumConstant|NONE
name|NONE
block|}
block|}
end_class

end_unit

