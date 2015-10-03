begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer
package|package
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
name|datatransfer
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/** Block Construction Stage */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|BlockConstructionStage
specifier|public
enum|enum
name|BlockConstructionStage
block|{
comment|/** The enumerates are always listed as regular stage followed by the    * recovery stage.    * Changing this order will make getRecoveryStage not working.    */
comment|// pipeline set up for block append
DECL|enumConstant|PIPELINE_SETUP_APPEND
name|PIPELINE_SETUP_APPEND
block|,
comment|// pipeline set up for failed PIPELINE_SETUP_APPEND recovery
DECL|enumConstant|PIPELINE_SETUP_APPEND_RECOVERY
name|PIPELINE_SETUP_APPEND_RECOVERY
block|,
comment|// data streaming
DECL|enumConstant|DATA_STREAMING
name|DATA_STREAMING
block|,
comment|// pipeline setup for failed data streaming recovery
DECL|enumConstant|PIPELINE_SETUP_STREAMING_RECOVERY
name|PIPELINE_SETUP_STREAMING_RECOVERY
block|,
comment|// close the block and pipeline
DECL|enumConstant|PIPELINE_CLOSE
name|PIPELINE_CLOSE
block|,
comment|// Recover a failed PIPELINE_CLOSE
DECL|enumConstant|PIPELINE_CLOSE_RECOVERY
name|PIPELINE_CLOSE_RECOVERY
block|,
comment|// pipeline set up for block creation
DECL|enumConstant|PIPELINE_SETUP_CREATE
name|PIPELINE_SETUP_CREATE
block|,
comment|// transfer RBW for adding datanodes
DECL|enumConstant|TRANSFER_RBW
name|TRANSFER_RBW
block|,
comment|// transfer Finalized for adding datanodes
DECL|enumConstant|TRANSFER_FINALIZED
name|TRANSFER_FINALIZED
block|;
DECL|field|RECOVERY_BIT
specifier|final
specifier|static
specifier|private
name|byte
name|RECOVERY_BIT
init|=
operator|(
name|byte
operator|)
literal|1
decl_stmt|;
comment|/**    * get the recovery stage of this stage    */
DECL|method|getRecoveryStage ()
specifier|public
name|BlockConstructionStage
name|getRecoveryStage
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|PIPELINE_SETUP_CREATE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected blockStage "
operator|+
name|this
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|values
argument_list|()
index|[
name|ordinal
argument_list|()
operator||
name|RECOVERY_BIT
index|]
return|;
block|}
block|}
block|}
end_enum

end_unit

