begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestKerberosName
specifier|public
class|class
name|TestKerberosName
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
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
literal|"hadoop.security.auth_to_local"
argument_list|,
operator|(
literal|"RULE:[1:$1@$0](.*@YAHOO\\.COM)s/@.*//\n"
operator|+
literal|"RULE:[2:$1](johndoe)s/^.*$/guest/\n"
operator|+
literal|"RULE:[2:$1;$2](^.*;admin$)s/;admin$//\n"
operator|+
literal|"RULE:[2:$2](root)\n"
operator|+
literal|"DEFAULT"
operator|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|KerberosName
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|KerberosName
operator|.
name|printRules
argument_list|()
expr_stmt|;
block|}
DECL|method|checkTranslation (String from, String to)
specifier|private
name|void
name|checkTranslation
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Translate "
operator|+
name|from
argument_list|)
expr_stmt|;
name|KerberosName
name|nm
init|=
operator|new
name|KerberosName
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|String
name|simple
init|=
name|nm
operator|.
name|getShortName
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"to "
operator|+
name|simple
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"short name incorrect"
argument_list|,
name|to
argument_list|,
name|simple
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRules ()
specifier|public
name|void
name|testRules
parameter_list|()
throws|throws
name|Exception
block|{
name|checkTranslation
argument_list|(
literal|"omalley@APACHE.ORG"
argument_list|,
literal|"omalley"
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"hdfs/10.0.0.1@APACHE.ORG"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"oom@YAHOO.COM"
argument_list|,
literal|"oom"
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"johndoe/zoo@FOO.COM"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"joe/admin@FOO.COM"
argument_list|,
literal|"joe"
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"joe/root@FOO.COM"
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkBadName (String name)
specifier|private
name|void
name|checkBadName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checking "
operator|+
name|name
operator|+
literal|" to ensure it is bad."
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|KerberosName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get exception for "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// PASS
block|}
block|}
DECL|method|checkBadTranslation (String from)
specifier|private
name|void
name|checkBadTranslation
parameter_list|(
name|String
name|from
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checking bad translation for "
operator|+
name|from
argument_list|)
expr_stmt|;
name|KerberosName
name|nm
init|=
operator|new
name|KerberosName
argument_list|(
name|from
argument_list|)
decl_stmt|;
try|try
block|{
name|nm
operator|.
name|getShortName
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get exception for "
operator|+
name|from
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
comment|// PASS
block|}
block|}
annotation|@
name|Test
DECL|method|testAntiPatterns ()
specifier|public
name|void
name|testAntiPatterns
parameter_list|()
throws|throws
name|Exception
block|{
name|checkBadName
argument_list|(
literal|"owen/owen/owen@FOO.COM"
argument_list|)
expr_stmt|;
name|checkBadName
argument_list|(
literal|"owen@foo/bar.com"
argument_list|)
expr_stmt|;
name|checkBadTranslation
argument_list|(
literal|"foo@ACME.COM"
argument_list|)
expr_stmt|;
name|checkBadTranslation
argument_list|(
literal|"root/joe@FOO.COM"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

