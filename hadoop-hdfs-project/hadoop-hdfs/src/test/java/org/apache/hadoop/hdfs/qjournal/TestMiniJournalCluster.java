begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|URI
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
name|qjournal
operator|.
name|server
operator|.
name|JournalNode
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
DECL|class|TestMiniJournalCluster
specifier|public
class|class
name|TestMiniJournalCluster
block|{
annotation|@
name|Test
DECL|method|testStartStop ()
specifier|public
name|void
name|testStartStop
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniJournalCluster
name|c
init|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|c
operator|.
name|waitActive
argument_list|()
expr_stmt|;
try|try
block|{
name|URI
name|uri
init|=
name|c
operator|.
name|getQuorumJournalURI
argument_list|(
literal|"myjournal"
argument_list|)
decl_stmt|;
name|String
index|[]
name|addrs
init|=
name|uri
operator|.
name|getAuthority
argument_list|()
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|addrs
operator|.
name|length
argument_list|)
expr_stmt|;
name|JournalNode
name|node
init|=
name|c
operator|.
name|getJournalNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|dir
init|=
name|node
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"journalnode-0"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

