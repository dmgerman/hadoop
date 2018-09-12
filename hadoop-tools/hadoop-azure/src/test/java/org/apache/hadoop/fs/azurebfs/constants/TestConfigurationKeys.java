begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.constants
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
operator|.
name|constants
package|;
end_package

begin_comment
comment|/**  * Responsible to keep all the Azure Blob File System configurations keys in Hadoop configuration file.  */
end_comment

begin_class
DECL|class|TestConfigurationKeys
specifier|public
specifier|final
class|class
name|TestConfigurationKeys
block|{
DECL|field|FS_AZURE_ACCOUNT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_NAME
init|=
literal|"fs.azure.account.name"
decl_stmt|;
DECL|field|FS_AZURE_ABFS_ACCOUNT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ABFS_ACCOUNT_NAME
init|=
literal|"fs.azure.abfs.account.name"
decl_stmt|;
DECL|field|FS_AZURE_ACCOUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_ACCOUNT_KEY
init|=
literal|"fs.azure.account.key"
decl_stmt|;
DECL|field|FS_AZURE_CONTRACT_TEST_URI
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_CONTRACT_TEST_URI
init|=
literal|"fs.contract.test.fs.abfs"
decl_stmt|;
DECL|field|FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_ID
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_ID
init|=
literal|"fs.azure.account.oauth2.contributor.client.id"
decl_stmt|;
DECL|field|FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_SECRET
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_SECRET
init|=
literal|"fs.azure.account.oauth2.contributor.client.secret"
decl_stmt|;
DECL|field|FS_AZURE_BLOB_DATA_READER_CLIENT_ID
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_BLOB_DATA_READER_CLIENT_ID
init|=
literal|"fs.azure.account.oauth2.reader.client.id"
decl_stmt|;
DECL|field|FS_AZURE_BLOB_DATA_READER_CLIENT_SECRET
specifier|public
specifier|static
specifier|final
name|String
name|FS_AZURE_BLOB_DATA_READER_CLIENT_SECRET
init|=
literal|"fs.azure.account.oauth2.reader.client.secret"
decl_stmt|;
DECL|field|TEST_CONFIGURATION_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TEST_CONFIGURATION_FILE_NAME
init|=
literal|"azure-test.xml"
decl_stmt|;
DECL|field|TEST_CONTAINER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TEST_CONTAINER_PREFIX
init|=
literal|"abfs-testcontainer-"
decl_stmt|;
DECL|field|TEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|TEST_TIMEOUT
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|method|TestConfigurationKeys ()
specifier|private
name|TestConfigurationKeys
parameter_list|()
block|{}
block|}
end_class

end_unit

