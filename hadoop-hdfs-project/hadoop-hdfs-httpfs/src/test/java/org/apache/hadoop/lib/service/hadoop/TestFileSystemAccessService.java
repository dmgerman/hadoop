begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
operator|.
name|hadoop
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|CommonConfigurationKeysPublic
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
name|Path
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
name|lib
operator|.
name|server
operator|.
name|Server
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccess
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccessException
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
name|lib
operator|.
name|service
operator|.
name|instrumentation
operator|.
name|InstrumentationService
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
name|HFSTestCase
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
name|TestDir
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
name|TestDirHelper
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
name|TestException
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
name|TestHdfs
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
name|TestHdfsHelper
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
name|StringUtils
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestFileSystemAccessService
specifier|public
class|class
name|TestFileSystemAccessService
extends|extends
name|HFSTestCase
block|{
DECL|method|createHadoopConf (Configuration hadoopConf)
specifier|private
name|void
name|createHadoopConf
parameter_list|(
name|Configuration
name|hadoopConf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|File
name|hdfsSite
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"hdfs-site.xml"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|hdfsSite
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|writeXml
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|createHadoopConf ()
specifier|public
name|void
name|createHadoopConf
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|"FOO"
argument_list|)
expr_stmt|;
name|createHadoopConf
argument_list|(
name|hadoopConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|simpleSecurity ()
specifier|public
name|void
name|simpleSecurity
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|ServiceException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H01.*"
argument_list|)
annotation|@
name|TestDir
DECL|method|noKerberosKeytabProperty ()
specifier|public
name|void
name|noKerberosKeytabProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.kerberos.keytab"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|ServiceException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H01.*"
argument_list|)
annotation|@
name|TestDir
DECL|method|noKerberosPrincipalProperty ()
specifier|public
name|void
name|noKerberosPrincipalProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.kerberos.keytab"
argument_list|,
literal|"/tmp/foo"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.kerberos.principal"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|ServiceException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H02.*"
argument_list|)
annotation|@
name|TestDir
DECL|method|kerberosInitializationFailure ()
specifier|public
name|void
name|kerberosInitializationFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.kerberos.keytab"
argument_list|,
literal|"/tmp/foo"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.kerberos.principal"
argument_list|,
literal|"foo@FOO"
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|ServiceException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H09.*"
argument_list|)
annotation|@
name|TestDir
DECL|method|invalidSecurity ()
specifier|public
name|void
name|invalidSecurity
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.authentication.type"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|serviceHadoopConf ()
specifier|public
name|void
name|serviceHadoopConf
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccessService
name|fsAccess
init|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fsAccess
operator|.
name|serviceHadoopConf
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"FOO"
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|serviceHadoopConfCustomDir ()
specifier|public
name|void
name|serviceHadoopConfCustomDir
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|hadoopConfDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"confx"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
operator|new
name|File
argument_list|(
name|hadoopConfDir
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.config.dir"
argument_list|,
name|hadoopConfDir
argument_list|)
expr_stmt|;
name|File
name|hdfsSite
init|=
operator|new
name|File
argument_list|(
name|hadoopConfDir
argument_list|,
literal|"hdfs-site.xml"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|hdfsSite
argument_list|)
decl_stmt|;
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|"BAR"
argument_list|)
expr_stmt|;
name|hadoopConf
operator|.
name|writeXml
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccessService
name|fsAccess
init|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fsAccess
operator|.
name|serviceHadoopConf
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"BAR"
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|inWhitelists ()
specifier|public
name|void
name|inWhitelists
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccessService
name|fsAccess
init|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|fsAccess
operator|.
name|validateNamenode
argument_list|(
literal|"NN"
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.name.node.whitelist"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|fsAccess
operator|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
expr_stmt|;
name|fsAccess
operator|.
name|validateNamenode
argument_list|(
literal|"NN"
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.name.node.whitelist"
argument_list|,
literal|"NN"
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|fsAccess
operator|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
expr_stmt|;
name|fsAccess
operator|.
name|validateNamenode
argument_list|(
literal|"NN"
argument_list|)
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|FileSystemAccessException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H05.*"
argument_list|)
annotation|@
name|TestDir
DECL|method|NameNodeNotinWhitelists ()
specifier|public
name|void
name|NameNodeNotinWhitelists
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.hadoop.name.node.whitelist"
argument_list|,
literal|"NN"
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccessService
name|fsAccess
init|=
operator|(
name|FileSystemAccessService
operator|)
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|fsAccess
operator|.
name|validateNamenode
argument_list|(
literal|"NNx"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestHdfs
DECL|method|createFileSystem ()
specifier|public
name|void
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|createHadoopConf
argument_list|(
name|hadoopConf
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccess
name|hadoop
init|=
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|hadoop
operator|.
name|createFileSystem
argument_list|(
literal|"u"
argument_list|,
name|hadoop
operator|.
name|getFileSystemConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|hadoop
operator|.
name|releaseFileSystem
argument_list|(
name|fs
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{     }
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestHdfs
DECL|method|fileSystemExecutor ()
specifier|public
name|void
name|fileSystemExecutor
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|createHadoopConf
argument_list|(
name|hadoopConf
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccess
name|hadoop
init|=
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fsa
index|[]
init|=
operator|new
name|FileSystem
index|[
literal|1
index|]
decl_stmt|;
name|hadoop
operator|.
name|execute
argument_list|(
literal|"u"
argument_list|,
name|hadoop
operator|.
name|getFileSystemConfiguration
argument_list|()
argument_list|,
operator|new
name|FileSystemAccess
operator|.
name|FileSystemExecutor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|execute
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|fsa
index|[
literal|0
index|]
operator|=
name|fs
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|fsa
index|[
literal|0
index|]
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{     }
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestException
argument_list|(
name|exception
operator|=
name|FileSystemAccessException
operator|.
name|class
argument_list|,
name|msgRegExp
operator|=
literal|"H06.*"
argument_list|)
annotation|@
name|TestDir
annotation|@
name|TestHdfs
DECL|method|fileSystemExecutorNoNameNode ()
specifier|public
name|void
name|fileSystemExecutorNoNameNode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|createHadoopConf
argument_list|(
name|hadoopConf
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccess
name|fsAccess
init|=
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|hdfsConf
init|=
name|fsAccess
operator|.
name|getFileSystemConfiguration
argument_list|()
decl_stmt|;
name|hdfsConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|fsAccess
operator|.
name|execute
argument_list|(
literal|"u"
argument_list|,
name|hdfsConf
argument_list|,
operator|new
name|FileSystemAccess
operator|.
name|FileSystemExecutor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|execute
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
annotation|@
name|TestHdfs
DECL|method|fileSystemExecutorException ()
specifier|public
name|void
name|fileSystemExecutorException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|services
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|InstrumentationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|FileSystemAccessService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|TestHdfsHelper
operator|.
name|getHdfsConf
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|createHadoopConf
argument_list|(
name|hadoopConf
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"server.services"
argument_list|,
name|services
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|"server"
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|FileSystemAccess
name|hadoop
init|=
name|server
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fsa
index|[]
init|=
operator|new
name|FileSystem
index|[
literal|1
index|]
decl_stmt|;
try|try
block|{
name|hadoop
operator|.
name|execute
argument_list|(
literal|"u"
argument_list|,
name|hadoop
operator|.
name|getFileSystemConfiguration
argument_list|()
argument_list|,
operator|new
name|FileSystemAccess
operator|.
name|FileSystemExecutor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|execute
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fsa
index|[
literal|0
index|]
operator|=
name|fs
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileSystemAccessException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ex
operator|.
name|getError
argument_list|()
argument_list|,
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H03
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|fsa
index|[
literal|0
index|]
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{     }
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

