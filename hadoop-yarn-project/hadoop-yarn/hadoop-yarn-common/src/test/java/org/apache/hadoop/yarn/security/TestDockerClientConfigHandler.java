begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|FileUtils
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
name|io
operator|.
name|DataOutputBuffer
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
name|io
operator|.
name|Text
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
name|Credentials
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|yarn
operator|.
name|util
operator|.
name|DockerClientConfigHandler
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
name|BufferedWriter
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test the functionality of the DockerClientConfigHandler.  */
end_comment

begin_class
DECL|class|TestDockerClientConfigHandler
specifier|public
class|class
name|TestDockerClientConfigHandler
block|{
DECL|field|JSON
specifier|public
specifier|static
specifier|final
name|String
name|JSON
init|=
literal|"{\"auths\": "
operator|+
literal|"{\"https://index.docker.io/v1/\": "
operator|+
literal|"{\"auth\": \"foobarbaz\"},"
operator|+
literal|"\"registry.example.com\": "
operator|+
literal|"{\"auth\": \"bazbarfoo\"}}}"
decl_stmt|;
DECL|field|APPLICATION_ID
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_ID
init|=
literal|"application_2313_2131341"
decl_stmt|;
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|file
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"docker-client-config"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|BufferedWriter
name|bw
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|bw
operator|.
name|write
argument_list|(
name|JSON
argument_list|)
expr_stmt|;
name|bw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadCredentialsFromConfigFile ()
specifier|public
name|void
name|testReadCredentialsFromConfigFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Credentials
name|credentials
init|=
name|DockerClientConfigHandler
operator|.
name|readCredentialsFromConfigFile
argument_list|(
operator|new
name|Path
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|,
name|APPLICATION_ID
argument_list|)
decl_stmt|;
name|Token
name|token1
init|=
name|credentials
operator|.
name|getToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"https://index.docker.io/v1/-"
operator|+
name|APPLICATION_ID
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DockerCredentialTokenIdentifier
operator|.
name|KIND
argument_list|,
name|token1
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobarbaz"
argument_list|,
operator|new
name|String
argument_list|(
name|token1
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DockerCredentialTokenIdentifier
name|ti1
init|=
operator|(
name|DockerCredentialTokenIdentifier
operator|)
name|token1
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"https://index.docker.io/v1/"
argument_list|,
name|ti1
operator|.
name|getRegistryUrl
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|ti1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Token
name|token2
init|=
name|credentials
operator|.
name|getToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"registry.example.com-"
operator|+
name|APPLICATION_ID
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DockerCredentialTokenIdentifier
operator|.
name|KIND
argument_list|,
name|token2
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bazbarfoo"
argument_list|,
operator|new
name|String
argument_list|(
name|token2
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DockerCredentialTokenIdentifier
name|ti2
init|=
operator|(
name|DockerCredentialTokenIdentifier
operator|)
name|token2
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"registry.example.com"
argument_list|,
name|ti2
operator|.
name|getRegistryUrl
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|ti2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetCredentialsFromTokensByteBuffer ()
specifier|public
name|void
name|testGetCredentialsFromTokensByteBuffer
parameter_list|()
throws|throws
name|Exception
block|{
name|Credentials
name|credentials
init|=
name|DockerClientConfigHandler
operator|.
name|readCredentialsFromConfigFile
argument_list|(
operator|new
name|Path
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|,
name|APPLICATION_ID
argument_list|)
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ByteBuffer
name|tokens
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|Credentials
name|credentialsOut
init|=
name|DockerClientConfigHandler
operator|.
name|getCredentialsFromTokensByteBuffer
argument_list|(
name|tokens
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|credentials
operator|.
name|numberOfTokens
argument_list|()
argument_list|,
name|credentialsOut
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|tkIn
range|:
name|credentials
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|DockerCredentialTokenIdentifier
name|ti
init|=
operator|(
name|DockerCredentialTokenIdentifier
operator|)
name|tkIn
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|Token
name|tkOut
init|=
name|credentialsOut
operator|.
name|getToken
argument_list|(
operator|new
name|Text
argument_list|(
name|ti
operator|.
name|getRegistryUrl
argument_list|()
operator|+
literal|"-"
operator|+
name|ti
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|tkIn
operator|.
name|getKind
argument_list|()
argument_list|,
name|tkOut
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|tkIn
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|tkOut
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|tkIn
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|tkOut
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tkIn
operator|.
name|getService
argument_list|()
argument_list|,
name|tkOut
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteDockerCredentialsToPath ()
specifier|public
name|void
name|testWriteDockerCredentialsToPath
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|outFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"docker-client-config"
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
name|outFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|Credentials
name|credentials
init|=
name|DockerClientConfigHandler
operator|.
name|readCredentialsFromConfigFile
argument_list|(
operator|new
name|Path
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|,
name|APPLICATION_ID
argument_list|)
decl_stmt|;
name|DockerClientConfigHandler
operator|.
name|writeDockerCredentialsToPath
argument_list|(
name|outFile
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fileContents
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"auths"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"registry.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"https://index.docker.io/v1/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"foobarbaz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"bazbarfoo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

