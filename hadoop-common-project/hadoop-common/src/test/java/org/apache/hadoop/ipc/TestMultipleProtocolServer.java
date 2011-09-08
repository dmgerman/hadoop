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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
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
name|Before
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
name|Test
import|;
end_import

begin_class
DECL|class|TestMultipleProtocolServer
specifier|public
class|class
name|TestMultipleProtocolServer
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
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"Foo"
argument_list|)
DECL|interface|Foo0
interface|interface
name|Foo0
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
name|String
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"Foo"
argument_list|)
DECL|interface|Foo1
interface|interface
name|Foo1
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
literal|1L
decl_stmt|;
DECL|method|ping ()
name|String
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|ping2 ()
name|String
name|ping2
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"Foo"
argument_list|)
DECL|interface|FooUnimplemented
interface|interface
name|FooUnimplemented
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
literal|2L
decl_stmt|;
DECL|method|ping ()
name|String
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|interface|Mixin
interface|interface
name|Mixin
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
DECL|method|hello ()
name|void
name|hello
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|interface|Bar
interface|interface
name|Bar
extends|extends
name|Mixin
extends|,
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
DECL|method|echo (int i)
name|int
name|echo
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|Foo0Impl
class|class
name|Foo0Impl
implements|implements
name|Foo0
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
name|Foo0
operator|.
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
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
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
name|clientMethodsHash
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
name|clientMethodsHash
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
name|String
name|ping
parameter_list|()
block|{
return|return
literal|"Foo0"
return|;
block|}
block|}
DECL|class|Foo1Impl
class|class
name|Foo1Impl
implements|implements
name|Foo1
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
name|Foo1
operator|.
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
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
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
name|clientMethodsHash
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
name|clientMethodsHash
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
name|String
name|ping
parameter_list|()
block|{
return|return
literal|"Foo1"
return|;
block|}
annotation|@
name|Override
DECL|method|ping2 ()
specifier|public
name|String
name|ping2
parameter_list|()
block|{
return|return
literal|"Foo1"
return|;
block|}
block|}
DECL|class|BarImpl
class|class
name|BarImpl
implements|implements
name|Bar
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
name|Bar
operator|.
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
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
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
name|clientMethodsHash
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
name|clientMethodsHash
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
DECL|method|echo (int i)
specifier|public
name|int
name|echo
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
return|;
block|}
annotation|@
name|Override
DECL|method|hello ()
specifier|public
name|void
name|hello
parameter_list|()
block|{             }
block|}
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
comment|// create a server with two handlers
name|server
operator|=
name|RPC
operator|.
name|getServer
argument_list|(
name|Foo0
operator|.
name|class
argument_list|,
operator|new
name|Foo0Impl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|Foo1
operator|.
name|class
argument_list|,
operator|new
name|Foo1Impl
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|Bar
operator|.
name|class
argument_list|,
operator|new
name|BarImpl
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|Mixin
operator|.
name|class
argument_list|,
operator|new
name|BarImpl
argument_list|()
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
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test1 ()
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|IOException
block|{
name|ProtocolProxy
argument_list|<
name|?
argument_list|>
name|proxy
decl_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|Foo0
operator|.
name|class
argument_list|,
name|Foo0
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Foo0
name|foo0
init|=
operator|(
name|Foo0
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Foo0"
argument_list|,
name|foo0
operator|.
name|ping
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|Foo1
operator|.
name|class
argument_list|,
name|Foo1
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Foo1
name|foo1
init|=
operator|(
name|Foo1
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Foo1"
argument_list|,
name|foo1
operator|.
name|ping
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Foo1"
argument_list|,
name|foo1
operator|.
name|ping
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|Bar
operator|.
name|class
argument_list|,
name|Foo1
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Bar
name|bar
init|=
operator|(
name|Bar
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|99
argument_list|,
name|bar
operator|.
name|echo
argument_list|(
literal|99
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now test Mixin class method
name|Mixin
name|mixin
init|=
name|bar
decl_stmt|;
name|mixin
operator|.
name|hello
argument_list|()
expr_stmt|;
block|}
comment|// Server does not implement the FooUnimplemented version of protocol Foo.
comment|// See that calls to it fail.
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testNonExistingProtocol ()
specifier|public
name|void
name|testNonExistingProtocol
parameter_list|()
throws|throws
name|IOException
block|{
name|ProtocolProxy
argument_list|<
name|?
argument_list|>
name|proxy
decl_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|FooUnimplemented
operator|.
name|class
argument_list|,
name|FooUnimplemented
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FooUnimplemented
name|foo
init|=
operator|(
name|FooUnimplemented
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|foo
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
comment|/**    * getProtocolVersion of an unimplemented version should return highest version    * Similarly getProtocolSignature should work.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testNonExistingProtocol2 ()
specifier|public
name|void
name|testNonExistingProtocol2
parameter_list|()
throws|throws
name|IOException
block|{
name|ProtocolProxy
argument_list|<
name|?
argument_list|>
name|proxy
decl_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|FooUnimplemented
operator|.
name|class
argument_list|,
name|FooUnimplemented
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FooUnimplemented
name|foo
init|=
operator|(
name|FooUnimplemented
operator|)
name|proxy
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Foo1
operator|.
name|versionID
argument_list|,
name|foo
operator|.
name|getProtocolVersion
argument_list|(
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|FooUnimplemented
operator|.
name|class
argument_list|)
argument_list|,
name|FooUnimplemented
operator|.
name|versionID
argument_list|)
argument_list|)
expr_stmt|;
name|foo
operator|.
name|getProtocolSignature
argument_list|(
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|FooUnimplemented
operator|.
name|class
argument_list|)
argument_list|,
name|FooUnimplemented
operator|.
name|versionID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testIncorrectServerCreation ()
specifier|public
name|void
name|testIncorrectServerCreation
parameter_list|()
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|getServer
argument_list|(
name|Foo1
operator|.
name|class
argument_list|,
operator|new
name|Foo0Impl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

