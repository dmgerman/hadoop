begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosPrincipal
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestSecurityUtil
specifier|public
class|class
name|TestSecurityUtil
block|{
annotation|@
name|Test
DECL|method|isOriginalTGTReturnsCorrectValues ()
specifier|public
name|void
name|isOriginalTGTReturnsCorrectValues
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo@foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo.bar.bat@foo.bar.bat"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"blah"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"/@"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo@FOO"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (String original, String hostname, String expected)
specifier|private
name|void
name|verify
parameter_list|(
name|String
name|original
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|original
argument_list|,
name|hostname
argument_list|)
argument_list|)
expr_stmt|;
name|InetAddress
name|addr
init|=
name|mockAddr
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|original
argument_list|,
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|mockAddr (String reverseTo)
specifier|private
name|InetAddress
name|mockAddr
parameter_list|(
name|String
name|reverseTo
parameter_list|)
block|{
name|InetAddress
name|mock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|reverseTo
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
return|return
name|mock
return|;
block|}
annotation|@
name|Test
DECL|method|testGetServerPrincipal ()
specifier|public
name|void
name|testGetServerPrincipal
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|service
init|=
literal|"hdfs/"
decl_stmt|;
name|String
name|realm
init|=
literal|"@REALM"
decl_stmt|;
name|String
name|hostname
init|=
literal|"foohost"
decl_stmt|;
name|String
name|userPrincipal
init|=
literal|"foo@FOOREALM"
decl_stmt|;
name|String
name|shouldReplace
init|=
name|service
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
name|realm
decl_stmt|;
name|String
name|replaced
init|=
name|service
operator|+
name|hostname
operator|+
name|realm
decl_stmt|;
name|verify
argument_list|(
name|shouldReplace
argument_list|,
name|hostname
argument_list|,
name|replaced
argument_list|)
expr_stmt|;
name|String
name|shouldNotReplace
init|=
name|service
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
literal|"NAME"
operator|+
name|realm
decl_stmt|;
name|verify
argument_list|(
name|shouldNotReplace
argument_list|,
name|hostname
argument_list|,
name|shouldNotReplace
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|userPrincipal
argument_list|,
name|hostname
argument_list|,
name|userPrincipal
argument_list|)
expr_stmt|;
comment|// testing reverse DNS lookup doesn't happen
name|InetAddress
name|notUsed
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shouldNotReplace
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|shouldNotReplace
argument_list|,
name|notUsed
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|notUsed
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalHostNameForNullOrWild ()
specifier|public
name|void
name|testLocalHostNameForNullOrWild
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|local
init|=
name|SecurityUtil
operator|.
name|getLocalHostName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs/"
operator|+
name|local
operator|+
literal|"@REALM"
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
literal|"hdfs/_HOST@REALM"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs/"
operator|+
name|local
operator|+
literal|"@REALM"
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
literal|"hdfs/_HOST@REALM"
argument_list|,
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStartsWithIncorrectSettings ()
specifier|public
name|void
name|testStartsWithIncorrectSettings
parameter_list|()
throws|throws
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|String
name|keyTabKey
init|=
literal|"key"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|keyTabKey
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|keyTabKey
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Exception for empty keytabfile name was expected"
argument_list|,
name|gotException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetHostFromPrincipal ()
specifier|public
name|void
name|testGetHostFromPrincipal
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"host"
argument_list|,
name|SecurityUtil
operator|.
name|getHostFromPrincipal
argument_list|(
literal|"service/host@realm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|SecurityUtil
operator|.
name|getHostFromPrincipal
argument_list|(
literal|"service@realm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

