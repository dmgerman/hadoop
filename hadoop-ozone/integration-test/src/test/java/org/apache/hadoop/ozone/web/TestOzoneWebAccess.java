begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
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
name|ozone
operator|.
name|MiniOzoneClassicCluster
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|client
operator|.
name|rest
operator|.
name|headers
operator|.
name|Header
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClients
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
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|HttpHeaders
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_CREATED
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
operator|.
name|getRequestID
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

begin_comment
comment|/**  * Test Ozone Access through REST protocol.  */
end_comment

begin_class
DECL|class|TestOzoneWebAccess
specifier|public
class|class
name|TestOzoneWebAccess
block|{
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneClassicCluster
name|cluster
decl_stmt|;
DECL|field|port
specifier|private
specifier|static
name|int
name|port
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "local" , which uses a local directory to    * emulate Ozone backend.    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestOzoneWebAccess
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_LOCAL
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|port
operator|=
name|MiniOzoneClassicCluster
operator|.
name|getOzoneRestPort
argument_list|(
name|dataNode
argument_list|)
expr_stmt|;
block|}
comment|/**    * shutdown MiniOzoneCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
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
block|}
comment|/**    * Send a vaild Ozone Request.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testOzoneRequest ()
specifier|public
name|void
name|testOzoneRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, dd MMM yyyy HH:mm:ss ZZZ"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|CloseableHttpClient
name|client
init|=
name|HttpClients
operator|.
name|createDefault
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
try|try
block|{
name|HttpPost
name|httppost
init|=
operator|new
name|HttpPost
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d/%s"
argument_list|,
name|port
argument_list|,
name|volumeName
argument_list|)
argument_list|)
decl_stmt|;
name|httppost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_VERSION_HEADER
argument_list|,
name|Header
operator|.
name|OZONE_V1_VERSION_HEADER
argument_list|)
expr_stmt|;
name|httppost
operator|.
name|addHeader
argument_list|(
name|HttpHeaders
operator|.
name|DATE
argument_list|,
name|format
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httppost
operator|.
name|addHeader
argument_list|(
name|HttpHeaders
operator|.
name|AUTHORIZATION
argument_list|,
name|Header
operator|.
name|OZONE_SIMPLE_AUTHENTICATION_SCHEME
operator|+
literal|" "
operator|+
name|OzoneConsts
operator|.
name|OZONE_SIMPLE_HDFS_USER
argument_list|)
expr_stmt|;
name|httppost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_USER
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_SIMPLE_HDFS_USER
argument_list|)
expr_stmt|;
name|HttpResponse
name|response
init|=
name|client
operator|.
name|execute
argument_list|(
name|httppost
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|toString
argument_list|()
argument_list|,
name|HTTP_CREATED
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

