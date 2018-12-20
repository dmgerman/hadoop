begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|security
operator|.
name|exception
operator|.
name|SCMSecurityException
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
name|hdds
operator|.
name|security
operator|.
name|token
operator|.
name|OzoneBlockTokenIdentifier
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
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
name|hdds
operator|.
name|scm
operator|.
name|TestUtils
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
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientGrpc
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
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientSpi
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|util
operator|.
name|Time
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
name|TemporaryFolder
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests ozone containers via secure grpc/netty.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestSecureOzoneContainer
specifier|public
class|class
name|TestSecureOzoneContainer
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
name|TestSecureOzoneContainer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Set the timeout for every test.    */
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
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|tempFolder
specifier|public
name|TemporaryFolder
name|tempFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|secConfig
specifier|private
name|SecurityConfig
name|secConfig
decl_stmt|;
DECL|field|requireBlockToken
specifier|private
name|Boolean
name|requireBlockToken
decl_stmt|;
DECL|field|hasBlockToken
specifier|private
name|Boolean
name|hasBlockToken
decl_stmt|;
DECL|field|blockTokeExpired
specifier|private
name|Boolean
name|blockTokeExpired
decl_stmt|;
DECL|method|TestSecureOzoneContainer (Boolean requireBlockToken, Boolean hasBlockToken, Boolean blockTokenExpired)
specifier|public
name|TestSecureOzoneContainer
parameter_list|(
name|Boolean
name|requireBlockToken
parameter_list|,
name|Boolean
name|hasBlockToken
parameter_list|,
name|Boolean
name|blockTokenExpired
parameter_list|)
block|{
name|this
operator|.
name|requireBlockToken
operator|=
name|requireBlockToken
expr_stmt|;
name|this
operator|.
name|hasBlockToken
operator|=
name|hasBlockToken
expr_stmt|;
name|this
operator|.
name|blockTokeExpired
operator|=
name|blockTokenExpired
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|blockTokenOptions ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|blockTokenOptions
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|true
block|,
literal|true
block|,
literal|false
block|}
block|,
block|{
literal|true
block|,
literal|true
block|,
literal|true
block|}
block|,
block|{
literal|true
block|,
literal|false
block|,
literal|false
block|}
block|,
block|{
literal|false
block|,
literal|true
block|,
literal|false
block|}
block|,
block|{
literal|false
block|,
literal|false
block|,
literal|false
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|String
name|ozoneMetaPath
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
literal|"ozoneMeta"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_DIRS
argument_list|,
name|ozoneMetaPath
argument_list|)
expr_stmt|;
name|secConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateOzoneContainer ()
specifier|public
name|void
name|testCreateOzoneContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test case: requireBlockToken: {} hasBlockToken: {} "
operator|+
literal|"blockTokenExpired: {}."
argument_list|,
name|requireBlockToken
argument_list|,
name|hasBlockToken
argument_list|,
name|blockTokeExpired
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_GRPC_BLOCK_TOKEN_ENABLED
argument_list|,
name|requireBlockToken
argument_list|)
expr_stmt|;
name|long
name|containerID
init|=
name|ContainerTestHelper
operator|.
name|getTestContainerID
argument_list|()
decl_stmt|;
name|OzoneContainer
name|container
init|=
literal|null
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"java.library.path"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|tempFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getFirstNode
argument_list|()
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|dn
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|container
operator|=
operator|new
name|OzoneContainer
argument_list|(
name|dn
argument_list|,
name|conf
argument_list|,
name|getContext
argument_list|(
name|dn
argument_list|)
argument_list|)
expr_stmt|;
comment|//Setting scmId, as we start manually ozone container.
name|container
operator|.
name|getDispatcher
argument_list|()
operator|.
name|setScmId
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"user1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"usergroup"
block|}
argument_list|)
decl_stmt|;
name|long
name|expiryDate
init|=
operator|(
name|blockTokeExpired
operator|)
condition|?
name|Time
operator|.
name|now
argument_list|()
operator|-
literal|60
operator|*
literal|60
operator|*
literal|2
else|:
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|60
operator|*
literal|60
operator|*
literal|24
decl_stmt|;
name|OzoneBlockTokenIdentifier
name|tokenId
init|=
operator|new
name|OzoneBlockTokenIdentifier
argument_list|(
literal|"testUser"
argument_list|,
literal|"cid:lud:bcsid"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
operator|.
name|class
argument_list|)
argument_list|,
name|expiryDate
argument_list|,
literal|"1234"
argument_list|,
literal|128L
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|dn
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
name|port
operator|=
name|secConfig
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT_DEFAULT
argument_list|)
expr_stmt|;
block|}
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|dn
operator|.
name|getIpAddress
argument_list|()
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|OzoneBlockTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|(
name|tokenId
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|2
index|]
argument_list|,
name|tokenId
operator|.
name|getKind
argument_list|()
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasBlockToken
condition|)
block|{
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
block|{
try|try
block|{
name|XceiverClientGrpc
name|client
init|=
operator|new
name|XceiverClientGrpc
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|(
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|createContainerForTesting
argument_list|(
name|client
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|requireBlockToken
operator|&&
name|hasBlockToken
operator|&&
operator|!
name|blockTokeExpired
condition|)
block|{
name|fail
argument_list|(
literal|"Client with BlockToken should succeed when block token is"
operator|+
literal|" required."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requireBlockToken
operator|&&
name|hasBlockToken
operator|&&
name|blockTokeExpired
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Receive expected exception"
argument_list|,
name|e
operator|instanceof
name|SCMSecurityException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requireBlockToken
operator|&&
operator|!
name|hasBlockToken
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Receive expected exception"
argument_list|,
name|e
operator|instanceof
name|SCMSecurityException
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|createContainerForTesting (XceiverClientSpi client, long containerID)
specifier|public
specifier|static
name|void
name|createContainerForTesting
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerID
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Create container
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerID
argument_list|,
name|client
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getContext (DatanodeDetails datanodeDetails)
specifier|private
name|StateContext
name|getContext
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
block|{
name|DatanodeStateMachine
name|stateMachine
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|stateMachine
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|stateMachine
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
block|}
end_class

end_unit

