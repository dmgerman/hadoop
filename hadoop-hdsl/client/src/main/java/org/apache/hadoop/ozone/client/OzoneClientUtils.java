begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZonedDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|classification
operator|.
name|InterfaceStability
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
name|hdsl
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|scm
operator|.
name|ScmConfigKeys
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
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|config
operator|.
name|RequestConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClients
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

begin_comment
comment|/**  * Utility methods for Ozone and Container Clients.  *  * The methods to retrieve SCM service endpoints assume there is a single  * SCM service instance. This will change when we switch to replicated service  * instances for redundancy.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OzoneClientUtils
specifier|public
specifier|final
class|class
name|OzoneClientUtils
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
name|OzoneClientUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NO_PORT
specifier|private
specifier|static
specifier|final
name|int
name|NO_PORT
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|OzoneClientUtils ()
specifier|private
name|OzoneClientUtils
parameter_list|()
block|{   }
comment|/**    * Date format that used in ozone. Here the format is thread safe to use.    */
DECL|field|DATE_FORMAT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|DateTimeFormatter
argument_list|>
name|DATE_FORMAT
init|=
name|ThreadLocal
operator|.
name|withInitial
argument_list|(
parameter_list|()
lambda|->
block|{
name|DateTimeFormatter
name|format
init|=
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_DATE_FORMAT
argument_list|)
decl_stmt|;
return|return
name|format
operator|.
name|withZone
argument_list|(
name|ZoneId
operator|.
name|of
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_TIME_ZONE
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
comment|/**    * Returns the cache value to be used for list calls.    * @param conf Configuration object    * @return list cache size    */
DECL|method|getListCacheSize (Configuration conf)
specifier|public
specifier|static
name|int
name|getListCacheSize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_LIST_CACHE_SIZE
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_LIST_CACHE_SIZE_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * @return a default instance of {@link CloseableHttpClient}.    */
DECL|method|newHttpClient ()
specifier|public
specifier|static
name|CloseableHttpClient
name|newHttpClient
parameter_list|()
block|{
return|return
name|OzoneClientUtils
operator|.
name|newHttpClient
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link CloseableHttpClient} configured by given configuration.    * If conf is null, returns a default instance.    *    * @param conf configuration    * @return a {@link CloseableHttpClient} instance.    */
DECL|method|newHttpClient (Configuration conf)
specifier|public
specifier|static
name|CloseableHttpClient
name|newHttpClient
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|long
name|socketTimeout
init|=
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT
decl_stmt|;
name|long
name|connectionTimeout
init|=
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|socketTimeout
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_SOCKET_TIMEOUT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|connectionTimeout
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_CONNECTION_TIMEOUT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|CloseableHttpClient
name|client
init|=
name|HttpClients
operator|.
name|custom
argument_list|()
operator|.
name|setDefaultRequestConfig
argument_list|(
name|RequestConfig
operator|.
name|custom
argument_list|()
operator|.
name|setSocketTimeout
argument_list|(
name|Math
operator|.
name|toIntExact
argument_list|(
name|socketTimeout
argument_list|)
argument_list|)
operator|.
name|setConnectTimeout
argument_list|(
name|Math
operator|.
name|toIntExact
argument_list|(
name|connectionTimeout
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|client
return|;
block|}
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
comment|/**    * Convert time in millisecond to a human readable format required in ozone.    * @return a human readable string for the input time    */
DECL|method|formatDateTime (long millis)
specifier|public
specifier|static
name|String
name|formatDateTime
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
name|ZonedDateTime
name|dateTime
init|=
name|ZonedDateTime
operator|.
name|ofInstant
argument_list|(
name|Instant
operator|.
name|ofEpochSecond
argument_list|(
name|millis
argument_list|)
argument_list|,
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|getZone
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|DATE_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|dateTime
argument_list|)
return|;
block|}
comment|/**    * Convert time in ozone date format to millisecond.    * @return time in milliseconds    */
DECL|method|formatDateTime (String date)
specifier|public
specifier|static
name|long
name|formatDateTime
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
name|ZonedDateTime
operator|.
name|parse
argument_list|(
name|date
argument_list|,
name|DATE_FORMAT
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toInstant
argument_list|()
operator|.
name|getEpochSecond
argument_list|()
return|;
block|}
comment|/**    * Returns the maximum no of outstanding async requests to be handled by    * Standalone and Ratis client.    */
DECL|method|getMaxOutstandingRequests (Configuration config)
specifier|public
specifier|static
name|int
name|getMaxOutstandingRequests
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
return|return
name|config
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS
argument_list|,
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS_DEFAULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

