begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.header
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|header
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
import|;
end_import

begin_comment
comment|/**  * S3 Authorization header.  * Ref: https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-auth-using  * -authorization-header.html  */
end_comment

begin_class
DECL|class|AuthorizationHeaderV4
specifier|public
class|class
name|AuthorizationHeaderV4
block|{
DECL|field|CREDENTIAL
specifier|private
specifier|final
specifier|static
name|String
name|CREDENTIAL
init|=
literal|"Credential="
decl_stmt|;
DECL|field|SIGNEDHEADERS
specifier|private
specifier|final
specifier|static
name|String
name|SIGNEDHEADERS
init|=
literal|"SignedHeaders="
decl_stmt|;
DECL|field|SIGNATURE
specifier|private
specifier|final
specifier|static
name|String
name|SIGNATURE
init|=
literal|"Signature="
decl_stmt|;
DECL|field|authHeader
specifier|private
name|String
name|authHeader
decl_stmt|;
DECL|field|algorithm
specifier|private
name|String
name|algorithm
decl_stmt|;
DECL|field|credential
specifier|private
name|String
name|credential
decl_stmt|;
DECL|field|signedHeaders
specifier|private
name|String
name|signedHeaders
decl_stmt|;
DECL|field|signature
specifier|private
name|String
name|signature
decl_stmt|;
DECL|field|credentialObj
specifier|private
name|Credential
name|credentialObj
decl_stmt|;
comment|/**    * Construct AuthorizationHeader object.    * @param header    */
DECL|method|AuthorizationHeaderV4 (String header)
specifier|public
name|AuthorizationHeaderV4
parameter_list|(
name|String
name|header
parameter_list|)
throws|throws
name|OS3Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|this
operator|.
name|authHeader
operator|=
name|header
expr_stmt|;
name|parseAuthHeader
argument_list|()
expr_stmt|;
block|}
comment|/**    * This method parses authorization header.    *    *  Authorization Header sample:    *  AWS4-HMAC-SHA256 Credential=AKIAJWFJK62WUTKNFJJA/20181009/us-east-1/s3    *  /aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date,    * Signature=db81b057718d7c1b3b8dffa29933099551c51d787b3b13b9e0f9ebed45982bf2    * @throws OS3Exception    */
DECL|method|parseAuthHeader ()
specifier|public
name|void
name|parseAuthHeader
parameter_list|()
throws|throws
name|OS3Exception
block|{
name|String
index|[]
name|split
init|=
name|authHeader
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|.
name|length
operator|!=
literal|4
condition|)
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|MALFORMED_HEADER
argument_list|,
name|authHeader
argument_list|)
throw|;
block|}
name|algorithm
operator|=
name|split
index|[
literal|0
index|]
expr_stmt|;
name|credential
operator|=
name|split
index|[
literal|1
index|]
expr_stmt|;
name|signedHeaders
operator|=
name|split
index|[
literal|2
index|]
expr_stmt|;
name|signature
operator|=
name|split
index|[
literal|3
index|]
expr_stmt|;
if|if
condition|(
name|credential
operator|.
name|startsWith
argument_list|(
name|CREDENTIAL
argument_list|)
condition|)
block|{
name|credential
operator|=
name|credential
operator|.
name|substring
argument_list|(
name|CREDENTIAL
operator|.
name|length
argument_list|()
argument_list|,
name|credential
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|MALFORMED_HEADER
argument_list|,
name|authHeader
argument_list|)
throw|;
block|}
if|if
condition|(
name|signedHeaders
operator|.
name|startsWith
argument_list|(
name|SIGNEDHEADERS
argument_list|)
condition|)
block|{
name|signedHeaders
operator|=
name|signedHeaders
operator|.
name|substring
argument_list|(
name|SIGNEDHEADERS
operator|.
name|length
argument_list|()
argument_list|,
name|signedHeaders
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|MALFORMED_HEADER
argument_list|,
name|authHeader
argument_list|)
throw|;
block|}
if|if
condition|(
name|signature
operator|.
name|startsWith
argument_list|(
name|SIGNATURE
argument_list|)
condition|)
block|{
name|signature
operator|=
name|signature
operator|.
name|substring
argument_list|(
name|SIGNATURE
operator|.
name|length
argument_list|()
argument_list|,
name|signature
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|MALFORMED_HEADER
argument_list|,
name|authHeader
argument_list|)
throw|;
block|}
comment|// Parse credential. Other parts of header are not validated yet. When
comment|// security comes, it needs to be completed.
name|credentialObj
operator|=
operator|new
name|Credential
argument_list|(
name|credential
argument_list|)
expr_stmt|;
block|}
DECL|method|getAuthHeader ()
specifier|public
name|String
name|getAuthHeader
parameter_list|()
block|{
return|return
name|authHeader
return|;
block|}
DECL|method|getAlgorithm ()
specifier|public
name|String
name|getAlgorithm
parameter_list|()
block|{
return|return
name|algorithm
return|;
block|}
DECL|method|getCredential ()
specifier|public
name|String
name|getCredential
parameter_list|()
block|{
return|return
name|credential
return|;
block|}
DECL|method|getSignedHeaders ()
specifier|public
name|String
name|getSignedHeaders
parameter_list|()
block|{
return|return
name|signedHeaders
return|;
block|}
DECL|method|getSignature ()
specifier|public
name|String
name|getSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
DECL|method|getAccessKeyID ()
specifier|public
name|String
name|getAccessKeyID
parameter_list|()
block|{
return|return
name|credentialObj
operator|.
name|getAccessKeyID
argument_list|()
return|;
block|}
DECL|method|getDate ()
specifier|public
name|String
name|getDate
parameter_list|()
block|{
return|return
name|credentialObj
operator|.
name|getDate
argument_list|()
return|;
block|}
DECL|method|getAwsRegion ()
specifier|public
name|String
name|getAwsRegion
parameter_list|()
block|{
return|return
name|credentialObj
operator|.
name|getAwsRegion
argument_list|()
return|;
block|}
DECL|method|getAwsService ()
specifier|public
name|String
name|getAwsService
parameter_list|()
block|{
return|return
name|credentialObj
operator|.
name|getAwsService
argument_list|()
return|;
block|}
DECL|method|getAwsRequest ()
specifier|public
name|String
name|getAwsRequest
parameter_list|()
block|{
return|return
name|credentialObj
operator|.
name|getAwsRequest
argument_list|()
return|;
block|}
block|}
end_class

end_unit

