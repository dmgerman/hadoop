begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.alias
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|alias
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|security
operator|.
name|ProviderUtils
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
name|Charsets
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
name|net
operator|.
name|URI
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
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStoreException
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
import|import
name|java
operator|.
name|security
operator|.
name|UnrecoverableKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Abstract class for implementing credential providers that are based on  * Java Keystores as the underlying credential store.  *  * The password for the keystore is taken from the HADOOP_CREDSTORE_PASSWORD  * environment variable with a default of 'none'.  *  * It is expected that for access to credential protected resource to copy the  * creds from the original provider into the job's Credentials object, which is  * accessed via the UserProvider. Therefore, these providers won't be directly  * used by MapReduce tasks.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractJavaKeyStoreProvider
specifier|public
specifier|abstract
class|class
name|AbstractJavaKeyStoreProvider
extends|extends
name|CredentialProvider
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractJavaKeyStoreProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CREDENTIAL_PASSWORD_ENV_VAR
specifier|public
specifier|static
specifier|final
name|String
name|CREDENTIAL_PASSWORD_ENV_VAR
init|=
literal|"HADOOP_CREDSTORE_PASSWORD"
decl_stmt|;
DECL|field|CREDENTIAL_PASSWORD_FILE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CREDENTIAL_PASSWORD_FILE_KEY
init|=
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CREDENTIAL_PASSWORD_FILE_KEY
decl_stmt|;
DECL|field|CREDENTIAL_PASSWORD_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CREDENTIAL_PASSWORD_DEFAULT
init|=
literal|"none"
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|URI
name|uri
decl_stmt|;
DECL|field|keyStore
specifier|private
name|KeyStore
name|keyStore
decl_stmt|;
DECL|field|password
specifier|private
name|char
index|[]
name|password
init|=
literal|null
decl_stmt|;
DECL|field|changed
specifier|private
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
DECL|field|readLock
specifier|private
name|Lock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|AbstractJavaKeyStoreProvider (URI uri, Configuration conf)
specifier|protected
name|AbstractJavaKeyStoreProvider
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|initFileSystem
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|locateKeystore
argument_list|()
expr_stmt|;
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|protected
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|setPath (Path p)
specifier|public
name|void
name|setPath
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|p
expr_stmt|;
block|}
DECL|method|getPassword ()
specifier|public
name|char
index|[]
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
DECL|method|setPassword (char[] pass)
specifier|public
name|void
name|setPassword
parameter_list|(
name|char
index|[]
name|pass
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|pass
expr_stmt|;
block|}
DECL|method|isChanged ()
specifier|public
name|boolean
name|isChanged
parameter_list|()
block|{
return|return
name|changed
return|;
block|}
DECL|method|setChanged (boolean chg)
specifier|public
name|void
name|setChanged
parameter_list|(
name|boolean
name|chg
parameter_list|)
block|{
name|this
operator|.
name|changed
operator|=
name|chg
expr_stmt|;
block|}
DECL|method|getReadLock ()
specifier|public
name|Lock
name|getReadLock
parameter_list|()
block|{
return|return
name|readLock
return|;
block|}
DECL|method|setReadLock (Lock rl)
specifier|public
name|void
name|setReadLock
parameter_list|(
name|Lock
name|rl
parameter_list|)
block|{
name|this
operator|.
name|readLock
operator|=
name|rl
expr_stmt|;
block|}
DECL|method|getWriteLock ()
specifier|public
name|Lock
name|getWriteLock
parameter_list|()
block|{
return|return
name|writeLock
return|;
block|}
DECL|method|setWriteLock (Lock wl)
specifier|public
name|void
name|setWriteLock
parameter_list|(
name|Lock
name|wl
parameter_list|)
block|{
name|this
operator|.
name|writeLock
operator|=
name|wl
expr_stmt|;
block|}
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
DECL|method|getKeyStore ()
specifier|public
name|KeyStore
name|getKeyStore
parameter_list|()
block|{
return|return
name|keyStore
return|;
block|}
DECL|method|getPathAsString ()
specifier|protected
specifier|final
name|String
name|getPathAsString
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSchemeName ()
specifier|protected
specifier|abstract
name|String
name|getSchemeName
parameter_list|()
function_decl|;
DECL|method|getOutputStreamForKeystore ()
specifier|protected
specifier|abstract
name|OutputStream
name|getOutputStreamForKeystore
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|keystoreExists ()
specifier|protected
specifier|abstract
name|boolean
name|keystoreExists
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getInputStreamForFile ()
specifier|protected
specifier|abstract
name|InputStream
name|getInputStreamForFile
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|createPermissions (String perms)
specifier|protected
specifier|abstract
name|void
name|createPermissions
parameter_list|(
name|String
name|perms
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|stashOriginalFilePermissions ()
specifier|protected
specifier|abstract
name|void
name|stashOriginalFilePermissions
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|initFileSystem (URI keystoreUri)
specifier|protected
name|void
name|initFileSystem
parameter_list|(
name|URI
name|keystoreUri
parameter_list|)
throws|throws
name|IOException
block|{
name|path
operator|=
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
name|keystoreUri
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
literal|"backing jks path initialized to "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCredentialEntry (String alias)
specifier|public
name|CredentialEntry
name|getCredentialEntry
parameter_list|(
name|String
name|alias
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|SecretKeySpec
name|key
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|keyStore
operator|.
name|containsAlias
argument_list|(
name|alias
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|key
operator|=
operator|(
name|SecretKeySpec
operator|)
name|keyStore
operator|.
name|getKey
argument_list|(
name|alias
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get credential "
operator|+
name|alias
operator|+
literal|" from "
operator|+
name|getPathAsString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get algorithm for credential "
operator|+
name|alias
operator|+
literal|" from "
operator|+
name|getPathAsString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnrecoverableKeyException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't recover credential "
operator|+
name|alias
operator|+
literal|" from "
operator|+
name|getPathAsString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|CredentialEntry
argument_list|(
name|alias
argument_list|,
name|bytesToChars
argument_list|(
name|key
operator|.
name|getEncoded
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|bytesToChars (byte[] bytes)
specifier|public
specifier|static
name|char
index|[]
name|bytesToChars
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|pass
decl_stmt|;
name|pass
operator|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
return|return
name|pass
operator|.
name|toCharArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAliases ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAliases
parameter_list|()
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|alias
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Enumeration
argument_list|<
name|String
argument_list|>
name|e
init|=
name|keyStore
operator|.
name|aliases
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|alias
operator|=
name|e
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get alias "
operator|+
name|alias
operator|+
literal|" from "
operator|+
name|getPathAsString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|list
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createCredentialEntry (String alias, char[] credential)
specifier|public
name|CredentialEntry
name|createCredentialEntry
parameter_list|(
name|String
name|alias
parameter_list|,
name|char
index|[]
name|credential
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|keyStore
operator|.
name|containsAlias
argument_list|(
name|alias
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Credential "
operator|+
name|alias
operator|+
literal|" already exists in "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
name|innerSetCredential
argument_list|(
name|alias
argument_list|,
name|credential
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Problem looking up credential "
operator|+
name|alias
operator|+
literal|" in "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteCredentialEntry (String name)
specifier|public
name|void
name|deleteCredentialEntry
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
if|if
condition|(
name|keyStore
operator|.
name|containsAlias
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|keyStore
operator|.
name|deleteEntry
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Credential "
operator|+
name|name
operator|+
literal|" does not exist in "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Problem removing "
operator|+
name|name
operator|+
literal|" from "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|innerSetCredential (String alias, char[] material)
name|CredentialEntry
name|innerSetCredential
parameter_list|(
name|String
name|alias
parameter_list|,
name|char
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|keyStore
operator|.
name|setKeyEntry
argument_list|(
name|alias
argument_list|,
operator|new
name|SecretKeySpec
argument_list|(
operator|new
name|String
argument_list|(
name|material
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"AES"
argument_list|)
argument_list|,
name|password
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't store credential "
operator|+
name|alias
operator|+
literal|" in "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|CredentialEntry
argument_list|(
name|alias
argument_list|,
name|material
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|changed
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Keystore hasn't changed, returning."
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing out keystore."
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
name|getOutputStreamForKeystore
argument_list|()
init|)
block|{
name|keyStore
operator|.
name|store
argument_list|(
name|out
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't store keystore "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No such algorithm storing keystore "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|CertificateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Certificate exception storing keystore "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|changed
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Open up and initialize the keyStore.    *    * @throws IOException If there is a problem reading the password file    * or a problem reading the keystore.    */
DECL|method|locateKeystore ()
specifier|private
name|void
name|locateKeystore
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|password
operator|=
name|ProviderUtils
operator|.
name|locatePassword
argument_list|(
name|CREDENTIAL_PASSWORD_ENV_VAR
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|CREDENTIAL_PASSWORD_FILE_KEY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|password
operator|=
name|CREDENTIAL_PASSWORD_DEFAULT
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
name|KeyStore
name|ks
decl_stmt|;
name|ks
operator|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
literal|"jceks"
argument_list|)
expr_stmt|;
if|if
condition|(
name|keystoreExists
argument_list|()
condition|)
block|{
name|stashOriginalFilePermissions
argument_list|()
expr_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|getInputStreamForFile
argument_list|()
init|)
block|{
name|ks
operator|.
name|load
argument_list|(
name|in
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|createPermissions
argument_list|(
literal|"600"
argument_list|)
expr_stmt|;
comment|// required to create an empty keystore. *sigh*
name|ks
operator|.
name|load
argument_list|(
literal|null
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
name|keyStore
operator|=
name|ks
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't create keystore"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't load keystore "
operator|+
name|getPathAsString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsPassword ()
specifier|public
name|boolean
name|needsPassword
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
literal|null
operator|==
name|ProviderUtils
operator|.
name|locatePassword
argument_list|(
name|CREDENTIAL_PASSWORD_ENV_VAR
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|CREDENTIAL_PASSWORD_FILE_KEY
argument_list|)
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|noPasswordWarning ()
specifier|public
name|String
name|noPasswordWarning
parameter_list|()
block|{
return|return
name|ProviderUtils
operator|.
name|noPasswordWarning
argument_list|(
name|CREDENTIAL_PASSWORD_ENV_VAR
argument_list|,
name|CREDENTIAL_PASSWORD_FILE_KEY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|noPasswordError ()
specifier|public
name|String
name|noPasswordError
parameter_list|()
block|{
return|return
name|ProviderUtils
operator|.
name|noPasswordError
argument_list|(
name|CREDENTIAL_PASSWORD_ENV_VAR
argument_list|,
name|CREDENTIAL_PASSWORD_FILE_KEY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|uri
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

