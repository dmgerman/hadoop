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
name|Hex
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|LengthInputStream
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
name|Keys
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
name|response
operator|.
name|KeyInfo
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_CREATED
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

begin_comment
comment|/**  * KeyHandler deals with basic Key Operations.  */
end_comment

begin_class
DECL|class|KeyHandler
specifier|public
class|class
name|KeyHandler
implements|implements
name|Keys
block|{
comment|/**    * Gets the Key/key information if it exists.    *    * @param volume  Storage Volume    * @param bucket  Name of the bucket    * @param key Name of the key    * @param info Tag info    * @param req Request    * @param uriInfo Uri Info    * @param headers Http Header    * @return Response    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|getKey (String volume, String bucket, String key, String info, Request req, UriInfo uriInfo, HttpHeaders headers)
specifier|public
name|Response
name|getKey
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
name|String
name|info
parameter_list|,
name|Request
name|req
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
return|return
operator|new
name|KeyProcessTemplate
argument_list|()
block|{
comment|/**        * Abstract function that gets implemented in the KeyHandler functions.        * This function will just deal with the core file system related logic        * and will rely on handleCall function for repetitive error checks        *        * @param args - parsed bucket args, name, userName, ACLs etc        * @param input - The body as an Input Stream        * @param request - Http request        * @param headers - Parsed http Headers.        * @param uriInfo - UriInfo        *        * @return Response        *        * @throws IOException - From the file system operations        */
annotation|@
name|Override
specifier|public
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
name|uriInfo
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
throws|,
name|NoSuchAlgorithmException
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
return|return
name|getKey
argument_list|(
name|args
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|equals
argument_list|(
name|Header
operator|.
name|OZONE_LIST_QUERY_KEY
argument_list|)
condition|)
block|{
return|return
name|getKeyInfo
argument_list|(
name|args
argument_list|)
return|;
block|}
name|OzoneException
name|ozException
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|INVALID_QUERY_PARAM
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ozException
operator|.
name|setMessage
argument_list|(
literal|"Unrecognized query param : "
operator|+
name|info
argument_list|)
expr_stmt|;
throw|throw
name|ozException
throw|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|,
name|req
argument_list|,
name|headers
argument_list|,
name|uriInfo
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Gets the Key if it exists.    */
DECL|method|getKey (KeyArgs args)
specifier|private
name|Response
name|getKey
parameter_list|(
name|KeyArgs
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
name|LengthInputStream
name|stream
init|=
name|fs
operator|.
name|newKeyReader
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
name|stream
argument_list|)
return|;
block|}
comment|/**    * Gets the Key information if it exists.    */
DECL|method|getKeyInfo (KeyArgs args)
specifier|private
name|Response
name|getKeyInfo
parameter_list|(
name|KeyArgs
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
name|KeyInfo
name|keyInfo
init|=
name|fs
operator|.
name|getKeyInfo
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
name|keyInfo
operator|.
name|toJsonString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Adds a key to an existing bucket. If the object already exists this call    * will overwrite or add with new version number if the bucket versioning is    * turned on.    *    * @param volume  Storage Volume Name    * @param bucket  Name of the bucket    * @param keys    Name of the Object    * @param is      InputStream or File Data    * @param req     Request    * @param info    - UriInfo    * @param headers http headers    * @return Response    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|putKey (String volume, String bucket, String keys, InputStream is, Request req, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|putKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|keys
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Request
name|req
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
return|return
operator|new
name|KeyProcessTemplate
argument_list|()
block|{
comment|/**        * Abstract function that gets implemented in the KeyHandler functions.        * This function will just deal with the core file system related logic        * and will rely on handleCall function for repetitive error checks        *        * @param args - parsed bucket args, name, userName, ACLs etc        * @param input - The body as an Input Stream        * @param request - Http request        * @param headers - Parsed http Headers.        * @param info - UriInfo        *        * @return Response        *        * @throws IOException - From the file system operations        */
annotation|@
name|Override
specifier|public
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
block|{
specifier|final
name|int
name|eof
init|=
operator|-
literal|1
decl_stmt|;
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4
operator|*
literal|1024
index|]
decl_stmt|;
name|String
name|contentLenString
init|=
name|getContentLength
argument_list|(
name|headers
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|newLen
init|=
name|contentLenString
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|contentLen
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|newLen
argument_list|)
decl_stmt|;
name|args
operator|.
name|setSize
argument_list|(
name|contentLen
argument_list|)
expr_stmt|;
name|MessageDigest
name|md5
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
name|OutputStream
name|stream
init|=
name|fs
operator|.
name|newKeyWriter
argument_list|(
name|args
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|<
name|contentLen
operator|)
operator|&&
operator|(
name|len
operator|!=
name|eof
operator|)
condition|)
block|{
name|int
name|readSize
init|=
operator|(
name|contentLen
operator|-
name|bytesRead
operator|>
name|buffer
operator|.
name|length
operator|)
condition|?
name|buffer
operator|.
name|length
else|:
name|contentLen
operator|-
name|bytesRead
decl_stmt|;
name|len
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|readSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|!=
name|eof
condition|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|md5
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|len
expr_stmt|;
block|}
block|}
name|checkFileLengthMatch
argument_list|(
name|args
argument_list|,
name|fs
argument_list|,
name|contentLen
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|String
name|hashString
init|=
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|md5
operator|.
name|digest
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO : Enable hash value checking.
comment|//          String contentHash = getContentMD5(headers, args);
comment|//          checkFileHashMatch(args, hashString, fs, contentHash);
name|args
operator|.
name|setHash
argument_list|(
name|hashString
argument_list|)
expr_stmt|;
name|args
operator|.
name|setSize
argument_list|(
name|bytesRead
argument_list|)
expr_stmt|;
name|fs
operator|.
name|commitKey
argument_list|(
name|args
argument_list|,
name|stream
argument_list|)
expr_stmt|;
return|return
name|OzoneUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_CREATED
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|keys
argument_list|,
name|req
argument_list|,
name|headers
argument_list|,
name|info
argument_list|,
name|is
argument_list|)
return|;
block|}
comment|/**    * Deletes an existing key.    *    * @param volume  Storage Volume Name    * @param bucket  Name of the bucket    * @param keys    Name of the Object    * @param req     http Request    * @param info    - UriInfo    * @param headers HttpHeaders    * @return Response    * @throws OzoneException    */
annotation|@
name|Override
DECL|method|deleteKey (String volume, String bucket, String keys, Request req, UriInfo info, HttpHeaders headers)
specifier|public
name|Response
name|deleteKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|keys
parameter_list|,
name|Request
name|req
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
return|return
operator|new
name|KeyProcessTemplate
argument_list|()
block|{
comment|/**        * Abstract function that gets implemented in the KeyHandler functions.        * This function will just deal with the core file system related logic        * and will rely on handleCall function for repetitive error checks        *        * @param args - parsed bucket args, name, userName, ACLs etc        * @param input - The body as an Input Stream        * @param request - Http request        * @param headers - Parsed http Headers.        * @param info - UriInfo        *        * @return Response        *        * @throws IOException - From the file system operations        */
annotation|@
name|Override
specifier|public
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
block|{
name|StorageHandler
name|fs
init|=
name|StorageHandlerBuilder
operator|.
name|getStorageHandler
argument_list|()
decl_stmt|;
name|fs
operator|.
name|deleteKey
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|OzoneUtils
operator|.
name|getResponse
argument_list|(
name|args
argument_list|,
name|HTTP_OK
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
operator|.
name|handleCall
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|keys
argument_list|,
name|req
argument_list|,
name|headers
argument_list|,
name|info
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

