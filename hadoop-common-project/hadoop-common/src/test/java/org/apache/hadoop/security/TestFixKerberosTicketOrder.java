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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
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
name|assertFalse
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
name|assertTrue
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
name|HashMap
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosTicket
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|Sasl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslClient
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
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
name|minikdc
operator|.
name|KerberosSecurityTestcase
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
name|SaslRpcServer
operator|.
name|AuthMethod
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
name|SaslRpcServer
operator|.
name|QualityOfProtection
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
operator|.
name|AuthenticationMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Testcase for HADOOP-13433 that verifies the logic of fixKerberosTicketOrder.  */
end_comment

begin_class
DECL|class|TestFixKerberosTicketOrder
specifier|public
class|class
name|TestFixKerberosTicketOrder
extends|extends
name|KerberosSecurityTestcase
block|{
DECL|field|clientPrincipal
specifier|private
name|String
name|clientPrincipal
init|=
literal|"client"
decl_stmt|;
DECL|field|server1Protocol
specifier|private
name|String
name|server1Protocol
init|=
literal|"server1"
decl_stmt|;
DECL|field|server2Protocol
specifier|private
name|String
name|server2Protocol
init|=
literal|"server2"
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
DECL|field|server1Principal
specifier|private
name|String
name|server1Principal
init|=
name|server1Protocol
operator|+
literal|"/"
operator|+
name|host
decl_stmt|;
DECL|field|server2Principal
specifier|private
name|String
name|server2Principal
init|=
name|server2Protocol
operator|+
literal|"/"
operator|+
name|host
decl_stmt|;
DECL|field|keytabFile
specifier|private
name|File
name|keytabFile
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|props
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|keytabFile
operator|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"keytab"
argument_list|)
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|clientPrincipal
argument_list|,
name|server1Principal
argument_list|,
name|server2Principal
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setShouldRenewImmediatelyForTests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|props
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
name|QualityOfProtection
operator|.
name|AUTHENTICATION
operator|.
name|saslQop
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|clientPrincipal
argument_list|,
name|keytabFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|SaslClient
name|client
init|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server1Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
name|ugi
operator|.
name|getSubject
argument_list|()
decl_stmt|;
comment|// move tgt to the last
for|for
control|(
name|KerberosTicket
name|ticket
range|:
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
control|)
block|{
if|if
condition|(
name|ticket
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"krbtgt"
argument_list|)
condition|)
block|{
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|remove
argument_list|(
name|ticket
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|ticket
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// make sure the first ticket is not tgt
name|assertFalse
argument_list|(
literal|"The first ticket is still tgt, "
operator|+
literal|"the implementation in jdk may have been changed, "
operator|+
literal|"please reconsider the problem in HADOOP-13433"
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|c
operator|instanceof
name|KerberosTicket
argument_list|)
operator|.
name|map
argument_list|(
name|c
lambda|->
operator|(
operator|(
name|KerberosTicket
operator|)
name|c
operator|)
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"krbtgt"
argument_list|)
argument_list|)
expr_stmt|;
comment|// should fail as we send a service ticket instead of tgt to KDC.
name|intercept
argument_list|(
name|SaslException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
block|@Override           public Void run(
argument_list|)
throws|throws
name|Exception
block|{
name|SaslClient
name|client
operator|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server2Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|ugi
operator|.
name|fixKerberosTicketOrder
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// check if TGT is the first ticket after the fix.
end_comment

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"The first ticket is not tgt"
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|c
operator|instanceof
name|KerberosTicket
argument_list|)
operator|.
name|map
argument_list|(
name|c
lambda|->
operator|(
operator|(
name|KerberosTicket
operator|)
name|c
operator|)
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"krbtgt"
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// make sure we can still get new service ticket after the fix.
end_comment

begin_expr_stmt
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|SaslClient
name|client
init|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server2Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"No service ticket for "
operator|+
name|server2Protocol
operator|+
literal|" found"
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|t
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|server2Protocol
argument_list|)
argument_list|)
operator|.
name|findAny
argument_list|()
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    @
name|Test
DECL|method|testWithDestroyedTGT ()
specifier|public
name|void
name|testWithDestroyedTGT
parameter_list|()
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|clientPrincipal
argument_list|,
name|keytabFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|SaslClient
name|client
init|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server1Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
name|ugi
operator|.
name|getSubject
argument_list|()
decl_stmt|;
comment|// mark the ticket as destroyed
for|for
control|(
name|KerberosTicket
name|ticket
range|:
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
control|)
block|{
if|if
condition|(
name|ticket
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"krbtgt"
argument_list|)
condition|)
block|{
name|ticket
operator|.
name|destroy
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|ugi
operator|.
name|fixKerberosTicketOrder
argument_list|()
expr_stmt|;
comment|// verify that after fixing, the tgt ticket should be removed
name|assertFalse
argument_list|(
literal|"The first ticket is not tgt"
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|c
operator|instanceof
name|KerberosTicket
argument_list|)
operator|.
name|map
argument_list|(
name|c
lambda|->
operator|(
operator|(
name|KerberosTicket
operator|)
name|c
operator|)
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
comment|// should fail as we send a service ticket instead of tgt to KDC.
name|intercept
argument_list|(
name|SaslException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
block|@Override           public Void run(
argument_list|)
throws|throws
name|Exception
block|{
name|SaslClient
name|client
operator|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server2Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
end_function

begin_empty_stmt
unit|}))
empty_stmt|;
end_empty_stmt

begin_comment
comment|// relogin to get a new ticket
end_comment

begin_expr_stmt
name|ugi
operator|.
name|reloginFromKeytab
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// make sure we can get new service ticket after the relogin.
end_comment

begin_expr_stmt
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|SaslClient
name|client
init|=
name|Sasl
operator|.
name|createSaslClient
argument_list|(
operator|new
name|String
index|[]
block|{
name|AuthMethod
operator|.
name|KERBEROS
operator|.
name|getMechanismName
argument_list|()
block|}
argument_list|,
name|clientPrincipal
argument_list|,
name|server2Protocol
argument_list|,
name|host
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|client
operator|.
name|evaluateChallenge
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|client
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"No service ticket for "
operator|+
name|server2Protocol
operator|+
literal|" found"
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|t
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|server2Protocol
argument_list|)
argument_list|)
operator|.
name|findAny
argument_list|()
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

unit|} }
end_unit

