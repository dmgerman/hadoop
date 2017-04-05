begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.exception
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|exception
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
operator|.
name|Public
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
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  *<p>  * Logical error codes from<code>FederationStateStore</code>.  *</p>  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Unstable
DECL|enum|FederationStateStoreErrorCode
specifier|public
enum|enum
name|FederationStateStoreErrorCode
block|{
DECL|enumConstant|MEMBERSHIP_INSERT_FAIL
name|MEMBERSHIP_INSERT_FAIL
argument_list|(
literal|1101
argument_list|,
literal|"Fail to insert a tuple into Membership table."
argument_list|)
block|,
DECL|enumConstant|MEMBERSHIP_DELETE_FAIL
name|MEMBERSHIP_DELETE_FAIL
argument_list|(
literal|1102
argument_list|,
literal|"Fail to delete a tuple from Membership table."
argument_list|)
block|,
DECL|enumConstant|MEMBERSHIP_SINGLE_SELECT_FAIL
name|MEMBERSHIP_SINGLE_SELECT_FAIL
argument_list|(
literal|1103
argument_list|,
literal|"Fail to select a tuple from Membership table."
argument_list|)
block|,
DECL|enumConstant|MEMBERSHIP_MULTIPLE_SELECT_FAIL
name|MEMBERSHIP_MULTIPLE_SELECT_FAIL
argument_list|(
literal|1104
argument_list|,
literal|"Fail to select multiple tuples from Membership table."
argument_list|)
block|,
DECL|enumConstant|MEMBERSHIP_UPDATE_DEREGISTER_FAIL
name|MEMBERSHIP_UPDATE_DEREGISTER_FAIL
argument_list|(
literal|1105
argument_list|,
literal|"Fail to update/deregister a tuple in Membership table."
argument_list|)
block|,
DECL|enumConstant|MEMBERSHIP_UPDATE_HEARTBEAT_FAIL
name|MEMBERSHIP_UPDATE_HEARTBEAT_FAIL
argument_list|(
literal|1106
argument_list|,
literal|"Fail to update/heartbeat a tuple in Membership table."
argument_list|)
block|,
DECL|enumConstant|APPLICATIONS_INSERT_FAIL
name|APPLICATIONS_INSERT_FAIL
argument_list|(
literal|1201
argument_list|,
literal|"Fail to insert a tuple into ApplicationsHomeSubCluster table."
argument_list|)
block|,
DECL|enumConstant|APPLICATIONS_DELETE_FAIL
name|APPLICATIONS_DELETE_FAIL
argument_list|(
literal|1202
argument_list|,
literal|"Fail to delete a tuple from ApplicationsHomeSubCluster table"
argument_list|)
block|,
DECL|enumConstant|APPLICATIONS_SINGLE_SELECT_FAIL
name|APPLICATIONS_SINGLE_SELECT_FAIL
argument_list|(
literal|1203
argument_list|,
literal|"Fail to select a tuple from ApplicationsHomeSubCluster table."
argument_list|)
block|,
DECL|enumConstant|APPLICATIONS_MULTIPLE_SELECT_FAIL
name|APPLICATIONS_MULTIPLE_SELECT_FAIL
argument_list|(
literal|1204
argument_list|,
literal|"Fail to select multiple tuple from ApplicationsHomeSubCluster table."
argument_list|)
block|,
DECL|enumConstant|APPLICATIONS_UPDATE_FAIL
name|APPLICATIONS_UPDATE_FAIL
argument_list|(
literal|1205
argument_list|,
literal|"Fail to update a tuple in ApplicationsHomeSubCluster table."
argument_list|)
block|,
DECL|enumConstant|POLICY_INSERT_FAIL
name|POLICY_INSERT_FAIL
argument_list|(
literal|1301
argument_list|,
literal|"Fail to insert a tuple into Policy table."
argument_list|)
block|,
DECL|enumConstant|POLICY_DELETE_FAIL
name|POLICY_DELETE_FAIL
argument_list|(
literal|1302
argument_list|,
literal|"Fail to delete a tuple from Membership table."
argument_list|)
block|,
DECL|enumConstant|POLICY_SINGLE_SELECT_FAIL
name|POLICY_SINGLE_SELECT_FAIL
argument_list|(
literal|1303
argument_list|,
literal|"Fail to select a tuple from Policy table."
argument_list|)
block|,
DECL|enumConstant|POLICY_MULTIPLE_SELECT_FAIL
name|POLICY_MULTIPLE_SELECT_FAIL
argument_list|(
literal|1304
argument_list|,
literal|"Fail to select multiple tuples from Policy table."
argument_list|)
block|,
DECL|enumConstant|POLICY_UPDATE_FAIL
name|POLICY_UPDATE_FAIL
argument_list|(
literal|1305
argument_list|,
literal|"Fail to update a tuple in Policy table."
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|msg
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
DECL|method|FederationStateStoreErrorCode (int id, String msg)
name|FederationStateStoreErrorCode
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
block|}
comment|/**    * Get the error code related to the FederationStateStore failure.    *    * @return the error code related to the FederationStateStore failure.    */
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**    * Get the error message related to the FederationStateStore failure.    *    * @return the error message related to the FederationStateStore failure.    */
DECL|method|getMsg ()
specifier|public
name|String
name|getMsg
parameter_list|()
block|{
return|return
name|this
operator|.
name|msg
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"\nError Code: "
operator|+
name|this
operator|.
name|id
operator|+
literal|"\nError Message: "
operator|+
name|this
operator|.
name|msg
return|;
block|}
block|}
end_enum

end_unit

