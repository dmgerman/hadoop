begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|authentication
operator|.
name|util
operator|.
name|KerberosUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|GSSName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ietf
operator|.
name|jgss
operator|.
name|Oid
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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
operator|.
name|Builder
import|;
end_import

begin_comment
comment|/**  * Http connection utilities.  *  */
end_comment

begin_class
DECL|class|HttpUtil
specifier|public
class|class
name|HttpUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HttpUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BASE_64_CODEC
specifier|private
specifier|static
specifier|final
name|Base64
name|BASE_64_CODEC
init|=
operator|new
name|Base64
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|HttpUtil ()
specifier|protected
name|HttpUtil
parameter_list|()
block|{
comment|// prevents calls from subclass
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Generate SPNEGO challenge request token.    *    * @param server - hostname to contact    * @throws IOException    * @throws InterruptedException    */
DECL|method|generateToken (String server)
specifier|public
specifier|static
name|String
name|generateToken
parameter_list|(
name|String
name|server
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"The user credential is {}"
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|String
name|challenge
init|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// This Oid for Kerberos GSS-API mechanism.
name|Oid
name|mechOid
init|=
name|KerberosUtil
operator|.
name|getOidInstance
argument_list|(
literal|"GSS_KRB5_MECH_OID"
argument_list|)
decl_stmt|;
name|GSSManager
name|manager
init|=
name|GSSManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|// GSS name for server
name|GSSName
name|serverName
init|=
name|manager
operator|.
name|createName
argument_list|(
literal|"HTTP@"
operator|+
name|server
argument_list|,
name|GSSName
operator|.
name|NT_HOSTBASED_SERVICE
argument_list|)
decl_stmt|;
comment|// Create a GSSContext for authentication with the service.
comment|// We're passing client credentials as null since we want them to
comment|// be read from the Subject.
name|GSSContext
name|gssContext
init|=
name|manager
operator|.
name|createContext
argument_list|(
name|serverName
operator|.
name|canonicalize
argument_list|(
name|mechOid
argument_list|)
argument_list|,
name|mechOid
argument_list|,
literal|null
argument_list|,
name|GSSContext
operator|.
name|DEFAULT_LIFETIME
argument_list|)
decl_stmt|;
name|gssContext
operator|.
name|requestMutualAuth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|gssContext
operator|.
name|requestCredDeleg
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Establish context
name|byte
index|[]
name|inToken
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|byte
index|[]
name|outToken
init|=
name|gssContext
operator|.
name|initSecContext
argument_list|(
name|inToken
argument_list|,
literal|0
argument_list|,
name|inToken
operator|.
name|length
argument_list|)
decl_stmt|;
name|gssContext
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// Base64 encoded and stringified token for server
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got valid challenge for host {}"
argument_list|,
name|serverName
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|BASE_64_CODEC
operator|.
name|encode
argument_list|(
name|outToken
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GSSException
decl||
name|IllegalAccessException
decl||
name|NoSuchFieldException
decl||
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|challenge
return|;
block|}
DECL|method|connect (String url)
specifier|public
specifier|static
name|Builder
name|connect
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|boolean
name|useKerberos
init|=
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
decl_stmt|;
name|URI
name|resource
init|=
operator|new
name|URI
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Client
name|client
init|=
name|Client
operator|.
name|create
argument_list|()
decl_stmt|;
name|Builder
name|builder
init|=
name|client
operator|.
name|resource
argument_list|(
name|url
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
if|if
condition|(
name|useKerberos
condition|)
block|{
name|String
name|challenge
init|=
name|generateToken
argument_list|(
name|resource
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|AUTHORIZATION
argument_list|,
literal|"Negotiate "
operator|+
name|challenge
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authorization: Negotiate {}"
argument_list|,
name|challenge
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

