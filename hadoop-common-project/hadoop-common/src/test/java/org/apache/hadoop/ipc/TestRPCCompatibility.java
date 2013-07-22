begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|fail
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|ipc
operator|.
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|GetProtocolSignatureRequestProto
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
name|ipc
operator|.
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|GetProtocolSignatureResponseProto
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
name|ipc
operator|.
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|ProtocolSignatureProto
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
name|ipc
operator|.
name|protobuf
operator|.
name|RpcHeaderProtos
operator|.
name|RpcResponseHeaderProto
operator|.
name|RpcErrorCodeProto
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
comment|/** Unit test for supporting method-name based compatible RPCs. */
end_comment

begin_class
DECL|class|TestRPCCompatibility
specifier|public
class|class
name|TestRPCCompatibility
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|addr
specifier|private
specifier|static
name|InetSocketAddress
name|addr
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|server
decl_stmt|;
DECL|field|proxy
specifier|private
name|ProtocolProxy
argument_list|<
name|?
argument_list|>
name|proxy
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
name|TestRPCCompatibility
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|interface|TestProtocol0
specifier|public
interface|interface
name|TestProtocol0
extends|extends
name|VersionedProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|0L
decl_stmt|;
DECL|method|ping ()
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|interface|TestProtocol1
specifier|public
interface|interface
name|TestProtocol1
extends|extends
name|TestProtocol0
block|{
DECL|method|echo (String value)
name|String
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|// TestProtocol2 is a compatible impl of TestProtocol1 - hence use its name
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"org.apache.hadoop.ipc.TestRPCCompatibility$TestProtocol1"
argument_list|)
DECL|interface|TestProtocol2
specifier|public
interface|interface
name|TestProtocol2
extends|extends
name|TestProtocol1
block|{
DECL|method|echo (int value)
name|int
name|echo
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|TestImpl0
specifier|public
specifier|static
class|class
name|TestImpl0
implements|implements
name|TestProtocol0
block|{
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|versionID
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHashCode)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHashCode
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|VersionedProtocol
argument_list|>
name|inter
decl_stmt|;
try|try
block|{
name|inter
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|VersionedProtocol
argument_list|>
operator|)
name|getClass
argument_list|()
operator|.
name|getGenericInterfaces
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
return|return
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|clientMethodsHashCode
argument_list|,
name|getProtocolVersion
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|)
argument_list|,
name|inter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
block|{
return|return;
block|}
block|}
DECL|class|TestImpl1
specifier|public
specifier|static
class|class
name|TestImpl1
extends|extends
name|TestImpl0
implements|implements
name|TestProtocol1
block|{
annotation|@
name|Override
DECL|method|echo (String value)
specifier|public
name|String
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TestProtocol1
operator|.
name|versionID
return|;
block|}
block|}
DECL|class|TestImpl2
specifier|public
specifier|static
class|class
name|TestImpl2
extends|extends
name|TestImpl1
implements|implements
name|TestProtocol2
block|{
annotation|@
name|Override
DECL|method|echo (int value)
specifier|public
name|int
name|echo
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TestProtocol2
operator|.
name|versionID
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|ProtocolSignature
operator|.
name|resetCache
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
operator|.
name|getProxy
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|// old client vs new server
DECL|method|testVersion0ClientVersion1Server ()
specifier|public
name|void
name|testVersion0ClientVersion1Server
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a server with two handlers
name|TestImpl1
name|impl
init|=
operator|new
name|TestImpl1
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|impl
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|,
name|TestProtocol0
operator|.
name|class
argument_list|,
name|impl
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|TestProtocol0
operator|.
name|class
argument_list|,
name|TestProtocol0
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|TestProtocol0
name|proxy0
init|=
operator|(
name|TestProtocol0
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|proxy0
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
comment|// old client vs new server
DECL|method|testVersion1ClientVersion0Server ()
specifier|public
name|void
name|testVersion1ClientVersion0Server
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a server with two handlers
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol0
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
operator|new
name|TestImpl0
argument_list|()
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|,
name|TestProtocol1
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|TestProtocol1
name|proxy1
init|=
operator|(
name|TestProtocol1
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|proxy1
operator|.
name|ping
argument_list|()
expr_stmt|;
try|try
block|{
name|proxy1
operator|.
name|echo
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Echo should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
DECL|class|Version2Client
specifier|private
class|class
name|Version2Client
block|{
DECL|field|proxy2
specifier|private
name|TestProtocol2
name|proxy2
decl_stmt|;
DECL|field|serverInfo
specifier|private
name|ProtocolProxy
argument_list|<
name|TestProtocol2
argument_list|>
name|serverInfo
decl_stmt|;
DECL|method|Version2Client ()
specifier|private
name|Version2Client
parameter_list|()
throws|throws
name|IOException
block|{
name|serverInfo
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|,
name|TestProtocol2
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy2
operator|=
name|serverInfo
operator|.
name|getProxy
argument_list|()
expr_stmt|;
block|}
DECL|method|echo (int value)
specifier|public
name|int
name|echo
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|NumberFormatException
block|{
if|if
condition|(
name|serverInfo
operator|.
name|isMethodSupported
argument_list|(
literal|"echo"
argument_list|,
name|int
operator|.
name|class
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"echo int is supported"
argument_list|)
expr_stmt|;
return|return
operator|-
name|value
return|;
comment|// use version 3 echo long
block|}
else|else
block|{
comment|// server is version 2
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"echo int is NOT supported"
argument_list|)
expr_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|proxy2
operator|.
name|echo
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|echo (String value)
specifier|public
name|String
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy2
operator|.
name|echo
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
block|{
name|proxy2
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|// Compatible new client& old server
DECL|method|testVersion2ClientVersion1Server ()
specifier|public
name|void
name|testVersion2ClientVersion1Server
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a server with two handlers
name|TestImpl1
name|impl
init|=
operator|new
name|TestImpl1
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|impl
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|,
name|TestProtocol0
operator|.
name|class
argument_list|,
name|impl
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|Version2Client
name|client
init|=
operator|new
name|Version2Client
argument_list|()
decl_stmt|;
name|client
operator|.
name|ping
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|client
operator|.
name|echo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
comment|// echo(int) is not supported by server, so returning 3
comment|// This verifies that echo(int) and echo(String)'s hash codes are different
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|client
operator|.
name|echo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// equal version client and server
DECL|method|testVersion2ClientVersion2Server ()
specifier|public
name|void
name|testVersion2ClientVersion2Server
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a server with two handlers
name|TestImpl2
name|impl
init|=
operator|new
name|TestImpl2
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|impl
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|,
name|TestProtocol0
operator|.
name|class
argument_list|,
name|impl
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|Version2Client
name|client
init|=
operator|new
name|Version2Client
argument_list|()
decl_stmt|;
name|client
operator|.
name|ping
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|client
operator|.
name|echo
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now that echo(int) is supported by the server, echo(int) should return -3
name|assertEquals
argument_list|(
operator|-
literal|3
argument_list|,
name|client
operator|.
name|echo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|interface|TestProtocol3
specifier|public
interface|interface
name|TestProtocol3
block|{
DECL|method|echo (String value)
name|int
name|echo
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
DECL|method|echo (int value)
name|int
name|echo
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
DECL|method|echo_alias (int value)
name|int
name|echo_alias
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
DECL|method|echo (int value1, int value2)
name|int
name|echo
parameter_list|(
name|int
name|value1
parameter_list|,
name|int
name|value2
parameter_list|)
function_decl|;
block|}
annotation|@
name|Test
DECL|method|testHashCode ()
specifier|public
name|void
name|testHashCode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure that overriding methods have different hashcodes
name|Method
name|strMethod
init|=
name|TestProtocol3
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|stringEchoHash
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|strMethod
argument_list|)
decl_stmt|;
name|Method
name|intMethod
init|=
name|TestProtocol3
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|intEchoHash
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|intMethod
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|stringEchoHash
operator|==
name|intEchoHash
argument_list|)
expr_stmt|;
comment|// make sure methods with the same signature
comment|// from different declaring classes have the same hash code
name|int
name|intEchoHash1
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|TestProtocol2
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intEchoHash
argument_list|,
name|intEchoHash1
argument_list|)
expr_stmt|;
comment|// Methods with the same name and parameter types but different returning
comment|// types have different hash codes
name|int
name|stringEchoHash1
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|TestProtocol2
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|stringEchoHash
operator|==
name|stringEchoHash1
argument_list|)
expr_stmt|;
comment|// Make sure that methods with the same returning type and parameter types
comment|// but different method names have different hash code
name|int
name|intEchoHashAlias
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|TestProtocol3
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo_alias"
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|intEchoHash
operator|==
name|intEchoHashAlias
argument_list|)
expr_stmt|;
comment|// Make sure that methods with the same returninig type and method name but
comment|// larger number of parameter types have different hash code
name|int
name|intEchoHash2
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|TestProtocol3
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|intEchoHash
operator|==
name|intEchoHash2
argument_list|)
expr_stmt|;
comment|// make sure that methods order does not matter for method array hash code
name|int
name|hash1
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
operator|new
name|Method
index|[]
block|{
name|intMethod
block|,
name|strMethod
block|}
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
operator|new
name|Method
index|[]
block|{
name|strMethod
block|,
name|intMethod
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hash1
argument_list|,
name|hash2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"org.apache.hadoop.ipc.TestRPCCompatibility$TestProtocol1"
argument_list|)
DECL|interface|TestProtocol4
specifier|public
interface|interface
name|TestProtocol4
extends|extends
name|TestProtocol2
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|4L
decl_stmt|;
annotation|@
name|Override
DECL|method|echo (int value)
name|int
name|echo
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|Test
DECL|method|testVersionMismatch ()
specifier|public
name|void
name|testVersionMismatch
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
operator|new
name|TestImpl2
argument_list|()
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|TestProtocol4
name|proxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol4
operator|.
name|class
argument_list|,
name|TestProtocol4
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|echo
argument_list|(
literal|21
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The call must throw VersionMismatch exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RPC
operator|.
name|VersionMismatch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
operator|.
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ex
operator|.
name|getErrorCode
argument_list|()
operator|.
name|equals
argument_list|(
name|RpcErrorCodeProto
operator|.
name|ERROR_RPC_VERSION_MISMATCH
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Expected version mismatch but got "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIsMethodSupported ()
specifier|public
name|void
name|testIsMethodSupported
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
operator|new
name|TestImpl2
argument_list|()
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|addr
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|TestProtocol2
name|proxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|,
name|TestProtocol2
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|boolean
name|supported
init|=
name|RpcClientUtil
operator|.
name|isMethodSupported
argument_list|(
name|proxy
argument_list|,
name|TestProtocol2
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|)
argument_list|,
literal|"echo"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|supported
argument_list|)
expr_stmt|;
name|supported
operator|=
name|RpcClientUtil
operator|.
name|isMethodSupported
argument_list|(
name|proxy
argument_list|,
name|TestProtocol2
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|TestProtocol2
operator|.
name|class
argument_list|)
argument_list|,
literal|"echo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|supported
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that ProtocolMetaInfoServerSideTranslatorPB correctly looks up    * the server registry to extract protocol signatures and versions.    */
annotation|@
name|Test
DECL|method|testProtocolMetaInfoSSTranslatorPB ()
specifier|public
name|void
name|testProtocolMetaInfoSSTranslatorPB
parameter_list|()
throws|throws
name|Exception
block|{
name|TestImpl1
name|impl
init|=
operator|new
name|TestImpl1
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|impl
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|2
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|,
name|TestProtocol0
operator|.
name|class
argument_list|,
name|impl
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|ProtocolMetaInfoServerSideTranslatorPB
name|xlator
init|=
operator|new
name|ProtocolMetaInfoServerSideTranslatorPB
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|GetProtocolSignatureResponseProto
name|resp
init|=
name|xlator
operator|.
name|getProtocolSignature
argument_list|(
literal|null
argument_list|,
name|createGetProtocolSigRequestProto
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|)
argument_list|)
decl_stmt|;
comment|//No signatures should be found
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getProtocolSignatureCount
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
name|xlator
operator|.
name|getProtocolSignature
argument_list|(
literal|null
argument_list|,
name|createGetProtocolSigRequestProto
argument_list|(
name|TestProtocol1
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resp
operator|.
name|getProtocolSignatureCount
argument_list|()
argument_list|)
expr_stmt|;
name|ProtocolSignatureProto
name|sig
init|=
name|resp
operator|.
name|getProtocolSignatureList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestProtocol1
operator|.
name|versionID
argument_list|,
name|sig
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|int
name|expected
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|TestProtocol1
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"echo"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|m
range|:
name|sig
operator|.
name|getMethodsList
argument_list|()
control|)
block|{
if|if
condition|(
name|expected
operator|==
name|m
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
DECL|method|createGetProtocolSigRequestProto ( Class<?> protocol, RPC.RpcKind rpcKind)
specifier|private
name|GetProtocolSignatureRequestProto
name|createGetProtocolSigRequestProto
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|)
block|{
name|GetProtocolSignatureRequestProto
operator|.
name|Builder
name|builder
init|=
name|GetProtocolSignatureRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProtocol
argument_list|(
name|protocol
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRpcKind
argument_list|(
name|rpcKind
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

