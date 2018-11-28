begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|cli
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeAttributeInfo
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeAttributeKey
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeAttributeType
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeLabel
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|yarn
operator|.
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_class
DECL|class|TestClusterCLI
specifier|public
class|class
name|TestClusterCLI
block|{
DECL|field|sysOutStream
name|ByteArrayOutputStream
name|sysOutStream
decl_stmt|;
DECL|field|sysOut
specifier|private
name|PrintStream
name|sysOut
decl_stmt|;
DECL|field|sysErrStream
name|ByteArrayOutputStream
name|sysErrStream
decl_stmt|;
DECL|field|sysErr
specifier|private
name|PrintStream
name|sysErr
decl_stmt|;
DECL|field|client
specifier|private
name|YarnClient
name|client
init|=
name|mock
argument_list|(
name|YarnClient
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|sysOutStream
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|sysOut
operator|=
name|spy
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|sysOutStream
argument_list|)
argument_list|)
expr_stmt|;
name|sysErrStream
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|sysErr
operator|=
name|spy
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|sysErrStream
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetClusterNodeLabels ()
specifier|public
name|void
name|testGetClusterNodeLabels
parameter_list|()
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|client
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"label1"
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"label2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterCLI
name|cli
init|=
name|createAndGetClusterCLI
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|ClusterCLI
operator|.
name|CMD
block|,
literal|"-"
operator|+
name|ClusterCLI
operator|.
name|LIST_LABELS_CMD
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"Node Labels:<label1:exclusivity=true>,<label2:exclusivity=true>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetClusterNodeAttributes ()
specifier|public
name|void
name|testGetClusterNodeAttributes
parameter_list|()
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|client
operator|.
name|getClusterAttributes
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NodeAttributeInfo
operator|.
name|newInstance
argument_list|(
name|NodeAttributeKey
operator|.
name|newInstance
argument_list|(
literal|"GPU"
argument_list|)
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|)
argument_list|,
name|NodeAttributeInfo
operator|.
name|newInstance
argument_list|(
name|NodeAttributeKey
operator|.
name|newInstance
argument_list|(
literal|"CPU"
argument_list|)
argument_list|,
name|NodeAttributeType
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterCLI
name|cli
init|=
name|createAndGetClusterCLI
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|ClusterCLI
operator|.
name|CMD
block|,
literal|"-"
operator|+
name|ClusterCLI
operator|.
name|LIST_CLUSTER_ATTRIBUTES
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"rm.yarn.io/GPU(STRING)"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"rm.yarn.io/CPU(STRING)"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetClusterNodeLabelsWithLocalAccess ()
specifier|public
name|void
name|testGetClusterNodeLabelsWithLocalAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|client
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"remote1"
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"remote2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterCLI
name|cli
init|=
name|createAndGetClusterCLI
argument_list|()
decl_stmt|;
name|ClusterCLI
operator|.
name|localNodeLabelsManager
operator|=
name|mock
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ClusterCLI
operator|.
name|localNodeLabelsManager
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"local1"
argument_list|)
argument_list|,
name|NodeLabel
operator|.
name|newInstance
argument_list|(
literal|"local2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|rc
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|ClusterCLI
operator|.
name|CMD
block|,
literal|"-"
operator|+
name|ClusterCLI
operator|.
name|LIST_LABELS_CMD
block|,
literal|"-"
operator|+
name|ClusterCLI
operator|.
name|DIRECTLY_ACCESS_NODE_LABEL_STORE
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
comment|// it should return local* instead of remote*
name|pw
operator|.
name|print
argument_list|(
literal|"Node Labels:<local1:exclusivity=true>,<local2:exclusivity=true>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEmptyClusterNodeLabels ()
specifier|public
name|void
name|testGetEmptyClusterNodeLabels
parameter_list|()
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|client
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterCLI
name|cli
init|=
name|createAndGetClusterCLI
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|ClusterCLI
operator|.
name|CMD
block|,
literal|"-"
operator|+
name|ClusterCLI
operator|.
name|LIST_LABELS_CMD
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"Node Labels: "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterCLI
name|cli
init|=
name|createAndGetClusterCLI
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"cluster"
block|,
literal|"--help"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"usage: yarn cluster"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" -dnl,--directly-access-node-label-store   This is DEPRECATED, will be"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           removed in future releases."
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           Directly access node label"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           store, with this option, all"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           node label related operations"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           will NOT connect RM. Instead,"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           they will access/modify stored"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           node labels directly. By"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           default, it is false (access"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           via RM). AND PLEASE NOTE: if"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           you configured"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           yarn.node-labels.fs-store.root-"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           dir to a local directory"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           (instead of NFS or HDFS), this"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           option will only work when the"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           command run on the machine"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           where RM is running. Also, this"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           option is UNSTABLE, could be"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           removed in future releases."
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" -h,--help                                 Displays help for all commands."
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" -lna,--list-node-attributes               List cluster node-attribute"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           collection"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" -lnl,--list-node-labels                   List cluster node-label"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"                                           collection"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|sysOut
argument_list|)
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createAndGetClusterCLI ()
specifier|private
name|ClusterCLI
name|createAndGetClusterCLI
parameter_list|()
block|{
name|ClusterCLI
name|cli
init|=
operator|new
name|ClusterCLI
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|createAndStartYarnClient
parameter_list|()
block|{       }
block|}
decl_stmt|;
name|cli
operator|.
name|setClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|sysOut
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|sysErr
argument_list|)
expr_stmt|;
return|return
name|cli
return|;
block|}
block|}
end_class

end_unit

