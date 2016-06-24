begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.command
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
name|diskbalancer
operator|.
name|command
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|allOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|junit
operator|.
name|Assert
operator|.
name|assertThat
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
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
name|lang
operator|.
name|StringUtils
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
operator|.
name|ClusterConnector
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
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
operator|.
name|ConnectorFactory
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
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerCluster
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
name|Lists
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
operator|.
name|CANCEL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
operator|.
name|HELP
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
operator|.
name|NODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
operator|.
name|PLAN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
operator|.
name|QUERY
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_comment
comment|/**  * Tests various CLI commands of DiskBalancer.  */
end_comment

begin_class
DECL|class|TestDiskBalancerCommand
specifier|public
class|class
name|TestDiskBalancerCommand
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|clusterJson
specifier|private
name|URI
name|clusterJson
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
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
literal|3
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
literal|2
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
name|clusterJson
operator|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/diskBalancer/data-cluster-64node-3disk.json"
argument_list|)
operator|.
name|toURI
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
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
comment|// Just make sure we can shutdown datanodes.
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* test basic report */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportSimple ()
specifier|public
name|void
name|testReportSimple
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cmdLine
init|=
literal|"hdfs diskbalancer -report"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Processing report command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"No top limit specified"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"using default top value"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"100"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Reporting top"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"64"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"DataNode(s) benefiting from running DiskBalancer"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|32
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"30/64 null[null:0]"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"9 volumes with node data density 1.97"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* test less than 64 DataNode(s) as total, e.g., -report -top 32 */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportLessThanTotal ()
specifier|public
name|void
name|testReportLessThanTotal
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cmdLine
init|=
literal|"hdfs diskbalancer -report -top 32"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Processing report command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Reporting top"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"32"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"DataNode(s) benefiting from running DiskBalancer"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|31
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"30/32 null[null:0]"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"9 volumes with node data density 1.97"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* test more than 64 DataNode(s) as total, e.g., -report -top 128 */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportMoreThanTotal ()
specifier|public
name|void
name|testReportMoreThanTotal
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cmdLine
init|=
literal|"hdfs diskbalancer -report -top 128"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Processing report command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Reporting top"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"64"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"DataNode(s) benefiting from running DiskBalancer"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|31
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"30/64 null[null:0]"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"9 volumes with node data density 1.97"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* test invalid top limit, e.g., -report -top xx */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportInvalidTopLimit ()
specifier|public
name|void
name|testReportInvalidTopLimit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cmdLine
init|=
literal|"hdfs diskbalancer -report -top xx"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Processing report command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Top limit input is not numeric"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"using default top value"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"100"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Reporting top"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"64"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"DataNode(s) benefiting from running DiskBalancer"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|32
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"30/64 null[null:0]"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"9 volumes with node data density 1.97"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportNode ()
specifier|public
name|void
name|testReportNode
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cmdLine
init|=
literal|"hdfs diskbalancer -report -node "
operator|+
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"Processing report command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"Reporting volume information for DataNode"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"null[null:0]"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"9 volumes with node data density 1.97"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/KmHefYNURo"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.20 used: 39160240782/200000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.80 free: 160839759218/200000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/Mxfcfmb24Y"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.92 used: 733099315216/800000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.08 free: 66900684784/800000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/xx3j3ph3zd"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.72 used: 289544224916/400000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.28 free: 110455775084/400000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"RAM_DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/BoBlQFxhfw"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.60 used: 477590453390/800000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.40 free: 322409546610/800000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"RAM_DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/DtmAygEU6f"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.34 used: 134602910470/400000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.66 free: 265397089530/400000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"RAM_DISK"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/MXRyYsCz3U"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.55 used: 438102096853/800000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.45 free: 361897903147/800000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|9
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"SSD"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/BGe09Y77dI"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.89 used: 890446265501/1000000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.11 free: 109553734499/1000000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|10
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"SSD"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/JX3H8iHggM"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.31 used: 2782614512957/9000000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.69 free: 6217385487043/9000000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
literal|11
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"SSD"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/tmp/disk/uLOYmVZfWV"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.75 used: 1509592146007/2000000000000"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"0.25 free: 490407853993/2000000000000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReadClusterFromJson ()
specifier|public
name|void
name|testReadClusterFromJson
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterConnector
name|jsonConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|clusterJson
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|jsonConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|64
argument_list|,
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* test -plan  DataNodeID */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testPlanNode ()
specifier|public
name|void
name|testPlanNode
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|planArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s %s"
argument_list|,
name|PLAN
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer %s"
argument_list|,
name|planArg
argument_list|)
decl_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
comment|/* Test that illegal arguments are handled correctly*/
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testIllegalArgument ()
specifier|public
name|void
name|testIllegalArgument
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|planArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s %s"
argument_list|,
name|PLAN
argument_list|,
literal|"a87654a9-54c7-4693-8dd9-c9c7021dc340"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer %s -report"
argument_list|,
name|planArg
argument_list|)
decl_stmt|;
comment|// -plan and -report cannot be used together.
comment|// tests the validate command line arguments function.
name|thrown
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCancelCommand ()
specifier|public
name|void
name|testCancelCommand
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|cancelArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s %s"
argument_list|,
name|CANCEL
argument_list|,
literal|"nosuchplan"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nodeArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s %s"
argument_list|,
name|NODE
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
comment|// Port:Host format is expected. So cancel command will throw.
name|thrown
operator|.
name|expect
argument_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer  %s %s"
argument_list|,
name|cancelArg
argument_list|,
name|nodeArg
argument_list|)
decl_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
expr_stmt|;
block|}
comment|/*    Makes an invalid query attempt to non-existent Datanode.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testQueryCommand ()
specifier|public
name|void
name|testQueryCommand
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|queryArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s %s"
argument_list|,
name|QUERY
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|java
operator|.
name|net
operator|.
name|UnknownHostException
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer %s"
argument_list|,
name|queryArg
argument_list|)
decl_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testHelpCommand ()
specifier|public
name|void
name|testHelpCommand
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|helpArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-%s"
argument_list|,
name|HELP
argument_list|)
decl_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer %s"
argument_list|,
name|helpArg
argument_list|)
decl_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
expr_stmt|;
block|}
DECL|method|runCommandInternal (final String cmdLine)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|runCommandInternal
parameter_list|(
specifier|final
name|String
name|cmdLine
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|cmds
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|cmdLine
argument_list|,
literal|' '
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
name|db
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bufOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bufOut
argument_list|)
decl_stmt|;
name|db
operator|.
name|run
argument_list|(
name|cmds
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|bufOut
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|outputs
operator|.
name|add
argument_list|(
name|scanner
operator|.
name|nextLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|outputs
return|;
block|}
DECL|method|runCommand (final String cmdLine)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|runCommand
parameter_list|(
specifier|final
name|String
name|cmdLine
parameter_list|)
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|clusterJson
argument_list|)
expr_stmt|;
return|return
name|runCommandInternal
argument_list|(
name|cmdLine
argument_list|)
return|;
block|}
DECL|method|runCommand (final String cmdLine, MiniDFSCluster miniCluster)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|runCommand
parameter_list|(
specifier|final
name|String
name|cmdLine
parameter_list|,
name|MiniDFSCluster
name|miniCluster
parameter_list|)
throws|throws
name|Exception
block|{
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|miniCluster
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|runCommandInternal
argument_list|(
name|cmdLine
argument_list|)
return|;
block|}
comment|/**    * Making sure that we can query the node without having done a submit.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testDiskBalancerQueryWithoutSubmit ()
specifier|public
name|void
name|testDiskBalancerQueryWithoutSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDatanodes
init|=
literal|2
decl_stmt|;
name|MiniDFSCluster
name|miniDFSCluster
init|=
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
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|miniDFSCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|miniDFSCluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|queryArg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-query localhost:%d"
argument_list|,
name|dataNode
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|cmdLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"hdfs diskbalancer %s"
argument_list|,
name|queryArg
argument_list|)
decl_stmt|;
name|runCommand
argument_list|(
name|cmdLine
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|miniDFSCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

