begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
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
name|FSDataInputStream
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
name|io
operator|.
name|DataInputByteBuffer
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
name|security
operator|.
name|DockerCredentialTokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|node
operator|.
name|ObjectNode
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
name|IOException
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
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Commonly needed actions for handling the Docker client configurations.  *  * Credentials that are used to access private Docker registries are supplied.  * Actions include:  *<ul>  *<li>Read the Docker client configuration json file from a  *   {@link FileSystem}.</li>  *<li>Extract the authentication information from the configuration into  *   {@link Token} and {@link Credentials} objects.</li>  *<li>Tokens are commonly shipped via the  *   {@link org.apache.hadoop.yarn.api.records.ContainerLaunchContext} as a  *   {@link ByteBuffer}, extract the {@link Credentials}.</li>  *<li>Write the Docker client configuration json back to the local filesystem  *   to be used by the Docker command line.</li>  *</ul>  */
end_comment

begin_class
DECL|class|DockerClientConfigHandler
specifier|public
specifier|final
class|class
name|DockerClientConfigHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DockerClientConfigHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONFIG_AUTHS_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_AUTHS_KEY
init|=
literal|"auths"
decl_stmt|;
DECL|field|CONFIG_AUTH_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_AUTH_KEY
init|=
literal|"auth"
decl_stmt|;
DECL|method|DockerClientConfigHandler ()
specifier|private
name|DockerClientConfigHandler
parameter_list|()
block|{ }
comment|/**    * Read the Docker client configuration and extract the auth tokens into    * Credentials.    *    * @param configFile the Path to the Docker client configuration.    * @param conf the Configuration object, needed by the FileSystem.    * @param applicationId the application ID to associate the Credentials with.    * @return the populated Credential object with the Docker Tokens.    * @throws IOException if the file can not be read.    */
DECL|method|readCredentialsFromConfigFile (Path configFile, Configuration conf, String applicationId)
specifier|public
specifier|static
name|Credentials
name|readCredentialsFromConfigFile
parameter_list|(
name|Path
name|configFile
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|applicationId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read the config file
name|String
name|contents
init|=
literal|null
decl_stmt|;
name|configFile
operator|=
operator|new
name|Path
argument_list|(
name|configFile
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|configFile
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|FSDataInputStream
name|fileHandle
init|=
name|fs
operator|.
name|open
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileHandle
operator|!=
literal|null
condition|)
block|{
name|contents
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|fileHandle
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read Docker client configuration: "
operator|+
name|configFile
argument_list|)
throw|;
block|}
comment|// Parse the JSON and create the Tokens/Credentials.
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|JsonFactory
name|factory
init|=
name|mapper
operator|.
name|getJsonFactory
argument_list|()
decl_stmt|;
name|JsonParser
name|parser
init|=
name|factory
operator|.
name|createJsonParser
argument_list|(
name|contents
argument_list|)
decl_stmt|;
name|JsonNode
name|rootNode
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootNode
operator|.
name|has
argument_list|(
name|CONFIG_AUTHS_KEY
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|rootNode
operator|.
name|get
argument_list|(
name|CONFIG_AUTHS_KEY
argument_list|)
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|registryUrl
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|registryCred
init|=
name|rootNode
operator|.
name|get
argument_list|(
name|CONFIG_AUTHS_KEY
argument_list|)
operator|.
name|get
argument_list|(
name|registryUrl
argument_list|)
operator|.
name|get
argument_list|(
name|CONFIG_AUTH_KEY
argument_list|)
operator|.
name|asText
argument_list|()
decl_stmt|;
name|TokenIdentifier
name|tokenId
init|=
operator|new
name|DockerCredentialTokenIdentifier
argument_list|(
name|registryUrl
argument_list|,
name|applicationId
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DockerCredentialTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<>
argument_list|(
name|tokenId
operator|.
name|getBytes
argument_list|()
argument_list|,
name|registryCred
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
name|tokenId
operator|.
name|getKind
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|registryUrl
argument_list|)
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
name|registryUrl
operator|+
literal|"-"
operator|+
name|applicationId
argument_list|)
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Token read from Docker client configuration file: "
operator|+
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|credentials
return|;
block|}
comment|/**    * Convert the Token ByteBuffer to the appropriate Credentials object.    *    * @param tokens the Tokens from the ContainerLaunchContext.    * @return the Credentials object populated from the Tokens.    */
DECL|method|getCredentialsFromTokensByteBuffer ( ByteBuffer tokens)
specifier|public
specifier|static
name|Credentials
name|getCredentialsFromTokensByteBuffer
parameter_list|(
name|ByteBuffer
name|tokens
parameter_list|)
throws|throws
name|IOException
block|{
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputByteBuffer
name|dibb
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|dibb
operator|.
name|reset
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|dibb
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|Token
name|token
range|:
name|credentials
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Token read from token storage: "
operator|+
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|credentials
return|;
block|}
comment|/**    * Extract the Docker related tokens from the Credentials and write the Docker    * client configuration to the supplied File.    *    * @param outConfigFile the File to write the Docker client configuration to.    * @param credentials the populated Credentials object.    * @throws IOException if the write fails.    */
DECL|method|writeDockerCredentialsToPath (File outConfigFile, Credentials credentials)
specifier|public
specifier|static
name|void
name|writeDockerCredentialsToPath
parameter_list|(
name|File
name|outConfigFile
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|ObjectNode
name|rootNode
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
name|ObjectNode
name|registryUrlNode
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
name|boolean
name|foundDockerCred
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|credentials
operator|.
name|numberOfTokens
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|tk
range|:
name|credentials
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|tk
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|DockerCredentialTokenIdentifier
operator|.
name|KIND
argument_list|)
condition|)
block|{
name|foundDockerCred
operator|=
literal|true
expr_stmt|;
name|DockerCredentialTokenIdentifier
name|ti
init|=
operator|(
name|DockerCredentialTokenIdentifier
operator|)
name|tk
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|ObjectNode
name|registryCredNode
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
name|registryUrlNode
operator|.
name|put
argument_list|(
name|ti
operator|.
name|getRegistryUrl
argument_list|()
argument_list|,
name|registryCredNode
argument_list|)
expr_stmt|;
name|registryCredNode
operator|.
name|put
argument_list|(
name|CONFIG_AUTH_KEY
argument_list|,
operator|new
name|String
argument_list|(
name|tk
operator|.
name|getPassword
argument_list|()
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Prepared token for write: "
operator|+
name|tk
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|foundDockerCred
condition|)
block|{
name|rootNode
operator|.
name|put
argument_list|(
name|CONFIG_AUTHS_KEY
argument_list|,
name|registryUrlNode
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mapper
operator|.
name|writerWithDefaultPrettyPrinter
argument_list|()
operator|.
name|writeValueAsString
argument_list|(
name|rootNode
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|outConfigFile
argument_list|,
name|json
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

