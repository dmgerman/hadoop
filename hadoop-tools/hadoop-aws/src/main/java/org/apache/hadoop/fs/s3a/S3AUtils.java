begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonServiceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|EnvironmentVariableCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|InstanceProfileCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|AmazonS3Exception
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectSummary
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|s3native
operator|.
name|S3xLoginHelper
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
name|security
operator|.
name|ProviderUtils
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
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|net
operator|.
name|URI
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
name|AccessDeniedException
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
name|Map
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
name|ExecutionException
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|ACCESS_KEY
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|AWS_CREDENTIALS_PROVIDER
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|ENDPOINT
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|MULTIPART_MIN_SIZE
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|SECRET_KEY
import|;
end_import

begin_comment
comment|/**  * Utility methods for S3A code.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|S3AUtils
specifier|public
specifier|final
class|class
name|S3AUtils
block|{
comment|/** Reuse the S3AFileSystem log. */
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|S3AFileSystem
operator|.
name|LOG
decl_stmt|;
DECL|field|CONSTRUCTOR_EXCEPTION
specifier|static
specifier|final
name|String
name|CONSTRUCTOR_EXCEPTION
init|=
literal|"constructor exception"
decl_stmt|;
DECL|field|INSTANTIATION_EXCEPTION
specifier|static
specifier|final
name|String
name|INSTANTIATION_EXCEPTION
init|=
literal|"instantiation exception"
decl_stmt|;
DECL|field|NOT_AWS_PROVIDER
specifier|static
specifier|final
name|String
name|NOT_AWS_PROVIDER
init|=
literal|"does not implement AWSCredentialsProvider"
decl_stmt|;
DECL|field|ENDPOINT_KEY
specifier|static
specifier|final
name|String
name|ENDPOINT_KEY
init|=
literal|"Endpoint"
decl_stmt|;
DECL|method|S3AUtils ()
specifier|private
name|S3AUtils
parameter_list|()
block|{   }
comment|/**    * Translate an exception raised in an operation into an IOException.    * The specific type of IOException depends on the class of    * {@link AmazonClientException} passed in, and any status codes included    * in the operation. That is: HTTP error codes are examined and can be    * used to build a more specific response.    * @param operation operation    * @param path path operated on (must not be null)    * @param exception amazon exception raised    * @return an IOE which wraps the caught exception.    */
DECL|method|translateException (String operation, Path path, AmazonClientException exception)
specifier|public
specifier|static
name|IOException
name|translateException
parameter_list|(
name|String
name|operation
parameter_list|,
name|Path
name|path
parameter_list|,
name|AmazonClientException
name|exception
parameter_list|)
block|{
return|return
name|translateException
argument_list|(
name|operation
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|exception
argument_list|)
return|;
block|}
comment|/**    * Translate an exception raised in an operation into an IOException.    * The specific type of IOException depends on the class of    * {@link AmazonClientException} passed in, and any status codes included    * in the operation. That is: HTTP error codes are examined and can be    * used to build a more specific response.    * @param operation operation    * @param path path operated on (may be null)    * @param exception amazon exception raised    * @return an IOE which wraps the caught exception.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ThrowableInstanceNeverThrown"
argument_list|)
DECL|method|translateException (String operation, String path, AmazonClientException exception)
specifier|public
specifier|static
name|IOException
name|translateException
parameter_list|(
name|String
name|operation
parameter_list|,
name|String
name|path
parameter_list|,
name|AmazonClientException
name|exception
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s%s: %s"
argument_list|,
name|operation
argument_list|,
name|path
operator|!=
literal|null
condition|?
operator|(
literal|" on "
operator|+
name|path
operator|)
else|:
literal|""
argument_list|,
name|exception
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|exception
operator|instanceof
name|AmazonServiceException
operator|)
condition|)
block|{
return|return
operator|new
name|AWSClientIOException
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
return|;
block|}
else|else
block|{
name|IOException
name|ioe
decl_stmt|;
name|AmazonServiceException
name|ase
init|=
operator|(
name|AmazonServiceException
operator|)
name|exception
decl_stmt|;
comment|// this exception is non-null if the service exception is an s3 one
name|AmazonS3Exception
name|s3Exception
init|=
name|ase
operator|instanceof
name|AmazonS3Exception
condition|?
operator|(
name|AmazonS3Exception
operator|)
name|ase
else|:
literal|null
decl_stmt|;
name|int
name|status
init|=
name|ase
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
literal|301
case|:
if|if
condition|(
name|s3Exception
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|s3Exception
operator|.
name|getAdditionalDetails
argument_list|()
operator|!=
literal|null
operator|&&
name|s3Exception
operator|.
name|getAdditionalDetails
argument_list|()
operator|.
name|containsKey
argument_list|(
name|ENDPOINT_KEY
argument_list|)
condition|)
block|{
name|message
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Received permanent redirect response to "
operator|+
literal|"endpoint %s.  This likely indicates that the S3 endpoint "
operator|+
literal|"configured in %s does not match the AWS region containing "
operator|+
literal|"the bucket."
argument_list|,
name|s3Exception
operator|.
name|getAdditionalDetails
argument_list|()
operator|.
name|get
argument_list|(
name|ENDPOINT_KEY
argument_list|)
argument_list|,
name|ENDPOINT
argument_list|)
expr_stmt|;
block|}
name|ioe
operator|=
operator|new
name|AWSS3IOException
argument_list|(
name|message
argument_list|,
name|s3Exception
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ioe
operator|=
operator|new
name|AWSServiceIOException
argument_list|(
name|message
argument_list|,
name|ase
argument_list|)
expr_stmt|;
block|}
break|break;
comment|// permissions
case|case
literal|401
case|:
case|case
literal|403
case|:
name|ioe
operator|=
operator|new
name|AccessDeniedException
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|ase
argument_list|)
expr_stmt|;
break|break;
comment|// the object isn't there
case|case
literal|404
case|:
case|case
literal|410
case|:
name|ioe
operator|=
operator|new
name|FileNotFoundException
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|ase
argument_list|)
expr_stmt|;
break|break;
comment|// out of range. This may happen if an object is overwritten with
comment|// a shorter one while it is being read.
case|case
literal|416
case|:
name|ioe
operator|=
operator|new
name|EOFException
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// no specific exit code. Choose an IOE subclass based on the class
comment|// of the caught exception
name|ioe
operator|=
name|s3Exception
operator|!=
literal|null
condition|?
operator|new
name|AWSS3IOException
argument_list|(
name|message
argument_list|,
name|s3Exception
argument_list|)
else|:
operator|new
name|AWSServiceIOException
argument_list|(
name|message
argument_list|,
name|ase
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|ioe
return|;
block|}
block|}
comment|/**    * Extract an exception from a failed future, and convert to an IOE.    * @param operation operation which failed    * @param path path operated on (may be null)    * @param ee execution exception    * @return an IOE which can be thrown    */
DECL|method|extractException (String operation, String path, ExecutionException ee)
specifier|public
specifier|static
name|IOException
name|extractException
parameter_list|(
name|String
name|operation
parameter_list|,
name|String
name|path
parameter_list|,
name|ExecutionException
name|ee
parameter_list|)
block|{
name|IOException
name|ioe
decl_stmt|;
name|Throwable
name|cause
init|=
name|ee
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|AmazonClientException
condition|)
block|{
name|ioe
operator|=
name|translateException
argument_list|(
name|operation
argument_list|,
name|path
argument_list|,
operator|(
name|AmazonClientException
operator|)
name|cause
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|IOException
condition|)
block|{
name|ioe
operator|=
operator|(
name|IOException
operator|)
name|cause
expr_stmt|;
block|}
else|else
block|{
name|ioe
operator|=
operator|new
name|IOException
argument_list|(
name|operation
operator|+
literal|" failed: "
operator|+
name|cause
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
return|return
name|ioe
return|;
block|}
comment|/**    * Get low level details of an amazon exception for logging; multi-line.    * @param e exception    * @return string details    */
DECL|method|stringify (AmazonServiceException e)
specifier|public
specifier|static
name|String
name|stringify
parameter_list|(
name|AmazonServiceException
name|e
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s: %s error %d: %s; %s%s%n"
argument_list|,
name|e
operator|.
name|getErrorType
argument_list|()
argument_list|,
name|e
operator|.
name|getServiceName
argument_list|()
argument_list|,
name|e
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
operator|(
name|e
operator|.
name|isRetryable
argument_list|()
condition|?
literal|" (retryable)"
else|:
literal|""
operator|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rawResponseContent
init|=
name|e
operator|.
name|getRawResponseContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|rawResponseContent
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|rawResponseContent
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get low level details of an amazon exception for logging; multi-line.    * @param e exception    * @return string details    */
DECL|method|stringify (AmazonS3Exception e)
specifier|public
specifier|static
name|String
name|stringify
parameter_list|(
name|AmazonS3Exception
name|e
parameter_list|)
block|{
comment|// get the low level details of an exception,
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|stringify
argument_list|(
operator|(
name|AmazonServiceException
operator|)
name|e
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|details
init|=
name|e
operator|.
name|getAdditionalDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|d
range|:
name|details
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|d
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|d
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Create a files status instance from a listing.    * @param keyPath path to entry    * @param summary summary from AWS    * @param blockSize block size to declare.    * @return a status entry    */
DECL|method|createFileStatus (Path keyPath, S3ObjectSummary summary, long blockSize)
specifier|public
specifier|static
name|S3AFileStatus
name|createFileStatus
parameter_list|(
name|Path
name|keyPath
parameter_list|,
name|S3ObjectSummary
name|summary
parameter_list|,
name|long
name|blockSize
parameter_list|)
block|{
if|if
condition|(
name|objectRepresentsDirectory
argument_list|(
name|summary
operator|.
name|getKey
argument_list|()
argument_list|,
name|summary
operator|.
name|getSize
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
name|keyPath
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
name|summary
operator|.
name|getSize
argument_list|()
argument_list|,
name|dateToLong
argument_list|(
name|summary
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|,
name|keyPath
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
block|}
comment|/**    * Predicate: does the object represent a directory?.    * @param name object name    * @param size object size    * @return true if it meets the criteria for being an object    */
DECL|method|objectRepresentsDirectory (final String name, final long size)
specifier|public
specifier|static
name|boolean
name|objectRepresentsDirectory
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|long
name|size
parameter_list|)
block|{
return|return
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|&&
name|name
operator|.
name|charAt
argument_list|(
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
operator|&&
name|size
operator|==
literal|0L
return|;
block|}
comment|/**    * Date to long conversion.    * Handles null Dates that can be returned by AWS by returning 0    * @param date date from AWS query    * @return timestamp of the object    */
DECL|method|dateToLong (final Date date)
specifier|public
specifier|static
name|long
name|dateToLong
parameter_list|(
specifier|final
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
return|return
literal|0L
return|;
block|}
return|return
name|date
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/**    * Create the AWS credentials from the providers and the URI.    * @param binding Binding URI, may contain user:pass login details    * @param conf filesystem configuration    * @param fsURI fS URI âafter any login details have been stripped.    * @return a credentials provider list    * @throws IOException Problems loading the providers (including reading    * secrets from credential files).    */
DECL|method|createAWSCredentialProviderSet ( URI binding, Configuration conf, URI fsURI)
specifier|public
specifier|static
name|AWSCredentialProviderList
name|createAWSCredentialProviderSet
parameter_list|(
name|URI
name|binding
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|URI
name|fsURI
parameter_list|)
throws|throws
name|IOException
block|{
name|AWSCredentialProviderList
name|credentials
init|=
operator|new
name|AWSCredentialProviderList
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|awsClasses
decl_stmt|;
try|try
block|{
name|awsClasses
operator|=
name|conf
operator|.
name|getClasses
argument_list|(
name|AWS_CREDENTIALS_PROVIDER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|Throwable
name|c
init|=
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|?
name|e
operator|.
name|getCause
argument_list|()
else|:
name|e
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"From option "
operator|+
name|AWS_CREDENTIALS_PROVIDER
operator|+
literal|' '
operator|+
name|c
argument_list|,
name|c
argument_list|)
throw|;
block|}
if|if
condition|(
name|awsClasses
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|S3xLoginHelper
operator|.
name|Login
name|creds
init|=
name|getAWSAccessKeys
argument_list|(
name|binding
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|add
argument_list|(
operator|new
name|BasicAWSCredentialsProvider
argument_list|(
name|creds
operator|.
name|getUser
argument_list|()
argument_list|,
name|creds
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|add
argument_list|(
operator|new
name|EnvironmentVariableCredentialsProvider
argument_list|()
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|add
argument_list|(
operator|new
name|InstanceProfileCredentialsProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|aClass
range|:
name|awsClasses
control|)
block|{
name|credentials
operator|.
name|add
argument_list|(
name|createAWSCredentialProvider
argument_list|(
name|conf
argument_list|,
name|aClass
argument_list|,
name|fsURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|credentials
return|;
block|}
comment|/**    * Create an AWS credential provider.    * @param conf configuration    * @param credClass credential class    * @param uri URI of the FS    * @return the instantiated class    * @throws IOException on any instantiation failure.    */
DECL|method|createAWSCredentialProvider ( Configuration conf, Class<?> credClass, URI uri)
specifier|static
name|AWSCredentialsProvider
name|createAWSCredentialProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|credClass
parameter_list|,
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|AWSCredentialsProvider
name|credentials
decl_stmt|;
name|String
name|className
init|=
name|credClass
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|AWSCredentialsProvider
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|credClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Class "
operator|+
name|credClass
operator|+
literal|" "
operator|+
name|NOT_AWS_PROVIDER
argument_list|)
throw|;
block|}
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Credential provider class is {}"
argument_list|,
name|className
argument_list|)
expr_stmt|;
try|try
block|{
name|credentials
operator|=
operator|(
name|AWSCredentialsProvider
operator|)
name|credClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|URI
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
decl||
name|SecurityException
name|e
parameter_list|)
block|{
name|credentials
operator|=
operator|(
name|AWSCredentialsProvider
operator|)
name|credClass
operator|.
name|getDeclaredConstructor
argument_list|()
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
decl||
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s "
operator|+
name|CONSTRUCTOR_EXCEPTION
operator|+
literal|".  A class specified in %s must provide an accessible constructor "
operator|+
literal|"accepting URI and Configuration, or an accessible default "
operator|+
literal|"constructor."
argument_list|,
name|className
argument_list|,
name|AWS_CREDENTIALS_PROVIDER
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|className
operator|+
literal|" "
operator|+
name|INSTANTIATION_EXCEPTION
operator|+
literal|"."
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using {} for {}."
argument_list|,
name|credentials
argument_list|,
name|uri
argument_list|)
expr_stmt|;
return|return
name|credentials
return|;
block|}
comment|/**    * Return the access key and secret for S3 API use.    * Credentials may exist in configuration, within credential providers    * or indicated in the UserInfo of the name URI param.    * @param name the URI for which we need the access keys.    * @param conf the Configuration object to interrogate for keys.    * @return AWSAccessKeys    * @throws IOException problems retrieving passwords from KMS.    */
DECL|method|getAWSAccessKeys (URI name, Configuration conf)
specifier|public
specifier|static
name|S3xLoginHelper
operator|.
name|Login
name|getAWSAccessKeys
parameter_list|(
name|URI
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|S3xLoginHelper
operator|.
name|Login
name|login
init|=
name|S3xLoginHelper
operator|.
name|extractLoginDetailsWithWarnings
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Configuration
name|c
init|=
name|ProviderUtils
operator|.
name|excludeIncompatibleCredentialProviders
argument_list|(
name|conf
argument_list|,
name|S3AFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|accessKey
init|=
name|getPassword
argument_list|(
name|c
argument_list|,
name|ACCESS_KEY
argument_list|,
name|login
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|secretKey
init|=
name|getPassword
argument_list|(
name|c
argument_list|,
name|SECRET_KEY
argument_list|,
name|login
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|S3xLoginHelper
operator|.
name|Login
argument_list|(
name|accessKey
argument_list|,
name|secretKey
argument_list|)
return|;
block|}
comment|/**    * Get a password from a configuration, or, if a value is passed in,    * pick that up instead.    * @param conf configuration    * @param key key to look up    * @param val current value: if non empty this is used instead of    * querying the configuration.    * @return a password or "".    * @throws IOException on any problem    */
DECL|method|getPassword (Configuration conf, String key, String val)
specifier|static
name|String
name|getPassword
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|val
argument_list|)
condition|?
name|lookupPassword
argument_list|(
name|conf
argument_list|,
name|key
argument_list|,
literal|""
argument_list|)
else|:
name|val
return|;
block|}
comment|/**    * Get a password from a configuration/configured credential providers.    * @param conf configuration    * @param key key to look up    * @param defVal value to return if there is no password    * @return a password or the value in {@code defVal}    * @throws IOException on any problem    */
DECL|method|lookupPassword (Configuration conf, String key, String defVal)
specifier|static
name|String
name|lookupPassword
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|defVal
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|char
index|[]
name|pass
init|=
name|conf
operator|.
name|getPassword
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|pass
operator|!=
literal|null
condition|?
operator|new
name|String
argument_list|(
name|pass
argument_list|)
operator|.
name|trim
argument_list|()
else|:
name|defVal
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find password option "
operator|+
name|key
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/**    * String information about a summary entry for debug messages.    * @param summary summary object    * @return string value    */
DECL|method|stringify (S3ObjectSummary summary)
specifier|public
specifier|static
name|String
name|stringify
parameter_list|(
name|S3ObjectSummary
name|summary
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|summary
operator|.
name|getKey
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|100
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|summary
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"size="
argument_list|)
operator|.
name|append
argument_list|(
name|summary
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get a integer option>= the minimum allowed value.    * @param conf configuration    * @param key key to look up    * @param defVal default value    * @param min minimum value    * @return the value    * @throws IllegalArgumentException if the value is below the minimum    */
DECL|method|intOption (Configuration conf, String key, int defVal, int min)
specifier|static
name|int
name|intOption
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|defVal
parameter_list|,
name|int
name|min
parameter_list|)
block|{
name|int
name|v
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|key
argument_list|,
name|defVal
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|v
operator|>=
name|min
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Value of %s: %d is below the minimum value %d"
argument_list|,
name|key
argument_list|,
name|v
argument_list|,
name|min
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/**    * Get a long option>= the minimum allowed value.    * @param conf configuration    * @param key key to look up    * @param defVal default value    * @param min minimum value    * @return the value    * @throws IllegalArgumentException if the value is below the minimum    */
DECL|method|longOption (Configuration conf, String key, long defVal, long min)
specifier|static
name|long
name|longOption
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|defVal
parameter_list|,
name|long
name|min
parameter_list|)
block|{
name|long
name|v
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|key
argument_list|,
name|defVal
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|v
operator|>=
name|min
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Value of %s: %d is below the minimum value %d"
argument_list|,
name|key
argument_list|,
name|v
argument_list|,
name|min
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/**    * Get a size property from the configuration: this property must    * be at least equal to {@link Constants#MULTIPART_MIN_SIZE}.    * If it is too small, it is rounded up to that minimum, and a warning    * printed.    * @param conf configuration    * @param property property name    * @param defVal default value    * @return the value, guaranteed to be above the minimum size    */
DECL|method|getMultipartSizeProperty (Configuration conf, String property, long defVal)
specifier|public
specifier|static
name|long
name|getMultipartSizeProperty
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|property
parameter_list|,
name|long
name|defVal
parameter_list|)
block|{
name|long
name|partSize
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|property
argument_list|,
name|defVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|partSize
operator|<
name|MULTIPART_MIN_SIZE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} must be at least 5 MB; configured value is {}"
argument_list|,
name|property
argument_list|,
name|partSize
argument_list|)
expr_stmt|;
name|partSize
operator|=
name|MULTIPART_MIN_SIZE
expr_stmt|;
block|}
return|return
name|partSize
return|;
block|}
comment|/**    * Ensure that the long value is in the range of an integer.    * @param name property name for error messages    * @param size original size    * @return the size, guaranteed to be less than or equal to the max    * value of an integer.    */
DECL|method|ensureOutputParameterInRange (String name, long size)
specifier|public
specifier|static
name|int
name|ensureOutputParameterInRange
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"s3a: {} capped to ~2.14GB"
operator|+
literal|" (maximum allowed size with current output mechanism)"
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|size
return|;
block|}
block|}
block|}
end_class

end_unit

