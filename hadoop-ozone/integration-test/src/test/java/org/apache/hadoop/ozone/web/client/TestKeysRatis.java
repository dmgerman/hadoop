begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.client
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
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|MiniOzoneCluster
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
name|ozone
operator|.
name|RatisTestHelper
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
name|protocol
operator|.
name|ClientProtocol
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
name|Ignore
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
name|client
operator|.
name|TestKeys
operator|.
name|PutHelper
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
name|client
operator|.
name|TestKeys
operator|.
name|getMultiPartKey
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestGetKeyInfo
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestPutAndDeleteKey
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestPutAndGetKey
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestPutAndGetKeyWithDnRestart
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestPutAndListKey
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
name|client
operator|.
name|TestKeys
operator|.
name|runTestPutKey
import|;
end_import

begin_comment
comment|/** The same as {@link TestKeys} except that this test is Ratis enabled. */
end_comment

begin_class
DECL|class|TestKeysRatis
specifier|public
class|class
name|TestKeysRatis
block|{
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
DECL|field|suite
specifier|private
specifier|static
name|RatisTestHelper
operator|.
name|RatisTestSuite
name|suite
decl_stmt|;
DECL|field|ozoneCluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|ozoneCluster
init|=
literal|null
decl_stmt|;
DECL|field|path
specifier|static
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|ClientProtocol
name|client
init|=
literal|null
decl_stmt|;
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
name|suite
operator|=
operator|new
name|RatisTestHelper
operator|.
name|RatisTestSuite
argument_list|(
name|TestBucketsRatis
operator|.
name|class
argument_list|)
expr_stmt|;
name|path
operator|=
name|suite
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|)
expr_stmt|;
name|ozoneCluster
operator|=
name|suite
operator|.
name|getCluster
argument_list|()
expr_stmt|;
name|ozoneCluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|client
operator|=
name|suite
operator|.
name|newOzoneClient
argument_list|()
expr_stmt|;
block|}
comment|/**    * shutdown MiniDFSCluster.    */
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
name|suite
operator|!=
literal|null
condition|)
block|{
name|suite
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutKey ()
specifier|public
name|void
name|testPutKey
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestPutKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestPutKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"disabling for now, datanodes restart with ratis is buggy"
argument_list|)
annotation|@
name|Test
DECL|method|testPutAndGetKeyWithDnRestart ()
specifier|public
name|void
name|testPutAndGetKeyWithDnRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestPutAndGetKeyWithDnRestart
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|,
name|ozoneCluster
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestPutAndGetKeyWithDnRestart
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|,
name|ozoneCluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndGetKey ()
specifier|public
name|void
name|testPutAndGetKey
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestPutAndGetKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestPutAndGetKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndDeleteKey ()
specifier|public
name|void
name|testPutAndDeleteKey
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestPutAndDeleteKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestPutAndDeleteKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutAndListKey ()
specifier|public
name|void
name|testPutAndListKey
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestPutAndListKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestPutAndListKey
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetKeyInfo ()
specifier|public
name|void
name|testGetKeyInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestGetKeyInfo
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|delimiter
init|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|runTestGetKeyInfo
argument_list|(
operator|new
name|PutHelper
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
name|getMultiPartKey
argument_list|(
name|delimiter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

