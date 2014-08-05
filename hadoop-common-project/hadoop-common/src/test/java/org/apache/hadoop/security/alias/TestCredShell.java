begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.alias
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|alias
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
name|assertEquals
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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_class
DECL|class|TestCredShell
specifier|public
class|class
name|TestCredShell
block|{
DECL|field|outContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|outContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|errContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|tmpDir
specifier|private
specifier|static
specifier|final
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"creds"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|errContent
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCredentialSuccessfulLifecycle ()
specifier|public
name|void
name|testCredentialSuccessfulLifecycle
parameter_list|()
throws|throws
name|Exception
block|{
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|,
literal|"-value"
block|,
literal|"p@ssw0rd"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|cs
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1 has been successfully "
operator|+
literal|"created."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"list"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1"
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args4
init|=
block|{
literal|"delete"
block|,
literal|"credential1"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1 has been successfully "
operator|+
literal|"deleted."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args5
init|=
block|{
literal|"list"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidProvider ()
specifier|public
name|void
name|testInvalidProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|,
literal|"-value"
block|,
literal|"p@ssw0rd"
block|,
literal|"-provider"
block|,
literal|"sdff://file/tmp/credstore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|cs
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are no valid "
operator|+
literal|"CredentialProviders configured."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransientProviderWarning ()
specifier|public
name|void
name|testTransientProviderWarning
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|,
literal|"-value"
block|,
literal|"p@ssw0rd"
block|,
literal|"-provider"
block|,
literal|"user:///"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|cs
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"WARNING: you are modifying a "
operator|+
literal|"transient provider."
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"delete"
block|,
literal|"credential1"
block|,
literal|"-provider"
block|,
literal|"user:///"
block|}
decl_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1 has been successfully "
operator|+
literal|"deleted."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransientProviderOnlyConfig ()
specifier|public
name|void
name|testTransientProviderOnlyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|cs
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|CredentialProviderFactory
operator|.
name|CREDENTIAL_PROVIDER_PATH
argument_list|,
literal|"user:///"
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|rc
operator|=
name|cs
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are no valid "
operator|+
literal|"CredentialProviders configured."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPromptForCredentialWithEmptyPasswd ()
specifier|public
name|void
name|testPromptForCredentialWithEmptyPasswd
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|passwords
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|passwords
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|passwords
operator|.
name|add
argument_list|(
literal|"p@ssw0rd"
argument_list|)
expr_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|shell
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|shell
operator|.
name|setPasswordReader
argument_list|(
operator|new
name|MockPasswordReader
argument_list|(
name|passwords
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|=
name|shell
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Passwords don't match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPromptForCredential ()
specifier|public
name|void
name|testPromptForCredential
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"credential1"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|passwords
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|passwords
operator|.
name|add
argument_list|(
literal|"p@ssw0rd"
argument_list|)
expr_stmt|;
name|passwords
operator|.
name|add
argument_list|(
literal|"p@ssw0rd"
argument_list|)
expr_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|CredentialShell
name|shell
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|shell
operator|.
name|setPasswordReader
argument_list|(
operator|new
name|MockPasswordReader
argument_list|(
name|passwords
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|=
name|shell
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1 has been successfully "
operator|+
literal|"created."
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"delete"
block|,
literal|"credential1"
block|,
literal|"-provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/credstore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|shell
operator|.
name|run
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"credential1 has been successfully "
operator|+
literal|"deleted."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|MockPasswordReader
specifier|public
class|class
name|MockPasswordReader
extends|extends
name|CredentialShell
operator|.
name|PasswordReader
block|{
DECL|field|passwords
name|List
argument_list|<
name|String
argument_list|>
name|passwords
init|=
literal|null
decl_stmt|;
DECL|method|MockPasswordReader (List<String> passwds)
specifier|public
name|MockPasswordReader
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|passwds
parameter_list|)
block|{
name|passwords
operator|=
name|passwds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readPassword (String prompt)
specifier|public
name|char
index|[]
name|readPassword
parameter_list|(
name|String
name|prompt
parameter_list|)
block|{
if|if
condition|(
name|passwords
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|pass
init|=
name|passwords
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|pass
operator|==
literal|null
condition|?
literal|null
else|:
name|pass
operator|.
name|toCharArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|format (String message)
specifier|public
name|void
name|format
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEmptyArgList ()
specifier|public
name|void
name|testEmptyArgList
parameter_list|()
throws|throws
name|Exception
block|{
name|CredentialShell
name|shell
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|shell
operator|.
name|init
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommandHelpExitsNormally ()
specifier|public
name|void
name|testCommandHelpExitsNormally
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|cmd
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"create"
argument_list|,
literal|"list"
argument_list|,
literal|"delete"
argument_list|)
control|)
block|{
name|CredentialShell
name|shell
init|=
operator|new
name|CredentialShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected help argument on "
operator|+
name|cmd
operator|+
literal|" to return 0"
argument_list|,
literal|0
argument_list|,
name|shell
operator|.
name|init
argument_list|(
operator|new
name|String
index|[]
block|{
name|cmd
block|,
literal|"-help"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

