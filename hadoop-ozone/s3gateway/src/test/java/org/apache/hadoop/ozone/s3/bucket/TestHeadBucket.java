begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.bucket
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
name|bucket
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
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|exception
operator|.
name|S3ErrorTable
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|fail
import|;
end_import

begin_comment
comment|/**  * This class test HeadBucket functionality.  */
end_comment

begin_class
DECL|class|TestHeadBucket
specifier|public
class|class
name|TestHeadBucket
block|{
DECL|field|volumeName
specifier|private
name|String
name|volumeName
init|=
literal|"myVolume"
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
init|=
literal|"myBucket"
decl_stmt|;
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
DECL|field|headBucket
specifier|private
name|HeadBucket
name|headBucket
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
comment|// Create volume and bucket
name|objectStoreStub
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|volumeStub
init|=
name|objectStoreStub
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|volumeStub
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
comment|// Create HeadBucket and setClient to OzoneClientStub
name|headBucket
operator|=
operator|new
name|HeadBucket
argument_list|()
expr_stmt|;
name|headBucket
operator|.
name|setClient
argument_list|(
name|clientStub
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeadBucket ()
specifier|public
name|void
name|testHeadBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|headBucket
operator|.
name|head
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeadFail ()
specifier|public
name|void
name|testHeadFail
parameter_list|()
block|{
try|try
block|{
name|headBucket
operator|.
name|head
argument_list|(
name|volumeName
argument_list|,
literal|"unknownbucket"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|OS3Exception
condition|)
block|{
name|assertEquals
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_BUCKET
operator|.
name|getCode
argument_list|()
argument_list|,
operator|(
operator|(
name|OS3Exception
operator|)
name|ex
operator|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_BUCKET
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
operator|(
operator|(
name|OS3Exception
operator|)
name|ex
operator|)
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"testHeadFail failed"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|fail
argument_list|(
literal|"testHeadFail failed"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

