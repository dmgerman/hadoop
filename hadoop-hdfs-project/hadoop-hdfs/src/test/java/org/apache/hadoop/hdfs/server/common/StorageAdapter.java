begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
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
name|common
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Test methods that need to access package-private parts of  * Storage  */
end_comment

begin_class
DECL|class|StorageAdapter
specifier|public
specifier|abstract
class|class
name|StorageAdapter
block|{
comment|/**    * Inject and return a spy on a storage directory    */
DECL|method|spyOnStorageDirectory ( Storage s, int idx)
specifier|public
specifier|static
name|StorageDirectory
name|spyOnStorageDirectory
parameter_list|(
name|Storage
name|s
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|StorageDirectory
name|dir
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|s
operator|.
name|getStorageDir
argument_list|(
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|getStorageDirs
argument_list|()
operator|.
name|set
argument_list|(
name|idx
argument_list|,
name|dir
argument_list|)
expr_stmt|;
return|return
name|dir
return|;
block|}
block|}
end_class

end_unit

