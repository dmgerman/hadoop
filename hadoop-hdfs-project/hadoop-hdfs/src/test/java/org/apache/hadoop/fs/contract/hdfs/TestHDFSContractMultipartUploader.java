begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|hdfs
package|;
end_package

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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|contract
operator|.
name|AbstractContractMultipartUploaderTest
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
name|contract
operator|.
name|AbstractFSContract
import|;
end_import

begin_comment
comment|/**  * Test MultipartUploader tests on HDFS.  */
end_comment

begin_class
DECL|class|TestHDFSContractMultipartUploader
specifier|public
class|class
name|TestHDFSContractMultipartUploader
extends|extends
name|AbstractContractMultipartUploaderTest
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractContractMultipartUploaderTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createCluster ()
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|HDFSContract
operator|.
name|createCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|HDFSContract
operator|.
name|destroyCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|HDFSContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * HDFS doesn't have any restriction on the part size.    * @return 1KB    */
annotation|@
name|Override
DECL|method|partSizeInBytes ()
specifier|protected
name|int
name|partSizeInBytes
parameter_list|()
block|{
return|return
literal|1024
return|;
block|}
annotation|@
name|Override
DECL|method|finalizeConsumesUploadIdImmediately ()
specifier|protected
name|boolean
name|finalizeConsumesUploadIdImmediately
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|supportsConcurrentUploadsToSamePath ()
specifier|protected
name|boolean
name|supportsConcurrentUploadsToSamePath
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

