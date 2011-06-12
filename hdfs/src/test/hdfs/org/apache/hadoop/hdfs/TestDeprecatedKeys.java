begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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

begin_class
DECL|class|TestDeprecatedKeys
specifier|public
class|class
name|TestDeprecatedKeys
extends|extends
name|TestCase
block|{
comment|//Tests a deprecated key
DECL|method|testDeprecatedKeys ()
specifier|public
name|void
name|testDeprecatedKeys
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
name|set
argument_list|(
literal|"topology.script.file.name"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|String
name|scriptFile
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scriptFile
operator|.
name|equals
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"dfs.replication.interval"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|String
name|alpha
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
decl_stmt|;
name|int
name|repInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|repInterval
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

