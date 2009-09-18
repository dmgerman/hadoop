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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|*
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
name|apache
operator|.
name|avro
operator|.
name|ipc
operator|.
name|AvroRemoteException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|util
operator|.
name|Utf8
import|;
end_import

begin_comment
comment|/** Unit tests for AvroRpc. */
end_comment

begin_class
DECL|class|TestAvroRpc
specifier|public
class|class
name|TestAvroRpc
extends|extends
name|TestCase
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
name|TestAvroRpc
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
DECL|field|datasize
name|int
name|datasize
init|=
literal|1024
operator|*
literal|100
decl_stmt|;
DECL|field|numThreads
name|int
name|numThreads
init|=
literal|50
decl_stmt|;
DECL|method|TestAvroRpc (String name)
specifier|public
name|TestAvroRpc
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|class|TestImpl
specifier|public
specifier|static
class|class
name|TestImpl
implements|implements
name|AvroTestProtocol
block|{
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
block|{}
DECL|method|echo (Utf8 value)
specifier|public
name|Utf8
name|echo
parameter_list|(
name|Utf8
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|add (int v1, int v2)
specifier|public
name|int
name|add
parameter_list|(
name|int
name|v1
parameter_list|,
name|int
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
DECL|method|error ()
specifier|public
name|int
name|error
parameter_list|()
throws|throws
name|Problem
block|{
throw|throw
operator|new
name|Problem
argument_list|()
throw|;
block|}
block|}
DECL|method|testCalls ()
specifier|public
name|void
name|testCalls
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Server
name|server
init|=
name|AvroRpc
operator|.
name|getServer
argument_list|(
operator|new
name|TestImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|AvroTestProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|proxy
operator|=
operator|(
name|AvroTestProtocol
operator|)
name|AvroRpc
operator|.
name|getProxy
argument_list|(
name|AvroTestProtocol
operator|.
name|class
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
name|Utf8
name|utf8Result
init|=
name|proxy
operator|.
name|echo
argument_list|(
operator|new
name|Utf8
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Utf8
argument_list|(
literal|"hello world"
argument_list|)
argument_list|,
name|utf8Result
argument_list|)
expr_stmt|;
name|int
name|intResult
init|=
name|proxy
operator|.
name|add
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|intResult
argument_list|)
expr_stmt|;
name|boolean
name|caught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|error
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AvroRemoteException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught "
operator|+
name|e
argument_list|)
expr_stmt|;
name|caught
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

