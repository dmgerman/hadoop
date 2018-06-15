begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|CloudStorageAccount
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlockBlob
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
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
comment|/**  * Test AzureBlobFileSystem back compatibility with WASB.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemBackCompat
specifier|public
class|class
name|ITestAzureBlobFileSystemBackCompat
extends|extends
name|DependencyInjectedTest
block|{
DECL|method|ITestAzureBlobFileSystemBackCompat ()
specifier|public
name|ITestAzureBlobFileSystemBackCompat
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlobBackCompat ()
specifier|public
name|void
name|testBlobBackCompat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|storageConnectionString
init|=
name|getBlobConnectionString
argument_list|()
decl_stmt|;
name|CloudStorageAccount
name|storageAccount
init|=
name|CloudStorageAccount
operator|.
name|parse
argument_list|(
name|storageConnectionString
argument_list|)
decl_stmt|;
name|CloudBlobClient
name|blobClient
init|=
name|storageAccount
operator|.
name|createCloudBlobClient
argument_list|()
decl_stmt|;
name|CloudBlobContainer
name|container
init|=
name|blobClient
operator|.
name|getContainerReference
argument_list|(
name|this
operator|.
name|getFileSystemName
argument_list|()
argument_list|)
decl_stmt|;
name|container
operator|.
name|createIfNotExists
argument_list|()
expr_stmt|;
name|CloudBlockBlob
name|blockBlob
init|=
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"test/10/10/10"
argument_list|)
decl_stmt|;
name|blockBlob
operator|.
name|uploadText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|blockBlob
operator|=
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"test/10/123/3/2/1/3"
argument_list|)
expr_stmt|;
name|blockBlob
operator|.
name|uploadText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|fileStatuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test/10/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileStatuses
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileStatuses
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileStatuses
index|[
literal|0
index|]
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileStatuses
index|[
literal|0
index|]
operator|.
name|getLen
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileStatuses
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileStatuses
index|[
literal|1
index|]
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileStatuses
index|[
literal|1
index|]
operator|.
name|getLen
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlobConnectionString ()
specifier|private
name|String
name|getBlobConnectionString
parameter_list|()
block|{
name|String
name|connectionString
decl_stmt|;
if|if
condition|(
name|isEmulator
argument_list|()
condition|)
block|{
name|connectionString
operator|=
literal|"DefaultEndpointsProtocol=http;BlobEndpoint=http://"
operator|+
name|this
operator|.
name|getHostName
argument_list|()
operator|+
literal|":8880/"
operator|+
name|this
operator|.
name|getAccountName
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
index|[
literal|0
index|]
operator|+
literal|";AccountName="
operator|+
name|this
operator|.
name|getAccountName
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
index|[
literal|0
index|]
operator|+
literal|";AccountKey="
operator|+
name|this
operator|.
name|getAccountKey
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|connectionString
operator|=
literal|"DefaultEndpointsProtocol=http;BlobEndpoint=http://"
operator|+
name|this
operator|.
name|getAccountName
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"\\.dfs\\."
argument_list|,
literal|".blob."
argument_list|)
operator|+
literal|";AccountName="
operator|+
name|this
operator|.
name|getAccountName
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
index|[
literal|0
index|]
operator|+
literal|";AccountKey="
operator|+
name|this
operator|.
name|getAccountKey
argument_list|()
expr_stmt|;
block|}
return|return
name|connectionString
return|;
block|}
block|}
end_class

end_unit

