begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
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
name|fs
operator|.
name|FileUtil
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|LogCapturer
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
name|base
operator|.
name|Supplier
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
name|Test
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
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
operator|.
name|KeyStoreTestUtil
operator|.
name|createTrustStore
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
name|security
operator|.
name|ssl
operator|.
name|KeyStoreTestUtil
operator|.
name|generateCertificate
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
name|security
operator|.
name|ssl
operator|.
name|KeyStoreTestUtil
operator|.
name|generateKeyPair
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
name|assertFalse
import|;
end_import

begin_class
DECL|class|TestReloadingX509TrustManager
specifier|public
class|class
name|TestReloadingX509TrustManager
block|{
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestReloadingX509TrustManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cert1
specifier|private
name|X509Certificate
name|cert1
decl_stmt|;
DECL|field|cert2
specifier|private
name|X509Certificate
name|cert2
decl_stmt|;
DECL|field|reloaderLog
specifier|private
specifier|final
name|LogCapturer
name|reloaderLog
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|ReloadingX509TrustManager
operator|.
name|LOG
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testLoadMissingTrustStore ()
specifier|public
name|void
name|testLoadMissingTrustStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|truststoreLocation
init|=
name|BASEDIR
operator|+
literal|"/testmissing.jks"
decl_stmt|;
name|ReloadingX509TrustManager
name|tm
init|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
literal|"jks"
argument_list|,
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|tm
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tm
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testLoadCorruptTrustStore ()
specifier|public
name|void
name|testLoadCorruptTrustStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|truststoreLocation
init|=
name|BASEDIR
operator|+
literal|"/testcorrupt.jks"
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|truststoreLocation
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|ReloadingX509TrustManager
name|tm
init|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
literal|"jks"
argument_list|,
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|tm
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tm
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReload ()
specifier|public
name|void
name|testReload
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|kp
init|=
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|cert1
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert1"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|cert2
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert2"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|String
name|truststoreLocation
init|=
name|BASEDIR
operator|+
literal|"/testreload.jks"
decl_stmt|;
name|createTrustStore
argument_list|(
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|"cert1"
argument_list|,
name|cert1
argument_list|)
expr_stmt|;
specifier|final
name|ReloadingX509TrustManager
name|tm
init|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
literal|"jks"
argument_list|,
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|tm
operator|.
name|init
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Wait so that the file modification time is different
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|tm
operator|.
name|getReloadInterval
argument_list|()
operator|+
literal|1000
operator|)
argument_list|)
expr_stmt|;
comment|// Add another cert
name|Map
argument_list|<
name|String
argument_list|,
name|X509Certificate
argument_list|>
name|certs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|X509Certificate
argument_list|>
argument_list|()
decl_stmt|;
name|certs
operator|.
name|put
argument_list|(
literal|"cert1"
argument_list|,
name|cert1
argument_list|)
expr_stmt|;
name|certs
operator|.
name|put
argument_list|(
literal|"cert2"
argument_list|,
name|cert2
argument_list|)
expr_stmt|;
name|createTrustStore
argument_list|(
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
name|certs
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
operator|==
literal|2
return|;
block|}
block|}
argument_list|,
operator|(
name|int
operator|)
name|tm
operator|.
name|getReloadInterval
argument_list|()
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tm
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReloadMissingTrustStore ()
specifier|public
name|void
name|testReloadMissingTrustStore
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|kp
init|=
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|cert1
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert1"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|cert2
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert2"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|String
name|truststoreLocation
init|=
name|BASEDIR
operator|+
literal|"/testmissing.jks"
decl_stmt|;
name|createTrustStore
argument_list|(
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|"cert1"
argument_list|,
name|cert1
argument_list|)
expr_stmt|;
name|ReloadingX509TrustManager
name|tm
init|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
literal|"jks"
argument_list|,
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|tm
operator|.
name|init
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|X509Certificate
name|cert
init|=
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertFalse
argument_list|(
name|reloaderLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|ReloadingX509TrustManager
operator|.
name|RELOAD_ERROR_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
name|truststoreLocation
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|waitForFailedReloadAtLeastOnce
argument_list|(
operator|(
name|int
operator|)
name|tm
operator|.
name|getReloadInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cert
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reloaderLog
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
name|tm
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testReloadCorruptTrustStore ()
specifier|public
name|void
name|testReloadCorruptTrustStore
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyPair
name|kp
init|=
name|generateKeyPair
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|cert1
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert1"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|cert2
operator|=
name|generateCertificate
argument_list|(
literal|"CN=Cert2"
argument_list|,
name|kp
argument_list|,
literal|30
argument_list|,
literal|"SHA1withRSA"
argument_list|)
expr_stmt|;
name|String
name|truststoreLocation
init|=
name|BASEDIR
operator|+
literal|"/testcorrupt.jks"
decl_stmt|;
name|createTrustStore
argument_list|(
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|"cert1"
argument_list|,
name|cert1
argument_list|)
expr_stmt|;
name|ReloadingX509TrustManager
name|tm
init|=
operator|new
name|ReloadingX509TrustManager
argument_list|(
literal|"jks"
argument_list|,
name|truststoreLocation
argument_list|,
literal|"password"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
try|try
block|{
name|tm
operator|.
name|init
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|X509Certificate
name|cert
init|=
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
comment|// Wait so that the file modification time is different
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|tm
operator|.
name|getReloadInterval
argument_list|()
operator|+
literal|1000
operator|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|reloaderLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|ReloadingX509TrustManager
operator|.
name|RELOAD_ERROR_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|truststoreLocation
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|waitForFailedReloadAtLeastOnce
argument_list|(
operator|(
name|int
operator|)
name|tm
operator|.
name|getReloadInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cert
argument_list|,
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reloaderLog
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
name|tm
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**Wait for the reloader thread to load the configurations at least once    * by probing the log of the thread if the reload fails.    */
DECL|method|waitForFailedReloadAtLeastOnce (int reloadInterval)
specifier|private
name|void
name|waitForFailedReloadAtLeastOnce
parameter_list|(
name|int
name|reloadInterval
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|reloaderLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|ReloadingX509TrustManager
operator|.
name|RELOAD_ERROR_MESSAGE
argument_list|)
return|;
block|}
block|}
argument_list|,
name|reloadInterval
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

