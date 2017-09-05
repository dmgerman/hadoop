begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMSACLs
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMSConfiguration
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|KMSWebApp
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|MiniKMS
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
name|junit
operator|.
name|After
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test class for re-encryption with minikms.  */
end_comment

begin_class
DECL|class|TestReencryptionWithKMS
specifier|public
class|class
name|TestReencryptionWithKMS
extends|extends
name|TestReencryption
block|{
DECL|field|miniKMS
specifier|private
name|MiniKMS
name|miniKMS
decl_stmt|;
DECL|field|kmsDir
specifier|private
name|String
name|kmsDir
decl_stmt|;
annotation|@
name|Override
DECL|method|getKeyProviderURI ()
specifier|protected
name|String
name|getKeyProviderURI
parameter_list|()
block|{
return|return
name|KMSClientProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://"
operator|+
name|miniKMS
operator|.
name|getKMSUrl
argument_list|()
operator|.
name|toExternalForm
argument_list|()
operator|.
name|replace
argument_list|(
literal|"://"
argument_list|,
literal|"@"
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
name|Exception
block|{
name|kmsDir
operator|=
literal|"target/test-classes/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
specifier|final
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|kmsDir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|MiniKMS
operator|.
name|Builder
name|miniKMSBuilder
init|=
operator|new
name|MiniKMS
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|miniKMS
operator|=
name|miniKMSBuilder
operator|.
name|setKmsConfDir
argument_list|(
name|dir
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|miniKMS
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
if|if
condition|(
name|miniKMS
operator|!=
literal|null
condition|)
block|{
name|miniKMS
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setProvider ()
specifier|protected
name|void
name|setProvider
parameter_list|()
block|{   }
annotation|@
name|Test
DECL|method|testReencryptionKMSACLs ()
specifier|public
name|void
name|testReencryptionKMSACLs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|aclPath
init|=
operator|new
name|Path
argument_list|(
name|kmsDir
argument_list|,
name|KMSConfiguration
operator|.
name|KMS_ACLS_XML
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|acl
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addResource
argument_list|(
name|aclPath
argument_list|)
expr_stmt|;
comment|// should not require any of the get ACLs.
name|acl
operator|.
name|set
argument_list|(
name|KMSACLs
operator|.
name|Type
operator|.
name|GET
operator|.
name|getBlacklistConfigKey
argument_list|()
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|acl
operator|.
name|set
argument_list|(
name|KMSACLs
operator|.
name|Type
operator|.
name|GET_KEYS
operator|.
name|getBlacklistConfigKey
argument_list|()
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
specifier|final
name|File
name|kmsAcl
init|=
operator|new
name|File
argument_list|(
name|aclPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|kmsAcl
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|kmsAcl
argument_list|)
init|)
block|{
name|acl
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|KMSWebApp
operator|.
name|getACLs
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|testReencryptionBasic
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

