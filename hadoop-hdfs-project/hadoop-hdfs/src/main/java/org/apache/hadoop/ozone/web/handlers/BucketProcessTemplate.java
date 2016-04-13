begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
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
name|handlers
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
name|fs
operator|.
name|StorageType
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
name|exceptions
operator|.
name|ErrorTable
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
name|exceptions
operator|.
name|OzoneException
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
name|headers
operator|.
name|Header
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
name|interfaces
operator|.
name|UserAuth
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
name|response
operator|.
name|BucketInfo
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
name|response
operator|.
name|ListKeys
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
name|OzoneConsts
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
name|utils
operator|.
name|OzoneUtils
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
name|slf4j
operator|.
name|MDC
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|UriInfo
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
name|nio
operator|.
name|file
operator|.
name|DirectoryNotEmptyException
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
name|FileAlreadyExistsException
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
name|NoSuchFileException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_OK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OZONE_COMPONENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OZONE_RESOURCE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OZONE_REQUEST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OZONE_USER
import|;
end_import

begin_comment
comment|/**  * This class abstracts way the repetitive tasks in  * Bucket handling code.  */
end_comment

begin_class
DECL|class|BucketProcessTemplate
specifier|public
specifier|abstract
class|class
name|BucketProcessTemplate
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
name|BucketProcessTemplate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This function serves as the common error handling function    * for all bucket related operations.    *    * @param volume - Volume Name    * @param bucket - Bucket Name    * @param request - Http Request    * @param uriInfo - Http Uri    * @param headers - Http Headers    *    * @return Response    *    * @throws OzoneException    */
DECL|method|handleCall (String volume, String bucket, Request request, UriInfo uriInfo, HttpHeaders headers)
specifier|public
name|Response
name|handleCall
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|Request
name|request
parameter_list|,
name|UriInfo
name|uriInfo
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
comment|// TODO : Add logging
name|String
name|reqID
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
name|String
name|hostName
init|=
name|OzoneUtils
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_COMPONENT
argument_list|,
literal|"ozone"
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_REQUEST
argument_list|,
name|reqID
argument_list|)
expr_stmt|;
name|UserArgs
name|userArgs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|userArgs
operator|=
operator|new
name|UserArgs
argument_list|(
name|reqID
argument_list|,
name|hostName
argument_list|,
name|request
argument_list|,
name|uriInfo
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|OzoneUtils
operator|.
name|validate
argument_list|(
name|request
argument_list|,
name|headers
argument_list|,
name|reqID
argument_list|,
name|bucket
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|OzoneUtils
operator|.
name|verifyBucketName
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|UserAuth
name|auth
init|=
name|UserHandlerBuilder
operator|.
name|getAuthHandler
argument_list|()
decl_stmt|;
name|userArgs
operator|.
name|setUserName
argument_list|(
name|auth
operator|.
name|getUser
argument_list|(
name|userArgs
argument_list|)
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_USER
argument_list|,
name|userArgs
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|BucketArgs
name|args
init|=
operator|new
name|BucketArgs
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|MDC
operator|.
name|put
argument_list|(
name|OZONE_RESOURCE
argument_list|,
name|args
operator|.
name|getResourceName
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|doProcess
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|response
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|argEx
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid bucket. ex:{}"
argument_list|,
name|argEx
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_BUCKET_NAME
argument_list|,
name|userArgs
argument_list|,
name|argEx
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|fsExp
parameter_list|)
block|{
name|handleIOException
argument_list|(
name|bucket
argument_list|,
name|reqID
argument_list|,
name|hostName
argument_list|,
name|fsExp
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Reads ACLs from headers and throws appropriate exception if needed.    *    * @param args - bucketArgs    *    * @throws OzoneException    */
DECL|method|getAclsFromHeaders (BucketArgs args, boolean parseRemoveACL)
name|void
name|getAclsFromHeaders
parameter_list|(
name|BucketArgs
name|args
parameter_list|,
name|boolean
name|parseRemoveACL
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|acls
init|=
name|getAcls
argument_list|(
name|args
argument_list|,
name|Header
operator|.
name|OZONE_ACL_REMOVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|!=
literal|null
operator|&&
operator|!
name|acls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|args
operator|.
name|removeAcls
argument_list|(
name|acls
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|!
name|parseRemoveACL
operator|)
operator|&&
name|args
operator|.
name|getRemoveAcls
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_ACL
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
literal|"Invalid Remove ACLs"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
name|acls
operator|=
name|getAcls
argument_list|(
name|args
argument_list|,
name|Header
operator|.
name|OZONE_ACL_ADD
argument_list|)
expr_stmt|;
if|if
condition|(
name|acls
operator|!=
literal|null
operator|&&
operator|!
name|acls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|args
operator|.
name|addAcls
argument_list|(
name|acls
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_ACL
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Converts FileSystem IO exceptions to OZONE exceptions.    *    * @param bucket Name of the bucket    * @param reqID Request ID    * @param hostName Machine Name    * @param fsExp Exception    *    * @throws OzoneException    */
DECL|method|handleIOException (String bucket, String reqID, String hostName, IOException fsExp)
name|void
name|handleIOException
parameter_list|(
name|String
name|bucket
parameter_list|,
name|String
name|reqID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|IOException
name|fsExp
parameter_list|)
throws|throws
name|OzoneException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"IOException: {}"
argument_list|,
name|fsExp
argument_list|)
expr_stmt|;
if|if
condition|(
name|fsExp
operator|instanceof
name|FileAlreadyExistsException
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|BUCKET_ALREADY_EXISTS
argument_list|,
name|reqID
argument_list|,
name|bucket
argument_list|,
name|hostName
argument_list|)
throw|;
block|}
if|if
condition|(
name|fsExp
operator|instanceof
name|DirectoryNotEmptyException
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|BUCKET_NOT_EMPTY
argument_list|,
name|reqID
argument_list|,
name|bucket
argument_list|,
name|hostName
argument_list|)
throw|;
block|}
if|if
condition|(
name|fsExp
operator|instanceof
name|NoSuchFileException
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_BUCKET_NAME
argument_list|,
name|reqID
argument_list|,
name|bucket
argument_list|,
name|hostName
argument_list|)
throw|;
block|}
comment|// default we don't handle this exception yet.
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|reqID
argument_list|,
name|bucket
argument_list|,
name|hostName
argument_list|)
throw|;
block|}
comment|/**    * Abstract function that gets implemented in the BucketHandler functions.    * This function will just deal with the core file system related logic    * and will rely on handleCall function for repetitive error checks    *    * @param args - parsed bucket args, name, userName, ACLs etc    *    * @return Response    *    * @throws OzoneException    * @throws IOException    */
DECL|method|doProcess (BucketArgs args)
specifier|public
specifier|abstract
name|Response
name|doProcess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
function_decl|;
comment|/**    * Returns the ACL String if available.    * This function ignores all ACLs that are not prefixed with either    * ADD or Remove    *    * @param args - BucketArgs    * @param tag - Tag for different type of acls    *    * @return List of ACLs    *    */
DECL|method|getAcls (BucketArgs args, String tag)
name|List
argument_list|<
name|String
argument_list|>
name|getAcls
parameter_list|(
name|BucketArgs
name|args
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|aclStrings
init|=
name|args
operator|.
name|getHeaders
argument_list|()
operator|.
name|getRequestHeader
argument_list|(
name|Header
operator|.
name|OZONE_ACLS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|filteredSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|aclStrings
operator|!=
literal|null
condition|)
block|{
name|filteredSet
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|aclStrings
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|tag
argument_list|)
condition|)
block|{
name|filteredSet
operator|.
name|add
argument_list|(
name|s
operator|.
name|replaceFirst
argument_list|(
name|tag
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|filteredSet
return|;
block|}
comment|/**    * Returns bucket versioning Info.    *    * @param args - BucketArgs    *    * @return - String    *    * @throws OzoneException    */
DECL|method|getVersioning (BucketArgs args)
name|OzoneConsts
operator|.
name|Versioning
name|getVersioning
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|versionStrings
init|=
name|args
operator|.
name|getHeaders
argument_list|()
operator|.
name|getRequestHeader
argument_list|(
name|Header
operator|.
name|OZONE_BUCKET_VERSIONING
argument_list|)
decl_stmt|;
if|if
condition|(
name|versionStrings
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|versionStrings
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_BUCKET_VERSION
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
literal|"Exactly one bucket version header required"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
name|String
name|version
init|=
name|versionStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|valueOf
argument_list|(
name|version
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Malformed Version. version: {}"
argument_list|,
name|version
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_BUCKET_VERSION
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns Storage Class if Available or returns Default.    *    * @param args - bucketArgs    *    * @return StorageType    *    * @throws OzoneException    */
DECL|method|getStorageType (BucketArgs args)
name|StorageType
name|getStorageType
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|storageClassString
init|=
literal|null
decl_stmt|;
try|try
block|{
name|storageClassString
operator|=
name|args
operator|.
name|getHeaders
argument_list|()
operator|.
name|getRequestHeader
argument_list|(
name|Header
operator|.
name|OZONE_STORAGE_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|storageClassString
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|storageClassString
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_STORAGE_TYPE
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
literal|"Exactly one storage class header required"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
return|return
name|StorageType
operator|.
name|valueOf
argument_list|(
name|storageClassString
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|storageClassString
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Malformed storage type. Type: {}"
argument_list|,
name|storageClassString
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_STORAGE_TYPE
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns BucketInfo response.    *    * @param args - BucketArgs    *    * @return BucketInfo    *    * @throws IOException    * @throws OzoneException    */
DECL|method|getBucketInfoResponse (BucketArgs args)
name|Response
name|getBucketInfoResponse
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|BucketInfo
name|info
init|=
name|fs
operator|.
name|getBucketInfo
argument_list|(
name|args
argument_list|)
decl_stmt|;
return|return
name|OzoneUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_OK
argument_list|,
name|info
operator|.
name|toJsonString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns list of objects in a bucket.    * @param args - ListArgs    * @return Response    * @throws IOException    * @throws OzoneException    */
DECL|method|getBucketKeysList (ListArgs args)
name|Response
name|getBucketKeysList
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|ListKeys
name|objects
init|=
name|fs
operator|.
name|listKeys
argument_list|(
name|args
argument_list|)
decl_stmt|;
return|return
name|OzoneUtils
operator|.
name|getResponse
argument_list|(
name|args
operator|.
name|getArgs
argument_list|()
argument_list|,
name|HTTP_OK
argument_list|,
name|objects
operator|.
name|toJsonString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

