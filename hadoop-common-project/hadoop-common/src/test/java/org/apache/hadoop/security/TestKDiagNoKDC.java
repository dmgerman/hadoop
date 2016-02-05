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
name|ARG_KEYLEN
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
name|ARG_KEYTAB
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
name|ARG_NOFAIL
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
name|ARG_NOLOGIN
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
name|ARG_PRINCIPAL
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
name|ARG_SECURE
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
name|CAT_CONFIG
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
name|CAT_KERBEROS
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
name|CAT_LOGIN
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
name|KerberosDiagsFailure
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
name|exec
import|;
end_import

begin_class
DECL|class|TestKDiagNoKDC
specifier|public
class|class
name|TestKDiagNoKDC
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
name|TestKDiagNoKDC
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
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|int
name|kdiag
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|KDiag
operator|.
name|exec
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/**    * Test that the core kdiag command works when there's no KDC around.    * This test produces different outcomes on hosts where there is a default    * KDC -it needs to work on hosts without kerberos as well as those with it.    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testKDiagStandalone ()
specifier|public
name|void
name|testKDiagStandalone
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
DECL|method|testKDiagNoLogin ()
specifier|public
name|void
name|testKDiagNoLogin
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
annotation|@
name|Test
DECL|method|testKDiagStandaloneNofail ()
specifier|public
name|void
name|testKDiagStandaloneNofail
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
name|ARG_NOFAIL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKDiagUsage ()
specifier|public
name|void
name|testKDiagUsage
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|kdiag
argument_list|(
literal|"usage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

