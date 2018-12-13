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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
import|;
end_import

begin_comment
comment|/**  * Exception indicating that DataNode does not have a replica  * that matches the target block.  */
end_comment

begin_class
DECL|class|ReplicaNotFoundException
specifier|public
class|class
name|ReplicaNotFoundException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|NON_RBW_REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|NON_RBW_REPLICA
init|=
literal|"Cannot recover a non-RBW replica "
decl_stmt|;
DECL|field|UNFINALIZED_REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|UNFINALIZED_REPLICA
init|=
literal|"Cannot append to an unfinalized replica "
decl_stmt|;
DECL|field|UNFINALIZED_AND_NONRBW_REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|UNFINALIZED_AND_NONRBW_REPLICA
init|=
literal|"Cannot recover append/close to a replica that's not FINALIZED and not RBW"
operator|+
literal|" "
decl_stmt|;
DECL|field|NON_EXISTENT_REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|NON_EXISTENT_REPLICA
init|=
literal|"Replica does not exist "
decl_stmt|;
DECL|field|UNEXPECTED_GS_REPLICA
specifier|public
specifier|final
specifier|static
name|String
name|UNEXPECTED_GS_REPLICA
init|=
literal|"Cannot append to a replica with unexpected generation stamp "
decl_stmt|;
DECL|field|POSSIBLE_ROOT_CAUSE_MSG
specifier|public
specifier|final
specifier|static
name|String
name|POSSIBLE_ROOT_CAUSE_MSG
init|=
literal|". The block may have been removed recently by the balancer "
operator|+
literal|"or by intentionally reducing the replication factor. "
operator|+
literal|"This condition is usually harmless. To be certain, please check the "
operator|+
literal|"preceding datanode log messages for signs of a more serious issue."
decl_stmt|;
DECL|method|ReplicaNotFoundException ()
specifier|public
name|ReplicaNotFoundException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|ReplicaNotFoundException (ExtendedBlock b)
specifier|public
name|ReplicaNotFoundException
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
block|{
name|super
argument_list|(
literal|"Replica not found for "
operator|+
name|b
operator|+
name|POSSIBLE_ROOT_CAUSE_MSG
argument_list|)
expr_stmt|;
block|}
DECL|method|ReplicaNotFoundException (String msg)
specifier|public
name|ReplicaNotFoundException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

