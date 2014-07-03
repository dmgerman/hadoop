begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|hdfs
package|;
end_package

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
name|contract
operator|.
name|AbstractFSContract
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
name|contract
operator|.
name|AbstractFSContractTestBase
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
name|contract
operator|.
name|ContractOptions
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|junit
operator|.
name|Assert
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

begin_comment
comment|/**  * The contract of HDFS  * This changes its feature set from platform for platform -the default  * set is updated during initialization.  */
end_comment

begin_class
DECL|class|HDFSContract
specifier|public
class|class
name|HDFSContract
extends|extends
name|AbstractFSContract
block|{
DECL|field|CONTRACT_HDFS_XML
specifier|public
specifier|static
specifier|final
name|String
name|CONTRACT_HDFS_XML
init|=
literal|"contract/hdfs.xml"
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
name|AbstractFSContractTestBase
operator|.
name|TEST_FILE_LEN
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|method|HDFSContract (Configuration conf)
specifier|public
name|HDFSContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//insert the base features
name|addConfResource
argument_list|(
name|CONTRACT_HDFS_XML
argument_list|)
expr_stmt|;
block|}
DECL|method|createCluster ()
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|CONTRACT_HDFS_XML
argument_list|)
expr_stmt|;
comment|//hack in a 256 byte block size
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
block|}
DECL|method|destroyCluster ()
specifier|public
specifier|static
name|void
name|destroyCluster
parameter_list|()
throws|throws
name|IOException
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
DECL|method|getCluster ()
specifier|public
specifier|static
name|MiniDFSCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"contract options not loaded"
argument_list|,
name|isSupported
argument_list|(
name|ContractOptions
operator|.
name|IS_CASE_SENSITIVE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestFileSystem ()
specifier|public
name|FileSystem
name|getTestFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
comment|//assumes cluster is not null
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"cluster not created"
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
return|return
name|cluster
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"hdfs"
return|;
block|}
annotation|@
name|Override
DECL|method|getTestPath ()
specifier|public
name|Path
name|getTestPath
parameter_list|()
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

