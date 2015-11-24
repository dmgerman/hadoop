begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.connectors
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
name|diskbalancer
operator|.
name|connectors
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * ClusterConnector interface hides all specifics about how we communicate to  * the HDFS cluster. This interface returns data in classes that diskbalancer  * understands.  */
end_comment

begin_interface
DECL|interface|ClusterConnector
specifier|public
interface|interface
name|ClusterConnector
block|{
comment|/**    * getNodes function returns a list of DiskBalancerDataNodes.    *    * @return Array of DiskBalancerDataNodes    */
DECL|method|getNodes ()
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|getNodes
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Returns info about the connector.    *    * @return String.    */
DECL|method|getConnectorInfo ()
name|String
name|getConnectorInfo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

