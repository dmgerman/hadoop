begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
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
name|util
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
name|kerby
operator|.
name|util
operator|.
name|Hex
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
name|javax
operator|.
name|crypto
operator|.
name|Mac
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
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

begin_comment
comment|/**  * AWS v4 authentication payload validator. For more details refer to AWS  * documentation https://docs.aws.amazon.com/general/latest/gr/  * sigv4-create-canonical-request.html.  **/
end_comment

begin_class
DECL|class|AWSV4AuthValidator
specifier|final
class|class
name|AWSV4AuthValidator
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AWSV4AuthValidator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HMAC_SHA256_ALGORITHM
specifier|private
specifier|static
specifier|final
name|String
name|HMAC_SHA256_ALGORITHM
init|=
literal|"HmacSHA256"
decl_stmt|;
DECL|field|UTF_8
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF_8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"utf-8"
argument_list|)
decl_stmt|;
DECL|method|AWSV4AuthValidator ()
specifier|private
name|AWSV4AuthValidator
parameter_list|()
block|{   }
DECL|method|urlDecode (String str)
specifier|private
specifier|static
name|String
name|urlDecode
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
return|return
name|URLDecoder
operator|.
name|decode
argument_list|(
name|str
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|hash (String payload)
specifier|public
specifier|static
name|String
name|hash
parameter_list|(
name|String
name|payload
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-256"
argument_list|)
decl_stmt|;
name|md
operator|.
name|update
argument_list|(
name|payload
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%064x"
argument_list|,
operator|new
name|java
operator|.
name|math
operator|.
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|md
operator|.
name|digest
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|sign (byte[] key, String msg)
specifier|private
specifier|static
name|byte
index|[]
name|sign
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
try|try
block|{
name|SecretKeySpec
name|signingKey
init|=
operator|new
name|SecretKeySpec
argument_list|(
name|key
argument_list|,
name|HMAC_SHA256_ALGORITHM
argument_list|)
decl_stmt|;
name|Mac
name|mac
init|=
name|Mac
operator|.
name|getInstance
argument_list|(
name|HMAC_SHA256_ALGORITHM
argument_list|)
decl_stmt|;
name|mac
operator|.
name|init
argument_list|(
name|signingKey
argument_list|)
expr_stmt|;
return|return
name|mac
operator|.
name|doFinal
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|gse
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|gse
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns signing key.    *    * @param key    * @param strToSign    *    * SignatureKey = HMAC-SHA256(HMAC-SHA256(HMAC-SHA256(HMAC-SHA256("AWS4" +    * "<YourSecretAccessKey>","20130524"),"us-east-1"),"s3"),"aws4_request")    *    * For more details refer to AWS documentation: https://docs.aws.amazon    * .com/AmazonS3/latest/API/sig-v4-header-based-auth.html    *    * */
DECL|method|getSigningKey (String key, String strToSign)
specifier|private
specifier|static
name|byte
index|[]
name|getSigningKey
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|strToSign
parameter_list|)
block|{
name|String
index|[]
name|signData
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|StringUtils
operator|.
name|split
argument_list|(
name|strToSign
argument_list|,
literal|'\n'
argument_list|)
index|[
literal|2
index|]
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|dateStamp
init|=
name|signData
index|[
literal|0
index|]
decl_stmt|;
name|String
name|regionName
init|=
name|signData
index|[
literal|1
index|]
decl_stmt|;
name|String
name|serviceName
init|=
name|signData
index|[
literal|2
index|]
decl_stmt|;
name|byte
index|[]
name|kDate
init|=
name|sign
argument_list|(
operator|(
literal|"AWS4"
operator|+
name|key
operator|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
name|dateStamp
argument_list|)
decl_stmt|;
name|byte
index|[]
name|kRegion
init|=
name|sign
argument_list|(
name|kDate
argument_list|,
name|regionName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|kService
init|=
name|sign
argument_list|(
name|kRegion
argument_list|,
name|serviceName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|kSigning
init|=
name|sign
argument_list|(
name|kService
argument_list|,
literal|"aws4_request"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|Hex
operator|.
name|encode
argument_list|(
name|kSigning
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|kSigning
return|;
block|}
comment|/**    * Validate request by comparing Signature from request. Returns true if    * aws request is legit else returns false.    * Signature = HEX(HMAC_SHA256(key, String to Sign))    *    * For more details refer to AWS documentation: https://docs.aws.amazon.com    * /AmazonS3/latest/API/sigv4-streaming.html    */
DECL|method|validateRequest (String strToSign, String signature, String userKey)
specifier|public
specifier|static
name|boolean
name|validateRequest
parameter_list|(
name|String
name|strToSign
parameter_list|,
name|String
name|signature
parameter_list|,
name|String
name|userKey
parameter_list|)
block|{
name|String
name|expectedSignature
init|=
name|Hex
operator|.
name|encode
argument_list|(
name|sign
argument_list|(
name|getSigningKey
argument_list|(
name|userKey
argument_list|,
name|strToSign
argument_list|)
argument_list|,
name|strToSign
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expectedSignature
operator|.
name|equals
argument_list|(
name|signature
argument_list|)
return|;
block|}
block|}
end_class

end_unit

