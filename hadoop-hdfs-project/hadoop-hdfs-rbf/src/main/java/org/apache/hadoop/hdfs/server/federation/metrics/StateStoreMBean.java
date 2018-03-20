begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|metrics
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
comment|/**  * JMX interface for the State Store metrics.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|StateStoreMBean
specifier|public
interface|interface
name|StateStoreMBean
block|{
DECL|method|getReadOps ()
name|long
name|getReadOps
parameter_list|()
function_decl|;
DECL|method|getReadAvg ()
name|double
name|getReadAvg
parameter_list|()
function_decl|;
DECL|method|getWriteOps ()
name|long
name|getWriteOps
parameter_list|()
function_decl|;
DECL|method|getWriteAvg ()
name|double
name|getWriteAvg
parameter_list|()
function_decl|;
DECL|method|getFailureOps ()
name|long
name|getFailureOps
parameter_list|()
function_decl|;
DECL|method|getFailureAvg ()
name|double
name|getFailureAvg
parameter_list|()
function_decl|;
DECL|method|getRemoveOps ()
name|long
name|getRemoveOps
parameter_list|()
function_decl|;
DECL|method|getRemoveAvg ()
name|double
name|getRemoveAvg
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

