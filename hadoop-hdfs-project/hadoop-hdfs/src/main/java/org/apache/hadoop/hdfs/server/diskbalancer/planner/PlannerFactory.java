begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.planner
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
name|planner
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
import|;
end_import

begin_comment
comment|/**  * Returns a planner based on the user defined tags.  */
end_comment

begin_class
DECL|class|PlannerFactory
specifier|public
specifier|final
class|class
name|PlannerFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PlannerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|GREEDY_PLANNER
specifier|public
specifier|static
specifier|final
name|String
name|GREEDY_PLANNER
init|=
literal|"greedyPlanner"
decl_stmt|;
comment|/**    *  Gets a planner object.    * @param plannerName - name of the planner.    * @param node - Datanode.    * @param threshold - percentage    * @return Planner    */
DECL|method|getPlanner (String plannerName, DiskBalancerDataNode node, double threshold)
specifier|public
specifier|static
name|Planner
name|getPlanner
parameter_list|(
name|String
name|plannerName
parameter_list|,
name|DiskBalancerDataNode
name|node
parameter_list|,
name|double
name|threshold
parameter_list|)
block|{
if|if
condition|(
name|plannerName
operator|.
name|equals
argument_list|(
name|GREEDY_PLANNER
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Creating a %s for Node : %s IP : %s ID : %s"
argument_list|,
name|GREEDY_PLANNER
argument_list|,
name|node
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|node
operator|.
name|getDataNodeIP
argument_list|()
argument_list|,
name|node
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GreedyPlanner
argument_list|(
name|threshold
argument_list|,
name|node
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unrecognized planner name : "
operator|+
name|plannerName
argument_list|)
throw|;
block|}
DECL|method|PlannerFactory ()
specifier|private
name|PlannerFactory
parameter_list|()
block|{
comment|// Never constructed
block|}
block|}
end_class

end_unit

