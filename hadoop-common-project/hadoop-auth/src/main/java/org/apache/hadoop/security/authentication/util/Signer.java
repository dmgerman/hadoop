begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|util
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
comment|/**  * Signs strings and verifies signed strings using a SHA digest.  */
end_comment

begin_class
DECL|class|Signer
specifier|public
class|class
name|Signer
block|{
DECL|field|SIGNATURE
specifier|private
specifier|static
specifier|final
name|String
name|SIGNATURE
init|=
literal|"&s="
decl_stmt|;
DECL|field|secretProvider
specifier|private
name|SignerSecretProvider
name|secretProvider
decl_stmt|;
comment|/**    * Creates a Signer instance using the specified SignerSecretProvider.  The    * SignerSecretProvider should already be initialized.    *    * @param secretProvider The SignerSecretProvider to use    */
DECL|method|Signer (SignerSecretProvider secretProvider)
specifier|public
name|Signer
parameter_list|(
name|SignerSecretProvider
name|secretProvider
parameter_list|)
block|{
if|if
condition|(
name|secretProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"secretProvider cannot be NULL"
argument_list|)
throw|;
block|}
name|this
operator|.
name|secretProvider
operator|=
name|secretProvider
expr_stmt|;
block|}
comment|/**    * Returns a signed string.    *    * @param str string to sign.    *    * @return the signed string.    */
DECL|method|sign (String str)
specifier|public
specifier|synchronized
name|String
name|sign
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
operator|||
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NULL or empty string to sign"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|secret
init|=
name|secretProvider
operator|.
name|getCurrentSecret
argument_list|()
decl_stmt|;
name|String
name|signature
init|=
name|computeSignature
argument_list|(
name|secret
argument_list|,
name|str
argument_list|)
decl_stmt|;
return|return
name|str
operator|+
name|SIGNATURE
operator|+
name|signature
return|;
block|}
comment|/**    * Verifies a signed string and extracts the original string.    *    * @param signedStr the signed string to verify and extract.    *    * @return the extracted original string.    *    * @throws SignerException thrown if the given string is not a signed string or if the signature is invalid.    */
DECL|method|verifyAndExtract (String signedStr)
specifier|public
name|String
name|verifyAndExtract
parameter_list|(
name|String
name|signedStr
parameter_list|)
throws|throws
name|SignerException
block|{
name|int
name|index
init|=
name|signedStr
operator|.
name|lastIndexOf
argument_list|(
name|SIGNATURE
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|SignerException
argument_list|(
literal|"Invalid signed text: "
operator|+
name|signedStr
argument_list|)
throw|;
block|}
name|String
name|originalSignature
init|=
name|signedStr
operator|.
name|substring
argument_list|(
name|index
operator|+
name|SIGNATURE
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|rawValue
init|=
name|signedStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|checkSignatures
argument_list|(
name|rawValue
argument_list|,
name|originalSignature
argument_list|)
expr_stmt|;
return|return
name|rawValue
return|;
block|}
comment|/**    * Returns then signature of a string.    *    * @param secret The secret to use    * @param str string to sign.    *    * @return the signature for the string.    */
DECL|method|computeSignature (byte[] secret, String str)
specifier|protected
name|String
name|computeSignature
parameter_list|(
name|byte
index|[]
name|secret
parameter_list|,
name|String
name|str
parameter_list|)
block|{
try|try
block|{
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA"
argument_list|)
decl_stmt|;
name|md
operator|.
name|update
argument_list|(
name|str
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|secret
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest
init|=
name|md
operator|.
name|digest
argument_list|()
decl_stmt|;
return|return
operator|new
name|Base64
argument_list|(
literal|0
argument_list|)
operator|.
name|encodeToString
argument_list|(
name|digest
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"It should not happen, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|checkSignatures (String rawValue, String originalSignature)
specifier|protected
name|void
name|checkSignatures
parameter_list|(
name|String
name|rawValue
parameter_list|,
name|String
name|originalSignature
parameter_list|)
throws|throws
name|SignerException
block|{
name|boolean
name|isValid
init|=
literal|false
decl_stmt|;
name|byte
index|[]
index|[]
name|secrets
init|=
name|secretProvider
operator|.
name|getAllSecrets
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|secrets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|secret
init|=
name|secrets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|secret
operator|!=
literal|null
condition|)
block|{
name|String
name|currentSignature
init|=
name|computeSignature
argument_list|(
name|secret
argument_list|,
name|rawValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|originalSignature
operator|.
name|equals
argument_list|(
name|currentSignature
argument_list|)
condition|)
block|{
name|isValid
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|isValid
condition|)
block|{
throw|throw
operator|new
name|SignerException
argument_list|(
literal|"Invalid signature"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

