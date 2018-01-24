begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common.sps
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
operator|.
name|sps
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
comment|/**  * Block movement status code.  */
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
DECL|enum|BlockMovementStatus
specifier|public
enum|enum
name|BlockMovementStatus
block|{
comment|/** Success. */
DECL|enumConstant|DN_BLK_STORAGE_MOVEMENT_SUCCESS
name|DN_BLK_STORAGE_MOVEMENT_SUCCESS
argument_list|(
literal|0
argument_list|)
block|,
comment|/**    * Failure due to generation time stamp mismatches or network errors    * or no available space.    */
DECL|enumConstant|DN_BLK_STORAGE_MOVEMENT_FAILURE
name|DN_BLK_STORAGE_MOVEMENT_FAILURE
argument_list|(
operator|-
literal|1
argument_list|)
block|;
comment|// TODO: need to support different type of failures. Failure due to network
comment|// errors, block pinned, no space available etc.
DECL|field|code
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
DECL|method|BlockMovementStatus (int code)
name|BlockMovementStatus
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
comment|/**    * @return the status code.    */
DECL|method|getStatusCode ()
name|int
name|getStatusCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
block|}
end_enum

end_unit

