begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfsproxy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfsproxy
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|HdfsConfiguration
import|;
end_import

begin_comment
comment|/** Unit tests for ProxyUtil */
end_comment

begin_class
DECL|class|TestProxyUtil
specifier|public
class|class
name|TestProxyUtil
extends|extends
name|TestCase
block|{
DECL|field|TEST_PROXY_CONF_DIR
specifier|private
specifier|static
name|String
name|TEST_PROXY_CONF_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.proxy.conf.dir"
argument_list|,
literal|"./conf"
argument_list|)
decl_stmt|;
DECL|field|TEST_PROXY_HTTPS_PORT
specifier|private
specifier|static
name|String
name|TEST_PROXY_HTTPS_PORT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.proxy.https.port"
argument_list|,
literal|"8443"
argument_list|)
decl_stmt|;
DECL|method|testSendCommand ()
specifier|public
name|void
name|testSendCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
literal|"ssl-client.xml"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
literal|"hdfsproxy-default.xml"
argument_list|)
expr_stmt|;
name|String
name|address
init|=
literal|"localhost:"
operator|+
name|TEST_PROXY_HTTPS_PORT
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hdfsproxy.https.address"
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|String
name|hostFname
init|=
name|TEST_PROXY_CONF_DIR
operator|+
literal|"/hdfsproxy-hosts"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hdfsproxy.hosts"
argument_list|,
name|hostFname
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ProxyUtil
operator|.
name|sendCommand
argument_list|(
name|conf
argument_list|,
literal|"/test/reloadPermFiles"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hdfsproxy.https.address"
argument_list|,
literal|"localhost:7777"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ProxyUtil
operator|.
name|sendCommand
argument_list|(
name|conf
argument_list|,
literal|"/test/reloadPermFiles"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ProxyUtil
operator|.
name|sendCommand
argument_list|(
name|conf
argument_list|,
literal|"/test/reloadPermFiles"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

