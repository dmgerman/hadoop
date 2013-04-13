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
name|java
operator|.
name|io
operator|.
name|File
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
name|unix
operator|.
name|DomainSocket
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
name|unix
operator|.
name|TemporarySocketDirectory
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
name|Assume
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
name|BeforeClass
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
name|*
import|;
end_import

begin_class
DECL|class|TestParallelShortCircuitRead
specifier|public
class|class
name|TestParallelShortCircuitRead
extends|extends
name|TestParallelReadUtil
block|{
DECL|field|sockDir
specifier|private
specifier|static
name|TemporarySocketDirectory
name|sockDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|static
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
operator|!=
literal|null
condition|)
return|return;
name|DFSInputStream
operator|.
name|tcpReadsDisabledForTesting
operator|=
literal|true
expr_stmt|;
name|sockDir
operator|=
operator|new
name|TemporarySocketDirectory
argument_list|()
expr_stmt|;
name|HdfsConfiguration
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
name|DFSConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|sockDir
operator|.
name|getDir
argument_list|()
argument_list|,
literal|"TestParallelLocalRead.%d.sock"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_SKIP_CHECKSUM_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DomainSocket
operator|.
name|disableBindPathValidation
argument_list|()
expr_stmt|;
name|setupCluster
argument_list|(
literal|1
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|static
specifier|public
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
operator|!=
literal|null
condition|)
return|return;
name|sockDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestParallelReadUtil
operator|.
name|teardownCluster
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

