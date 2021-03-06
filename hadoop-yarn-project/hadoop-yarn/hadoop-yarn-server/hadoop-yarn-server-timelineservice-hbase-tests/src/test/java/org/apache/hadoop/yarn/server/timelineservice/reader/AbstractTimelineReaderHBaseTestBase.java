begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
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
name|assertNotNull
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|net
operator|.
name|URL
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|hbase
operator|.
name|HBaseTestingUtility
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
name|timelineservice
operator|.
name|FlowActivityEntity
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
name|conf
operator|.
name|YarnConfiguration
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
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|DataGeneratorForTest
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
name|webapp
operator|.
name|YarnJacksonJaxbJsonProvider
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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|GenericType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|config
operator|.
name|ClientConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|config
operator|.
name|DefaultClientConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|client
operator|.
name|urlconnection
operator|.
name|HttpURLConnectionFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|client
operator|.
name|urlconnection
operator|.
name|URLConnectionClientHandler
import|;
end_import

begin_comment
comment|/**  * Test Base for TimelineReaderServer HBase tests.  */
end_comment

begin_class
DECL|class|AbstractTimelineReaderHBaseTestBase
specifier|public
specifier|abstract
class|class
name|AbstractTimelineReaderHBaseTestBase
block|{
DECL|field|serverPort
specifier|private
specifier|static
name|int
name|serverPort
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|TimelineReaderServer
name|server
decl_stmt|;
DECL|field|util
specifier|private
specifier|static
name|HBaseTestingUtility
name|util
decl_stmt|;
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|=
operator|new
name|HBaseTestingUtility
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|util
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"hfile.format.version"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|util
operator|.
name|startMiniCluster
argument_list|()
expr_stmt|;
name|DataGeneratorForTest
operator|.
name|createSchema
argument_list|(
name|util
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|util
operator|!=
literal|null
condition|)
block|{
name|util
operator|.
name|shutdownMiniCluster
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initialize ()
specifier|protected
specifier|static
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Configuration
name|config
init|=
name|util
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_VERSION
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_READER_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"cluster1"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_READER_CLASS
argument_list|,
literal|"org.apache.hadoop.yarn.server.timelineservice.storage."
operator|+
literal|"HBaseTimelineReaderImpl"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
literal|"hfile.format.version"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|TimelineReaderServer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|addFilters
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// The parent code uses hadoop-common jar from this version of
comment|// Hadoop, but the tests are using hadoop-common jar from
comment|// ${hbase-compatible-hadoop.version}.  This version uses Jetty 9
comment|// while ${hbase-compatible-hadoop.version} uses Jetty 6, and there
comment|// are many differences, including classnames and packages.
comment|// We do nothing here, so that we don't cause a NoSuchMethodError or
comment|// NoClassDefFoundError.
comment|// Once ${hbase-compatible-hadoop.version} is changed to Hadoop 3,
comment|// we should be able to remove this @Override.
block|}
block|}
expr_stmt|;
name|server
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|serverPort
operator|=
name|server
operator|.
name|getWebServerPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Web server failed to start"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createClient ()
specifier|protected
name|Client
name|createClient
parameter_list|()
block|{
name|ClientConfig
name|cfg
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|getClasses
argument_list|()
operator|.
name|add
argument_list|(
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
operator|new
name|Client
argument_list|(
operator|new
name|URLConnectionClientHandler
argument_list|(
operator|new
name|DummyURLConnectionFactory
argument_list|()
argument_list|)
argument_list|,
name|cfg
argument_list|)
return|;
block|}
DECL|method|getResponse (Client client, URI uri)
specifier|protected
name|ClientResponse
name|getResponse
parameter_list|(
name|Client
name|client
parameter_list|,
name|URI
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|ClientResponse
name|resp
init|=
name|client
operator|.
name|resource
argument_list|(
name|uri
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|==
literal|null
operator|||
name|resp
operator|.
name|getStatusInfo
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|resp
operator|!=
literal|null
condition|)
block|{
name|msg
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|resp
operator|.
name|getStatusInfo
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incorrect response from timeline reader. "
operator|+
literal|"Status="
operator|+
name|msg
argument_list|)
throw|;
block|}
return|return
name|resp
return|;
block|}
DECL|method|verifyHttpResponse (Client client, URI uri, Status status)
specifier|protected
name|void
name|verifyHttpResponse
parameter_list|(
name|Client
name|client
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Status
name|status
parameter_list|)
block|{
name|ClientResponse
name|resp
init|=
name|client
operator|.
name|resource
argument_list|(
name|uri
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Response from server should have been "
operator|+
name|status
argument_list|,
name|resp
operator|.
name|getStatusInfo
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|==
name|status
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Response is: "
operator|+
name|resp
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyFlowEntites (Client client, URI uri, int noOfEntities)
specifier|protected
name|List
argument_list|<
name|FlowActivityEntity
argument_list|>
name|verifyFlowEntites
parameter_list|(
name|Client
name|client
parameter_list|,
name|URI
name|uri
parameter_list|,
name|int
name|noOfEntities
parameter_list|)
throws|throws
name|Exception
block|{
name|ClientResponse
name|resp
init|=
name|getResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FlowActivityEntity
argument_list|>
name|entities
init|=
name|resp
operator|.
name|getEntity
argument_list|(
operator|new
name|GenericType
argument_list|<
name|List
argument_list|<
name|FlowActivityEntity
argument_list|>
argument_list|>
argument_list|()
block|{         }
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entities
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|noOfEntities
argument_list|,
name|entities
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entities
return|;
block|}
DECL|class|DummyURLConnectionFactory
specifier|protected
specifier|static
class|class
name|DummyURLConnectionFactory
implements|implements
name|HttpURLConnectionFactory
block|{
annotation|@
name|Override
DECL|method|getHttpURLConnection (final URL url)
specifier|public
name|HttpURLConnection
name|getHttpURLConnection
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getHBaseTestingUtility ()
specifier|protected
specifier|static
name|HBaseTestingUtility
name|getHBaseTestingUtility
parameter_list|()
block|{
return|return
name|util
return|;
block|}
DECL|method|getServerPort ()
specifier|public
specifier|static
name|int
name|getServerPort
parameter_list|()
block|{
return|return
name|serverPort
return|;
block|}
block|}
end_class

end_unit

