begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|nio
operator|.
name|ByteBuffer
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|crypto
operator|.
name|CryptoCodec
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
name|crypto
operator|.
name|CryptoInputStream
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|crypto
operator|.
name|CryptoFSDataInputStream
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
name|crypto
operator|.
name|CryptoFSDataOutputStream
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
name|IOUtils
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|security
operator|.
name|TokenCache
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
name|UserGroupInformation
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
name|LimitInputStream
import|;
end_import

begin_comment
comment|/**  * This class provides utilities to make it easier to work with Cryptographic  * Streams. Specifically for dealing with encrypting intermediate data such  * MapReduce spill files.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CryptoUtils
specifier|public
class|class
name|CryptoUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CryptoUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|isShuffleEncrypted (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isShuffleEncrypted
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MR_ENCRYPTED_INTERMEDIATE_DATA
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_ENCRYPTED_INTERMEDIATE_DATA
argument_list|)
return|;
block|}
comment|/**    * This method creates and initializes an IV (Initialization Vector)    *     * @param conf    * @return byte[]    * @throws IOException    */
DECL|method|createIV (Configuration conf)
specifier|public
specifier|static
name|byte
index|[]
name|createIV
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|CryptoCodec
name|cryptoCodec
init|=
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|isShuffleEncrypted
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|byte
index|[]
name|iv
init|=
operator|new
name|byte
index|[
name|cryptoCodec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
index|]
decl_stmt|;
name|cryptoCodec
operator|.
name|generateSecureRandom
argument_list|(
name|iv
argument_list|)
expr_stmt|;
return|return
name|iv
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|cryptoPadding (Configuration conf)
specifier|public
specifier|static
name|int
name|cryptoPadding
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Sizeof(IV) + long(start-offset)
return|return
name|isShuffleEncrypted
argument_list|(
name|conf
argument_list|)
condition|?
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
operator|+
literal|8
else|:
literal|0
return|;
block|}
DECL|method|getEncryptionKey ()
specifier|private
specifier|static
name|byte
index|[]
name|getEncryptionKey
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|TokenCache
operator|.
name|getShuffleSecretKey
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getBufferSize (Configuration conf)
specifier|private
specifier|static
name|int
name|getBufferSize
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
name|MRJobConfig
operator|.
name|MR_ENCRYPTED_INTERMEDIATE_DATA_BUFFER_KB
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_ENCRYPTED_INTERMEDIATE_DATA_BUFFER_KB
argument_list|)
operator|*
literal|1024
return|;
block|}
comment|/**    * Wraps a given FSDataOutputStream with a CryptoOutputStream. The size of the    * data buffer required for the stream is specified by the    * "mapreduce.job.encrypted-intermediate-data.buffer.kb" Job configuration    * variable.    *     * @param conf    * @param out    * @return FSDataOutputStream    * @throws IOException    */
DECL|method|wrapIfNecessary (Configuration conf, FSDataOutputStream out)
specifier|public
specifier|static
name|FSDataOutputStream
name|wrapIfNecessary
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSDataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isShuffleEncrypted
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|8
argument_list|)
operator|.
name|putLong
argument_list|(
name|out
operator|.
name|getPos
argument_list|()
argument_list|)
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|iv
init|=
name|createIV
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|iv
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"IV written to Stream ["
operator|+
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|iv
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CryptoFSDataOutputStream
argument_list|(
name|out
argument_list|,
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
argument_list|,
name|getBufferSize
argument_list|(
name|conf
argument_list|)
argument_list|,
name|getEncryptionKey
argument_list|()
argument_list|,
name|iv
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|out
return|;
block|}
block|}
comment|/**    * Wraps a given InputStream with a CryptoInputStream. The size of the data    * buffer required for the stream is specified by the    * "mapreduce.job.encrypted-intermediate-data.buffer.kb" Job configuration    * variable.    *     * If the value of 'length' is> -1, The InputStream is additionally wrapped    * in a LimitInputStream. CryptoStreams are late buffering in nature. This    * means they will always try to read ahead if they can. The LimitInputStream    * will ensure that the CryptoStream does not read past the provided length    * from the given Input Stream.    *     * @param conf    * @param in    * @param length    * @return InputStream    * @throws IOException    */
DECL|method|wrapIfNecessary (Configuration conf, InputStream in, long length)
specifier|public
specifier|static
name|InputStream
name|wrapIfNecessary
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InputStream
name|in
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isShuffleEncrypted
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|int
name|bufferSize
init|=
name|getBufferSize
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|>
operator|-
literal|1
condition|)
block|{
name|in
operator|=
operator|new
name|LimitInputStream
argument_list|(
name|in
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|offsetArray
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|offsetArray
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|long
name|offset
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|offsetArray
argument_list|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|CryptoCodec
name|cryptoCodec
init|=
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|byte
index|[]
name|iv
init|=
operator|new
name|byte
index|[
name|cryptoCodec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|iv
argument_list|,
literal|0
argument_list|,
name|cryptoCodec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"IV read from ["
operator|+
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|iv
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CryptoInputStream
argument_list|(
name|in
argument_list|,
name|cryptoCodec
argument_list|,
name|bufferSize
argument_list|,
name|getEncryptionKey
argument_list|()
argument_list|,
name|iv
argument_list|,
name|offset
operator|+
name|cryptoPadding
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
return|;
block|}
block|}
comment|/**    * Wraps a given FSDataInputStream with a CryptoInputStream. The size of the    * data buffer required for the stream is specified by the    * "mapreduce.job.encrypted-intermediate-data.buffer.kb" Job configuration    * variable.    *     * @param conf    * @param in    * @return FSDataInputStream    * @throws IOException    */
DECL|method|wrapIfNecessary (Configuration conf, FSDataInputStream in)
specifier|public
specifier|static
name|FSDataInputStream
name|wrapIfNecessary
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSDataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isShuffleEncrypted
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|CryptoCodec
name|cryptoCodec
init|=
name|CryptoCodec
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|bufferSize
init|=
name|getBufferSize
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Not going to be used... but still has to be read...
comment|// Since the O/P stream always writes it..
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
operator|new
name|byte
index|[
literal|8
index|]
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|byte
index|[]
name|iv
init|=
operator|new
name|byte
index|[
name|cryptoCodec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|iv
argument_list|,
literal|0
argument_list|,
name|cryptoCodec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"IV read from Stream ["
operator|+
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|iv
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CryptoFSDataInputStream
argument_list|(
name|in
argument_list|,
name|cryptoCodec
argument_list|,
name|bufferSize
argument_list|,
name|getEncryptionKey
argument_list|()
argument_list|,
name|iv
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
return|;
block|}
block|}
block|}
end_class

end_unit

