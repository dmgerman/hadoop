begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
operator|.
name|asString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
operator|.
name|bytes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|FileSystem
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
name|permission
operator|.
name|FsPermission
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
name|v2
operator|.
name|api
operator|.
name|MRDelegationTokenIdentifier
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
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|delegation
operator|.
name|DelegationKey
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServerCommonProtos
operator|.
name|VersionProto
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
name|yarn
operator|.
name|server
operator|.
name|records
operator|.
name|Version
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
name|yarn
operator|.
name|server
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|VersionPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|utils
operator|.
name|LeveldbIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|leveldbjni
operator|.
name|internal
operator|.
name|NativeDB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
import|;
end_import

begin_class
DECL|class|HistoryServerLeveldbStateStoreService
specifier|public
class|class
name|HistoryServerLeveldbStateStoreService
extends|extends
name|HistoryServerStateStoreService
block|{
DECL|field|DB_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DB_NAME
init|=
literal|"mr-jhs-state"
decl_stmt|;
DECL|field|DB_SCHEMA_VERSION_KEY
specifier|private
specifier|static
specifier|final
name|String
name|DB_SCHEMA_VERSION_KEY
init|=
literal|"jhs-schema-version"
decl_stmt|;
DECL|field|TOKEN_MASTER_KEY_KEY_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|TOKEN_MASTER_KEY_KEY_PREFIX
init|=
literal|"tokens/key_"
decl_stmt|;
DECL|field|TOKEN_STATE_KEY_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|TOKEN_STATE_KEY_PREFIX
init|=
literal|"tokens/token_"
decl_stmt|;
DECL|field|CURRENT_VERSION_INFO
specifier|private
specifier|static
specifier|final
name|Version
name|CURRENT_VERSION_INFO
init|=
name|Version
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
name|DB
name|db
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HistoryServerLeveldbStateStoreService
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|initStorage (Configuration conf)
specifier|protected
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|startStorage ()
specifier|protected
name|void
name|startStorage
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|storeRoot
init|=
name|createStorageDir
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|options
operator|.
name|logger
argument_list|(
operator|new
name|LeveldbLogger
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using state database at "
operator|+
name|storeRoot
operator|+
literal|" for recovery"
argument_list|)
expr_stmt|;
name|File
name|dbfile
init|=
operator|new
name|File
argument_list|(
name|storeRoot
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbfile
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeDB
operator|.
name|DBException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|isNotFound
argument_list|()
operator|||
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|" does not exist "
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating state database at "
operator|+
name|dbfile
argument_list|)
expr_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|db
operator|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|dbfile
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|// store version
name|storeVersion
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|dbErr
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|dbErr
operator|.
name|getMessage
argument_list|()
argument_list|,
name|dbErr
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|checkVersion
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closeStorage ()
specifier|protected
name|void
name|closeStorage
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadState ()
specifier|public
name|HistoryServerState
name|loadState
parameter_list|()
throws|throws
name|IOException
block|{
name|HistoryServerState
name|state
init|=
operator|new
name|HistoryServerState
argument_list|()
decl_stmt|;
name|int
name|numKeys
init|=
name|loadTokenMasterKeys
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovered "
operator|+
name|numKeys
operator|+
literal|" token master keys"
argument_list|)
expr_stmt|;
name|int
name|numTokens
init|=
name|loadTokens
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovered "
operator|+
name|numTokens
operator|+
literal|" tokens"
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
DECL|method|loadTokenMasterKeys (HistoryServerState state)
specifier|private
name|int
name|loadTokenMasterKeys
parameter_list|(
name|HistoryServerState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numKeys
init|=
literal|0
decl_stmt|;
name|LeveldbIterator
name|iter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|iter
operator|=
operator|new
name|LeveldbIterator
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seek
argument_list|(
name|bytes
argument_list|(
name|TOKEN_MASTER_KEY_KEY_PREFIX
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|asString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|startsWith
argument_list|(
name|TOKEN_MASTER_KEY_KEY_PREFIX
argument_list|)
condition|)
block|{
break|break;
block|}
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
literal|"Loading master key from "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadTokenMasterKey
argument_list|(
name|state
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error loading token master key from "
operator|+
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
operator|++
name|numKeys
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|iter
operator|!=
literal|null
condition|)
block|{
name|iter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|numKeys
return|;
block|}
DECL|method|loadTokenMasterKey (HistoryServerState state, byte[] data)
specifier|private
name|void
name|loadTokenMasterKey
parameter_list|(
name|HistoryServerState
name|state
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|DelegationKey
name|key
init|=
operator|new
name|DelegationKey
argument_list|()
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|key
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|state
operator|.
name|tokenMasterKeyState
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|loadTokens (HistoryServerState state)
specifier|private
name|int
name|loadTokens
parameter_list|(
name|HistoryServerState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
name|LeveldbIterator
name|iter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|iter
operator|=
operator|new
name|LeveldbIterator
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|iter
operator|.
name|seek
argument_list|(
name|bytes
argument_list|(
name|TOKEN_STATE_KEY_PREFIX
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|asString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|startsWith
argument_list|(
name|TOKEN_STATE_KEY_PREFIX
argument_list|)
condition|)
block|{
break|break;
block|}
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
literal|"Loading token from "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadToken
argument_list|(
name|state
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error loading token state from "
operator|+
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
operator|++
name|numTokens
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|iter
operator|!=
literal|null
condition|)
block|{
name|iter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|numTokens
return|;
block|}
DECL|method|loadToken (HistoryServerState state, byte[] data)
specifier|private
name|void
name|loadToken
parameter_list|(
name|HistoryServerState
name|state
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|MRDelegationTokenIdentifier
name|tokenId
init|=
operator|new
name|MRDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|long
name|renewDate
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|tokenId
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|renewDate
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
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
name|state
operator|.
name|tokenState
operator|.
name|put
argument_list|(
name|tokenId
argument_list|,
name|renewDate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
name|void
name|storeToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"Storing token "
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|memStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dataStream
init|=
operator|new
name|DataOutputStream
argument_list|(
name|memStream
argument_list|)
decl_stmt|;
try|try
block|{
name|tokenId
operator|.
name|write
argument_list|(
name|dataStream
argument_list|)
expr_stmt|;
name|dataStream
operator|.
name|writeLong
argument_list|(
name|renewDate
argument_list|)
expr_stmt|;
name|dataStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataStream
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|dataStream
argument_list|)
expr_stmt|;
block|}
name|String
name|dbKey
init|=
name|getTokenDatabaseKey
argument_list|(
name|tokenId
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|dbKey
argument_list|)
argument_list|,
name|memStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
name|void
name|updateToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|IOException
block|{
name|storeToken
argument_list|(
name|tokenId
argument_list|,
name|renewDate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeToken (MRDelegationTokenIdentifier tokenId)
specifier|public
name|void
name|removeToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dbKey
init|=
name|getTokenDatabaseKey
argument_list|(
name|tokenId
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|delete
argument_list|(
name|bytes
argument_list|(
name|dbKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getTokenDatabaseKey (MRDelegationTokenIdentifier tokenId)
specifier|private
name|String
name|getTokenDatabaseKey
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|)
block|{
return|return
name|TOKEN_STATE_KEY_PREFIX
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|storeTokenMasterKey (DelegationKey masterKey)
specifier|public
name|void
name|storeTokenMasterKey
parameter_list|(
name|DelegationKey
name|masterKey
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"Storing master key "
operator|+
name|masterKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|memStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dataStream
init|=
operator|new
name|DataOutputStream
argument_list|(
name|memStream
argument_list|)
decl_stmt|;
try|try
block|{
name|masterKey
operator|.
name|write
argument_list|(
name|dataStream
argument_list|)
expr_stmt|;
name|dataStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataStream
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|dataStream
argument_list|)
expr_stmt|;
block|}
name|String
name|dbKey
init|=
name|getTokenMasterKeyDatabaseKey
argument_list|(
name|masterKey
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|dbKey
argument_list|)
argument_list|,
name|memStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeTokenMasterKey (DelegationKey masterKey)
specifier|public
name|void
name|removeTokenMasterKey
parameter_list|(
name|DelegationKey
name|masterKey
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"Removing master key "
operator|+
name|masterKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|dbKey
init|=
name|getTokenMasterKeyDatabaseKey
argument_list|(
name|masterKey
argument_list|)
decl_stmt|;
try|try
block|{
name|db
operator|.
name|delete
argument_list|(
name|bytes
argument_list|(
name|dbKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getTokenMasterKeyDatabaseKey (DelegationKey masterKey)
specifier|private
name|String
name|getTokenMasterKeyDatabaseKey
parameter_list|(
name|DelegationKey
name|masterKey
parameter_list|)
block|{
return|return
name|TOKEN_MASTER_KEY_KEY_PREFIX
operator|+
name|masterKey
operator|.
name|getKeyId
argument_list|()
return|;
block|}
DECL|method|createStorageDir (Configuration conf)
specifier|private
name|Path
name|createStorageDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|confPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_LEVELDB_STATE_STORE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|confPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No store location directory configured in "
operator|+
name|JHAdminConfig
operator|.
name|MR_HS_LEVELDB_STATE_STORE_PATH
argument_list|)
throw|;
block|}
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
name|confPath
argument_list|,
name|DB_NAME
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|root
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|root
return|;
block|}
DECL|method|loadVersion ()
name|Version
name|loadVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|db
operator|.
name|get
argument_list|(
name|bytes
argument_list|(
name|DB_SCHEMA_VERSION_KEY
argument_list|)
argument_list|)
decl_stmt|;
comment|// if version is not stored previously, treat it as 1.0.
if|if
condition|(
name|data
operator|==
literal|null
operator|||
name|data
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Version
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
return|;
block|}
name|Version
name|version
init|=
operator|new
name|VersionPBImpl
argument_list|(
name|VersionProto
operator|.
name|parseFrom
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|version
return|;
block|}
DECL|method|storeVersion ()
specifier|private
name|void
name|storeVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|dbStoreVersion
argument_list|(
name|CURRENT_VERSION_INFO
argument_list|)
expr_stmt|;
block|}
DECL|method|dbStoreVersion (Version state)
name|void
name|dbStoreVersion
parameter_list|(
name|Version
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|key
init|=
name|DB_SCHEMA_VERSION_KEY
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
operator|(
name|VersionPBImpl
operator|)
name|state
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
try|try
block|{
name|db
operator|.
name|put
argument_list|(
name|bytes
argument_list|(
name|key
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getCurrentVersion ()
name|Version
name|getCurrentVersion
parameter_list|()
block|{
return|return
name|CURRENT_VERSION_INFO
return|;
block|}
comment|/**    * 1) Versioning scheme: major.minor. For e.g. 1.0, 1.1, 1.2...1.25, 2.0 etc.    * 2) Any incompatible change of state-store is a major upgrade, and any    *    compatible change of state-store is a minor upgrade.    * 3) Within a minor upgrade, say 1.1 to 1.2:    *    overwrite the version info and proceed as normal.    * 4) Within a major upgrade, say 1.2 to 2.0:    *    throw exception and indicate user to use a separate upgrade tool to    *    upgrade state or remove incompatible old state.    */
DECL|method|checkVersion ()
specifier|private
name|void
name|checkVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|Version
name|loadedVersion
init|=
name|loadVersion
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loaded state version info "
operator|+
name|loadedVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|loadedVersion
operator|.
name|equals
argument_list|(
name|getCurrentVersion
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|loadedVersion
operator|.
name|isCompatibleTo
argument_list|(
name|getCurrentVersion
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing state version info "
operator|+
name|getCurrentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|storeVersion
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible version for state: expecting state version "
operator|+
name|getCurrentVersion
argument_list|()
operator|+
literal|", but loading version "
operator|+
name|loadedVersion
argument_list|)
throw|;
block|}
block|}
DECL|class|LeveldbLogger
specifier|private
specifier|static
class|class
name|LeveldbLogger
implements|implements
name|Logger
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
name|LeveldbLogger
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|log (String message)
specifier|public
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

