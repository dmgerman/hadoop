begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
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
comment|/**  * The current state of the container of an application.  **/
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|ContainerState
specifier|public
enum|enum
name|ContainerState
block|{
DECL|enumConstant|RUNNING_BUT_UNREADY
DECL|enumConstant|READY
DECL|enumConstant|STOPPED
DECL|enumConstant|NEEDS_UPGRADE
DECL|enumConstant|UPGRADING
DECL|enumConstant|SUCCEEDED
name|RUNNING_BUT_UNREADY
block|,
name|READY
block|,
name|STOPPED
block|,
name|NEEDS_UPGRADE
block|,
name|UPGRADING
block|,
name|SUCCEEDED
block|,
DECL|enumConstant|FAILED
DECL|enumConstant|FAILED_UPGRADE
name|FAILED
block|,
name|FAILED_UPGRADE
block|; }
end_enum

end_unit

