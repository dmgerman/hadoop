begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
package|;
end_package

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
name|hdfs
operator|.
name|nfs
operator|.
name|mount
operator|.
name|RpcProgramMountd
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|oncrpc
operator|.
name|RegistrationClient
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
name|oncrpc
operator|.
name|RpcCall
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
name|oncrpc
operator|.
name|XDR
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
name|oncrpc
operator|.
name|security
operator|.
name|CredentialsNone
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
name|oncrpc
operator|.
name|security
operator|.
name|VerifierNone
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
name|portmap
operator|.
name|PortmapMapping
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
name|portmap
operator|.
name|PortmapRequest
import|;
end_import

begin_class
DECL|class|TestPortmapRegister
specifier|public
class|class
name|TestPortmapRegister
block|{
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
name|TestPortmapRegister
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testRequest (XDR request, XDR request2)
specifier|static
name|void
name|testRequest
parameter_list|(
name|XDR
name|request
parameter_list|,
name|XDR
name|request2
parameter_list|)
block|{
name|RegistrationClient
name|registrationClient
init|=
operator|new
name|RegistrationClient
argument_list|(
literal|"localhost"
argument_list|,
name|Nfs3Constant
operator|.
name|SUN_RPCBIND
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|registrationClient
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|PortmapMapping
name|mapEntry
init|=
operator|new
name|PortmapMapping
argument_list|(
name|RpcProgramMountd
operator|.
name|PROGRAM
argument_list|,
name|RpcProgramMountd
operator|.
name|VERSION_1
argument_list|,
name|PortmapMapping
operator|.
name|TRANSPORT_UDP
argument_list|,
name|RpcProgramMountd
operator|.
name|PORT
argument_list|)
decl_stmt|;
name|XDR
name|mappingRequest
init|=
name|PortmapRequest
operator|.
name|create
argument_list|(
name|mapEntry
argument_list|)
decl_stmt|;
name|RegistrationClient
name|registrationClient
init|=
operator|new
name|RegistrationClient
argument_list|(
literal|"localhost"
argument_list|,
name|Nfs3Constant
operator|.
name|SUN_RPCBIND
argument_list|,
name|mappingRequest
argument_list|)
decl_stmt|;
name|registrationClient
operator|.
name|run
argument_list|()
expr_stmt|;
name|Thread
name|t1
init|=
operator|new
name|Runtest1
argument_list|()
decl_stmt|;
comment|//Thread t2 = testa.new Runtest2();
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//t2.start();
name|t1
operator|.
name|join
argument_list|()
expr_stmt|;
comment|//t2.join();
comment|//testDump();
block|}
DECL|class|Runtest1
specifier|static
class|class
name|Runtest1
extends|extends
name|Thread
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//testGetportMount();
name|PortmapMapping
name|mapEntry
init|=
operator|new
name|PortmapMapping
argument_list|(
name|RpcProgramMountd
operator|.
name|PROGRAM
argument_list|,
name|RpcProgramMountd
operator|.
name|VERSION_1
argument_list|,
name|PortmapMapping
operator|.
name|TRANSPORT_UDP
argument_list|,
name|RpcProgramMountd
operator|.
name|PORT
argument_list|)
decl_stmt|;
name|XDR
name|req
init|=
name|PortmapRequest
operator|.
name|create
argument_list|(
name|mapEntry
argument_list|)
decl_stmt|;
name|testRequest
argument_list|(
name|req
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Runtest2
specifier|static
class|class
name|Runtest2
extends|extends
name|Thread
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|testDump
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createPortmapXDRheader (XDR xdr_out, int procedure)
specifier|static
name|void
name|createPortmapXDRheader
parameter_list|(
name|XDR
name|xdr_out
parameter_list|,
name|int
name|procedure
parameter_list|)
block|{
comment|// TODO: Move this to RpcRequest
name|RpcCall
operator|.
name|getInstance
argument_list|(
literal|0
argument_list|,
literal|100000
argument_list|,
literal|2
argument_list|,
name|procedure
argument_list|,
operator|new
name|CredentialsNone
argument_list|()
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|xdr_out
argument_list|)
expr_stmt|;
comment|/*     xdr_out.putInt(1); //unix auth     xdr_out.putVariableOpaque(new byte[20]);     xdr_out.putInt(0);     xdr_out.putInt(0); */
block|}
DECL|method|testGetportMount ()
specifier|static
name|void
name|testGetportMount
parameter_list|()
block|{
name|XDR
name|xdr_out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createPortmapXDRheader
argument_list|(
name|xdr_out
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|100005
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|XDR
name|request2
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createPortmapXDRheader
argument_list|(
name|xdr_out
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|100005
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testRequest
argument_list|(
name|xdr_out
argument_list|,
name|request2
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetport ()
specifier|static
name|void
name|testGetport
parameter_list|()
block|{
name|XDR
name|xdr_out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createPortmapXDRheader
argument_list|(
name|xdr_out
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|100003
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|xdr_out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|XDR
name|request2
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createPortmapXDRheader
argument_list|(
name|xdr_out
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|100003
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|request2
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testRequest
argument_list|(
name|xdr_out
argument_list|,
name|request2
argument_list|)
expr_stmt|;
block|}
DECL|method|testDump ()
specifier|static
name|void
name|testDump
parameter_list|()
block|{
name|XDR
name|xdr_out
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createPortmapXDRheader
argument_list|(
name|xdr_out
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|testRequest
argument_list|(
name|xdr_out
argument_list|,
name|xdr_out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

