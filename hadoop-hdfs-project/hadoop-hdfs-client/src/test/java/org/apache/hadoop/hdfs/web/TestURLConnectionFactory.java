begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|HttpURLConnection
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|ConnectionConfigurator
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
name|security
operator|.
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|security
operator|.
name|ssl
operator|.
name|SSLFactory
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
name|junit
operator|.
name|Assert
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
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestURLConnectionFactory
specifier|public
specifier|final
class|class
name|TestURLConnectionFactory
block|{
annotation|@
name|Test
DECL|method|testConnConfiguratior ()
specifier|public
name|void
name|testConnConfiguratior
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|URL
name|u
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|HttpURLConnection
argument_list|>
name|conns
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|URLConnectionFactory
name|fc
init|=
operator|new
name|URLConnectionFactory
argument_list|(
operator|new
name|ConnectionConfigurator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|u
argument_list|,
name|conn
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
name|conns
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|fc
operator|.
name|openConnection
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|conns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSLInitFailure ()
specifier|public
name|void
name|testSSLInitFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SSLFactory
operator|.
name|SSL_HOSTNAME_VERIFIER_KEY
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|LogCapturer
name|logs
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|URLConnectionFactory
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|URLConnectionFactory
operator|.
name|newDefaultURLConnectionFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected log for ssl init failure not found!"
argument_list|,
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Cannot load customized ssl related configuration"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSSLFactoryCleanup ()
specifier|public
name|void
name|testSSLFactoryCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|baseDir
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestURLConnectionFactory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|baseDir
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
name|String
name|keystoreDir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|sslConfDir
init|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestURLConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoreDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Configuration
name|sslConf
init|=
name|KeyStoreTestUtil
operator|.
name|getSslConfig
argument_list|()
decl_stmt|;
name|sslConf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"swebhdfs://localhost"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|sslConf
argument_list|)
decl_stmt|;
name|ThreadGroup
name|threadGroup
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadGroup
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|threadGroup
operator|=
name|threadGroup
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadGroup
operator|.
name|activeCount
argument_list|()
index|]
decl_stmt|;
name|threadGroup
operator|.
name|enumerate
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|Thread
name|reloaderThread
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
if|if
condition|(
operator|(
name|thread
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Truststore reloader thread"
argument_list|)
operator|)
condition|)
block|{
name|reloaderThread
operator|=
name|thread
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Reloader is not alive"
argument_list|,
name|reloaderThread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|boolean
name|reloaderStillAlive
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|reloaderStillAlive
operator|=
name|reloaderThread
operator|.
name|isAlive
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|reloaderStillAlive
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Reloader is still alive"
argument_list|,
name|reloaderStillAlive
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

