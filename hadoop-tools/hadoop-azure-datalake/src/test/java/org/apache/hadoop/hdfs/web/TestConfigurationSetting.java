begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|fs
operator|.
name|adl
operator|.
name|TestableAdlFileSystem
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|ConfCredentialBasedAccessTokenProvider
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
name|hdfs
operator|.
name|web
operator|.
name|oauth2
operator|.
name|CredentialBasedAccessTokenProvider
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
name|Test
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * This class is responsible for testing adl file system required configuration  * and feature set keys.  */
end_comment

begin_class
DECL|class|TestConfigurationSetting
specifier|public
class|class
name|TestConfigurationSetting
block|{
annotation|@
name|Test
DECL|method|testAllConfiguration ()
specifier|public
name|void
name|testAllConfiguration
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|TestableAdlFileSystem
name|fs
init|=
operator|new
name|TestableAdlFileSystem
argument_list|()
decl_stmt|;
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
name|HdfsClientConfigKeys
operator|.
name|OAUTH_REFRESH_URL_KEY
argument_list|,
literal|"http://localhost:1111/refresh"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CredentialBasedAccessTokenProvider
operator|.
name|OAUTH_CREDENTIAL_KEY
argument_list|,
literal|"credential"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|OAUTH_CLIENT_ID_KEY
argument_list|,
literal|"MY_CLIENTID"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|ACCESS_TOKEN_PROVIDER_KEY
argument_list|,
name|ConfCredentialBasedAccessTokenProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_OAUTH_ENABLED_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"adl://localhost:1234"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Default setting check
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureRedirectOff
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureGetBlockLocationLocallyBundled
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureConcurrentReadWithReadAhead
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fs
operator|.
name|isOverrideOwnerFeatureOn
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
name|fs
operator|.
name|getMaxBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fs
operator|.
name|getMaxConcurrentConnection
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Configuration toggle check
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.redirection.off"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fs
operator|.
name|isFeatureRedirectOff
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.redirection.off"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureRedirectOff
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.getblocklocation.locally.bundled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fs
operator|.
name|isFeatureGetBlockLocationLocallyBundled
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.getblocklocation.locally.bundled"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureGetBlockLocationLocallyBundled
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.readahead"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fs
operator|.
name|isFeatureConcurrentReadWithReadAhead
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.readahead"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isFeatureConcurrentReadWithReadAhead
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.readahead.max.buffersize"
argument_list|,
literal|"101"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|fs
operator|.
name|getMaxBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.feature.override.readahead.max.buffersize"
argument_list|,
literal|"12134565"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12134565
argument_list|,
name|fs
operator|.
name|getMaxBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.debug.override.localuserasfileowner"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fs
operator|.
name|isOverrideOwnerFeatureOn
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"adl.debug.override.localuserasfileowner"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fs
operator|.
name|isOverrideOwnerFeatureOn
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

