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
name|ListBuckets
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
name|ListVolumes
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
name|VolumeInfo
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
comment|/**  * This class abstracts way the repetitive tasks in  * handling volume related code.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|VolumeProcessTemplate
specifier|public
specifier|abstract
class|class
name|VolumeProcessTemplate
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
name|VolumeProcessTemplate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The handle call is the common functionality for Volume    * handling code.    *    * @param volume - Name of the Volume    * @param request - request    * @param info - UriInfo    * @param headers - Http Headers    *    * @return Response    *    * @throws OzoneException    */
DECL|method|handleCall (String volume, Request request, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|handleCall
parameter_list|(
name|String
name|volume
parameter_list|,
name|Request
name|request
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
throws|throws
name|OzoneException
block|{
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
name|info
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
name|volume
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
comment|// we use the same logic for both bucket and volume names
name|OzoneUtils
operator|.
name|verifyBucketName
argument_list|(
name|volume
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
name|VolumeArgs
name|args
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volume
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
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"illegal argument. {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_VOLUME_NAME
argument_list|,
name|userArgs
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|handleIOException
argument_list|(
name|volume
argument_list|,
name|reqID
argument_list|,
name|hostName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Specific handler for each call.    *    * @param args - Volume Args    *    * @return - Response    *    * @throws IOException    * @throws OzoneException    */
DECL|method|doProcess (VolumeArgs args)
specifier|public
specifier|abstract
name|Response
name|doProcess
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
function_decl|;
comment|/**    * Maps Java File System Exceptions to Ozone Exceptions in the Volume path.    *    * @param volume - Name of the Volume    * @param reqID - Request ID    * @param hostName - HostName    * @param fsExp - Exception    *    * @throws OzoneException    */
DECL|method|handleIOException (String volume, String reqID, String hostName, IOException fsExp)
specifier|private
name|void
name|handleIOException
parameter_list|(
name|String
name|volume
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
name|OzoneException
name|exp
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fsExp
operator|instanceof
name|FileAlreadyExistsException
condition|)
block|{
name|exp
operator|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_ALREADY_EXISTS
argument_list|,
name|reqID
argument_list|,
name|volume
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fsExp
operator|instanceof
name|DirectoryNotEmptyException
condition|)
block|{
name|exp
operator|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|VOLUME_NOT_EMPTY
argument_list|,
name|reqID
argument_list|,
name|volume
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fsExp
operator|instanceof
name|NoSuchFileException
condition|)
block|{
name|exp
operator|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_VOLUME_NAME
argument_list|,
name|reqID
argument_list|,
name|volume
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|fsExp
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|exp
operator|!=
literal|null
operator|)
condition|)
block|{
name|exp
operator|.
name|setMessage
argument_list|(
name|fsExp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// We don't handle that FS error yet, report a Server Internal Error
if|if
condition|(
name|exp
operator|==
literal|null
condition|)
block|{
name|exp
operator|=
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
name|volume
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fsExp
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|setMessage
argument_list|(
name|fsExp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"IOException: {}"
argument_list|,
name|exp
argument_list|)
expr_stmt|;
throw|throw
name|exp
throw|;
block|}
comment|/**    * Set the user provided string into args and throw ozone exception    * if needed.    *    * @param args - volume args    * @param quota - quota sting    *    * @throws OzoneException    */
DECL|method|setQuotaArgs (VolumeArgs args, String quota)
name|void
name|setQuotaArgs
parameter_list|(
name|VolumeArgs
name|args
parameter_list|,
name|String
name|quota
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
name|args
operator|.
name|setQuota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
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
literal|"Malformed Quota: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MALFORMED_QUOTA
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Wraps calls into volumeInfo data.    *    * @param args - volumeArgs    *    * @return - VolumeInfo    *    * @throws IOException    * @throws OzoneException    */
DECL|method|getVolumeInfoResponse (VolumeArgs args)
name|Response
name|getVolumeInfoResponse
parameter_list|(
name|VolumeArgs
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
name|VolumeInfo
name|info
init|=
name|fs
operator|.
name|getVolumeInfo
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
comment|/**    * Returns all the volumes belonging to a user.    *    * @param user - userArgs    *    * @return - Response    *    * @throws OzoneException    * @throws IOException    */
DECL|method|getVolumesByUser (UserArgs user)
name|Response
name|getVolumesByUser
parameter_list|(
name|UserArgs
name|user
parameter_list|)
throws|throws
name|OzoneException
throws|,
name|IOException
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|ListVolumes
name|volumes
init|=
name|fs
operator|.
name|listVolumes
argument_list|(
name|user
argument_list|)
decl_stmt|;
return|return
name|OzoneUtils
operator|.
name|getResponse
argument_list|(
name|user
argument_list|,
name|HTTP_OK
argument_list|,
name|volumes
operator|.
name|toJsonString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * This call can also be invoked by Admins of the system where they can    * get the list of buckets of any user.    *    * User makes a call like    * GET / HTTP/1.1    * Host: ozone.self    *    * @param args - volumeArgs    *    * @return Response - A list of buckets owned this user    *    * @throws OzoneException    */
DECL|method|getVolumesByUser (VolumeArgs args)
name|Response
name|getVolumesByUser
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
name|String
name|validatedUser
init|=
name|args
operator|.
name|getUserName
argument_list|()
decl_stmt|;
try|try
block|{
name|UserAuth
name|auth
init|=
name|UserHandlerBuilder
operator|.
name|getAuthHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|auth
operator|.
name|isAdmin
argument_list|(
name|args
argument_list|)
condition|)
block|{
name|validatedUser
operator|=
name|auth
operator|.
name|getOzoneUser
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|validatedUser
operator|==
literal|null
condition|)
block|{
name|validatedUser
operator|=
name|auth
operator|.
name|getUser
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
name|UserArgs
name|user
init|=
operator|new
name|UserArgs
argument_list|(
name|validatedUser
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|,
name|args
operator|.
name|getHostName
argument_list|()
argument_list|,
name|args
operator|.
name|getRequest
argument_list|()
argument_list|,
name|args
operator|.
name|getUri
argument_list|()
argument_list|,
name|args
operator|.
name|getHeaders
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getVolumesByUser
argument_list|(
name|user
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"unable to get the volume list for the user. Ex: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|OzoneException
name|exp
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
decl_stmt|;
name|exp
operator|.
name|setMessage
argument_list|(
literal|"unable to get the volume list for the user"
argument_list|)
expr_stmt|;
throw|throw
name|exp
throw|;
block|}
block|}
comment|/**    * Returns a list of Buckets in a Volume.    *    * @return List of Buckets    *    * @throws OzoneException    */
DECL|method|getBucketsInVolume (VolumeArgs args)
name|Response
name|getBucketsInVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
comment|// UserAuth auth = UserHandlerBuilder.getAuthHandler();
comment|// TODO : Check ACLS.
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|ListBuckets
name|bucketList
init|=
name|fs
operator|.
name|listBuckets
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
name|bucketList
operator|.
name|toJsonString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"unable to get the bucket list for the specified volume."
operator|+
literal|" Ex: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|OzoneException
name|exp
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|args
argument_list|,
name|ex
argument_list|)
decl_stmt|;
name|exp
operator|.
name|setMessage
argument_list|(
literal|"unable to get the bucket list for the specified volume."
argument_list|)
expr_stmt|;
throw|throw
name|exp
throw|;
block|}
block|}
block|}
end_class

end_unit

