begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|DFSTestUtil
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestSecondaryWebUi
specifier|public
class|class
name|TestSecondaryWebUi
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|snn
specifier|private
specifier|static
name|SecondaryNameNode
name|snn
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUpCluster ()
specifier|public
specifier|static
name|void
name|setUpCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
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
literal|0
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
name|snn
operator|=
operator|new
name|SecondaryNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutDownCluster ()
specifier|public
specifier|static
name|void
name|shutDownCluster
parameter_list|()
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
if|if
condition|(
name|snn
operator|!=
literal|null
condition|)
block|{
name|snn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSecondaryWebUi ()
specifier|public
name|void
name|testSecondaryWebUi
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|pageContents
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|SecondaryNameNode
operator|.
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getPort
argument_list|()
operator|+
literal|"/status.jsp"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't find \"Last Checkpoint\""
argument_list|,
name|pageContents
operator|.
name|contains
argument_list|(
literal|"Last Checkpoint"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSecondaryWebJmx ()
specifier|public
name|void
name|testSecondaryWebJmx
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|IOException
block|{
name|String
name|pageContents
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|SecondaryNameNode
operator|.
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getPort
argument_list|()
operator|+
literal|"/jmx"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pageContents
operator|.
name|contains
argument_list|(
literal|"Hadoop:service=SecondaryNameNode,name=JvmMetrics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

