begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.protocol
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
name|federation
operator|.
name|store
operator|.
name|protocol
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreSerializer
import|;
end_import

begin_comment
comment|/**  * API request for overriding an existing namenode registration in the state  * store.  */
end_comment

begin_class
DECL|class|UpdateNamenodeRegistrationRequest
specifier|public
specifier|abstract
class|class
name|UpdateNamenodeRegistrationRequest
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|UpdateNamenodeRegistrationRequest
name|newInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|UpdateNamenodeRegistrationRequest
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|newInstance ( String nameserviceId, String namenodeId, FederationNamenodeServiceState state)
specifier|public
specifier|static
name|UpdateNamenodeRegistrationRequest
name|newInstance
parameter_list|(
name|String
name|nameserviceId
parameter_list|,
name|String
name|namenodeId
parameter_list|,
name|FederationNamenodeServiceState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|UpdateNamenodeRegistrationRequest
name|request
init|=
name|newInstance
argument_list|()
decl_stmt|;
name|request
operator|.
name|setNameserviceId
argument_list|(
name|nameserviceId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNamenodeId
argument_list|(
name|namenodeId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getNameserviceId ()
specifier|public
specifier|abstract
name|String
name|getNameserviceId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getNamenodeId ()
specifier|public
specifier|abstract
name|String
name|getNamenodeId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getState ()
specifier|public
specifier|abstract
name|FederationNamenodeServiceState
name|getState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNameserviceId (String nsId)
specifier|public
specifier|abstract
name|void
name|setNameserviceId
parameter_list|(
name|String
name|nsId
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNamenodeId (String nnId)
specifier|public
specifier|abstract
name|void
name|setNamenodeId
parameter_list|(
name|String
name|nnId
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setState (FederationNamenodeServiceState state)
specifier|public
specifier|abstract
name|void
name|setState
parameter_list|(
name|FederationNamenodeServiceState
name|state
parameter_list|)
function_decl|;
block|}
end_class

end_unit

