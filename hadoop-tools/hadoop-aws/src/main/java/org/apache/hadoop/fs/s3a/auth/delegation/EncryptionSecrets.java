begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
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
operator|.
name|auth
operator|.
name|delegation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|lang3
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
name|fs
operator|.
name|s3a
operator|.
name|S3AEncryptionMethods
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
name|io
operator|.
name|LongWritable
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * Encryption options in a form which can serialized or marshalled as a hadoop  * Writeable.  *  * Maintainers: For security reasons, don't print any of this.  *  * Note this design marshalls/unmarshalls its serialVersionUID  * in its writable, which is used to compare versions.  *  *<i>Important.</i>  * If the wire format is ever changed incompatibly,  * update the serial version UID to ensure that older clients get safely  * rejected.  *  *<i>Important</i>  * Do not import any AWS SDK classes, directly or indirectly.  * This is to ensure that S3A Token identifiers can be unmarshalled even  * without that SDK.  */
end_comment

begin_class
DECL|class|EncryptionSecrets
specifier|public
class|class
name|EncryptionSecrets
implements|implements
name|Writable
implements|,
name|Serializable
block|{
DECL|field|MAX_SECRET_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|MAX_SECRET_LENGTH
init|=
literal|2048
decl_stmt|;
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1208329045511296375L
decl_stmt|;
comment|/**    * Encryption algorithm to use: must match one in    * {@link S3AEncryptionMethods}.    */
DECL|field|encryptionAlgorithm
specifier|private
name|String
name|encryptionAlgorithm
init|=
literal|""
decl_stmt|;
comment|/**    * Encryption key: possibly sensitive information.    */
DECL|field|encryptionKey
specifier|private
name|String
name|encryptionKey
init|=
literal|""
decl_stmt|;
comment|/**    * This field isn't serialized/marshalled; it is rebuilt from the    * encryptionAlgorithm field.    */
DECL|field|encryptionMethod
specifier|private
specifier|transient
name|S3AEncryptionMethods
name|encryptionMethod
init|=
name|S3AEncryptionMethods
operator|.
name|NONE
decl_stmt|;
comment|/**    * Empty constructor, for use in marshalling.    */
DECL|method|EncryptionSecrets ()
specifier|public
name|EncryptionSecrets
parameter_list|()
block|{   }
comment|/**    * Create a pair of secrets.    * @param encryptionAlgorithm algorithm enumeration.    * @param encryptionKey key/key reference.    * @throws IOException failure to initialize.    */
DECL|method|EncryptionSecrets (final S3AEncryptionMethods encryptionAlgorithm, final String encryptionKey)
specifier|public
name|EncryptionSecrets
parameter_list|(
specifier|final
name|S3AEncryptionMethods
name|encryptionAlgorithm
parameter_list|,
specifier|final
name|String
name|encryptionKey
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|encryptionAlgorithm
operator|.
name|getMethod
argument_list|()
argument_list|,
name|encryptionKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a pair of secrets.    * @param encryptionAlgorithm algorithm name    * @param encryptionKey key/key reference.    * @throws IOException failure to initialize.    */
DECL|method|EncryptionSecrets (final String encryptionAlgorithm, final String encryptionKey)
specifier|public
name|EncryptionSecrets
parameter_list|(
specifier|final
name|String
name|encryptionAlgorithm
parameter_list|,
specifier|final
name|String
name|encryptionKey
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|encryptionAlgorithm
operator|=
name|encryptionAlgorithm
expr_stmt|;
name|this
operator|.
name|encryptionKey
operator|=
name|encryptionKey
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write out the encryption secrets.    * @param out {@code DataOutput} to serialize this object into.    * @throws IOException IO failure    */
annotation|@
name|Override
DECL|method|write (final DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|LongWritable
argument_list|(
name|serialVersionUID
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|encryptionAlgorithm
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|encryptionKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read in from the writable stream.    * After reading, call {@link #init()}.    * @param in {@code DataInput} to deserialize this object from.    * @throws IOException failure to read/validate data.    */
annotation|@
name|Override
DECL|method|readFields (final DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
specifier|final
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LongWritable
name|version
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
name|version
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|.
name|get
argument_list|()
operator|!=
name|serialVersionUID
condition|)
block|{
throw|throw
operator|new
name|DelegationTokenIOException
argument_list|(
literal|"Incompatible EncryptionSecrets version"
argument_list|)
throw|;
block|}
name|encryptionAlgorithm
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|MAX_SECRET_LENGTH
argument_list|)
expr_stmt|;
name|encryptionKey
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|,
name|MAX_SECRET_LENGTH
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * For java serialization: read and then call {@link #init()}.    * @param in input    * @throws IOException IO problem    * @throws ClassNotFoundException problem loading inner class.    */
DECL|method|readObject (ObjectInputStream in)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|in
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Init all state, including after any read.    * @throws IOException error rebuilding state.    */
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|encryptionMethod
operator|=
name|S3AEncryptionMethods
operator|.
name|getMethod
argument_list|(
name|encryptionAlgorithm
argument_list|)
expr_stmt|;
block|}
DECL|method|getEncryptionAlgorithm ()
specifier|public
name|String
name|getEncryptionAlgorithm
parameter_list|()
block|{
return|return
name|encryptionAlgorithm
return|;
block|}
DECL|method|getEncryptionKey ()
specifier|public
name|String
name|getEncryptionKey
parameter_list|()
block|{
return|return
name|encryptionKey
return|;
block|}
comment|/**    * Does this instance have encryption options?    * That is: is the algorithm non-null.    * @return true if there's an encryption algorithm.    */
DECL|method|hasEncryptionAlgorithm ()
specifier|public
name|boolean
name|hasEncryptionAlgorithm
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|encryptionAlgorithm
argument_list|)
return|;
block|}
comment|/**    * Does this instance have an encryption key?    * @return true if there's an encryption key.    */
DECL|method|hasEncryptionKey ()
specifier|public
name|boolean
name|hasEncryptionKey
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|encryptionKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (final Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|EncryptionSecrets
name|that
init|=
operator|(
name|EncryptionSecrets
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|encryptionAlgorithm
argument_list|,
name|that
operator|.
name|encryptionAlgorithm
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|encryptionKey
argument_list|,
name|that
operator|.
name|encryptionKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|encryptionAlgorithm
argument_list|,
name|encryptionKey
argument_list|)
return|;
block|}
comment|/**    * Get the encryption method.    * @return the encryption method    */
DECL|method|getEncryptionMethod ()
specifier|public
name|S3AEncryptionMethods
name|getEncryptionMethod
parameter_list|()
block|{
return|return
name|encryptionMethod
return|;
block|}
comment|/**    * String function returns the encryption mode but not any other    * secrets.    * @return a string safe for logging.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|S3AEncryptionMethods
operator|.
name|NONE
operator|.
name|equals
argument_list|(
name|encryptionMethod
argument_list|)
condition|?
literal|"(no encryption)"
else|:
name|encryptionMethod
operator|.
name|getMethod
argument_list|()
return|;
block|}
block|}
end_class

end_unit

