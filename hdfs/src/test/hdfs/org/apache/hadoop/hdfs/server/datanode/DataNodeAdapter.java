begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
package|;
end_package

begin_comment
comment|/**  * WARNING!! This is TEST ONLY class: it never has to be used  * for ANY development purposes.  *   * This is a utility class to expose DataNode functionality for  * unit and functional tests.  */
end_comment

begin_class
DECL|class|DataNodeAdapter
specifier|public
class|class
name|DataNodeAdapter
block|{
comment|/**    * Fetch a copy of ReplicaInfo from a datanode by block id    * @param dn datanode to retrieve a replicainfo object from    * @param bpid Block pool Id    * @param blkId id of the replica's block    * @return copy of ReplicaInfo object @link{FSDataset#fetchReplicaInfo}    */
DECL|method|fetchReplicaInfo (final DataNode dn, final String bpid, final long blkId)
specifier|public
specifier|static
name|ReplicaInfo
name|fetchReplicaInfo
parameter_list|(
specifier|final
name|DataNode
name|dn
parameter_list|,
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blkId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|FSDataset
operator|)
name|dn
operator|.
name|data
operator|)
operator|.
name|fetchReplicaInfo
argument_list|(
name|bpid
argument_list|,
name|blkId
argument_list|)
return|;
block|}
DECL|method|setHeartbeatsDisabledForTests (DataNode dn, boolean heartbeatsDisabledForTests)
specifier|public
specifier|static
name|void
name|setHeartbeatsDisabledForTests
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|boolean
name|heartbeatsDisabledForTests
parameter_list|)
block|{
name|dn
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|heartbeatsDisabledForTests
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

