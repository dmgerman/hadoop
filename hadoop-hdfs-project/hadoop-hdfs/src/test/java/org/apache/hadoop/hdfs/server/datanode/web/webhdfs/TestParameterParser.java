begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web.webhdfs
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
name|datanode
operator|.
name|web
operator|.
name|webhdfs
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
name|hdfs
operator|.
name|DFSTestUtil
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
name|HAUtil
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|resources
operator|.
name|DelegationParam
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
name|resources
operator|.
name|NamenodeAddressParam
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
name|resources
operator|.
name|OffsetParam
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
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|QueryStringDecoder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
DECL|class|TestParameterParser
specifier|public
class|class
name|TestParameterParser
block|{
DECL|field|LOGICAL_NAME
specifier|private
specifier|static
specifier|final
name|String
name|LOGICAL_NAME
init|=
literal|"minidfs"
decl_stmt|;
annotation|@
name|Test
DECL|method|testDeserializeHAToken ()
specifier|public
name|void
name|testDeserializeHAToken
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|DFSTestUtil
operator|.
name|newHAConfiguration
argument_list|(
name|LOGICAL_NAME
argument_list|)
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|QueryStringDecoder
name|decoder
init|=
operator|new
name|QueryStringDecoder
argument_list|(
name|WebHdfsHandler
operator|.
name|WEBHDFS_PREFIX
operator|+
literal|"/?"
operator|+
name|NamenodeAddressParam
operator|.
name|NAME
operator|+
literal|"="
operator|+
name|LOGICAL_NAME
operator|+
literal|"&"
operator|+
name|DelegationParam
operator|.
name|NAME
operator|+
literal|"="
operator|+
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
decl_stmt|;
name|ParameterParser
name|testParser
init|=
operator|new
name|ParameterParser
argument_list|(
name|decoder
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|tok2
init|=
name|testParser
operator|.
name|delegationToken
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|HAUtil
operator|.
name|isTokenForLogicalUri
argument_list|(
name|tok2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecodePath ()
specifier|public
name|void
name|testDecodePath
parameter_list|()
block|{
specifier|final
name|String
name|SCAPED_PATH
init|=
literal|"hdfs-6662/test%25251%26%3Dtest?op=OPEN"
decl_stmt|;
specifier|final
name|String
name|EXPECTED_PATH
init|=
literal|"/hdfs-6662/test%251&=test"
decl_stmt|;
name|Configuration
name|conf
init|=
name|DFSTestUtil
operator|.
name|newHAConfiguration
argument_list|(
name|LOGICAL_NAME
argument_list|)
decl_stmt|;
name|QueryStringDecoder
name|decoder
init|=
operator|new
name|QueryStringDecoder
argument_list|(
name|WebHdfsHandler
operator|.
name|WEBHDFS_PREFIX
operator|+
literal|"/"
operator|+
name|SCAPED_PATH
argument_list|)
decl_stmt|;
name|ParameterParser
name|testParser
init|=
operator|new
name|ParameterParser
argument_list|(
name|decoder
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EXPECTED_PATH
argument_list|,
name|testParser
operator|.
name|path
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffset ()
specifier|public
name|void
name|testOffset
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|X
init|=
literal|42
decl_stmt|;
name|long
name|offset
init|=
operator|new
name|OffsetParam
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|X
argument_list|)
argument_list|)
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"OffsetParam: "
argument_list|,
name|X
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|=
operator|new
name|OffsetParam
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
operator|.
name|getOffset
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"OffsetParam with null should have defaulted to 0"
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
expr_stmt|;
try|try
block|{
name|offset
operator|=
operator|new
name|OffsetParam
argument_list|(
literal|"abc"
argument_list|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"OffsetParam with nondigit value should have thrown IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
block|}
end_class

end_unit

