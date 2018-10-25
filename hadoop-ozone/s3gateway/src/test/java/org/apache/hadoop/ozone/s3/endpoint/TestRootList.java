begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|endpoint
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
name|ozone
operator|.
name|client
operator|.
name|ObjectStore
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
name|client
operator|.
name|OzoneClientStub
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
name|client
operator|.
name|OzoneVolume
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
name|lang3
operator|.
name|RandomStringUtils
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|header
operator|.
name|AuthenticationHeaderParser
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

begin_comment
comment|/**  * This class test HeadBucket functionality.  */
end_comment

begin_class
DECL|class|TestRootList
specifier|public
class|class
name|TestRootList
block|{
DECL|field|clientStub
specifier|private
name|OzoneClientStub
name|clientStub
decl_stmt|;
DECL|field|objectStoreStub
specifier|private
name|ObjectStore
name|objectStoreStub
decl_stmt|;
DECL|field|volumeStub
specifier|private
name|OzoneVolume
name|volumeStub
decl_stmt|;
DECL|field|rootEndpoint
specifier|private
name|RootEndpoint
name|rootEndpoint
decl_stmt|;
DECL|field|userName
specifier|private
name|String
name|userName
init|=
literal|"ozone"
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
comment|//Create client stub and object store stub.
name|clientStub
operator|=
operator|new
name|OzoneClientStub
argument_list|()
expr_stmt|;
name|objectStoreStub
operator|=
name|clientStub
operator|.
name|getObjectStore
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
literal|"s3"
operator|+
name|userName
decl_stmt|;
name|objectStoreStub
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|volumeStub
operator|=
name|objectStoreStub
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
comment|// Create HeadBucket and setClient to OzoneClientStub
name|rootEndpoint
operator|=
operator|new
name|RootEndpoint
argument_list|()
expr_stmt|;
name|rootEndpoint
operator|.
name|setClient
argument_list|(
name|clientStub
argument_list|)
expr_stmt|;
name|AuthenticationHeaderParser
name|parser
init|=
operator|new
name|AuthenticationHeaderParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setAuthHeader
argument_list|(
literal|"AWS "
operator|+
name|userName
operator|+
literal|":secret"
argument_list|)
expr_stmt|;
name|rootEndpoint
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListBucket ()
specifier|public
name|void
name|testListBucket
parameter_list|()
throws|throws
name|Exception
block|{
comment|// List operation should success even there is no bucket.
name|ListBucketResponse
name|response
init|=
name|rootEndpoint
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getBucketsNum
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|bucketBaseName
init|=
literal|"bucket-"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|volumeStub
operator|.
name|createBucket
argument_list|(
name|bucketBaseName
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|response
operator|=
name|rootEndpoint
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|response
operator|.
name|getBucketsNum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

