begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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

begin_comment
comment|/**  * Helper class that reports how much work has has been done by the node.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|WorkStatus
specifier|public
class|class
name|WorkStatus
block|{
DECL|field|result
specifier|private
name|int
name|result
decl_stmt|;
DECL|field|planID
specifier|private
name|String
name|planID
decl_stmt|;
DECL|field|status
specifier|private
name|String
name|status
decl_stmt|;
DECL|field|currentState
specifier|private
name|String
name|currentState
decl_stmt|;
comment|/**    * Constructs a workStatus Object.    *    * @param result       - int    * @param planID       - Plan ID    * @param status       - Current Status    * @param currentState - Current State    */
DECL|method|WorkStatus (int result, String planID, String status, String currentState)
specifier|public
name|WorkStatus
parameter_list|(
name|int
name|result
parameter_list|,
name|String
name|planID
parameter_list|,
name|String
name|status
parameter_list|,
name|String
name|currentState
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|planID
operator|=
name|planID
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
block|}
comment|/**    * Returns result.    *    * @return long    */
DECL|method|getResult ()
specifier|public
name|int
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
comment|/**    * Returns planID.    *    * @return String    */
DECL|method|getPlanID ()
specifier|public
name|String
name|getPlanID
parameter_list|()
block|{
return|return
name|planID
return|;
block|}
comment|/**    * Returns Status.    *    * @return String    */
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
comment|/**    * Gets current Status.    *    * @return - Json String    */
DECL|method|getCurrentState ()
specifier|public
name|String
name|getCurrentState
parameter_list|()
block|{
return|return
name|currentState
return|;
block|}
block|}
end_class

end_unit

