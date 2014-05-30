begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
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
operator|.
name|nfs3
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
name|assertTrue
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
name|NfsConfigKeys
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
name|Mountd
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestExportsTable
specifier|public
class|class
name|TestExportsTable
block|{
annotation|@
name|Test
DECL|method|testExportPoint ()
specifier|public
name|void
name|testExportPoint
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|null
decl_stmt|;
name|String
name|exportPoint
init|=
literal|"/myexport1"
decl_stmt|;
name|config
operator|.
name|setStrings
argument_list|(
name|NfsConfigKeys
operator|.
name|DFS_NFS_EXPORT_POINT_KEY
argument_list|,
name|exportPoint
argument_list|)
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
try|try
block|{
name|cluster
operator|=
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
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// Start nfs
specifier|final
name|Nfs3
name|nfsServer
init|=
operator|new
name|Nfs3
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|nfsServer
operator|.
name|startServiceInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Mountd
name|mountd
init|=
name|nfsServer
operator|.
name|getMountd
argument_list|()
decl_stmt|;
name|RpcProgramMountd
name|rpcMount
init|=
operator|(
name|RpcProgramMountd
operator|)
name|mountd
operator|.
name|getRpcProgram
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rpcMount
operator|.
name|getExports
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|String
name|exportInMountd
init|=
name|rpcMount
operator|.
name|getExports
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exportInMountd
operator|.
name|equals
argument_list|(
name|exportPoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

