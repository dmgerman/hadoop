begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
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
name|io
operator|.
name|IOUtils
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
name|minikdc
operator|.
name|MiniKdc
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
name|AfterClass
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
name|TestName
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
name|FileInputStream
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
name|util
operator|.
name|Properties
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
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
name|KDiag
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestKDiag
specifier|public
class|class
name|TestKDiag
extends|extends
name|Assert
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestKDiag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KEYLEN
specifier|public
specifier|static
specifier|final
name|String
name|KEYLEN
init|=
literal|"128"
decl_stmt|;
DECL|field|HDFS_SITE_XML
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_SITE_XML
init|=
literal|"org/apache/hadoop/security/secure-hdfs-site.xml"
decl_stmt|;
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
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
literal|30000
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|nameThread ()
specifier|public
specifier|static
name|void
name|nameThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit"
argument_list|)
expr_stmt|;
block|}
DECL|field|kdc
specifier|private
specifier|static
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|workDir
specifier|private
specifier|static
name|File
name|workDir
decl_stmt|;
DECL|field|keytab
specifier|private
specifier|static
name|File
name|keytab
decl_stmt|;
DECL|field|securityProperties
specifier|private
specifier|static
name|Properties
name|securityProperties
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startMiniKdc ()
specifier|public
specifier|static
name|void
name|startMiniKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|workDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.dir"
argument_list|,
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|securityProperties
operator|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
expr_stmt|;
name|kdc
operator|=
operator|new
name|MiniKdc
argument_list|(
name|securityProperties
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|keytab
operator|=
name|createKeytab
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"KERBEROS"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopMiniKdc ()
specifier|public
specifier|static
specifier|synchronized
name|void
name|stopMiniKdc
parameter_list|()
block|{
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
block|{
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|kdc
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|createKeytab (String...principals)
specifier|private
specifier|static
name|File
name|createKeytab
parameter_list|(
name|String
modifier|...
name|principals
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|keytab
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"keytab"
argument_list|)
decl_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytab
argument_list|,
name|principals
argument_list|)
expr_stmt|;
return|return
name|keytab
return|;
block|}
comment|/**    * Exec KDiag and expect a failure of a given category    * @param category category    * @param args args list    * @throws Exception any unexpected exception    */
DECL|method|kdiagFailure (String category, String ...args)
name|void
name|kdiagFailure
parameter_list|(
name|String
name|category
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|int
name|ex
init|=
name|exec
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Expected an exception in category {}, return code {}"
argument_list|,
name|category
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KerberosDiagsFailure
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getCategory
argument_list|()
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Expected an exception in category {}, got {}"
argument_list|,
name|category
argument_list|,
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|method|kdiag (String... args)
name|void
name|kdiag
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|KDiag
operator|.
name|exec
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicLoginFailure ()
specifier|public
name|void
name|testBasicLoginFailure
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_LOGIN
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicLoginSkipped ()
specifier|public
name|void
name|testBasicLoginSkipped
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_LOGIN
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_NOLOGIN
argument_list|)
expr_stmt|;
block|}
comment|/**    * This fails as the default cluster config is checked along with    * the CLI    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testSecure ()
specifier|public
name|void
name|testSecure
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_CONFIG
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_SECURE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoKeytab ()
specifier|public
name|void
name|testNoKeytab
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_KERBEROS
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_KEYTAB
argument_list|,
literal|"target/nofile"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeytabNoPrincipal ()
specifier|public
name|void
name|testKeytabNoPrincipal
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_KERBEROS
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConfIsSecure ()
specifier|public
name|void
name|testConfIsSecure
parameter_list|()
throws|throws
name|Throwable
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
operator|.
name|equals
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|SIMPLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeytabAndPrincipal ()
specifier|public
name|void
name|testKeytabAndPrincipal
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiag
argument_list|(
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKerberosName ()
specifier|public
name|void
name|testKerberosName
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_VERIFYSHORTNAME
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo/foo/foo@BAR.COM"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShortName ()
specifier|public
name|void
name|testShortName
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiag
argument_list|(
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
name|ARG_VERIFYSHORTNAME
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileOutput ()
specifier|public
name|void
name|testFileOutput
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"target/kdiag.txt"
argument_list|)
decl_stmt|;
name|kdiag
argument_list|(
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|,
name|ARG_OUTPUT
argument_list|,
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Output of {}"
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|dump
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadResource ()
specifier|public
name|void
name|testLoadResource
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiag
argument_list|(
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_RESOURCE
argument_list|,
name|HDFS_SITE_XML
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadInvalidResource ()
specifier|public
name|void
name|testLoadInvalidResource
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_CONFIG
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_RESOURCE
argument_list|,
literal|"no-such-resource.xml"
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequireJAAS ()
specifier|public
name|void
name|testRequireJAAS
parameter_list|()
throws|throws
name|Throwable
block|{
name|kdiagFailure
argument_list|(
name|CAT_JAAS
argument_list|,
name|ARG_KEYLEN
argument_list|,
name|KEYLEN
argument_list|,
name|ARG_JAAS
argument_list|,
name|ARG_KEYTAB
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|ARG_PRINCIPAL
argument_list|,
literal|"foo@EXAMPLE.COM"
argument_list|)
expr_stmt|;
block|}
comment|/*  commented out as once JVM gets configured, it stays configured   @Test(expected = IOException.class)   public void testKeytabUnknownPrincipal() throws Throwable {     kdiag(ARG_KEYLEN, KEYLEN,         ARG_KEYTAB, keytab.getAbsolutePath(),         ARG_PRINCIPAL, "bob@EXAMPLE.COM");   } */
comment|/**    * Dump any file to standard out.    * @param file file to dump    * @throws IOException IO problems    */
DECL|method|dump (File file)
specifier|private
name|void
name|dump
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|line
range|:
name|IOUtils
operator|.
name|readLines
argument_list|(
name|in
argument_list|)
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

