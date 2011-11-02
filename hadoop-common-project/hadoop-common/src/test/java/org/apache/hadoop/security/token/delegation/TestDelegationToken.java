begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation
package|package
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
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|io
operator|.
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|security
operator|.
name|AccessControlException
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
name|SecretManager
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
name|SecretManager
operator|.
name|InvalidToken
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
name|AbstractDelegationTokenSecretManager
operator|.
name|DelegationTokenInformation
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
name|Daemon
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
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDelegationToken
specifier|public
class|class
name|TestDelegationToken
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
name|TestDelegationToken
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KIND
specifier|private
specifier|static
specifier|final
name|Text
name|KIND
init|=
operator|new
name|Text
argument_list|(
literal|"MY KIND"
argument_list|)
decl_stmt|;
DECL|class|TestDelegationTokenIdentifier
specifier|public
specifier|static
class|class
name|TestDelegationTokenIdentifier
extends|extends
name|AbstractDelegationTokenIdentifier
implements|implements
name|Writable
block|{
DECL|method|TestDelegationTokenIdentifier ()
specifier|public
name|TestDelegationTokenIdentifier
parameter_list|()
block|{     }
DECL|method|TestDelegationTokenIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|TestDelegationTokenIdentifier
parameter_list|(
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
name|super
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND
return|;
block|}
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
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestDelegationTokenSecretManager
specifier|public
specifier|static
class|class
name|TestDelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
block|{
DECL|method|TestDelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval)
specifier|public
name|TestDelegationTokenSecretManager
parameter_list|(
name|long
name|delegationKeyUpdateInterval
parameter_list|,
name|long
name|delegationTokenMaxLifetime
parameter_list|,
name|long
name|delegationTokenRenewInterval
parameter_list|,
name|long
name|delegationTokenRemoverScanInterval
parameter_list|)
block|{
name|super
argument_list|(
name|delegationKeyUpdateInterval
argument_list|,
name|delegationTokenMaxLifetime
argument_list|,
name|delegationTokenRenewInterval
argument_list|,
name|delegationTokenRemoverScanInterval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|TestDelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|TestDelegationTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createPassword (TestDelegationTokenIdentifier t)
specifier|protected
name|byte
index|[]
name|createPassword
parameter_list|(
name|TestDelegationTokenIdentifier
name|t
parameter_list|)
block|{
return|return
name|super
operator|.
name|createPassword
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|createPassword (TestDelegationTokenIdentifier t, DelegationKey key)
specifier|public
name|byte
index|[]
name|createPassword
parameter_list|(
name|TestDelegationTokenIdentifier
name|t
parameter_list|,
name|DelegationKey
name|key
parameter_list|)
block|{
return|return
name|SecretManager
operator|.
name|createPassword
argument_list|(
name|t
operator|.
name|getBytes
argument_list|()
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getAllTokens ()
specifier|public
name|Map
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|,
name|DelegationTokenInformation
argument_list|>
name|getAllTokens
parameter_list|()
block|{
return|return
name|currentTokens
return|;
block|}
DECL|method|getKey (TestDelegationTokenIdentifier id)
specifier|public
name|DelegationKey
name|getKey
parameter_list|(
name|TestDelegationTokenIdentifier
name|id
parameter_list|)
block|{
return|return
name|allKeys
operator|.
name|get
argument_list|(
name|id
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|TokenSelector
specifier|public
specifier|static
class|class
name|TokenSelector
extends|extends
name|AbstractDelegationTokenSelector
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
block|{
DECL|method|TokenSelector ()
specifier|protected
name|TokenSelector
parameter_list|()
block|{
name|super
argument_list|(
name|KIND
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSerialization ()
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDelegationTokenIdentifier
name|origToken
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"alice"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"bob"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"colin"
argument_list|)
argument_list|)
decl_stmt|;
name|TestDelegationTokenIdentifier
name|newToken
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|origToken
operator|.
name|setIssueDate
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|origToken
operator|.
name|setMasterKeyId
argument_list|(
literal|321
argument_list|)
expr_stmt|;
name|origToken
operator|.
name|setMaxDate
argument_list|(
literal|314
argument_list|)
expr_stmt|;
name|origToken
operator|.
name|setSequenceNumber
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
comment|// clone origToken into newToken
name|DataInputBuffer
name|inBuf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|outBuf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|origToken
operator|.
name|write
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
name|inBuf
operator|.
name|reset
argument_list|(
name|outBuf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|outBuf
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|newToken
operator|.
name|readFields
argument_list|(
name|inBuf
argument_list|)
expr_stmt|;
comment|// now test the fields
name|assertEquals
argument_list|(
literal|"alice"
argument_list|,
name|newToken
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
literal|"bob"
argument_list|)
argument_list|,
name|newToken
operator|.
name|getRenewer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"colin"
argument_list|,
name|newToken
operator|.
name|getUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|newToken
operator|.
name|getIssueDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|321
argument_list|,
name|newToken
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|314
argument_list|,
name|newToken
operator|.
name|getMaxDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|newToken
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|origToken
argument_list|,
name|newToken
argument_list|)
expr_stmt|;
block|}
DECL|method|generateDelegationToken ( TestDelegationTokenSecretManager dtSecretManager, String owner, String renewer)
specifier|private
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|generateDelegationToken
parameter_list|(
name|TestDelegationTokenSecretManager
name|dtSecretManager
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|renewer
parameter_list|)
block|{
name|TestDelegationTokenIdentifier
name|dtId
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|owner
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|(
name|dtId
argument_list|,
name|dtSecretManager
argument_list|)
return|;
block|}
DECL|method|shouldThrow (PrivilegedExceptionAction<Object> action, Class<? extends Throwable> except)
specifier|private
name|void
name|shouldThrow
parameter_list|(
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
name|action
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|except
parameter_list|)
block|{
try|try
block|{
name|action
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"action did not throw "
operator|+
name|except
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught an exception: "
argument_list|,
name|th
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"action threw wrong exception"
argument_list|,
name|except
argument_list|,
name|th
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenSecretManager ()
specifier|public
name|void
name|testDelegationTokenSecretManager
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|3
operator|*
literal|1000
argument_list|,
literal|1
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
decl_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"SomeUser"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
comment|// Fake renewer should not be able to renew
name|shouldThrow
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"FakeRenewer"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|AccessControlException
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"renew time is in future"
argument_list|,
name|time
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|TestDelegationTokenIdentifier
name|identifier
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|null
operator|!=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep to expire the token"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|//Token should be expired
try|try
block|{
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
comment|//Should not come here
name|Assert
operator|.
name|fail
argument_list|(
literal|"Token should have expired"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|e
parameter_list|)
block|{
comment|//Success
block|}
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep beyond the max lifetime"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|shouldThrow
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|InvalidToken
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dtSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCancelDelegationToken ()
specifier|public
name|void
name|testCancelDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|10
operator|*
literal|1000
argument_list|,
literal|1
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
decl_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"SomeUser"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
comment|//Fake renewer should not be able to renew
name|shouldThrow
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"FakeCanceller"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|AccessControlException
operator|.
name|class
argument_list|)
expr_stmt|;
name|dtSecretManager
operator|.
name|cancelToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
name|shouldThrow
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|InvalidToken
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dtSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRollMasterKey ()
specifier|public
name|void
name|testRollMasterKey
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|10
operator|*
literal|1000
argument_list|,
literal|1
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
decl_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
comment|//generate a token and store the password
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"SomeUser"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|oldPasswd
init|=
name|token
operator|.
name|getPassword
argument_list|()
decl_stmt|;
comment|//store the length of the keys list
name|int
name|prevNumKeys
init|=
name|dtSecretManager
operator|.
name|getAllKeys
argument_list|()
operator|.
name|length
decl_stmt|;
name|dtSecretManager
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
comment|//after rolling, the length of the keys list must increase
name|int
name|currNumKeys
init|=
name|dtSecretManager
operator|.
name|getAllKeys
argument_list|()
operator|.
name|length
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|currNumKeys
operator|-
name|prevNumKeys
operator|)
operator|>=
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//after rolling, the token that was generated earlier must
comment|//still be valid (retrievePassword will fail if the token
comment|//is not valid)
name|ByteArrayInputStream
name|bi
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|TestDelegationTokenIdentifier
name|identifier
init|=
name|dtSecretManager
operator|.
name|createIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|bi
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|newPasswd
init|=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
comment|//compare the passwords
name|Assert
operator|.
name|assertEquals
argument_list|(
name|oldPasswd
argument_list|,
name|newPasswd
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dtSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testDelegationTokenSelector ()
specifier|public
name|void
name|testDelegationTokenSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|10
operator|*
literal|1000
argument_list|,
literal|1
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
decl_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|AbstractDelegationTokenSelector
name|ds
init|=
operator|new
name|AbstractDelegationTokenSelector
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|(
name|KIND
argument_list|)
decl_stmt|;
comment|//Creates a collection of tokens
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token1
init|=
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"SomeUser1"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|token1
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
literal|"MY-SERVICE1"
argument_list|)
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token2
init|=
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"SomeUser2"
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|token2
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
literal|"MY-SERVICE2"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|token2
argument_list|)
expr_stmt|;
comment|//try to select a token with a given service name (created earlier)
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|t
init|=
name|ds
operator|.
name|selectToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"MY-SERVICE1"
argument_list|)
argument_list|,
name|tokens
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|t
argument_list|,
name|token1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dtSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testParallelDelegationTokenCreation ()
specifier|public
name|void
name|testParallelDelegationTokenCreation
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|2000
argument_list|,
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|int
name|numThreads
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|numTokensPerThread
init|=
literal|100
decl_stmt|;
class|class
name|tokenIssuerThread
implements|implements
name|Runnable
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTokensPerThread
condition|;
name|i
operator|++
control|)
block|{
name|generateDelegationToken
argument_list|(
name|dtSecretManager
argument_list|,
literal|"auser"
argument_list|,
literal|"arenewer"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
block|}
block|}
name|Thread
index|[]
name|issuers
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|issuers
index|[
name|i
index|]
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|tokenIssuerThread
argument_list|()
argument_list|)
expr_stmt|;
name|issuers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|issuers
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|,
name|DelegationTokenInformation
argument_list|>
name|tokenCache
init|=
name|dtSecretManager
operator|.
name|getAllTokens
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numTokensPerThread
operator|*
name|numThreads
argument_list|,
name|tokenCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|iter
init|=
name|tokenCache
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TestDelegationTokenIdentifier
name|id
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|DelegationTokenInformation
name|info
init|=
name|tokenCache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|DelegationKey
name|key
init|=
name|dtSecretManager
operator|.
name|getKey
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|storedPassword
init|=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|byte
index|[]
name|password
init|=
name|dtSecretManager
operator|.
name|createPassword
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|password
argument_list|,
name|storedPassword
argument_list|)
argument_list|)
expr_stmt|;
comment|//verify by secret manager api
name|dtSecretManager
operator|.
name|verifyToken
argument_list|(
name|id
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|dtSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenNullRenewer ()
specifier|public
name|void
name|testDelegationTokenNullRenewer
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDelegationTokenSecretManager
name|dtSecretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|10
operator|*
literal|1000
argument_list|,
literal|1
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
decl_stmt|;
name|dtSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|TestDelegationTokenIdentifier
name|dtId
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"theuser"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|(
name|dtId
argument_list|,
name|dtSecretManager
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Renewal must not succeed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//PASS
block|}
block|}
block|}
end_class

end_unit

