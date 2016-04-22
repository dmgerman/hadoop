begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

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
name|DataInputStream
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
name|DataOutputStream
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
name|io
operator|.
name|FileInputStream
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|io
operator|.
name|Charsets
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
name|WritableUtils
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_comment
comment|/**  * A class that provides the facilities of reading and writing   * secret keys and Tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Credentials
specifier|public
class|class
name|Credentials
implements|implements
name|Writable
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
name|Credentials
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|secretKeysMap
specifier|private
name|Map
argument_list|<
name|Text
argument_list|,
name|byte
index|[]
argument_list|>
name|secretKeysMap
init|=
operator|new
name|HashMap
argument_list|<
name|Text
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tokenMap
specifier|private
name|Map
argument_list|<
name|Text
argument_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tokenMap
init|=
operator|new
name|HashMap
argument_list|<
name|Text
argument_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Create an empty credentials instance    */
DECL|method|Credentials ()
specifier|public
name|Credentials
parameter_list|()
block|{   }
comment|/**    * Create a copy of the given credentials    * @param credentials to copy    */
DECL|method|Credentials (Credentials credentials)
specifier|public
name|Credentials
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
name|this
operator|.
name|addAll
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the Token object for the alias    * @param alias the alias for the Token    * @return token for this alias    */
DECL|method|getToken (Text alias)
specifier|public
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|getToken
parameter_list|(
name|Text
name|alias
parameter_list|)
block|{
return|return
name|tokenMap
operator|.
name|get
argument_list|(
name|alias
argument_list|)
return|;
block|}
comment|/**    * Add a token in the storage (in memory)    * @param alias the alias for the key    * @param t the token object    */
DECL|method|addToken (Text alias, Token<? extends TokenIdentifier> t)
specifier|public
name|void
name|addToken
parameter_list|(
name|Text
name|alias
parameter_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|tokenMap
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Null token ignored for "
operator|+
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return all the tokens in the in-memory map    */
DECL|method|getAllTokens ()
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|getAllTokens
parameter_list|()
block|{
return|return
name|tokenMap
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * @return number of Tokens in the in-memory map    */
DECL|method|numberOfTokens ()
specifier|public
name|int
name|numberOfTokens
parameter_list|()
block|{
return|return
name|tokenMap
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns the key bytes for the alias    * @param alias the alias for the key    * @return key for this alias    */
DECL|method|getSecretKey (Text alias)
specifier|public
name|byte
index|[]
name|getSecretKey
parameter_list|(
name|Text
name|alias
parameter_list|)
block|{
return|return
name|secretKeysMap
operator|.
name|get
argument_list|(
name|alias
argument_list|)
return|;
block|}
comment|/**    * @return number of keys in the in-memory map    */
DECL|method|numberOfSecretKeys ()
specifier|public
name|int
name|numberOfSecretKeys
parameter_list|()
block|{
return|return
name|secretKeysMap
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Set the key for an alias    * @param alias the alias for the key    * @param key the key bytes    */
DECL|method|addSecretKey (Text alias, byte[] key)
specifier|public
name|void
name|addSecretKey
parameter_list|(
name|Text
name|alias
parameter_list|,
name|byte
index|[]
name|key
parameter_list|)
block|{
name|secretKeysMap
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove the key for a given alias.    * @param alias the alias for the key    */
DECL|method|removeSecretKey (Text alias)
specifier|public
name|void
name|removeSecretKey
parameter_list|(
name|Text
name|alias
parameter_list|)
block|{
name|secretKeysMap
operator|.
name|remove
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return all the secret key entries in the in-memory map    */
DECL|method|getAllSecretKeys ()
specifier|public
name|List
argument_list|<
name|Text
argument_list|>
name|getAllSecretKeys
parameter_list|()
block|{
name|List
argument_list|<
name|Text
argument_list|>
name|list
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|<
name|Text
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|secretKeysMap
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**    * Convenience method for reading a token storage file, and loading the Tokens    * therein in the passed UGI    * @param filename    * @param conf    * @throws IOException    */
DECL|method|readTokenStorageFile (Path filename, Configuration conf)
specifier|public
specifier|static
name|Credentials
name|readTokenStorageFile
parameter_list|(
name|Path
name|filename
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
try|try
block|{
name|in
operator|=
name|filename
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|open
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|credentials
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
literal|"Exception reading "
operator|+
name|filename
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convenience method for reading a token storage file, and loading the Tokens    * therein in the passed UGI    * @param filename    * @param conf    * @throws IOException    */
DECL|method|readTokenStorageFile (File filename, Configuration conf)
specifier|public
specifier|static
name|Credentials
name|readTokenStorageFile
parameter_list|(
name|File
name|filename
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|filename
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|credentials
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
literal|"Exception reading "
operator|+
name|filename
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convenience method for reading a token storage file directly from a     * datainputstream    */
DECL|method|readTokenStorageStream (DataInputStream in)
specifier|public
name|void
name|readTokenStorageStream
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|magic
init|=
operator|new
name|byte
index|[
name|TOKEN_STORAGE_MAGIC
operator|.
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|magic
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|magic
argument_list|,
name|TOKEN_STORAGE_MAGIC
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Bad header found in token storage."
argument_list|)
throw|;
block|}
name|byte
name|version
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|TOKEN_STORAGE_VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown version "
operator|+
name|version
operator|+
literal|" in token storage."
argument_list|)
throw|;
block|}
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|field|TOKEN_STORAGE_MAGIC
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|TOKEN_STORAGE_MAGIC
init|=
literal|"HDTS"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
DECL|field|TOKEN_STORAGE_VERSION
specifier|private
specifier|static
specifier|final
name|byte
name|TOKEN_STORAGE_VERSION
init|=
literal|0
decl_stmt|;
DECL|method|writeTokenStorageToStream (DataOutputStream os)
specifier|public
name|void
name|writeTokenStorageToStream
parameter_list|(
name|DataOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|write
argument_list|(
name|TOKEN_STORAGE_MAGIC
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|TOKEN_STORAGE_VERSION
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTokenStorageFile (Path filename, Configuration conf)
specifier|public
name|void
name|writeTokenStorageFile
parameter_list|(
name|Path
name|filename
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|os
init|=
name|filename
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
operator|.
name|create
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|writeTokenStorageToStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stores all the keys to DataOutput    * @param out    * @throws IOException    */
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write out tokens first
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|tokenMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|e
range|:
name|tokenMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|// now write out secret keys
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|secretKeysMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|byte
index|[]
argument_list|>
name|e
range|:
name|secretKeysMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Loads all the keys    * @param in    * @throws IOException    */
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|secretKeysMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|tokenMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Text
name|alias
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|alias
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|t
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|t
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|tokenMap
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Text
name|alias
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|alias
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|secretKeysMap
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Copy all of the credentials from one credential object into another.    * Existing secrets and tokens are overwritten.    * @param other the credentials to copy    */
DECL|method|addAll (Credentials other)
specifier|public
name|void
name|addAll
parameter_list|(
name|Credentials
name|other
parameter_list|)
block|{
name|addAll
argument_list|(
name|other
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy all of the credentials from one credential object into another.    * Existing secrets and tokens are not overwritten.    * @param other the credentials to copy    */
DECL|method|mergeAll (Credentials other)
specifier|public
name|void
name|mergeAll
parameter_list|(
name|Credentials
name|other
parameter_list|)
block|{
name|addAll
argument_list|(
name|other
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|addAll (Credentials other, boolean overwrite)
specifier|private
name|void
name|addAll
parameter_list|(
name|Credentials
name|other
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|byte
index|[]
argument_list|>
name|secret
range|:
name|other
operator|.
name|secretKeysMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Text
name|key
init|=
name|secret
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|secretKeysMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|||
name|overwrite
condition|)
block|{
name|secretKeysMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|secret
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|token
range|:
name|other
operator|.
name|tokenMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Text
name|key
init|=
name|token
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|tokenMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|||
name|overwrite
condition|)
block|{
name|tokenMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|token
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

