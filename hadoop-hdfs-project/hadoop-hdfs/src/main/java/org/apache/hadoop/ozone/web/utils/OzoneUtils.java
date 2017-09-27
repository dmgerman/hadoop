begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.utils
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
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|base
operator|.
name|Strings
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|client
operator|.
name|io
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
name|handlers
operator|.
name|UserArgs
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
name|client
operator|.
name|rest
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
name|scm
operator|.
name|ScmConfigKeys
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
name|util
operator|.
name|Time
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
name|MediaType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Set of Utility functions used in ozone.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OzoneUtils
specifier|public
specifier|final
class|class
name|OzoneUtils
block|{
DECL|field|ENCODING_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ENCODING_NAME
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|ENCODING
specifier|public
specifier|static
specifier|final
name|Charset
name|ENCODING
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|ENCODING_NAME
argument_list|)
decl_stmt|;
DECL|method|OzoneUtils ()
specifier|private
name|OzoneUtils
parameter_list|()
block|{
comment|// Never constructed
block|}
comment|/**    * Date format that used in ozone. Here the format is thread safe to use.    */
DECL|field|DATE_FORMAT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
name|DATE_FORMAT
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SimpleDateFormat
name|initialValue
parameter_list|()
block|{
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_TIME_ZONE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|format
return|;
block|}
block|}
decl_stmt|;
comment|/**    * verifies that bucket name / volume name is a valid DNS name.    *    * @param resName Bucket or volume Name to be validated    *    * @throws IllegalArgumentException    */
DECL|method|verifyResourceName (String resName)
specifier|public
specifier|static
name|void
name|verifyResourceName
parameter_list|(
name|String
name|resName
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|resName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|resName
operator|.
name|length
argument_list|()
operator|<
name|OzoneConsts
operator|.
name|OZONE_MIN_BUCKET_NAME_LENGTH
operator|)
operator|||
operator|(
name|resName
operator|.
name|length
argument_list|()
operator|>
name|OzoneConsts
operator|.
name|OZONE_MAX_BUCKET_NAME_LENGTH
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume length is illegal, "
operator|+
literal|"valid length is 3-63 characters"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|resName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'.'
operator|)
operator|||
operator|(
name|resName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name cannot start with a period or dash"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|resName
operator|.
name|charAt
argument_list|(
name|resName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'.'
operator|)
operator|||
operator|(
name|resName
operator|.
name|charAt
argument_list|(
name|resName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'-'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name cannot end with a period or dash"
argument_list|)
throw|;
block|}
name|boolean
name|isIPv4
init|=
literal|true
decl_stmt|;
name|char
name|prev
init|=
operator|(
name|char
operator|)
literal|0
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|resName
operator|.
name|length
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|char
name|currChar
init|=
name|resName
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|currChar
operator|!=
literal|'.'
condition|)
block|{
name|isIPv4
operator|=
operator|(
operator|(
name|currChar
operator|>=
literal|'0'
operator|)
operator|&&
operator|(
name|currChar
operator|<=
literal|'9'
operator|)
operator|)
operator|&&
name|isIPv4
expr_stmt|;
block|}
if|if
condition|(
name|currChar
operator|>
literal|'A'
operator|&&
name|currChar
operator|<
literal|'Z'
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name does not support uppercase characters"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|currChar
operator|!=
literal|'.'
operator|)
operator|&&
operator|(
name|currChar
operator|!=
literal|'-'
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|currChar
operator|<
literal|'0'
operator|)
operator|||
operator|(
name|currChar
operator|>
literal|'9'
operator|&&
name|currChar
operator|<
literal|'a'
operator|)
operator|||
operator|(
name|currChar
operator|>
literal|'z'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name has an "
operator|+
literal|"unsupported character : "
operator|+
name|currChar
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|(
name|prev
operator|==
literal|'.'
operator|)
operator|&&
operator|(
name|currChar
operator|==
literal|'.'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name should not "
operator|+
literal|"have two contiguous periods"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|prev
operator|==
literal|'-'
operator|)
operator|&&
operator|(
name|currChar
operator|==
literal|'.'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name should not have period after dash"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|prev
operator|==
literal|'.'
operator|)
operator|&&
operator|(
name|currChar
operator|==
literal|'-'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name should not have dash after period"
argument_list|)
throw|;
block|}
name|prev
operator|=
name|currChar
expr_stmt|;
block|}
if|if
condition|(
name|isIPv4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket or Volume name cannot be an IPv4 address or all numeric"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Verifies that max key length is a valid value.    *    * @param length    *          The max key length to be validated    *    * @throws IllegalArgumentException    */
DECL|method|verifyMaxKeyLength (String length)
specifier|public
specifier|static
name|void
name|verifyMaxKeyLength
parameter_list|(
name|String
name|length
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|int
name|maxKey
init|=
literal|0
decl_stmt|;
try|try
block|{
name|maxKey
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid max key length, the vaule should be digital."
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxKey
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid max key length, the vaule should be a positive number."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a random Request ID.    *    * Request ID is returned to the client as well as flows through the system    * facilitating debugging on why a certain request failed.    *    * @return String random request ID    */
DECL|method|getRequestID ()
specifier|public
specifier|static
name|String
name|getRequestID
parameter_list|()
block|{
return|return
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Return host name if possible.    *    * @return Host Name or localhost    */
DECL|method|getHostName ()
specifier|public
specifier|static
name|String
name|getHostName
parameter_list|()
block|{
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
try|try
block|{
name|host
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|// Ignore the error
block|}
return|return
name|host
return|;
block|}
comment|/**    * Basic validate routine to make sure that all the    * required headers are in place.    *    * @param request - http request    * @param headers - http headers    * @param reqId - request id    * @param resource - Resource Name    * @param hostname - Hostname    *    * @throws OzoneException    */
DECL|method|validate (Request request, HttpHeaders headers, String reqId, String resource, String hostname)
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|Request
name|request
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|,
name|String
name|reqId
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|OzoneException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ozHeader
init|=
name|headers
operator|.
name|getRequestHeader
argument_list|(
name|Header
operator|.
name|OZONE_VERSION_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|ozHeader
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MISSING_VERSION
argument_list|,
name|reqId
argument_list|,
name|resource
argument_list|,
name|hostname
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|date
init|=
name|headers
operator|.
name|getRequestHeader
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
throw|throw
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|MISSING_DATE
argument_list|,
name|reqId
argument_list|,
name|resource
argument_list|,
name|hostname
argument_list|)
throw|;
block|}
comment|/*     TODO :     Ignore the results for time being. Eventually we can validate if the     request Date time is too skewed and reject if it is so.     */
name|parseDate
argument_list|(
name|date
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|reqId
argument_list|,
name|resource
argument_list|,
name|hostname
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses the Date String coming from the Users.    *    * @param dateString - Date String    * @param reqID - Ozone Request ID    * @param resource - Resource Name    * @param hostname - HostName    *    * @return - Date    *    * @throws OzoneException - in case of parsing error    */
DECL|method|parseDate (String dateString, String reqID, String resource, String hostname)
specifier|public
specifier|static
specifier|synchronized
name|Date
name|parseDate
parameter_list|(
name|String
name|dateString
parameter_list|,
name|String
name|reqID
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|OzoneException
block|{
try|try
block|{
return|return
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
name|OzoneException
name|exp
init|=
name|ErrorTable
operator|.
name|newError
argument_list|(
name|ErrorTable
operator|.
name|BAD_DATE
argument_list|,
name|reqID
argument_list|,
name|resource
argument_list|,
name|hostname
argument_list|)
decl_stmt|;
name|exp
operator|.
name|setMessage
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|exp
throw|;
block|}
block|}
comment|/**    * Returns a response with appropriate OZONE headers and payload.    *    * @param args - UserArgs or Inherited class    * @param statusCode - HttpStatus code    * @param payload - Content Body    *    * @return JAX-RS Response    */
DECL|method|getResponse (UserArgs args, int statusCode, String payload)
specifier|public
specifier|static
name|Response
name|getResponse
parameter_list|(
name|UserArgs
name|args
parameter_list|,
name|int
name|statusCode
parameter_list|,
name|String
name|payload
parameter_list|)
block|{
name|String
name|date
init|=
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|payload
argument_list|)
operator|.
name|header
argument_list|(
name|Header
operator|.
name|OZONE_SERVER_NAME
argument_list|,
name|args
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
name|Header
operator|.
name|OZONE_REQUEST_ID
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|,
name|date
argument_list|)
operator|.
name|status
argument_list|(
name|statusCode
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a response with appropriate OZONE headers and payload.    *    * @param args - UserArgs or Inherited class    * @param statusCode - HttpStatus code    * @param stream InputStream    *    * @return JAX-RS Response    */
DECL|method|getResponse (UserArgs args, int statusCode, LengthInputStream stream)
specifier|public
specifier|static
name|Response
name|getResponse
parameter_list|(
name|UserArgs
name|args
parameter_list|,
name|int
name|statusCode
parameter_list|,
name|LengthInputStream
name|stream
parameter_list|)
block|{
name|String
name|date
init|=
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|stream
argument_list|,
name|MediaType
operator|.
name|APPLICATION_OCTET_STREAM
argument_list|)
operator|.
name|header
argument_list|(
name|Header
operator|.
name|OZONE_SERVER_NAME
argument_list|,
name|args
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
name|Header
operator|.
name|OZONE_REQUEST_ID
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|)
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|,
name|date
argument_list|)
operator|.
name|status
argument_list|(
name|statusCode
argument_list|)
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_LENGTH
argument_list|,
name|stream
operator|.
name|getLength
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Checks and creates Ozone Metadir Path if it does not exist.    *    * @param conf - Configuration    *    * @return File MetaDir    */
DECL|method|getScmMetadirPath (Configuration conf)
specifier|public
specifier|static
name|File
name|getScmMetadirPath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|metaDirPath
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|metaDirPath
argument_list|)
expr_stmt|;
name|File
name|dirPath
init|=
operator|new
name|File
argument_list|(
name|metaDirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dirPath
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|dirPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to create paths. Path: "
operator|+
name|dirPath
argument_list|)
throw|;
block|}
return|return
name|dirPath
return|;
block|}
comment|/**    * Get the path for datanode id file.    *    * @param conf - Configuration    * @return the path of datanode id as string    */
DECL|method|getDatanodeIDPath (Configuration conf)
specifier|public
specifier|static
name|String
name|getDatanodeIDPath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|dataNodeIDPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|dataNodeIDPath
argument_list|)
condition|)
block|{
name|String
name|metaPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|metaPath
argument_list|)
condition|)
block|{
comment|// this means meta data is not found, in theory should not happen at
comment|// this point because should've failed earlier.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to locate meta data"
operator|+
literal|"directory when getting datanode id path"
argument_list|)
throw|;
block|}
name|dataNodeIDPath
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|metaPath
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID_PATH_DEFAULT
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|dataNodeIDPath
return|;
block|}
comment|/**    * Convert time in millisecond to a human readable format required in ozone.    * @return a human readable string for the input time    */
DECL|method|formatTime (long millis)
specifier|public
specifier|static
name|String
name|formatTime
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
return|return
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|millis
argument_list|)
return|;
block|}
comment|/**    * Convert time in ozone date format to millisecond.    * @return time in milliseconds    */
DECL|method|formatDate (String date)
specifier|public
specifier|static
name|long
name|formatDate
parameter_list|(
name|String
name|date
parameter_list|)
throws|throws
name|ParseException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|date
argument_list|,
literal|"Date string should not be null."
argument_list|)
expr_stmt|;
return|return
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|date
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

