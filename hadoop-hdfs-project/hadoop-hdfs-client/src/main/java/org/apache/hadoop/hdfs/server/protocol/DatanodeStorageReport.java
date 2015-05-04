begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
import|;
end_import

begin_comment
comment|/**  * Class captures information of a datanode and its storages.  */
end_comment

begin_class
DECL|class|DatanodeStorageReport
specifier|public
class|class
name|DatanodeStorageReport
block|{
DECL|field|datanodeInfo
specifier|final
name|DatanodeInfo
name|datanodeInfo
decl_stmt|;
DECL|field|storageReports
specifier|final
name|StorageReport
index|[]
name|storageReports
decl_stmt|;
DECL|method|DatanodeStorageReport (DatanodeInfo datanodeInfo, StorageReport[] storageReports)
specifier|public
name|DatanodeStorageReport
parameter_list|(
name|DatanodeInfo
name|datanodeInfo
parameter_list|,
name|StorageReport
index|[]
name|storageReports
parameter_list|)
block|{
name|this
operator|.
name|datanodeInfo
operator|=
name|datanodeInfo
expr_stmt|;
name|this
operator|.
name|storageReports
operator|=
name|storageReports
expr_stmt|;
block|}
DECL|method|getDatanodeInfo ()
specifier|public
name|DatanodeInfo
name|getDatanodeInfo
parameter_list|()
block|{
return|return
name|datanodeInfo
return|;
block|}
DECL|method|getStorageReports ()
specifier|public
name|StorageReport
index|[]
name|getStorageReports
parameter_list|()
block|{
return|return
name|storageReports
return|;
block|}
block|}
end_class

end_unit

