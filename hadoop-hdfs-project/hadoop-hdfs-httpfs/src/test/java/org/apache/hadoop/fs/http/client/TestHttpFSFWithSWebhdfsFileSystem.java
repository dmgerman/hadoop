begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
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
name|hdfs
operator|.
name|web
operator|.
name|SWebHdfsFileSystem
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
name|TestJettyHelper
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|UUID
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestHttpFSFWithSWebhdfsFileSystem
specifier|public
class|class
name|TestHttpFSFWithSWebhdfsFileSystem
extends|extends
name|TestHttpFSWithHttpFSFileSystem
block|{
DECL|field|classpathDir
specifier|private
specifier|static
name|String
name|classpathDir
decl_stmt|;
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
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|keyStoreDir
specifier|private
specifier|static
name|String
name|keyStoreDir
decl_stmt|;
DECL|field|sslConf
specifier|private
specifier|static
name|Configuration
name|sslConf
decl_stmt|;
block|{
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"classutils.txt"
argument_list|)
decl_stmt|;
name|classpathDir
operator|=
name|url
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
if|if
condition|(
name|classpathDir
operator|.
name|startsWith
argument_list|(
literal|"file:"
argument_list|)
condition|)
block|{
name|classpathDir
operator|=
name|classpathDir
operator|.
name|substring
argument_list|(
literal|"file:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|classpathDir
operator|=
name|classpathDir
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|classpathDir
operator|.
name|length
argument_list|()
operator|-
literal|"/classutils.txt"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot find test classes dir"
argument_list|)
throw|;
block|}
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
parameter_list|(
name|base
parameter_list|)
constructor_decl|;
name|base
operator|.
name|mkdirs
parameter_list|()
constructor_decl|;
name|keyStoreDir
operator|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
try|try
block|{
name|sslConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keyStoreDir
argument_list|,
name|classpathDir
argument_list|,
name|sslConf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|jettyTestHelper
operator|=
operator|new
name|TestJettyHelper
argument_list|(
literal|"jks"
argument_list|,
name|keyStoreDir
operator|+
literal|"/serverKS.jks"
argument_list|,
literal|"serverP"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanUp ()
specifier|public
specifier|static
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|File
argument_list|(
name|classpathDir
argument_list|,
literal|"ssl-client.xml"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|classpathDir
argument_list|,
literal|"ssl-server.xml"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keyStoreDir
argument_list|,
name|classpathDir
argument_list|)
expr_stmt|;
block|}
DECL|method|TestHttpFSFWithSWebhdfsFileSystem (Operation operation)
specifier|public
name|TestHttpFSFWithSWebhdfsFileSystem
parameter_list|(
name|Operation
name|operation
parameter_list|)
block|{
name|super
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileSystemClass ()
specifier|protected
name|Class
name|getFileSystemClass
parameter_list|()
block|{
return|return
name|SWebHdfsFileSystem
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|getScheme ()
specifier|protected
name|String
name|getScheme
parameter_list|()
block|{
return|return
literal|"swebhdfs"
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpFSFileSystem ()
specifier|protected
name|FileSystem
name|getHttpFSFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|sslConf
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.swebhdfs.impl"
argument_list|,
name|getFileSystemClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"swebhdfs://"
operator|+
name|TestJettyHelper
operator|.
name|getJettyURL
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|getAuthority
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

