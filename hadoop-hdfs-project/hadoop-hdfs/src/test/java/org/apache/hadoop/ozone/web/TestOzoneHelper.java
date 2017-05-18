begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|ozone
operator|.
name|web
operator|.
name|exceptions
operator|.
name|ErrorTable
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
name|web
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|HttpGet
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
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_OK
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
comment|/**  * Helper functions to test Ozone.  */
end_comment

begin_class
DECL|class|TestOzoneHelper
specifier|public
class|class
name|TestOzoneHelper
block|{
DECL|method|createHttpClient ()
specifier|public
name|CloseableHttpClient
name|createHttpClient
parameter_list|()
block|{
return|return
name|HttpClients
operator|.
name|createDefault
argument_list|()
return|;
block|}
comment|/**    * Creates Volumes on Ozone Store.    *    * @throws IOException    */
DECL|method|testCreateVolumes (int port)
specifier|public
name|void
name|testCreateVolumes
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
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
name|monotonicNow
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
comment|/**    * Create Volumes with Quota.    *    * @throws IOException    */
DECL|method|testCreateVolumesWithQuota (int port)
specifier|public
name|void
name|testCreateVolumesWithQuota
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
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
literal|"http://localhost:%d/%s?quota=10TB"
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
name|monotonicNow
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
comment|/**    * Create Volumes with Invalid Quota.    *    * @throws IOException    */
DECL|method|testCreateVolumesWithInvalidQuota (int port)
specifier|public
name|void
name|testCreateVolumesWithInvalidQuota
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
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
literal|"http://localhost:%d/%s?quota=NaN"
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
name|monotonicNow
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
name|ErrorTable
operator|.
name|MALFORMED_QUOTA
operator|.
name|getHttpCode
argument_list|()
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
comment|/**    * To create a volume a user name must be specified using OZONE_USER header.    * This test verifies that we get an error in case we call without a OZONE    * user name.    *    * @throws IOException    */
DECL|method|testCreateVolumesWithInvalidUser (int port)
specifier|public
name|void
name|testCreateVolumesWithInvalidUser
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
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
literal|"http://localhost:%d/%s?quota=1TB"
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
name|monotonicNow
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
name|ErrorTable
operator|.
name|USER_NOT_FOUND
operator|.
name|getHttpCode
argument_list|()
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
comment|/**    * Only Admins can create volumes in Ozone. This test uses simple userauth as    * backend and hdfs and root are admin users in the simple backend.    *<p>    * This test tries to create a volume as user bilbo.    *    * @throws IOException    */
DECL|method|testCreateVolumesWithOutAdminRights (int port)
specifier|public
name|void
name|testCreateVolumesWithOutAdminRights
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
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
literal|"http://localhost:%d/%s?quota=NaN"
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
name|monotonicNow
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
literal|"bilbo"
argument_list|)
expr_stmt|;
comment|// This is not a root user in Simple Auth
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
name|ErrorTable
operator|.
name|ACCESS_DENIED
operator|.
name|getHttpCode
argument_list|()
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
comment|/**    * Create a bunch of volumes in a loop.    *    * @throws IOException    */
DECL|method|testCreateVolumesInLoop (int port)
specifier|public
name|void
name|testCreateVolumesInLoop
parameter_list|(
name|int
name|port
parameter_list|)
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
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|1000
condition|;
name|x
operator|++
control|)
block|{
name|CloseableHttpClient
name|client
init|=
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
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
literal|"http://localhost:%d/%s?quota=10TB"
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
name|monotonicNow
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
name|userName
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
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get volumes owned by the user.    *    * @throws IOException    */
DECL|method|testGetVolumesByUser (int port)
specifier|public
name|void
name|testGetVolumesByUser
parameter_list|(
name|int
name|port
parameter_list|)
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
comment|// We need to create a volume for this test to succeed.
name|testCreateVolumes
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|CloseableHttpClient
name|client
init|=
name|createHttpClient
argument_list|()
decl_stmt|;
try|try
block|{
name|HttpGet
name|httpget
init|=
operator|new
name|HttpGet
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d/"
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|httpget
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
name|httpget
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
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpget
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
name|httpget
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
name|httpget
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|toString
argument_list|()
argument_list|,
name|HTTP_OK
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
comment|/**    * Admins can read volumes belonging to other users.    *    * @throws IOException    */
DECL|method|testGetVolumesOfAnotherUser (int port)
specifier|public
name|void
name|testGetVolumesOfAnotherUser
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
try|try
block|{
name|HttpGet
name|httpget
init|=
operator|new
name|HttpGet
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d/"
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|httpget
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
name|httpget
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
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpget
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
name|OZONE_SIMPLE_ROOT_USER
argument_list|)
expr_stmt|;
comment|// User Root is getting volumes belonging to user HDFS
name|httpget
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
name|httpget
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|toString
argument_list|()
argument_list|,
name|HTTP_OK
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
comment|/**    * if you try to read volumes belonging to another user,    * then server always ignores it.    *    * @throws IOException    */
DECL|method|testGetVolumesOfAnotherUserShouldFail (int port)
specifier|public
name|void
name|testGetVolumesOfAnotherUserShouldFail
parameter_list|(
name|int
name|port
parameter_list|)
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
name|createHttpClient
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
try|try
block|{
name|HttpGet
name|httpget
init|=
operator|new
name|HttpGet
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"http://localhost:%d/"
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|httpget
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
name|httpget
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
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpget
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
name|userName
argument_list|)
expr_stmt|;
comment|// userName is NOT a root user, hence he should NOT be able to read the
comment|// volumes of user HDFS
name|httpget
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
name|httpget
argument_list|)
decl_stmt|;
comment|// We will get an Error called userNotFound when using Simple Auth Scheme
name|assertEquals
argument_list|(
name|response
operator|.
name|toString
argument_list|()
argument_list|,
name|ErrorTable
operator|.
name|USER_NOT_FOUND
operator|.
name|getHttpCode
argument_list|()
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

