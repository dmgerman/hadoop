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
name|InetAddress
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|conf
operator|.
name|NfsConfiguration
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
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3
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
name|nfs3
operator|.
name|RpcProgramNfs3
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestMountd
specifier|public
class|class
name|TestMountd
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
name|TestMountd
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testStart ()
specifier|public
name|void
name|testStart
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Start minicluster
name|NfsConfiguration
name|config
init|=
operator|new
name|NfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Use emphral port in case tests are running in parallel
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.mountd.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.server.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Start nfs
name|Nfs3
name|nfs3
init|=
operator|new
name|Nfs3
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|nfs3
operator|.
name|startServiceInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|RpcProgramMountd
name|mountd
init|=
operator|(
name|RpcProgramMountd
operator|)
name|nfs3
operator|.
name|getMountd
argument_list|()
operator|.
name|getRpcProgram
argument_list|()
decl_stmt|;
name|mountd
operator|.
name|nullOp
argument_list|(
operator|new
name|XDR
argument_list|()
argument_list|,
literal|1234
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|RpcProgramNfs3
name|nfsd
init|=
operator|(
name|RpcProgramNfs3
operator|)
name|nfs3
operator|.
name|getRpcProgram
argument_list|()
decl_stmt|;
name|nfsd
operator|.
name|nullProcedure
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

