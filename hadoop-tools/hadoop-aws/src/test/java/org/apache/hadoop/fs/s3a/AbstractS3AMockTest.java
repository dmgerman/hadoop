begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonServiceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
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
name|Rule
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
name|ExpectedException
import|;
end_import

begin_comment
comment|/**  * Abstract base class for S3A unit tests using a mock S3 client.  */
end_comment

begin_class
DECL|class|AbstractS3AMockTest
specifier|public
specifier|abstract
class|class
name|AbstractS3AMockTest
block|{
DECL|field|BUCKET
specifier|protected
specifier|static
specifier|final
name|String
name|BUCKET
init|=
literal|"mock-bucket"
decl_stmt|;
DECL|field|NOT_FOUND
specifier|protected
specifier|static
specifier|final
name|AmazonServiceException
name|NOT_FOUND
decl_stmt|;
static|static
block|{
name|NOT_FOUND
operator|=
operator|new
name|AmazonServiceException
argument_list|(
literal|"Not Found"
argument_list|)
expr_stmt|;
name|NOT_FOUND
operator|.
name|setStatusCode
argument_list|(
literal|404
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|protected
name|S3AFileSystem
name|fs
decl_stmt|;
DECL|field|s3
specifier|protected
name|AmazonS3
name|s3
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|S3_CLIENT_FACTORY_IMPL
argument_list|,
name|MockS3ClientFactory
operator|.
name|class
argument_list|,
name|S3ClientFactory
operator|.
name|class
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|new
name|S3AFileSystem
argument_list|()
expr_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|FS_S3A
operator|+
literal|"://"
operator|+
name|BUCKET
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
name|s3
operator|=
name|fs
operator|.
name|getAmazonS3Client
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
throws|throws
name|Exception
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

