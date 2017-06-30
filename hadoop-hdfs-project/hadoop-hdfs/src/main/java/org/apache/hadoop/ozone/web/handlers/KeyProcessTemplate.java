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
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
operator|.
name|BAD_DIGEST
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
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
operator|.
name|INCOMPLETE_BODY
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
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
operator|.
name|INVALID_BUCKET_NAME
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
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
operator|.
name|INVALID_REQUEST
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
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
operator|.
name|SERVER_ERROR
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
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
operator|.
name|newError
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
comment|/**  * This class abstracts way the repetitive tasks in  Key handling code.  */
end_comment

begin_class
DECL|class|KeyProcessTemplate
specifier|public
specifier|abstract
class|class
name|KeyProcessTemplate
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
name|KeyProcessTemplate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This function serves as the common error handling function for all Key    * related operations.    *    * @param bucket  bucket Name    * @param key     the object name    * @param headers Http headers    * @param is      Input XML stream    * @throws OzoneException    */
DECL|method|handleCall (String volume, String bucket, String key, Request request, HttpHeaders headers, UriInfo info, InputStream is)
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
name|String
name|key
parameter_list|,
name|Request
name|request
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|InputStream
name|is
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
name|bucket
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|OzoneUtils
operator|.
name|verifyResourceName
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
name|KeyArgs
name|args
init|=
operator|new
name|KeyArgs
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
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
argument_list|,
name|is
argument_list|,
name|request
argument_list|,
name|headers
argument_list|,
name|info
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
name|argExp
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid bucket in key call. ex:{}"
argument_list|,
name|argExp
argument_list|)
expr_stmt|;
throw|throw
name|newError
argument_list|(
name|INVALID_BUCKET_NAME
argument_list|,
name|userArgs
argument_list|,
name|argExp
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|fsExp
parameter_list|)
block|{
comment|// TODO : Handle errors from the FileSystem , let us map to server error
comment|// for now.
name|LOG
operator|.
name|debug
argument_list|(
literal|"IOException. ex : {}"
argument_list|,
name|fsExp
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|SERVER_ERROR
argument_list|,
name|userArgs
argument_list|,
name|fsExp
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|algoEx
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NoSuchAlgorithmException. Probably indicates an unusual java "
operator|+
literal|"installation.  ex : {}"
argument_list|,
name|algoEx
argument_list|)
expr_stmt|;
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|SERVER_ERROR
argument_list|,
name|userArgs
argument_list|,
name|algoEx
argument_list|)
throw|;
block|}
block|}
comment|/**    * Abstract function that gets implemented in the KeyHandler functions. This    * function will just deal with the core file system related logic and will    * rely on handleCall function for repetitive error checks    *    * @param args    - parsed bucket args, name, userName, ACLs etc    * @param input   - The body as an Input Stream    * @param request - Http request    * @param headers - Parsed http Headers.    * @param info    - UriInfo    * @return Response    * @throws IOException - From the file system operations    */
DECL|method|doProcess (KeyArgs args, InputStream input, Request request, HttpHeaders headers, UriInfo info)
specifier|public
specifier|abstract
name|Response
name|doProcess
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|InputStream
name|input
parameter_list|,
name|Request
name|request
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|,
name|UriInfo
name|info
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
throws|,
name|NoSuchAlgorithmException
function_decl|;
comment|/**    * checks if the File Content-MD5 we wrote matches the hash we computed from    * the stream. if it does match we delete the file and throw and exception to    * let the user know that we have a hash mismatch    *    * @param args           Object Args    * @param computedString MD5 hash value    * @param fs             Pointer to File System so we can delete the file    * @param contentHash    User Specified hash string    * @throws IOException    * @throws OzoneException    */
DECL|method|checkFileHashMatch (KeyArgs args, String computedString, StorageHandler fs, String contentHash)
specifier|public
name|void
name|checkFileHashMatch
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|String
name|computedString
parameter_list|,
name|StorageHandler
name|fs
parameter_list|,
name|String
name|contentHash
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
if|if
condition|(
name|contentHash
operator|!=
literal|null
condition|)
block|{
name|String
name|contentString
init|=
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|contentHash
argument_list|)
argument_list|,
name|OzoneUtils
operator|.
name|ENCODING
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|contentString
operator|.
name|equals
argument_list|(
name|computedString
argument_list|)
condition|)
block|{
name|fs
operator|.
name|deleteKey
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|BAD_DIGEST
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"MD5 Digest mismatch. Expected %s Found "
operator|+
literal|"%s"
argument_list|,
name|contentString
argument_list|,
name|computedString
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
comment|/**    * check if the content-length matches the actual stream length. if we find a    * mismatch we will delete the file and throw an exception to let the user    * know that length mismatch detected    *    * @param args       Object Args    * @param fs         Pointer to File System Object, to delete the file that we    *                   wrote    * @param contentLen Http Content-Length Header    * @param bytesRead  Actual Bytes we read from the stream    * @throws IOException    * @throws OzoneException    */
DECL|method|checkFileLengthMatch (KeyArgs args, StorageHandler fs, int contentLen, int bytesRead)
specifier|public
name|void
name|checkFileLengthMatch
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|StorageHandler
name|fs
parameter_list|,
name|int
name|contentLen
parameter_list|,
name|int
name|bytesRead
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
if|if
condition|(
name|bytesRead
operator|!=
name|contentLen
condition|)
block|{
name|fs
operator|.
name|deleteKey
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|INCOMPLETE_BODY
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Body length mismatch. Expected length : %d"
operator|+
literal|" Found %d"
argument_list|,
name|contentLen
argument_list|,
name|bytesRead
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
comment|/**    * Returns Content Length header value if available.    *    * @param headers - Http Headers    * @return - String or null    */
DECL|method|getContentLength (HttpHeaders headers, KeyArgs args)
specifier|public
name|String
name|getContentLength
parameter_list|(
name|HttpHeaders
name|headers
parameter_list|,
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|OzoneException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|contentLengthList
init|=
name|headers
operator|.
name|getRequestHeader
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|contentLengthList
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|contentLengthList
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
return|return
name|contentLengthList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|OzoneException
name|ex
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|INVALID_REQUEST
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setMessage
argument_list|(
literal|"Content-Length is a required header for putting a key."
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
comment|/**    * Returns Content MD5 value if available.    *    * @param headers - Http Headers    * @return - String or null    */
DECL|method|getContentMD5 (HttpHeaders headers, KeyArgs args)
specifier|public
name|String
name|getContentMD5
parameter_list|(
name|HttpHeaders
name|headers
parameter_list|,
name|KeyArgs
name|args
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|contentLengthList
init|=
name|headers
operator|.
name|getRequestHeader
argument_list|(
name|Header
operator|.
name|CONTENT_MD5
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|contentLengthList
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|contentLengthList
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
return|return
name|contentLengthList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// TODO : Should we make this compulsory ?
comment|//    OzoneException ex = ErrorTable.newError(ErrorTable.invalidRequest, args);
comment|//    ex.setMessage("Content-MD5 is a required header for putting a key");
comment|//    throw ex;
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

